package com.justtennis.listener.ok;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.sqlite.SQLiteDatabase;

import com.cameleon.common.android.db.sqlite.helper.GenericDBHelper;
import com.justtennis.db.DBDictionary;
import com.justtennis.db.sqlite.helper.DBAddressHelper;
import com.justtennis.db.sqlite.helper.DBBonusHelper;
import com.justtennis.db.sqlite.helper.DBClubHelper;
import com.justtennis.db.sqlite.helper.DBInviteHelper;
import com.justtennis.db.sqlite.helper.DBMessageHelper;
import com.justtennis.db.sqlite.helper.DBPlayerHelper;
import com.justtennis.db.sqlite.helper.DBRankingHelper;
import com.justtennis.db.sqlite.helper.DBSaisonHelper;
import com.justtennis.db.sqlite.helper.DBScoreSetHelper;
import com.justtennis.db.sqlite.helper.DBTournamentHelper;
import com.justtennis.db.sqlite.helper.DBUserHelper;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.tool.DBFeedTool;
import com.justtennis.ui.rxjava.RxNavigationDrawer;

import java.io.IOException;

import io.reactivex.Observable;

public class OnClickDBRecreateListenerOk implements OnClickListener {

	private Activity context;

	public OnClickDBRecreateListenerOk(Activity context) {
		this.context = context;
	}

	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
			for(GenericDBHelper helper : DBDictionary.getInstance(context.getApplicationContext(), notifier).getListHelper()) {
				helper.dropTable(helper.getWritableDatabase());
			}

			for(GenericDBHelper helper : DBDictionary.getInstance(context.getApplicationContext(), notifier).getListHelper()) {
				// Score is in same DB as Invite
				if (!(helper instanceof DBScoreSetHelper)) {
					helper.onCreate(helper.getWritableDatabase());
				}
			}

			DBFeedTool.getInstance(context.getApplicationContext()).doFeed();

			RxNavigationDrawer.publish(RxNavigationDrawer.SUBJECT_DB_RESTORED, Observable.empty());

		}
	}

}