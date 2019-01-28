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
import android.graphics.Typeface;
import android.util.TypedValue;

import com.dimowner.airycompass.R;
import com.dimowner.airycompass.util.AndroidUtils;

public class AccuracyDrawerSimple2 implements ViewDrawer<Integer> {

	private static final float PADDING = AndroidUtils.dpToPx(32);
	private static final float TEXT_HEIGHT = AndroidUtils.dpToPx(14);

	private String accName;
	private String accValue;

	private String accLow;
	private String accMedium;
	private String accHigh;
	private String accUnreliable;

	private Paint accuracyTextPaint;

	private float HEIGHT;
	private int accuracy = 0;

	public AccuracyDrawerSimple2(Context context) {
		Typeface typeface = Typeface.create("sans-serif-light", Typeface.NORMAL);

		Resources res = context.getResources();
		accName = res.getString(R.string.accuracy2);
		accValue = res.getString(R.string.accuracy_unreliable);

		accLow = context.getResources().getString(R.string.accuracy_low);
		accMedium = context.getResources().getString(R.string.accuracy_medium);
		accHigh = context.getResources().getString(R.string.accuracy_high);
		accUnreliable = context.getResources().getString(R.string.accuracy_unreliable);

		int accuracyTextColor;
		TypedValue typedValue = new TypedValue();
		Resources.Theme theme = context.getTheme();
		if (theme.resolveAttribute(R.attr.indicatorTextColor, typedValue, true)) {
			accuracyTextColor = typedValue.data;
		} else {
			accuracyTextColor = res.getColor(R.color.magnetic_text_color);
		}

		accuracyTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		accuracyTextPaint.setColor(accuracyTextColor);
		accuracyTextPaint.setTextSize(TEXT_HEIGHT);
		accuracyTextPaint.setTypeface(typeface);
	}

	@Override
	public void layout(int width, int height) {
		HEIGHT = height;
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawText(accName + ": " + accValue, PADDING, HEIGHT-PADDING, accuracyTextPaint);
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
}
