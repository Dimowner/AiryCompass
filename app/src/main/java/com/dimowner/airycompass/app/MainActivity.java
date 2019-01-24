/*
 * Copyright 2019 Dmitriy Ponomarenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import com.dimowner.airycompass.R;
import com.dimowner.airycompass.app.widget.AccelerometerView;
import com.dimowner.airycompass.app.widget.CompassView;
import com.dimowner.airycompass.sensor.SensorEventListenerImpl;

public class MainActivity extends Activity implements SensorEventListenerImpl.SensorsListener {

	private CompassView compassView;
	private AccelerometerView accelerometerView;
	private SensorEventListenerImpl sensorEventListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		compassView = findViewById(R.id.compass_view);
		accelerometerView = findViewById(R.id.accelerometer_view);

		sensorEventListener = new SensorEventListenerImpl(getApplicationContext());
		sensorEventListener.setSensorsListener(this);


		SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		if ((sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) || (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null)) {
			noSensorsAlert();
		}
	}

	public void noSensorsAlert(){
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
		compassView.updateAzimuth((azimuth + 360) % 360);
		accelerometerView.updateView(pitch, roll);
	}

	@Override
	public void onMagneticFieldChange(float value) {
		compassView.updateMagneticField(value);
	}
}
