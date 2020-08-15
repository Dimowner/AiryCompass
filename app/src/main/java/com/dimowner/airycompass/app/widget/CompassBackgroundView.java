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

public class CompassBackgroundView extends View {

	public static final float OUTER_RADIUS = 0.43f;
	public static final float MIDDLE_RADIUS = 0.33f;
	public static final float INNER_RADIUS = 0.24f;

	private Paint outerCirclePaint;
	private Paint middleCirclePaint;
	private Paint innerCirclePaint;

	private Paint northMarkStaticPaint;

	private Point CENTER;
	private float WIDTH;

	private Path staticNorthMarkPath = null;

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
		Resources res = context.getResources();

		int outerCircleColor= res.getColor(R.color.md_indigo_300);
		int middleCircleColor = res.getColor(R.color.md_indigo_400x);
		int innerCircleColor = res.getColor(R.color.md_indigo_500x);
		int northMarkColor = res.getColor(R.color.md_red_400);

		if (attrs != null) {
			TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CompassBackgroundView);

			if (ta != null) {
				//Read View custom attributes
				outerCircleColor  = ta.getColor(R.styleable.CompassBackgroundView_outerCircle, res.getColor(R.color.md_indigo_300));
				middleCircleColor = ta.getColor(R.styleable.CompassBackgroundView_middleCircle, res.getColor(R.color.md_indigo_400x));
				innerCircleColor = ta.getColor(R.styleable.CompassBackgroundView_innerCircle, res.getColor(R.color.md_indigo_500x));
				northMarkColor = ta.getColor(R.styleable.CompassBackgroundView_northMarkArrow, res.getColor(R.color.md_red_400));
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

		CENTER = new Point(0, 0);

		//Background circles
		outerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		outerCirclePaint.setColor(outerCircleColor);
		outerCirclePaint.setStyle(Paint.Style.STROKE);
		outerCirclePaint.setStrokeWidth(AndroidUtils.dpToPx(1));

		middleCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		middleCirclePaint.setColor(middleCircleColor);
		middleCirclePaint.setStyle(Paint.Style.STROKE);
		middleCirclePaint.setStrokeWidth(AndroidUtils.dpToPx(41));

		innerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		innerCirclePaint.setColor(innerCircleColor);
		innerCirclePaint.setStyle(Paint.Style.STROKE);
		innerCirclePaint.setStrokeWidth(AndroidUtils.dpToPx(34));

		//NorthMark
		northMarkStaticPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		northMarkStaticPaint.setStyle(Paint.Style.FILL);
		northMarkStaticPaint.setColor(northMarkColor);
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

		layoutStaticNorthMark();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (!isSimple) {
			//Draw outer circle
			canvas.drawCircle(CENTER.x, CENTER.y, WIDTH * OUTER_RADIUS, outerCirclePaint);
			//Draw middle circle
			canvas.drawCircle(CENTER.x, CENTER.y, WIDTH * MIDDLE_RADIUS, middleCirclePaint);
			//Draw inner circle
			canvas.drawCircle(CENTER.x, CENTER.y, WIDTH * INNER_RADIUS, innerCirclePaint);
		}

		//Draw static north mark (triangle)
		canvas.drawPath(staticNorthMarkPath, northMarkStaticPaint);
	}

	private void initConstants() {
		WIDTH = getWidth();
		CENTER.set((int)WIDTH/2, getHeight()/2);
	}

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
}
