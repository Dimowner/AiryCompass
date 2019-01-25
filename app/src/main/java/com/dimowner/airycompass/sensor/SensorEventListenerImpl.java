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

public class SensorEventListenerImpl implements SensorEventListener {

	private boolean hasGravitySensor = true;

	private float orientation[] = new float[3];
	private SensorManager sensorManager;
	private Sensor accelerometerSensor;
	private Sensor gravitySensor;
	private Sensor magneticSensor;
	private float[] acceleration = new float[3];
	private float[] gravity = new float[3];
	private float[] geomagnetic = new float[3];
	private static final int UPDATE_INTERVAL = 10; //mills
	private static final int UPDATE_INTERVAL2 = 40; //mills
	private long accelerometerPrevTime = 0;
	private long linearAccelerometerPrevTime = 0;
	private long magneticPrevTime = 0;
	private float[] rotationMatrixR = new float[9];
	private SensorsListener sensorsListener;
	private final float ALPHA = 0.96f;
	private final float ALPHA2 = 0.8f;

	public SensorEventListenerImpl(Context context) {
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (sensorsListener == null) return;

		long time = System.currentTimeMillis();
		switch (event.sensor.getType()) {
			case Sensor.TYPE_GRAVITY:
				if (hasGravitySensor) {
					gravity[0] = event.values[0];
					gravity[1] = event.values[1];
					gravity[2] = event.values[2];

					updateOrientation(time);
				}
				break;
			case Sensor.TYPE_ACCELEROMETER:
				if (!hasGravitySensor) {
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
					sensorsListener.onLinearAccelerationChange(acceleration[0], acceleration[1], acceleration[2]);
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
					sensorsListener.onMagneticFieldChange(magneticField);
					magneticPrevTime = time;
				}
				break;
		}
	}

	private void updateOrientation(long time) {
		if (SensorManager.getRotationMatrix(rotationMatrixR, null, gravity, geomagnetic)) {
			orientation = SensorManager.getOrientation(rotationMatrixR, orientation);
			if (time - accelerometerPrevTime > UPDATE_INTERVAL) {
				sensorsListener.onRotationChange(
						(float) Math.toDegrees(orientation[0]), //azimuth
						(float) Math.toDegrees(orientation[1]), //pitch
						(float) Math.toDegrees(orientation[2])  //roll
				);
				accelerometerPrevTime = time;
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		Timber.d("onAccuracyChanged sensor: " + sensor.getName() + " type: " + sensor.getType() + " accuracy: " + accuracy);
		if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			if (sensorsListener != null) {
				sensorsListener.onAccuracyChanged(accuracy);
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

	public void setHasGravitySensor(boolean b) {
		this.hasGravitySensor = b;
	}

	public void start() {
		sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
		sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
		sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_GAME);
	}

	public void stop() {
		sensorManager.unregisterListener(this);
	}

	public void setSensorsListener(SensorsListener sensorsListener) {
		this.sensorsListener = sensorsListener;
	}

	public interface SensorsListener {
		void onRotationChange(float azimuth, float pitch, float roll);

		void onMagneticFieldChange(float value);

		void onLinearAccelerationChange(float x, float y, float z);

		void onAccuracyChanged(int accuracy);
	}
}
