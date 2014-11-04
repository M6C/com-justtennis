package com.justtennis.db.sqlite.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.service.SaisonService;
import com.justtennis.db.sqlite.helper.DBInviteHelper;
import com.justtennis.db.sqlite.helper.DBPlayerHelper;
import com.justtennis.domain.Player;
import com.justtennis.domain.Saison;
import com.justtennis.manager.TypeManager;
import com.justtennis.tool.DbTool;

public class DBPlayerDataSource extends GenericDBDataSourceByType<Player> {

	private static final String TAG = DBPlayerDataSource.class.getCanonicalName();

	// Database fields
	private String[] allColumns = {
		DBPlayerHelper.COLUMN_ID,
		DBPlayerHelper.COLUMN_ID_SAISON,
		DBPlayerHelper.COLUMN_ID_TOURNAMENT,
		DBPlayerHelper.COLUMN_ID_CLUB,
		DBPlayerHelper.COLUMN_ID_ADDRESS,
		DBPlayerHelper.COLUMN_ID_RANKING,
		DBPlayerHelper.COLUMN_ID_RANKING_ESTIMAGE,
		DBPlayerHelper.COLUMN_FIRSTNAME,
		DBPlayerHelper.COLUMN_LASTNAME,
		DBPlayerHelper.COLUMN_BIRTHDAY,
		DBPlayerHelper.COLUMN_PHONENUMBER,
		DBPlayerHelper.COLUMN_ADDRESS,
		DBPlayerHelper.COLUMN_POSTALCODE,
		DBPlayerHelper.COLUMN_LOCALITY,
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
		values.put(DBPlayerHelper.COLUMN_ID_SAISON, player.getIdSaison());
		values.put(DBPlayerHelper.COLUMN_ID_TOURNAMENT, player.getIdTournament());
		values.put(DBPlayerHelper.COLUMN_ID_CLUB, player.getIdClub());
		values.put(DBPlayerHelper.COLUMN_ID_ADDRESS, player.getIdAddress());
		values.put(DBPlayerHelper.COLUMN_ID_RANKING, player.getIdRanking());
		values.put(DBPlayerHelper.COLUMN_ID_RANKING_ESTIMAGE, player.getIdRankingEstimate());
		values.put(DBPlayerHelper.COLUMN_FIRSTNAME, player.getFirstName());
		values.put(DBPlayerHelper.COLUMN_LASTNAME, player.getLastName());
		values.put(DBPlayerHelper.COLUMN_BIRTHDAY, player.getBirthday());
		values.put(DBPlayerHelper.COLUMN_PHONENUMBER, player.getPhonenumber());
		values.put(DBPlayerHelper.COLUMN_ADDRESS, player.getAddress());
		values.put(DBPlayerHelper.COLUMN_POSTALCODE, player.getPostalCode());
		values.put(DBPlayerHelper.COLUMN_LOCALITY, player.getLocality());
		values.put(DBPlayerHelper.COLUMN_ID_EXTERNAL, player.getIdExternal());
		values.put(DBPlayerHelper.COLUMN_ID_GOOGLE, player.getIdGoogle());
		values.put(DBPlayerHelper.COLUMN_TYPE, player.getType().toString());
	}

	@Override
	protected Player cursorToPojo(Cursor cursor) {
		int col = 0;
		Player player = new Player();
		player.setId(DbTool.getInstance().toLong(cursor, col++));
		player.setIdSaison(DbTool.getInstance().toLong(cursor, col++));
		player.setIdTournament(DbTool.getInstance().toLong(cursor, col++));
		player.setIdClub(DbTool.getInstance().toLong(cursor, col++));
		player.setIdAddress(DbTool.getInstance().toLong(cursor, col++));
		player.setIdRanking(DbTool.getInstance().toLong(cursor, col++));
		player.setIdRankingEstimate(DbTool.getInstance().toLong(cursor, col++));
		player.setFirstName(cursor.getString(col++));
		player.setLastName(cursor.getString(col++));
		player.setBirthday(cursor.getString(col++));
		player.setPhonenumber(cursor.getString(col++));
		player.setAddress(cursor.getString(col++));
		player.setPostalCode(cursor.getString(col++));
		player.setLocality(cursor.getString(col++));
		player.setIdExternal(DbTool.getInstance().toLong(cursor, col++));
		player.setIdGoogle(DbTool.getInstance().toLong(cursor, col++));
		player.setType(TypeManager.TYPE.valueOf(DbTool.getInstance().toString(cursor, col++, TypeManager.TYPE.TRAINING.toString())));
		return player;
	}

	@Override
	protected String customizeWhere(String where) {
		where = super.customizeWhere(where);

		Saison saison = TypeManager.getInstance().getSaison();
		if (saison != null && saison.getId() != null && !SaisonService.isEmpty(saison)) {
			where += " AND (" + DBPlayerHelper.COLUMN_ID_SAISON + " = " + saison.getId() + " OR " + DBPlayerHelper.COLUMN_ID_SAISON + " IS NULL)";
		}
		return where;
	}

	@Override
	protected String getTag() {
		return TAG;
	}
}