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

package com.dimowner.airycompass;

import android.content.Context;

import com.dimowner.airycompass.app.settings.SettingsContract;
import com.dimowner.airycompass.app.settings.SettingsPresenter;
import com.dimowner.airycompass.data.Prefs;
import com.dimowner.airycompass.data.PrefsImpl;

public class Injector {

	private Context context;

	private SettingsContract.UserActionsListener settingsPresenter;

	public Injector(Context context) {
		this.context = context;
	}

	public Prefs providePrefs() {
		return PrefsImpl.getInstance(context);
	}

	public ColorMap provideColorMap() {
		return ColorMap.getInstance(providePrefs());
	}

	public SettingsContract.UserActionsListener provideSettingsPresenter() {
		if (settingsPresenter == null) {
			settingsPresenter = new SettingsPresenter(providePrefs());
		}
		return settingsPresenter;
	}

	public void releaseMainPresenter() {
//		if (mainPresenter != null) {
//			mainPresenter.unbindView();
//			mainPresenter.clear();
//			mainPresenter = null;
//		}
	}

	public void releaseSettingsPresenter() {
		if (settingsPresenter != null) {
			settingsPresenter.unbindView();
			settingsPresenter.clear();
			settingsPresenter = null;
		}
	}

	public void closeTasks() {
	}
}
