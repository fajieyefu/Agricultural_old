package com.yiyun.sijianguan.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetector {
	private Context _context;

	public ConnectionDetector(Context context) {
		this._context = context;
	}

	public boolean isConn() {
		boolean bisConnFlag = false;
		ConnectivityManager conManager = (ConnectivityManager) _context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = conManager.getActiveNetworkInfo();
		if (network != null) {
			bisConnFlag = conManager.getActiveNetworkInfo().isAvailable();
		}
//		if (network == null)
//			throw new Exception("当前没有网络连接，请打开wifi或数据连接后重试！");
//
//		int net_type = network.getType();
//
//		// 若当前连接是wifi连接，则使用wifi进行连接
//		if (net_type == ConnectivityManager.TYPE_WIFI) {
//
//			throw new Exception("请您关闭WIFI，只打开数据连接后重试！");
//		}

		return bisConnFlag;
	}

	public void setNetworkMethod(final Context context) {
		// 提示对话框
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("网络设置提示")
				.setMessage("网络连接不可用,是否进行设置?")
				.setPositiveButton("设置", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent intent = null;
						// 判断手机系统的版本 即API大于10 就是3.0或以上版本
						if (android.os.Build.VERSION.SDK_INT > 10) {
							intent = new Intent(
									android.provider.Settings.ACTION_WIRELESS_SETTINGS);
						} else {
							intent = new Intent();
							ComponentName component = new ComponentName(
									"com.android.settings",
									"com.android.settings.WirelessSettings");
							intent.setComponent(component);
							intent.setAction("android.intent.action.VIEW");
						}
						context.startActivity(intent);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						Activity activity = (Activity) context;
						activity.finish();
					}
				}).show();
	}
}
