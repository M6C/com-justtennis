package com.justtennis.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.justtennis.R;
import com.justtennis.adapter.PalmaresFastAdapter;
import com.justtennis.adapter.manager.RankingListManager;
import com.justtennis.adapter.manager.RankingListManager.IRankingListListener;
import com.justtennis.business.PalmaresFastBusiness;
import com.justtennis.domain.Ranking;
import com.justtennis.notifier.NotifierMessageLogger;

import org.gdocument.gtracergps.launcher.log.Logger;

public class PalmaresFastFragment extends Fragment {

	private static final String TAG = PalmaresFastFragment.class.getSimpleName();
	private static final String[] LIST_BONUS = new String[]{"0", "15", "30", "45"};
	public static final String EXTRA_PALMARES = "EXTRA_PALMARES";

	private PalmaresFastBusiness business;

	private ListView list;
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
	private View swCompute;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.list_palmares_fast, container, false);

		FragmentActivity activity = getActivity();
		Context context = getContext();
		NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
		business = new PalmaresFastBusiness(context, notifier);
		adapter = new PalmaresFastAdapter(activity, business.getList());
		rankingListManager = RankingListManager.getInstance(context, notifier);

		llCompute = rootView.findViewById(R.id.ll_compute);
		llScore = rootView.findViewById(R.id.ll_score);
		tvSumPoint = rootView.findViewById(R.id.tv_sum_point);
		tvNbVictory = rootView.findViewById(R.id.tv_nb_victory);
		tvNbVictoryDetail = rootView.findViewById(R.id.tv_nb_victory_detail);
		tvVE2I5G = rootView.findViewById(R.id.tv_ve2i5g);
		spBonus = rootView.findViewById(R.id.sp_bonus);
		swCompute = rootView.findViewById(R.id.sw_compute);
		swCompute.setOnClickListener(this::onClickCompute);

		list = rootView.findViewById(R.id.list);

		list.setAdapter(adapter);
		list.setItemsCanFocus(true);

		business.onCreate(activity);
	
		initializeRankingList();
		intializeBonusList();
		initializeVisibility();
//		TypeManager.getInstance().initializeActivity(rootView.findViewById(R.id.layout_main), false);

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		business.onResume();
		refresh();
	}

	public void refresh() {
		Logger.logMe(TAG, "PALMARES FAST - PalmaresFastActivity - refresh");
		initializePalmaresPoint();
		initializePalmaresNbVictory();
	}

	private void initializeFocus() {
		Logger.logMe(TAG, "PALMARES FAST - PalmaresFastActivity - initializeFocus");
		list.smoothScrollToPosition(business.getRankingPosition());
	}

	private void initializeVisibility() {
		llCompute.setVisibility(bComputeVisible ? View.VISIBLE : View.GONE);
		llScore.setVisibility(!bComputeVisible ? View.VISIBLE : View.GONE);
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

	public void refreshData() {
		Logger.logMe(TAG, "PALMARES FAST - PalmaresFastActivity - refreshData");
		business.refreshData();

		adapter.notifyDataSetChanged();
		refresh();
	}

	private void initializeRankingList() {
		Logger.logMe(TAG, "PALMARES FAST - PalmaresFastActivity - initializeRankingList");
		IRankingListListener listener = new IRankingListListener() {
			@Override
			public void onRankingSelected(Ranking ranking) {
				business.setIdRanking(ranking.getId());
				refreshData();
//				initializeFocus();
			}
		};
		rankingListManager.manageRanking(getActivity(), rootView, listener, business.getIdRanking(), false);
	}

	private void intializeBonusList() {
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item_bonus, LIST_BONUS);
		dataAdapter.setDropDownViewResource(R.layout.spinner_item_bonus);
		spBonus.setAdapter(dataAdapter);

		spBonus.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				TextView tv = (TextView)spBonus.getRootView().findViewById(android.R.id.text1);
				tv.setTextColor(PalmaresFastFragment.this.getResources().getColor(position == 0 ? R.color.spinner_color_hint : android.R.color.black));

				business.setPointBonus(Integer.parseInt(LIST_BONUS[position]));
				refreshData();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
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
		tvNbVictory.setText(business.getNbVictoryCalculate() + "/" + business.getNbVictorySum());
		tvNbVictoryDetail.setText("("+ business.getNbVictoryAdditional() + ")");
		tvVE2I5G.setText(Integer.toString(business.getVE2I5G()));
	}
}