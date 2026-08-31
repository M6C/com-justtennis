package com.justtennis.activity;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.EditText;
import android.widget.ListView;

import com.justtennis.R;
import com.justtennis.adapter.ListPersonAdapter;
import com.justtennis.business.ListPersonBusiness;
import com.justtennis.listener.action.OnEditorActionListenerFilter;
import com.justtennis.listener.itemclick.OnItemClickListPerson;
import com.justtennis.manager.TypeManager;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.tool.ToolPermission;

import org.gdocument.gtracergps.launcher.log.Logger;

import java.util.Date;

public class ListPersonActivity extends GenericActivity {

	private static final String TAG = ListPersonActivity.class.getSimpleName();
	public static final String EXTRA_PLAYER = "PLAYER";

	private ListPersonBusiness business;

	private ListPersonAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_person);
		
		business = new ListPersonBusiness(this, NotifierMessageLogger.getInstance());
		adapter = new ListPersonAdapter(this, business.getList());

		ListView list = findViewById(R.id.list);
		EditText etFilter = findViewById(R.id.et_filter);
		list.setAdapter(adapter);

		list.setOnItemClickListener(new OnItemClickListPerson(this));
		list.setTextFilterEnabled(true);
		etFilter.addTextChangedListener(new OnEditorActionListenerFilter(adapter.getFilter()));
		TypeManager.getInstance().initializeActivity(findViewById(R.id.layout_main), false);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (ToolPermission.checkPermissionREAD_CONTACTS(this, true)) {
			initialize();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == ToolPermission.MY_PERMISSIONS_REQUEST) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				initialize();
			} else {
				logMe("Permission Denied ! Cancel initialization");
				finish();
			}
		}
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

	protected void logMe(String msg, Date dateStart) {
		logMe("ListPlayerActivity time:" + (new Date().getTime() - dateStart.getTime()) + " millisecond - " + msg);
	}

	protected static void logMe(String msg) {
		Logger.logMe(TAG, msg);
	}

	public void refresh() {
		adapter.setValue(business.getList());
		adapter.notifyDataSetChanged();
	}
}