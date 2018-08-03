package com.justtennis.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cameleon.common.android.factory.FactoryDialog;
import com.justtennis.R;
import com.justtennis.activity.InviteActivity;
import com.justtennis.adapter.viewholder.CommonListViewHolder;
import com.justtennis.adapter.viewholder.ListInviteViewHolder;
import com.justtennis.business.ListInviteBusiness;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Saison;
import com.justtennis.listener.ok.OnClickInviteDeleteListenerOk;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.tool.FragmentTool;
import com.justtennis.ui.common.CommonEnum;
import com.justtennis.ui.rxjava.RxCommonList;
import com.justtennis.ui.rxjava.RxListInvite;
import com.justtennis.ui.rxjava.RxNavigationDrawer;

import org.gdocument.gtracergps.launcher.log.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ListInviteFragment extends CommonListFragment<Invite> {

    public static final String TAG = ListInviteFragment.class.getSimpleName();

    private static List<Invite> mList = new ArrayList<>();
    private ListInviteBusiness business;
    private boolean mTwoPane;

    public static ListInviteFragment build() {
        ListInviteFragment fragment = new ListInviteFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_LIST, (Serializable) mList);
        args.putInt(EXTRA_ITEM_LAYOUT , R.layout.list_invite_row);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFactoryViewHolder(ListInviteViewHolder::build);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        mTwoPane = false;

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        business = new ListInviteBusiness(getContext(), this, NotifierMessageLogger.getInstance());
        refresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        business.onResume();

        initializeSubscribeSelectSaison();
        initializeSubscribeListPlayer();
        initializeSubscribeCommonList();
        //RxFragment.publish(RxFragment.SUBJECT_ON_SHOW, TAG);
    }

    @Override
    public void onPause() {
        RxListInvite.unregister(this);
        RxCommonList.unregister(this);
        super.onPause();
    }

    @Override
    public void refresh() {
        business.refreshData();
        mList.clear();
        mList.addAll(business.getList());
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void initializeFab() {
        super.initializeFab();
        FragmentTool.onClickFab(activity, v -> onClickMatch());
    }

    private void initializeSubscribeListPlayer() {
        RxListInvite.subscribe(RxListInvite.SUBJECT_REFRESH, this, o -> refresh());
        RxListInvite.subscribe(RxListInvite.SUBJECT_ON_CLICK_DELETE_ITEM, this, o -> onClickDelete((View) o));
    }

    private void initializeSubscribeCommonList() {
        RxCommonList.subscribe(RxCommonList.SUBJECT_ON_CLICK_ITEM, this, o -> onClickItem((View) o));
    }

    private void initializeSubscribeSelectSaison() {
        RxNavigationDrawer.subscribe(RxNavigationDrawer.SUBJECT_SELECT_SAISON, this, o -> {
            business.setSaison((Saison) o);
            refresh();
        });
    }

    private void onClickItem(View view) {
        Invite item = (Invite)((CommonListViewHolder)view.getTag()).data;
        if (mTwoPane) {
            InviteFragment fragment = new InviteFragment();
            Bundle arguments = new Bundle();
            arguments.putSerializable(InviteActivity.EXTRA_INVITE, item);
            arguments.putSerializable(InviteActivity.EXTRA_MODE, CommonEnum.INVITE_MODE.INVITE_DETAIL);
            fragment.setArguments(arguments);
            FragmentTool.replaceFragment(Objects.requireNonNull(getActivity()), fragment);
        } else {
            InviteFragment fragment = new InviteFragment();
            Bundle args = new Bundle();
            args.putSerializable(InviteActivity.EXTRA_INVITE, item);
            args.putSerializable(InviteActivity.EXTRA_MODE, CommonEnum.INVITE_MODE.INVITE_DETAIL);
            fragment.setArguments(args);
            FragmentTool.replaceFragment(Objects.requireNonNull(getActivity()), fragment);
        }
    }

    private void onClickDelete(View view) {
        Invite invite = (Invite)view.getTag();
        OnClickInviteDeleteListenerOk listener = new OnClickInviteDeleteListenerOk(business, invite);
        FactoryDialog.getInstance()
                .buildOkCancelDialog(business.getContext(), listener, R.string.dialog_invite_delete_title, R.string.dialog_invite_delete_message)
                .show();
    }

    private void onClickMatch() {
        InviteFragment fragment = new InviteFragment();
        Bundle args = new Bundle();
        args.putLong(InviteActivity.EXTRA_PLAYER_ID, business.getUnknownPlayerId());
        args.putSerializable(InviteActivity.EXTRA_MODE, CommonEnum.INVITE_MODE.INVITE_SIMPLE);
        fragment.setArguments(args);
        FragmentTool.replaceFragment(activity, fragment);
    }

    protected void logMe(String msg, Date dateStart) {
        logMe("ListInviteActivity time:" + (new Date().getTime() - dateStart.getTime()) + " millisecond - " + msg);
    }

    protected static void logMe(String msg) {
        com.crashlytics.android.Crashlytics.log(msg);
        Logger.logMe(TAG, msg);
    }
}