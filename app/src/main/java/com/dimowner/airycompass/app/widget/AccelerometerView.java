package com.dimowner.airycompass.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import com.dimowner.airycompass.R;
import com.dimowner.airycompass.util.AndroidUtils;

import timber.log.Timber;

public class AccelerometerView extends View {

	private Paint pathPaint;
	private Paint ballPaint;
	private Path path;

	private float roll = 0f;
	private float pitch = 0f;

	private float xPos;
	private float yPos;

	private Point CENTER;
	private int MAX_ACCELERATION;

	private static int MAX_MOVE = (int) AndroidUtils.dpToPx(50); //dip
	//Converted value from pixels to coefficient used in function which describes move.
	private float k = (float) (MAX_MOVE / (Math.PI/2));


	public AccelerometerView(Context context) {
		super(context);
		init(context);
	}

	public AccelerometerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public AccelerometerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		int color = context.getResources().getColor(R.color.md_white_1000);

		pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		pathPaint.setColor(color);
		pathPaint.setStrokeWidth(1);
		pathPaint.setStyle(Paint.Style.STROKE);

		ballPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		ballPaint.setColor(color);
		ballPaint.setStyle(Paint.Style.FILL);

		CENTER = new Point(0, 0);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		Timber.v("onMeasure");

		int w = MeasureSpec.getSize(widthMeasureSpec);
		int h = MeasureSpec.getSize(heightMeasureSpec);

		if (w > h) {
			w = h;
		} else {
			h = w;
		}

		setMeasuredDimension(
				resolveSize(w, widthMeasureSpec),
				resolveSize(h, heightMeasureSpec));
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		int width = getWidth();
		MAX_MOVE = width/2;
		k = (float) (MAX_MOVE / (Math.PI/2));
		MAX_ACCELERATION = width/10;
		CENTER.set(width/2, getHeight()/2);

		//Layout grid
		if (path == null) {
			float radius = width/2f - width*0.03f;
			path = new Path();
			path.moveTo(CENTER.x - radius, CENTER.y);
			path.lineTo(CENTER.x + radius, CENTER.y);
			path.moveTo(CENTER.x, CENTER.y - radius);
			path.lineTo(CENTER.x, CENTER.y + radius);
			path.addCircle(CENTER.x, CENTER.y, radius, Path.Direction.CCW);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Timber.v("onDraw");
		//Draw grid
		canvas.drawPath(path, pathPaint);
		//Draw ball
		canvas.drawCircle(CENTER.x - xPos, CENTER.y + yPos, getWidth()/10f, ballPaint);
	}

	public void updateOrientation(float pitch, float roll) {
		if ((int)this.pitch != (int)pitch || (int)this.roll != (int)roll) {
			this.pitch = pitch;
			this.roll = roll;

			xPos = getWidth() * 0.37f * (float) Math.cos(Math.toRadians(90 - roll));
			yPos = getWidth() * 0.37f * (float) Math.cos(Math.toRadians(90 - pitch));

			invalidate();
		}
	}

	public void updateLinearAcceleration(float x, float y) {
		if ((int)pitch != (int)x*1000 || (int)roll != (int)y*1000) {
			this.pitch = x*1000;
			this.roll = y*1000;

			xPos = (float) (k * Math.atan(x*MAX_ACCELERATION /k));
			yPos = (float) (k * Math.atan(y*MAX_ACCELERATION /k));

//			Timber.v("updateLinearAcceleration pitch = " + x + " roll = " + y + " xPos = " + xPos + " yPos = " + yPos);
			invalidate();
		}
	}
}
