package com.justtennis.ui.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.justtennis.R;
import com.justtennis.databinding.FragmentScoreBinding;
import com.justtennis.listener.action.TextWatcherFieldScoreSetBold;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.tool.FragmentTool;
import com.justtennis.ui.business.ScoreBusiness;
import com.justtennis.ui.rxjava.RxFragment;
import com.justtennis.ui.viewmodel.ScoreViewModel;


public class ScoreFragment extends Fragment {

    public static final String TAG = ScoreFragment.class.getSimpleName();

    protected static final String EXTRA_VIEW_MODEL = "EXTRA_VIEW_MODEL";

    private FragmentActivity activity;
	private ScoreBusiness business;
	private FragmentScoreBinding binding;
    private View rootView;
    private ScoreViewModel model;

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

    public static ScoreFragment build(String[][] score) {
		ScoreFragment fragment = new ScoreFragment();
		Bundle args = new Bundle();
		args.putSerializable(ScoreBusiness.EXTRA_DATA, score);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		activity = getActivity();
		assert activity != null;
		Context context = activity.getApplicationContext();
		assert context != null;

        Bundle bundle = (savedInstanceState != null) ? savedInstanceState : getArguments();
        if (bundle != null) {
            model = (ScoreViewModel) bundle.getSerializable(EXTRA_VIEW_MODEL);
        }

		business = new ScoreBusiness(context, NotifierMessageLogger.getInstance());
		business.onCreate(this);
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_score, container, false);

        rootView = binding.getRoot();

        initializeView();

        initializeDataScore();

        initializeListener();
        initializeFab();

        return rootView;
	}

    @Override
    public void onResume() {
        super.onResume();
        //RxFragment.publish(RxFragment.SUBJECT_ON_SHOW, TAG);
    }

    private void initializeFab() {
		FragmentTool.initializeFabDrawable(activity, FragmentTool.INIT_FAB_IMAGE.VALIDATE);
		FragmentTool.onClickFab(activity, v -> this.onClickValidate());
	}

	private void onClickValidate() {
        saveScores();

        model.select(business.getScores());

		finish();
	}

	private void initializeView() {
        etScore11 = rootView.findViewById(R.id.et_score1_1);
        etScore21 = rootView.findViewById(R.id.et_score2_1);
        etScore12 = rootView.findViewById(R.id.et_score1_2);
        etScore22 = rootView.findViewById(R.id.et_score2_2);
        etScore13 = rootView.findViewById(R.id.et_score1_3);
        etScore23 = rootView.findViewById(R.id.et_score2_3);
        etScore14 = rootView.findViewById(R.id.et_score1_4);
        etScore24 = rootView.findViewById(R.id.et_score2_4);
        etScore15 = rootView.findViewById(R.id.et_score1_5);
        etScore25 = rootView.findViewById(R.id.et_score2_5);
    }

    private void initializeListener() {
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

    private void finish() {
        FragmentTool.finish(activity);
    }
}