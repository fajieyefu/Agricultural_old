package com.yiyun.sijianguan.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;

public class Dialog_Cancle {

	Activity mActivity;

	public Dialog_Cancle(Activity activity) {
		// TODO Auto-generated constructor stub
		mActivity = activity;
	}

	// 点击取消按钮
	public void dialog() {
		AlertDialog.Builder builder = new Builder(mActivity);
		builder.setMessage("确定要退出吗？");
		builder.setPositiveButton("确认",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						mActivity.finish();

					}
				});
		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

					}
				});
		builder.create().show();
	}
}
