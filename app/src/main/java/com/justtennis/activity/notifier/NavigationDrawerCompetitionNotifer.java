package com.justtennis.activity.notifier;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.justtennis.R;
import com.justtennis.activity.MainActivity;
import com.justtennis.domain.ComputeDataRanking;
import com.justtennis.drawer.adapter.notifier.INavigationDrawerNotifer;

public final class NavigationDrawerCompetitionNotifer implements INavigationDrawerNotifer {

    private MainActivity mainActivity;
    private TextView tvMatchValue;
    private TextView tvMatchMax;
    private ProgressBar pbMatch;
    private TextView tvPointValue;
    private TextView tvPointMax;
    private ProgressBar pbPoint;

    public NavigationDrawerCompetitionNotifer(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onCreateView(View view) {
        tvMatchValue = (TextView) view.findViewById(R.id.tv_match_value);
        tvMatchMax = (TextView) view.findViewById(R.id.tv_match_max);
        pbMatch = (ProgressBar) view.findViewById(R.id.pb_match);
        tvPointValue = (TextView) view.findViewById(R.id.tv_point_value);
        tvPointMax = (TextView) view.findViewById(R.id.tv_point_max);
        pbPoint = (ProgressBar) view.findViewById(R.id.pb_point);

        initiazeView(view);
    }

    @Override
    public void onUpdateView(View view) {
//			initiazeView(view);
    }

    private void initiazeView(View view) {
        ComputeDataRanking dataRanking = mainActivity.getBusiness().getDataRanking();
        int point = dataRanking.getPointCalculate() + dataRanking.getPointBonus();
        int nbVictory = dataRanking.getListInviteCalculed().size() + dataRanking.getListInviteNotUsed().size();

        tvMatchMax.setText(Integer.toString(dataRanking.getNbMatch()));
        tvMatchValue.setText(Integer.toString(nbVictory));
        pbMatch.setMax(dataRanking.getNbMatch());
        pbMatch.setProgress(nbVictory);
        tvPointMax.setText(Integer.toString(dataRanking.getPointObjectif()));
        tvPointValue.setText(Integer.toString(point));
        pbPoint.setMax(point > dataRanking.getPointObjectif() ? point : dataRanking.getPointObjectif());
        pbPoint.setProgress(point);
    }
}
