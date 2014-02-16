package com.justtennis.adapter;

import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.justtennis.ApplicationConfig;
import com.justtennis.R;
import com.justtennis.domain.Invite;
import com.justtennis.domain.ScoreSet;
import com.justtennis.filter.ListInviteByPlayerFilter;

public class ListInviteAdapter extends ArrayAdapter<Invite> {

	public enum ADAPTER_INVITE_MODE {
		MODIFY, READ;
	}

	private List<Invite> value;
	private ArrayList<Invite> valueOld;
	private Activity activity;
	@SuppressLint("SimpleDateFormat")
	private final static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	private ADAPTER_INVITE_MODE mode;
	private Filter filter = null;

	public ListInviteAdapter(Activity activity, List<Invite> value) {
		this(activity, value, ADAPTER_INVITE_MODE.MODIFY);
	}

	public ListInviteAdapter(Activity activity, List<Invite> value, ADAPTER_INVITE_MODE mode) {
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

	public static class ViewHolder {
		public Invite invite;
		public TextView tvPlayer;
		public TextView tvDate;
		public ImageView ivStatus;
		public ImageView imageDelete;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Invite v = value.get(position);
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.list_invite_row, null);
		}
		rowView.setTag(v);

		TextView tvPlayer = (TextView) rowView.findViewById(R.id.tv_player);
		TextView tvDate = (TextView) rowView.findViewById(R.id.tv_date);
		TextView tvScore = (TextView) rowView.findViewById(R.id.tv_score);
		ImageView ivStatus = (ImageView) rowView.findViewById(R.id.iv_status);
		ImageView imageDelete = (ImageView) rowView.findViewById(R.id.iv_delete);
		View vTypeEntrainement = rowView.findViewById(R.id.tv_type_entrainement);
		View vTypeMatch = rowView.findViewById(R.id.tv_type_match);

		tvPlayer.setText(v.getPlayer()==null ? "" : v.getPlayer().getFirstName() + " " + v.getPlayer().getLastName());
		tvDate.setText(v.getDate()==null ? "" : sdf.format(v.getDate()));
		imageDelete.setTag(v);

		if (ApplicationConfig.SHOW_ID) {
			tvPlayer.setText(tvPlayer.getText() + " [" + v.getPlayer().getId() + "|" + v.getPlayer().getIdExternal() + "]");
			tvDate.setText(tvDate.getText() + " [" + v.getId() + "|" + v.getIdExternal() + "]");
		}

		String textScore = buildTextScore(v);
		if (textScore != null) {
			tvScore.setVisibility(View.VISIBLE);
			tvScore.setText(textScore);
		} else  {
			tvScore.setVisibility(View.GONE);
		}
//		int iRessource = R.drawable.check_yellow;
//		switch(v.getStatus()) {
//			case ACCEPT:
//				iRessource = R.drawable.check_green;
//				break;
//			case REFUSE:
//				iRessource = R.drawable.check_red;
//				break;
//			default:
//		}
//		ivStatus.setImageDrawable(activity.getResources().getDrawable(iRessource));
		switch(v.getScoreResult()) {
			case VICTORY:
				break;
			case DEFEAT:
				break;
			default:
		}
		
		switch(mode) {
			case READ:
				imageDelete.setVisibility(View.GONE);
				break;
			case MODIFY:
			default:
				imageDelete.setVisibility(View.VISIBLE);
				break;
		}

		switch(v.getType()) {
			case ENTRAINEMENT:
				vTypeEntrainement.setVisibility(View.VISIBLE);
				vTypeMatch.setVisibility(View.GONE);
				break;
			case MATCH:
			default:
				vTypeEntrainement.setVisibility(View.GONE);
				vTypeMatch.setVisibility(View.VISIBLE);
				break;
		}
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
	
	private String buildTextScore(Invite invite) {
		String ret = null;
		if (invite.getListScoreSet()!=null && invite.getListScoreSet().size() > 0) {
			for(ScoreSet score : invite.getListScoreSet()) {
				if (score.getValue1() > 0 || score.getValue2() > 0) {
					if (ret == null) {
						ret = score.getValue1() + "-" + score.getValue2();
					} else {
						ret += " / " + score.getValue1() + "-" + score.getValue2();
					}
				}
			}
		}
		return ret;
	}
}