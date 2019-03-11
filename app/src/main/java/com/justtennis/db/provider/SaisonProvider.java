package com.justtennis.db.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.justtennis.BuildConfig;
import com.justtennis.db.DBDictionary;
import com.justtennis.db.service.SaisonService;
import com.justtennis.db.sqlite.helper.DBSaisonHelper;
import com.justtennis.db.sqlite.helper.GenericJustTennisDBHelper;
import com.justtennis.domain.Saison;
import com.justtennis.notifier.NotifierMessageLogger;

public class SaisonProvider extends ContentProvider {

    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID + ".provider.saison";
    public static final String CONTENT_PROVIDER_MIME = "vnd.android.cursor.item/" + CONTENT_AUTHORITY;
    public static final Uri CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private GenericJustTennisDBHelper dbHelper;

    public SaisonProvider() {
        // Nothing to do
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return CONTENT_PROVIDER_MIME;
    }

    @Override
    public boolean onCreate() {
        dbHelper = DBDictionary.getInstance(getContext(), NotifierMessageLogger.getInstance()).getDBHelperByClassType(Saison.class);
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;//return dbHelper.getReadableDatabase().query(DBSaisonHelper.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
//        long id = -1;
//        if (values.size() == 1 && values.containsKey(DBSaisonHelper.COLUMN_NAME)) {
//            String name = values.getAsString(DBSaisonHelper.COLUMN_NAME);
//            if (name != null && name.length() == 4) {
//                int millesime = Integer.parseInt(name);
//                NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
//                SaisonService saisonService = new SaisonService(getContext(), notifier);
//                Saison saison = saisonService.getSaison(millesime);
//                if (saison == null) {
//                    boolean active = saisonService.getSaisonActive() == null;
//                    // Create saison from millesime
//                    id = saisonService.create(millesime, active).getId();
//                } else {
//                    id = saison.getId();
//                }
//            }
//        }
//        if (id == -1) {
//            id = dbHelper.getWritableDatabase().insert(DBSaisonHelper.TABLE_NAME, null, values);
//        }
        return null;//return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
