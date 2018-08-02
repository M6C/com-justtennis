package com.justtennis.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.justtennis.ApplicationConfig;
import com.justtennis.adapter.manager.RankingViewManager;
import com.justtennis.adapter.viewholder.InviteViewHolder;
import com.justtennis.db.service.ScoreSetService;
import com.justtennis.domain.Invite;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.parser.LocationParser;
import com.justtennis.ui.common.CommonEnum.LIST_VIEW_MODE;

import java.text.SimpleDateFormat;

public class InviteViewHelper {

    @SuppressLint("SimpleDateFormat")
    private final static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public static void initializeView(Context context, InviteViewHolder viewHolder, Invite invite, LIST_VIEW_MODE mode) {

        NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
        LocationParser locationParser = LocationParser.getInstance(context, notifier);
        ScoreSetService scoreSetService = new ScoreSetService(context, notifier);
        RankingViewManager rankingViewManager = RankingViewManager.getInstance(context, notifier);

        viewHolder.tvPlayer.setText(invite.getPlayer() == null ? "" : Html.fromHtml("<b>" + invite.getPlayer().getFirstName() + "</b> " + invite.getPlayer().getLastName()));
        viewHolder.tvDate.setText(invite.getDate() == null ? "" : sdf.format(invite.getDate()));
        viewHolder.imageDelete.setTag(invite);

        rankingViewManager.manageRanking(viewHolder.itemView, invite, true);

        if (ApplicationConfig.SHOW_ID) {
            viewHolder.tvPlayer.setText(viewHolder.tvPlayer.getText() + " [id:" + invite.getPlayer().getId() + "|idExt:" + invite.getPlayer().getIdExternal() + "]");
            viewHolder.tvDate.setText(viewHolder.tvDate.getText() + " [id:" + invite.getId() + "|idExt:" + invite.getIdExternal() + "]");
        }

        initializeLocation(locationParser, invite, viewHolder.tvClubName);

        String textScore = scoreSetService.buildTextScore(invite);
        if (textScore != null) {
            viewHolder.tvScore.setVisibility(View.VISIBLE);
            viewHolder.tvScore.setText(Html.fromHtml(textScore));
        } else {
            viewHolder.tvScore.setVisibility(View.GONE);
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
                viewHolder.imageDelete.setVisibility(View.GONE);
                break;
            case MODIFY:
            default:
                viewHolder.imageDelete.setVisibility(View.VISIBLE);
                break;
        }

        switch (invite.getType()) {
            case COMPETITION:
                viewHolder.vTypeEntrainement.setVisibility(View.GONE);
                viewHolder.vTypeMatch.setVisibility(View.VISIBLE);
                break;
            case TRAINING:
            default:
                viewHolder.vTypeEntrainement.setVisibility(View.VISIBLE);
                viewHolder.vTypeMatch.setVisibility(View.GONE);
                break;
        }
    }

    public static void initializeLocation(LocationParser locationParser, Invite v, TextView clubName) {
        String[] address = locationParser.toAddress(v);
        if (address != null) {
            clubName.setText(address[0]);
            clubName.setVisibility(View.VISIBLE);
        } else {
            clubName.setVisibility(View.GONE);
        }
    }
}