package com.justtennis.adapter;

import android.app.Activity;
import android.os.Handler;
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
import com.justtennis.db.service.PlayerService;
import com.justtennis.db.service.RankingService;
import com.justtennis.domain.Player;
import com.justtennis.domain.Ranking;
import com.justtennis.filter.ListPlayerByTypeFilter;
import com.justtennis.manager.ContactManager;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.parser.LocationParser;

import org.gdocument.gtracergps.launcher.log.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ListPlayerAdapter extends ArrayAdapter<Player> {

	private static final String TAG = ListPlayerAdapter.class.getSimpleName();

	private Date dateStart = new Date();
	private Runnable runnableNotifyDataSetChanged = null;
	private Handler handler = new Handler();

	private List<Player> value;
	private Activity activity;
	private Filter filter = null;
	private ArrayList<Player> valueOld;
	private ArrayList<Integer> valuePosition = new ArrayList<Integer>();
	private HashMap<Long, Ranking> rankingService;
	private LocationParser locationParser;
	private RankingViewManager rankingViewManager;

	public ListPlayerAdapter(Activity activity, List<Player> value) {
		super(activity, R.layout.list_player_row, android.R.id.text1, value);
		logMe("Constructor BEGIN");

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
		rankingViewManager = RankingViewManager.getInstance(activity, notifier);
		logMe("Constructor END");
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
		View rowView = convertView;
		if (rowView == null || !this.valuePosition.contains(position)) {
			logMe("getView BEGIN", position);
			this.valuePosition.add(position);
	
			final Player v = value.get(position);
//			if (rowView == null) {
				LayoutInflater inflater = activity.getLayoutInflater();
				rowView = inflater.inflate(R.layout.list_player_row, null);
//			}
			rowView.setTag(v.getId());
	
			int iVisibility = (isUnknownPlayer(v) ? View.GONE : View.VISIBLE);
	
			ImageView imagePlayer = (ImageView) rowView.findViewById(R.id.iv_player);
	//		ImageView imageSend = (ImageView) rowView.findViewById(R.id.iv_send);
			ImageView imageDelete = (ImageView) rowView.findViewById(R.id.iv_delete);
			TextView name = (TextView) rowView.findViewById(R.id.tv_player);
			TextView clubName = (TextView) rowView.findViewById(R.id.tv_club_name);
	
			imageDelete.setVisibility(iVisibility);
	
			Ranking r = rankingService.get(v.getIdRanking()); 
			imagePlayer.setTag(v);
	//		imageSend.setTag(v);
			imageDelete.setTag(v);
			name.setText(Html.fromHtml("<b>" + v.getFirstName() + "</b> " + v.getLastName()));
	
			rankingViewManager.manageRanking(rowView, v, true);
	
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
				name.setText(name.getText() + " [id:" + v.getId() + "|idExt:" + v.getIdExternal() + "]");
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
			logMe("getView END", position);
		}
	    return rowView;
	}

	@Override
	public void notifyDataSetChanged() {
		logMe("notifyDataSetChanged BEGIN");
		if (runnableNotifyDataSetChanged != null) {
			handler.removeCallbacks(runnableNotifyDataSetChanged);
		}
		runnableNotifyDataSetChanged = new Runnable() {

			@Override
			public void run() {
				logMe("notifyDataSetChanged CALL");
				ListPlayerAdapter.this.notifyDataSetChanged();
				handler.removeCallbacks(runnableNotifyDataSetChanged);
				runnableNotifyDataSetChanged = null;
			}
		};
		handler.postDelayed(runnableNotifyDataSetChanged, 1000);
		logMe("notifyDataSetChanged END");
	}
	public List<Player> getValue() {
		return value;
	}

	public void setValue(List<Player> value) {
		logMe("setValue BEGIN");
		this.value = value;

		valueOld.clear();
		valueOld.addAll(this.value);

		this.valuePosition.clear();
		logMe("setValue END");
	}

	private boolean isUnknownPlayer(Player player) {
		return PlayerService.isUnknownPlayer(player);
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

	protected void logMe(String msg, int position) {
		if (position == 0) {
			logMe(msg + " position:" + position);
		}
    }

	protected void logMe(String msg) {
		String text = "ListPlayerAdapter time:" + (new Date().getTime() - dateStart.getTime()) + " millisecond - " + msg;
		Logger.logMe(TAG, text);
		com.crashlytics.android.Crashlytics.log(text);
    }
}