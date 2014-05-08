package com.justtennis.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import com.justtennis.activity.ListPlayerActivity;
import com.justtennis.business.ListPlayerBusiness;
import com.justtennis.db.service.ClubService;
import com.justtennis.db.service.RankingService;
import com.justtennis.db.service.TournamentService;
import com.justtennis.domain.Club;
import com.justtennis.domain.Player;
import com.justtennis.domain.Tournament;
import com.justtennis.domain.Player.PLAYER_TYPE;
import com.justtennis.domain.Ranking;
import com.justtennis.filter.ListPlayerByTypeFilter;
import com.justtennis.manager.ContactManager;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.parser.LocationParser;

public class ListPlayerAdapter extends ArrayAdapter<Player> {

	private List<Player> value;
	private ListPlayerActivity activity;
	private Filter filter = null;
	private ArrayList<Player> valueOld;
	private HashMap<Long, Ranking> rankingService;
	private LocationParser locationParser;

	public ListPlayerAdapter(ListPlayerActivity activity, List<Player> value) {
		super(activity, R.layout.list_player_row, android.R.id.text1, value);

		this.activity = activity;
		this.value = value;
		this.valueOld = new ArrayList<Player>(value);

		this.filter = new ListPlayerByTypeFilter(new ListPlayerByTypeFilter.IValueNotifier() {
			@Override
			public void setValue(List<Player> value) {
				ListPlayerAdapter.this.value.clear();
				ListPlayerAdapter.this.value.addAll(value);
				notifyDataSetChanged();
			}
		}, valueOld);
		NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
		rankingService = new RankingService(activity, notifier).getMapById();
		locationParser = LocationParser.getInstance(activity, notifier);
	}

	@Override
	public Filter getFilter() {
		if (filter!=null) {
			return filter;
		} else {
			return super.getFilter();
		}
	}


	public static class ViewHolder {
		public Player player;
		public TextView name;
		public TextView clubName;
		public ImageView imageSend;
		public ImageView imagePlayer;
		public ImageView imageDelete;
		public ImageView imageQRCode;
		public ImageView imagePlay;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Player v = value.get(position);
		ListPlayerBusiness business = activity.getBusiness();
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.list_player_row, null);
		}
		rowView.setTag(v.getId());

		int iVisibility = (business.isUnknownPlayer(v) ? View.GONE : View.VISIBLE);

		ImageView imagePlayer = (ImageView) rowView.findViewById(R.id.iv_player);
//		ImageView imageSend = (ImageView) rowView.findViewById(R.id.iv_send);
		ImageView imageDelete = (ImageView) rowView.findViewById(R.id.iv_delete);
		TextView ranking = (TextView) rowView.findViewById(R.id.tv_ranking);
		TextView name = (TextView) rowView.findViewById(R.id.tv_name);
		TextView clubName = (TextView) rowView.findViewById(R.id.tv_club_name);

		imageDelete.setVisibility(iVisibility);

		Ranking r = rankingService.get(v.getIdRanking()); 
		imagePlayer.setTag(v);
//		imageSend.setTag(v);
		imageDelete.setTag(v);
		ranking.setText(r == null ? "" : r.getRanking());
		name.setText(Html.fromHtml("<b>" + v.getFirstName() + "</b> " + v.getLastName()));

		initializeLocation(v, clubName);

	//		if (v.getPhoto()!=null) {
//			imagePlayer.setImageBitmap(v.getPhoto());
//		}
		if (v.getIdGoogle()!=null && v.getIdGoogle().longValue()>0l) {
			imagePlayer.setImageBitmap(ContactManager.getInstance().getPhoto(activity, v.getIdGoogle()));
		}
		else {
			imagePlayer.setImageResource(R.drawable.player_unknow_2);
		}

		if (ApplicationConfig.SHOW_ID) {
			name.setText(name.getText() + " [" + v.getId() + "|" + v.getIdExternal() + "]");
		}

//		switch (activity.getMode()) {
//			case EDIT:
//				if (v.getIdExternal()!=null && v.getIdExternal()>0) {
//					imageSend.setVisibility(View.GONE);
//				}
//				else {
//					imageSend.setVisibility(iVisibility);
//				}
//				break;
//			case INVITE:
//				imageSend.setVisibility(View.GONE);
//				imageDelete.setVisibility(View.GONE);
//				break;
//		}
	    return rowView;
	}

	public List<Player> getValue() {
		return value;
	}

	public void setValue(List<Player> value) {
		this.value = value;

		valueOld.clear();
		valueOld.addAll(this.value);
	}

	private void initializeLocation(final Player v, TextView clubName) {
		String[] address = locationParser.toAddress(v);
		if (address != null) {
			clubName.setText(address[0]);
			clubName.setVisibility(View.VISIBLE);
		} else {
			clubName.setVisibility(View.GONE);
		}
	}
}