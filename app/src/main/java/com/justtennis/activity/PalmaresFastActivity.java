package com.justtennis.activity;

import org.gdocument.gtracergps.launcher.log.Logger;

import android.os.Bundle;
import android.view.View;
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
import com.justtennis.manager.TypeManager;
import com.justtennis.notifier.NotifierMessageLogger;

public class PalmaresFastActivity extends GenericActivity {

	private static final String TAG = PalmaresFastActivity.class.getSimpleName();
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.list_palmares_fast);

		NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
		business = new PalmaresFastBusiness(this, notifier);
		adapter = new PalmaresFastAdapter(this, business.getList());
		rankingListManager = RankingListManager.getInstance(this, notifier);

		llCompute = (LinearLayout) findViewById(R.id.ll_compute);
		llScore = (LinearLayout) findViewById(R.id.ll_score);
		tvSumPoint = (TextView) findViewById(R.id.tv_sum_point);
		tvNbVictory = (TextView) findViewById(R.id.tv_nb_victory);
		tvNbVictoryDetail = (TextView) findViewById(R.id.tv_nb_victory_detail);
		tvVE2I5G = (TextView) findViewById(R.id.tv_ve2i5g);
		spBonus = (Spinner)findViewById(R.id.sp_bonus);

		list = (ListView)findViewById(R.id.list);

		list.setAdapter(adapter);
		list.setItemsCanFocus(true);

		business.onCreate();
	
		initializeRankingList();
		intializeBonusList();
		initializeVisibility();
		TypeManager.getInstance().initializeActivity(findViewById(R.id.layout_main), false);
	}

	@Override
	protected void onResume() {
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

	public void onClickPoint(View view) {
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
		rankingListManager.manageRanking(this, listener, business.getIdRanking(), false);
	}

	private void intializeBonusList() {
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_bonus, LIST_BONUS);
		dataAdapter.setDropDownViewResource(R.layout.spinner_item_bonus);
		spBonus.setAdapter(dataAdapter);

		spBonus.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				TextView tv = (TextView)spBonus.findViewById(android.R.id.text1);
				tv.setTextColor(PalmaresFastActivity.this.getResources().getColor(position == 0 ? R.color.spinner_color_hint : android.R.color.black));

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