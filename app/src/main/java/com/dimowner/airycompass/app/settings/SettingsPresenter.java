package com.dimowner.airycompass.app.settings;

import com.dimowner.airycompass.data.Prefs;

public class SettingsPresenter implements SettingsContract.UserActionsListener {

	private SettingsContract.View view;

	private final Prefs prefs;

	private boolean showAdvanced = false;

	public SettingsPresenter(Prefs prefs) {
		this.prefs = prefs;
	}

	@Override
	public void loadSettings() {
		view.showProgress();
		view.showKeepScreenOnSetting(prefs.isKeepScreenOn());
		view.showEnergySavingModeSetting(prefs.isEnergySavingMode());
		view.showSimpleModeSetting(prefs.isSimpleMode());
		view.setShowAccelerationView(prefs.isShowAcceleration());
		view.setShowOrientationView(prefs.isShowOrientation());
		view.setShowAccuracyView(prefs.isShowAccuracy());
		view.setShowMagneticView(prefs.isShowMagnetic());
	}

	@Override
	public void keepScreenOn(boolean keep) {
		prefs.setKeepScreenOn(keep);
	}

	@Override
	public void simpleMode(boolean b) {
		prefs.setSimpleMode(b);
	}

	@Override
	public void energySavingMode(boolean b) {
		prefs.setEnergySavingMode(b);
	}

	@Override
	public void showAccelerationView(boolean b) {
		prefs.setShowAcceleration(b);
	}

	@Override
	public void showOrientationView(boolean b) {
		prefs.setShowOrientation(b);
	}

	@Override
	public void showAccuracyView(boolean b) {
		prefs.setShowAccuracy(b);
	}

	@Override
	public void showMagneticView(boolean b) {
		prefs.setShowMagnetic(b);
	}

	@Override
	public void showAdvancedClicked() {
		showAdvanced = !showAdvanced;
		if (showAdvanced) {
			view.showAdvanced();
		} else {
			view.hideAdvanced();
		}

	}

	@Override
	public void bindView(SettingsContract.View view) {
		this.view = view;
		this.view.hideAdvanced();
	}

	@Override
	public void unbindView() {
		this.view = null;
	}
}
