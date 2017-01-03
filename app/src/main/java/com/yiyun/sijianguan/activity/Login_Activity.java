package com.yiyun.sijianguan.activity;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.yiyun.sijianguan.R;
import com.yiyun.sijianguan.common.ActivityCollector;
import com.yiyun.sijianguan.common.BaseActivity;
import com.yiyun.sijianguan.common.Common_data;
import com.yiyun.sijianguan.common.ConnectionDetector;
import com.yiyun.sijianguan.common.Md5Encoding;
import com.yiyun.sijianguan.common.Setting;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login_Activity extends BaseActivity {
	Button login, intial_setting;
	EditText login_user, login_psw;
	String username, password;
	ConnectionDetector cd;
	boolean cancel_flag = false;
	final int initial_ex = 1;
	int ex = 0;

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case initial_ex:
				Toast.makeText(Login_Activity.this, "网络异常,请检查初始化设置！",
						Toast.LENGTH_LONG).show();
				break;

			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
		setContentView(R.layout.login_activity);
		init1();
	}

	private void init1() {

		login = (Button) findViewById(R.id.login);
		intial_setting = (Button) findViewById(R.id.intial_setting);
		login_user = (EditText) findViewById(R.id.login_user);
		login_psw = (EditText) findViewById(R.id.login_psw);
		SharedPreferences sp = getSharedPreferences(Common_data.share_name,
				MODE_PRIVATE);
		login_user.setText(sp.getString(Common_data.username, ""));
		login_psw.setText(sp.getString(Common_data.password, ""));
		login_psw.setTransformationMethod(PasswordTransformationMethod
				.getInstance());// 隐藏密码
		login.setOnClickListener(login_listener);
		intial_setting.setOnClickListener(intial_setting_listener);

	}

	private OnClickListener login_listener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			username = login_user.getText().toString().trim();
			password = login_psw.getText().toString().trim();
			SharedPreferences sp = getSharedPreferences(Common_data.share_name,
					MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString(Common_data.username, username);
			editor.putString(Common_data.password, password);
			editor.commit();
			new Md5Encoding();
			password = Md5Encoding.md5(password);

			cd = new ConnectionDetector(Login_Activity.this);
			try {
				if (!"".equals(username) || !"".equals(password)) {
					if (cd.isConn()) {
						new loginCheck_task().execute();

					} else {
						cd.setNetworkMethod(Login_Activity.this);
					}

				} else {
					// 给出错误提示
					Toast.makeText(Login_Activity.this, "请输入账号或密码！",
							Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				Toast.makeText(Login_Activity.this, "网络异常！", Toast.LENGTH_LONG)
						.show();
			}

		}
	};
	private OnClickListener intial_setting_listener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(Login_Activity.this, Setting.class);
			startActivity(intent);
			SharedPreferences sp = getSharedPreferences("common_url_data",
					MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putInt("setting_flag", 1);
			editor.commit();
		}
	};

	private class loginCheck_task extends AsyncTask<Void, Void, String> {
		private ProgressDialog progress;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			progress = new ProgressDialog(Login_Activity.this);
			progress.setTitle("登录");
			progress.setMessage("请稍后...");
			progress.show();

			progress.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) {
					// TODO Auto-generated method stub
					dialog1(Login_Activity.this, "确定取消登录吗?", true, "确认", true,
							"取消");
					if (cancel_flag) {
						cancel(cancel_flag);
						Toast.makeText(Login_Activity.this, "您已取消登录！",
								Toast.LENGTH_LONG).show();

					}

				}
			});
		}

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			if (isCancelled()) {
				cancel_flag = true;
				return "true";
			}
			// 调用的方法名称
			String methodName = "LoginCheck";
			// 命名空间
			String nameSpace = Common_data.nameSpace;
			// EndPoint
			String endPoint = Common_data.endPoint;
			// SOAP Action
			String soapAction = nameSpace + methodName;
			// 指定WebService的命名空间和调用的方法名
			SoapObject rpc = new SoapObject(nameSpace, methodName);
			// 设置需调用WebService接口需要传入的参数
			rpc.addProperty("userName", username);
			rpc.addProperty("password", password);
			// 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER12);
			envelope.bodyOut = rpc;
			// 设置是否调用的是dotNet开发的WebService
			envelope.dotNet = true;
			// 等价于envelope.bodyOut = rpc;
			HttpTransportSE transport = new HttpTransportSE(endPoint);
			try {
				// 调用WebService
				transport.call(soapAction, envelope);
			} catch (IllegalArgumentException e) {
				Message msg = new Message();
				msg.what = initial_ex;
				handler.sendMessage(msg);
				e.printStackTrace();
				ex = 1;
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 获取返回的数据
			SoapObject object = (SoapObject) envelope.bodyIn;

			if (object != null) {
				// 获取返回的结果
				return object.getProperty(0).toString();
			} else {
				return null;
			}

		}

		protected void onPostExecute(String result) {
			if (progress != null) {
				progress.dismiss();
			}
			if (("1").equals(result)) {

				Toast.makeText(Login_Activity.this, "登陆成功", Toast.LENGTH_LONG)
						.show();
				Intent intent = new Intent(Login_Activity.this,
						Fore_MainActivity.class);
				startActivity(intent);
//				Login_Activity.this.finish();
			} else if (ex == 1) {

			} else {
				Toast.makeText(Login_Activity.this, "账号或密码错误",
						Toast.LENGTH_LONG).show();
			}

		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		dialog(Login_Activity.this, "确定退出吗?", true, "确认", true, "取消");

	}

	// 点击取消按钮
	public void dialog(final Activity activity, String message,
			Boolean cancelable, String okText, Boolean cancelButton,
			String cancelText) {
		AlertDialog.Builder builder = new Builder(activity);
		builder.setMessage(message);
		builder.setCancelable(cancelable);
		builder.setPositiveButton(okText,
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						ActivityCollector.finishAll();
						Login_Activity.this.finish();
//
//						try {
//						} catch (Exception e) {
//							e.printStackTrace();
//							Builder d = new AlertDialog.Builder(activity);
//							d.setMessage(e.getMessage()).show();
//							return;
//						}
//						Intent startMain = new Intent(Intent.ACTION_MAIN);
//						startMain.addCategory(Intent.CATEGORY_HOME);
//						startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//						activity.startActivity(startMain);
//						ExitApplication.getInstance().exit();

					}
				});
		if (cancelButton == true) {
			builder.setNegativeButton(cancelText,
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}
					});
		}
		builder.create().show();
	}

	public void dialog1(final Activity activity, String message,
			Boolean cancelable, String okText, Boolean cancelButton,
			String cancelText) {
		AlertDialog.Builder builder = new Builder(activity);
		builder.setMessage(message);
		builder.setCancelable(cancelable);
		builder.setPositiveButton(okText,
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

						try {
						} catch (Exception e) {
							e.printStackTrace();
							Builder d = new AlertDialog.Builder(activity);
							d.setMessage(e.getMessage()).show();
							return;
						}
						cancel_flag = true;
					}
				});
		if (cancelButton == true) {
			builder.setNegativeButton(cancelText,
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							cancel_flag = false;
							dialog.dismiss();

						}
					});
		}
		builder.create().show();
	}
}
