package com.justtennis.db.provider;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.cameleon.common.android.db.sqlite.datasource.GenericDBDataSource;
import com.cameleon.common.android.db.sqlite.helper.GenericDBHelper;
import com.justtennis.BuildConfig;
import com.justtennis.db.DBDictionary;
import com.justtennis.db.sqlite.datasource.DBPlayerDataSource;
import com.justtennis.db.sqlite.helper.GenericJustTennisDBHelper;
import com.justtennis.domain.Player;
import com.justtennis.notifier.NotifierMessageLogger;

public class PlayerProvider extends AbstractContentProvider {

    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID + ".provider.player";
    public static final String CONTENT_PROVIDER_MIME = "vnd.android.cursor.item/" + CONTENT_AUTHORITY;
    public static final Uri CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private GenericJustTennisDBHelper dbHelper;
    private GenericDBDataSource dbDataSource;

    public PlayerProvider() {
        // Nothing to do
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return CONTENT_PROVIDER_MIME;
    }

    @Override
    public boolean onCreate() {
        dbHelper = DBDictionary.getInstance(getContext(), NotifierMessageLogger.getInstance()).getDBHelperByClassType(Player.class);
        dbDataSource = new DBPlayerDataSource(getContext(), NotifierMessageLogger.getInstance());
        return true;
    }

    @Override
    protected GenericDBHelper getDbHelper() {
        return dbHelper;
    }

    @Override
    protected GenericDBDataSource getDbDataSource() {
        return dbDataSource;
    }
}
