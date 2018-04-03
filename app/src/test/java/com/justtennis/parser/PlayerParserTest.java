package com.justtennis.parser;

import com.justtennis.domain.Player;
import com.justtennis.tool.ParserTool;

import junit.framework.TestCase;

public class PlayerParserTest extends TestCase {

	private PlayerParser parser;
	private String dataSeparator;

	protected void setUp() throws Exception {
		parser = PlayerParser.getInstance();
		dataSeparator = PlayerParser.getDataSeparator();
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetInstance() {
		assertEquals(parser, PlayerParser.getInstance());
	}

	public void testToDataText() {
		Player player = buildPlayer();

		String text = parser.toDataText(player);

		int i=0;
		String[] split = text.split(dataSeparator);

		assertEquals(player.getId().toString(), split[i++]);
		assertEquals(player.getFirstName(), split[i++]);
		assertEquals(player.getLastName(), split[i++]);
		assertEquals(player.getBirthday(), split[i++]);
		assertEquals(player.getPhonenumber(), split[i++]);
		assertEquals(player.getIdClub(), ParserTool.getInstance().parseLong(split[i++]));
		assertEquals(player.getIdRanking(), ParserTool.getInstance().parseLong(split[i++]));
		assertEquals(player.getLocality(), split[i++]);
		assertEquals(player.getPostalCode(), split[i++]);
		assertEquals(player.getAddress(), split[i++]);
		assertEquals(player.getIdExternal(), ParserTool.getInstance().parseLong(split[i++]));
	}

	public void testFromData() {
		Player player = buildPlayer();
		String text = 
			player.getId() + dataSeparator + 
			player.getFirstName() + dataSeparator + 
			player.getLastName() + dataSeparator + 
			player.getBirthday() + dataSeparator + 
			player.getPhonenumber() + dataSeparator + 
			player.getIdClub() + dataSeparator + 
			player.getIdRanking() + dataSeparator + 
			player.getLocality() + dataSeparator + 
			player.getPostalCode() + dataSeparator + 
			player.getAddress() + dataSeparator + 
			player.getIdExternal();

		Player playerAssert = parser.fromData(text);

		assertEquals(player.getId(), playerAssert.getId());
		assertEquals(player.getFirstName(), playerAssert.getFirstName());
		assertEquals(player.getLastName(), playerAssert.getLastName());
		assertEquals(player.getBirthday(), playerAssert.getBirthday());
		assertEquals(player.getPhonenumber(), playerAssert.getPhonenumber());
		assertEquals(player.getIdClub(), playerAssert.getIdClub());
		assertEquals(player.getIdRanking(), playerAssert.getIdRanking());
		assertEquals(player.getLocality(), playerAssert.getLocality());
		assertEquals(player.getPostalCode(), playerAssert.getPostalCode());
		assertEquals(player.getAddress(), playerAssert.getAddress());
		assertEquals(player.getIdExternal(), playerAssert.getIdExternal());
	}

	public void testGetNbDataField() {
		assertEquals(12, PlayerParser.getNbDataField());
	}

	private Player buildPlayer() {
		Player player = new Player();
		player.setAddress("adresse1 player");
		player.setBirthday("birthday player");
		player.setFirstName("firstname player");
		player.setId(Long.valueOf(5));
		player.setIdClub(Long.valueOf(6));
		player.setIdExternal(Long.valueOf(7));
		player.setIdRanking(Long.valueOf(8));
		player.setLastName("lastname player");
		player.setLocality("locality player");
		player.setPhonenumber("phonenumber player");
		player.setPostalCode("postalcode player");
		return player;
	}
}
