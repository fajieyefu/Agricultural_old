/**
 * 审核界面
 */
package com.yiyun.sijianguan.activity;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.yiyun.sijianguan.R;
import com.yiyun.sijianguan.common.BaseActivity;
import com.yiyun.sijianguan.common.Common_data;
import com.yiyun.sijianguan.common.ConnectionDetector;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author lyc
 * 
 */
public class Contract_Detail extends BaseActivity implements OnClickListener {

	TextView work_area_detail, village_detail, process_name_detail,
			apply_type_detail, apply_content_detail, apply_money_detail,
			apply_man_detail, apply_date_detail, contractNUM, contract_A,
			contract_B, apply_update_detail;
	Button process_btn, sp_save, sp_cancel, sp_digital_sign, pic_sq;
	RadioButton radioButton1, radioButton2, radioButton3;
	RadioGroup sqresult_radiogroup;
	String radio_re = "N", apply_code = "", pathStr = "", image_name = "",
			user_name = null;
	EditText sp_content;
	ImageView sign_pic;
	int show_status = 0;
	PopupWindow pop;
	Bitmap bitmap, down_bitmap;
	ArrayList<String> pic_url;
	Bitmap[] bitmaps;
	private ProgressDialog progress_url;
	ConnectionDetector cd;
	int sq_pic_status = 0;
	// 下载图片handler
	Handler handler1 = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				new download_image_url_task().execute();
				break;
			case 2:
				new argiculturl_upload_data_task().execute();
				break;
			case 3:
				pic_sq.setVisibility(View.VISIBLE);
				break;

			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contract_detail);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		init();
		pic_sq.setOnClickListener(Contract_Detail.this);
		sp_save.setOnClickListener(Contract_Detail.this);
		sp_cancel.setOnClickListener(Contract_Detail.this);
		process_btn.setOnClickListener(Contract_Detail.this);
		sp_digital_sign.setOnClickListener(Contract_Detail.this);
		sqresult_radiogroup.setOnCheckedChangeListener(radio_listener);
	}

	public void init() {
		cd = new ConnectionDetector(Contract_Detail.this);
		sign_pic = (ImageView) findViewById(R.id.contract_sign_pic);
		sp_digital_sign = (Button) findViewById(R.id.contract_sp_digital_sign);
		sp_content = (EditText) findViewById(R.id.contract_sp_ds);
		radioButton1 = (RadioButton) findViewById(R.id.contract_radioButton1);
		radioButton2 = (RadioButton) findViewById(R.id.contract_radioButton2);
		radioButton3 = (RadioButton) findViewById(R.id.contract_radioButton3);
		sqresult_radiogroup = (RadioGroup) findViewById(R.id.contract_sqresult_radiogroup);
		sp_save = (Button) findViewById(R.id.contract_sp_save);
		sp_cancel = (Button) findViewById(R.id.contract_sp_cancel);
		process_btn = (Button) findViewById(R.id.contract_process_btn);
		pic_sq = (Button) findViewById(R.id.contract_pic_sq);
		work_area_detail = (TextView) findViewById(R.id.contract_work_area_detail);
		village_detail = (TextView) findViewById(R.id.contract_village_detail);
		process_name_detail = (TextView) findViewById(R.id.contract_process_name_detail);
		apply_type_detail = (TextView) findViewById(R.id.contract_apply_type_detail);
		apply_content_detail = (TextView) findViewById(R.id.contract_apply_content_detail);
		apply_money_detail = (TextView) findViewById(R.id.contract_apply_money_detail);
		apply_man_detail = (TextView) findViewById(R.id.contract_apply_man_detail);
		apply_date_detail = (TextView) findViewById(R.id.contract_apply_date_detail);
		contractNUM = (TextView) findViewById(R.id.contract_contractNUM);
		contract_A = (TextView) findViewById(R.id.contract_A);
		contract_B = (TextView) findViewById(R.id.contract_B);

		SharedPreferences sp = getSharedPreferences(Common_data.share_name,
				MODE_PRIVATE);
		user_name = sp.getString(Common_data.username, "");
		show_status = getIntent().getIntExtra("status", 0);
		apply_man_detail.setText(sp.getString("Contract_man_name", ""));
		process_name_detail.setText(sp.getString("Contract_name", ""));
		apply_type_detail.setText(sp.getString("Contract_type", ""));
		apply_date_detail.setText(sp.getString("Contract_date", ""));
		contractNUM.setText(sp.getString("Contract_code", ""));
		contract_A.setText(sp.getString("Contract_A", ""));
		contract_B.setText(sp.getString("Contract_B", ""));
		apply_money_detail.setText(sp.getString("Contract_money", ""));
		work_area_detail.setText(sp.getString("Area_big", ""));
		village_detail.setText(sp.getString("Area_small", ""));
		apply_content_detail.setText(sp.getString("Contract_text", ""));
		apply_code = sp.getString("Contract_code", "");
		SharedPreferences sp_uni = getSharedPreferences("cahce", MODE_PRIVATE);
		String sp_content_str = sp_uni.getString("sp_content", "");
		sp_content.setText(sp_content_str);
		radioButton2.setChecked(true);

		SharedPreferences sps = getSharedPreferences("image_info", MODE_PRIVATE);
		image_name = sps.getString(Common_data.image_name, "");
		if (show_status == 1) {
			radioButton1.setChecked(true);
			radio_re = "Y";
			pathStr = sps.getString(Common_data.image_path, "");
			InputStream is;
			bitmap = null;
			try {
				is = new FileInputStream(pathStr);
				bitmap = BitmapFactory.decodeStream(is);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (bitmap != null) {
				sign_pic.setImageBitmap(bitmap);
			} else {
				System.out.println("bitmap is null");
			}

		}
		Message msg = new Message();
		msg.what = 1;
		handler1.sendMessage(msg);

	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.contract_process_btn:
			Intent intent0 = new Intent(Contract_Detail.this,
					Process_Detail.class);
			intent0.putExtra("apply_type", apply_type_detail.getText()
					.toString());
			intent0.putExtra("type", "3");
			intent0.putExtra("apply_code", apply_code);
			startActivity(intent0);
			break;
		case R.id.contract_pic_sq:
			if (sq_pic_status == 1) {
				Intent intent3 = new Intent(Contract_Detail.this, Pic_res.class);
				intent3.putStringArrayListExtra("url", pic_url);
				intent3.putExtra("activity_type", "list_datail_sq");
				startActivity(intent3);
			}
			break;
		case R.id.contract_sp_digital_sign:
			Intent intent1 = new Intent(Contract_Detail.this,
					Digital_sign.class);
			SharedPreferences sp = getSharedPreferences("cahce", MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("sp_content", sp_content.getText().toString());
			editor.putString("type", "3");// 1:请示；2:申请；3:合同；
			editor.commit();
			startActivity(intent1);
			break;
		case R.id.contract_sp_save:

			if (cd.isConn()) {
				if (!image_name.equals("")) {
					if (radio_re.equals("N") || radio_re.equals("C")) {
						String sp_str = sp_content.getText().toString();
						if (sp_str.equals("") || sp_str == null) {
							Toast.makeText(this, "审批说明无内容,无法保存！",
									Toast.LENGTH_LONG).show();
						} else {
							new Imag_upload_task().execute();
						}
					}else {
						new Imag_upload_task().execute();
					}

				} else {
					Toast.makeText(this, "请签字后,保存！", Toast.LENGTH_LONG).show();
				}

			} else {
				cd.setNetworkMethod(Contract_Detail.this);
			}

			break;
		case R.id.contract_sp_cancel:
			Intent intent = new Intent(Contract_Detail.this, MainActivity.class);
			startActivity(intent);
			Contract_Detail.this.finish();
			break;

		}
	}

	public void onThumbnailClick(View v) throws FileNotFoundException {

		if (!pathStr.equals("")) {
			final AlertDialog dialog = new AlertDialog.Builder(this).create();
			ImageView imgView = getView();
			dialog.setView(imgView);
			dialog.show();
			// 点击图片消失
			imgView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
		} else {
			Toast.makeText(this, "当前无图片！", Toast.LENGTH_LONG).show();
		}

		// // 全屏显示的方法
		// final Dialog dialog = new Dialog(this,
		// android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		// ImageView imgView = getView();
		// dialog.setContentView(imgView);
		// dialog.show();
	}

	private ImageView getView() throws FileNotFoundException {
		ImageView imgView = new ImageView(this);
		imgView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		if (!pathStr.equals("")) {
			InputStream is = new FileInputStream(pathStr);
			Drawable drawable = BitmapDrawable.createFromStream(is, null);
			imgView.setImageDrawable(drawable);
			return imgView;
		} else {
			Toast.makeText(this, "bitmap is null", Toast.LENGTH_LONG).show();
			return imgView;
		}

	}

	private OnCheckedChangeListener radio_listener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub

			if (checkedId == radioButton1.getId()) {
				radio_re = "Y";
			} else if (checkedId == radioButton2.getId()) {
				radio_re = "N";
			} else if (checkedId == radioButton3.getId()) {
				radio_re = "C";
			}

		}
	};

	private class download_image_url_task extends AsyncTask<Void, Void, String> {
		private ProgressDialog progress;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			progress = new ProgressDialog(Contract_Detail.this);
			progress.setTitle("数据加载中");
			progress.setCancelable(false);
			progress.setMessage("请稍后...");
			progress.show();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String methodName = "download_image";
			// 命名空间
			String nameSpace = Common_data.nameSpace;
			// EndPoint
			String endPoint = Common_data.endPoint;
			// SOAP Action
			String soapAction = nameSpace + methodName;

			// 指定WebService的命名空间和调用的方法名
			SoapObject rpc = new SoapObject(nameSpace, methodName);
			// 设置需调用WebService接口需要传入的参数
			rpc.addProperty("apply_code", apply_code);
			rpc.addProperty("type", "3");// 类型
			// 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER12);
			envelope.bodyOut = rpc;
			// 设置是否调用的是dotNet开发的WebService
			envelope.dotNet = true;
			HttpTransportSE transport = new HttpTransportSE(endPoint, 5000);
			try {
				// 调用WebService
				transport.call(soapAction, envelope);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 获取返回的数据
			SoapObject object = (SoapObject) envelope.bodyIn;
			String result1 = object.getProperty(0).toString();
			return result1;
		}

		protected void onPostExecute(String result) {
			if (progress != null) {
				progress.dismiss();
			}
			if (result.equals("data is null")) {
				sq_pic_status = 0;
			} else {
				sq_pic_status = 1;
				pic_url = new ArrayList<String>();
				String path = Common_data.image_url;
				try {
					JSONObject object = new JSONObject(result);
					JSONArray jsonArray = object.getJSONArray("Table");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObj = jsonArray.getJSONObject(i);
						pic_url.add(path + jsonObj.getString("file_path"));
					}
				} catch (JSONException e) {
					System.out.println("Jsons parse error !");
					e.printStackTrace();

				}
				Message msg = new Message();
				msg.what = 3;
				handler1.sendMessage(msg);
			}
		}

	}

	private class argiculturl_upload_data_task extends
			AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub

		}

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String sp_content_str = sp_content.getText().toString();
			// 调用的方法名称
			String methodName = "Agricultural_upload_contract_data";
			// 命名空间
			String nameSpace = Common_data.nameSpace;
			// EndPoint
			String endPoint = Common_data.endPoint;
			// SOAP Action
			String soapAction = nameSpace + methodName;

			// 指定WebService的命名空间和调用的方法名
			SoapObject rpc = new SoapObject(nameSpace, methodName);
			// 设置需调用WebService接口需要传入的参数
			SharedPreferences sp = getSharedPreferences(Common_data.share_name,
					MODE_PRIVATE);
			// 提交日期 ： sql操作 getdate()
			rpc.addProperty("apply_code1", apply_code);// 合同号
			rpc.addProperty("audit_man1",
					sp.getString(Common_data.username, ""));// 审批人
			// rpc.addProperty("audit_order", sp.getString("audit_order",
			// ""));// 审批顺序
			rpc.addProperty("audit_flag1", radio_re);// 审批结果
			rpc.addProperty("audit_text1", sp_content_str);// 审批意见
			// 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER12);
			envelope.bodyOut = rpc;
			// 设置是否调用的是dotNet开发的WebService
			envelope.dotNet = true;
			HttpTransportSE transport = new HttpTransportSE(endPoint);
			try {
				// 调用WebService
				transport.call(soapAction, envelope);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 获取返回的数据
			SoapObject object = (SoapObject) envelope.bodyIn;
			return object.getProperty(0).toString();

		}

		protected void onPostExecute(String result) {
			if (progress_url != null) {
				progress_url.dismiss();
			}
			if (("success").equals(result)) {
				Toast.makeText(Contract_Detail.this, "保存成功！", Toast.LENGTH_LONG)
						.show();
				Intent intent = new Intent(Contract_Detail.this,
						Contract_Activity.class);
				startActivity(intent);

			} else if (("failed").equals(result)) {
				Toast.makeText(Contract_Detail.this, "保存失败！", Toast.LENGTH_LONG)
						.show();
			} else if (result == null) {
				Toast.makeText(Contract_Detail.this, "网络异常！", Toast.LENGTH_LONG)
						.show();
			}

		}

	}

	private class Imag_upload_task extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			progress_url = new ProgressDialog(Contract_Detail.this);
			progress_url.setTitle("数据上传中");
			progress_url.setCancelable(false);
			progress_url.setMessage("请稍后...");
			progress_url.show();
			progress_url.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface arg0) {
					// TODO Auto-generated method stub
					cancel(true);
					Toast.makeText(Contract_Detail.this, "您已取消上传！",
							Toast.LENGTH_LONG).show();
				}
			});
		}

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			if (isCancelled()) {
				return "true";
			}
			// 调用的方法名称
			String methodName = "uploadImage_contract";
			// 命名空间
			String nameSpace = Common_data.nameSpace;
			// EndPoint
			String endPoint = Common_data.endPoint;
			// SOAP Action
			String soapAction = nameSpace + methodName;
			String uploadBuffer = compressImage(bitmap);
			SoapObject soapObject = new SoapObject(nameSpace, methodName);
			soapObject.addProperty("apply_code", apply_code); // 单号
			soapObject.addProperty("audit_man", user_name); // 审批人
			soapObject.addProperty("filename", image_name); // 参数1 图片名
			soapObject.addProperty("image", uploadBuffer); // 参数2 图片字符串
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER12);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(soapObject);
			HttpTransportSE httpTranstation = new HttpTransportSE(endPoint);
			try {
				httpTranstation.call(soapAction, envelope);
			} catch (Exception e) {
				e.printStackTrace();

				Toast.makeText(Contract_Detail.this, "网络异常！", Toast.LENGTH_LONG)
						.show();
			}
			SoapObject object = (SoapObject) envelope.bodyIn;

			return object.getProperty(0).toString();
		}

		protected void onPostExecute(String result) {

			if (("文件上传成功").equals(result)) {
				// lyc:针对oom 先判断是否已经回收:
				if (bitmap != null && !bitmap.isRecycled()) {
					// 回收并且置为null
					bitmap.recycle();
					bitmap = null;
				}
				System.gc();
				Message msg = new Message();
				msg.what = 2;
				handler1.sendMessage(msg);
			} else {
				Toast.makeText(Contract_Detail.this, "图片上传失败！",
						Toast.LENGTH_LONG).show();
			}

		}
	}

	private String compressImage(Bitmap image) {
		image.getByteCount();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.PNG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 60;
		while (baos.toByteArray().length / 1024 > 1024) { // 循环判断如果压缩后图片是否大于1m,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.PNG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		image.getByteCount();

		String result = new String(Base64.encode(baos.toByteArray()));
		// ByteArrayInputStream isBm = new
		// ByteArrayInputStream(baos.toByteArray());//
		// 把压缩后的数据baos存放到ByteArrayInputStream中
		// Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//
		// 把ByteArrayInputStream数据生成图片
		return result;

	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(Contract_Detail.this,
				Contract_Activity.class);
		startActivity(intent);
		Contract_Detail.this.finish();
	}
}
