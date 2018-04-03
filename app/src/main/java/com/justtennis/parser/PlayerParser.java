package com.justtennis.parser;

import com.justtennis.domain.Person;
import com.justtennis.domain.Player;
import com.justtennis.domain.User;

public class PlayerParser extends GenericParser {
	
	private static final String DATA_SEPARATOR = "!";
	private static PlayerParser instance = null;
	private static final int NB_DATA_FIELD = 12;
	private static final int VERSION = 1;
	
	protected PlayerParser() {
	}

	public static PlayerParser getInstance() {
		if (instance==null)
			instance = new PlayerParser();
		return instance;
	}

	public String toDataText(Player player) {
		return VERSION + DATA_SEPARATOR +
				player.getId() + DATA_SEPARATOR + 
				player.getFirstName() + DATA_SEPARATOR + 
				player.getLastName() + DATA_SEPARATOR + 
				player.getBirthday() + DATA_SEPARATOR + 
				player.getPhonenumber() + DATA_SEPARATOR + 
				player.getIdClub() + DATA_SEPARATOR + 
				player.getIdRanking() + DATA_SEPARATOR + 
				player.getLocality() + DATA_SEPARATOR + 
				player.getPostalCode() + DATA_SEPARATOR + 
				player.getAddress() + DATA_SEPARATOR + 
				player.getIdExternal();
	}
	
	public Player fromData(String text) {
		Player ret = newObject();
		int i=0;
		String[] split = text.split(DATA_SEPARATOR);
		int version = Integer.parseInt(split[i++]);
		switch (version) {
			case 1:
			default:
				ret.setId(parseLong(split[i++]));
				ret.setFirstName(parseString(split[i++]));
				ret.setLastName(parseString(split[i++]));
				ret.setBirthday(parseString(split[i++]));
				ret.setPhonenumber(parseString(split[i++]));
				ret.setIdClub(parseLong(split[i++]));
				ret.setIdRanking(parseLong(split[i++]));
				ret.setLocality(parseString(split[i++]));
				ret.setPostalCode(parseString(split[i++]));
				ret.setAddress(parseString(split[i++]));
				ret.setIdExternal(parseLong(split[i++]));
		}
		return ret;
	}

	public final Player fromUser(User user) {
		Player ret = new Player();
		ret.setId(user.getId());
		ret.setFirstName(user.getFirstName());
		ret.setLastName(user.getLastName());
		ret.setBirthday(user.getBirthday());
		ret.setPhonenumber(user.getPhonenumber());
		ret.setIdClub(user.getIdClub());
		ret.setIdRanking(user.getIdRanking());
		ret.setLocality(user.getLocality());
		ret.setPostalCode(user.getPostalCode());
		ret.setAddress(user.getAddress());
		ret.setIdExternal(user.getIdExternal());
		ret.setType(user.getType());
		return ret;
	}

	public final Player fromPersonForCreate(Person person) {
		Player player = fromPerson(person);
		player.setId(null);
		return player;
	}
		
	public final Player fromPerson(Person person) {
		Player ret = new Player();
		ret.setId(person.getId());
		ret.setFirstName(person.getFirstName());
		ret.setLastName(person.getLastName());
		ret.setBirthday(person.getBirthday());
		ret.setPhonenumber(person.getPhonenumber());
		ret.setLocality(person.getLocality());
		ret.setPostalCode(person.getPostalCode());
		ret.setAddress(person.getAddress());
		ret.setIdGoogle(person.getId());
		return ret;
	}

	public static String getDataSeparator() {
		return DATA_SEPARATOR;
	}

	public static int getNbDataField() {
		return NB_DATA_FIELD;
	}

	protected Player newObject() {
		return new Player();
	}
}