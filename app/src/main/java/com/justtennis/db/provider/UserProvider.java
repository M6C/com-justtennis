package com.justtennis.db.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.justtennis.BuildConfig;
import com.justtennis.db.DBDictionary;
import com.justtennis.db.sqlite.helper.DBUserHelper;
import com.justtennis.db.sqlite.helper.GenericJustTennisDBHelper;
import com.justtennis.domain.User;
import com.justtennis.notifier.NotifierMessageLogger;

public class UserProvider extends ContentProvider {

    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID + ".provider.user";
    public static final String CONTENT_PROVIDER_MIME = "vnd.android.cursor.item/" + CONTENT_AUTHORITY;
    public static final Uri CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private GenericJustTennisDBHelper dbHelper;

    public UserProvider() {
    }

    @Override
    public String getType(Uri uri) {
        return CONTENT_PROVIDER_MIME;
    }

    @Override
    public boolean onCreate() {
        dbHelper = DBDictionary.getInstance(getContext(), NotifierMessageLogger.getInstance()).getDBHelperByClassType(User.class);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return dbHelper.getReadableDatabase().query(DBUserHelper.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
