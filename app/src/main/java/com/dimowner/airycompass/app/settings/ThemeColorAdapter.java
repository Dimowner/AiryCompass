package com.dimowner.airycompass.app.settings;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dimowner.airycompass.R;

import java.util.List;

public class ThemeColorAdapter extends ArrayAdapter<ThemeColorAdapter.ThemeItem> {

	private LayoutInflater inflater;
	private int itemRes;
	private List<ThemeItem> data;

	ThemeColorAdapter(Activity context, int res, int txtRes, List<ThemeItem> items){
		super(context, res, txtRes, items);
		this.inflater = context.getLayoutInflater();
		this.data = items;
		this.itemRes = res;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getView(convertView, position, parent, true);
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getView(convertView, position, parent, false);
	}

	private View getView(View convertView, int position, ViewGroup parent, boolean showDrawable) {
		if(convertView == null){
			convertView = inflater.inflate(itemRes, parent, false);
		}
		TextView txtColor = convertView.findViewById(R.id.txtColor);
		ImageView ivImage = convertView.findViewById(R.id.ivImage);
		LinearLayout pnlItem = convertView.findViewById(R.id.pnlItem);
		txtColor.setText(data.get(position).getColorName());
		if (!showDrawable) {
			ivImage.setVisibility(View.INVISIBLE);
			pnlItem.setBackgroundColor(data.get(position).getColor());
		} else {
			ivImage.setVisibility(View.VISIBLE);
			pnlItem.setBackgroundColor(getContext().getResources().getColor(R.color.transparent));
		}
		return convertView;
	}

	public static class ThemeItem {
		private String colorName;
		private int color;

		ThemeItem(String colorName, int color) {
			this.colorName = colorName;
			this.color = color;
		}

		String getColorName() {
			return colorName;
		}

		public int getColor() {
			return color;
		}
	}
}
