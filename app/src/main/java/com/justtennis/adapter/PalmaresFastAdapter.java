package com.justtennis.adapter;

import com.justtennis.R;
import com.justtennis.adapter.viewholder.PalmaresFastViewHolder;
import com.justtennis.domain.PalmaresFastValue;
import com.justtennis.ui.adapter.CommonListRecyclerViewAdapter;

import java.util.List;

public class PalmaresFastAdapter extends CommonListRecyclerViewAdapter<PalmaresFastValue> {

	private static final String TAG = PalmaresFastAdapter.class.getSimpleName();

	public PalmaresFastAdapter(List<PalmaresFastValue> value) {
		super(value, R.layout.list_palmares_fast_row);
		setFactoryViewHolder(PalmaresFastViewHolder::build);
	}
}