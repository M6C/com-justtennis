package com.justtennis.db.sqlite.datasource;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.cameleon.common.android.db.sqlite.helper.GenericDBHelper;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.cameleon.common.tool.DbTool;
import com.justtennis.db.sqlite.helper.DBPersonHelper;
import com.justtennis.db.sqlite.helper.DBPlayerHelper;
import com.justtennis.db.sqlite.helper.DBUserHelper;
import com.justtennis.domain.User;

public class DBUserDataSource extends DBPersonDataSource<User> {

	private static final String TAG = DBUserDataSource.class.getCanonicalName();

	// Database fields
	private String[] allColumns = {
		GenericDBHelper.COLUMN_ID,
		DBPersonHelper.COLUMN_FIRSTNAME,
		DBPersonHelper.COLUMN_LASTNAME,
		DBPersonHelper.COLUMN_BIRTHDAY,
		DBPersonHelper.COLUMN_PHONENUMBER,
		DBPersonHelper.COLUMN_ADDRESS,
		DBPersonHelper.COLUMN_POSTALCODE,
		DBPersonHelper.COLUMN_LOCALITY,
		DBPlayerHelper.COLUMN_ID_SAISON,
		DBPlayerHelper.COLUMN_ID_TOURNAMENT,
		DBPlayerHelper.COLUMN_ID_CLUB,
		DBPlayerHelper.COLUMN_ID_ADDRESS,
		DBPlayerHelper.COLUMN_ID_RANKING,
		DBPlayerHelper.COLUMN_ID_RANKING_ESTIMAGE
	};

	public DBUserDataSource(Context context, INotifierMessage notificationMessage) {
		super(new DBUserHelper(context, notificationMessage), notificationMessage);
	}

	/**
	 * Return an User for a Saison
	 * @param idSaison Saison id
	 * @return an User or null
	 */
	public User getByIdSaison(long idSaison) {
		User ret = null;
		String sqlWhere = DBUserHelper.COLUMN_ID_SAISON + " = " + idSaison;
		List<User> list = query(sqlWhere);
		if (list != null && list.size() > 0) {
			ret = list.get(0);
		}
		return ret;
	}

	@Override
	public String[] getAllColumns() {
		return allColumns;
	}

	@Override
	protected String customizeWhere(String where) {
		return customizeWhereSaison(where);
	}

	@Override
	protected void putContentValue(ContentValues values, User user) {
		super.putPersonValue(values, user);
		values.put(DBPlayerHelper.COLUMN_ID_SAISON, user.getIdSaison());
		values.put(DBPlayerHelper.COLUMN_ID_TOURNAMENT, user.getIdTournament());
		values.put(DBPlayerHelper.COLUMN_ID_CLUB, user.getIdClub());
		values.put(DBPlayerHelper.COLUMN_ID_ADDRESS, user.getIdAddress());
		values.put(DBPlayerHelper.COLUMN_ID_RANKING, user.getIdRanking());
		values.put(DBPlayerHelper.COLUMN_ID_RANKING_ESTIMAGE, user.getIdRankingEstimate());
	}

	@Override
	protected User cursorToPojo(Cursor cursor) {
		int col = 0;
		User user = new User();
		col = super.cursorToPojo(cursor, user, col);
		user.setIdSaison(DbTool.getInstance().toLong(cursor, col++));
		user.setIdTournament(DbTool.getInstance().toLong(cursor, col++));
		user.setIdClub(DbTool.getInstance().toLong(cursor, col++));
		user.setIdAddress(DbTool.getInstance().toLong(cursor, col++));
		user.setIdRanking(DbTool.getInstance().toLong(cursor, col++));
		user.setIdRankingEstimate(DbTool.getInstance().toLong(cursor, col++));
		return user;
	}
	
	@Override
	protected String getTag() {
		return TAG;
	}
}