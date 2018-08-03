package com.justtennis.activity.notifier;

import android.view.View;
import android.widget.TextView;

import com.justtennis.R;
import com.justtennis.activity.MainActivity;
import com.justtennis.adapter.manager.RankingListManager;
import com.justtennis.domain.Ranking;
import com.justtennis.domain.User;
import com.justtennis.drawer.adapter.notifier.INavigationDrawerNotifer;

public final class NavigationDrawerUserNotifer implements INavigationDrawerNotifer {

    private MainActivity mainActivity;
    private RankingListManager rankingListManager;
    private TextView tvName;

    public NavigationDrawerUserNotifer(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onCreateView(View view) {
        rankingListManager = RankingListManager.getInstance(mainActivity, mainActivity);
        tvName = (TextView) view.findViewById(R.id.tv_title);

        User user = mainActivity.getBusiness().getUser();
        if (user != null) {
            tvName.setText(user.getFullName());
        }
        manageRanking(mainActivity, view, user, true);
        manageRanking(mainActivity, view, user, false);
    }

    @Override
    public void onUpdateView(View view) {
//			User user = business.getUser();
//			tvName.setText(user.getFullName());
//			rankingListManager.initializeRankingSpinner(view, user, true);
//			rankingListManager.initializeRankingSpinner(view, user, false);
    }

    private void manageRanking(MainActivity mainActivity, View view, User user, final boolean estimate) {
        RankingListManager.IRankingListListener listener = new RankingListManager.IRankingListListener() {
            @Override
            public void onRankingSelected(Ranking ranking) {
                Long oldId = null;
                User user = mainActivity.getBusiness().getUser();
                if (user == null) {
                    return;
                }
                if (estimate) {
                    oldId = user.getIdRankingEstimate();
                    if (ranking.equals(mainActivity.getBusiness().getRankingNC())) {
                        user.setIdRankingEstimate(null);
                    } else {
                        user.setIdRankingEstimate(ranking.getId());
                    }
                } else {
                    oldId = user.getIdRanking();
                    if (ranking.equals(mainActivity.getBusiness().getRankingNC())) {
                        user.setIdRanking(null);
                    } else {
                        user.setIdRanking(ranking.getId());
                    }
                }
                if (oldId != null && !oldId.equals(ranking.getId())) {
                    mainActivity.refreshDrawer();
                }
            }
        };
        rankingListManager.manageRanking(mainActivity, view, listener, user, estimate);
    }
}
