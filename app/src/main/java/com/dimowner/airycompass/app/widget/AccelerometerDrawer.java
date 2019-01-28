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
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.dimowner.airycompass.R;

public class AccelerometerDrawer implements ViewDrawer<PointF> {

	private Paint pathPaint;
	private Paint ballPaint;
	private Path path;

	private float xPos;
	private float yPos;

	private Point CENTER;
	private int RADIUS;

	private boolean isSimple;


	public AccelerometerDrawer(Context context, AttributeSet attrs, boolean isSimple) {
		this.isSimple = isSimple;
		int gridColor = context.getResources().getColor(R.color.md_white_1000);
		int ballColor = context.getResources().getColor(R.color.md_white_1000);

		if (attrs != null) {
			TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AccelerometerView);
			if (ta != null) {
				//Read View custom attributes
				gridColor = ta.getColor(R.styleable.AccelerometerView_gridColor, context.getResources().getColor(R.color.md_white_1000));
				ballColor = ta.getColor(R.styleable.AccelerometerView_ballColor, context.getResources().getColor(R.color.md_white_1000));
				ta.recycle();
			} else {
				//If failed to read View attributes, then read app theme attributes for for view colors.
				TypedValue typedValue = new TypedValue();
				Resources.Theme theme = context.getTheme();
				theme.resolveAttribute(R.attr.accelerometerGridColor, typedValue, true);
				gridColor = typedValue.data;
				theme.resolveAttribute(R.attr.accelerometerBallColor, typedValue, true);
				ballColor = typedValue.data;
			}
		}

		pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		pathPaint.setStrokeWidth(1);
		pathPaint.setStyle(Paint.Style.STROKE);
		pathPaint.setColor(gridColor);

		ballPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		ballPaint.setStyle(Paint.Style.FILL);
		ballPaint.setColor(ballColor);

		CENTER = new Point(0, 0);
	}

	@Override
	public void layout(int width, int height) {
		RADIUS = width/10;
		CENTER.set(width/2, height/2);

		//Layout grid
		if (path == null) {
			float radius = width/2f - width*0.03f;
			path = new Path();
			path.moveTo(CENTER.x - radius, CENTER.y);
			path.lineTo(CENTER.x + radius, CENTER.y);
			path.moveTo(CENTER.x, CENTER.y - radius);
			path.lineTo(CENTER.x, CENTER.y + radius);
			if (!isSimple) {
				path.addCircle(CENTER.x, CENTER.y, radius, Path.Direction.CCW);
			}
		}
	}

	@Override
	public void draw(Canvas canvas) {
		//Draw grid
		canvas.drawPath(path, pathPaint);
		//Draw ball
		canvas.drawCircle(CENTER.x - xPos, CENTER.y + yPos, RADIUS, ballPaint);
	}

	@Override
	public void update(PointF p) {
		this.xPos = p.x;
		this.yPos = p.y;
	}
}
