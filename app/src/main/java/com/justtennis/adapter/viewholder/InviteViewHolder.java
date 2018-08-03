package com.justtennis.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.justtennis.R;

public class InviteViewHolder extends RecyclerView.ViewHolder {
    public TextView tvPlayer;
    public TextView tvDate;
    public TextView tvScore;
    public TextView tvClubName;
    public ImageView imageDelete;
    public View vTypeEntrainement;
    public View vTypeMatch;

    public InviteViewHolder(View itemView) {
        super(itemView);
        tvPlayer = itemView.findViewById(R.id.tv_player);
        tvDate = itemView.findViewById(R.id.tv_date);
        tvScore = itemView.findViewById(R.id.tv_score);
        tvClubName = itemView.findViewById(R.id.tv_club_name);
        imageDelete = itemView.findViewById(R.id.iv_delete);
        vTypeEntrainement = itemView.findViewById(R.id.tv_type_entrainement);
        vTypeMatch = itemView.findViewById(R.id.tv_type_match);
    }
}
