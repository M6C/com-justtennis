package com.justtennis.db.sqlite.datasource;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.helper.DBRankingHelper;
import com.justtennis.domain.Ranking;
import com.justtennis.tool.DbTool;

public class DBRankingDataSource extends GenericDBDataSource<Ranking> {

	private static final String TAG = DBRankingDataSource.class.getCanonicalName();

	// Database fields
	private String[] allColumns = {
		DBRankingHelper.COLUMN_ID,
		DBRankingHelper.COLUMN_RANKING,
		DBRankingHelper.COLUMN_SERIE,
		DBRankingHelper.COLUMN_POSITION,
		DBRankingHelper.COLUMN_RANK_POINT_MAN,
		DBRankingHelper.COLUMN_RANK_POINT_WOMAN,
		DBRankingHelper.COLUMN_VICTORY_MAN,
		DBRankingHelper.COLUMN_VICTORY_WOMAN
	};

	public DBRankingDataSource(Context context, INotifierMessage notificationMessage) {
		super(new DBRankingHelper(context, notificationMessage), notificationMessage);
	}

	/**
	 * Return all Ranking
	 * @return Ranking list
	 */
	public List<Ranking> getAll() {
		return query(null);
	}

	/**
	 * Return NC Ranking
	 * @return Ranking
	 */
	public Ranking getNC() {
		List<Ranking> list = query(DBRankingHelper.COLUMN_RANKING + " = ?", new String[]{DBRankingHelper.RANKING_NC});
		return (list!=null && list.size()>0 ? list.get(0) : null);
	}

	/**
	 * @param position
	 * @return
	 */
	public List<Ranking> getWithPostionEqualUpper(int position) {
		return query(DBRankingHelper.COLUMN_POSITION + " >= ?", new String[]{DBRankingHelper.RANKING_NC});
	}

	@Override
	protected String[] getAllColumns() {
		return allColumns;
	}

	@Override
	protected void putContentValue(ContentValues values, Ranking ranking) {
		values.put(DBRankingHelper.COLUMN_RANKING, ranking.getRanking());
		values.put(DBRankingHelper.COLUMN_SERIE, ranking.getSerie());
		values.put(DBRankingHelper.COLUMN_POSITION, ranking.getOrder());
		values.put(DBRankingHelper.COLUMN_RANK_POINT_MAN, ranking.getRankingPointMan());
		values.put(DBRankingHelper.COLUMN_RANK_POINT_WOMAN, ranking.getRankingPointWoman());
		values.put(DBRankingHelper.COLUMN_VICTORY_MAN, ranking.getVictoryMan());
		values.put(DBRankingHelper.COLUMN_VICTORY_WOMAN, ranking.getVictoryWoman());
	}

	@Override
	protected Ranking cursorToPojo(Cursor cursor) {
		int col = 0;
		Ranking ranking = new Ranking();
		ranking.setId(DbTool.getInstance().toLong(cursor, col++));
		ranking.setRanking(cursor.getString(col++));
		ranking.setSerie(DbTool.getInstance().toInteger(cursor, col++));
		ranking.setOrder(DbTool.getInstance().toInteger(cursor, col++));
		ranking.setRankingPointMan(DbTool.getInstance().toInteger(cursor, col++));
		ranking.setRankingPointWoman(DbTool.getInstance().toInteger(cursor, col++));
		ranking.setVictoryMan(DbTool.getInstance().toInteger(cursor, col++));
		ranking.setVictoryWoman(DbTool.getInstance().toInteger(cursor, col++));
		return ranking;
	}
	
	@Override
	protected String getTag() {
		return TAG;
	}
}