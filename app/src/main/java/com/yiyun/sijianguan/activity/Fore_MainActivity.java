package com.yiyun.sijianguan.activity;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import com.yiyun.sijianguan.R;
import com.yiyun.sijianguan.common.BaseActivity;
import com.yiyun.sijianguan.common.Common_data;
import com.yiyun.sijianguan.common.ConnectionDetector;
import com.yiyun.sijianguan.common.ExitApplication;
import com.yiyun.sijianguan.common.My_Village_Adapter;
import com.yiyun.sijianguan.common.RefreshableView;
import com.yiyun.sijianguan.common.RefreshableView.PullToRefreshListener;
import com.yiyun.sijianguan.common.ToastView;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

public class Fore_MainActivity extends BaseActivity {

	ListView lv;
	List<GetListInfo> list_info = null;
	String username;
	My_Village_Adapter myAdapter;
	Button refresh, main_exit;
	ConnectionDetector cd;
	RefreshableView refreshableView;
	private final int REFRSH_PULL = 1;
	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REFRSH_PULL:
				new get_list_task().execute();
				refreshableView.finishRefreshing();
				break;

			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fore_activity_main);
		lv = (ListView) findViewById(R.id.fore_main_list);
		cd = new ConnectionDetector(Fore_MainActivity.this);
		main_exit = (Button) findViewById(R.id.fore_main_exit);
		refresh = (Button) findViewById(R.id.fore_refresh);
		refreshableView = (RefreshableView) findViewById(R.id.fore_refreshable_view);
		SharedPreferences sp = getSharedPreferences(Common_data.share_name,
				MODE_PRIVATE);
		username = sp.getString(Common_data.username, "");
		lv.setOnItemClickListener(new lv_onclick());
		refresh.setOnClickListener(refresh_listener);
		new get_list_task().execute();
		main_exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(Fore_MainActivity.this, Login_Activity.class);
				startActivity(intent);
				Fore_MainActivity.this.finish();
				ExitApplication.getInstance().exit();
			}
		});
		refreshableView.setOnRefreshListener(new PullToRefreshListener() {
			@Override
			public void onRefresh() {
				Message msg = new Message();
				msg.what = REFRSH_PULL;
				mHandler.sendMessage(msg);

			}
		}, 0);

	}

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

			if (list_info.get(arg2).getWORK_TYPE().toString().equals("申请|请示")) {
				Intent intent = new Intent(Fore_MainActivity.this,
						MainActivity.class);
				SharedPreferences sp = getSharedPreferences(
						Common_data.share_name, MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putString("village", list_info.get(arg2).getArea_small()
						.toString());
				editor.commit();
				startActivity(intent);
				Fore_MainActivity.this.finish();
			} else  if (list_info.get(arg2).getWORK_TYPE().toString().equals("合同")){
				Intent intent = new Intent(Fore_MainActivity.this,
						Contract_Activity.class);
				SharedPreferences sp = getSharedPreferences(
						Common_data.share_name, MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putString("village", list_info.get(arg2).getArea_small()
						.toString());
				editor.putString("work_type", list_info.get(arg2)
						.getWORK_TYPE().toString());
				editor.commit();
				startActivity(intent);
				Fore_MainActivity.this.finish();
			}else{
				Intent intent = new Intent(Fore_MainActivity.this,
						Seal_Activity.class);
				SharedPreferences sp = getSharedPreferences(
						Common_data.share_name, MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putString("village", list_info.get(arg2).getArea_small()
						.toString());
				editor.putString("work_type", list_info.get(arg2)
						.getWORK_TYPE().toString());
				editor.commit();
				startActivity(intent);
				Fore_MainActivity.this.finish();

			}

		}

	}

	private class get_list_task extends AsyncTask<Void, Void, String> {
		private ProgressDialog progress;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			progress = new ProgressDialog(Fore_MainActivity.this);
			progress.setTitle("数据加载中");
			progress.setCancelable(false);
			progress.setMessage("请稍后...");
			progress.show();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String methodName = "Select_Apply_village_Info";
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
			// 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER12);
			envelope.bodyOut = rpc;
			// 设置是否调用的是dotNet开发的WebService
			envelope.dotNet = true;
			HttpTransportSE transport = new HttpTransportSE(endPoint, 5000);
			// 调用WebService
			try {
				transport.call(soapAction, envelope);
			} catch (ConnectException e) {
				// TODO: handle exception
				e.printStackTrace();
				try {
					ToastView toast = new ToastView(Fore_MainActivity.this,
							"网络异常，请稍后再试！");
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				} catch (RuntimeException e2) {
					// TODO: handle exception
					e.printStackTrace();
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
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
				myAdapter = new My_Village_Adapter(Fore_MainActivity.this, null);
				myAdapter.notifyDataSetChanged();
				lv.setAdapter(myAdapter);
				TextView emptyView = new TextView(Fore_MainActivity.this);
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
						getListInfo.setCount_num(jsonObj.getString("NUM"));
						getListInfo.setArea_small(jsonObj
								.getString("area_small"));
						getListInfo
								.setWORK_TYPE(jsonObj.getString("WORK_TYPE"));
						list_info.add(getListInfo);
					}
					myAdapter = new My_Village_Adapter(Fore_MainActivity.this,
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
//		Intent intent = new Intent();
//		intent.setClass(Fore_MainActivity.this, Login_Activity.class);
//		startActivity(intent);
		Fore_MainActivity.this.finish();
//		ExitApplication.getInstance().exit();
	}

}
