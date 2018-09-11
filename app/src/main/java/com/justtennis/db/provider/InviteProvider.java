package com.justtennis.db.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.justtennis.db.DBDictionary;
import com.justtennis.db.sqlite.helper.DBInviteHelper;
import com.justtennis.db.sqlite.helper.GenericJustTennisDBHelper;
import com.justtennis.domain.Invite;
import com.justtennis.notifier.NotifierMessageLogger;

public class InviteProvider extends ContentProvider {

    public static final String CONTENT_PROVIDER_MIME = "vnd.android.cursor.item/com.justtennis.provider.invite";
    private GenericJustTennisDBHelper dbHelper;

    public InviteProvider() {
    }

    @Override
    public String getType(Uri uri) {
        return CONTENT_PROVIDER_MIME;
    }

    @Override
    public boolean onCreate() {
        dbHelper = DBDictionary.getInstance(getContext(), NotifierMessageLogger.getInstance()).getDBHelperByClassType(Invite.class);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return dbHelper.getReadableDatabase().query(DBInviteHelper.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = dbHelper.getWritableDatabase().insert(DBInviteHelper.TABLE_NAME, null, values);
        return ContentUris.withAppendedId(uri, id);
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
