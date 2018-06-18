package com.justtennis.adapter.viewholder;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
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

    private Context context;
    private final LocationParser locationParser;
    private final RankingViewManager rankingViewManager;
    private final ContactManager contactManager;
    private ImageView imagePlayer;
    private ImageView imageDelete;
    private TextView name;
    private TextView clubName;

    protected ListPlayerViewHolder(View itemView) {
        super(itemView);

        context = itemView.getContext().getApplicationContext();
        NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
        locationParser = LocationParser.getInstance(context, notifier);
        rankingViewManager = RankingViewManager.getInstance(context, notifier);
        contactManager = ContactManager.getInstance(context);

        imagePlayer = itemView.findViewById(R.id.iv_player);
        imageDelete = itemView.findViewById(R.id.iv_delete);
        name = itemView.findViewById(R.id.tv_name);
        clubName = itemView.findViewById(R.id.tv_club_name);
    }

    public static ListPlayerViewHolder build(View view) {
        return new ListPlayerViewHolder(view);
    }

    @Override
    public void showData(Player player) {

        boolean isUnknownPlayer = PlayerService.isUnknownPlayer(player);
        int iVisibility = isUnknownPlayer ? View.GONE : View.VISIBLE;

        imageDelete.setVisibility(iVisibility);
        imageDelete.setOnClickListener(v -> RxListPlayer.publish(RxListPlayer.SUBJECT_ON_CLICK_DELETE_ITEM, v));

        imagePlayer.setTag(player);
        imageDelete.setTag(player);
        name.setText(Html.fromHtml("<b>" + player.getFirstName() + "</b> " + player.getLastName()));

        rankingViewManager.manageRanking(itemView, player, true);

        initializeLocation(player, clubName);

        if (player.getIdGoogle()!=null && player.getIdGoogle() > 0L) {
            imagePlayer.setImageBitmap(contactManager.getPhoto(player.getIdGoogle()));
        }
        else {
            imagePlayer.setImageResource(R.drawable.player_unknow_2);
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
