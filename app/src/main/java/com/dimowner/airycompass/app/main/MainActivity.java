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

package com.dimowner.airycompass.app.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dimowner.airycompass.ACApplication;
import com.dimowner.airycompass.ColorMap;
import com.dimowner.airycompass.R;
import com.dimowner.airycompass.app.settings.SettingsActivity;
import com.dimowner.airycompass.app.widget.AccelerometerView;
import com.dimowner.airycompass.app.widget.AccuracyView;
import com.dimowner.airycompass.app.widget.CompassCompoundView;
import com.dimowner.airycompass.app.widget.MagneticFieldView;

public class MainActivity extends Activity implements MainContract.View, View.OnClickListener {

	//	private CompassView compassView;
	private TextView txtAccuracyAlert;
	private CompassCompoundView compassCompoundView;
	private MagneticFieldView magneticFieldView;
	private AccuracyView accuracyView;

	private AccelerometerView orientationView;
	private AccelerometerView linearAccelerationView;
	private MainContract.UserActionsListener presenter;

	private ColorMap colorMap;
	private ColorMap.OnThemeColorChangeListener onThemeColorChangeListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		colorMap = ACApplication.getInjector().provideColorMap();
		setTheme(colorMap.getAppThemeResource());
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		compassCompoundView = findViewById(R.id.compass_view_compound);
//		compassView = findViewById(R.id.compass_view);
		magneticFieldView = findViewById(R.id.magnetic_field_view);
		txtAccuracyAlert = findViewById(R.id.txt_accuracy);
		accuracyView = findViewById(R.id.accuracy_view);
		orientationView = findViewById(R.id.accelerometer_view);
		linearAccelerationView = findViewById(R.id.accelerometer_view2);

		ImageButton btnSettings = findViewById(R.id.btn_settings);
		btnSettings.setOnClickListener(this);

		presenter = ACApplication.getInjector().provideMainPresenter();

		onThemeColorChangeListener = new ColorMap.OnThemeColorChangeListener() {
			@Override
			public void onThemeColorChange(int pos) {
				setTheme(colorMap.getAppThemeResource());
				recreate();
			}
		};
		colorMap.addOnThemeColorChangeListener(onThemeColorChangeListener);
	}

	@Override
	public void onStart() {
		super.onStart();
		presenter.bindView(this);
	}

	@Override
	public void onStop() {
		if (presenter != null) {
			presenter.unbindView();
		}
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		colorMap.removeOnThemeColorChangeListener(onThemeColorChangeListener);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_settings:
				startActivity(SettingsActivity.getStartIntent(getApplicationContext()));
				break;
		}
	}

	@Override
	public void keepScreenOn(boolean on) {
		if (on) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	}

	@Override
	public void updateRotation(float azimuth) {
		compassCompoundView.updateRotation((azimuth + 360) % 360);
	}

	@Override
	public void updateOrientation(float pitch, float roll) {
		orientationView.updateOrientation(pitch, roll);
	}

	@Override
	public void updateMagneticField(float magneticVal) {
		magneticFieldView.updateMagneticField(magneticVal);
	}

	@Override
	public void updateLinearAcceleration(float x, float y) {
		linearAccelerationView.updateLinearAcceleration(x, y);
	}

	@Override
	public void updateAccuracy(int accuracy) {
		accuracyView.updateAccuracyField(accuracy);
	}

	@Override
	public void alertBadAccuracy() {
		txtAccuracyAlert.setVisibility(View.VISIBLE);
	}

	@Override
	public void hideAlertBadAccuracy() {
		txtAccuracyAlert.setVisibility(View.GONE);
	}

	@Override
	public void showSensorsNotFound() {
		AlertDialog alertDialog = new AlertDialog.Builder(this)
				.setTitle(R.string.error)
				.setMessage(R.string.unable_to_init_sensor)
				.setNegativeButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						finish();
					}
				})
				.create();
		alertDialog.show();
	}

	@Override
	public void showSimpleMode(boolean isSimple) {
		linearAccelerationView.setSimpleMode(isSimple);
		orientationView.setSimpleMode(isSimple);
		accuracyView.setSimpleMode(isSimple);
		magneticFieldView.setSimpleMode(isSimple);
	}

	@Override
	public void showProgress() {
	}

	@Override
	public void hideProgress() {
	}

	@Override
	public void showError(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
	}

	@Override
	public void showError(int resId) {
		Toast.makeText(getApplicationContext(), resId, Toast.LENGTH_LONG).show();
	}
}
