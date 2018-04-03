package com.justtennis.parser;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.test.AndroidTestCase;

import com.cameleon.common.tool.CryptoTool;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Player;
import com.justtennis.domain.User;
import com.justtennis.parser.SmsParser.MSG_TYPE;

public class SmsParserTest extends AndroidTestCase {

	private SmsParser parser;
	private SimpleDateFormat sdf;

	protected void setUp() throws Exception {
		parser = SmsParser.getInstance(getContext());
		sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	}

	protected void tearDown() throws Exception {
	}

	public void testGetMessageType() {
		String separator = SmsParser.getDataSeparator();
		SmsParser.MSG_TYPE type = SmsParser.MSG_TYPE.PLAY_INVITE;
		String message = SmsParser.getInstance(getContext()).prepareMessage(type.code + separator + 1 + separator + "TEST");
		boolean check = type == parser.getMessageType(message);
		assertTrue(check);
	}

	public void testToMessageAdd() {
		User user = buildUser();
		Player player = buildPlayer();
		Invite invite = new Invite(user, player);
		
		String message = parser.toMessageAdd(invite);

		assertToMessage(MSG_TYPE.PLAYER_ADD, message);
	}

	public void testToMessagePlay() {
		User user = buildUser();
		Player player = buildPlayer();
		Date date = new Date(0);
		Invite invite = new Invite(user, player, date);

		String message = parser.toMessageInvite(invite);

		assertToMessage(MSG_TYPE.PLAY_INVITE, message);
	}

	public void testFromMessageAdd() {
		User user = buildUser();
		Player player = buildPlayer();
		Invite invite = new Invite(user, player);
		
		String message1 = parser.toMessageAdd(invite);
		String message = message1;
		invite = parser.fromMessageAdd(message);

		assertNotNull(invite);
		assertNotNull(invite.getUser());
		assertNotNull(invite.getPlayer());
		assertNull(invite.getDate());

		User userAssert = invite.getUser();
		Player playerAssert = invite.getPlayer();
		
		assertUser(user, userAssert);
		assertPlayer(player, playerAssert);
	}

	public void testFromMessagePlay() {
		User user = buildUser();
		Player player = buildPlayer();
		Date date = new Date(0);
		Invite invite = new Invite(user, player, date);

		String message = parser.toMessageInvite(invite);
		invite = parser.fromMessageInvite(message);

		assertNotNull(invite);
		assertNotNull(invite.getUser());
		assertNotNull(invite.getPlayer());
		assertEquals(sdf.format(date), sdf.format(invite.getDate()));

		User userAssert = invite.getUser();
		Player playerAssert = invite.getPlayer();
		
		assertUser(user, userAssert);
		assertPlayer(player, playerAssert);
	}

	private void assertUser(User user, User userAssert) {
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

	private void assertPlayer(Player player, Player playerAssert) {
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

	public void testFromMessageDemandeAddYes() {
		User user = buildUser();
		Player player = buildPlayer();
		Invite invite = new Invite(user, player);

		String message = parser.toMessageDemandeAddYes(invite);
		invite = parser.fromMessageDemandeAddYes(message);

		assertNotNull(invite);
		assertNotNull(invite.getUser());
		assertNotNull(invite.getPlayer());

		User userAssert = invite.getUser();
		Player playerAssert = invite.getPlayer();
		
		assertUser(user, userAssert);
		assertPlayer(player, playerAssert);
	}

	public void testFromMessageDemandeAddNo() {
		User user = buildUser();
		Player player = buildPlayer();
		Invite invite = new Invite(user, player);

		String message = parser.toMessageDemandeAddNo(invite);
		invite = parser.fromMessageDemandeAddNo(message);

		assertNotNull(invite);
		assertNotNull(invite.getUser());
		assertNotNull(invite.getPlayer());

		User userAssert = invite.getUser();
		Player playerAssert = invite.getPlayer();
		
		assertUser(user, userAssert);
		assertPlayer(player, playerAssert);
	}

	public void testFormatDate() {
		Date date = new Date(0);

		String format = parser.formatDate(date);
		
		assertEquals(sdf.format(date), format);
	}

	private void assertToMessage(MSG_TYPE type, String message) {
		String[] splitedMessage = message.split("\\"+SmsParser.getDataSeparator(), 2);

		assertEquals(2, splitedMessage.length);
		assertEquals(SmsParser.getMessageStart(), splitedMessage[0]);
		
		splitedMessage[1] = CryptoTool.getInstance().decrypte(splitedMessage[1]);

		splitedMessage = splitedMessage[1].split("\\"+SmsParser.getDataSeparator(), 2);
		
		assertEquals(2, splitedMessage.length);
		assertEquals(type.code, splitedMessage[0]);
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
