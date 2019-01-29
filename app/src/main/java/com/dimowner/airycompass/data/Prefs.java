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

package com.dimowner.airycompass.data;

public interface Prefs {

	boolean isFirstRun();
	void firstRunExecuted();

	void setAppThemeColor(int colorMapPosition);
	int getThemeColor();

	void setKeepScreenOn(boolean on);
	boolean isKeepScreenOn();

	void setSimpleMode(boolean on);
	boolean isSimpleMode();

	void setEnergySavingMode(boolean on);
	boolean isEnergySavingMode();

	void setShowAcceleration(boolean b);
	boolean isShowAcceleration();

	void setShowOrientation(boolean b);
	boolean isShowOrientation();

	void setShowAccuracy(boolean b);
	boolean isShowAccuracy();

	void setShowMagnetic(boolean b);
	boolean isShowMagnetic();
}
