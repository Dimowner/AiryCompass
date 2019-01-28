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

public class AccuracyDrawerSimple implements ViewDrawer<Integer> {

	public static final float THICKNESS = AndroidUtils.dpToPx(4);
	public static final float PADDING = AndroidUtils.dpToPx(16);
	public static final float INDICATOR_WIDTH = AndroidUtils.dpToPx(120);
	public static final float TEXT_HEIGHT = AndroidUtils.dpToPx(12);

	private String accName;
	private String accValue;

	private String accLow;
	private String accMedium;
	private String accHigh;
	private String accUnreliable;

	private Paint accuracyBackgroundPaint;
	private Paint accuracyTextPaint;
	private Paint accuracyFieldPaint;

	private Point CENTER;
	private float HEIGHT;

	private Path accuracyPath;
	private Path accuracyBackgroundPath = null;

	private int accuracy = 0;

	public AccuracyDrawerSimple(Context context) {
		Typeface typeface = Typeface.create("sans-serif-light", Typeface.NORMAL);

		Resources res = context.getResources();
		accName = res.getString(R.string.accuracy);
		accValue = res.getString(R.string.accuracy_unreliable);

		accLow = context.getResources().getString(R.string.accuracy_low);
		accMedium = context.getResources().getString(R.string.accuracy_medium);
		accHigh = context.getResources().getString(R.string.accuracy_high);
		accUnreliable = context.getResources().getString(R.string.accuracy_unreliable);

		int accuracyBackgroundColor;
		int accuracyTextColor;
		int accuracyColor;

//		if (attrs != null) {
//			TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AccuracyView);
//			if (ta != null) {
//				//Read View custom attributes
//				accuracyBackgroundColor  = ta.getColor(R.styleable.AccuracyView_accuracyBackground, res.getColor(R.color.magnetic_background));
//				accuracyTextColor = ta.getColor(R.styleable.AccuracyView_accuracyText, res.getColor(R.color.magnetic_text_color));
//				accuracyColor = ta.getColor(R.styleable.AccuracyView_accuracyIndicator, res.getColor(R.color.accuracy_color));
//				ta.recycle();
//			} else {
				//If failed to read View attributes, then read app theme attributes for for view colors.
				TypedValue typedValue = new TypedValue();
				Resources.Theme theme = context.getTheme();
				if (theme.resolveAttribute(R.attr.indicatorBackgroundColor, typedValue, true)) {
					accuracyBackgroundColor = typedValue.data;
				} else {
					accuracyBackgroundColor = res.getColor(R.color.magnetic_background);
				}
				if (theme.resolveAttribute(R.attr.indicatorTextColor, typedValue, true)) {
					accuracyTextColor = typedValue.data;
				} else {
					accuracyTextColor = res.getColor(R.color.magnetic_text_color);
				}
				if (theme.resolveAttribute(R.attr.indicatorFieldColor, typedValue, true)) {
					accuracyColor = typedValue.data;
				} else {
					accuracyColor = res.getColor(R.color.accuracy_color);
				}
//			}
//		}

		accuracyPath = new Path();
		CENTER = new Point(0, 0);

		//Accuracy view
		accuracyBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		accuracyBackgroundPaint.setStyle(Paint.Style.STROKE);
		accuracyBackgroundPaint.setStrokeCap(Paint.Cap.ROUND);
		accuracyBackgroundPaint.setStrokeWidth(THICKNESS);
		accuracyBackgroundPaint.setColor(accuracyBackgroundColor);

		accuracyTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		accuracyTextPaint.setColor(accuracyTextColor);
		accuracyTextPaint.setTextSize(TEXT_HEIGHT);
		accuracyTextPaint.setTypeface(typeface);
		accuracyTextPaint.setTextAlign(Paint.Align.CENTER);

		accuracyFieldPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		accuracyFieldPaint.setColor(accuracyColor);
		accuracyFieldPaint.setStrokeWidth(THICKNESS);
		accuracyFieldPaint.setStyle(Paint.Style.STROKE);
		accuracyFieldPaint.setStrokeCap(Paint.Cap.ROUND);
	}

	@Override
	public void layout(int width, int height) {
		initConstants(width, height);
		layoutAccuracy();
	}

	@Override
	public void draw(Canvas canvas) {
		drawAccuracy(canvas);
	}

	@Override
	public void update(Integer a) {
		if (accuracy != a) {
			accuracy = a;
			switch (accuracy) {
				case 1:
					accValue = accLow;
					break;
				case 2:
					accValue = accMedium;
					break;
				case 3:
					accValue = accHigh;
					break;
				case 0:
				default:
					accValue = accUnreliable;
			}
		}
	}

	private void initConstants(int width, int height) {
		HEIGHT = height;
		CENTER.set(width/2, height/2);
	}

	private void layoutAccuracy() {
		if (accuracyBackgroundPath == null) {
			accuracyBackgroundPath = new Path();
			accuracyBackgroundPath.moveTo(CENTER.x, HEIGHT-PADDING);
			accuracyBackgroundPath.lineTo(CENTER.x+INDICATOR_WIDTH/2, HEIGHT-PADDING);
		}
	}

	private void drawAccuracy(Canvas canvas) {
		//Draw accuracy background
		canvas.drawPath(accuracyBackgroundPath, accuracyBackgroundPaint);

		int max = 60;
		float percent = Math.min(1, (float) (accuracy*20) / max);

		//Draw accuracy field value
		float padd = AndroidUtils.dpToPx(8);
		percent = percent * INDICATOR_WIDTH;
		accuracyPath.reset();
		accuracyPath = new Path();
		accuracyPath.moveTo(CENTER.x+INDICATOR_WIDTH/2, HEIGHT-PADDING);
		accuracyPath.lineTo(CENTER.x+INDICATOR_WIDTH/2 - percent, HEIGHT-PADDING);

		canvas.drawPath(accuracyPath, accuracyFieldPaint);

		Rect rect = new Rect();
		accuracyTextPaint.getTextBounds(accValue, 0, accValue.length(), rect);
		canvas.drawText(accValue, CENTER.x-INDICATOR_WIDTH/2-rect.width()/2f-padd, HEIGHT-PADDING, accuracyTextPaint);
		accuracyTextPaint.getTextBounds(accName, 0, accName.length(), rect);
		canvas.drawText(accName, CENTER.x+INDICATOR_WIDTH/2+rect.width()/2f+padd, HEIGHT-PADDING, accuracyTextPaint);
	}
}
