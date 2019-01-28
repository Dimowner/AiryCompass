/*
 * Copyright 2019 Dmitriy Ponomarenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain prevDegree copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dimowner.airycompass.app.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.dimowner.airycompass.R;
import com.dimowner.airycompass.util.AndroidUtils;

import timber.log.Timber;

public class CompassBackgroundView extends View {

//	public static final String DEGREE_SIGN = "°";

	public static final float OUTER_RADIUS = 0.43f;
	public static final float MIDDLE_RADIUS = 0.325f;
	public static final float INNER_RADIUS = 0.24f;
//	public static final float SMALL_MARK_INNER_RADIUS = 0.35f;
//	public static final float SMALL_MARK_OUTER_RADIUS = 0.38f;
//	public static final float BIG_MARK_INNER_RADIUS = 0.34f;
//	public static final float BIG_MARK_OUTER_RADIUS = 0.38f;
//	public static final float NORTH_MARK_INNER_RADIUS = 0.30f;
//	public static final float NORTH_MARK_OUTER_RADIUS = 0.4f;
//	public static final float DEGREE_RADIUS = 0.30f;
//	public static final float DIRECTION_RADIUS = 0.215f;
//	public static final float MAGNETIC_VIEW_RADIUS = 0.407f;
//	public static final float MAGNETIC_NAME_RADIUS = 0.414f;
//	public static final float MAGNETIC_VAL_RADIUS = 0.4f;
//
//	private String n;
//	private String e;
//	private String s;
//	private String w;
//	private String ne;
//	private String se;
//	private String sw;
//	private String nw;
//	private String mT;
//	private String magField;

	private Paint outerCirclePaint;
	private Paint middleCirclePaint;
	private Paint innerCirclePaint;
//	private Paint magneticBackgroundPaint;
//	private Paint magneticTextPaint;
//	private Paint magneticFieldPaint;
//	private Paint smallMarkPaint;
//	private Paint bigMarkPaint;
//	private Paint northMarkPaint;
//	private Paint northMarkTextPaint;
	private Paint northMarkStaticPaint;
//	private Paint degreeTextPaint;
//	private Paint directionTextMainPaint;
//	private Paint directionTextSlavePaint;
//	private Paint currentDirectionTextPaint;

	private Point CENTER;
	private float WIDTH;

//	private Path smallMarkPath = null;
//	private Path bigMarkPath = null;
//	private Path northMarkPath = null;
//	private Path magneticPath = null;
//	private Path magneticBackgroundPath = null;
	private Path staticNorthMarkPath = null;
//	private Path northMarkPath2 = null;

//	private float azimuth;
//	private float magneticField;

	private boolean isSimple = false;


	public CompassBackgroundView(Context context) {
		super(context);
		init(context, null);
	}

	public CompassBackgroundView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}


	public CompassBackgroundView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
//		Typeface typeface = Typeface.create("sans-serif-light", Typeface.NORMAL);
//		Typeface typeface2 = Typeface.create("sans-serif-medium", Typeface.NORMAL);

		Resources res = context.getResources();
//		n = res.getString(R.string.n);
//		e = res.getString(R.string.e);
//		s = res.getString(R.string.s);
//		w = res.getString(R.string.w);
//		ne = res.getString(R.string.ne);
//		se = res.getString(R.string.se);
//		sw = res.getString(R.string.sw);
//		nw = res.getString(R.string.nw);
//		mT = res.getString(R.string.mt_val);
//		magField = res.getString(R.string.mag_field);

		int outerCircleColor= res.getColor(R.color.outer_circle_color);
		int middleCircleColor = res.getColor(R.color.middle_circle_color);
		int innerCircleColor = res.getColor(R.color.inner_circle_color);
		int northMarkColor = res.getColor(R.color.north_mark_color);

		if (attrs != null) {
			TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CompassBackgroundView);

			if (ta != null) {
				//Read View custom attributes
				outerCircleColor  = ta.getColor(R.styleable.CompassBackgroundView_outerCircle, res.getColor(R.color.outer_circle_color));
				middleCircleColor = ta.getColor(R.styleable.CompassBackgroundView_middleCircle, res.getColor(R.color.middle_circle_color));
				innerCircleColor = ta.getColor(R.styleable.CompassBackgroundView_innerCircle, res.getColor(R.color.inner_circle_color));
				northMarkColor = ta.getColor(R.styleable.CompassBackgroundView_northMarkArrow, res.getColor(R.color.north_mark_color));
				ta.recycle();
			}
		} else {
			//If failed to read View attributes, then read app theme attributes for for view colors.
			TypedValue typedValue = new TypedValue();
			Resources.Theme theme = context.getTheme();
			theme.resolveAttribute(R.attr.outerCircleColor, typedValue, true);
			outerCircleColor = typedValue.data;
			theme.resolveAttribute(R.attr.middleCircleColor, typedValue, true);
			middleCircleColor = typedValue.data;
			theme.resolveAttribute(R.attr.innerCircleColor, typedValue, true);
			innerCircleColor = typedValue.data;
			theme.resolveAttribute(R.attr.northMarkColor, typedValue, true);
			northMarkColor = typedValue.data;
		}

//		outerCircleColor = res.getColor(r);
//		theme.resolveAttribute(R.attr.expandableListItemColor, typedValue, true);
//		itemColor = typedValue.data;
//		theme.resolveAttribute(R.attr.selectedItemBackground, typedValue, true);
//		selectedItemColor = typedValue.data;
//		theme.resolveAttribute(R.attr.selectedItemTextColor, typedValue, true);
//		selectedItemTextColor = typedValue.data;
//		theme.resolveAttribute(R.attr.primaryTextColor, typedValue, true);
//		textColor = typedValue.data;

//		TypedArray a = context.getTheme().obtainStyledAttributes(
//		new int[] {R.attr.expandableListViewBackground, R.attr.expandableListItemColor,
//				R.attr.selectedItemBackground, R.attr.selectedItemTextColor,
//				R.attr.primaryTextColor});
//		backgroundColor = a.getColor(0, 0);
//		itemColor = a.getColor(1, 0);
//		selectedItemColor = a.getColor(2, 0);
//		selectedItemTextColor = a.getColor(3, 0);
//		textColor = a.getColor(4, 0);

//		magneticPath = new Path();
		CENTER = new Point(0, 0);

		//Background circles
		outerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		outerCirclePaint.setColor(outerCircleColor);
		outerCirclePaint.setStyle(Paint.Style.STROKE);
		outerCirclePaint.setStrokeWidth(AndroidUtils.dpToPx(1));

		middleCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		middleCirclePaint.setColor(middleCircleColor);
		middleCirclePaint.setStyle(Paint.Style.STROKE);
		middleCirclePaint.setStrokeWidth(AndroidUtils.dpToPx(42));

		innerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		innerCirclePaint.setColor(innerCircleColor);
		innerCirclePaint.setStyle(Paint.Style.STROKE);
		innerCirclePaint.setStrokeWidth(AndroidUtils.dpToPx(34));
//
//		//Magnetic view
//		magneticBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		magneticBackgroundPaint.setStyle(Paint.Style.STROKE);
//		magneticBackgroundPaint.setStrokeCap(Paint.Cap.ROUND);
//		magneticBackgroundPaint.setStrokeWidth(AndroidUtils.dpToPx(8));
//		magneticBackgroundPaint.setColor(magneticBackgroundColor);
//
//		magneticTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		magneticTextPaint.setColor(magneticTextColor);
//		magneticTextPaint.setTextSize(AndroidUtils.dpToPx(10));
//		magneticTextPaint.setTypeface(typeface);
//		magneticTextPaint.setTextAlign(Paint.Align.CENTER);
//
//		magneticFieldPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		magneticFieldPaint.setColor(magneticGradientColor);
//		magneticFieldPaint.setStrokeWidth(AndroidUtils.dpToPx(8));
//		magneticFieldPaint.setStyle(Paint.Style.STROKE);
//		magneticFieldPaint.setStrokeCap(Paint.Cap.ROUND);
//
//		//Clock marks
//		smallMarkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		smallMarkPaint.setStrokeCap(Paint.Cap.ROUND);
//		smallMarkPaint.setColor(smallMarkColor);
//		smallMarkPaint.setStyle(Paint.Style.STROKE);
//		smallMarkPaint.setStrokeWidth(AndroidUtils.dpToPx(1));
//
//		bigMarkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		bigMarkPaint.setStrokeCap(Paint.Cap.ROUND);
//		bigMarkPaint.setColor(bigMarkColor);
//		bigMarkPaint.setStyle(Paint.Style.STROKE);
//		bigMarkPaint.setStrokeWidth(AndroidUtils.dpToPx(2f));

		//NorthMark
		northMarkStaticPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		northMarkStaticPaint.setStyle(Paint.Style.FILL);
		northMarkStaticPaint.setColor(northMarkColor);
//
//		northMarkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		northMarkPaint.setStrokeCap(Paint.Cap.ROUND);
//		northMarkPaint.setColor(northMarkColor);
//		northMarkPaint.setStyle(Paint.Style.STROKE);
//		northMarkPaint.setStrokeWidth(AndroidUtils.dpToPx(4f));
//
//		northMarkTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		northMarkTextPaint.setColor(northMarkColor);
//		northMarkTextPaint.setTypeface(typeface2);
//		northMarkTextPaint.setTextSize(AndroidUtils.dpToPx(28));
//		northMarkTextPaint.setTextAlign(Paint.Align.CENTER);
//
//		//Text degrees and directions
//		degreeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		degreeTextPaint.setTextSize(AndroidUtils.dpToPx(12));
//		degreeTextPaint.setTypeface(typeface);
//		degreeTextPaint.setColor(primaryTextColor);
//		degreeTextPaint.setTextAlign(Paint.Align.CENTER);
//
//		directionTextMainPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		directionTextMainPaint.setColor(primaryTextColor);
//		directionTextMainPaint.setTextSize(AndroidUtils.dpToPx(26));
//		directionTextMainPaint.setTypeface(typeface2);
//		directionTextMainPaint.setTextAlign(Paint.Align.CENTER);
//
//		directionTextSlavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		directionTextSlavePaint.setColor(secondaryTextColor);
//		directionTextSlavePaint.setTextSize(AndroidUtils.dpToPx(14));
//		directionTextSlavePaint.setTextAlign(Paint.Align.CENTER);
//
//		//Current direction
//		currentDirectionTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		currentDirectionTextPaint.setColor(primaryTextColor);
//		currentDirectionTextPaint.setTextSize(AndroidUtils.dpToPx(30));
//		currentDirectionTextPaint.setTextAlign(Paint.Align.CENTER);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

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
		initConstants();

//		layoutSmallClockMarks();
//		layoutBigClockMarks();
//
//		layoutNorthMark();
		layoutStaticNorthMark();

//		layoutMagnetic();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Timber.v("onDraw");

		if (!isSimple) {
			//Draw outer circle
			canvas.drawCircle(CENTER.x, CENTER.y, WIDTH * OUTER_RADIUS, outerCirclePaint);
			//Draw middle circle
			canvas.drawCircle(CENTER.x, CENTER.y, WIDTH * MIDDLE_RADIUS, middleCirclePaint);
			//Draw inner circle
			canvas.drawCircle(CENTER.x, CENTER.y, WIDTH * INNER_RADIUS, innerCirclePaint);
		}

//		drawMagnetic(canvas);
//
//		drawAzimuthValue(canvas);
		//Draw static north mark (triangle)
		canvas.drawPath(staticNorthMarkPath, northMarkStaticPaint);

//		canvas.save();
//		canvas.rotate(-azimuth, CENTER.x, CENTER.y);
//
//		//Draw small clock marks
//		canvas.drawPath(smallMarkPath, smallMarkPaint);
//		//Draw big clock marks
//		canvas.drawPath(bigMarkPath, bigMarkPaint);
//		//Draw north mark
//		canvas.drawPath(northMarkPath, northMarkPaint);
//		canvas.drawPath(northMarkPath2, northMarkStaticPaint);
//
//		//Draw direction degrees
//		float radius = WIDTH*DEGREE_RADIUS;
//		drawText(canvas, 300.0f, "30", radius, degreeTextPaint);
//		drawText(canvas, 330.0f, "60", radius, degreeTextPaint);
//		drawText(canvas, 360.0f, "90", radius, degreeTextPaint);
//		drawText(canvas, 30.0f, "120", radius, degreeTextPaint);
//		drawText(canvas, 60.0f, "150", radius, degreeTextPaint);
//		drawText(canvas, 90.0f, "180", radius, degreeTextPaint);
//		drawText(canvas, 120.0f, "210", radius, degreeTextPaint);
//		drawText(canvas, 150.0f, "240", radius, degreeTextPaint);
//		drawText(canvas, 180.0f, "270", radius, degreeTextPaint);
//		drawText(canvas, 210.0f, "300", radius, degreeTextPaint);
//		drawText(canvas, 240.0f, "330", radius, degreeTextPaint);
//
//		//Draw directions texts
//		float radiusPx = WIDTH*DIRECTION_RADIUS;
//		drawText(canvas, 270, n, radiusPx, northMarkTextPaint);
//		drawText(canvas, 0, e, radiusPx, directionTextMainPaint);
//		drawText(canvas, 90, s, radiusPx, directionTextMainPaint);
//		drawText(canvas, 180, w, radiusPx, directionTextMainPaint);
//		drawText(canvas, 315, ne, radiusPx, directionTextSlavePaint);
//		drawText(canvas, 45, se, radiusPx, directionTextSlavePaint);
//		drawText(canvas, 135, sw, radiusPx, directionTextSlavePaint);
//		drawText(canvas, 225, nw, radiusPx, directionTextSlavePaint);
//
//		canvas.restore();
	}

	private void initConstants() {
		WIDTH = getWidth();
		CENTER.set((int)WIDTH/2, getHeight()/2);
	}

//	private void layoutSmallClockMarks() {
//		if (smallMarkPath == null) {
//			float inner = WIDTH*SMALL_MARK_INNER_RADIUS;
//			float outer = WIDTH*SMALL_MARK_OUTER_RADIUS;
//			smallMarkPath = new Path();
//			float degreeStep = 2.5f;
//			for (float step = 0.0f; step < 2 * Math.PI; step += Math.toRadians(degreeStep)) {
//				float cos = (float) Math.cos(step);
//				float sin = (float) Math.sin(step);
//
//				float x = inner * cos;
//				float y = inner * sin;
//				smallMarkPath.moveTo(x + ((float) CENTER.x), y + ((float) CENTER.y));
//
//				x = outer * cos;
//				y = outer * sin;
//				smallMarkPath.lineTo(x + ((float) CENTER.x), y + ((float) CENTER.y));
//			}
//		}
//	}
//
//	private void layoutBigClockMarks() {
//		if (bigMarkPath == null) {
//			float inner = WIDTH*BIG_MARK_INNER_RADIUS;
//			float outer = WIDTH*BIG_MARK_OUTER_RADIUS;
//			bigMarkPath = new Path();
//			float degreeStep = 30.0f;
//			for (float step = 0.0f; step < 2 * Math.PI; step += Math.toRadians(degreeStep)) {
//				float cos = (float) Math.cos(step);
//				float sin = (float) Math.sin(step);
//
//				float x = inner * cos;
//				float y = inner * sin;
//				bigMarkPath.moveTo(x + ((float) CENTER.x), y + ((float) CENTER.y));
//
//				cos *= outer;
//				sin *= outer;
//				bigMarkPath.lineTo(cos + ((float) CENTER.x), sin + ((float) CENTER.y));
//			}
//		}
//	}
//
//	private void layoutNorthMark() {
//		if (northMarkPath == null) {
//			northMarkPath = new Path();
//			float radian = (float) Math.toRadians(270.0d);
//
//			float cos = (float) Math.cos((double) radian);
//			float sin = (float) Math.sin((double) radian);
//
//			float inner = WIDTH*NORTH_MARK_INNER_RADIUS;
//			float outer = WIDTH*NORTH_MARK_OUTER_RADIUS;
//
//			float x = inner * cos;
//			float y = inner * sin;
//
//			northMarkPath.moveTo(((float) CENTER.x) + x, ((float) CENTER.y) + y);
//
//			x = outer * cos;
//			y = outer * sin;
//			northMarkPath.lineTo(x + ((float) CENTER.x), y + ((float) CENTER.y));
//		}
//
//		if (northMarkPath2 == null) {
//			float length = AndroidUtils.dpToPx(12);
//			float x = CENTER.x;
//			float y = (CENTER.y - WIDTH*BIG_MARK_OUTER_RADIUS);
//			northMarkPath2 = new Path();
//			northMarkPath2.moveTo(x - length/2.0f, y);
//			northMarkPath2.lineTo(x + length/2.0f, y);
//			northMarkPath2.lineTo(x, CENTER.y - WIDTH*0.41f);
//			northMarkPath2.lineTo(x - length/2.0f, y);
//		}
//	}

	private void layoutStaticNorthMark() {
		if (staticNorthMarkPath == null) {
			float x = CENTER.x;
			float length = AndroidUtils.dpToPx(12);
			float y = (CENTER.y - WIDTH*OUTER_RADIUS + length - AndroidUtils.dpToPx(2));
			staticNorthMarkPath = new Path();
			staticNorthMarkPath.moveTo(x - length/2.0f, y - length);
			staticNorthMarkPath.lineTo(x + length/2.0f, y - length);
			staticNorthMarkPath.lineTo(x, y);
			staticNorthMarkPath.lineTo(x - length/2.0f, y - length);
		}
	}

	public void setSimpleMode(boolean simple) {
		isSimple = simple;
		invalidate();
	}

//
//	private void layoutMagnetic() {
//		if (magneticBackgroundPath == null) {
//			float r = WIDTH* MAGNETIC_VIEW_RADIUS;
//			RectF bound = new RectF(CENTER.x - r, CENTER.y - r, CENTER.x + r, CENTER.y + r);
//			magneticBackgroundPath = new Path();
//			magneticBackgroundPath.addArc(bound, 315, 85);
//		}
//	}
//
//	private void drawMagnetic(Canvas canvas) {
//		int angle = 85;
//		float r = WIDTH* MAGNETIC_VIEW_RADIUS;
//		RectF bound = new RectF(CENTER.x - r, CENTER.y - r, CENTER.x + r, CENTER.y + r);
//
//		//Draw magnetic background
//		canvas.drawPath(magneticBackgroundPath, magneticBackgroundPaint);
//
//		int max = 160;
//		float percent = Math.min(1, magneticField / max);
//		percent = percent * angle;
//
//		//Draw magnetic field value
//		magneticPath.reset();
//		magneticPath.addArc(bound, 315 + angle - percent, percent);
//		canvas.drawPath(magneticPath, magneticFieldPaint);
//
//		drawText(canvas, 307, (int)magneticField+mT, WIDTH*MAGNETIC_VAL_RADIUS, magneticTextPaint);
//		drawTextInverted(canvas, 53, magField, WIDTH*MAGNETIC_NAME_RADIUS, magneticTextPaint);
//	}
//
//	private void drawAzimuthValue(Canvas canvas) {
//		String str = ((int)azimuth) + DEGREE_SIGN + " " + getDirectionText(azimuth);
//		Rect rect = new Rect();
//		currentDirectionTextPaint.getTextBounds(str, 0, str.length(), rect);
//		canvas.drawText(str, CENTER.x, CENTER.y + rect.height()/2.0f, currentDirectionTextPaint);
//	}
//
//	private void drawText(Canvas canvas, float degree, String text, float radius, Paint paint) {
//		canvas.save();
//		canvas.translate(
//				((float) Math.cos(Math.toRadians(degree)) * radius) + CENTER.x,
//				((float) Math.sin(Math.toRadians(degree)) * radius) + CENTER.y
//		);
//		canvas.rotate(90.0f + degree);
//		canvas.drawText(text, 0, 0, paint);
//		canvas.restore();
//	}
//
//	private void drawTextInverted(Canvas canvas, float degree, String text, float radius, Paint paint) {
//		canvas.save();
//		canvas.translate(
//				((float) Math.cos(Math.toRadians(degree)) * radius) + CENTER.x,
//				((float) Math.sin(Math.toRadians(degree)) * radius) + CENTER.y
//		);
//		canvas.rotate(270f + degree);
//		canvas.drawText(text, 0, 0, paint);
//		canvas.restore();
//	}
//
//	public void updateMagneticField(float field) {
////		Timber.v("updateMagneticField omMF = " + magneticField + "newMF = " + field);
//		if ((int)magneticField != (int)field) {
//			magneticField = field;
//			invalidate();
//		}
//	}
//
//	public void updateAzimuth(float azimuth) {
////		Timber.v("updateAzimuth oldAz = " + this.azimuth + " newAz = " + azimuth);
//		if ((int)this.azimuth != (int)azimuth) {
//			this.azimuth = azimuth;
//			invalidate();
//		}
//	}
//
//	private String getDirectionText(float degree) {
//		final float step = 22.5f;
//		if (degree >= 0 && degree < step || degree > 360 - step) {
//			return n;
//		}
//		if (degree >= step && degree < step * 3) {
//			return ne;
//		}
//		if (degree >= step * 3 && degree < step * 5) {
//			return e;
//		}
//		if (degree >= step * 5 && degree < step * 7) {
//			return se;
//		}
//		if (degree >= step * 7 && degree < step * 9) {
//			return s;
//		}
//		if (degree >= step * 9 && degree < step * 11) {
//			return sw;
//		}
//		if (degree >= step * 11 && degree < step * 13) {
//			return w;
//		}
//		if (degree >= step * 13 && degree < step * 15) {
//			return nw;
//		}
//		return "";
//	}
}
