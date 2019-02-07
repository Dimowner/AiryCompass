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

package com.dimowner.airycompass;

import android.app.Application;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class ACApplication extends Application {

	public static Injector injector;

	public static Injector getInjector() {
		return injector;
	}

	@Override
	public void onCreate() {
		if (BuildConfig.DEBUG) {
			//Timber initialization
			Timber.plant(new Timber.DebugTree() {
				@Override
				protected String createStackElementTag(StackTraceElement element) {
					return "LC-LC " + super.createStackElementTag(element) + ":" + element.getLineNumber();
				}
			});
		}
		super.onCreate();
		Fabric.with(this, new Crashlytics());

		injector = new Injector(getApplicationContext());
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		Timber.v("onTerminate");
		injector.releaseMainPresenter();
		injector.closeTasks();
	}
}
