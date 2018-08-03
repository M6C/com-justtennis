package com.justtennis.adapter.viewholder;

import android.view.View;
import android.widget.TextView;

import com.justtennis.R;
import com.justtennis.domain.Invite;

public class ComputeListInviteViewHolder extends CommonListInviteViewHolder {

    private TextView tvPoint;
    private TextView tvBonusPoint;

    private ComputeListInviteViewHolder(View itemView) {
        super(itemView);
        tvPoint = itemView.findViewById(R.id.tv_point);
        tvBonusPoint = itemView.findViewById(R.id.tv_bonus_point);
    }

    public static ComputeListInviteViewHolder build(View view) {
        return new ComputeListInviteViewHolder(view);
    }

    @Override
    public void showData(Invite invite) {
        super.showData(invite);
        tvPoint.setText(invite.getPoint() > 0 ? ""+invite.getPoint() : "");
        if (tvBonusPoint != null) {
            tvBonusPoint.setText(invite.getBonusPoint() > 0 ? "" + invite.getBonusPoint() : "");
        }
    }
}