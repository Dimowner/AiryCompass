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

public class MagneticFieldDrawerSimple2 implements ViewDrawer<Float> {

	private static final float PADDING = AndroidUtils.dpToPx(8);
	private static final float X_PADDING = AndroidUtils.dpToPx(32);
	private static final float TEXT_HEIGHT = AndroidUtils.dpToPx(14);

	private String mT;
	private String magField;

	private Paint magneticTextPaint;
	private float HEIGHT;

	private float magneticField;

	public MagneticFieldDrawerSimple2(Context context) {
		Typeface typeface = Typeface.create("sans-serif-light", Typeface.NORMAL);

		Resources res = context.getResources();
		mT = res.getString(R.string.mt_val);
		magField = res.getString(R.string.magnetic_field);

		int magneticTextColor;

		TypedValue typedValue = new TypedValue();
		Resources.Theme theme = context.getTheme();
		if (theme.resolveAttribute(R.attr.indicatorTextColor, typedValue, true)) {
			magneticTextColor = typedValue.data;
		} else {
			magneticTextColor = res.getColor(R.color.magnetic_text_color);
		}

		magneticTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		magneticTextPaint.setColor(magneticTextColor);
		magneticTextPaint.setTextSize(TEXT_HEIGHT);
		magneticTextPaint.setTypeface(typeface);
	}

	@Override
	public void layout(int width, int height) {
		HEIGHT = height;
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawText(magField + ": " + (int)magneticField+mT, X_PADDING, HEIGHT-PADDING, magneticTextPaint);
	}

	@Override
	public void update(Float value) {
		magneticField = value;
	}
}
