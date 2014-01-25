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
		DBRankingHelper.COLUMN_POSITION
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

	@Override
	protected String[] getAllColumns() {
		return allColumns;
	}

	@Override
	protected void putContentValue(ContentValues values, Ranking ranking) {
		values.put(DBRankingHelper.COLUMN_RANKING, ranking.getRanking());
		values.put(DBRankingHelper.COLUMN_SERIE, ranking.getSerie());
		values.put(DBRankingHelper.COLUMN_POSITION, ranking.getOrder());
	}

	@Override
	protected Ranking cursorToPojo(Cursor cursor) {
		int col = 0;
		Ranking ranking = new Ranking();
		ranking.setId(DbTool.getInstance().toLong(cursor, col++));
		ranking.setRanking(cursor.getString(col++));
		ranking.setSerie(DbTool.getInstance().toInteger(cursor, col++));
		ranking.setOrder(DbTool.getInstance().toInteger(cursor, col++));
		return ranking;
	}
	
	@Override
	protected String getTag() {
		return TAG;
	}
}