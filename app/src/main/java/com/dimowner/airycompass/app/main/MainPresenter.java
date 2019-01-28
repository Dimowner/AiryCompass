/*
 * Copyright 2019 Dmitriy Ponomarenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dimowner.airycompass.app.main;

import com.dimowner.airycompass.data.Prefs;
import com.dimowner.airycompass.sensor.SensorsContract;
import timber.log.Timber;

public class MainPresenter implements MainContract.UserActionsListener {

	private MainContract.View view;

	private SensorsContract.Sensors sensors;
	private SensorsContract.SensorsCallback sensorsCallback;

	private final Prefs prefs;

	public MainPresenter(final Prefs prefs, final SensorsContract.Sensors sensors) {
		this.prefs = prefs;
		this.sensors = sensors;
	}

	@Override
	public void bindView(final MainContract.View v) {
		this.view = v;

		view.keepScreenOn(prefs.isKeepScreenOn());
		view.showSimpleMode(prefs.isSimpleMode());

		if (sensorsCallback == null) {
			sensorsCallback = new SensorsContract.SensorsCallback() {
				@Override
				public void onRotationChange(float azimuth, float pitch, float roll) {
					view.updateRotation(azimuth);
					view.updateOrientation(pitch, roll);
				}

				@Override
				public void onMagneticFieldChange(float value) {
					view.updateMagneticField(value);
				}

				@Override
				public void onLinearAccelerationChange(float x, float y, float z) {
					view.updateLinearAcceleration(x, y);
				}

				@Override
				public void onAccuracyChanged(int accuracy) {
					view.updateAccuracy(accuracy);
					switch (accuracy) {
						case 0:
						case 1:
						case 2:
							Timber.v("Accuracy is not good");
							view.alertBadAccuracy();
							break;
						case 3:
							Timber.v("High Accuracy");
							view.hideAlertBadAccuracy();
							break;
					}
				}

				@Override
				public void onSensorsNotFound() {
					view.showSensorsNotFound();
				}
			};
		}
		sensors.setEnergySavingMode(prefs.isEnergySavingMode());
		sensors.setSensorsCallback(sensorsCallback);
		sensors.start();
	}

	@Override
	public void unbindView() {
		this.view = null;
		sensors.stop();
	}
}
