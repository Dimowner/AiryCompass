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
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import com.dimowner.airycompass.util.AndroidUtils;

import timber.log.Timber;

public class AccelerometerView extends View {

	private float roll = 0f;
	private float pitch = 0f;

	private float xPos;
	private float yPos;

	private int MAX_ACCELERATION;

	private int MAX_MOVE = (int) AndroidUtils.dpToPx(50); //dip
	//Converted value from pixels to coefficient used in function which describes move.
	private float k = (float) (MAX_MOVE / (Math.PI/2));

	private ViewDrawer<PointF> drawer;
	private boolean isSimple = false;
	private AttributeSet attributeSet;

	public AccelerometerView(Context context) {
		super(context);
		init(context, null);
	}

	public AccelerometerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public AccelerometerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		attributeSet = attrs;
//		if (isSimple) {
//			drawer = new AccelerometerDrawerSimple(context, attributeSet);
//		} else {
			drawer = new AccelerometerDrawer(context, attributeSet, isSimple);
//		}
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

		drawer.layout(getWidth(), getHeight());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawer.draw(canvas);
	}

	public void updateOrientation(float pitch, float roll) {
		if ((int)this.pitch != (int)pitch || (int)this.roll != (int)roll) {
			this.pitch = pitch;
			this.roll = roll;

			xPos = getWidth() * 0.37f * (float) Math.cos(Math.toRadians(90 - roll));
			yPos = getWidth() * 0.37f * (float) Math.cos(Math.toRadians(90 - pitch));

			drawer.update(new PointF(xPos, yPos));
			invalidate();
		}
	}

	public void updateLinearAcceleration(float x, float y) {
		if ((int)pitch != (int)x*1000 || (int)roll != (int)y*1000) {
			this.pitch = x*1000;
			this.roll = y*1000;

			xPos = (float) (k * Math.atan(x*MAX_ACCELERATION/k));
			yPos = (float) (k * Math.atan(y*MAX_ACCELERATION/k));

//			Timber.v("updateLinearAcceleration pitch = " + x + " roll = " + y + " xPos = " + xPos + " yPos = " + yPos);
			drawer.update(new PointF(xPos, yPos));
			invalidate();
		}
	}

	public void setSimpleMode(boolean mode) {
		if (isSimple != mode) {
			isSimple = mode;
//			if (isSimple) {
//				drawer = new AccelerometerDrawerSimple(getContext(), attributeSet);
//			} else {
				drawer = new AccelerometerDrawer(getContext(), attributeSet, isSimple);
//			}
			requestLayout();
		}
	}
}
