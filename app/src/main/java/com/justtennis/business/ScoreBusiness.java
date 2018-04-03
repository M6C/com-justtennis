package com.justtennis.business;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.activity.ScoreActivity;

public class ScoreBusiness {

	@SuppressWarnings("unused")
	private static final String TAG = ScoreBusiness.class.getSimpleName();

	private String[][] scores;

	public ScoreBusiness(Context context, INotifierMessage notificationMessage) {
	}

	public void initializeData(Intent intent) {
		initializeDataScore(intent.getExtras());
	}

	public void initializeData(Bundle savedInstanceState) {
		initializeDataScore(savedInstanceState);
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
		if (data != null && data.containsKey(ScoreActivity.EXTRA_SCORE)) {
			Object[] d = (Object[]) data.getSerializable(ScoreActivity.EXTRA_SCORE);
			if (d != null && d.length > 0) {
				scores = new String[d.length][];
				for(int i=0 ; i<d.length ; i++) {
					scores[i] = (String[]) d[i];
				}
			}
		}
	}
}