package com.justtennis.db.provider;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ProviderTestCase2;
import android.test.RenamingDelegatingContext;
import android.util.Log;

import com.justtennis.db.sqlite.helper.DBInviteHelper;
import com.justtennis.tool.DBFeedTool;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class InviteProviderTest extends ProviderTestCase2<InviteProvider> {

    private static final String TAG = InviteProviderTest.class.getName();

    private static final String TEST_FILE_PREFIX = "test_";

    public InviteProviderTest() {
        super(InviteProvider.class, InviteProvider.CONTENT_AUTHORITY);
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        setContext(InstrumentationRegistry.getTargetContext());

        RenamingDelegatingContext context = new RenamingDelegatingContext(getMockContext(), TEST_FILE_PREFIX);
        DBFeedTool.getInstance(context).feed();

        //        NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
//        for(GenericDBHelper helper : DBDictionary.getInstance(getMockContext().getApplicationContext(), notifier).getListHelper()) {
//            helper.dropTable(helper.getWritableDatabase());
//        }
//
//        for(GenericDBHelper helper : DBDictionary.getInstance(getMockContext().getApplicationContext(), notifier).getListHelper()) {
//            // Score is in same DB as Invite
//            if (!(helper instanceof DBScoreSetHelper)) {
//                helper.onCreate(helper.getWritableDatabase());
//            }
//        }
//
//        DBFeedTool.getInstance(getMockContext().getApplicationContext()).doFeed();
    }

    @Test
    public void query() {
        ContentResolver contentResolver = getMockContext().getContentResolver();
        assertNotNull(contentResolver);

        Uri mContacts = InviteProvider.CONTENT_URI;
        String[] columns = new String[]{DBInviteHelper.COLUMN_ID, DBInviteHelper.COLUMN_ID_SAISON, DBInviteHelper.COLUMN_ID_CLUB, DBInviteHelper.COLUMN_ID_PLAYER, DBInviteHelper.COLUMN_TIME, DBInviteHelper.COLUMN_STATUS};
        Cursor cur = getMockContentResolver().query(mContacts, columns, null, null, null);
        assertNotNull(cur);
        Log.i(TAG, "query count:" + cur.getCount());
        assertEquals(0, cur.getCount());

        if (cur.moveToFirst()) {
            StringBuilder msg = new StringBuilder();
            do {
                for (String col : columns) {
                    if (msg.length() > 0) {
                        msg.append(" ");
                    }
                    msg.append(cur.getString(cur.getColumnIndex(col)));
                }
            } while (cur.moveToNext());
            Log.i(TAG, "query result:" + msg.toString());
        }
    }
}