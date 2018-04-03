package com.justtennis.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;

import com.justtennis.R;
import com.justtennis.domain.Invite.STATUS;

public class ListStatusAdapter extends BaseAdapter implements SpinnerAdapter {

	private static final Integer[] LIST_ID_DRAWABLE_STATUS = new Integer[]{R.drawable.check_green, R.drawable.check_red, R.drawable.check_yellow};
	private Activity activity;

	public ListStatusAdapter(Activity activity) {
		super();
		this.activity = activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Integer iDrawable = LIST_ID_DRAWABLE_STATUS[position];
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.list_status_row, null);
		}
		rowView.setTag(getStatus(position));

		ImageView ivStatus = (ImageView) rowView.findViewById(R.id.iv_row_status);
		ivStatus.setImageDrawable(activity.getResources().getDrawable(iDrawable));

		return rowView;
	}

	@Override
	public int getCount() {
		return LIST_ID_DRAWABLE_STATUS.length;
	}

	@Override
	public Object getItem(int position) {
		return LIST_ID_DRAWABLE_STATUS[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	public int getStatusPosition(STATUS status) {
		switch(status) {
			case ACCEPT:
				return 0;
			case REFUSE:
				return 1;
			default:
				return 2;
		}
	}
	
	private STATUS getStatus(Integer position) {
		switch(position) {
			case 0:
				return STATUS.ACCEPT;
			case 1:
				return STATUS.REFUSE;
			default:
				return STATUS.UNKNOW;
		}
	}
}