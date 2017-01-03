package com.yiyun.sijianguan.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.yiyun.sijianguan.R;
import com.yiyun.sijianguan.common.BaseActivity;
import com.yiyun.sijianguan.common.ConnectionDetector;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Pic_res extends BaseActivity {

	Bitmap down_bitmap;
	ArrayList<String> pic_url;
	Bitmap[] bitmaps;
	ImageView[] images;
	private float scaleWidth = 1;
	private float scaleHeight = 1;
	ConnectionDetector cd;
	String activity_type = null;
	Button pic_exit;
	private final int IOEX = 1, NULLEX = 2;
	private ViewGroup group;

	Handler handler1 = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
				case IOEX:
					Toast.makeText(Pic_res.this, "找不到此文件！", Toast.LENGTH_LONG)
							.show();
					break;
				case NULLEX:
					Toast.makeText(Pic_res.this, "空指针异常！", Toast.LENGTH_LONG)
							.show();
					break;

			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popwindow);
		 group = (ViewGroup) findViewById(R.id.qs_pic_rs);
		pic_exit = (Button) findViewById(R.id.pic_exit);
		activity_type = getIntent().getStringExtra("activity_type");
		cd = new ConnectionDetector(Pic_res.this);
		pic_url = getIntent().getStringArrayListExtra("url");
		Log.i("Pic_res:", pic_url.size() + "");
		if (cd.isConn()) {
			new download_image_task().execute();

		} else {
			cd.setNetworkMethod(Pic_res.this);
		}
		pic_exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// Intent intent = new Intent();
				// if (activity_type.equals("list_datail_sq")) {
				// intent.setClass(Pic_res.this, List_Detail.class);
				// } else if (activity_type.equals("list_datail_qs")) {
				// intent.setClass(Pic_res.this, List_Detail_Qs.class);
				// } else {
				// Toast.makeText(Pic_res.this, "error！", Toast.LENGTH_LONG)
				// .show();
				// }
				// lyc:针对oom 先判断是否已经回收:
				// for (int i = 0; i < pic_url.size(); i++) {
				// if (bitmaps[i] != null && !bitmaps[i].isRecycled()) {
				// // 回收并且置为null
				// bitmaps[i].recycle();
				// bitmaps[i] = null;
				// }
				// System.gc();
				// }
				// startActivity(intent);
				Pic_res.this.finish();
			}
		});

	}

	/* 图片缩小的method */
	private Bitmap small(Bitmap bitmap) {
		int bmpWidth = bitmap.getWidth();

		int bmpHeight = bitmap.getHeight();

		/* 设置图片缩小的比例 */
		double scale = 0.8;

		/* 计算出这次要缩小的比例 */
		scaleWidth = (float) (scaleWidth * scale);

		scaleHeight = (float) (scaleHeight * scale);
		/* 产生reSize后的Bitmap对象 */
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth,
				bmpHeight, matrix, true);
		return resizeBmp;

	}

	/* 图片放大的method */
	private Bitmap big(Bitmap bitmap) {
		int bmpWidth = bitmap.getWidth();

		int bmpHeight = bitmap.getHeight();

		/* 设置图片放大的比例 */
		double scale = 1;

		/* 计算这次要放大的比例 */
		scaleWidth = (float) (scaleWidth * 1);
		// scaleWidth = (float) (scaleWidth * scale);
		scaleHeight = (float) (scaleHeight * scale);

		/* 产生reSize后的Bitmap对象 */
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth,

				bmpHeight, matrix, true);
		return resizeBmp;
	}

	private class download_image_task extends AsyncTask<Void, Void, Bitmap[]> {
		private ProgressDialog progress;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			progress = new ProgressDialog(Pic_res.this);
			progress.setTitle("数据加载中");
			progress.setCancelable(false);
			progress.setMessage("请稍后...");
			progress.show();
			bitmaps = new Bitmap[pic_url.size()];


		}

		@Override
		protected Bitmap[] doInBackground(Void... arg0) {

			try {
				for (int i = 0; i < pic_url.size(); i++) {
					URL url = new URL(pic_url.get(i));
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setDoInput(true);
					conn.connect();
					InputStream inputStream = conn.getInputStream();
					down_bitmap = BitmapFactory.decodeStream(inputStream);
					bitmaps[i] = down_bitmap;
					inputStream.close();
				}
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
				Message msg = new Message();
				msg.what = IOEX;
				handler1.sendMessage(msg);

			} catch (NullPointerException e) {
				Message msg = new Message();
				msg.what = NULLEX;
				handler1.sendMessage(msg);
				e.printStackTrace();
			}

			return bitmaps;
		}

		protected void onPostExecute(final Bitmap[] bitmap) {

			if (progress != null) {
				progress.dismiss();
			}
			for (int j = 0; j < bitmap.length; j++) {
				if (bitmap[j] != null) {
					images = new ImageView[pic_url.size()];
					LinearLayout layout;
					ImageView imageView = null;
					layout = new LinearLayout(Pic_res.this);
					imageView = new ImageView(Pic_res.this);
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT);
					params.setMargins(1, 5, 1, 5);
					layout.setLayoutParams(params);
					layout.setBackgroundResource(R.drawable.more_item_unpress);
					layout.setGravity(Gravity.CENTER_HORIZONTAL);
					layout.setPadding(1, 5, 1, 5);
					layout.setShowDividers(11);
					imageView.setLayoutParams(new LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
					images[j] = imageView;
					layout.addView(imageView);
					group.addView(layout);
					images[j].setImageBitmap((bitmap[j]));

				} else {

				}
			}

		}
	}

}
