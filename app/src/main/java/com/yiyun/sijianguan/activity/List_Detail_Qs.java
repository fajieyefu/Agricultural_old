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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author lyc
 * 
 */
public class List_Detail_Qs extends BaseActivity implements OnClickListener {

	TextView apply_num_detail, work_area_detail, village_detail,
			process_name_detail, apply_type_detail, apply_content_detail,
			apply_money_detail, apply_man_detail, apply_date_detail,
			contractNUM_qs, apply_update_detail;
	Button process_btn, qs_dif, sp_cancel, sp_digital_sign, pic_qs;
	RadioButton radioButton1, radioButton2, radioButton3;
	RadioGroup sqresult_radiogroup;
	String radio_re = "N", apply_code, pathStr = "", image_name = "",
			user_name = null;
	EditText sp_content;
	ImageView sign_pic;
	int show_status = 0;
	Bitmap bitmap, down_bitmap;
	ArrayList<String> pic_url;
	Bitmap[] bitmaps;
	ImageView[] images;
	private ProgressDialog progress_url;
	int qs_pic_status = 0;
	ConnectionDetector cd;
	Handler handler1 = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				if (cd.isConn()) {
					new download_image_url_task().execute();
				} else {
					cd.setNetworkMethod(List_Detail_Qs.this);
				}
				break;
			case 2:
				if (cd.isConn()) {
					new argiculturl_upload_data_task().execute();
				} else {
					cd.setNetworkMethod(List_Detail_Qs.this);
				}
				break;
			case 3:
				pic_qs.setVisibility(View.VISIBLE);
				break;

			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_detail_qs);
		init();
		qs_dif.setOnClickListener(List_Detail_Qs.this);
		sp_cancel.setOnClickListener(List_Detail_Qs.this);
		process_btn.setOnClickListener(List_Detail_Qs.this);
		pic_qs.setOnClickListener(List_Detail_Qs.this);
		sp_digital_sign.setOnClickListener(List_Detail_Qs.this);
		sqresult_radiogroup.setOnCheckedChangeListener(radio_listener);

	}

	public void init() {
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		cd = new ConnectionDetector(List_Detail_Qs.this);
		SharedPreferences sp = getSharedPreferences(Common_data.share_name,
				MODE_PRIVATE);
		user_name = sp.getString(Common_data.username, "");
		show_status = getIntent().getIntExtra("status", 0);
		sign_pic = (ImageView) findViewById(R.id.sign_pic_qs);
		sp_digital_sign = (Button) findViewById(R.id.sp_digital_sign_qs);
		sp_content = (EditText) findViewById(R.id.sp_ds_qs);
		radioButton1 = (RadioButton) findViewById(R.id.radioButton1_qs);
		radioButton2 = (RadioButton) findViewById(R.id.radioButton2_qs);
		radioButton3 = (RadioButton) findViewById(R.id.radioButton3_qs);
		sqresult_radiogroup = (RadioGroup) findViewById(R.id.sqresult_radiogroup_qs);
		qs_dif = (Button) findViewById(R.id.qs_dif);
		sp_cancel = (Button) findViewById(R.id.sp_cancel_qs);
		process_btn = (Button) findViewById(R.id.process_btn_qs);
		pic_qs = (Button) findViewById(R.id.pic_qs);
		apply_num_detail = (TextView) findViewById(R.id.apply_num_detail_qs);
		work_area_detail = (TextView) findViewById(R.id.work_area_detail_qs);
		village_detail = (TextView) findViewById(R.id.village_detail_qs);
		process_name_detail = (TextView) findViewById(R.id.process_name_detail_qs);
		apply_type_detail = (TextView) findViewById(R.id.apply_type_detail_qs);
		apply_content_detail = (TextView) findViewById(R.id.apply_content_detail_qs);
		apply_money_detail = (TextView) findViewById(R.id.apply_money_detail_qs);
		apply_man_detail = (TextView) findViewById(R.id.apply_man_detail_qs);
		apply_date_detail = (TextView) findViewById(R.id.apply_date_detail_qs);
		contractNUM_qs = (TextView) findViewById(R.id.contractNUM_qs);
		apply_man_detail.setText(sp.getString("apply_man", ""));
		String apply_code1 = sp.getString("apply_num", "");
		apply_code = apply_code1.substring(0, apply_code1.indexOf("("));
		apply_num_detail.setText(apply_code1);
		process_name_detail.setText(sp.getString("flower_name", ""));
		apply_type_detail.setText(sp.getString("flower_type", ""));
		apply_date_detail.setText(sp.getString("apply_date", ""));
		apply_money_detail.setText(sp.getString("apply_money", ""));
		contractNUM_qs.setText(sp.getString("contract_code", ""));

		SharedPreferences sp_uni = getSharedPreferences("cahce", MODE_PRIVATE);
		String sp_content_str = sp_uni.getString("sp_content", "");
		sp_content.setText(sp_content_str);
		radioButton2.setChecked(true);
		String areaStr = sp.getString("apply_area", "");
		if (!areaStr.equals("")) {
			work_area_detail
					.setText(areaStr.substring(0, areaStr.indexOf("(")));
			village_detail.setText(areaStr.substring(areaStr.indexOf("(") + 1,
					areaStr.indexOf(")")));
		} else {
			work_area_detail.setText("");
			village_detail.setText("");
		}

		apply_content_detail.setText(sp.getString("apply_text", ""));
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
				Toast.makeText(this, "bitmap is null", Toast.LENGTH_LONG)
						.show();
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
		case R.id.process_btn_qs:
			Intent intent0 = new Intent(List_Detail_Qs.this,
					Process_Detail.class);
			intent0.putExtra("apply_type", apply_type_detail.getText()
					.toString());
			intent0.putExtra("type", "1");
			intent0.putExtra("apply_code", apply_code);
			startActivity(intent0);
			break;
		case R.id.pic_qs:
			if (qs_pic_status == 1) {
				Intent intent3 = new Intent(List_Detail_Qs.this, Pic_res.class);
				intent3.putStringArrayListExtra("url", pic_url);
				intent3.putExtra("activity_type", "list_datail_qs");
				startActivity(intent3);
			} else {
				Toast.makeText(List_Detail_Qs.this, "proplem occur！",
						Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.sp_digital_sign_qs:
			Intent intent1 = new Intent(List_Detail_Qs.this, Digital_sign.class);
			SharedPreferences sp = getSharedPreferences("cahce", MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("sp_content", sp_content.getText().toString());
			editor.putString("type", "1");
			editor.commit();
			// show_status = 1; lyc_change0617
			startActivity(intent1);
			break;
		case R.id.qs_dif:
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
				cd.setNetworkMethod(List_Detail_Qs.this);
			}

			break;
		case R.id.sp_cancel_qs:
			Intent intent = new Intent(List_Detail_Qs.this, MainActivity.class);
			startActivity(intent);
			List_Detail_Qs.this.finish();
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
			progress = new ProgressDialog(List_Detail_Qs.this);
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
			rpc.addProperty("type", "1");// 类型
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
				// 获取返回的结果
				qs_pic_status = 0;
			} else {
				qs_pic_status = 1;
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
			String methodName = "Agricultural_upload_inst_data";
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
			rpc.addProperty("apply_code1", apply_code);// 申请单号
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
				Toast.makeText(List_Detail_Qs.this, "保存成功！", Toast.LENGTH_LONG)
						.show();
				Intent intent = new Intent(List_Detail_Qs.this,
						MainActivity.class);
				startActivity(intent);

			} else if (("failed").equals(result)) {
				Toast.makeText(List_Detail_Qs.this, "保存失败！", Toast.LENGTH_LONG)
						.show();
			} else if (result == null) {
				Toast.makeText(List_Detail_Qs.this, "网络异常！", Toast.LENGTH_LONG)
						.show();
			}

		}

	}

	private class Imag_upload_task extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			progress_url = new ProgressDialog(List_Detail_Qs.this);
			progress_url.setTitle("数据上传中");
			progress_url.setCancelable(false);
			progress_url.setMessage("请稍后...");
			progress_url.show();
			progress_url.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface arg0) {
					// TODO Auto-generated method stub
					cancel(true);
					Toast.makeText(List_Detail_Qs.this, "您已取消上传！",
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
			String methodName = "uploadImage_inst";
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
				Toast.makeText(List_Detail_Qs.this, "网络异常！", Toast.LENGTH_LONG)
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
				Toast.makeText(List_Detail_Qs.this, "图片上传失败！",
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
		Intent intent = new Intent(List_Detail_Qs.this, MainActivity.class);
		startActivity(intent);
		List_Detail_Qs.this.finish();
	}
}
