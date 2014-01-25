package com.justtennis.business;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.service.RankingService;
import com.justtennis.db.service.UserService;
import com.justtennis.domain.Ranking;
import com.justtennis.domain.User;
import com.justtennis.domain.comparator.RankingComparatorByOrder;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.parser.UserParser;

public class UserBusiness {

	private UserService userService;
	private UserParser userParser;
	private Context context;
	private String[] listTxtRankings;
	private List<Ranking> listRanking;

	public UserBusiness(Context context, INotifierMessage notificationMessage) {
		this.context = context;
		userService = new UserService(context, notificationMessage);
		userParser = UserParser.getInstance();
	}

	public void initializeData() {
		SortedSet<Ranking> setRanking = new TreeSet<Ranking>(new RankingComparatorByOrder());

		listRanking = new RankingService(context, NotifierMessageLogger.getInstance()).getList();
		setRanking.addAll(listRanking);
		
		listRanking.clear();
		listRanking.addAll(setRanking);

		int i=0;
		listTxtRankings = new String[setRanking.size()];
		for(Ranking ranking : setRanking) {
			listTxtRankings[i++] = ranking.getRanking();
		}
	}

	public long getUserCount() {
		return userService.getCount();
	}

    /**
     * 
     * @param user
     */
	public void submit(User user) {
		userService.createOrUpdate(user);
	}

	public User findUser() {
		return userService.find();
	}
	
	public String toQRCode(User user) {
		return userParser.toDataText(user);
	}

	public String[] getListTxtRankings() {
		return listTxtRankings;
	}

	public List<Ranking> getListRanking() {
		return listRanking;
	}
}