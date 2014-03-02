package com.justtennis.listener.ok;

import java.io.IOException;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.justtennis.db.sqlite.helper.DBAddressHelper;
import com.justtennis.db.sqlite.helper.DBClubHelper;
import com.justtennis.db.sqlite.helper.DBInviteHelper;
import com.justtennis.db.sqlite.helper.DBMessageHelper;
import com.justtennis.db.sqlite.helper.DBPlayerHelper;
import com.justtennis.db.sqlite.helper.DBRankingHelper;
import com.justtennis.db.sqlite.helper.DBScoreSetHelper;
import com.justtennis.db.sqlite.helper.DBTournamentHelper;
import com.justtennis.db.sqlite.helper.DBUserHelper;
import com.justtennis.db.sqlite.helper.GenericDBHelper;
import com.justtennis.notifier.NotifierMessageLogger;

public class OnClickDBBackupListenerOk implements OnClickListener {

	private Activity context;

	public OnClickDBBackupListenerOk(Activity context) {
		this.context = context;
	}

	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
			GenericDBHelper[] listHelper  = new GenericDBHelper[] {
					new DBAddressHelper(context, notifier),
					new DBClubHelper(context, notifier),
					new DBInviteHelper(context, notifier),
					new DBMessageHelper(context, notifier),
					new DBPlayerHelper(context, notifier),
					new DBRankingHelper(context, notifier),
					new DBScoreSetHelper(context, notifier),
					new DBTournamentHelper(context, notifier),
					new DBUserHelper(context, notifier)
			};
			
			for(GenericDBHelper helper : listHelper) {
				try {
					helper.backupDbToSdcard();
				} catch (IOException e) {
					notifier.notifyError(e);
				}
			}
		}
	}

}