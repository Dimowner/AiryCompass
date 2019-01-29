package com.dimowner.airycompass;

import com.dimowner.airycompass.data.Prefs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ColorMap {

	private static ColorMap singleton;

	public static ColorMap getInstance(Prefs prefs) {
		if (singleton == null) {
			singleton = new ColorMap(prefs);
		}
		return singleton;
	}

	private int appThemeResource = 0;
	private int selected;
	private List<OnThemeColorChangeListener> onThemeColorChangeListeners;
	private Prefs prefs;

	private ColorMap(Prefs prefs) {
		onThemeColorChangeListeners = new ArrayList<>();
		this.prefs = prefs;
		selected = prefs.getThemeColor();
		init(selected);
	}

	private void init(int color) {
		if (color < 0 || color > 8) {
			color = new Random().nextInt(7);
		}
		switch (color) {
			case 0:
				appThemeResource = R.style.AppTheme;
				break;
			case 1:
				appThemeResource = R.style.AppTheme_Black;
				break;
			case 2:
				appThemeResource = R.style.AppTheme_Grey;
				break;
			case 3:
				appThemeResource = R.style.AppTheme_Brown;
				break;
			case 4:
				appThemeResource = R.style.AppTheme_Green;
				break;
			case 5:
				appThemeResource = R.style.AppTheme_Red;
				break;
			case 6:
				appThemeResource = R.style.AppTheme_Pink;
				break;
			case 7:
				appThemeResource = R.style.AppTheme_Purple;
				break;
			default:
				appThemeResource = R.style.AppTheme;
		}
	}

	public void updateColorMap(int num) {
		int ondSelected = selected;
		selected = num;
		if (ondSelected != selected) {
			prefs.setAppThemeColor(selected);
			init(selected);
			onThemeColorChange(selected);
		}
	}

	public int getSelected() {
		return selected;
	}

	public int getAppThemeResource() {
		return appThemeResource;
	}

	public int[] getColorResources() {
		return new int[] {
				R.color.colorPrimary,
				R.color.md_black_1000,
				R.color.md_grey_800x,
				R.color.md_brown_600,
				R.color.md_green_600,
				R.color.md_red_500,
				R.color.md_pink_500,
				R.color.md_deep_purple_500
		};
	}

	public void addOnThemeColorChangeListener(OnThemeColorChangeListener onThemeColorChangeListener) {
		this.onThemeColorChangeListeners.add(onThemeColorChangeListener);
	}

	public void removeOnThemeColorChangeListener(OnThemeColorChangeListener onThemeColorChangeListener) {
		this.onThemeColorChangeListeners.remove(onThemeColorChangeListener);
	}

	public void onThemeColorChange(int pos) {
		for (int i = 0; i < onThemeColorChangeListeners.size(); i++) {
			onThemeColorChangeListeners.get(i).onThemeColorChange(pos);
		}
	}

	public interface OnThemeColorChangeListener {
		void onThemeColorChange(int pos);
	}
}
