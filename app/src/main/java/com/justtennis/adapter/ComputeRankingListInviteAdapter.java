package com.justtennis.adapter;

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
import com.justtennis.adapter.manager.InviteViewManager;
import com.justtennis.adapter.manager.RankingListManager;
import com.justtennis.adapter.manager.RankingListManager.IRankingListListener;
import com.justtennis.adapter.manager.RankingViewManager;
import com.justtennis.db.service.PlayerService;
import com.justtennis.db.service.ScoreSetService;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Player;
import com.justtennis.domain.Ranking;
import com.justtennis.filter.ListInviteByPlayerFilter;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.parser.LocationParser;
import com.justtennis.ui.rxjava.RxComputeRanking;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ComputeRankingListInviteAdapter extends ArrayAdapter<Invite> {

	private List<Invite> value;
	private ArrayList<Invite> valueOld;
	private Activity activity;
	@SuppressLint("SimpleDateFormat")
	private final static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	private Filter filter = null;
	private ScoreSetService scoreSetService;
	private PlayerService playerService;
	private RankingViewManager rankingViewManager;
	private RankingListManager rankingListManager;
	private InviteViewManager inviteViewManager;
	private LocationParser locationParser;

	public ComputeRankingListInviteAdapter(Activity activity, List<Invite> value) {
		super(activity, R.layout.list_invite_row, android.R.id.text1, value);

		this.activity = activity;
		this.value = value;
		this.valueOld = new ArrayList<Invite>(value);
		
		this.filter = new ListInviteByPlayerFilter(value1 -> {
            ComputeRankingListInviteAdapter.this.value.clear();
            ComputeRankingListInviteAdapter.this.value.addAll(value1);
            notifyDataSetChanged();
        }, valueOld);
		NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
		locationParser = LocationParser.getInstance(activity, notifier);
		scoreSetService = new ScoreSetService(activity, notifier);
		playerService = new PlayerService(activity, NotifierMessageLogger.getInstance());
		rankingViewManager = RankingViewManager.getInstance(activity, notifier);
		rankingListManager = RankingListManager.getInstance(activity, notifier);
		inviteViewManager = InviteViewManager.getInstance(activity, notifier);
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
			rowView = inflater.inflate(R.layout.list_compute_ranking_invite_item, null);
		}
		rowView.setTag(v);

		ImageView ivStatus = rowView.findViewById(R.id.iv_status);
		TextView tvPlayer = (TextView) rowView.findViewById(R.id.tv_player);
		TextView tvDate = (TextView) rowView.findViewById(R.id.tv_date);
		TextView tvScore = (TextView) rowView.findViewById(R.id.tv_score);
		TextView tvClubName = (TextView) rowView.findViewById(R.id.tv_club_name);
		TextView tvPoint = (TextView) rowView.findViewById(R.id.tv_point);
		TextView tvBonusPoint = (TextView) rowView.findViewById(R.id.tv_bonus_point);

		inviteViewManager.manageStatusImage(ivStatus, v);

		initializeRanking(v, rowView, false);
		initializeRanking(v, rowView, true);

		tvPlayer.setText(v.getPlayer()==null ? "" : Html.fromHtml("<b>" + v.getPlayer().getFirstName() + "</b> " + v.getPlayer().getLastName()));
		tvDate.setText(v.getDate()==null ? "" : sdf.format(v.getDate()));
		tvPoint.setText(v.getPoint() > 0 ? ""+v.getPoint() : "");
		if (tvBonusPoint != null) {
			tvBonusPoint.setText(v.getBonusPoint() > 0 ? "" + v.getBonusPoint() : "");
		}

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

		switch(v.getScoreResult()) {
			case VICTORY:
				break;
			case DEFEAT:
				break;
			default:
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

	private void initializeRanking(Invite v, final View rowView, final boolean estimate) {
		rankingViewManager.manageRanking(rowView, v, estimate);
		IRankingListListener listenerRanking = new IRankingListListener() {

			@Override
			public void onRankingSelected(Ranking ranking) {
				Invite invite = (Invite) rowView.getTag();
				Player player = playerService.find(invite.getPlayer().getId());
				if (player != null) {
					if (estimate) {
						player.setIdRankingEstimate(ranking.getId());
					} else {
						player.setIdRanking(ranking.getId());
					}
					playerService.createOrUpdate(player);

					RxComputeRanking.publish(RxComputeRanking.SUBJECT_REFRESH);
				}
			}
		};
		rankingListManager.manageRankingTextViewDialog(activity, rowView, listenerRanking, estimate);
	}
}