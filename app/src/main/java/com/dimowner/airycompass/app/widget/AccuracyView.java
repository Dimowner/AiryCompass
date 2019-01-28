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
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class AccuracyView extends View {

	private int accuracy = 0;

	private ViewDrawer<Integer> drawer;
	private boolean isSimple = false;

	public AccuracyView(Context context) {
		super(context);
		init(context);
	}

	public AccuracyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public AccuracyView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		if (isSimple) {
			drawer = new AccuracyDrawerSimple(context);
		} else {
			drawer = new AccuracyDrawer(context);
		}
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
		drawer.layout(getWidth(), getHeight());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawer.draw(canvas);
	}

	public void updateAccuracyField(int a) {
		if (accuracy != a) {
			accuracy = a;
			drawer.update(a);
			invalidate();
		}
	}

	public void setSimpleMode(boolean mode) {
		if (isSimple != mode) {
			isSimple = mode;
			if (isSimple) {
				drawer = new AccuracyDrawerSimple(getContext());
			} else {
				drawer = new AccuracyDrawer(getContext());
			}
			drawer.update(accuracy);
			requestLayout();
		}
	}
}
