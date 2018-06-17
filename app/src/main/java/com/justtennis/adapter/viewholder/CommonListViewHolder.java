package com.justtennis.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cameleon.common.android.model.GenericDBPojo;
import com.justtennis.ui.common.CommonEnum;

public abstract class CommonListViewHolder <D extends GenericDBPojo<Long>> extends RecyclerView.ViewHolder {
    public D data;
    public CommonEnum.LIST_MODE_VIEW mode;

    protected CommonListViewHolder(View itemView) {
        super(itemView);
    }

    public static CommonListViewHolder build(View view) {
        throw new RuntimeException("Not Yet Implemented method build");
    }

    public void showData(D data) {
        throw new RuntimeException("Not Yet Implemented method showData:" + data);
    }
}
