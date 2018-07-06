package com.justtennis.adapter;

import android.util.SparseLongArray;

import com.justtennis.R;
import com.justtennis.adapter.viewholder.PalmaresFastViewHolder;
import com.justtennis.domain.PalmaresFastValue;
import com.justtennis.ui.adapter.CommonListRecyclerViewAdapter;

import java.util.Date;
import java.util.List;

public class PalmaresFastAdapter extends CommonListRecyclerViewAdapter<PalmaresFastValue> {

	private final SparseLongArray mapPosition = new SparseLongArray();

	public PalmaresFastAdapter(List<PalmaresFastValue> value) {
		super(value, R.layout.list_palmares_fast_row);
		setFactoryViewHolder(PalmaresFastViewHolder::build);
		setHasStableIds(true);
	}

	@Override
	public long getItemId(int position) {
		Long ret = mapPosition.get(position, -1);
		if (ret == -1) {
			ret = new Date().getTime();
			mapPosition.put(position, ret);
		}
		return ret;
	}
}