package com.dimowner.airycompass.app.settings;

import com.dimowner.airycompass.data.Prefs;

public class SettingsPresenter implements SettingsContract.UserActionsListener {

	private SettingsContract.View view;

	private final Prefs prefs;

	public SettingsPresenter(Prefs prefs) {
		this.prefs = prefs;
	}

	@Override
	public void loadSettings() {
		view.showProgress();
		view.showKeepScreenOn(prefs.isKeepScreenOn());
	}

	@Override
	public void keepScreenOn(boolean keep) {
		prefs.setKeepScreenOn(keep);
	}

	@Override
	public void bindView(SettingsContract.View view) {
		this.view = view;
	}

	@Override
	public void unbindView() {
		this.view = null;
	}
}
