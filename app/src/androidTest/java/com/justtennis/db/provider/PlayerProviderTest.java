package com.justtennis.db.provider;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ProviderTestCase2;
import android.test.RenamingDelegatingContext;
import android.util.Log;

import com.justtennis.db.sqlite.helper.DBPlayerHelper;
import com.justtennis.tool.DBFeedTool;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PlayerProviderTest extends ProviderTestCase2<InviteProvider> {

    private static final String TAG = PlayerProviderTest.class.getName();

    private static final String TEST_FILE_PREFIX = "test_";

    public PlayerProviderTest() {
        super(InviteProvider.class, InviteProvider.CONTENT_AUTHORITY);
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
        String[] columns = new String[]{DBPlayerHelper.COLUMN_ID, DBPlayerHelper.COLUMN_FIRSTNAME, DBPlayerHelper.COLUMN_LASTNAME, DBPlayerHelper.COLUMN_BIRTHDAY, DBPlayerHelper.COLUMN_TYPE, DBPlayerHelper.COLUMN_ID_SAISON, DBPlayerHelper.COLUMN_ID_CLUB};
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