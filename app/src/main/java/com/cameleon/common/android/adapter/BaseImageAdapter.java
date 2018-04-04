package com.cameleon.common.android.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class BaseImageAdapter extends ArrayAdapter<Integer> {

	private Activity context;
	private int resource;
	private int imageViewResourceId;
	private ViewBinder viewBinder;

	public BaseImageAdapter(Activity context, int resource, int imageViewResourceId, Integer[] objects) {
		super(context, resource, imageViewResourceId, objects);
		this.context = context;
		this.resource = resource;
		this.imageViewResourceId = imageViewResourceId;
	}

	public BaseImageAdapter(Activity context, int resource, int textViewResourceId, List<Integer> objects) {
		super(context, resource, textViewResourceId, objects);
		this.context = context;
		this.resource = resource;
		this.imageViewResourceId = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return buildView(position, convertView);
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return buildView(position, convertView);
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

	private View buildView(int position, View convertView) {
		Integer iDrawable = getItem(position);
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(resource, null);
		}
		if (viewBinder!=null) {
			viewBinder.setViewValue(position, rowView);
		}

		ImageView ivStatus = (ImageView) rowView.findViewById(imageViewResourceId);
		ivStatus.setImageDrawable(context.getResources().getDrawable(iDrawable));

		return rowView;
	}
}