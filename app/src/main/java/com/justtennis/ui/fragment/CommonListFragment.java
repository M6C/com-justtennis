package com.justtennis.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cameleon.common.android.model.GenericDBPojo;
import com.justtennis.R;
import com.justtennis.activity.interfaces.ICommonListActivity;
import com.justtennis.tool.FragmentTool;
import com.justtennis.ui.adapter.CommonListRecyclerViewAdapter;
import com.justtennis.ui.common.CommonEnum;
import com.justtennis.ui.manager.DrawerManager;

import org.gdocument.gtracergps.launcher.log.Logger;

import java.util.List;

public class CommonListFragment <D extends GenericDBPojo<Long>> extends Fragment implements ICommonListActivity {

    private static final String TAG = CommonListFragment.class.getSimpleName();

    public static final String EXTRA_MODE = "INVITE_MODE";
    public static final String EXTRA_LIST = "LIST";
    protected static final String EXTRA_ITEM_LAYOUT = "ITEM_LAYOUT";

    protected CommonListRecyclerViewAdapter<D> adapter;
    protected List<D> list = null;
    private RecyclerView mRecyclerView;
    private View mEmptyView;
    private CommonEnum.LIST_FRAGMENT_MODE mode;
    protected Context context;
    protected FragmentActivity activity;

    public CommonListFragment() {
        // Mandatory empty constructor for the fragment manager to instantiate the
        // fragment (e.g. upon screen orientation changes).
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = (savedInstanceState != null) ? savedInstanceState : getArguments();
        int itemLayoutId = -1;
        if (bundle != null) {
            list = (List<D>) bundle.getSerializable(EXTRA_LIST);
            itemLayoutId = bundle.getInt(EXTRA_ITEM_LAYOUT);
            mode = (CommonEnum.LIST_FRAGMENT_MODE) bundle.getSerializable(EXTRA_MODE);
        }
        assert list != null;
        if (itemLayoutId == -1) throw new AssertionError("itemLayoutId not initialized");

        adapter = new CommonListRecyclerViewAdapter<>(list, itemLayoutId);

        initializeDrawerToogle();
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getContext();
        activity = getActivity();

        assert context != null;
        assert activity != null;
    }

    @Override
    public void onResume() {
        super.onResume();

        initializeFab();
        adapter.notifyDataSetChanged();
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
        mRecyclerView.setNestedScrollingEnabled(false);
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

    protected void initializeFab() {
        FragmentTool.initializeFabDrawable(activity, FragmentTool.INIT_FAB_IMAGE.ADD);
        FragmentTool.hideFab(activity);
    }

    private void initializeDrawerToogle() {
        new DrawerManager().initializeDrawerToogle(getActivity());
    }

    public CommonEnum.LIST_FRAGMENT_MODE getMode() {
        return mode;
    }

    private static void logMe(String msg) {
        Logger.logMe(TAG, msg);
    }
}