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

package com.dimowner.airycompass.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import timber.log.Timber;

public class SensorsImpl implements SensorsContract.Sensors {

	private static final float ALPHA = 0.96f;
	private static final float ALPHA2 = 0.8f;
	private static final int UPDATE_INTERVAL = 10; //mills
	private static final int UPDATE_INTERVAL2 = 40; //mills

	private SensorManager sensorManager;
	private Sensor accelerometerSensor;
	private Sensor gravitySensor;
	private Sensor magneticSensor;

	private float[] acceleration = new float[3];
	private float[] gravity = new float[3];
	private float[] geomagnetic = new float[3];
	private float[] rotationMatrixR = new float[9];
	private float[] orientation = new float[3];

	private long accelerometerPrevTime = 0;
	private long linearAccelerometerPrevTime = 0;
	private long magneticPrevTime = 0;

	private SensorEventListener sensorEventListener;
	private SensorsContract.SensorsCallback callback;


	public SensorsImpl(Context context) {
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		if (accelerometerSensor == null || magneticSensor == null) {
			callback.onSensorsNotFound();
		}

		sensorEventListener = new SensorEventListener() {
			@Override
			public void onSensorChanged(SensorEvent event) {
				if (callback == null) return;

				long time = System.currentTimeMillis();
				switch (event.sensor.getType()) {
					case Sensor.TYPE_GRAVITY:
						if (gravitySensor != null) {
							gravity[0] = event.values[0];
							gravity[1] = event.values[1];
							gravity[2] = event.values[2];

							updateOrientation(time);
						}
						break;
					case Sensor.TYPE_ACCELEROMETER:
						if (gravitySensor == null) {
							gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * event.values[0];
							gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * event.values[1];
							gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * event.values[2];

							updateOrientation(time);
						}

						//Linear acceleration (acceleration minus gravity)
						acceleration[0] = ALPHA2 * acceleration[0] + (1 - ALPHA2) * (event.values[0] - gravity[0]);
						acceleration[1] = ALPHA2 * acceleration[1] + (1 - ALPHA2) * (event.values[1] - gravity[1]);
						acceleration[2] = ALPHA2 * acceleration[2] + (1 - ALPHA2) * (event.values[2] - gravity[2]);

						Timber.v("AccelerationChange a1 = " + acceleration[0] + " a2 = " + acceleration[1] + " a3 = " + acceleration[2]);
						if (time - linearAccelerometerPrevTime > UPDATE_INTERVAL) {
							callback.onLinearAccelerationChange(acceleration[0], acceleration[1], acceleration[2]);
							linearAccelerometerPrevTime = time;
						}
						break;
					case Sensor.TYPE_MAGNETIC_FIELD:
						geomagnetic[0] = ALPHA * geomagnetic[0] + (1 - ALPHA) * event.values[0];
						geomagnetic[1] = ALPHA * geomagnetic[1] + (1 - ALPHA) * event.values[1];
						geomagnetic[2] = ALPHA * geomagnetic[2] + (1 - ALPHA) * event.values[2];

						float magneticField = (float) Math.sqrt(geomagnetic[0] * geomagnetic[0]
								+ geomagnetic[1] * geomagnetic[1]
								+ geomagnetic[2] * geomagnetic[2]);
						if (time - magneticPrevTime > UPDATE_INTERVAL2) {
							callback.onMagneticFieldChange(magneticField);
							magneticPrevTime = time;
						}
						break;
				}
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				Timber.d("onAccuracyChanged sensor: " + sensor.getName() + " type: " + sensor.getType() + " accuracy: " + accuracy);
				if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
					if (callback != null) {
						callback.onAccuracyChanged(accuracy);
					}
					switch (accuracy) {
						case 0:
							Timber.v("Unreliable");
							break;
						case 1:
							Timber.v("Low Accuracy");
							break;
						case 2:
							Timber.v("Medium Accuracy");
							break;
						case 3:
							Timber.v("High Accuracy");
							break;
					}
				}
			}
		};
	}

	@Override
	public void setSensorsCallback(SensorsContract.SensorsCallback callback) {
		this.callback = callback;
	}

	@Override
	public void start() {
		sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
		sensorManager.registerListener(sensorEventListener, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
		sensorManager.registerListener(sensorEventListener, gravitySensor, SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	public void stop() {
		sensorManager.unregisterListener(sensorEventListener);
	}

	private void updateOrientation(long time) {
		if (SensorManager.getRotationMatrix(rotationMatrixR, null, gravity, geomagnetic)) {
			orientation = SensorManager.getOrientation(rotationMatrixR, orientation);
			if (time - accelerometerPrevTime > UPDATE_INTERVAL && callback != null) {
				callback.onRotationChange(
						(float) Math.toDegrees(orientation[0]), //azimuth
						(float) Math.toDegrees(orientation[1]), //pitch
						(float) Math.toDegrees(orientation[2])  //roll
				);
				accelerometerPrevTime = time;
			}
		}
	}
}
