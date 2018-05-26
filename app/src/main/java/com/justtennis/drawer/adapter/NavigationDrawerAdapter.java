package com.justtennis.drawer.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.justtennis.drawer.adapter.notifier.INavigationDrawerNotifer;
import com.justtennis.drawer.data.NavigationDrawerData;

public class NavigationDrawerAdapter extends BaseAdapter {

	private List<NavigationDrawerData> value = new ArrayList<NavigationDrawerData>();

	private Context context;

	public NavigationDrawerAdapter(Context context) {
		super();
		this.context = context;
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

	@SuppressLint("InviteViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		NavigationDrawerData v = value.get(position);
		long id = v.getId();
		INavigationDrawerNotifer notifier = v.getNotifer();

		View rowView = convertView;
		if (rowView == null || Long.parseLong(rowView.getTag().toString()) != id) {
	        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		this.value = value;
	}

}