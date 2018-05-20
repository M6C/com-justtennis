package com.justtennis.ui.adapter;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.justtennis.ui.fragment.ItemDetailFragment;

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
                arguments.putSerializable(InviteActivity.EXTRA_MODE, InviteActivity.MODE.INVITE_DETAIL);
                ItemDetailFragment fragment = new ItemDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.item_detail_container, fragment)
                        .commit();
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, InviteActivity.class);
                intent.putExtra(InviteActivity.EXTRA_INVITE, (Serializable)item.invite);
                intent.putExtra(InviteActivity.EXTRA_MODE, InviteActivity.MODE.INVITE_DETAIL);
                context.startActivity(intent);
            }
        }
    };

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

        InviteViewHelper.INVITE_MODE_VIEW mode = InviteViewHelper.INVITE_MODE_VIEW.MODIFY;
        InviteViewHelper.initializeView(mParentActivity, holder, mode);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}
