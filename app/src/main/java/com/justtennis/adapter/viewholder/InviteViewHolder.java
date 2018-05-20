package com.justtennis.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.justtennis.R;
import com.justtennis.domain.Invite;

public class InviteViewHolder extends RecyclerView.ViewHolder {
    public Invite invite;
    public TextView tvPlayer;
    public TextView tvDate;
    public TextView tvScore;
    public TextView tvClubName;
    public ImageView imageDelete;
    public View vTypeEntrainement;
    public View vTypeMatch;

    public InviteViewHolder(View itemView) {
        super(itemView);
        tvPlayer = (TextView) itemView.findViewById(R.id.tv_player);
        tvDate = (TextView) itemView.findViewById(R.id.tv_date);
        tvScore = (TextView) itemView.findViewById(R.id.tv_score);
        tvClubName = (TextView) itemView.findViewById(R.id.tv_club_name);
        imageDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
        vTypeEntrainement = itemView.findViewById(R.id.tv_type_entrainement);
        vTypeMatch = itemView.findViewById(R.id.tv_type_match);
    }
}
