package com.justtennis.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.justtennis.ApplicationConfig;
import com.justtennis.R;
import com.justtennis.adapter.manager.RankingViewManager;
import com.justtennis.db.service.RankingService;
import com.justtennis.db.service.ScoreSetService;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Ranking;
import com.justtennis.filter.ListInviteByPlayerFilter;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.parser.LocationParser;

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
	private HashMap<Long, Ranking> mapRanking;
	private ScoreSetService scoreSetService;
	private LocationParser locationParser;
	private RankingViewManager rankingViewManager;

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
		NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
		mapRanking = new RankingService(activity, notifier).getMapById();
		locationParser = LocationParser.getInstance(activity, notifier);
		scoreSetService = new ScoreSetService(activity, notifier);
		rankingViewManager = RankingViewManager.getInstance(activity, notifier);
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
		TextView tvClubName = (TextView) rowView.findViewById(R.id.tv_club_name);
		ImageView imageDelete = (ImageView) rowView.findViewById(R.id.iv_delete);
		View vTypeEntrainement = rowView.findViewById(R.id.tv_type_entrainement);
		View vTypeMatch = rowView.findViewById(R.id.tv_type_match);

		tvPlayer.setText(v.getPlayer()==null ? "" : Html.fromHtml("<b>" + v.getPlayer().getFirstName() + "</b> " + v.getPlayer().getLastName()));
		tvDate.setText(v.getDate()==null ? "" : sdf.format(v.getDate()));
		imageDelete.setTag(v);

		rankingViewManager.manageRanking(rowView, v, true);

		if (ApplicationConfig.SHOW_ID) {
			tvPlayer.setText(tvPlayer.getText() + " [id:" + v.getPlayer().getId() + "|idExt:" + v.getPlayer().getIdExternal() + "]");
			tvDate.setText(tvDate.getText() + " [id:" + v.getId() + "|idExt:" + v.getIdExternal() + "]");
		}

		initializeLocation(v, tvClubName);

		String textScore = scoreSetService.buildTextScore(v);
		if (textScore != null) {
			tvScore.setVisibility(View.VISIBLE);
			tvScore.setText(Html.fromHtml(textScore));
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
			case COMPETITION:
				vTypeEntrainement.setVisibility(View.GONE);
				vTypeMatch.setVisibility(View.VISIBLE);
				break;
			case TRAINING:
			default:
				vTypeEntrainement.setVisibility(View.VISIBLE);
				vTypeMatch.setVisibility(View.GONE);
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

	private void initializeLocation(final Invite v, TextView clubName) {
		String[] address = locationParser.toAddress(v);
		if (address != null) {
			clubName.setText(address[0]);
			clubName.setVisibility(View.VISIBLE);
		} else {
			clubName.setVisibility(View.GONE);
		}
	}
}