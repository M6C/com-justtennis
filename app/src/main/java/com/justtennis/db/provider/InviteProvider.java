package com.justtennis.db.provider;

import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.cameleon.common.android.db.sqlite.datasource.GenericDBDataSource;
import com.justtennis.BuildConfig;
import com.justtennis.db.DBDictionary;
import com.justtennis.db.sqlite.datasource.DBInviteDataSource;
import com.justtennis.db.sqlite.helper.GenericJustTennisDBHelper;
import com.justtennis.domain.Invite;
import com.justtennis.notifier.NotifierMessageLogger;

public class InviteProvider extends AbstractContentProvider {

    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID + ".provider.invite";
    public static final String CONTENT_PROVIDER_MIME = "vnd.android.cursor.item/" + CONTENT_AUTHORITY;
    public static final Uri CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private GenericJustTennisDBHelper dbHelper;
    private GenericDBDataSource dbDataSource;

    public InviteProvider() {
        // Nothing to do
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return CONTENT_PROVIDER_MIME;
    }

    @Override
    public boolean onCreate() {
        dbHelper = DBDictionary.getInstance(getContext(), NotifierMessageLogger.getInstance()).getDBHelperByClassType(Invite.class);
        dbDataSource = new DBInviteDataSource(getContext(), NotifierMessageLogger.getInstance());
        return true;
    }

    @Override
    protected SQLiteOpenHelper getDbHelper() {
        return dbHelper;
    }

    @Override
    protected GenericDBDataSource getDbDataSource() {
        return dbDataSource;
    }
}
