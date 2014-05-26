package com.justtennis.activity;

import java.util.HashMap;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
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
import com.justtennis.manager.TypeManager;
import com.justtennis.notifier.NotifierMessageLogger;

//http://code.google.com/p/achartengine/source/browse/trunk/achartengine/
public class PieChartActivity extends Activity {
	public static final String EXTRA_DATA = "EXTRA_DATA";

	/** Colors to be used for the pie slices. */
	private static int[] COLORS = new int[] { Color.GREEN, Color.BLUE, Color.MAGENTA, Color.CYAN };
	/** The main series that will include all the data. */
	private CategorySeries mSeries = new CategorySeries("");
	/** The main renderer for the main dataset. */
	private DefaultRenderer mRenderer = new DefaultRenderer();
	/** The chart view that displays the data. */
	private GraphicalView mChartView;
	
	private PieChartBusiness business;
	private Spinner spTypeData;
	private Spinner spScoreResultData;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pie_chart);
		business = new PieChartBusiness(this, NotifierMessageLogger.getInstance());

		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setStartAngle(180);
		mRenderer.setDisplayValues(false);
		mRenderer.setLabelsColor(Color.BLUE);
		mRenderer.setLabelsTextSize(32);
		mRenderer.setLegendTextSize(32);

		spTypeData = (Spinner) findViewById(R.id.sp_type_data);
		spScoreResultData = (Spinner) findViewById(R.id.sp_score_result_data);

		initializeTypeData();
		initializeScoreData();
		TypeManager.getInstance().initializeActivity(findViewById(R.id.layout_main), false);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if (getIntent().hasExtra(EXTRA_DATA)) {
			@SuppressWarnings("unchecked")
			HashMap<String, Double> data = (HashMap<String, Double>) getIntent().getSerializableExtra(EXTRA_DATA);
			for(String name : data.keySet()) {
				addValue(name, data.get(name));
			}
		}
		
		if (mChartView == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
			mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);
			mRenderer.setClickEnabled(true);
			mChartView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
					if (seriesSelection == null) {
						Toast.makeText(PieChartActivity.this, "No chart element selected", Toast.LENGTH_SHORT).show();
					} else {
						for (int i = 0; i < mSeries.getItemCount(); i++) {
							mRenderer.getSeriesRendererAt(i).setHighlighted(i == seriesSelection.getPointIndex());
						}
						mChartView.repaint();
						Toast.makeText(PieChartActivity.this,
								"Chart data point index " + seriesSelection.getPointIndex() + " selected" + " point value=" + seriesSelection.getValue(),
								Toast.LENGTH_SHORT).show();
					}
				}
			});
			layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		} else {
			mChartView.repaint();
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedState) {
		super.onRestoreInstanceState(savedState);
		mSeries = (CategorySeries) savedState.getSerializable("current_series");
		mRenderer = (DefaultRenderer) savedState.getSerializable("current_renderer");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("current_series", mSeries);
		outState.putSerializable("current_renderer", mRenderer);
	}

	private void addValue(String name, double value) {
		mSeries.add(name, value);//"Series " + (mSeries.getItemCount() + 1), value);
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
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, typeData);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spTypeData.setAdapter(dataAdapter);

		spTypeData.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mSeries.clear();
				mRenderer.removeAllRenderers();
				HashMap<String, Double> data = null;
				switch(position) {
					case 0:
						data = business.getData(CHART_DATA_TYPE.ALL);
						break;
					case 1:
						data = business.getData(CHART_DATA_TYPE.ENTRAINEMENT);
						break;
					case 2:
						data = business.getData(CHART_DATA_TYPE.MATCH);
						break;
				}
				
				if (data != null) {
					Double value = null;
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

	private void initializeScoreData() {
		String[] scoreResultData = new String[] {
			getString(CHART_SCORE_RESULT.ALL.stringId),
			getString(CHART_SCORE_RESULT.VICTORY.stringId),
			getString(CHART_SCORE_RESULT.DEFEAT.stringId),
			getString(CHART_SCORE_RESULT.UNFINISHED.stringId)
		};
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, scoreResultData);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spScoreResultData.setAdapter(dataAdapter);
		
		spScoreResultData.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mSeries.clear();
				mRenderer.removeAllRenderers();
				HashMap<String, Double> data = null;
				switch(position) {
					case 0:
						data = business.getData(CHART_SCORE_RESULT.ALL);
						break;
					case 1:
						data = business.getData(CHART_SCORE_RESULT.VICTORY);
						break;
					case 2:
						data = business.getData(CHART_SCORE_RESULT.DEFEAT);
						break;
					case 3:
						data = business.getData(CHART_SCORE_RESULT.UNFINISHED);
						break;
				}
				
				if (data != null) {
					Double value = null;
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
}