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
import com.yiyun.sijianguan.common.My_Contract_Adapter;
import com.yiyun.sijianguan.common.RefreshableView;
import com.yiyun.sijianguan.common.RefreshableView.PullToRefreshListener;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class Contract_Activity extends BaseActivity {

	ListView lv;
	List<GetListInfo> list_info = null;
	String username, search_Str, village = "", work_type = "";
	My_Contract_Adapter myAdapter;
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
		lv = (ListView) findViewById(R.id.contract_list);
		cd = new ConnectionDetector(Contract_Activity.this);
		refresh = (Button) findViewById(R.id.contract_refresh);
		refreshableView = (RefreshableView) findViewById(R.id.contract_refreshable_view);
		SharedPreferences sp = getSharedPreferences(Common_data.share_name,
				MODE_PRIVATE);
		username = sp.getString(Common_data.username, "");
		search_Str = sp.getString("search_flag", "0");
		village = sp.getString("village", "");
		work_type = sp.getString("work_type", "");
		new get_list_task().execute();
		lv.setOnItemClickListener(new lv_onclick());
		 refresh.setOnClickListener(refresh_listener);
		 refreshableView.setOnRefreshListener(new PullToRefreshListener() {
		 @Override
		 public void onRefresh() {
		 Message msg = new Message();
		 msg.what = REFRSH_PULL;
		 mHandler.sendMessage(msg);
		
		 }
		 }, 0);
//		 search_spinner = (Spinner)
//		 findViewById(R.id.contract_search_spinner);
//		 search_spinner.setOnItemSelectedListener(search_spinnerr);
		SharedPreferences sp_uni = getSharedPreferences("cahce", MODE_PRIVATE);
		SharedPreferences sps = getSharedPreferences("image_info", MODE_PRIVATE);
		Editor editor2 = sp_uni.edit();
		Editor editor3 = sps.edit();
		editor2.clear();
		editor3.clear();
		editor2.commit();
		editor3.commit();
	}

//	public OnItemSelectedListener search_spinnerr = new OnItemSelectedListener() {
//
//		@Override
//		public void onItemSelected(AdapterView<?> parent, View view,
//				int position, long id) { // TODO Auto-generated method stub
//			TextView tv = (TextView) view;
//			tv.setTextColor(getResources().getColor(R.color.darkblue));
//			search_Str = String.valueOf(search_spinner
//					.getSelectedItemPosition());
//
//			if (search_Str.equals("1")) {
//				search_spinner.setSelection(1);
//			} else if (search_Str.equals("2")) {
//				search_spinner.setSelection(2);
//			} else if (search_Str.equals("0")) {
//				search_spinner.setSelection(0);
//			}
//			if (Integer.valueOf(search_Str) < 0) {
//				search_Str = "";
//			}
//			Message msg = new Message();
//			msg.what = SPINNER_SEARCH;
//			mHandler.sendMessage(msg);
//
//		}
//
//		@Override
//		public void onNothingSelected(AdapterView<?> parent) {
//			// TODO Auto-generated method stub
//			search_Str = "";
//		}
//	};

	public OnClickListener refresh_listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			new get_list_task().execute();
		}
	};

	public class lv_onclick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int location,
				long arg3) {
			// TODO Auto-generated method stub

			SharedPreferences sp = getSharedPreferences(Common_data.share_name,
					MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("Contract_code", list_info.get(location)
					.getContract_code());
			editor.putString("Contract_man_name", list_info.get(location)
					.getContract_man_name());
			editor.putString("Contract_name", list_info.get(location)
					.getContract_name());
			editor.putString("Contract_money", list_info.get(location)
					.getContract_money());
			editor.putString("Contract_text", list_info.get(location)
					.getContract_text());
			editor.putString("Contract_type", list_info.get(location)
					.getContract_type());
			editor.putString("Contract_date", list_info.get(location)
					.getContract_date());
			editor.putString("Area_big", list_info.get(location).getArea_big());
			editor.putString("Area_small", list_info.get(location)
					.getArea_small());
			editor.putString("Contract_A", list_info.get(location)
					.getContract_A());
			editor.putString("Contract_B", list_info.get(location)
					.getContract_B());
			editor.commit();
			Intent intent = new Intent(Contract_Activity.this,
					Contract_Detail.class);
			startActivity(intent);
			Contract_Activity.this.finish();

		}

	}

	private class get_list_task extends AsyncTask<Void, Void, String> {
		private ProgressDialog progress;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			progress = new ProgressDialog(Contract_Activity.this);
			progress.setTitle("数据加载中");
			progress.setCancelable(false);
			progress.setMessage("请稍后...");
			progress.show();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String methodName = "Select_Contract_Info";
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
			Log.i("Contract_Activity",result1);
			return result1;
		}

		protected void onPostExecute(String result) {
			if (progress != null) {
				progress.dismiss();
			}
			if (result.equals("data is null")) {
				myAdapter = new My_Contract_Adapter(Contract_Activity.this,
						null);
				myAdapter.notifyDataSetChanged();
				lv.setAdapter(myAdapter);
				TextView emptyView = new TextView(Contract_Activity.this);
				emptyView.setLayoutParams(new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				emptyView.setText(getResources().getString(R.string.datanull));
				emptyView.setGravity(Gravity.CENTER_HORIZONTAL
						| Gravity.CENTER_VERTICAL);
				emptyView.setVisibility(View.GONE);
				((ViewGroup) lv.getParent()).addView(emptyView);
				lv.setEmptyView(emptyView);

			} else {
				list_info = new ArrayList<GetListInfo>();
				try {
					JSONObject object = new JSONObject(result);
					JSONArray jsonArray = object.getJSONArray("Table");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObj = jsonArray.getJSONObject(i);
						GetListInfo getListInfo = new GetListInfo();
						getListInfo.setContract_code(jsonObj
								.getString("Contract_code"));
						getListInfo.setContract_name(jsonObj
								.getString("Contract_name"));
						getListInfo.setContract_type(jsonObj
								.getString("contract_type"));
						getListInfo.setContract_man_name(jsonObj
								.getString("contract_man_name"));
						getListInfo.setContract_money(jsonObj
								.getString("Contract_AMT"));
						getListInfo.setArea_big(jsonObj.getString("area_big"));
						getListInfo.setArea_small(jsonObj
								.getString("area_small"));
						getListInfo.setContract_date(jsonObj
								.getString("Contract_date"));
						getListInfo.setContract_text(jsonObj
								.getString("Contract_text"));
						getListInfo.setAudit_order(jsonObj
								.getString("audit_order"));
						getListInfo.setContract_A(jsonObj
								.getString("Contract_A"));
						getListInfo.setContract_B(jsonObj
								.getString("Contract_B"));
						list_info.add(getListInfo);
					}
					myAdapter = new My_Contract_Adapter(Contract_Activity.this,
							list_info);
					myAdapter.notifyDataSetChanged();
					lv.setAdapter(myAdapter);
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
		 Editor editor = sp.edit();
		 editor.remove("bill_type_flag");
		 editor.remove("search_flag");
		 editor.commit();
		Intent intent = new Intent();
		intent.setClass(Contract_Activity.this, Fore_MainActivity.class);
		startActivity(intent);
		Contract_Activity.this.finish();
	}

}
