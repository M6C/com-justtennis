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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.justtennis.R;
import com.justtennis.business.PieChartBusiness;
import com.justtennis.domain.Invite.INVITE_TYPE;
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
	/** Button for adding entered data to the current series. */
	private Button mAdd;
	/** Edit text field for entering the slice value. */
	private EditText mValue;
	/** The chart view that displays the data. */
	private GraphicalView mChartView;
	
	private PieChartBusiness business;
	private Spinner spTypeData;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pie_chart);
		business = new PieChartBusiness(this, NotifierMessageLogger.getInstance());

		mValue = (EditText) findViewById(R.id.xValue);
		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setStartAngle(180);
		mRenderer.setDisplayValues(true);

		mAdd = (Button) findViewById(R.id.add);
		mAdd.setEnabled(true);
		mValue.setEnabled(true);

		mAdd.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				double value = 0;
				try {
					value = Double.parseDouble(mValue.getText().toString());
				} catch (NumberFormatException e) {
					mValue.requestFocus();
					return;
				}
				mValue.setText("");
				mValue.requestFocus();
				addValue("Series " + (mSeries.getItemCount() + 1), value);
			}
		});
		
		spTypeData = (Spinner) findViewById(R.id.sp_type_data);
		initializeTypeData();
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
				"All By Ranking",
				"Entrainement By Ranking",
				"Match By Ranking"
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
						data = business.getDataByRanking();
						break;
					case 1:
						data = business.getDataByRanking(INVITE_TYPE.ENTRAINEMENT);
						break;
					case 2:
						data = business.getDataByRanking(INVITE_TYPE.MATCH);
						break;
				}
				
				if (data != null) {
					for(String name : data.keySet()) {
						addValue(name, data.get(name));
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
}