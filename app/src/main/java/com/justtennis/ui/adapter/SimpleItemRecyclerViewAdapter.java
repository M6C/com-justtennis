package com.justtennis.ui.adapter;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.justtennis.R;
import com.justtennis.tool.FragmentTool;
import com.justtennis.ui.activity.ItemDetailActivity;
import com.justtennis.ui.activity.dummy.DummyContent;
import com.justtennis.ui.fragment.ItemDetailFragment;

import java.util.List;

public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

    public static final String ARG_ITEM_ID = "item_id";

    private final FragmentActivity mParentActivity;
    private final List<DummyContent.DummyItem> mValues;
    private final boolean mTwoPane;


    public SimpleItemRecyclerViewAdapter(FragmentActivity parent, List<DummyContent.DummyItem> items, boolean twoPane) {
        mValues = items;
        mParentActivity = parent;
        mTwoPane = twoPane;
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putString(ARG_ITEM_ID, item.id);
                ItemDetailFragment fragment = new ItemDetailFragment();
                fragment.setArguments(arguments);
                FragmentTool.replaceFragment(mParentActivity, fragment, R.id.item_detail_container);
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, ItemDetailActivity.class);
                intent.putExtra(ARG_ITEM_ID, item.id);

                context.startActivity(intent);
            }
        }
    };

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);

        holder.itemView.setTag(mValues.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mIdView;
        final TextView mContentView;

        ViewHolder(View view) {
            super(view);
            mIdView = (TextView) view.findViewById(R.id.id_text);
            mContentView = (TextView) view.findViewById(R.id.content);
        }
    }
}
