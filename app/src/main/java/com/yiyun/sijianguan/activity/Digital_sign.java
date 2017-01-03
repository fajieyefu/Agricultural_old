package com.yiyun.sijianguan.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.yiyun.sijianguan.R;
import com.yiyun.sijianguan.common.BaseActivity;
import com.yiyun.sijianguan.common.Common_data;
import com.yiyun.sijianguan.common.MainView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

//@TargetApi(8)
public class Digital_sign extends BaseActivity implements OnClickListener {
	private MainView view;
	public static int screenW, screenH;
	String type;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		screenW = getWindowManager().getDefaultDisplay().getWidth();
		screenH = getWindowManager().getDefaultDisplay().getHeight();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		setContentView(R.layout.digital_sign);
		view = (MainView) findViewById(R.id.mainView1);
		SharedPreferences sp = getSharedPreferences("cahce", MODE_PRIVATE);
		type = sp.getString("type", "1");
		findViewById(R.id.iv_btn_save).setOnClickListener(this);
		findViewById(R.id.iv_btn_clear).setOnClickListener(this);
		findViewById(R.id.iv_btn_back).setOnClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.mi_exit:
			finish();
			System.exit(0);
			break;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_btn_clear:
			view.clearSignature();
			break;
		case R.id.iv_btn_back:

			Digital_sign.this.finish();
			break;
		case R.id.iv_btn_save: {
			try {
				String sdState = Environment.getExternalStorageState(); // 判断sd卡是否存在
				// 检查SD卡是否可用
				if (!sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
					Toast.makeText(this, "SD卡未准备好！", Toast.LENGTH_SHORT).show();
					break;
				}
				// 获取系统图片存储路径
				File path = Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
				if (!path.exists()) {
					path.mkdirs();
				}
				// 根据当前时间生成图片名称
				String name = new SimpleDateFormat("yyyyMMdd_HHmmss")
						.format(new Date()) + ".png";
				// 合成完整路径，注意 / 分隔符
				String image_path = path.getPath() + "/" + name;
				SharedPreferences sps = getSharedPreferences("image_info",
						MODE_PRIVATE);
				Editor editore = sps.edit();
				editore.putString(Common_data.image_path, image_path);
				editore.putString(Common_data.image_name, name);
				editore.commit();
				view.saveToFile(image_path);
				if (type.equals("1")) {
					Intent intent = new Intent(Digital_sign.this,
							List_Detail_Qs.class);
					intent.putExtra("status", 1);
					startActivity(intent);
				} else if (type.equals("2")) {
					Intent intent = new Intent(Digital_sign.this,
							List_Detail.class);
					intent.putExtra("status", 1);
					startActivity(intent);
				}else if (type.equals("3")) {
					Intent intent = new Intent(Digital_sign.this,
							Contract_Detail.class);
					intent.putExtra("status", 1);
					startActivity(intent);
				}else if (type.equals("4")){
					Intent intent = new Intent(Digital_sign.this,
							Seal_Detail.class);
					intent.putExtra("status", 1);
					startActivity(intent);
				}


				Digital_sign.this.finish();
			} catch (FileNotFoundException e) {
				Toast.makeText(this, "保存失败！\n" + e, Toast.LENGTH_LONG).show();
			}
			break;
		}
		}
	}
}
