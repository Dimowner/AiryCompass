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

package com.dimowner.airycompass.sensor;

public interface SensorsContract {

	interface SensorsCallback {
		void onRotationChange(float azimuth, float pitch, float roll);
		void onMagneticFieldChange(float value);
		void onLinearAccelerationChange(float x, float y, float z);
		void onAccuracyChanged(int accuracy);
		void onSensorsNotFound();
	}

	interface Sensors {
		void setSensorsCallback(SensorsContract.SensorsCallback callback);
		void setEnergySavingMode(boolean b);
		void start();
		void stop();
	}
}
