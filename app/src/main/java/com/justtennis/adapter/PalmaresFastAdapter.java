package com.justtennis.adapter;

import java.util.List;

import org.gdocument.gtracergps.launcher.log.Logger;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.justtennis.R;
import com.justtennis.activity.PalmaresFastActivity;
import com.justtennis.domain.Invite;
import com.justtennis.domain.PalmaresFastValue;

public class PalmaresFastAdapter extends BaseAdapter {

	private static final String TAG = PalmaresFastAdapter.class.getSimpleName();

	private List<PalmaresFastValue> value;
	private PalmaresFastActivity activity;

	public PalmaresFastAdapter(PalmaresFastActivity activity, List<PalmaresFastValue> value) {
		super();

		this.activity = activity;
		this.value = value;
	}

	public static class ViewHolder {
		public Invite invite;
		public TextView tvPlayer;
		public TextView tvDate;
		public ImageView ivStatus;
		public ImageView imageDelete;
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
		return 0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PalmaresFastValue v = value.get(position);
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.list_palmares_fast_row, null);
		}
		rowView.setTag(v);

		TextView tvRanking = (TextView) rowView.findViewById(R.id.tv_ranking);
		EditText etNbVictory = (EditText) rowView.findViewById(R.id.et_nb_victory);
		EditText etNbDefeat = (EditText) rowView.findViewById(R.id.et_nb_defeat);

		etNbVictory.setTag(position);
		etNbDefeat.setTag(position);

		tvRanking.setText(v.getRanking().getRanking());
		etNbVictory.setText(Integer.toString(v.getNbVictory()));
		etNbDefeat.setText(Integer.toString(v.getNbDefeat()));
		Logger.logMe(TAG, "PALMARES FAST - PalmaresFastAdapter - onFocusChange setNbVictory data:" + v + " ranking:" + v.getRanking().getRanking() + " nbVictory:" + v.getNbVictory() + " nbDefeat:" + v.getNbDefeat());

		etNbVictory.addTextChangedListener(new EditTextWatcher(etNbVictory, true));
		etNbDefeat.addTextChangedListener(new EditTextWatcher(etNbDefeat, false));

		return rowView;
	}

	private class EditTextWatcher implements TextWatcher {

		private View view;
		private boolean victory;

		public EditTextWatcher(View view, boolean victory) {
			this.view = view;
			this.victory = victory;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (value != null && value.size() > 0 && view.getTag() != null && view.hasFocus()) {
				String txt = ((EditText)view).getText().toString();
				PalmaresFastValue v = value.get((Integer) view.getTag());
				int val = 0;
				try {
					val = Integer.parseInt(txt);
				} catch (Exception ex) {
					Logger.logMe(TAG, ex);
				}
				if (victory) {
					if (v.getNbVictory() != val) {
						Logger.logMe(TAG, "PALMARES FAST - PalmaresFastAdapter - onTextChange setNbVictory s.toString:" + s.toString() + " data:" + v + " ranking:" + v.getRanking().getRanking() + " val:" + val);
						v.setNbVictory(val);
					}
				} else {
					if (v.getNbDefeat() != val) {
						Logger.logMe(TAG, "PALMARES FAST - PalmaresFastAdapter - onTextChange setNbDefeat s.toString:" + s.toString() + " data:" + v + " ranking:" + v.getRanking().getRanking() + " val:" + val);
						v.setNbDefeat(val);
					}
				}
			}
		}
	}
}