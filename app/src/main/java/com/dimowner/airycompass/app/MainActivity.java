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

package com.dimowner.airycompass.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dimowner.airycompass.R;
import com.dimowner.airycompass.app.widget.AccelerometerView;
import com.dimowner.airycompass.app.widget.AccuracyView;
import com.dimowner.airycompass.app.widget.CompassViewCompound;
import com.dimowner.airycompass.app.widget.MagneticFieldView;
import com.dimowner.airycompass.sensor.SensorEventListenerImpl;

import timber.log.Timber;

public class MainActivity extends Activity implements SensorEventListenerImpl.SensorsListener {

//	private CompassView compassView;
	private TextView txtAccuracy;
	private CompassViewCompound compassViewCompound;
	private MagneticFieldView magneticFieldView;
	private AccuracyView accuracyView;

	private AccelerometerView orientationView;
	private AccelerometerView linearAccelerationView;
	private SensorEventListenerImpl sensorEventListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		compassViewCompound = findViewById(R.id.compass_view_compound);
//		compassView = findViewById(R.id.compass_view);
		magneticFieldView = findViewById(R.id.magnetic_field_view);
		txtAccuracy = findViewById(R.id.txt_accuracy);
		accuracyView = findViewById(R.id.accuracy_view);

		orientationView = findViewById(R.id.accelerometer_view);
		linearAccelerationView = findViewById(R.id.accelerometer_view2);

		sensorEventListener = new SensorEventListenerImpl(getApplicationContext());
		sensorEventListener.setSensorsListener(this);

		SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null
				|| sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null) {
			noSensorsAlert();
		}
		//If device doesn't have a gravity sensor use accelerometer sensor instead.
		if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) == null) {
			sensorEventListener.setHasGravitySensor(false);
		} else {
			sensorEventListener.setHasGravitySensor(true);
		}
	}

	public void noSensorsAlert(){
		//TODO: refactor dialog
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setMessage("Your device doesn't support the Compass.")
				.setCancelable(false)
				.setNegativeButton("Close",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						finish();
					}
				});
		alertDialog.show();
	}

	@Override
	public void onStart() {
		super.onStart();
		if (sensorEventListener != null) {
			sensorEventListener.start();
		}
	}

	@Override
	public void onStop() {
		if (sensorEventListener != null) {
			sensorEventListener.stop();
		}
		super.onStop();
	}

	@Override
	public void onRotationChange(float azimuth, float pitch, float roll) {
		compassViewCompound.updateRotation((azimuth + 360) % 360);
//		compassView.updateAzimuth((azimuth + 360) % 360);
		orientationView.updateOrientation(pitch, roll);
	}

	@Override
	public void onMagneticFieldChange(float value) {
//		compassView.updateMagneticField(value);
		magneticFieldView.updateMagneticField(value);
	}

	@Override
	public void onLinearAccelerationChange(float x, float y, float z) {
		linearAccelerationView.updateLinearAcceleration(x, y);
	}

	@Override
	public void onAccuracyChanged(int accuracy) {
		accuracyView.updateAccuracyField(accuracy);
		switch (accuracy) {
			case 0:
				Timber.v("Unreliable");
				txtAccuracy.setVisibility(View.VISIBLE);
				break;
			case 1:
				Timber.v("Low Accuracy");
				txtAccuracy.setVisibility(View.VISIBLE);
				break;
			case 2:
				Timber.v("Medium Accuracy");
				txtAccuracy.setVisibility(View.VISIBLE);
				break;
			case 3:
				Timber.v("High Accuracy");
				txtAccuracy.setVisibility(View.INVISIBLE);
				break;
		}
	}
}
