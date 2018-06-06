package com.justtennis.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.justtennis.R;
import com.justtennis.activity.interfaces.IListInviteActivity;
import com.justtennis.business.ListInviteBusiness;
import com.justtennis.domain.Saison;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.ui.adapter.SimpleInviteRecyclerViewAdapter;
import com.justtennis.ui.manager.DrawerManager;
import com.justtennis.ui.rxjava.RxBus;

import io.reactivex.functions.Consumer;

public class ItemDetailFragment extends Fragment implements IListInviteActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane = false;

    private SimpleInviteRecyclerViewAdapter adapter;
    private ListInviteBusiness business;
    private RecyclerView mRecyclerView;
    private View mEmptyView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FragmentActivity activity = getActivity();
        business = new ListInviteBusiness(getContext(), this, NotifierMessageLogger.getInstance());
        adapter = new SimpleInviteRecyclerViewAdapter(activity, business.getList(), mTwoPane);

        new DrawerManager().initializeDrawerToogle(activity);

        business.onCreate();
    }

    @Override
    public void onResume() {
        super.onResume();
        business.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);

        mRecyclerView = rootView.findViewById(R.id.item_list);
        mEmptyView = rootView.findViewById(R.id.empty_list);
        assert mRecyclerView != null;
        assert mEmptyView != null;
        setupRecyclerView();

        RxBus.subscribe(RxBus.SUBJECT_SELECT_SAISON, this, new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Saison saison = (Saison) o;
                business.setSaison(saison);
                business.refreshData();
                adapter.notifyDataSetChanged();
            }
        });
        RxBus.subscribe(RxBus.SUBJECT_DB_RESTORED, this, new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                business.refreshData();
                adapter.notifyDataSetChanged();
            }
        });
        RxBus.subscribe(RxBus.SUBJECT_CHANGE_TYPE, this, new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                business.refreshData();
                adapter.notifyDataSetChanged();
            }
        });
        return rootView;
    }

    @Override
    public void onDestroy() {
        RxBus.unregister(this);
        super.onDestroy();
    }

    private void setupRecyclerView() {
        RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                setViewVisbility();
            }
        };
        adapter.registerAdapterDataObserver(observer);
        mRecyclerView.setAdapter(adapter);
        observer.onChanged();
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
}