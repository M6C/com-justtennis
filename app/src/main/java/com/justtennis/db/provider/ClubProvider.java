package com.justtennis.db.provider;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.cameleon.common.android.db.sqlite.datasource.GenericDBDataSource;
import com.cameleon.common.android.db.sqlite.helper.GenericDBHelper;
import com.justtennis.BuildConfig;
import com.justtennis.db.DBDictionary;
import com.justtennis.db.sqlite.datasource.DBClubDataSource;
import com.justtennis.db.sqlite.helper.GenericJustTennisDBHelper;
import com.justtennis.domain.Club;
import com.justtennis.notifier.NotifierMessageLogger;

public class ClubProvider extends AbstractContentProvider {

    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID + ".provider.club";
    public static final String CONTENT_PROVIDER_MIME = "vnd.android.cursor.item/" + CONTENT_AUTHORITY;
    public static final Uri CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private GenericJustTennisDBHelper dbHelper;
    private GenericDBDataSource dbDataSource;

    public ClubProvider() {
        // Nothing to do
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return CONTENT_PROVIDER_MIME;
    }

    @Override
    public boolean onCreate() {
        dbHelper = DBDictionary.getInstance(getContext(), NotifierMessageLogger.getInstance()).getDBHelperByClassType(Club.class);
        dbDataSource = new DBClubDataSource(getContext(), NotifierMessageLogger.getInstance());
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