package com.justtennis.adapter.viewholder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.justtennis.ApplicationConfig;
import com.justtennis.R;
import com.justtennis.adapter.manager.RankingViewManager;
import com.justtennis.db.service.PlayerService;
import com.justtennis.domain.Player;
import com.justtennis.manager.ContactManager;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.parser.LocationParser;
import com.justtennis.ui.rxjava.RxListPlayer;

public class ListPlayerViewHolder extends CommonListViewHolder<Player> {

    private Activity activity;
    private Context context;
    private final LocationParser locationParser;
    private final RankingViewManager rankingViewManager;
    private final ContactManager contactManager;
    private LinearLayout llDelete;
    private ImageView imagePlayer;
    private TextView name;
    private TextView clubName;

    private ListPlayerViewHolder(Activity activity, View itemView) {
        super(itemView);

        this.activity = activity;
        context = itemView.getContext().getApplicationContext();
        NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
        locationParser = LocationParser.getInstance(context, notifier);
        rankingViewManager = RankingViewManager.getInstance(context, notifier);
        contactManager = ContactManager.getInstance();

        llDelete = itemView.findViewById(R.id.ll_delete);
        imagePlayer = itemView.findViewById(R.id.iv_player);
        name = itemView.findViewById(R.id.tv_title);
        clubName = itemView.findViewById(R.id.tv_club_name);
    }

    public static ListPlayerViewHolder build(Activity activity, View view) {
        return new ListPlayerViewHolder(activity, view);
    }

    @Override
    public void showData(Player player) {

        boolean isUnknownPlayer = PlayerService.isUnknownPlayer(player);
        int iVisibility = isUnknownPlayer ? View.GONE : View.VISIBLE;

        llDelete.setVisibility(iVisibility);
        llDelete.setOnClickListener(v -> RxListPlayer.publish(RxListPlayer.SUBJECT_ON_CLICK_DELETE_ITEM, v));

        imagePlayer.setTag(player);
        llDelete.setTag(player);
        name.setText(Html.fromHtml("<b>" + player.getFirstName() + "</b> " + player.getLastName()));

        rankingViewManager.manageRanking(itemView, player, true);

        initializeLocation(player, clubName);

        boolean unknown = true;
        if (player.getIdGoogle()!=null && player.getIdGoogle() > 0L) {
            Bitmap bmp = contactManager.getPhoto(activity, player.getIdGoogle());
            if (bmp != null) {
                imagePlayer.setImageBitmap(bmp);
                unknown = false;
            }
        }
        if (unknown) {
            imagePlayer.setImageDrawable(PlayerService.getUnknownPlayerRandomRes(activity));
        }

        if (ApplicationConfig.SHOW_ID) {
            name.setText(String.format(context.getString(R.string.list_player_item_name_with_id), String.valueOf(name.getText()), player.getId(), player.getIdExternal()));
        }
    }

    private void initializeLocation(final Player v, TextView clubName) {
        String[] address = locationParser.toAddress(v);
        if (address != null) {
            clubName.setText(address[0]);
            clubName.setVisibility(View.VISIBLE);
        } else {
            clubName.setText("");
            clubName.setVisibility(View.INVISIBLE);
        }
    }
}
