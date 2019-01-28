package com.dimowner.airycompass.app.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.TypedValue;

import com.dimowner.airycompass.R;
import com.dimowner.airycompass.util.AndroidUtils;

public class MagneticFieldDrawer implements ViewDrawer<Float> {

	private static final float MAGNETIC_VIEW_RADIUS = 0.407f;
	private static final float MAGNETIC_NAME_RADIUS = 0.414f;
	private static final float MAGNETIC_VAL_RADIUS = 0.4f;

	private String mT;
	private String magField;

	private Paint magneticBackgroundPaint;
	private Paint magneticTextPaint;
	private Paint magneticFieldPaint;

	private Point CENTER;
	private float WIDTH;

	private Path magneticPath;
	private Path magneticBackgroundPath = null;

	private float magneticField;

	public MagneticFieldDrawer(Context context) {
		Typeface typeface = Typeface.create("sans-serif-light", Typeface.NORMAL);

		Resources res = context.getResources();
		mT = res.getString(R.string.mt_val);
		magField = res.getString(R.string.mag_field);

		int magneticBackgroundColor;
		int magneticTextColor;
		int magneticFieldColor;

		TypedValue typedValue = new TypedValue();
		Resources.Theme theme = context.getTheme();
		if (theme.resolveAttribute(R.attr.indicatorBackgroundColor, typedValue, true)) {
			magneticBackgroundColor = typedValue.data;
		} else {
			magneticBackgroundColor = res.getColor(R.color.magnetic_background);
		}
		if (theme.resolveAttribute(R.attr.indicatorTextColor, typedValue, true)) {
			magneticTextColor = typedValue.data;
		} else {
			magneticTextColor = res.getColor(R.color.magnetic_text_color);
		}
		if (theme.resolveAttribute(R.attr.indicatorFieldColor, typedValue, true)) {
			magneticFieldColor = typedValue.data;
		} else {
			magneticFieldColor = res.getColor(R.color.magnetic_field_color);
		}

		magneticPath = new Path();
		CENTER = new Point(0, 0);

		//Magnetic view
		magneticBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		magneticBackgroundPaint.setStyle(Paint.Style.STROKE);
		magneticBackgroundPaint.setStrokeCap(Paint.Cap.ROUND);
		magneticBackgroundPaint.setStrokeWidth(AndroidUtils.dpToPx(6));
		magneticBackgroundPaint.setColor(magneticBackgroundColor);

		magneticTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		magneticTextPaint.setColor(magneticTextColor);
		magneticTextPaint.setTextSize(AndroidUtils.dpToPx(10));
		magneticTextPaint.setTypeface(typeface);
		magneticTextPaint.setTextAlign(Paint.Align.CENTER);

		magneticFieldPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		magneticFieldPaint.setColor(magneticFieldColor);
		magneticFieldPaint.setStrokeWidth(AndroidUtils.dpToPx(6));
		magneticFieldPaint.setStyle(Paint.Style.STROKE);
		magneticFieldPaint.setStrokeCap(Paint.Cap.ROUND);
	}

	@Override
	public void layout(int width, int height) {
		initConstants(width, height);
		layoutMagnetic();
	}

	@Override
	public void draw(Canvas canvas) {
		drawMagnetic(canvas);
	}

	@Override
	public void update(Float value) {
		magneticField = value;
	}

	private void initConstants(int width, int height) {
		WIDTH = width;
		CENTER.set(width/2, height/2);
	}

	private void layoutMagnetic() {
		if (magneticBackgroundPath == null) {
			float r = WIDTH* MAGNETIC_VIEW_RADIUS;
			RectF bound = new RectF(CENTER.x - r, CENTER.y - r, CENTER.x + r, CENTER.y + r);
			magneticBackgroundPath = new Path();
			magneticBackgroundPath.addArc(bound, 315, 85);
		}
	}

	private void drawMagnetic(Canvas canvas) {
		int angle = 85;
		float r = WIDTH* MAGNETIC_VIEW_RADIUS;
		RectF bound = new RectF(CENTER.x - r, CENTER.y - r, CENTER.x + r, CENTER.y + r);

		//Draw magnetic background
		canvas.drawPath(magneticBackgroundPath, magneticBackgroundPaint);

		int max = 160;
		float percent = Math.min(1, magneticField / max);
		percent = percent * angle;

		//Draw magnetic field value
		magneticPath.reset();
		magneticPath.addArc(bound, 315 + angle - percent, percent);
		canvas.drawPath(magneticPath, magneticFieldPaint);

		drawText(canvas, 307, (int)magneticField+mT, WIDTH*MAGNETIC_VAL_RADIUS, magneticTextPaint);
		drawTextInverted(canvas, 53, magField, WIDTH*MAGNETIC_NAME_RADIUS, magneticTextPaint);
	}

	private void drawText(Canvas canvas, float degree, String text, float radius, Paint paint) {
		canvas.save();
		canvas.translate(
				((float) Math.cos(Math.toRadians(degree)) * radius) + CENTER.x,
				((float) Math.sin(Math.toRadians(degree)) * radius) + CENTER.y
		);
		canvas.rotate(90.0f + degree);
		canvas.drawText(text, 0, 0, paint);
		canvas.restore();
	}

	private void drawTextInverted(Canvas canvas, float degree, String text, float radius, Paint paint) {
		canvas.save();
		canvas.translate(
				((float) Math.cos(Math.toRadians(degree)) * radius) + CENTER.x,
				((float) Math.sin(Math.toRadians(degree)) * radius) + CENTER.y
		);
		canvas.rotate(270f + degree);
		canvas.drawText(text, 0, 0, paint);
		canvas.restore();
	}
}
