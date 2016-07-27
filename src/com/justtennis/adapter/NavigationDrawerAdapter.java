package com.justtennis.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class NavigationDrawerAdapter extends BaseAdapter {

	private List<NavigationDrawerData> value = new ArrayList<NavigationDrawerAdapter.NavigationDrawerData>();

	private LayoutInflater inflater;

	public NavigationDrawerAdapter(LayoutInflater inflater) {
		super();
		this.inflater = inflater;
	}

	@Override
	public int getCount() {
		return this.value.size();
	}

	@Override
	public Object getItem(int position) {
		return this.value.get(position);
	}

	@Override
	public long getItemId(int position) {
		return this.value.get(position).getId();
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		NavigationDrawerData v = value.get(position);
		long id = v.getId();
		NavigationDrawerNotifer notifier = v.getNotifer();

		View rowView = convertView;
		if (rowView == null || Long.parseLong(rowView.getTag().toString()) != id) {
			rowView = inflater.inflate(v.getLayout(), null, false);
			rowView.setTag(id);
			if (notifier != null) {
				notifier.onCreateView(rowView);
			}
		} else {
			if (notifier != null) {
				notifier.onUpdateView(rowView);
			}
		}

		return rowView;
	}

	public List<NavigationDrawerData> getValue() {
		return value;
	}

	public void setValue(List<NavigationDrawerData> value) {
		this.value.clear();
		if (value != null) {
			this.value.addAll(value);
		}
	}

	public static class NavigationDrawerData {
		private long id;
		private int layout;
		private NavigationDrawerNotifer notifer;

		public NavigationDrawerData(long id, int layout, NavigationDrawerNotifer notifer) {
			this.id = id;
			this.layout = layout;
			this.notifer = notifer;
		}

		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}
		public int getLayout() {
			return layout;
		}
		public void setLayout(int layout) {
			this.layout = layout;
		}
		public NavigationDrawerNotifer getNotifer() {
			return notifer;
		}
		public void setNotifer(NavigationDrawerNotifer notifer) {
			this.notifer = notifer;
		}
	}

	public static interface NavigationDrawerNotifer {
		public void onCreateView(View view);
		public void onUpdateView(View view);
	}
}