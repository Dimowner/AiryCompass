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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.TypedValue;

import com.dimowner.airycompass.R;
import com.dimowner.airycompass.util.AndroidUtils;

public class MagneticFieldDrawerSimple implements ViewDrawer<Float> {

	private static final float THICKNESS = AndroidUtils.dpToPx(4);
	private static final float PADDING = AndroidUtils.dpToPx(4);
	private static final float TEXT_PADDING = AndroidUtils.dpToPx(8);
	private static final float INDICATOR_WIDTH = AndroidUtils.dpToPx(160);
	private static final float TEXT_HEIGHT = AndroidUtils.dpToPx(12);

	private String mT;
	private String magField;

	private Paint magneticBackgroundPaint;
	private Paint magneticTextPaint;
	private Paint magneticFieldPaint;

	private Point CENTER;
	private float HEIGHT;

	private Path magneticPath;
	private Path magneticBackgroundPath = null;

	private float magneticField;

	public MagneticFieldDrawerSimple(Context context) {
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
		magneticBackgroundPaint.setStrokeWidth(THICKNESS);
		magneticBackgroundPaint.setColor(magneticBackgroundColor);

		magneticTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		magneticTextPaint.setColor(magneticTextColor);
		magneticTextPaint.setTextSize(TEXT_HEIGHT);
		magneticTextPaint.setTypeface(typeface);
		magneticTextPaint.setTextAlign(Paint.Align.CENTER);

		magneticFieldPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		magneticFieldPaint.setColor(magneticFieldColor);
		magneticFieldPaint.setStrokeWidth(THICKNESS);
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
		HEIGHT = height;
		CENTER.set(width/2, height/2);
	}

	private void layoutMagnetic() {
		if (magneticBackgroundPath == null) {
			magneticBackgroundPath = new Path();
			magneticBackgroundPath.moveTo(CENTER.x-INDICATOR_WIDTH/2, HEIGHT-PADDING);
			magneticBackgroundPath.lineTo(CENTER.x+INDICATOR_WIDTH/2, HEIGHT-PADDING);
		}
	}

	private void drawMagnetic(Canvas canvas) {
		//Draw accuracy background
		canvas.drawPath(magneticBackgroundPath, magneticBackgroundPaint);

		int max = 160;
		float percent = Math.min(1, magneticField / max);

		percent = percent * INDICATOR_WIDTH;
		magneticPath.reset();
		magneticPath = new Path();
		magneticPath.moveTo(CENTER.x+INDICATOR_WIDTH/2, HEIGHT-PADDING);
		magneticPath.lineTo(CENTER.x+INDICATOR_WIDTH/2 - percent, HEIGHT-PADDING);

		canvas.drawPath(magneticPath, magneticFieldPaint);

		Rect rect = new Rect();
		String str = (int)magneticField+mT;
		magneticTextPaint.getTextBounds(str, 0, str.length(), rect);
		canvas.drawText(str, CENTER.x-INDICATOR_WIDTH/2-rect.width()/2f-TEXT_PADDING, HEIGHT-PADDING, magneticTextPaint);
		magneticTextPaint.getTextBounds(magField, 0, magField.length(), rect);
		canvas.drawText(magField, CENTER.x+INDICATOR_WIDTH/2+rect.width()/2f+TEXT_PADDING, HEIGHT-PADDING, magneticTextPaint);
	}
}
