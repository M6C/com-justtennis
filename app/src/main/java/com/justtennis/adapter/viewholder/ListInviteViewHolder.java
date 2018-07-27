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
import com.justtennis.ui.rxjava.RxListInvite;

import java.text.SimpleDateFormat;

public class ListInviteViewHolder extends CommonListViewHolder<Invite> {

    @SuppressLint("SimpleDateFormat")
    private final static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private Context context;
    private ImageView ivStatus;
    private TextView tvPlayer;
    private TextView tvDate;
    private TextView tvScore;
    private TextView tvClubName;
    private LinearLayout llDelete;
    private View vTypeEntrainement;
    private View vTypeMatch;

    private ListInviteViewHolder(View itemView) {
        super(itemView);

        context = itemView.getContext().getApplicationContext();
        ivStatus = itemView.findViewById(R.id.iv_status);
        tvPlayer = itemView.findViewById(R.id.tv_player);
        tvDate = itemView.findViewById(R.id.tv_date);
        tvScore = itemView.findViewById(R.id.tv_score);
        tvClubName = itemView.findViewById(R.id.tv_club_name);
        llDelete = itemView.findViewById(R.id.ll_delete);
        vTypeEntrainement = itemView.findViewById(R.id.tv_type_entrainement);
        vTypeMatch = itemView.findViewById(R.id.tv_type_match);
    }

    public static ListInviteViewHolder build(View view) {
        return new ListInviteViewHolder(view);
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
        llDelete.setTag(invite);
        llDelete.setOnClickListener(v -> RxListInvite.publish(RxListInvite.SUBJECT_ON_CLICK_DELETE_ITEM, v));

        rankingViewManager.manageRanking(itemView, invite, true);

        if (ApplicationConfig.SHOW_ID) {
            tvPlayer.setText(tvPlayer.getText() + " [id:" + invite.getPlayer().getId() + "|idExt:" + invite.getPlayer().getIdExternal() + "]");
            tvDate.setText(tvDate.getText() + " [id:" + invite.getId() + "|idExt:" + invite.getIdExternal() + "]");
        }

        initializeLocation(locationParser, invite, tvClubName);

        String textScore = scoreSetService.buildTextScore(invite);
        if (textScore != null) {
            tvScore.setVisibility(View.VISIBLE);
            tvScore.setText(Html.fromHtml(textScore));
        } else {
            tvScore.setVisibility(View.GONE);
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

        switch (mode) {
            case READ:
                llDelete.setVisibility(View.GONE);
                break;
            case MODIFY:
            default:
                llDelete.setVisibility(View.VISIBLE);
                break;
        }

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

    private static void initializeLocation(LocationParser locationParser, Invite v, TextView clubName) {
        String[] address = locationParser.toAddress(v);
        if (address != null) {
            clubName.setText(address[0]);
            clubName.setVisibility(View.VISIBLE);
        } else {
            clubName.setVisibility(View.GONE);
        }
    }
}
