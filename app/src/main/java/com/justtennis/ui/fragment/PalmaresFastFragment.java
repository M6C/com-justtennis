package com.justtennis.ui.fragment;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.justtennis.R;
import com.justtennis.adapter.PalmaresFastAdapter;
import com.justtennis.adapter.manager.RankingListManager;
import com.justtennis.adapter.manager.RankingListManager.IRankingListListener;
import com.justtennis.business.PalmaresFastBusiness;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.tool.FragmentTool;
import com.justtennis.ui.rxjava.RxFragment;

import org.gdocument.gtracergps.launcher.log.Logger;

import java.text.MessageFormat;

public class PalmaresFastFragment extends Fragment {

	private static final String TAG = PalmaresFastFragment.class.getSimpleName();
	private static final String[] LIST_BONUS = new String[]{"0", "15", "30", "45"};
	public static final String EXTRA_PALMARES = "EXTRA_PALMARES";

	private PalmaresFastBusiness business;

	private RecyclerView list;
	private PalmaresFastAdapter adapter;
	private LinearLayout llCompute;
	private LinearLayout llScore;
	private TextView tvSumPoint;
	private TextView tvNbVictory;
	private TextView tvNbVictoryDetail;
	private TextView tvVE2I5G;
	private Spinner spBonus;

	private RankingListManager rankingListManager;

	private boolean bComputeVisible = true;
	private View rootView;
	private FragmentActivity activity;

	public static PalmaresFastFragment build() {
		return new PalmaresFastFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Context context = getContext();
		activity = getActivity();

		assert context != null;
		assert activity != null;

		NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
		rankingListManager = RankingListManager.getInstance(context, notifier);
		business = new PalmaresFastBusiness(context, notifier);
		business.onCreate(activity);

		adapter = new PalmaresFastAdapter(business.getList());
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.list_palmares_fast, container, false);

		llCompute = rootView.findViewById(R.id.ll_compute);
		llScore = rootView.findViewById(R.id.ll_score);
		tvSumPoint = rootView.findViewById(R.id.tv_sum_point);
		tvNbVictory = rootView.findViewById(R.id.tv_nb_victory);
		tvNbVictoryDetail = rootView.findViewById(R.id.tv_nb_victory_detail);
		tvVE2I5G = rootView.findViewById(R.id.tv_ve2i5g);
		spBonus = rootView.findViewById(R.id.sp_bonus);
		list = rootView.findViewById(R.id.list);

		// Fix : Recyclerview not call any Adapter method :onCreateViewHolder,onBindViewHolder,
		list.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
		list.setNestedScrollingEnabled(false);
//		list.setHasFixedSize(false);
//		list.setLayoutManager(new SnappingLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
		list.setAdapter(adapter);

		initializeRankingList();
		intializeBonusList();
		initializeVisibility();

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		initializeFab();
		RxFragment.publish(RxFragment.SUBJECT_ON_SHOW, TAG);
	}

	public void refreshData() {
		Logger.logMe(TAG, "PALMARES FAST - PalmaresFastActivity - refreshData");
		business.refreshData();

		refresh();
		adapter.notifyDataSetChanged();
	}

	public void refresh() {
		Logger.logMe(TAG, "PALMARES FAST - PalmaresFastActivity - refresh");
		initializePalmaresPoint();
		initializePalmaresNbVictory();
	}

	public void onClickCompute(View view) {
		bComputeVisible = !bComputeVisible;
		initializeVisibility();
		if (bComputeVisible) {
			refreshData();
		} else {
			initializeFocus();
		}
	}

	private void initializeFocus() {
		Logger.logMe(TAG, "PALMARES FAST - PalmaresFastActivity - initializeFocus");
		list.smoothScrollToPosition(business.getIdRanking().intValue());
	}

	private void initializeFab() {
		FragmentTool.onClickFab(activity, this::onClickCompute);
	}

	private void initializeVisibility() {
		llCompute.setVisibility(bComputeVisible ? View.VISIBLE : View.GONE);
		llScore.setVisibility(!bComputeVisible ? View.VISIBLE : View.GONE);
		FragmentTool.initializeFabDrawable(activity, bComputeVisible ? R.drawable.ic_check_black_24dp : R.drawable.ic_arrow_back_black_24dp);
	}

	private void initializeRankingList() {
		Logger.logMe(TAG, "PALMARES FAST - PalmaresFastActivity - initializeRankingList");
		IRankingListListener listener = ranking -> {
            business.setIdRanking(ranking.getId());
            refreshData();
        };
		rankingListManager.manageRanking(activity, rootView, listener, business.getIdRanking(), false);
	}

	private void intializeBonusList() {
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(activity, R.layout.spinner_item_bonus, LIST_BONUS);
		dataAdapter.setDropDownViewResource(R.layout.spinner_item_bonus);
		spBonus.setAdapter(dataAdapter);

		spBonus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				TextView tv = spBonus.getRootView().findViewById(android.R.id.text1);
				tv.setTextColor(PalmaresFastFragment.this.getResources().getColor(position == 0 ? R.color.spinner_color_hint : android.R.color.black));

				business.setPointBonus(Integer.parseInt(LIST_BONUS[position]));
				refreshData();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				/* Empty because Nothing to do */
			}
		});
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
		tvNbVictoryDetail.setText(MessageFormat.format("({0})", business.getNbVictoryAdditional()));
		tvVE2I5G.setText(MessageFormat.format("{0}", business.getVE2I5G()));
	}
}