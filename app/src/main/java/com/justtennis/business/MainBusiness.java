package com.justtennis.business;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.business.sub.ComputeRankSubService;
import com.justtennis.db.DBDictionary;
import com.justtennis.db.service.PlayerService;
import com.justtennis.db.service.RankingService;
import com.justtennis.db.service.UserService;
import com.justtennis.db.sqlite.helper.GenericJustTennisDBHelper;
import com.justtennis.domain.ComputeDataRanking;
import com.justtennis.domain.Ranking;
import com.justtennis.domain.User;

public class MainBusiness {

	@SuppressWarnings("unused")
	private static final String TAG = MainBusiness.class.getSimpleName();

	private UserService userService;
	private PlayerService playerService;
	private ComputeRankSubService computeRankService;
	private RankingService rankingService;
	private DBDictionary dBDictionary;

	private Context context;

	private Ranking rankingNC;
	private User user;

	public MainBusiness(Context context, INotifierMessage notificationMessage) {
		this.context = context;
		userService = new UserService(context, notificationMessage);
		playerService = new PlayerService(context, notificationMessage);
		rankingService = new RankingService(context, notificationMessage);
		computeRankService = new ComputeRankSubService(context, notificationMessage);
		dBDictionary = DBDictionary.getInstance(context, notificationMessage);
	}

	public void onResume() {
		initializeData();
	}

	public User getUser() {
		return user;
	}

	public Ranking getRankingNC() {
		return rankingNC;
	}

	public ComputeDataRanking getDataRanking() {
		Long idRanking = null;
		if (user != null) {
			idRanking = user.getIdRankingEstimate();
			if (idRanking == null) {
				idRanking = user.getIdRanking();
			}
		}
		if (idRanking == null) {
			idRanking = rankingService.getNC().getId();
		}
		return computeRankService.computeDataRanking(idRanking, true);
	}

	public Long getUnknownPlayerId() {
		return playerService.getUnknownPlayer().getId();
	}

	public void initializeData() {
		rankingNC = rankingService.getNC();
		user = userService.find();
	}

	public GenericJustTennisDBHelper getDBHelper(String databaseName) {
		return dBDictionary.getDBHelperFromDatabaseName(databaseName);
	}

	public Context getContext() {
		return context;
	}
}