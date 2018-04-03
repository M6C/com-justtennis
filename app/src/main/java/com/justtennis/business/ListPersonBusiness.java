package com.justtennis.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gdocument.gtracergps.launcher.log.Logger;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.activity.ListPersonActivity;
import com.justtennis.db.service.PlayerService;
import com.justtennis.db.service.UserService;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Person;
import com.justtennis.domain.Player;
import com.justtennis.domain.User;
import com.justtennis.manager.ContactManager;
import com.justtennis.manager.SmsManager;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.parser.SmsParser;

public class ListPersonBusiness {

	private static final String TAG = ListPersonBusiness.class.getSimpleName();

	private PlayerService playerService;
	private UserService userService;
	private ContactManager contactManager;
	private List<Person> list = new ArrayList<Person>();
	private ListPersonActivity context;
	private User user;

	public ListPersonBusiness(ListPersonActivity listPersonActivity, INotifierMessage notificationMessage) {
		this.context = listPersonActivity;
//		contactManager = ContactManager.getInstance();
		contactManager = ContactManager.getInstance(context);
		playerService = new PlayerService(context, notificationMessage);
		userService = new UserService(listPersonActivity, NotifierMessageLogger.getInstance());
		user = userService.find();
	}

	public void initialize() {
		refreshData();
	}

	public List<Person> getList() {
		return list;
	}
	
	public Context getContext() {
		return context;
	}

	public void delete(Player player) {
		Logger.logMe(TAG, "Delete Button !!!");

		refreshData();
		context.refresh();
	}

	private void refreshData() {
		list.clear();
//		Person separator = null;
//
//		separator = new Person();
//		separator.setId(0l);
//		separator.setFirstName("[ ** Players ** ]");
//		list.add(separator);
//		list.addAll(playerService.getList());
//
//		separator = new Person();
//		separator.setId(0l);
//		separator.setFirstName("[ ** Contacts ** ]");
//		list.add(separator);
		list.addAll(contactManager.getListContact());
	}

	public void send(Player player) {
		Invite invite = new Invite(user, player, new Date());
		String message = SmsParser.getInstance(context).toMessageAdd(invite);
		SmsManager.getInstance().send(context, player.getPhonenumber(), message);
	}
}