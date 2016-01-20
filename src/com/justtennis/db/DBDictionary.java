package com.justtennis.db;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.helper.DBAddressHelper;
import com.justtennis.db.sqlite.helper.DBBonusHelper;
import com.justtennis.db.sqlite.helper.DBClubHelper;
import com.justtennis.db.sqlite.helper.DBInviteHelper;
import com.justtennis.db.sqlite.helper.DBMessageHelper;
import com.justtennis.db.sqlite.helper.DBPlayerHelper;
import com.justtennis.db.sqlite.helper.DBRankingHelper;
import com.justtennis.db.sqlite.helper.DBSaisonHelper;
import com.justtennis.db.sqlite.helper.DBScoreSetHelper;
import com.justtennis.db.sqlite.helper.DBTournamentHelper;
import com.justtennis.db.sqlite.helper.DBUserHelper;
import com.justtennis.db.sqlite.helper.GenericJustTennisDBHelper;

public class DBDictionary {
	
	private static final String TAG = DBDictionary.class.getSimpleName();

	private static DBDictionary instance = null;
	private GenericJustTennisDBHelper[] listHelper;
	private Map<Class<?>, GenericJustTennisDBHelper> mapHelperByClassType = new HashMap<Class<?>, GenericJustTennisDBHelper>();
	private Map<String, GenericJustTennisDBHelper> mapHelperByDatabaseName = new HashMap<String, GenericJustTennisDBHelper>();

	private DBDictionary() {
	}

	private DBDictionary(Context context, INotifierMessage notifier) {
		listHelper = new GenericJustTennisDBHelper[] {
			new DBAddressHelper(context, notifier),
			new DBBonusHelper(context, notifier),
			new DBClubHelper(context, notifier),
			new DBInviteHelper(context, notifier),
			new DBMessageHelper(context, notifier),
			new DBPlayerHelper(context, notifier),
			new DBRankingHelper(context, notifier),
			new DBSaisonHelper(context, notifier),
			new DBScoreSetHelper(context, notifier),
			new DBTournamentHelper(context, notifier),
			new DBUserHelper(context, notifier)
		};

		computeMapHelper();
	}

	public static DBDictionary getInstance(Context context, INotifierMessage notifier) {
		if (instance == null) {
			instance = new DBDictionary(context, notifier);
		}
		return instance;
	}

	public GenericJustTennisDBHelper getDBHelperFromDatabaseName(String databaseName) {
		return mapHelperByDatabaseName.get(databaseName.toLowerCase());
	}

	public Collection<GenericJustTennisDBHelper> getDBHelperByDatabaseName() {
		return mapHelperByDatabaseName.values();
	}

	private void computeMapHelper() {
		for(GenericJustTennisDBHelper helper : listHelper) {
			mapHelperByClassType.put(helper.getClassType(), helper);

			String databaseName = helper.getDatabaseName().toLowerCase();
			if (!mapHelperByDatabaseName.containsKey(databaseName)) {
				Log.d(TAG, "computeMapHelper put DB Helper found for databaseName:'"+databaseName+"' in mapHelperByDatabaseName");
				mapHelperByDatabaseName.put(databaseName, helper);
			}
		}
	}

	public GenericJustTennisDBHelper[] getListHelper() {
		return listHelper;
	}
}
