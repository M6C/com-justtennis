package com.justtennis.ui.fragment;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.justtennis.R;
import com.justtennis.activity.InviteActivity;
import com.justtennis.adapter.manager.RankingListManager;
import com.justtennis.adapter.manager.RankingListManager.IRankingListListener;
import com.justtennis.adapter.viewholder.ComputeListInviteViewHolder;
import com.justtennis.adapter.viewholder.ListInviteViewHolder;
import com.justtennis.business.ComputeRankingBusiness;
import com.justtennis.domain.Invite;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.tool.FragmentTool;
import com.justtennis.ui.adapter.CommonListRecyclerViewAdapter;
import com.justtennis.ui.common.CommonEnum;
import com.justtennis.ui.rxjava.RxCommonList;
import com.justtennis.ui.rxjava.RxComputeRanking;
import com.justtennis.ui.viewmodel.InviteViewModel;

import java.text.MessageFormat;
import java.util.Objects;

public class ComputeRankingFragment extends Fragment {

	@SuppressWarnings("unused")
	private static final String TAG = ComputeRankingFragment.class.getSimpleName();

	private static final int RESULT_ITEM_CLICK = 1;

	private ComputeRankingBusiness business;

	protected CommonListRecyclerViewAdapter<Invite> adapter;
	private TextView tvSumPoint;
	private TextView tvNbVictory;
	private TextView tvNbVictoryDetail;

	private RankingListManager rankingListManager;
	private FragmentActivity activity;

	private InviteViewModel modelInvite;

	public static ComputeRankingFragment build() {
		return new ComputeRankingFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Context context = getContext();
		activity = getActivity();

		assert context != null;
		assert activity != null;

		NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
		business = new ComputeRankingBusiness(context, notifier);
		business.onCreate();
		rankingListManager = RankingListManager.getInstance(context, notifier);
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.compute_ranking_list_invite, container, false);

		tvSumPoint = rootView.findViewById(R.id.tv_sum_point);
		tvNbVictory = rootView.findViewById(R.id.tv_nb_victory);
		tvNbVictoryDetail = rootView.findViewById(R.id.tv_nb_victory_detail);

		adapter = new CommonListRecyclerViewAdapter<>(business.getList(), R.layout.list_compute_ranking_invite_item);
		adapter.setFactoryViewHolder(ComputeListInviteViewHolder::build);

        RecyclerView mRecyclerView = rootView.findViewById(R.id.item_list);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(adapter);

		initializeFab();
		initializeRankingList(rootView);

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		business.onResume();
		initializeSubscribeComputeRanking();
		initializeSubscribeCommonList();
		refreshData();
	}

	@Override
	public void onPause() {
		RxComputeRanking.unregister(this);
		RxCommonList.unregister(this);
		super.onPause();
	}

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (requestCode == RESULT_ITEM_CLICK) {
//			business.refreshData();
//			adapter.notifyDataSetChanged();
//		}
//	}

	public void refresh() {
		initializePalmaresPoint();
		initializePalmaresNbVictory();
	}

	public void refreshData() {
		business.refreshData();

		adapter.notifyDataSetChanged();
		refresh();
	}

	protected void initializeFab() {
		FragmentTool.hideFab(activity);
	}

	private void initializeSubscribeComputeRanking() {
		RxComputeRanking.subscribe(RxComputeRanking.SUBJECT_REFRESH, this, o -> refreshData());
	}

	private void initializeRankingList(View rootView) {
		IRankingListListener listener = ranking -> {
            business.setIdRanking(ranking.getId());
            refreshData();
        };
		rankingListManager.manageRanking(activity, rootView, listener, business.getIdRanking(), false);
	}

	private void initializePalmaresPoint() {
		int point = business.getPointCalculate() + business.getPointBonus();
		String text = point + "/" + business.getPointObjectif();
		if (business.getPointBonus() > 0) {
			text += " [bonus:" + business.getPointBonus() + "]";
		}
		tvSumPoint.setText(text);
		tvSumPoint.setVisibility(View.VISIBLE);
	}

	private void initializePalmaresNbVictory() {
		tvNbVictory.setText(MessageFormat.format("{0}/{1}", business.getNbVictoryCalculate(), business.getNbVictorySum()));
		tvNbVictory.setVisibility(View.VISIBLE);
		tvNbVictoryDetail.setText(MessageFormat.format("({0}) [V-E-2I-5G:{1}]", business.getNbVictoryAdditional(), business.getVE2I5G()));
		tvNbVictoryDetail.setVisibility(View.VISIBLE);
	}

	private void initializeSubscribeCommonList() {
		RxCommonList.subscribe(RxCommonList.SUBJECT_ON_CLICK_ITEM, this, o -> onClickItem((View) o));
	}

	private void onClickItem(View view) {
//		Invite item = (Invite)view.getTag();
		Invite item = ((ListInviteViewHolder)view.getTag()).data;
		InviteFragment fragment = new InviteFragment();
		Bundle args = new Bundle();
		args.putSerializable(InviteActivity.EXTRA_INVITE, item);
		args.putSerializable(InviteActivity.EXTRA_MODE, CommonEnum.INVITE_MODE.INVITE_DETAIL);

		modelInvite = new ViewModelProvider.NewInstanceFactory().create(InviteViewModel.class);
		modelInvite.getSelected().observe(this, (Invite invite) -> refreshData());
		args.putSerializable(ListPlayerFragment.EXTRA_VIEW_MODEL, modelInvite);

		fragment.setArguments(args);
		FragmentTool.replaceFragment(Objects.requireNonNull(getActivity()), fragment);
	}
}