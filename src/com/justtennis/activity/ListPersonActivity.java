package com.justtennis.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;

import com.justtennis.R;
import com.justtennis.adapter.ListPersonAdapter;
import com.justtennis.business.ListPersonBusiness;
import com.justtennis.listener.action.OnEditorActionListenerFilter;
import com.justtennis.listener.itemclick.OnItemClickListPerson;
import com.justtennis.notifier.NotifierMessageLogger;

public class ListPersonActivity extends Activity {

	@SuppressWarnings("unused")
	private static final String TAG = ListPersonActivity.class.getSimpleName();
	public static final String EXTRA_PLAYER = "PLAYER";

	private ListPersonBusiness business;
	
	private ListView list;
	private ListPersonAdapter adapter;

	private EditText etFilter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_person);
		
		business = new ListPersonBusiness(this, NotifierMessageLogger.getInstance());
		adapter = new ListPersonAdapter(this, business.getList());

		list = (ListView)findViewById(R.id.list);
		etFilter = (EditText)findViewById(R.id.et_filter);
		list.setAdapter(adapter);

		list.setOnItemClickListener(new OnItemClickListPerson(this));
		list.setTextFilterEnabled(true);
		etFilter.addTextChangedListener(new OnEditorActionListenerFilter(adapter.getFilter()));
	}

	@Override
	protected void onResume() {
		super.onResume();

		initialize();
	}

	private void initialize() {

		ProgressDialog progressDialog = new ProgressDialog(this);
		try {
			progressDialog.setIndeterminate(true);
			progressDialog.show();

			business.initialize();
			refresh();
		} finally {
			progressDialog.dismiss();
		}
	}

	public void refresh() {
		adapter.setValue(business.getList());
		adapter.notifyDataSetChanged();
	}
}