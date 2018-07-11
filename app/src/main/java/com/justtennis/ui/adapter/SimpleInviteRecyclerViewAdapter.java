package com.justtennis.ui.adapter;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.justtennis.R;
import com.justtennis.activity.InviteActivity;
import com.justtennis.adapter.viewholder.InviteViewHolder;
import com.justtennis.domain.Invite;
import com.justtennis.helper.InviteViewHelper;
import com.justtennis.tool.FragmentTool;
import com.justtennis.ui.common.CommonEnum;
import com.justtennis.ui.fragment.InviteFragment;

import java.io.Serializable;
import java.util.List;

public class SimpleInviteRecyclerViewAdapter extends RecyclerView.Adapter<InviteViewHolder> {

    private final FragmentActivity mParentActivity;
    private final List<Invite> mValues;
    private final boolean mTwoPane;


    public SimpleInviteRecyclerViewAdapter(FragmentActivity parent, List<Invite> items, boolean twoPane) {
        mValues = items;
        mParentActivity = parent;
        mTwoPane = twoPane;
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            InviteViewHolder item = (InviteViewHolder) view.getTag();
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putSerializable(InviteActivity.EXTRA_INVITE, (Serializable)item.invite);
                arguments.putSerializable(InviteActivity.EXTRA_MODE, CommonEnum.INVITE_MODE.INVITE_DETAIL);
                InviteFragment fragment = new InviteFragment();
                fragment.setArguments(arguments);
                FragmentTool.replaceFragment(mParentActivity, fragment);
            } else {
                InviteFragment fragment = new InviteFragment();
                Bundle args = new Bundle();
                args.putSerializable(InviteActivity.EXTRA_INVITE, (Serializable)item.invite);
                args.putSerializable(InviteActivity.EXTRA_MODE, CommonEnum.INVITE_MODE.INVITE_DETAIL);
                fragment.setArguments(args);
                FragmentTool.replaceFragment(mParentActivity, fragment);
            }
        }
    };

    @NonNull
    @Override
    public InviteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_invite_row, parent, false);
        view.setOnClickListener(mOnClickListener);
        InviteViewHolder holder = new InviteViewHolder(view);
        view.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(final InviteViewHolder holder, int position) {
        holder.invite = mValues.get(position);

        InviteViewHelper.initializeView(mParentActivity, holder, CommonEnum.LIST_VIEW_MODE.MODIFY);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}
