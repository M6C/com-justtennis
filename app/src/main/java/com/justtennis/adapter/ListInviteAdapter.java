package com.justtennis.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
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
import com.justtennis.ui.common.CommonEnum;

import java.util.ArrayList;
import java.util.List;

public class ListInviteAdapter extends ArrayAdapter<Invite> {

	private List<Invite> value;
	private ArrayList<Invite> valueOld;
	private Activity activity;
	private CommonEnum.LIST_VIEW_MODE mode;
	private Filter filter = null;

	public ListInviteAdapter(Activity activity, List<Invite> value) {
		this(activity, value, CommonEnum.LIST_VIEW_MODE.MODIFY);
	}

	private ListInviteAdapter(Activity activity, List<Invite> value, CommonEnum.LIST_VIEW_MODE mode) {
		super(activity, R.layout.list_invite_row, android.R.id.text1, value);

		this.activity = activity;
		this.value = value;
		this.mode = mode;
		this.valueOld = new ArrayList<>(value);
		
		this.filter = new ListInviteByPlayerFilter(value1 -> {
            ListInviteAdapter.this.value.clear();
            ListInviteAdapter.this.value.addAll(value1);
            notifyDataSetChanged();
        }, valueOld);
	}

	@NonNull
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

	@NonNull
	@Override
	public View getView(int position, View convertView, @NonNull ViewGroup parent) {
		Invite invite = value.get(position);
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.list_invite_row, parent);
		}
		rowView.setTag(invite);

		InviteViewHelper.initializeView(activity, new InviteViewHolder(rowView), invite, mode);
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