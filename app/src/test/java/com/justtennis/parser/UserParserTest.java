package com.justtennis.parser;

import junit.framework.TestCase;

import com.justtennis.domain.Player;
import com.justtennis.domain.User;
import com.justtennis.tool.ParserTool;

public class UserParserTest extends TestCase {

	private UserParser parser;
	private String dataSeparator;

	protected void setUp() throws Exception {
		parser = UserParser.getInstance();
		dataSeparator = UserParser.getDataSeparator();
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetInstance() {
		assertEquals(parser, UserParser.getInstance());
	}

	public void testToDataText() {
		User user = buildUser();

		String text = parser.toDataText(user);

		int i=0;
		String[] split = text.split(dataSeparator);

		assertEquals(user.getId().toString(), split[i++]);
		assertEquals(user.getFirstName(), split[i++]);
		assertEquals(user.getLastName(), split[i++]);
		assertEquals(user.getBirthday(), split[i++]);
		assertEquals(user.getPhonenumber(), split[i++]);
		assertEquals(user.getIdClub(), ParserTool.getInstance().parseLong(split[i++]));
		assertEquals(user.getIdRanking(), ParserTool.getInstance().parseLong(split[i++]));
		assertEquals(user.getLocality(), split[i++]);
		assertEquals(user.getPostalCode(), split[i++]);
		assertEquals(user.getAddress(), split[i++]);
		assertEquals(user.getIdExternal(), ParserTool.getInstance().parseLong(split[i++]));
	}

	public void testFromData() {
		User user = buildUser();
		String text = 
			user.getId() + dataSeparator + 
			user.getFirstName() + dataSeparator + 
			user.getLastName() + dataSeparator + 
			user.getBirthday() + dataSeparator + 
			user.getPhonenumber() + dataSeparator + 
			user.getIdClub() + dataSeparator + 
			user.getIdRanking() + dataSeparator + 
			user.getLocality() + dataSeparator + 
			user.getPostalCode() + dataSeparator + 
			user.getAddress() + dataSeparator + 
			user.getIdExternal();

		User userAssert = (User)parser.fromData(text);

		assertEquals(user.getId(), userAssert.getId());
		assertEquals(user.getFirstName(), userAssert.getFirstName());
		assertEquals(user.getLastName(), userAssert.getLastName());
		assertEquals(user.getBirthday(), userAssert.getBirthday());
		assertEquals(user.getPhonenumber(), userAssert.getPhonenumber());
		assertEquals(user.getIdClub(), userAssert.getIdClub());
		assertEquals(user.getIdRanking(), userAssert.getIdRanking());
		assertEquals(user.getLocality(), userAssert.getLocality());
		assertEquals(user.getPostalCode(), userAssert.getPostalCode());
		assertEquals(user.getAddress(), userAssert.getAddress());
		assertEquals(user.getIdExternal(), userAssert.getIdExternal());
	}

	public void testGetNbDataField() {
		assertEquals(9, UserParser.getNbDataField());
	}

	private User buildUser() {
		User user = new User();
		user.setAddress("adresse user");
		user.setBirthday("birthday user");
		user.setFirstName("firstname user");
		user.setId(Long.valueOf(1));
		user.setIdClub(Long.valueOf(2));
		user.setIdExternal(Long.valueOf(3));
		user.setIdRanking(Long.valueOf(4));
		user.setLastName("lastname user");
		user.setLocality("locality user");
		user.setPhonenumber("phonenumber user");
		user.setPostalCode("postalcode user");
		return user;
	}
}