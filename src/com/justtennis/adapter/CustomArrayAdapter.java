package com.justtennis.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.justtennis.R;
import com.justtennis.domain.GenericDBPojo;

public class CustomArrayAdapter<T> extends ArrayAdapter<T> {

	public CustomArrayAdapter(Context context, List<T> objects) {
		super(context, R.layout.spinner_item, objects);
		setDropDownViewResource(R.layout.spinner_dropdown_item);
	}

	public abstract class OnItemSelectedListener<I extends GenericDBPojo<?>> implements android.widget.AdapterView.OnItemSelectedListener {
		public abstract boolean isHintItemSelected(I item);
		public abstract void onItemSelected(AdapterView<?> parent, View view, int position, long id, I item);
		public abstract I getItem(int position);

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			int color = 0;
			I item = getItem(position);
			if (isHintItemSelected(item)) {
				color = R.color.spinner_color_hint;
			} else {
				color = android.R.color.black;
			}
			if (view != null) {
				TextView textView = (TextView)view.findViewById(android.R.id.text1);
				if (textView != null) {
					textView.setTextColor(getContext().getResources().getColor(color));
				}
			}
			onItemSelected(parent, view, position, id, item);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
	}
}
