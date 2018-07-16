package com.justtennis.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.justtennis.R;
import com.justtennis.business.PieChartBusiness;
import com.justtennis.business.PieChartBusiness.CHART_DATA_TYPE;
import com.justtennis.business.PieChartBusiness.CHART_SCORE_RESULT;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.tool.FragmentTool;
import com.justtennis.ui.rxjava.RxFragment;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.util.HashMap;

//http://code.google.com/p/achartengine/source/browse/trunk/achartengine/
public class PieChartFragment extends Fragment {

	public static final String TAG = PieChartFragment.class.getSimpleName();

	public static final String EXTRA_DATA = "EXTRA_DATA";

	/** Colors to be used for the pie slices. */
	private static final int[] COLORS = new int[] { Color.GREEN, Color.BLUE, Color.MAGENTA, Color.CYAN };
	/** The main series that will include all the data. */
	private CategorySeries mSeries = new CategorySeries("");
	/** The main renderer for the main dataset. */
	private DefaultRenderer mRenderer = new DefaultRenderer();
	/** The chart view that displays the data. */
	private GraphicalView mChartView;
	
	private PieChartBusiness business;
	private Spinner spTypeData;
	private Spinner spScoreResultData;
	private View rootView;

	private Context context;
	private FragmentActivity activity;

	public static PieChartFragment build() {
		return new PieChartFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.pie_chart, container, false);
		business = new PieChartBusiness(getContext(), NotifierMessageLogger.getInstance());

		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setStartAngle(180);
		mRenderer.setDisplayValues(false);
		mRenderer.setLabelsColor(Color.BLUE);
		mRenderer.setLabelsTextSize(32);
		mRenderer.setLegendTextSize(32);

		spTypeData = rootView.findViewById(R.id.sp_type_data);
		spScoreResultData = rootView.findViewById(R.id.sp_score_result_data);

		return rootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		context = getContext();
		activity = getActivity();

		assert context != null;
		assert activity != null;

        initializeTypeData();
        initializeScoreData();

		if (savedInstanceState != null) {
			mSeries = (CategorySeries) savedInstanceState.getSerializable("current_series");
			mRenderer = (DefaultRenderer) savedInstanceState.getSerializable("current_renderer");
		} else {
			Intent intent = activity.getIntent();
			if (intent.hasExtra(EXTRA_DATA)) {
				@SuppressWarnings("unchecked")
				HashMap<String, Double> data = (HashMap<String, Double>) intent.getSerializableExtra(EXTRA_DATA);
				for (String name : data.keySet()) {
					addValue(name, data.get(name));
				}
			}
		}

		initializeChartView();
	}

	@Override
	public void onResume() {
		super.onResume();
		initializeFab();
		//RxFragment.publish(RxFragment.SUBJECT_ON_SHOW, TAG);
	}

	private void initializeFab() {
		FragmentTool.hideFab(activity);
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("current_series", mSeries);
		outState.putSerializable("current_renderer", mRenderer);
	}

	private void addValue(String name, double value) {
		mSeries.add(name, value);
		SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
		renderer.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
		mRenderer.addSeriesRenderer(renderer);
		mChartView.repaint();
	}

	private void initializeTypeData() {
		String[] typeData = new String[] {
			getString(CHART_DATA_TYPE.ALL.stringId),
			getString(CHART_DATA_TYPE.ENTRAINEMENT.stringId),
			getString(CHART_DATA_TYPE.MATCH.stringId)
		};
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, typeData);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spTypeData.setAdapter(dataAdapter);

		spTypeData.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mSeries.clear();
				mRenderer.removeAllRenderers();
				HashMap<String, Double> data = null;
				if (position == 0) {
					data = business.getData(CHART_DATA_TYPE.ALL);
				} else if (position == 1) {
					data = business.getData(CHART_DATA_TYPE.ENTRAINEMENT);

				} else if (position == 2) {
					data = business.getData(CHART_DATA_TYPE.MATCH);
				}
				
				if (data != null) {
					Double value;
					for (String name : data.keySet()) {
						value = data.get(name);
						if (value != null) {
							addValue(name, data.get(name));
						}
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	private void initializeScoreData() {
		String[] scoreResultData = new String[] {
			getString(CHART_SCORE_RESULT.ALL.stringId),
			getString(CHART_SCORE_RESULT.VICTORY.stringId),
			getString(CHART_SCORE_RESULT.DEFEAT.stringId),
			getString(CHART_SCORE_RESULT.UNFINISHED.stringId)
		};
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, scoreResultData);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spScoreResultData.setAdapter(dataAdapter);
		
		spScoreResultData.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mSeries.clear();
				mRenderer.removeAllRenderers();
				HashMap<String, Double> data = null;
				if (position == 0) {
					data = business.getData(CHART_SCORE_RESULT.ALL);
				} else if (position == 1) {
					data = business.getData(CHART_SCORE_RESULT.VICTORY);

				} else if (position == 2) {
					data = business.getData(CHART_SCORE_RESULT.DEFEAT);

				} else if (position == 3) {
					data = business.getData(CHART_SCORE_RESULT.UNFINISHED);
				}
				
				if (data != null) {
					Double value;
					for(String name : data.keySet()) {
						value = data.get(name);
						if (value!=null) {
							addValue(name, data.get(name));
						}
					}
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	private void initializeChartView() {
		if (mChartView == null) {
			LinearLayout layout = rootView.findViewById(R.id.chart);
			mChartView = ChartFactory.getPieChartView(context, mSeries, mRenderer);
			mRenderer.setClickEnabled(true);
			mChartView.setOnClickListener(v -> {
				SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
				if (seriesSelection == null) {
					Toast.makeText(activity, "No chart element selected", Toast.LENGTH_SHORT).show();
				} else {
					for (int i = 0; i < mSeries.getItemCount(); i++) {
						mRenderer.getSeriesRendererAt(i).setHighlighted(i == seriesSelection.getPointIndex());
					}
					mChartView.repaint();
					Toast.makeText(activity,
							"Chart data point index " + seriesSelection.getPointIndex() + " selected" + " point value=" + seriesSelection.getValue(),
							Toast.LENGTH_SHORT).show();
				}
			});
			layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		} else {
			mChartView.repaint();
		}
	}
}