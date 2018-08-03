package com.justtennis.adapter.viewholder;

import android.view.View;
import android.widget.LinearLayout;

import com.justtennis.R;
import com.justtennis.domain.Invite;
import com.justtennis.ui.rxjava.RxListInvite;

public class ListInviteViewHolder extends CommonListInviteViewHolder {

    private LinearLayout llDelete;
    private View vTypeEntrainement;
    private View vTypeMatch;

    private ListInviteViewHolder(View itemView) {
        super(itemView);

        llDelete = itemView.findViewById(R.id.ll_delete);
        vTypeEntrainement = itemView.findViewById(R.id.tv_type_entrainement);
        vTypeMatch = itemView.findViewById(R.id.tv_type_match);
    }

    public static ListInviteViewHolder build(View view) {
        return new ListInviteViewHolder(view);
    }

    @Override
    public void showData(Invite invite) {
        super.showData(invite);

        llDelete.setTag(invite);
        llDelete.setOnClickListener(v -> RxListInvite.publish(RxListInvite.SUBJECT_ON_CLICK_DELETE_ITEM, v));

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
}
