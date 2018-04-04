package com.cameleon.common.android.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class BaseViewAdapter extends ArrayAdapter<Integer> {

	private ViewBinder viewBinder;
	private LayoutInflater inflater;

	public BaseViewAdapter(Activity context, Integer[] drawable) {
		super(context, android.R.layout.simple_list_item_1, drawable);
		inflater = context.getLayoutInflater();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return buildView(position);
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return buildView(position);
	}

	public ViewBinder getViewBinder() {
		return viewBinder;
	}

	public void setViewBinder(ViewBinder viewBinder) {
		this.viewBinder = viewBinder;
	}

	public static interface ViewBinder {
		boolean setViewValue(int position, View view);
	}
	
	private View buildView(int position) {
		Integer iDrawable = getItem(position);
		View rowView = inflater.inflate(iDrawable, null);
		if (viewBinder!=null) {
			viewBinder.setViewValue(position, rowView);
		}
		return rowView;
	}
}