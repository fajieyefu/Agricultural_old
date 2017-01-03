package com.yiyun.sijianguan.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MainView extends View {
	private Bitmap mBitmap;// 绘成的图片
	private Canvas mCanvas;// 画布
	private Path mPath;// 绘图路径
	private Paint mBitmapPaint;// 绘制图片的画笔
	private Paint mPathPaint;// 绘制路径的画笔
	private float mX, mY;// 坐标
	private static final float TOUCH_TOLERANCE = 4;// 公差4dp
	MaskFilter maskFilter = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6,
			3.5f);

	// 构造函数
	public MainView(Context context) {
		super(context);
		init();
	}

	// 构造函数
	public MainView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	// 构造函数
	public MainView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	// 新建绘图路径，画笔，和画布
	private void init() {
		mPath = new Path();// 绘图路径
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);// 绘制图片的画笔
		mPathPaint = new Paint();// 绘制路径的画笔
		mPathPaint.setAntiAlias(true);
		mPathPaint.setDither(true);
		mPathPaint.setColor(0xFF000000);// 颜色
		mPathPaint.setStyle(Paint.Style.STROKE);// 样式 线条
		mPathPaint.setStrokeJoin(Paint.Join.ROUND);
		mPathPaint.setStrokeCap(Paint.Cap.ROUND);
		mPathPaint.setStrokeWidth(10);// 笔画宽度
		mPathPaint.setMaskFilter(maskFilter);
		
		
	}

	// 测量手机屏幕宽度和高度
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		this.createBitmap();
	}

	// 生成签名图片
	protected void createBitmap() {
		if (mBitmap != null) {
			return;
		}
		mBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(),
				Bitmap.Config.ARGB_8888);
		mBitmap.eraseColor(Color.TRANSPARENT);
		mCanvas = new Canvas(mBitmap);
		mCanvas.drawColor(Color.WHITE);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	// 绘制视图
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.TRANSPARENT);
		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
		canvas.drawPath(mPath, mPathPaint);
	}

	// 开始绘制
	private void touch_start(float x, float y) {
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
	}

	// 绘制过程
	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);// 横坐标移动量
		float dy = Math.abs(y - mY);// 纵坐标移动量
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);// 移动画笔
			mX = x;
			mY = y;
		}
	}

	// 停止绘制
	private void touch_up() {
		mPath.lineTo(mX, mY);// 路径到达终点坐标
		mCanvas.drawPath(mPath, mPathPaint);// 通过路径，在画布上画图
		mPath.reset();// 重新设置绘图路径到原始状态
	}

	// 触屏事件
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();// 横坐标
		float y = event.getY();// 纵坐标

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:// 手指接触到屏幕
			touch_start(x, y);
			invalidate();// 绘制结束更新视图，触发ondraw事件
			break;
		case MotionEvent.ACTION_MOVE:// 手指移动
			touch_move(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:// 手指释放
			touch_up();
			invalidate();
			break;
		}
		return true;
	}

	// 清除之前画的图像
	public void clearSignature() {
		mBitmap.eraseColor(Color.TRANSPARENT);
		invalidate();
	}

	// 获取画好的图像
	public Bitmap getSignatureBitmap() {
		return mBitmap;
	}



	/**
	 * 将画布的内容保存到文件 * @param filename
	 * 
	 * @throws FileNotFoundException
	 */
	public void saveToFile(String filename) throws FileNotFoundException {
		File f = new File(filename);
		if (f.exists())
			throw new RuntimeException("文件：" + filename + " 已存在！");

		FileOutputStream fos = new FileOutputStream(new File(filename));
		// 将 bitmap 压缩成其他格式的图片数据
		mBitmap.compress(CompressFormat.PNG, 100, fos);
		try {
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
