package com.justtennis.db.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.ArrayMap;

import com.cameleon.common.android.db.sqlite.datasource.GenericDBDataSource;
import com.cameleon.common.android.model.GenericDBPojo;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractContentProvider<P extends GenericDBPojo<Long>> extends ContentProvider {

    protected abstract SQLiteOpenHelper getDbHelper();
    protected abstract GenericDBDataSource<P> getDbDataSource();

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Map<String, String> map;

        String[] columns = getDbDataSource().getAllColumns();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            map = Arrays.stream(columns).collect(Collectors.toMap(col -> col, col -> col));
        } else {
            map = new ArrayMap<>();
            for (String c : columns) {
                map.put(c, c);
            }
        }

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setProjectionMap(map);
        builder.setStrict(true);
        return builder.query(getDbHelper().getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
//        long id = getDbHelper().getWritableDatabase().insert(getDbHelper().TABLE_NAME, null, values);
//        return ContentUris.withAppendedId(uri, id);
        return null;
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
