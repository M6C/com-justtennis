package com.justtennis.adapter.manager;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.justtennis.R;
import com.justtennis.domain.Invite;
import com.justtennis.domain.ScoreSet;
import com.justtennis.notifier.NotifierMessageLogger;

public class InviteViewManager {

	private static InviteViewManager instance;


	private InviteViewManager(Context context, NotifierMessageLogger notifier) {
	}

	public static InviteViewManager getInstance(Context context, NotifierMessageLogger notifier) {
		if (instance == null) {
			instance = new InviteViewManager(context, notifier);
		}
		return instance;
	}

	public void manageStatusImage(ImageView ivStatus, Invite invite) {
		if (Invite.SCORE_RESULT.DEFEAT.equals(invite.getScoreResult())) {
			ivStatus.setImageResource(R.drawable.ic_demon_01);
		} else if (Invite.SCORE_RESULT.VICTORY.equals(invite.getScoreResult())) {
			ivStatus.setImageResource(R.drawable.ic_podium_01);
		} else if (invite.getListScoreSet()!=null && !invite.getListScoreSet().isEmpty()) {
			ScoreSet score = invite.getListScoreSet().get(invite.getListScoreSet().size() - 1);
			if (score.getValue1().compareTo(score.getValue2()) > 0) {
				ivStatus.setImageResource(R.drawable.ic_podium_01);
			} else {
				ivStatus.setImageResource(R.drawable.ic_demon_01);
			}
		} else {
			ivStatus.setImageResource(R.drawable.ic_tennis_ball_02);
		}
	}
}