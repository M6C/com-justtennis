package com.justtennis.activity.notifier;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.justtennis.R;
import com.justtennis.activity.MainActivity;
import com.justtennis.domain.ComputeDataRanking;
import com.justtennis.drawer.adapter.notifier.INavigationDrawerNotifer;

public final class NavigationDrawerTrainingNotifer implements INavigationDrawerNotifer {

    private MainActivity mainActivity;
    private TextView tvMatchValue;
    private TextView tvMatchMax;
    private ProgressBar pbMatch;

    public NavigationDrawerTrainingNotifer(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onCreateView(View view) {
        tvMatchValue = (TextView) view.findViewById(R.id.tv_match_value);
        tvMatchMax = (TextView) view.findViewById(R.id.tv_match_max);
        pbMatch = (ProgressBar) view.findViewById(R.id.pb_match);

        initiazeView(view);
    }

    @Override
    public void onUpdateView(View view) {
        initiazeView(view);
    }

    private void initiazeView(View view) {
        ComputeDataRanking dataRanking = mainActivity.getBusiness().getDataRanking();

        tvMatchMax.setText(Integer.toString(dataRanking.getNbMatch()));
        tvMatchValue.setText(Integer.toString(dataRanking.getNbVictoryCalculate()));
        pbMatch.setMax(dataRanking.getNbMatch());
        pbMatch.setProgress(dataRanking.getNbVictoryCalculate());
    }
}
