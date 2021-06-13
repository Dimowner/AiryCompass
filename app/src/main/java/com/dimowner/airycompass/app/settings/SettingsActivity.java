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

package com.dimowner.airycompass.app.settings;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dimowner.airycompass.ACApplication;
import com.dimowner.airycompass.AppConstants;
import com.dimowner.airycompass.ColorMap;
import com.dimowner.airycompass.R;
import com.dimowner.airycompass.util.AnimationUtil;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends Activity implements SettingsContract.View, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

	private static final String VERSION_UNAVAILABLE = "N/A";

	public static final long ANIMATION_DURATION = 200;

	private Switch swKeepScreenOn;
	private Switch swEnergySaving;
	private Switch swSimpleMode;

	private Switch swShowAcceleration;
	private Switch swShowOrientation;
	private Switch swShowAccuracy;
	private Switch swShowMagnetic;

	private LinearLayout pnlShowAcceleration;
	private LinearLayout pnlShowOrientation;
	private LinearLayout pnlShowAccuracy;
	private LinearLayout pnlShowMagnetic;

	private ImageView imgAdvanced;

	private SettingsContract.UserActionsListener presenter;
	private ColorMap colorMap;
	private ColorMap.OnThemeColorChangeListener onThemeColorChangeListener;


	public static Intent getStartIntent(Context context) {
		Intent intent = new Intent(context, SettingsActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		colorMap = ACApplication.getInjector().provideColorMap();
		setTheme(colorMap.getAppThemeResource());
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		ImageButton btnBack = findViewById(R.id.btn_back);
		TextView btnRate = findViewById(R.id.btnRate);
		TextView btnRequest = findViewById(R.id.btnRequest);
		TextView txtAbout = findViewById(R.id.txtAbout);
		LinearLayout btnAdvanced= findViewById(R.id.pnlAdvanced);
		imgAdvanced = findViewById(R.id.imgAdvanced);
		txtAbout.setText(getAboutContent());
		btnBack.setOnClickListener(this);
		btnRate.setOnClickListener(this);
		btnRequest.setOnClickListener(this);
		btnAdvanced.setOnClickListener(this);
		swKeepScreenOn = findViewById(R.id.swKeepScreenOn);
		swEnergySaving = findViewById(R.id.swEnergySaving);
		swSimpleMode = findViewById(R.id.swSimpleMode);

		swShowAcceleration = findViewById(R.id.swShowAcceleration);
		swShowOrientation = findViewById(R.id.swShowOrientation);
		swShowAccuracy = findViewById(R.id.swShowAccuracy);
		swShowMagnetic = findViewById(R.id.swShowMagnetic);

		pnlShowAcceleration = findViewById(R.id.pnlAcceleration);
		pnlShowOrientation = findViewById(R.id.pnlOrientation);
		pnlShowAccuracy = findViewById(R.id.pnlAccuracy);
		pnlShowMagnetic = findViewById(R.id.pnlMagnetic);

		swKeepScreenOn.setOnCheckedChangeListener(this);
		swEnergySaving.setOnCheckedChangeListener(this);
		swSimpleMode.setOnCheckedChangeListener(this);
		swShowAcceleration.setOnCheckedChangeListener(this);
		swShowOrientation.setOnCheckedChangeListener(this);
		swShowAccuracy.setOnCheckedChangeListener(this);
		swShowMagnetic.setOnCheckedChangeListener(this);

		presenter = ACApplication.getInjector().provideSettingsPresenter();

		final Spinner spinner = findViewById(R.id.themeColor);
		List<ThemeColorAdapter.ThemeItem> items = new ArrayList<>();
		String[] values = getResources().getStringArray(R.array.theme_colors);
		int[] colorRes = colorMap.getColorResources();
		for (int i = 0; i < values.length; i++) {
			items.add(new ThemeColorAdapter.ThemeItem(values[i], getApplicationContext().getResources().getColor(colorRes[i])));
		}
		ThemeColorAdapter adapter = new ThemeColorAdapter(SettingsActivity.this,
				R.layout.list_item_spinner, R.id.txtColor, items);
		spinner.setAdapter(adapter);

		onThemeColorChangeListener = new ColorMap.OnThemeColorChangeListener() {
			@Override
			public void onThemeColorChange(int pos) {
				setTheme(colorMap.getAppThemeResource());
				recreate();
			}
		};
		colorMap.addOnThemeColorChangeListener(onThemeColorChangeListener);

		if (colorMap.getSelected() > 0) {
			spinner.setSelection(colorMap.getSelected());
		}
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				colorMap.updateColorMap(position);
			}
			@Override public void onNothingSelected(AdapterView<?> parent) { }
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		presenter.bindView(this);
		presenter.loadSettings();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (presenter != null) {
			presenter.unbindView();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		colorMap.removeOnThemeColorChangeListener(onThemeColorChangeListener);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_back:
				ACApplication.getInjector().releaseSettingsPresenter();
				finish();
				break;
			case R.id.btnRate:
				rateApp();
				break;
			case R.id.btnRequest:
				requestFeature();
				break;
			case R.id.pnlAdvanced:
				presenter.showAdvancedClicked();
				break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
			case R.id.swKeepScreenOn:
				presenter.keepScreenOn(isChecked);
				break;
			case R.id.swEnergySaving:
				presenter.energySavingMode(isChecked);
				break;
			case R.id.swSimpleMode:
				presenter.simpleMode(isChecked);
				break;
			case R.id.swShowAcceleration:
				presenter.showAccelerationView(isChecked);
				break;
			case R.id.swShowOrientation:
				presenter.showOrientationView(isChecked);
				break;
			case R.id.swShowAccuracy:
				presenter.showAccuracyView(isChecked);
				break;
			case R.id.swShowMagnetic:
				presenter.showMagneticView(isChecked);
				break;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		ACApplication.getInjector().releaseSettingsPresenter();
	}

	private void rateApp() {
		try {
			Intent rateIntent = rateIntentForUrl("market://details");
			startActivity(rateIntent);
		} catch (ActivityNotFoundException e) {
			Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
			startActivity(rateIntent);
		}
	}

	private void requestFeature() {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.setData(Uri.parse("mailto:"));
		i.putExtra(Intent.EXTRA_EMAIL, new String[]{AppConstants.REQUESTS_RECEIVER});
		i.putExtra(Intent.EXTRA_SUBJECT,
				"[" + getResources().getString(R.string.app_name) + "] - " + getResources().getString(R.string.request)
		);
		try {
			startActivity(Intent.createChooser(i, getResources().getString(R.string.send_email)));
		} catch (android.content.ActivityNotFoundException ex) {
			showError(R.string.email_clients_not_found);
		}
	}

	private Intent rateIntentForUrl(String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getApplicationContext().getPackageName())));
		int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
		if (Build.VERSION.SDK_INT >= 21) {
			flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
		} else {
			flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
		}
		intent.addFlags(flags);
		return intent;
	}

	public SpannableStringBuilder getAboutContent() {
		// Get app version;
		String packageName = getPackageName();
		String versionName;
		try {
			PackageInfo info = getPackageManager().getPackageInfo(packageName, 0);
			versionName = info.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			versionName = VERSION_UNAVAILABLE;
		}

		// Build the about body view and append the link to see OSS licenses
		SpannableStringBuilder aboutBody = new SpannableStringBuilder();
		aboutBody.append(Html.fromHtml(getString(R.string.about_body, versionName)));
		return aboutBody;
	}

	@Override
	public void showKeepScreenOnSetting(boolean b) {
		swKeepScreenOn.setChecked(b);
	}

	@Override
	public void showSimpleModeSetting(boolean b) {
		swSimpleMode.setChecked(b);
	}

	@Override
	public void showEnergySavingModeSetting(boolean b) {
		swEnergySaving.setChecked(b);
	}

	@Override
	public void showAdvanced() {
		AnimationUtil.viewRotationAnimation(imgAdvanced, ANIMATION_DURATION);
		pnlShowAcceleration.setVisibility(View.VISIBLE);
		pnlShowOrientation.setVisibility(View.VISIBLE);
		pnlShowAccuracy.setVisibility(View.VISIBLE);
		pnlShowMagnetic.setVisibility(View.VISIBLE);
	}

	@Override
	public void hideAdvanced() {
		AnimationUtil.viewBackRotationAnimation(imgAdvanced, ANIMATION_DURATION);
		pnlShowAcceleration.setVisibility(View.GONE);
		pnlShowOrientation.setVisibility(View.GONE);
		pnlShowAccuracy.setVisibility(View.GONE);
		pnlShowMagnetic.setVisibility(View.GONE);
	}

	@Override
	public void setShowAccelerationView(boolean b) {
		swShowAcceleration.setChecked(b);
	}

	@Override
	public void setShowOrientationView(boolean b) {
		swShowOrientation.setChecked(b);
	}

	@Override
	public void setShowAccuracyView(boolean b) {
		swShowAccuracy.setChecked(b);
	}

	@Override
	public void setShowMagneticView(boolean b) {
		swShowMagnetic.setChecked(b);
	}

	@Override
	public void showProgress() {
	}

	@Override
	public void hideProgress() {
	}

	@Override
	public void showError(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
	}

	@Override
	public void showError(int resId) {
		Toast.makeText(getApplicationContext(), resId, Toast.LENGTH_LONG).show();
	}
}
