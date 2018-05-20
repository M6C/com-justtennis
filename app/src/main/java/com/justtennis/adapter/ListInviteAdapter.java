package com.justtennis.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.justtennis.R;
import com.justtennis.adapter.viewholder.InviteViewHolder;
import com.justtennis.domain.Invite;
import com.justtennis.filter.ListInviteByPlayerFilter;
import com.justtennis.helper.InviteViewHelper;

import java.util.ArrayList;
import java.util.List;

public class ListInviteAdapter extends ArrayAdapter<Invite> {

	private List<Invite> value;
	private ArrayList<Invite> valueOld;
	private Activity activity;
	private InviteViewHelper.INVITE_MODE_VIEW mode;
	private Filter filter = null;

	public ListInviteAdapter(Activity activity, List<Invite> value) {
		this(activity, value, InviteViewHelper.INVITE_MODE_VIEW.MODIFY);
	}

	public ListInviteAdapter(Activity activity, List<Invite> value, InviteViewHelper.INVITE_MODE_VIEW mode) {
		super(activity, R.layout.list_invite_row, android.R.id.text1, value);

		this.activity = activity;
		this.value = value;
		this.mode = mode;
		this.valueOld = new ArrayList<Invite>(value);
		
		this.filter = new ListInviteByPlayerFilter(new ListInviteByPlayerFilter.IValueNotifier() {
			@Override
			public void setValue(List<Invite> value) {
				ListInviteAdapter.this.value.clear();
				ListInviteAdapter.this.value.addAll(value);
				notifyDataSetChanged();
			}
		}, valueOld);
	}

	@Override
	public Filter getFilter() {
		if (filter!=null) {
			return filter;
		} else {
			return super.getFilter();
		}
	}
	
	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Invite v = value.get(position);
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.list_invite_row, null);
		}

		InviteViewHolder viewHolder = new InviteViewHolder(rowView);
		viewHolder.invite = v;
		rowView.setTag(viewHolder);

		InviteViewHelper.initializeView(activity, viewHolder, mode);
	    return rowView;
	}

	/**
	 * @return the value
	 */
	public List<Invite> getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(List<Invite> value) {
		this.value = value;

		valueOld.clear();
		valueOld.addAll(this.value);
	}
}