package com.justtennis.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.cameleon.common.android.factory.FactoryDialog;
import com.justtennis.R;
import com.justtennis.adapter.viewholder.ListPlayerViewHolder;
import com.justtennis.business.ListPlayerBusiness;
import com.justtennis.domain.Player;
import com.justtennis.listener.itemclick.OnItemClickListPlayer;
import com.justtennis.listener.itemclick.OnItemClickListPlayerForResult;
import com.justtennis.listener.itemclick.OnItemClickListPlayerInvite;
import com.justtennis.listener.ok.OnClickPlayerDeleteListenerOk;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.ui.common.CommonEnum;
import com.justtennis.ui.rxjava.RxCommonList;
import com.justtennis.ui.rxjava.RxListPlayer;
import com.justtennis.ui.viewmodel.PlayerViewModel;

import org.gdocument.gtracergps.launcher.log.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ListPlayerFragment extends CommonListFragment<Player> {

	private static final String TAG = ListPlayerFragment.class.getSimpleName();
	public static final String EXTRA_VIEW_MODEL = "EXTRA_VIEW_MODEL";

	private static List<Player> mList = new ArrayList<>();

	private ListPlayerBusiness business;
	private AdapterView.OnItemClickListener onItemClick;
	private PlayerViewModel model;

	public static ListPlayerFragment build(Activity activity, NotifierMessageLogger notifier, CommonEnum.LIST_PLAYER_MODE mode) {
		return initialize(mode);
	}

	@NonNull
	private static ListPlayerFragment initialize(CommonEnum.LIST_PLAYER_MODE mode) {
		ListPlayerFragment fragment = new ListPlayerFragment();
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_MODE, mode);
		args.putSerializable(EXTRA_LIST, (Serializable) mList);
		args.putInt(EXTRA_ITEM_LAYOUT , R.layout.list_player_row);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		business = new ListPlayerBusiness(getActivity(), NotifierMessageLogger.getInstance());
		business.initialize();
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = super.onCreateView(inflater, container, savedInstanceState);

		setFactoryViewHolder(view -> ListPlayerViewHolder.build(getActivity(), view));

		initializeListener();
		initializeSubscribeListPlayer();
		initializeSubscribeCommonList();

        return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		refresh();
	}

	@Override
	public void onPause() {
		RxListPlayer.unregister(this);
		RxCommonList.unregister(this);
		super.onPause();
	}

	private void initializeListener() {
		FragmentActivity activity = getActivity();
		switch (getMode()) {
			case EDIT:
				onItemClick = new OnItemClickListPlayer(activity);
				break;
			case INVITE:
				onItemClick = new OnItemClickListPlayerInvite(activity);
				break;
			case FOR_RESULT:
				onItemClick = new OnItemClickListPlayerForResult(activity);
				break;
			case FOR_RESULT_FRAGMENT:
				assert getArguments() != null;
				model = (PlayerViewModel)getArguments().getSerializable(EXTRA_VIEW_MODEL);
				onItemClick = (parent, view, position, id) -> {
					model.select(((ListPlayerViewHolder)view.getTag()).data);
					Objects.requireNonNull(getActivity()).onBackPressed();
				};
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

	private void onClickDelete(View view) {
		Player player = (Player)view.getTag();
		if (business.getInviteCount(player) > 0) {
			Toast.makeText(getActivity(), R.string.dialog_player_error_delete_have_invite, Toast.LENGTH_LONG).show();
		} else {
			OnClickPlayerDeleteListenerOk listener = new OnClickPlayerDeleteListenerOk(business, player);
			FactoryDialog.getInstance()
					.buildOkCancelDialog(getContext(), listener, R.string.dialog_player_delete_title, R.string.dialog_player_delete_message)
					.show();
		}
	}

	protected void logMe(String msg, Date dateStart) {
		logMe("ListPlayerActivity time:" + (new Date().getTime() - dateStart.getTime()) + " millisecond - " + msg);
    }

	protected static void logMe(String msg) {
		Logger.logMe(TAG, msg);
    }
}