package com.justtennis.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cameleon.common.android.model.GenericDBPojo;
import com.justtennis.R;
import com.justtennis.activity.interfaces.ICommonListActivity;
import com.justtennis.ui.adapter.CommonListRecyclerViewAdapter;
import com.justtennis.ui.common.CommonEnum;
import com.justtennis.ui.manager.DrawerManager;

import java.util.List;

public class CommonListFragment <DATA extends GenericDBPojo<Long>> extends Fragment implements ICommonListActivity {

    public static final String EXTRA_MODE = "MODE";
    public static final String EXTRA_LIST = "LIST";
    protected static final String EXTRA_ITEM_LAYOUT = "ITEM_LAYOUT";

    protected CommonListRecyclerViewAdapter<DATA> adapter;
    private RecyclerView mRecyclerView;
    private View mEmptyView;
    private CommonEnum.LIST_PLAYER_MODE mode;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CommonListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = (savedInstanceState != null) ? savedInstanceState : getArguments();
        List<DATA> list = null;
        int itemLayoutId = -1;
        if (bundle != null) {
            list = (List<DATA>) bundle.getSerializable(EXTRA_LIST);
            itemLayoutId = bundle.getInt(EXTRA_ITEM_LAYOUT);
            mode = (CommonEnum.LIST_PLAYER_MODE) bundle.getSerializable(EXTRA_MODE);
        }
        assert list != null;
        if (itemLayoutId == -1) throw new AssertionError("itemLayoutId not initialized");

        adapter = new CommonListRecyclerViewAdapter<>(list, itemLayoutId);

        initializeDrawerToogle();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);

        mRecyclerView = rootView.findViewById(R.id.item_list);
        mEmptyView = rootView.findViewById(R.id.empty_list);
        assert mRecyclerView != null;
        assert mEmptyView != null;
        setupRecyclerView();

        return rootView;
    }

    public void setFactoryViewHolder(CommonListRecyclerViewAdapter.IFactoryViewHolder factoryViewHolder) {
        adapter.setFactoryViewHolder(factoryViewHolder);
    }

    private void setupRecyclerView() {
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                setViewVisbility();
            }
        });
        mRecyclerView.setAdapter(adapter);
        setViewVisbility();
    }

    private void setViewVisbility() {
        if (adapter.getItemCount() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void refresh() {
    }

    private void initializeDrawerToogle() {
        new DrawerManager().initializeDrawerToogle(getActivity());
    }

    public CommonEnum.LIST_PLAYER_MODE getMode() {
        return mode;
    }
}