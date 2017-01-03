package com.yiyun.sijianguan.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.yiyun.sijianguan.R;
import com.yiyun.sijianguan.common.BaseActivity;
import com.yiyun.sijianguan.common.Common_data;
import com.yiyun.sijianguan.common.ConnectionDetector;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Process_Detail extends BaseActivity {
	TextView process_detail_nm_top;
	String apply_type, apply_code;
	ConnectionDetector cd;
	String type = "1";
	Button process_detail_nm_exit;
	private static String TAG="Process_Detail";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.process_detail_nm);
		process_detail_nm_exit = (Button) findViewById(R.id.process_detail_nm_exit);
		process_detail_nm_top = (TextView) findViewById(R.id.process_detail_nm_top);
		type = getIntent().getStringExtra("type").toString();
		apply_type = getIntent().getStringExtra("apply_type").toString();
		apply_code = getIntent().getStringExtra("apply_code").toString();
		Log.i(TAG,apply_code);
		cd = new ConnectionDetector(Process_Detail.this);
		process_detail_nm_exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Process_Detail.this.finish();
			}
		});
		if (cd.isConn()) {
			if (!apply_type.equals("") || apply_type != null) {
				if (apply_type.equals("一般")) {
					process_detail_nm_top.setText(getResources().getString(
							R.string.process_deep) + "(一般)");
				} else if (apply_type.equals("重大")) {
					process_detail_nm_top.setText(getResources().getString(
							R.string.process_deep) + "(重大)");
				} else if (apply_type.equals("特大")) {
					process_detail_nm_top.setText(getResources().getString(
							R.string.process_deep) + "(特大)");
				}else if (apply_type.equals("公章")){
					process_detail_nm_top.setText("公章审批流程");

				}
				else {
					// 合同
					process_detail_nm_top.setText("合同审批流程");
				}
				new get_process_info_task().execute();
			}
		} else {
			cd.setNetworkMethod(Process_Detail.this);
		}

	}

	private class get_process_info_task extends
			AsyncTask<Void, Void, List<List_info>> {
		private ProgressDialog progress;

		@Override
		protected void onPreExecute() {
			progress = new ProgressDialog(Process_Detail.this);
			progress.setTitle("数据加载中");
			progress.setCancelable(false);
			progress.setMessage("请稍后...");
			progress.show();
		}

		@Override
		protected List<List_info> doInBackground(Void... arg0) {
			String methodName = "get_process_info";
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
			rpc.addProperty("type", type);
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
			if (object != null) {
				// 获取返回的结果
				String result = object.getProperty(0).toString();
				Log.i(TAG,result);
				List<List_info> mlList = new ArrayList<List_info>();
				try {
					JSONArray jsonObjs = new JSONObject(result)
							.getJSONArray("Table");
					for (int i = 0; i < jsonObjs.length(); i++) {
						JSONObject jsonObj = jsonObjs.getJSONObject(i);
						List_info list_info = new List_info();
						list_info.setAudit_order(jsonObj
								.getString("audit_order"));
						list_info.setAudit_man_name(jsonObj
								.getString("audit_man_name"));
						list_info
								.setAudit_time(jsonObj.getString("audit_time"));
						list_info
								.setAudit_flag(jsonObj.getString("audit_flag"));
						list_info
								.setAudit_text(jsonObj.getString("audit_text"));
						mlList.add(list_info);
					}
				} catch (JSONException e) {
					System.out.println("Jsons parse error !");
					e.printStackTrace();
				}
				return mlList;
			} else {
				return null;
			}
		}

		protected void onPostExecute(List<List_info> list) {
			if (progress != null) {
				progress.dismiss();
			}
			if (list != null) {
				ViewGroup linearlayout_1 = (ViewGroup) findViewById(R.id.linearlayout_1);
				for (int i = 0; i < list.size(); i++) {
					GridLayout gridLayout = new GridLayout(Process_Detail.this);
					ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT);
					gridLayout.setColumnCount(4);
					gridLayout.setRowCount(4);
					TextView tv[] = new TextView[10];
					for (int j = 0; j < 10; j++) {
						tv[j] = new TextView(Process_Detail.this);
						tv[j].setId(j);
						tv[j].setLayoutParams(new LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT));
						// 指定该组件所在的行
						GridLayout.Spec rowSpec = GridLayout.spec(j / 4);
						// 指定该组件所在列
						GridLayout.Spec columnSpec = GridLayout.spec(j % 4);
						GridLayout.LayoutParams params = new GridLayout.LayoutParams(
								rowSpec, columnSpec);
						params.setMargins(10, 5, 10, 5);
						gridLayout.addView(tv[j], params);
					}

					gridLayout.setLayoutParams(layoutParams);
					gridLayout
							.setBackgroundResource(R.drawable.circle_background);
					gridLayout.setPadding(5, 5, 5, 5);
					gridLayout.setUseDefaultMargins(true);
					linearlayout_1.addView(gridLayout);
					linearlayout_1.setPadding(5, 5, 5, 5);
					tv[0].setText(getResources().getString(R.string.sp_man));
					TextPaint tp = tv[0].getPaint();
					tp.setFakeBoldText(true);
					tv[1].setText(list.get(i).getAudit_man_name());
					tv[2].setText(getResources().getString(R.string.sp_time));
					TextPaint tp2 = tv[2].getPaint();
					tp2.setFakeBoldText(true);
					tv[3].setText(list.get(i).getAudit_time());
					tv[4].setText(getResources().getString(R.string.sp_status));
					TextPaint tp4 = tv[4].getPaint();
					tp4.setFakeBoldText(true);
					tv[5].setText(setstatus(list.get(i).getAudit_flag()));
					tv[6].setText(getResources().getString(R.string.sp_turn));
					TextPaint tp6 = tv[6].getPaint();
					tp6.setFakeBoldText(true);
					tv[7].setText(list.get(i).getAudit_order());
					tv[8].setText(getResources().getString(R.string.sp_des));
					TextPaint tp8 = tv[8].getPaint();
					tp8.setFakeBoldText(true);
					tv[9].setText(list.get(i).getAudit_text());
				}
			} else {
				Toast.makeText(Process_Detail.this, "数据异常！", Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	public String setstatus(String status) {

		if (status.equals("A")) {
			status = "未提交";
		} else if (status.equals("B")) {
			status = "审核中";
		} else if (status.equals("Y")) {
			status = "审核通过";
		} else if (status.equals("N")) {
			status = "审核退回";
		} else {
			Toast.makeText(Process_Detail.this, "出现未知状态！", Toast.LENGTH_LONG)
					.show();
		}
		return status;
	}

	public class List_info {

		String audit_order, audit_man_name, audit_time, audit_flag, audit_text;

		public String getAudit_order() {
			return audit_order;
		}

		public void setAudit_order(String audit_order) {
			this.audit_order = audit_order;
		}

		public String getAudit_man_name() {
			return audit_man_name;
		}

		public void setAudit_man_name(String audit_man_name) {
			this.audit_man_name = audit_man_name;
		}

		public String getAudit_time() {
			return audit_time;
		}

		public void setAudit_time(String audit_time) {
			this.audit_time = audit_time;
		}

		public String getAudit_flag() {
			return audit_flag;
		}

		public void setAudit_flag(String audit_flag) {
			this.audit_flag = audit_flag;
		}

		public String getAudit_text() {
			return audit_text;
		}

		public void setAudit_text(String audit_text) {
			this.audit_text = audit_text;
		}

		public List_info() {
			super();
		}

	}
}
