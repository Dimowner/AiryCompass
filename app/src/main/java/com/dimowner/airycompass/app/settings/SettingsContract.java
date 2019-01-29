package com.dimowner.airycompass.app.settings;


import com.dimowner.airycompass.Contract;

public class SettingsContract {

	interface View extends Contract.View {

		void showKeepScreenOnSetting(boolean b);

		void showSimpleModeSetting(boolean b);

		void showEnergySavingModeSetting(boolean b);

		void showAdvanced();
		void hideAdvanced();

		void setShowAccelerationView(boolean b);
		void setShowOrientationView(boolean b);
		void setShowAccuracyView(boolean b);
		void setShowMagneticView(boolean b);
	}

	public interface UserActionsListener extends Contract.UserActionsListener<SettingsContract.View> {

		void loadSettings();

		void keepScreenOn(boolean b);

		void simpleMode(boolean b);

		void energySavingMode(boolean b);

		void showAccelerationView(boolean b);
		void showOrientationView(boolean b);
		void showAccuracyView(boolean b);
		void showMagneticView(boolean b);

		void showAdvancedClicked();
	}
}
