package com.justtennis.ui.adapter;


import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cameleon.common.android.model.GenericDBPojo;
import com.justtennis.adapter.viewholder.CommonListViewHolder;
import com.justtennis.ui.common.CommonEnum;
import com.justtennis.ui.rxjava.RxCommonList;

import java.util.List;

public class CommonListRecyclerViewAdapter <DATA extends GenericDBPojo<Long>> extends RecyclerView.Adapter<CommonListViewHolder> {

    private List<DATA> mValues;
    private @LayoutRes int mItemLayoutId;
    private IFactoryViewHolder factoryViewHolder;

    public CommonListRecyclerViewAdapter(List<DATA> items, @LayoutRes int itemLayoutId) {
        mValues = items;
        mItemLayoutId = itemLayoutId;
        factoryViewHolder = CommonListViewHolder::build;
    }

    @NonNull
    @Override
    public CommonListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mItemLayoutId, parent, false);

        CommonListViewHolder holder = factoryViewHolder.create(view);
        view.setTag(holder);
        view.setOnClickListener(v -> RxCommonList.publish(RxCommonList.SUBJECT_ON_CLICK_ITEM, v));

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CommonListViewHolder holder, int position) {
        DATA data = mValues.get(position);
        holder.mode = CommonEnum.LIST_VIEW_MODE.MODIFY;
        holder.data = data;
        holder.showData(data);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setFactoryViewHolder(IFactoryViewHolder factoryViewHolder) {
        this.factoryViewHolder = factoryViewHolder;
    }

    public interface IFactoryViewHolder {
        CommonListViewHolder create(View view);
    }
}
