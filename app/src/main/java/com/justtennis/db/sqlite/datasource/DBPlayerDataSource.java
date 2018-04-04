package com.justtennis.db.sqlite.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.cameleon.common.android.db.sqlite.helper.GenericDBHelper;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.cameleon.common.tool.DbTool;
import com.justtennis.db.sqlite.helper.DBPersonHelper;
import com.justtennis.db.sqlite.helper.DBPlayerHelper;
import com.justtennis.domain.Player;
import com.justtennis.manager.TypeManager;

public class DBPlayerDataSource extends DBPersonDataSource<Player> {

	private static final String TAG = DBPlayerDataSource.class.getCanonicalName();

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
		DBPlayerHelper.COLUMN_ID_RANKING_ESTIMAGE,
		DBPlayerHelper.COLUMN_ID_EXTERNAL,
		DBPlayerHelper.COLUMN_ID_GOOGLE,
		DBPlayerHelper.COLUMN_TYPE
	};

	public DBPlayerDataSource(Context context, INotifierMessage notificationMessage) {
		super(new DBPlayerHelper(context, notificationMessage), notificationMessage);
	}

	@Override
	protected String[] getAllColumns() {
		return allColumns;
	}

	@Override
	protected void putContentValue(ContentValues values, Player player) {
		super.putPersonValue(values, player);
		values.put(DBPlayerHelper.COLUMN_ID_SAISON, player.getIdSaison());
		values.put(DBPlayerHelper.COLUMN_ID_TOURNAMENT, player.getIdTournament());
		values.put(DBPlayerHelper.COLUMN_ID_CLUB, player.getIdClub());
		values.put(DBPlayerHelper.COLUMN_ID_ADDRESS, player.getIdAddress());
		values.put(DBPlayerHelper.COLUMN_ID_RANKING, player.getIdRanking());
		values.put(DBPlayerHelper.COLUMN_ID_RANKING_ESTIMAGE, player.getIdRankingEstimate());
		values.put(DBPlayerHelper.COLUMN_ID_EXTERNAL, player.getIdExternal());
		values.put(DBPlayerHelper.COLUMN_ID_GOOGLE, player.getIdGoogle());
		values.put(DBPlayerHelper.COLUMN_TYPE, player.getType().toString());
	}

	@Override
	protected Player cursorToPojo(Cursor cursor) {
		int col = 0;
		Player player = new Player();
		col = super.cursorToPojo(cursor, player, col);
		player.setIdSaison(DbTool.getInstance().toLong(cursor, col++));
		player.setIdTournament(DbTool.getInstance().toLong(cursor, col++));
		player.setIdClub(DbTool.getInstance().toLong(cursor, col++));
		player.setIdAddress(DbTool.getInstance().toLong(cursor, col++));
		player.setIdRanking(DbTool.getInstance().toLong(cursor, col++));
		player.setIdRankingEstimate(DbTool.getInstance().toLong(cursor, col++));
		player.setIdExternal(DbTool.getInstance().toLong(cursor, col++));
		player.setIdGoogle(DbTool.getInstance().toLong(cursor, col++));
		player.setType(TypeManager.TYPE.valueOf(DbTool.getInstance().toString(cursor, col++, TypeManager.TYPE.TRAINING.toString())));
		return player;
	}

	@Override
	protected String getTag() {
		return TAG;
	}
}