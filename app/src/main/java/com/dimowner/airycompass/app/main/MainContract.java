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

import com.dimowner.airycompass.Contract;

public interface MainContract {

	interface View extends Contract.View {

		void keepScreenOn(boolean on);

		void updateRotation(float azimuth);

		void updateOrientation(float pitch, float roll);

		void updateMagneticField(float magneticVal);

		void updateLinearAcceleration(float x, float y);

		void updateAccuracy(int accuracy);

		void alertBadAccuracy();

		void hideAlertBadAccuracy();

		void showSensorsNotFound();
	}

	interface UserActionsListener extends Contract.UserActionsListener<MainContract.View> {
	}
}
