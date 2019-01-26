package com.dimowner.airycompass.app.settings;


import com.dimowner.airycompass.Contract;

public class SettingsContract {

	interface View extends Contract.View {

		void showKeepScreenOn(boolean b);
	}

	public interface UserActionsListener extends Contract.UserActionsListener<SettingsContract.View> {

		void loadSettings();

		void keepScreenOn(boolean b);
	}
}
