package com.justtennis.db.sqlite.datasource;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.helper.DBInviteHelper;
import com.justtennis.domain.Address;
import com.justtennis.domain.Club;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Invite.SCORE_RESULT;
import com.justtennis.domain.Invite.STATUS;
import com.justtennis.domain.Player;
import com.justtennis.domain.Tournament;
import com.justtennis.manager.TypeManager;
import com.justtennis.tool.DbTool;

public class DBInviteDataSource extends GenericDBDataSourceByType<Invite> {

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
		DBInviteHelper.COLUMN_ID_RANKING,
		DBInviteHelper.COLUMN_SCORE_RESULT,
		DBInviteHelper.COLUMN_ID_ADDRESS,
		DBInviteHelper.COLUMN_ID_CLUB,
		DBInviteHelper.COLUMN_ID_TOURNAMENT
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

	/**
	 * Return Count Invite by Id Player
	 * @return Count Invite by Id Player
	 */
	public int countByIdPlayer(long idPlayer) {
		String sql = "SELECT COUNT(1) NB FROM " + dbHelper.getTableName() + 
			" WHERE " + DBInviteHelper.COLUMN_ID_PLAYER + " = " + idPlayer;
		List<HashMap<String,Object>> result = rawQuery(sql);
		
		if (result==null || result.size() == 0 || !result.get(0).containsKey("NB")) {
			return 0;
		} else {
			return Integer.valueOf(result.get(0).get("NB").toString());
		}
	}

	/**
	 * Return Count Invite by Ranking
	 * @return Count Invite by Ranking
	 */
	public HashMap<String,Double> countByTypeGroupByRanking(TypeManager.TYPE type, Invite.SCORE_RESULT scoreResult) {
		String where = " WHERE ";
		where += DBInviteHelper.COLUMN_TIME + " < " + Calendar.getInstance().getTimeInMillis();
		if (type != null) {
			where += " AND " + DBInviteHelper.COLUMN_TYPE + " = '" + type + "'";
		} else {
			where = customizeWhere(where);
		}
		if (scoreResult != null) {
			where += " AND " + DBInviteHelper.COLUMN_SCORE_RESULT + " = '" + scoreResult + "'";
		}

		String sql = "SELECT "+DBInviteHelper.COLUMN_ID_RANKING+" ID_RANKING, COUNT(1) NB FROM " + dbHelper.getTableName() + 
			where + " GROUP BY " + DBInviteHelper.COLUMN_ID_RANKING;
		return rawQuerCount(sql);
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
		values.put(DBInviteHelper.COLUMN_SCORE_RESULT, invite.getScoreResult().toString());
		values.put(DBInviteHelper.COLUMN_ID_ADDRESS, invite.getIdAddress());
		values.put(DBInviteHelper.COLUMN_ID_CLUB, invite.getIdClub());
		values.put(DBInviteHelper.COLUMN_ID_TOURNAMENT, invite.getIdTournament());
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
		invite.setStatus(STATUS.valueOf(DbTool.getInstance().toString(cursor, col++, STATUS.UNKNOW.toString())));
		invite.setType(TypeManager.TYPE.valueOf(DbTool.getInstance().toString(cursor, col++, TypeManager.TYPE.ENTRAINEMENT.toString())));
		invite.setIdExternal(DbTool.getInstance().toLong(cursor, col++));
		invite.setIdCalendar(DbTool.getInstance().toLong(cursor, col++));
		invite.setIdRanking(DbTool.getInstance().toLong(cursor, col++));
		invite.setScoreResult(SCORE_RESULT.valueOf(DbTool.getInstance().toString(cursor, col++, SCORE_RESULT.UNFINISHED.toString())));
		invite.setAddress(DbTool.getInstance().toPojo(cursor, col++, Address.class));
		invite.setClub(DbTool.getInstance().toPojo(cursor, col++, Club.class));
		invite.setTournament(DbTool.getInstance().toPojo(cursor, col++, Tournament.class));
		return invite;
	}
	
	@Override
	protected String getTag() {
		return TAG;
	}

	private HashMap<String, Double> rawQuerCount(String sql) {
		HashMap<String, Double> ret = new HashMap<String, Double>();
		List<HashMap<String,Object>> data = rawQuery(sql);

		for(HashMap<String,Object> row : data) {
			ret.put(row.get("ID_RANKING").toString(), Double.parseDouble(row.get("NB").toString()));
		}
		return ret;
	}
}