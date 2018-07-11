package com.justtennis.ui.business;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.activity.ScoreActivity;

public class ScoreBusiness {

	private static final String TAG = ScoreBusiness.class.getSimpleName();

	public static final String EXTRA_DATA = "EXTRA_DATA";

	private String[][] scores;

	public ScoreBusiness(Context context, INotifierMessage notificationMessage) {
	}

	public void onCreate(Fragment fragment) {
		Bundle bundle = fragment.getArguments();
		assert bundle != null;
		initializeDataScore(bundle);
	}


	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(ScoreActivity.EXTRA_SCORE, scores);
	}

	public String[][] getScores() {
		return scores;
	}

	public void setScores(String[][] scores) {
		this.scores = scores;
	}

	private void initializeDataScore(Bundle data) {
		if (data != null && data.containsKey(EXTRA_DATA)) {
			Object[] d = (Object[]) data.getSerializable(EXTRA_DATA);
			if (d != null && d.length > 0) {
				scores = new String[d.length][];
				for(int i=0 ; i<d.length ; i++) {
					scores[i] = (String[]) d[i];
				}
			}
		}
	}
}