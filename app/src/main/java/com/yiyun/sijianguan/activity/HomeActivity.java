package com.yiyun.sijianguan.activity;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.yiyun.sijianguan.R;
import com.yiyun.sijianguan.common.BaseActivity;
import com.yiyun.sijianguan.common.Common_data;
import com.yiyun.sijianguan.common.ConnectionDetector;
import com.yiyun.sijianguan.update.DownLoadManager;
import com.yiyun.sijianguan.update.UpdataInfo;
import com.yiyun.sijianguan.update.UpdataInfoParser;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class HomeActivity extends BaseActivity {
	ConnectionDetector cd;
	private final String TAG = this.getClass().getName();
	private final int UPDATA_NONEED = 0;
	private final int UPDATA_CLIENT = 1;
	private final int GET_PORT_ERROR = 3;
	private final int GET_UNDATAINFO_ERROR = 2;
	private final int DOWN_ERROR = 4;
	private UpdataInfo info;
	private ProgressDialog progress;
	SharedPreferences sp;
	int setting_flag = 0;

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATA_NONEED:
				if (progress != null) {
					progress.dismiss();
				}
				Intent intent = new Intent(HomeActivity.this, Login_Activity.class);
				startActivity(intent);
				HomeActivity.this.finish();
				break;
			case UPDATA_CLIENT:
				// 对话框通知用户升级程序
				showUpdataDialog();
				break;
			case GET_PORT_ERROR:
				// 服务器超时
//				Toast.makeText(HomeActivity.this, "系统设置错误,请修正！", Toast.LENGTH_LONG)
//						.show();
				Intent intent1 = new Intent(HomeActivity.this, Login_Activity.class);
				startActivity(intent1);
				HomeActivity.this.finish();
				break;
			case GET_UNDATAINFO_ERROR:
				// 服务器超时
				Toast.makeText(HomeActivity.this, "服务器异常,请联系管理员！", Toast.LENGTH_LONG)
						.show();
				HomeActivity.this.finish();
				break;
			case DOWN_ERROR:
				// 下载apk失败
				Toast.makeText(getApplicationContext(),
						"下载新版本失败,服务器异常,请联系管理员！", Toast.LENGTH_SHORT).show();
				HomeActivity.this.finish();
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.initial);
		cd = new ConnectionDetector(HomeActivity.this);
		sp = getSharedPreferences("common_url_data", MODE_PRIVATE);
		if (cd.isConn()) {

			new Net_Check_task().execute();

		} else {
			cd.setNetworkMethod(HomeActivity.this);
		}

	}

	private String getVersionName() throws Exception {
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageManager packageManager = getPackageManager();
		PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),
				0);
		return packInfo.versionName;
	}

	

	protected void showUpdataDialog() {
		AlertDialog.Builder builer = new Builder(this);
		builer.setTitle("当前有新版本，是否升级？");
		builer.setMessage(info.getDescription());
		// 当点确定按钮时从服务器上下载 新的apk 然后安装 װ
		builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				downLoadApk();
			}
		});
		builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(HomeActivity.this, Login_Activity.class);
				startActivity(intent);
				HomeActivity.this.finish();
			}
		});
		AlertDialog dialog = builer.create();
		dialog.show();
	}

	/*
	 * 从服务器中下载APK
	 */
	protected void downLoadApk() {
		final ProgressDialog pd; // 进度条对话框
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("正在下载更新");
		pd.show();
		new Thread() {
			@Override
			public void run() {
				try {
					File file = DownLoadManager.getFileFromServer(
							info.getUrl(), pd);
					sleep(3000);
					installApk(file);
					pd.dismiss(); // 结束掉进度条对话框
				} catch (Exception e) {
					Message msg = new Message();
					msg.what = DOWN_ERROR;
					handler.sendMessage(msg);
					e.printStackTrace();
				}
			}
		}.start();
	}

	// 安装apk
	protected void installApk(File file) {
		Intent intent = new Intent();
		// 执行动作
		intent.setAction(Intent.ACTION_VIEW);
		// 执行的数据类型
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}

	private class Net_Check_task extends AsyncTask<Void, Void, Void> {

		InputStream is;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			progress = new ProgressDialog(HomeActivity.this);
			progress.setCancelable(false);
			progress.setMessage("网络自检中...");
			progress.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				String url_server;
				int setting_flag = sp.getInt("setting_flag", 0);
				if (setting_flag == 1) {
					url_server = sp.getString("url_server",
							Common_data.url_server);
				} else {
					url_server = Common_data.url_server;
				}
				String path = url_server;
				URL url = new URL(path);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setConnectTimeout(5000);
				conn.setRequestMethod("GET");
				int responseCode = conn.getResponseCode();
				if (responseCode == 200) {
					// 从服务器获得一个输入流，也证明网络是好的
					is = conn.getInputStream();
				}
				info = UpdataInfoParser.getUpdataInfo(is);
				if (info.getVersion().equals(getVersionName())) {
					Log.i(TAG, "版本号相同");
					Message msg = new Message();
					msg.what = UPDATA_NONEED;
					handler.sendMessage(msg);
				} else {
					Log.i(TAG, "版本号不相同 ");
					Message msg = new Message();
					msg.what = UPDATA_CLIENT;
					handler.sendMessage(msg);
				}
			}catch (java.lang.IllegalArgumentException e) {
				// TODO: handle exception
				Message msg = new Message();
				msg.what = GET_PORT_ERROR;
				handler.sendMessage(msg);
				e.printStackTrace();
			}  catch (Exception e) {
				Message msg = new Message();
				msg.what = GET_UNDATAINFO_ERROR;
				handler.sendMessage(msg);
				e.printStackTrace();
			}

			return null;

		}

	}

}
