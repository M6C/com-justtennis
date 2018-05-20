package com.justtennis.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.justtennis.R;
import com.justtennis.activity.interfaces.IListInviteActivity;
import com.justtennis.business.ListInviteBusiness;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.ui.adapter.SimpleInviteRecyclerViewAdapter;

public class ItemDetailFragment extends Fragment implements IListInviteActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane = false;

    private SimpleInviteRecyclerViewAdapter adapter;
    private ListInviteBusiness business;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        business = new ListInviteBusiness(getContext(), this, NotifierMessageLogger.getInstance());
        adapter = new SimpleInviteRecyclerViewAdapter(getActivity(), business.getList(), mTwoPane);

        business.onCreate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);

        View recyclerView = rootView.findViewById(R.id.item_list);
        View emptyView = rootView.findViewById(R.id.empty_list);
        assert recyclerView != null;
        assert emptyView != null;
        setupRecyclerView((RecyclerView) recyclerView, emptyView);

        return rootView;
    }

    private void setupRecyclerView(@NonNull final RecyclerView recyclerView, @NonNull final View emptyView) {
        RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                if (adapter.getItemCount() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        };
        adapter.registerAdapterDataObserver(observer);
        recyclerView.setAdapter(adapter);
        observer.onChanged();
    }

    @Override
    public void refresh() {
    }
}