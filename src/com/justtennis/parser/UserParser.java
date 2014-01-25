package com.justtennis.parser;

import com.justtennis.domain.Player;
import com.justtennis.domain.User;


public class UserParser extends PlayerParser {

	private static UserParser instance = null;

	protected UserParser() {
	}

	public static UserParser getInstance() {
		if (instance==null)
			instance = new UserParser();
		return (UserParser)instance;
	}

	@Override
	protected Player newObject() {
		return new User();
	}
}