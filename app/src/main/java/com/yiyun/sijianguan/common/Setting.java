package com.yiyun.sijianguan.common;

import com.yiyun.sijianguan.R;
import com.yiyun.sijianguan.activity.HomeActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Setting extends Activity {

	EditText setting_web_address, setting_dkh, setting_dkh_img;
	Button setting_save, setting_cancel;
	SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		sp = getSharedPreferences("common_url_data", MODE_PRIVATE);
		setting_web_address = (EditText) findViewById(R.id.setting_web_address);
		setting_dkh = (EditText) findViewById(R.id.setting_dkh);
		setting_dkh_img = (EditText) findViewById(R.id.setting_dkh1);
		setting_web_address.setText(sp.getString("ip_str", ""));
		setting_dkh.setText(sp.getString("port_str", ""));
		setting_dkh_img.setText(sp.getString("port_img_str", ""));
		setting_save = (Button) findViewById(R.id.setting_save);
		setting_cancel = (Button) findViewById(R.id.setting_cancel);
		setting_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String ip_str = setting_web_address.getText().toString();
				String port_str = setting_dkh.getText().toString();
				String port_img_str = setting_dkh_img.getText().toString();
				String endpoint = "http://" + ip_str + ":" + port_str
						+ "/As/Service1.asmx";
				String url_server = "http://" + ip_str + ":" + port_str
						+ "/As/apk/version_info.xml";
				String image_url = "http://" + ip_str + ":" + port_img_str
						+ "/upload/oas/";
				
				Editor editor = sp.edit();
				editor.putString("ip_str", ip_str);
				editor.putString("port_str", port_str);
				editor.putString("port_img_str", port_img_str);
				editor.putString("endPoint", endpoint);
				editor.putString("url_server", url_server);
				editor.putString("image_url", image_url);
				editor.commit();
				Intent intent = new Intent(Setting.this, HomeActivity.class);
				startActivity(intent);
				Setting.this.finish();
			}
		});
		setting_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(Setting.this, HomeActivity.class);
				startActivity(intent);
				Setting.this.finish();
			}
		});

	}

}
