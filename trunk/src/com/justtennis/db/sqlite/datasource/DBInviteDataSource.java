package com.justtennis.db.sqlite.datasource;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.helper.DBInviteHelper;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Invite.INVITE_TYPE;
import com.justtennis.domain.Invite.STATUS;
import com.justtennis.domain.Player;
import com.justtennis.tool.DbTool;

public class DBInviteDataSource extends GenericDBDataSource<Invite> {

	private static final String TAG = DBInviteDataSource.class.getCanonicalName();

	// Database fields
	private String[] allColumns = {
		DBInviteHelper.COLUMN_ID,
		DBInviteHelper.COLUMN_ID_PLAYER,
		DBInviteHelper.COLUMN_TIME,
		DBInviteHelper.COLUMN_STATUS,
		DBInviteHelper.COLUMN_TYPE,
		DBInviteHelper.COLUMN_ID_EXTERNAL,
		DBInviteHelper.COLUMN_ID_CALENDAR,
		DBInviteHelper.COLUMN_ID_RANKING
	};

	public DBInviteDataSource(Context context, INotifierMessage notificationMessage) {
		super(new DBInviteHelper(context, notificationMessage), notificationMessage);
	}

	/**
	 * Return all Invite for a Player
	 * @param idPlayer Player id
	 * @return Invite list
	 */
	public List<Invite> getByIdPlayer(long idPlayer) {
		String sqlWhere = DBInviteHelper.COLUMN_ID_PLAYER + " = " + idPlayer;
		return query(sqlWhere);
	}

	@Override
	protected String[] getAllColumns() {
		return allColumns;
	}

	@Override
	protected void putContentValue(ContentValues values, Invite invite) {
		values.put(DBInviteHelper.COLUMN_ID_PLAYER, invite.getPlayer().getId());
		values.put(DBInviteHelper.COLUMN_TIME, invite.getDate().getTime());
		values.put(DBInviteHelper.COLUMN_STATUS, invite.getStatus().toString());
		values.put(DBInviteHelper.COLUMN_TYPE, invite.getType().toString());
		values.put(DBInviteHelper.COLUMN_ID_EXTERNAL, invite.getIdExternal());
		values.put(DBInviteHelper.COLUMN_ID_CALENDAR, invite.getIdCalendar());
		values.put(DBInviteHelper.COLUMN_ID_RANKING, invite.getIdRanking());
	}

	@Override
	protected Invite cursorToPojo(Cursor cursor) {
		int col = 0;
		Invite invite = new Invite();
		invite.setId(DbTool.getInstance().toLong(cursor, col++));
		Player player = new Player();
		player.setId(DbTool.getInstance().toLong(cursor, col++));
		invite.setPlayer(player);
		String date = cursor.getString(col++);
		invite.setDate(date==null || "null".equals(date.toLowerCase(Locale.FRANCE)) ? null : new Date(Long.parseLong(date)));
		invite.setStatus(STATUS.valueOf(cursor.getString(col++)));
		String type = cursor.getString(col++);
		invite.setType(type==null ? INVITE_TYPE.ENTRAINEMENT : INVITE_TYPE.valueOf(type));
		invite.setIdExternal(DbTool.getInstance().toLong(cursor, col++));
		invite.setIdCalendar(DbTool.getInstance().toLong(cursor, col++));
		invite.setIdRanking(DbTool.getInstance().toLong(cursor, col++));
		return invite;
	}
	
	@Override
	protected String getTag() {
		return TAG;
	}
}