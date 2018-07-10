package com.justtennis.adapter.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.justtennis.ApplicationConfig;
import com.justtennis.R;
import com.justtennis.domain.Club;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.parser.LocationParser;
import com.justtennis.ui.rxjava.RxListPlayer;

import java.text.MessageFormat;

public class ListClubViewHolder extends CommonListViewHolder<Club> {

    private Context context;
    private final LocationParser locationParser;
    private LinearLayout llDelete;
    private TextView name;
    private TextView clubName;

    private ListClubViewHolder(View itemView) {
        super(itemView);

        context = itemView.getContext().getApplicationContext();
        NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
        locationParser = LocationParser.getInstance(context, notifier);

        llDelete = itemView.findViewById(R.id.ll_delete);
        name = itemView.findViewById(R.id.tv_name);
        clubName = itemView.findViewById(R.id.tv_club_name);
    }

    public static ListClubViewHolder build(View view) {
        return new ListClubViewHolder(view);
    }

    @Override
    public void showData(Club club) {

        int iVisibility = View.VISIBLE;

        llDelete.setVisibility(iVisibility);
        llDelete.setOnClickListener(v -> RxListPlayer.publish(RxListPlayer.SUBJECT_ON_CLICK_DELETE_ITEM, v));

        llDelete.setTag(club);
        name.setText(club.getName());

        initializeLocation(club, clubName);

        if (ApplicationConfig.SHOW_ID) {
            name.setText(String.format(context.getString(R.string.list_club_item_name_with_id), String.valueOf(name.getText()), club.getId()));
        }
    }

    private void initializeLocation(final Club v, TextView clubName) {
        String[] address = locationParser.toAddress(v);
        if (address != null && (!address[1].isEmpty() || !address[2].isEmpty())) {
            clubName.setText(MessageFormat.format("{0} {1}", address[1], address[2]).trim());
            clubName.setVisibility(View.VISIBLE);
        } else {
            clubName.setText("");
            clubName.setVisibility(View.INVISIBLE);
        }
    }
}
