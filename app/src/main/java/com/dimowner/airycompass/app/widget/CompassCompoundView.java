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
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dimowner.airycompass.R;

public class CompassCompoundView extends FrameLayout {

	public static final String DEGREE_SIGN = "°";
	private static final int UPDATE_INTERVAL_DIRECTION = 120; //mills
	private long directionUpdatePrevTime = 0;

	private CompassClockfaceView compassClockfaceView;
	private CompassBackgroundView compassBackgroundView;
	private TextView txtDirection;

	private String n;
	private String e;
	private String s;
	private String w;
	private String ne;
	private String se;
	private String sw;
	private String nw;

	public CompassCompoundView(Context context) {
		super(context);
		init(context);
	}

	public CompassCompoundView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CompassCompoundView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		inflate(context, R.layout.compass_view_compound, this);

		compassClockfaceView = findViewById(R.id.compass_clockface_view);
		compassBackgroundView = findViewById(R.id.compass_background_view);
		txtDirection = findViewById(R.id.txt_direction);

		Resources res = context.getResources();
		n = res.getString(R.string.n);
		e = res.getString(R.string.e);
		s = res.getString(R.string.s);
		w = res.getString(R.string.w);
		ne = res.getString(R.string.ne);
		se = res.getString(R.string.se);
		sw = res.getString(R.string.sw);
		nw = res.getString(R.string.nw);
	}

	public void updateRotation(float azimuth) {
		compassClockfaceView.updateAzimuth(azimuth);
		long curTime = System.currentTimeMillis();
		if (curTime - directionUpdatePrevTime > UPDATE_INTERVAL_DIRECTION) {
			String str = ((int) azimuth) + DEGREE_SIGN + " " + getDirectionText(azimuth);
			txtDirection.setText(str);
			directionUpdatePrevTime = curTime;
		}
	}

	public void setSimpleMode(boolean b) {
		compassBackgroundView.setSimpleMode(b);
	}

	private String getDirectionText(float degree) {
		final float step = 22.5f;
		if (degree >= 0 && degree < step || degree > 360 - step) {
			return n;
		}
		if (degree >= step && degree < step * 3) {
			return ne;
		}
		if (degree >= step * 3 && degree < step * 5) {
			return e;
		}
		if (degree >= step * 5 && degree < step * 7) {
			return se;
		}
		if (degree >= step * 7 && degree < step * 9) {
			return s;
		}
		if (degree >= step * 9 && degree < step * 11) {
			return sw;
		}
		if (degree >= step * 11 && degree < step * 13) {
			return w;
		}
		if (degree >= step * 13 && degree < step * 15) {
			return nw;
		}
		return "";
	}
}
