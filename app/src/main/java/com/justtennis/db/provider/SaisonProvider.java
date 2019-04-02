package com.justtennis.db.provider;

import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.cameleon.common.android.db.sqlite.datasource.GenericDBDataSource;
import com.cameleon.common.android.db.sqlite.helper.GenericDBHelper;
import com.justtennis.BuildConfig;
import com.justtennis.db.DBDictionary;
import com.justtennis.db.sqlite.datasource.DBSaisonDataSource;
import com.justtennis.db.sqlite.helper.GenericJustTennisDBHelper;
import com.justtennis.domain.Saison;
import com.justtennis.notifier.NotifierMessageLogger;

public class SaisonProvider extends AbstractContentProvider {

    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID + ".provider.saison";
    public static final String CONTENT_PROVIDER_MIME = "vnd.android.cursor.item/" + CONTENT_AUTHORITY;
    public static final Uri CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private GenericJustTennisDBHelper dbHelper;
    private GenericDBDataSource dbDataSource;

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
        dbDataSource = new DBSaisonDataSource(getContext(), NotifierMessageLogger.getInstance());
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
}
