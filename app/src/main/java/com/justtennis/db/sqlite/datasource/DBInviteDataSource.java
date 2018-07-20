package com.justtennis.db.sqlite.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.cameleon.common.tool.DbTool;
import com.justtennis.db.service.SaisonService;
import com.justtennis.db.sqlite.helper.DBInviteHelper;
import com.justtennis.domain.Address;
import com.justtennis.domain.Club;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Invite.SCORE_RESULT;
import com.justtennis.domain.Invite.STATUS;
import com.justtennis.domain.Player;
import com.justtennis.domain.Saison;
import com.justtennis.domain.Tournament;
import com.justtennis.manager.TypeManager;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DBInviteDataSource extends GenericDBDataSourceByType<Invite> {

	private static final String TAG = DBInviteDataSource.class.getCanonicalName();

	// Database fields
	private String[] allColumns = {
		DBInviteHelper.COLUMN_ID,
		DBInviteHelper.COLUMN_ID_SAISON,
		DBInviteHelper.COLUMN_ID_PLAYER,
		DBInviteHelper.COLUMN_TIME,
		DBInviteHelper.COLUMN_STATUS,
		DBInviteHelper.COLUMN_TYPE,
		DBInviteHelper.COLUMN_ID_EXTERNAL,
		DBInviteHelper.COLUMN_ID_CALENDAR,
		DBInviteHelper.COLUMN_ID_RANKING,
		DBInviteHelper.COLUMN_ID_RANKING_ESTIMATE,
		DBInviteHelper.COLUMN_SCORE_RESULT,
		DBInviteHelper.COLUMN_ID_ADDRESS,
		DBInviteHelper.COLUMN_ID_CLUB,
		DBInviteHelper.COLUMN_ID_TOURNAMENT,
		DBInviteHelper.COLUMN_BONUS_POINT
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
	 * Return all Invite for a Tournament
	 * @param idTournament Tournament id
	 * @return Invite list
	 */
	public List<Invite> getByIdTournament(long idTournament) {
		String sqlWhere = DBInviteHelper.COLUMN_ID_TOURNAMENT + " = " + idTournament;
		return query(sqlWhere);
	}

	/**
	 * Return all Invite with no Tournament
	 * @return Invite list
	 */
	public List<Invite> getByNoTournament() {
		String sqlWhere = DBInviteHelper.COLUMN_ID_TOURNAMENT + " IS NULL ";
		return query(sqlWhere);
	}
	
	/**
	 * Return all Invite for a Tournament
	 * @param scoreResult score result
	 * @return Invite list
	 */
	public List<Invite> getByScoreResult(Invite.SCORE_RESULT scoreResult) {
		String sqlWhere = DBInviteHelper.COLUMN_SCORE_RESULT + " = '" + scoreResult.toString() + "'";
		return query(sqlWhere);
	}

	/**
	 * Return Count Invite by Id Player
	 * @return Count Invite by Id Player
	 */
	public int countByIdPlayer(long idPlayer) {
		return countById(DBInviteHelper.COLUMN_ID_PLAYER, idPlayer);
	}

	/**
	 * Return Count Invite by Id Saison
	 * @return Count Invite by Id Saison
	 */
	public int countByIdSaison(long idSaison) {
		return countById(DBInviteHelper.COLUMN_ID_SAISON, idSaison);
	}

	public int countByIdClub(Long idClub) {
		return countById(DBInviteHelper.COLUMN_ID_CLUB, idClub);
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
			where = customizeWhereSaison(where);
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

	/**
	 * Return Count Invite by Type
	 * @return Count Invite by Type
	 */
	public int countByScoreResult(Invite.SCORE_RESULT[] scoreResult) {
		String where = " WHERE " + DBInviteHelper.COLUMN_TIME + " < " + Calendar.getInstance().getTimeInMillis();

		if (scoreResult != null && scoreResult.length > 0) {
			StringBuilder inValue = new StringBuilder("(");
			for(int i=0 ; i<scoreResult.length ; i++) {
				if (i>0) {
					inValue.append(",");
				}
				inValue.append("'").append(scoreResult[i]).append("'");
			}
			inValue.append(")");
			where += " AND " + DBInviteHelper.COLUMN_SCORE_RESULT + " IN " + inValue.toString();
		}

		where = customizeWhere(where);

		String sql = "SELECT COUNT(1) NB FROM " + dbHelper.getTableName() + where;
		return count(sql);
	}

	public void setSaisonWhereIsNull(Saison saison) {
		String sql = 
			"UPDATE " + DBInviteHelper.TABLE_NAME + 
			" SET " + DBInviteHelper.COLUMN_ID_SAISON + " = '" + saison.getId() + "'" +
			" WHERE " + DBInviteHelper.COLUMN_ID_SAISON + " IS NULL";
		logMe(sql);
		db.execSQL(sql);
	}

	@Override
	protected String[] getAllColumns() {
		return allColumns;
	}

	@Override
	protected void putContentValue(ContentValues values, Invite invite) {
		values.put(DBInviteHelper.COLUMN_ID_SAISON, invite.getSaison().getId());
		values.put(DBInviteHelper.COLUMN_ID_PLAYER, invite.getPlayer().getId());
		values.put(DBInviteHelper.COLUMN_TIME, invite.getDate().getTime());
		values.put(DBInviteHelper.COLUMN_STATUS, invite.getStatus().toString());
		values.put(DBInviteHelper.COLUMN_TYPE, invite.getType().toString());
		values.put(DBInviteHelper.COLUMN_ID_EXTERNAL, invite.getIdExternal());
		values.put(DBInviteHelper.COLUMN_ID_CALENDAR, invite.getIdCalendar());
		values.put(DBInviteHelper.COLUMN_ID_RANKING, invite.getIdRanking());
		values.put(DBInviteHelper.COLUMN_ID_RANKING_ESTIMATE, invite.getIdRankingEstimate());
		values.put(DBInviteHelper.COLUMN_SCORE_RESULT, invite.getScoreResult().toString());
		values.put(DBInviteHelper.COLUMN_ID_ADDRESS, invite.getIdAddress());
		values.put(DBInviteHelper.COLUMN_ID_CLUB, invite.getIdClub());
		values.put(DBInviteHelper.COLUMN_ID_TOURNAMENT, invite.getIdTournament());
		values.put(DBInviteHelper.COLUMN_BONUS_POINT, invite.getBonusPoint());
	}

	@Override
	protected Invite cursorToPojo(Cursor cursor) {
		int col = 0;
		Invite invite = new Invite(DbTool.getInstance().toLong(cursor, col++));
		invite.setSaison(new Saison(DbTool.getInstance().toLong(cursor, col++)));
		invite.setPlayer(new Player(DbTool.getInstance().toLong(cursor, col++)));
		String date = cursor.getString(col++);
		invite.setDate(date==null || "null".equals(date.toLowerCase(Locale.FRANCE)) ? null : new Date(Long.parseLong(date)));
		invite.setStatus(STATUS.valueOf(DbTool.getInstance().toString(cursor, col++, STATUS.UNKNOW.toString())));
		invite.setType(TypeManager.TYPE.valueOf(DbTool.getInstance().toString(cursor, col++, TypeManager.TYPE.TRAINING.toString())));
		invite.setIdExternal(DbTool.getInstance().toLong(cursor, col++));
		invite.setIdCalendar(DbTool.getInstance().toLong(cursor, col++));
		invite.setIdRanking(DbTool.getInstance().toLong(cursor, col++));
		invite.setIdRankingEstimate(DbTool.getInstance().toLong(cursor, col++));
		invite.setScoreResult(SCORE_RESULT.valueOf(DbTool.getInstance().toString(cursor, col++, SCORE_RESULT.UNFINISHED.toString())));
		invite.setAddress(DbTool.getInstance().toPojo(cursor, col++, Address.class));
		invite.setClub(DbTool.getInstance().toPojo(cursor, col++, Club.class));
		invite.setTournament(DbTool.getInstance().toPojo(cursor, col++, Tournament.class));
		Integer bonusPoint  = DbTool.getInstance().toInteger(cursor, col);
		invite.setBonusPoint(bonusPoint == null ? 0 : bonusPoint);
		return invite;
	}

	@Override
	protected String customizeWhere(String where) {
		String ret = super.customizeWhere(where);

		ret = customizeWhereSaison(ret);

		return ret;
	}

	private String customizeWhereSaison(String where) {
		StringBuilder stb = new StringBuilder(where);
		Saison saison = TypeManager.getInstance().getSaison();
		if (saison != null && saison.getId() != null && !SaisonService.isEmpty(saison)) {
			stb.append(" AND ").append(DBInviteHelper.COLUMN_ID_SAISON).append(" = ").append(saison.getId());
		}
		return stb.toString();
	}

	@Override
	protected String getTag() {
		return TAG;
	}

	private HashMap<String, Double> rawQuerCount(String sql) {
		HashMap<String, Double> ret = new HashMap<>();
		List<HashMap<String,Object>> data = rawQuery(sql);

		for(HashMap<String,Object> row : data) {
			Object id_ranking = row.get("ID_RANKING");
			if (id_ranking != null) {
				Object nb = row.get("NB");
				ret.put(id_ranking.toString(), (nb == null ? 0.0d : Double.parseDouble(nb.toString())));
			}
		}
		return ret;
	}

	/**
	 * Return Count Invite by Id
	 * @return Count Invite by Id
	 */
	private int countById(String columnName, long idSaison) {
		String sql = "SELECT COUNT(1) NB FROM " + dbHelper.getTableName() + 
			" WHERE " + columnName + " = " + idSaison;
		return count(sql);
	}

	private int count(String sql) {
		List<HashMap<String,Object>> result = rawQuery(sql);
		
		if (result==null || result.size() == 0 || !result.get(0).containsKey("NB")) {
			return 0;
		} else {
			return Integer.valueOf(result.get(0).get("NB").toString());
		}
	}
}