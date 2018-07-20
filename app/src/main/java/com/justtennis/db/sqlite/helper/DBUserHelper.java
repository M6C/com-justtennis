package com.justtennis.db.sqlite.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.service.SaisonService;
import com.justtennis.domain.Saison;
import com.justtennis.domain.User;

public class DBUserHelper extends DBPersonHelper {

	private static final String TAG = DBUserHelper.class.getCanonicalName();

	public static final String TABLE_NAME = "USER";

	public static final String COLUMN_ID_SAISON = "ID_SAISON";
	public static final String COLUMN_ID_TOURNAMENT = "ID_TOURNAMENT";
	public static final String COLUMN_ID_CLUB = "ID_CLUB";
	public static final String COLUMN_ID_ADDRESS = "ID_ADDRESS";
	public static final String COLUMN_ID_RANKING = "ID_RANKING";
	public static final String COLUMN_ID_RANKING_ESTIMAGE = "ID_RANKING_ESTIMAGE";

	private static final String DATABASE_NAME = "User.db";
	private static final int DATABASE_VERSION = 9;

	private static final Class<?> CLASS_TYPE = User.class;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME + "(" + 
		COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
		COLUMN_ID_SAISON + " INTEGER NULL, " + 
		COLUMN_ID_TOURNAMENT + " INTEGER NULL, " + 
		COLUMN_ID_CLUB + " INTEGER NULL, " + 
		COLUMN_ID_ADDRESS + " INTEGER NULL, " + 
		COLUMN_ID_RANKING + " INTEGER NULL, " + 
		COLUMN_ID_RANKING_ESTIMAGE + " INTEGER NULL, " + 
		COLUMN_FIRSTNAME + " TEXT NULL, " + 
		COLUMN_LASTNAME + " TEXT NULL, " + 
		COLUMN_BIRTHDAY + " INTEGER NULL, " + 
		COLUMN_PHONENUMBER + " TEXT NULL, " + 
		COLUMN_ADDRESS + " TEXT NULL, " + 
		COLUMN_POSTALCODE + " TEXT NULL, " + 
		COLUMN_LOCALITY + " TEXT NULL " + 
	");";

	public DBUserHelper(Context context, INotifierMessage notificationMessage) {
		super(context, notificationMessage, DATABASE_NAME, DATABASE_VERSION);
		this.context = context;
		this.notificationMessage = notificationMessage;
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		if (newVersion>oldVersion) {
			logMe("UPGRADE DATABASE VERSION:" + oldVersion + " TO " + newVersion);
			if (oldVersion <= 5) {
				addColumn(database, COLUMN_ID_TOURNAMENT, " INTEGER NULL ");
			}
			if (oldVersion <= 6) {
				addColumn(database, COLUMN_ID_ADDRESS, " INTEGER NULL ");
			}
			if (oldVersion <= 7) {
				addColumn(database, COLUMN_ID_RANKING_ESTIMAGE, " INTEGER NULL ");
			}
			if (oldVersion <= 8) {
				addColumn(database, COLUMN_ID_SAISON, " INTEGER NULL ");
				SaisonService saisonService = new SaisonService(context, notificationMessage);
				Saison saison = saisonService.getSaisonActiveOrFirst();
				if (saison != null) {
					logMe("UPGRADE COLUMN '" + COLUMN_ID_SAISON + "' TO SAISON id:" + saison.getId() + " Name:" + saison.getName());
					updateColumn(database, COLUMN_ID_SAISON, saison.getId().toString(), null);
				} else {
					logMe("UPGRADE NO SAISON FOUND TO UPDATE USER");
				}
			}
		}
		else {
			super.onUpgrade(database, oldVersion, newVersion);
		}
	}

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public String getDatabaseCreate() {
		return DATABASE_CREATE;
	}
	
	@Override
	public Class<?> getClassType() {
		return CLASS_TYPE;
	}
}