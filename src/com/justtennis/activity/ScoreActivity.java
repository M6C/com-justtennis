package com.justtennis.activity;

import java.io.Serializable;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.justtennis.R;
import com.justtennis.business.ScoreBusiness;
import com.justtennis.listener.action.TextWatcherFieldScoreSetBold;
import com.justtennis.manager.TypeManager;
import com.justtennis.notifier.NotifierMessageLogger;

public class ScoreActivity extends GenericActivity {

	private static final String TAG = ScoreActivity.class.getSimpleName();

	public enum MODE {
		INVITE_DEMANDE,
		INVITE_CONFIRM
	};
	public static final String EXTRA_SCORE = "SCORE";

	private ScoreBusiness business;

	private EditText etScore11;
	private EditText etScore21;
	private EditText etScore12;
	private EditText etScore22;
	private EditText etScore13;
	private EditText etScore23;
	private EditText etScore14;
	private EditText etScore24;
	private EditText etScore15;
	private EditText etScore25;

	private Bundle savedInstanceState;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (this.savedInstanceState==null) {
			this.savedInstanceState = savedInstanceState;
		}

		setContentView(R.layout.score);

		etScore11 = (EditText)findViewById(R.id.et_score1_1);
		etScore21 = (EditText)findViewById(R.id.et_score2_1);
		etScore12 = (EditText)findViewById(R.id.et_score1_2);
		etScore22 = (EditText)findViewById(R.id.et_score2_2);
		etScore13 = (EditText)findViewById(R.id.et_score1_3);
		etScore23 = (EditText)findViewById(R.id.et_score2_3);
		etScore14 = (EditText)findViewById(R.id.et_score1_4);
		etScore24 = (EditText)findViewById(R.id.et_score2_4);
		etScore15 = (EditText)findViewById(R.id.et_score1_5);
		etScore25 = (EditText)findViewById(R.id.et_score2_5);

		etScore11.addTextChangedListener(new TextWatcherFieldScoreSetBold(etScore11, etScore21));
		etScore21.addTextChangedListener(new TextWatcherFieldScoreSetBold(etScore21, etScore11));
		etScore12.addTextChangedListener(new TextWatcherFieldScoreSetBold(etScore12, etScore22));
		etScore22.addTextChangedListener(new TextWatcherFieldScoreSetBold(etScore22, etScore12));
		etScore13.addTextChangedListener(new TextWatcherFieldScoreSetBold(etScore13, etScore23));
		etScore23.addTextChangedListener(new TextWatcherFieldScoreSetBold(etScore23, etScore13));
		etScore14.addTextChangedListener(new TextWatcherFieldScoreSetBold(etScore14, etScore24));
		etScore24.addTextChangedListener(new TextWatcherFieldScoreSetBold(etScore24, etScore14));
		etScore15.addTextChangedListener(new TextWatcherFieldScoreSetBold(etScore15, etScore25));
		etScore25.addTextChangedListener(new TextWatcherFieldScoreSetBold(etScore25, etScore15));

		business = new ScoreBusiness(this, NotifierMessageLogger.getInstance());

		TypeManager.getInstance().initializeActivity(findViewById(R.id.layout_main), false);
	}

	@Override
	protected void onResume() {
		super.onResume();
		initializeData();
	}

	@Override
	public void onBackPressed() {
		onClickCancel(null);
		super.onBackPressed();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		business.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

	public void onClickOk(View view) {
		saveScores();
		Intent intent = new Intent();
		intent.putExtra(EXTRA_SCORE, (Serializable)business.getScores());
		setResult(RESULT_OK, intent);
		finish();
	}
	
	public void onClickCancel(View view) {
		setResult(RESULT_CANCELED);
		finish();
	}

	private void initializeData() {
		Intent intent = getIntent();
		if (savedInstanceState!=null) {
			business.initializeData(savedInstanceState);
			savedInstanceState = null;
		}
		else {
			business.initializeData(intent);
		}
		initializeDataScore();
	}

	private void initializeDataScore() {
		Log.d(TAG, "initializeDataScore");

		String[][] scores = business.getScores();
		if (scores!=null) {
			int len = scores.length;
			for(int row = 1 ; row <= len ; row++) {
				String[] score = scores[row-1];
				switch(row) {
					case 1:
					default: {
						etScore11.setText(score[0]);
						etScore21.setText(score[1]);
					}
					break;
					case 2: {
						etScore12.setText(score[0]);
						etScore22.setText(score[1]);
					}
					break;
					case 3: {
						etScore13.setText(score[0]);
						etScore23.setText(score[1]);
					}
					break;
					case 4: {
						etScore14.setText(score[0]);
						etScore24.setText(score[1]);
					}
					break;
					case 5: {
						etScore15.setText(score[0]);
						etScore25.setText(score[1]);
					}
					break;
				}
			}
		}
	}

	private void saveScores() {
		String[][] scores = new String[][]{
				{etScore11.getText().toString(), etScore21.getText().toString()},
				{etScore12.getText().toString(), etScore22.getText().toString()},
				{etScore13.getText().toString(), etScore23.getText().toString()},
				{etScore14.getText().toString(), etScore24.getText().toString()},
				{etScore15.getText().toString(), etScore25.getText().toString()}
			};
		business.setScores(scores);
	}
}