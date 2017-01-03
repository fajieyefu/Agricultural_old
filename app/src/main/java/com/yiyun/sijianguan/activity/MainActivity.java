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
import com.yiyun.sijianguan.common.MyAdapter;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends BaseActivity {

	ListView lv;
	List<GetListInfo> list_info = null;
	String username, bill_type = "1", search_Str, village = "",work_type="";
	MyAdapter myAdapter;
	Button refresh;
	Spinner search_spinner;
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
		setContentView(R.layout.activity_main);
		lv = (ListView) findViewById(R.id.main_list);
		cd = new ConnectionDetector(MainActivity.this);
		refresh = (Button) findViewById(R.id.refresh);
		search_spinner = (Spinner) findViewById(R.id.search_spinner);
		refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
		SharedPreferences sp = getSharedPreferences(Common_data.share_name,
				MODE_PRIVATE);
		username = sp.getString(Common_data.username, "");
		bill_type = sp.getString("bill_type_flag", "0");
		search_Str = sp.getString("search_flag", "0");
		village = sp.getString("village", "");
		work_type = sp.getString("work_type", "");

		lv.setOnItemClickListener(new lv_onclick());
		search_spinner.setOnItemSelectedListener(search_spinnerr);
		refresh.setOnClickListener(refresh_listener);
		refreshableView.setOnRefreshListener(new PullToRefreshListener() {
			@Override
			public void onRefresh() {
				Message msg = new Message();
				msg.what = REFRSH_PULL;
				mHandler.sendMessage(msg);

			}
		}, 0);
		SharedPreferences sp_uni = getSharedPreferences("cahce", MODE_PRIVATE);
		SharedPreferences sps = getSharedPreferences("image_info", MODE_PRIVATE);
		Editor editor2 = sp_uni.edit();
		Editor editor3 = sps.edit();
		editor2.clear();
		editor3.clear();
		editor2.commit();
		editor3.commit();
	}

	public OnItemSelectedListener search_spinnerr = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			TextView tv = (TextView) view;
			tv.setTextColor(getResources().getColor(R.color.darkblue));
			search_Str = String.valueOf(search_spinner
					.getSelectedItemPosition());

			if (search_Str.equals("1")) {
				search_spinner.setSelection(1);
				bill_type = "1";
			} else if (search_Str.equals("2")) {
				search_spinner.setSelection(2);
				bill_type = "2";
			} else if (search_Str.equals("0")) {
				search_spinner.setSelection(0);
				bill_type = "0";
			}
			if (Integer.valueOf(search_Str) < 0) {
				search_Str = "";
			}
			Message msg = new Message();
			msg.what = SPINNER_SEARCH;
			mHandler.sendMessage(msg);

		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			search_Str = "";
		}
	};

	public OnClickListener refresh_listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			new get_list_task().execute();
		}
	};

	public class lv_onclick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			RelativeLayout relativeLayout = (RelativeLayout) arg1;
			TextView text1 = (TextView) relativeLayout
					.findViewById(R.id.apply_man);
			TextView text2 = (TextView) relativeLayout
					.findViewById(R.id.apply_num);
			TextView text3 = (TextView) relativeLayout
					.findViewById(R.id.flower_name);
			TextView text4 = (TextView) relativeLayout
					.findViewById(R.id.flower_type);
			TextView text5 = (TextView) relativeLayout
					.findViewById(R.id.apply_date);
			TextView text6 = (TextView) relativeLayout
					.findViewById(R.id.apply_money);
			TextView text7 = (TextView) relativeLayout
					.findViewById(R.id.apply_area);
			TextView text8 = (TextView) relativeLayout
					.findViewById(R.id.apply_text);
			TextView text9 = (TextView) relativeLayout
					.findViewById(R.id.audit_order);
			TextView text10 = (TextView) relativeLayout
					.findViewById(R.id.contract_code);
			SharedPreferences sp = getSharedPreferences(Common_data.share_name,
					MODE_PRIVATE);
			Editor editor = sp.edit();
			if (!text1.getText().toString().isEmpty()
					|| text1.getText().toString() != null) {
				editor.putString("apply_man", text1.getText().toString());
			} else {
				editor.putString("apply_man", "");
			}

			if (!text2.getText().toString().isEmpty()
					|| text2.getText().toString() != null) {
				editor.putString("apply_num", text2.getText().toString());
			} else {
				editor.putString("apply_num", "");
			}
			if (!text3.getText().toString().isEmpty()
					|| text3.getText().toString() != null) {
				editor.putString("flower_name", text3.getText().toString());
			} else {
				editor.putString("flower_name", "");
			}
			if (!text4.getText().toString().isEmpty()
					|| text4.getText().toString() != null) {
				editor.putString("flower_type", text4.getText().toString());
			} else {
				editor.putString("flower_type", "");
			}
			if (!text5.getText().toString().isEmpty()
					|| text5.getText().toString() != null) {
				editor.putString("apply_date", text5.getText().toString());
			} else {
				editor.putString("apply_date", "");
			}
			if (!text6.getText().toString().isEmpty()
					|| text6.getText().toString() != null) {
				editor.putString("apply_money", text6.getText().toString());
			} else {
				editor.putString("apply_money", "");
			}
			if (!text7.getText().toString().isEmpty()
					|| text7.getText().toString() != null) {
				editor.putString("apply_area", text7.getText().toString());
			} else {
				editor.putString("apply_area", "");
			}
			if (!text8.getText().toString().isEmpty()
					|| text8.getText().toString() != null) {
				editor.putString("apply_text", text8.getText().toString());
			} else {
				editor.putString("apply_text", "");
			}
			if (!text9.getText().toString().isEmpty()
					|| text9.getText().toString() != null) {
				editor.putString("audit_order", text9.getText().toString());
			} else {
				editor.putString("audit_order", "");
			}
			if (!text10.getText().toString().isEmpty()
					|| text10.getText().toString() != null) {
				editor.putString("contract_code", text10.getText().toString());
			} else {
				editor.putString("contract_code", "");
			}
			editor.putString("bill_type_flag", bill_type);// spinner
			editor.putString("search_flag", search_Str);// spinner
			editor.commit();
			String type = text2.getText().toString();
			String apply_type = type.substring(type.length() - 3,
					type.length() - 1);
			if (apply_type.equals("请示")) {
				Intent intent = new Intent(MainActivity.this,
						List_Detail_Qs.class);
				startActivity(intent);
				MainActivity.this.finish();
			} else if (apply_type.equals("申请")) {
				Intent intent = new Intent(MainActivity.this, List_Detail.class);
				startActivity(intent);
				MainActivity.this.finish();
			}

		}

	}

	private class get_list_task extends AsyncTask<Void, Void, String> {
		private ProgressDialog progress;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			progress = new ProgressDialog(MainActivity.this);
			progress.setTitle("数据加载中");
			progress.setCancelable(false);
			progress.setMessage("请稍后...");
			progress.show();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String methodName = "Select_Apply_Info";
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
			rpc.addProperty("bill_type", bill_type);
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
			Log.i("MainActivity",result1);
			return result1;
		}

		protected void onPostExecute(String result) {
			if (progress != null) {
				progress.dismiss();
			}
			if (result.equals("data is null")) {
				myAdapter = new MyAdapter(MainActivity.this, null);
				myAdapter.notifyDataSetChanged();
				lv.setAdapter(myAdapter);
				TextView emptyView = new TextView(MainActivity.this);
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
						getListInfo.setApply_code(jsonObj
								.getString("apply_code"));
						getListInfo.setFlower_name(jsonObj
								.getString("flower_name"));
						getListInfo.setFlower_type(jsonObj
								.getString("flower_type"));
						getListInfo.setApply_man_name(jsonObj
								.getString("Apply_man_name"));
						getListInfo
								.setApply_amt(jsonObj.getString("Apply_amt"));
						getListInfo.setArea_big(jsonObj.getString("area_big"));
						getListInfo.setArea_small(jsonObj
								.getString("area_small"));
						getListInfo.setStart_time(jsonObj
								.getString("start_time"));
						getListInfo.setApply_text(jsonObj
								.getString("Apply_text"));
						getListInfo.setAudit_order(jsonObj
								.getString("audit_order"));
						getListInfo
								.setBill_type(jsonObj.getString("bill_type"));
						getListInfo.setContract_code(jsonObj
								.getString("contract_code"));
						list_info.add(getListInfo);
					}
					myAdapter = new MyAdapter(MainActivity.this, list_info);
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
		intent.setClass(MainActivity.this, Fore_MainActivity.class);
		startActivity(intent);
		MainActivity.this.finish();
	}

}
