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
        business.onCreate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);

        View recyclerView = rootView.findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        return rootView;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleInviteRecyclerViewAdapter(getActivity(), business.getList(), mTwoPane));
    }

    @Override
    public void refresh() {
    }
}