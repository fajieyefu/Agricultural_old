package com.yiyun.sijianguan.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

import com.yiyun.sijianguan.R;
import com.yiyun.sijianguan.common.BaseActivity;
import com.yiyun.sijianguan.common.Common_data;
import com.yiyun.sijianguan.common.ConnectionDetector;
import com.yiyun.sijianguan.common.RefreshableView;
import com.yiyun.sijianguan.common.SealAdapter;
import com.yiyun.sijianguan.common.SealApplyInfo;

/**
 * Created by qiancheng on 2016/12/22.
 */
public class Seal_Activity extends BaseActivity {
	ListView lv;
	List<SealApplyInfo> list_info = new ArrayList<SealApplyInfo>();
	String username, search_Str, village = "", work_type = "";
	SealAdapter myAdapter;
	Button refresh;
	//	Spinner search_spinner;
	ConnectionDetector cd;
	RefreshableView refreshableView;
	private final int REFRSH_PULL = 1, DATA_NULL = 2, SPINNER_SEARCH = 3;
	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
				case REFRSH_PULL:
					new get_list_task().execute();
					refreshableView.finishRefreshing();
					break;
				case SPINNER_SEARCH:
					new get_list_task().execute();
					break;

			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contract_activity);
		TextView titleText = (TextView) findViewById(R.id.contract_main_top);
		titleText.setText("用章列表");
		lv = (ListView) findViewById(R.id.contract_list);
		cd = new ConnectionDetector(Seal_Activity.this);
		refresh = (Button) findViewById(R.id.contract_refresh);
		refreshableView = (RefreshableView) findViewById(R.id.contract_refreshable_view);
		SharedPreferences sp = getSharedPreferences(Common_data.share_name,
				MODE_PRIVATE);
		username = sp.getString(Common_data.username, "");
		search_Str = sp.getString("search_flag", "0");
		village = sp.getString("village", "");
		work_type = sp.getString("work_type", "");
		myAdapter = new SealAdapter(this,list_info);
		lv.setAdapter(myAdapter);
		new get_list_task().execute();
		lv.setOnItemClickListener(new lv_onclick());
		refresh.setOnClickListener(refresh_listener);
		refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
			@Override
			public void onRefresh() {
				Message msg = new Message();
				msg.what = REFRSH_PULL;
				mHandler.sendMessage(msg);

			}
		}, 0);
		SharedPreferences sp_uni = getSharedPreferences("cahce", MODE_PRIVATE);
		SharedPreferences sps = getSharedPreferences("image_info", MODE_PRIVATE);
		SharedPreferences.Editor editor2 = sp_uni.edit();
		SharedPreferences.Editor editor3 = sps.edit();
		editor2.clear();
		editor3.clear();
		editor2.commit();
		editor3.commit();
	}

	public View.OnClickListener refresh_listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			new get_list_task().execute();
		}
	};

	public class lv_onclick implements AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int location,
								long arg3) {
			// TODO Auto-generated method stub

			SharedPreferences sp = getSharedPreferences(Common_data.share_name,
					MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			editor.putString("Contract_code", list_info.get(location)
					.getApply_code());
			editor.putString("use_man", list_info.get(location)
					.getUse_man());
			editor.putString("use_address", list_info.get(location)
					.getUse_address());
			editor.putString("apply_text", list_info.get(location)
					.getApply_text());
			editor.putString("area_big", list_info.get(location)
					.getArea_big());
			editor.putString("area_small", list_info.get(location)
					.getArea_small());
			editor.putString("apply_date", list_info.get(location)
					.getApply_date());
			editor.putString("audit_order", list_info.get(location)
					.getAudit_order());
			editor.putString("apply_man_name",list_info.get(location).getApply_man_name());
			editor.commit();
			Intent intent = new Intent(Seal_Activity.this,
					Seal_Detail.class);
			startActivity(intent);
			Seal_Activity.this.finish();

		}

	}

	private class get_list_task extends AsyncTask<Void, Void, String> {
		private ProgressDialog progress;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			progress = new ProgressDialog(Seal_Activity.this);
			progress.setTitle("数据加载中");
			progress.setCancelable(false);
			progress.setMessage("请稍后...");
			progress.show();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String methodName = "Select_gongzhang_Info";
			// 命名空间
			String nameSpace = Common_data.nameSpace;
			// EndPoint
			String endPoint = Common_data.endPoint;
			// SOAP Action
			String soapAction = nameSpace + methodName;

			// 指定WebService的命名空间和调用的方法名
			SoapObject rpc = new SoapObject(nameSpace, methodName);

			// 设置需调用WebService接口需要传入的参数
			rpc.addProperty("audit_man", username);
			rpc.addProperty("village", village);
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
			Log.i("Seal_Activity",result1);
			return result1;
		}

		protected void onPostExecute(String result) {
			if (progress != null) {
				progress.dismiss();
			}
			if (result.equals("data is null")) {
				list_info.clear();
				myAdapter.notifyDataSetChanged();
//				TextView emptyView = new TextView(Seal_Activity.this);
//				emptyView.setLayoutParams(new ViewGroup.LayoutParams(
//						ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
//				emptyView.setText(getResources().getString(R.string.datanull));
//				emptyView.setGravity(Gravity.CENTER_HORIZONTAL
//						| Gravity.CENTER_VERTICAL);
//				emptyView.setVisibility(View.GONE);
//				((ViewGroup) lv.getParent()).addView(emptyView);
//				lv.setEmptyView(emptyView);

			} else {
				try {
					list_info.clear();
					JSONObject object = new JSONObject(result);
					JSONArray jsonArray = object.getJSONArray("Table");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObj = jsonArray.getJSONObject(i);
						SealApplyInfo sealApplyInfo = new SealApplyInfo();
						sealApplyInfo.setApply_code(jsonObj.optString("apply_code"));
						sealApplyInfo.setUse_man(jsonObj.optString("use_man"));
						sealApplyInfo.setUse_address(jsonObj.optString("use_address"));
						sealApplyInfo.setApply_text(jsonObj.optString("apply_text"));
						sealApplyInfo.setArea_big(jsonObj.optString("area_big"));
						sealApplyInfo.setArea_small(jsonObj.optString("area_small"));
						sealApplyInfo.setApply_date(jsonObj.optString("apply_date"));
						sealApplyInfo.setAudit_order(jsonObj.optString("audit_order"));
						sealApplyInfo.setApply_man_name(jsonObj.optString("apply_man_name"));
						list_info.add(sealApplyInfo);
					}
					myAdapter.notifyDataSetChanged();
				} catch (JSONException e) {
					System.out.println("Jsons parse error !");
					e.printStackTrace();
				}
			}

		}
	}

	@Override
	public void onBackPressed() {
		SharedPreferences sp = getSharedPreferences(Common_data.share_name,
				MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove("bill_type_flag");
		editor.remove("search_flag");
		editor.commit();
		Intent intent = new Intent();
		intent.setClass(Seal_Activity.this, Fore_MainActivity.class);
		startActivity(intent);
		Seal_Activity.this.finish();
	}
}
