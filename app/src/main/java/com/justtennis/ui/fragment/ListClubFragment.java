package com.justtennis.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.cameleon.common.android.factory.FactoryDialog;
import com.justtennis.R;
import com.justtennis.adapter.viewholder.ListClubViewHolder;
import com.justtennis.domain.Club;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.tool.FragmentTool;
import com.justtennis.ui.business.ListClubBusiness;
import com.justtennis.ui.common.CommonEnum;
import com.justtennis.ui.rxjava.RxCommonList;
import com.justtennis.ui.rxjava.RxFragment;
import com.justtennis.ui.rxjava.RxListPlayer;

import org.gdocument.gtracergps.launcher.log.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListClubFragment extends CommonListFragment<Club> {

	public static final String TAG = ListClubFragment.class.getSimpleName();

	private static List<Club> mList = new ArrayList<>();

	private ListClubBusiness business;
	private AdapterView.OnItemClickListener onItemClick;

	public static ListClubFragment build() {
		return initialize(CommonEnum.LIST_FRAGMENT_MODE.EDIT);
	}

	public static ListClubFragment build(CommonEnum.LIST_FRAGMENT_MODE mode) {
		return initialize(mode);
	}

	@NonNull
	private static ListClubFragment initialize(CommonEnum.LIST_FRAGMENT_MODE mode) {
		ListClubFragment fragment = new ListClubFragment();
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_LIST, (Serializable) mList);
		args.putSerializable(EXTRA_MODE, mode);
		args.putInt(EXTRA_ITEM_LAYOUT , R.layout.list_club_row);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		business = new ListClubBusiness(getActivity(), NotifierMessageLogger.getInstance());
		business.initialize();
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = super.onCreateView(inflater, container, savedInstanceState);

		setFactoryViewHolder(ListClubViewHolder::build);

		initializeListener();

        return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		refresh();
		initializeSubscribeListPlayer();
		initializeSubscribeCommonList();
		RxFragment.publish(RxFragment.SUBJECT_ON_SHOW, TAG);
	}

	@Override
	public void onPause() {
		RxListPlayer.unregister(this);
		RxCommonList.unregister(this);
		super.onPause();
	}

	@Override
	protected void initializeFab() {
		super.initializeFab();
		FragmentTool.onClickFab(activity, v -> onClickAdd());
	}

	private void initializeListener() {
		switch (getMode()) {
			case FOR_RESULT_FRAGMENT:
				assert getArguments() != null;
				onItemClick = (parent, view, position, id) -> {
					model.select(((ListClubViewHolder)view.getTag()).data);
					finish();
				};
				break;
			case EDIT:
			default:
				onItemClick = (parent, view, position, id) -> {
					Club club = ((ListClubViewHolder)view.getTag()).data;
					ClubFragment fragment = ClubFragment.build(club);
					FragmentTool.replaceFragment(activity, fragment);
				};
				break;
		}
	}

	private void initializeSubscribeListPlayer() {
		RxListPlayer.subscribe(RxListPlayer.SUBJECT_REFRESH, this, o -> refresh());
		RxListPlayer.subscribe(RxListPlayer.SUBJECT_ON_CLICK_DELETE_ITEM, this, o -> onClickDelete((View) o));
	}

	private void initializeSubscribeCommonList() {
		RxCommonList.subscribe(RxCommonList.SUBJECT_ON_CLICK_ITEM, this, o -> onItemClick.onItemClick(null, (View) o, 0, 0));
	}

	@Override
	public void refresh() {
		business.refreshData();
		mList.clear();
		mList.addAll(business.getList());
		adapter.notifyDataSetChanged();
	}

	public void onClickAdd() {
		ClubFragment fragment = ClubFragment.build();
		FragmentTool.replaceFragment(activity, fragment);
	}

	private void onClickDelete(View view) {
		Club club = (Club)view.getTag();
		if (business.getInviteCount(club) > 0) {
			Toast.makeText(getActivity(), R.string.dialog_club_error_delete_have_invite, Toast.LENGTH_LONG).show();
		} else {
			DialogInterface.OnClickListener listener = (dialog, which) -> delete(club);
			FactoryDialog.getInstance()
					.buildOkCancelDialog(getContext(), listener, R.string.dialog_player_delete_title, R.string.dialog_player_delete_message)
					.show();
		}
	}

	private void delete(Club club) {
		business.delete(club);
		refresh();
	}

	private void finish() {
		FragmentTool.finish(activity);
	}

	protected void logMe(String msg, Date dateStart) {
		logMe("ListPlayerActivity time:" + (new Date().getTime() - dateStart.getTime()) + " millisecond - " + msg);
    }

	protected static void logMe(String msg) {
		Logger.logMe(TAG, msg);
    }
}