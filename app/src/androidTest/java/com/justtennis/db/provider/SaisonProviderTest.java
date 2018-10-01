package com.justtennis.db.provider;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ProviderTestCase2;
import android.test.RenamingDelegatingContext;
import android.util.Log;

import com.justtennis.db.sqlite.helper.DBSaisonHelper;
import com.justtennis.tool.DBFeedTool;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SaisonProviderTest extends ProviderTestCase2<SaisonProvider> {

    private static final String TAG = SaisonProviderTest.class.getName();

    private static final String TEST_FILE_PREFIX = "test_";

    public SaisonProviderTest() {
        super(SaisonProvider.class, SaisonProvider.CONTENT_AUTHORITY);
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        setContext(InstrumentationRegistry.getTargetContext());

        RenamingDelegatingContext context = new RenamingDelegatingContext(getMockContext(), TEST_FILE_PREFIX);
        DBFeedTool.getInstance(context).feed();
    }

    @Test
    public void query() {
        ContentResolver contentResolver = getMockContext().getContentResolver();
        assertNotNull(contentResolver);

        Uri mContacts = SaisonProvider.CONTENT_URI;
        String[] columns = new String[]{DBSaisonHelper.COLUMN_ID, DBSaisonHelper.COLUMN_NAME, DBSaisonHelper.COLUMN_BEGIN, DBSaisonHelper.COLUMN_END, DBSaisonHelper.COLUMN_ACTIVE};
        Cursor cur = getMockContentResolver().query(mContacts, columns, null, null, null);
        assertNotNull(cur);
        Log.i(TAG, "query count:" + cur.getCount());
        assertEquals(0, cur.getCount());

        if (cur.moveToFirst()) {
            StringBuilder msg = new StringBuilder();
            do {
                for (String col : columns) {
                    msg.append(col).append(":").append(cur.getString(cur.getColumnIndex(col))).append(" ");
                }
            } while (cur.moveToNext());
            Log.i(TAG, "query result:" + msg.toString());
        }
    }
}