package com.justtennis.business;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.service.MessageService;
import com.justtennis.db.service.RankingService;
import com.justtennis.db.service.UserService;
import com.justtennis.domain.Message;
import com.justtennis.domain.Ranking;
import com.justtennis.domain.User;
import com.justtennis.domain.comparator.RankingComparatorByOrder;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.parser.LocationParser;
import com.justtennis.parser.UserParser;

public class UserBusiness {

	private UserService userService;
	private MessageService messageService;
	private UserParser userParser;
	private LocationParser locationParser;
	private Context context;
	private String[] listTxtRankings;
	private List<Ranking> listRanking;
	private Message message;

	public UserBusiness(Context context, INotifierMessage notificationMessage) {
		this.context = context;
		userService = new UserService(context, notificationMessage);
		messageService = new MessageService(context, notificationMessage);
		userParser = UserParser.getInstance();
		locationParser = LocationParser.getInstance(context, notificationMessage);
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

		message = messageService.getCommon();
		if (message==null) {
			message = new Message();
		}
	}

	public long getUserCount() {
		return userService.getCount();
	}

    /**
     * 
     * @param user
     */
	public void submit(User user, String message) {
		userService.createOrUpdate(user);

		this.message.setMessage(message);

		// Save in database
		messageService.createOrUpdate(this.message);
	}

	public User findUser() {
		return userService.find();
	}

	public String[] getLocationLine(User user) {
		return locationParser.toAddress(user);
	}
	
	public String getMessage() {
		return message.getMessage();
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