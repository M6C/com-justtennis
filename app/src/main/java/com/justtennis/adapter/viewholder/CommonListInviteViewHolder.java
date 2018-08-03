package com.justtennis.adapter.viewholder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.justtennis.ApplicationConfig;
import com.justtennis.R;
import com.justtennis.adapter.manager.InviteViewManager;
import com.justtennis.adapter.manager.RankingViewManager;
import com.justtennis.db.service.ScoreSetService;
import com.justtennis.domain.Invite;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.parser.LocationParser;

import java.text.SimpleDateFormat;

public class CommonListInviteViewHolder extends CommonListViewHolder<Invite> {

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private Context context;
    private ImageView ivStatus;
    private TextView tvPlayer;
    private TextView tvDate;
    private TextView tvScore;
    private TextView tvClubName;
    private LinearLayout llScore;
    private LinearLayout llClubName;
    private View vTypeEntrainement;
    private View vTypeMatch;

    protected CommonListInviteViewHolder(View itemView) {
        super(itemView);

        context = itemView.getContext().getApplicationContext();
        ivStatus = itemView.findViewById(R.id.iv_status);
        tvPlayer = itemView.findViewById(R.id.tv_player);
        tvDate = itemView.findViewById(R.id.tv_date);
        llScore = itemView.findViewById(R.id.ll_score);
        tvScore = itemView.findViewById(R.id.tv_score);
        llClubName = itemView.findViewById(R.id.ll_club_name);
        tvClubName = itemView.findViewById(R.id.tv_club_name);
        vTypeEntrainement = itemView.findViewById(R.id.tv_type_entrainement);
        vTypeMatch = itemView.findViewById(R.id.tv_type_match);
    }

    @Override
    public void showData(Invite invite) {

        NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
        LocationParser locationParser = LocationParser.getInstance(context, notifier);
        ScoreSetService scoreSetService = new ScoreSetService(context, notifier);
        RankingViewManager rankingViewManager = RankingViewManager.getInstance(context, notifier);
        InviteViewManager inviteViewManager = InviteViewManager.getInstance(context, notifier);

        inviteViewManager.manageStatusImage(ivStatus, invite);

        tvPlayer.setText(invite.getPlayer() == null ? "" : Html.fromHtml("<b>" + invite.getPlayer().getFirstName() + "</b> " + invite.getPlayer().getLastName()));
        tvDate.setText(invite.getDate() == null ? "" : sdf.format(invite.getDate()));

        rankingViewManager.manageRanking(itemView, invite, true);
        rankingViewManager.manageRanking(itemView, invite, false);

        int rankingVisibility = itemView.findViewById(R.id.tv_ranking).getVisibility() == View.VISIBLE ||
                itemView.findViewById(R.id.tv_ranking_estimate).getVisibility() == View.VISIBLE ?
                View.VISIBLE : View.GONE;

        itemView.findViewById(R.id.ll_ranking).setVisibility(rankingVisibility);

        if (ApplicationConfig.SHOW_ID) {
            tvPlayer.setText(tvPlayer.getText() + " [id:" + invite.getPlayer().getId() + "|idExt:" + invite.getPlayer().getIdExternal() + "]");
            tvDate.setText(tvDate.getText() + " [id:" + invite.getId() + "|idExt:" + invite.getIdExternal() + "]");
        }

        initializeLocation(locationParser, invite, tvClubName, llClubName);

        String textScore = scoreSetService.buildTextScore(invite);
        if (textScore != null) {
            llScore.setVisibility(View.VISIBLE);
            tvScore.setText(Html.fromHtml(textScore));
        } else {
            llScore.setVisibility(View.GONE);
        }
//		int iRessource = R.drawable.check_yellow;
//		switch(invite.getStatus()) {
//			case ACCEPT:
//				iRessource = R.drawable.check_green;
//				break;
//			case REFUSE:
//				iRessource = R.drawable.check_red;
//				break;
//			default:
//		}
//		ivStatus.setImageDrawable(activity.getResources().getDrawable(iRessource));
        switch (invite.getScoreResult()) {
            case VICTORY:
                break;
            case DEFEAT:
                break;
            default:
        }

        if (vTypeEntrainement != null && vTypeMatch != null) {
            switch (invite.getType()) {
                case COMPETITION:
                    vTypeEntrainement.setVisibility(View.GONE);
                    vTypeMatch.setVisibility(View.VISIBLE);
                    break;
                case TRAINING:
                default:
                    vTypeEntrainement.setVisibility(View.VISIBLE);
                    vTypeMatch.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private static void initializeLocation(LocationParser locationParser, Invite v, TextView clubName, LinearLayout llClubName) {
        String[] address = locationParser.toAddress(v);
        if (address != null) {
            clubName.setText(address[0]);
            llClubName.setVisibility(View.VISIBLE);
        } else {
            llClubName.setVisibility(View.GONE);
        }
    }
}
