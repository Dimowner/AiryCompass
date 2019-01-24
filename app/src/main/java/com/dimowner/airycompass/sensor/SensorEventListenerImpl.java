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

	private float orientation[] = new float[3];
	private SensorManager sensorManager;
	private Sensor accelerometerSensor;
	private Sensor magneticSensor;
	private float[] gravity = new float[3];
	private float[] geomagnetic = new float[3];
	private static final int UPDATE_INTERVAL = 10; //mills
	private long accelerometerPrevTime = 0;
	private long magneticPrevTime = 0;
	private float[] rotationMatrixR = new float[9];
	private float[] rotationMatrixI = new float[9];
	private SensorsListener sensorsListener;
	private final float ALPHA = 0.96f;

	public SensorEventListenerImpl(Context context) {
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (sensorsListener == null) return;

		long time = System.currentTimeMillis();
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * event.values[0];
			gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * event.values[1];
			gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * event.values[2];

			if (SensorManager.getRotationMatrix(rotationMatrixR, rotationMatrixI, gravity, geomagnetic)) {
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
		} else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			geomagnetic[0] = ALPHA * geomagnetic[0] + (1 - ALPHA) * event.values[0];
			geomagnetic[1] = ALPHA * geomagnetic[1] + (1 - ALPHA) * event.values[1];
			geomagnetic[2] = ALPHA * geomagnetic[2] + (1 - ALPHA) * event.values[2];

			float magneticField = (float) Math.sqrt(geomagnetic[0] * geomagnetic[0]
					+ geomagnetic[1] * geomagnetic[1]
					+ geomagnetic[2] * geomagnetic[2]);
			if (time - magneticPrevTime > UPDATE_INTERVAL) {
				sensorsListener.onMagneticFieldChange(magneticField);
				magneticPrevTime = time;
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		Timber.d("onAccuracyChanged sensor: " + sensor.getName() + " type: " + sensor.getType() + " accuracy: " + accuracy);
	}

	public void start() {
		sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
		sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
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
	}
}
