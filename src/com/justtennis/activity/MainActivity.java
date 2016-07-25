package com.justtennis.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gdocument.gtracergps.launcher.log.Logger;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.cameleon.common.android.db.sqlite.helper.GenericDBHelper;
import com.cameleon.common.android.factory.FactoryDialog;
import com.cameleon.common.android.factory.listener.OnClickViewListener;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.R;
import com.justtennis.activity.ListPlayerActivity.MODE;
import com.justtennis.activity.MatchActivity.PlaceholderFragment;
import com.justtennis.adapter.CustomArrayAdapter;
import com.justtennis.adapter.NavigationDrawerAdapter;
import com.justtennis.adapter.NavigationDrawerAdapter.NavigationDrawerData;
import com.justtennis.adapter.NavigationDrawerAdapter.NavigationDrawerNotifer;
import com.justtennis.business.MainBusiness;
import com.justtennis.domain.Saison;
import com.justtennis.fragment.NavigationDrawerFragment;
import com.justtennis.listener.ok.OnClickDBBackupListenerOk;
import com.justtennis.listener.ok.OnClickDBRestoreListenerOk;
import com.justtennis.listener.ok.OnClickSendApkListenerOk;
import com.justtennis.listener.ok.OnClickSendDBListenerOk;
import com.justtennis.manager.TypeManager;
import com.justtennis.manager.TypeManager.TYPE;

public class MainActivity extends GenericActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, INotifierMessage {

	private static final String TAG = MainActivity.class.getSimpleName();
	private static final int RESULT_CODE_QRCODE_SCAN = 0;
	private MainBusiness business;

	private NavigationDrawerFragment mNavigationDrawerFragment;
	private RelativeLayout layoutMain;
	private TypeManager typeManager;
	private View menuOverFlowContent;

	private boolean backPressedToExitOnce = false;
	private Toast toast = null;
	private NavigationDrawerSaisonNotifer notiferSaison;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		business = new MainBusiness(this, this);
		typeManager = TypeManager.getInstance(this.getApplicationContext(), this);

		setContentView(R.layout.main_01);

		layoutMain = (RelativeLayout)findViewById(R.id.container);
//		menuOverFlowContent = findViewById(R.id.ll_menu_overflow_content);

		initializeDrawer();
		initializeLayoutType(layoutMain);

		Intent intent = getIntent();
		if (Intent.ACTION_SEND_MULTIPLE.equals(intent.getAction())) {
			handleSendMultipleDb(intent);
		} else if (intent.getData() != null && intent.getData().getEncodedPath() != null) {
			handleSendDb(intent.getData().getEncodedPath());
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		business.onResume();

		initializeData();

		if (business.getUserCount()==0) {
			Intent intent = new Intent(getApplicationContext(), UserActivity.class);
			startActivity(intent);
			
			finish();
		}
	}

	private void initializeData() {
//		initializeSaisonList();
//		initializeSaison();
	}

	private void initializeLayoutType(View view) {
		view = (view.getParent()==null) ? view : ((View)view.getParent());
		View root = view.getRootView();
		typeManager.initializeActivity(layoutMain, true);
		switch(typeManager.getType()) {
			case COMPETITION: {
				((LinearLayout)view.findViewById(R.id.ll_type_match)).setAlpha(1f);
				((LinearLayout)view.findViewById(R.id.ll_type_training)).setAlpha(.2f);
				if (root.findViewById(R.id.iv_match) != null) {
					((ImageView)root.findViewById(R.id.iv_match)).setVisibility(View.VISIBLE);
				}
				if (root.findViewById(R.id.iv_play) != null) {
					((ImageView)root.findViewById(R.id.iv_play)).setVisibility(View.GONE);
				}
			}
			break;

			case TRAINING:
			default: {
				((LinearLayout)view.findViewById(R.id.ll_type_match)).setAlpha(.2f);
				((LinearLayout)view.findViewById(R.id.ll_type_training)).setAlpha(1f);
				if (root.findViewById(R.id.iv_match) != null) {
					((ImageView)root.findViewById(R.id.iv_match)).setVisibility(View.GONE);
				}
				if (root.findViewById(R.id.iv_play) != null) {
					((ImageView)root.findViewById(R.id.iv_play)).setVisibility(View.VISIBLE);
				}
			}
			break;
		}
	}

	private void initializeDrawer() {
		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);

		notiferSaison = new NavigationDrawerSaisonNotifer();

		List<NavigationDrawerData> value = new ArrayList<NavigationDrawerAdapter.NavigationDrawerData>();
//		value.add(new NavigationDrawerAdapter.NavigationDrawerData(0, R.layout.fragment_navigation_drawer_element_saison, notiferSaison));

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), value);
		mNavigationDrawerFragment.setHeader(new NavigationDrawerAdapter.NavigationDrawerData(0, R.layout.fragment_navigation_drawer_header_saison, notiferSaison));
		mNavigationDrawerFragment.setFooter(new NavigationDrawerAdapter.NavigationDrawerData(1, R.layout.fragment_navigation_drawer_footer_type, new NavigationDrawerTypeNotifer()));
	}

	private void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						PlaceholderFragment.newInstance(position + 1)).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			setTitle(R.string.application_label);
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		boolean ret = true;

		switch (item.getItemId()) {
			case R.id.action_palmares_fast:
				onClickFastPalmares(null);
				break;
			case R.id.action_message:
				onClickMessage(null);
				break;
			case R.id.action_list_person:
				onClickListPerson(null);
				break;
			case R.id.action_send_apk:
				onClickSendApk(null);
				break;
			case R.id.action_send_db:
				onClickSendDb(null);
				break;
			case R.id.action_db_backup:
				onClickDBBackup(null);
				break;
			case R.id.action_db_restore:
				onClickDBRestore(null);
				break;
			default:
				ret = super.onMenuItemSelected(featureId, item);
		}

		return ret;
	}

	@Override
	public void onBackPressed() {
	    if (backPressedToExitOnce) {
	        super.onBackPressed();
	    } else if (this.toast == null) {
	        this.backPressedToExitOnce = true;
	        this.toast = Toast.makeText(this, R.string.press_again_to_exit, Toast.LENGTH_SHORT);
	        this.toast.show();
	        new Handler().postDelayed(new Runnable() {

	            @Override
	            public void run() {
	                backPressedToExitOnce = false;
	                MainActivity.this.toast = null;
	            }
	        }, 2000);
	    }
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode==RESULT_CODE_QRCODE_SCAN) {
			if (resultCode == RESULT_OK) {
				String qrcodeData = data.getStringExtra("SCAN_RESULT");
				String format = data.getStringExtra("SCAN_RESULT_FORMAT");
				// Handle successful scan
				Logger.logMe(TAG, "qrcodeData:"+qrcodeData);
				Logger.logMe(TAG, "format:"+format);
			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
			}
		}
	}

	@Override
	public void notifyError(Exception arg0) {
	}

	@Override
	public void notifyMessage(String arg0) {
	}

	public void onClickQRCodeScan(View view) {
		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
//		intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
//		intent.putExtra("SCAN_WIDTH", 800);
//		intent.putExtra("SCAN_HEIGHT", 400);
//		intent.putExtra("RESULT_DISPLAY_DURATION_MS", 3000L);
		intent.putExtra("RESULT_DISPLAY_DURATION_MS", 0L);
//		intent.putExtra("PROMPT_MESSAGE", "Custom prompt to scan a product");
		startActivityForResult(intent, RESULT_CODE_QRCODE_SCAN);
	}

	public void onClickUser(View view) {
		Intent intent = new Intent(getApplicationContext(), UserActivity.class);
		startActivity(intent);
	}
	
	public void onClickMessage(View view) {
		Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
		startActivity(intent);
	}
	
	public void onClickFastPalmares(View view) {
		Intent intent = new Intent(this, PalmaresFastActivity.class);
		startActivity(intent);
	}

	public void onClickListPlayer(View view) {
		Intent intent = new Intent(getApplicationContext(), ListPlayerActivity.class);
		intent.putExtra(ListPlayerActivity.EXTRA_MODE, MODE.EDIT);
		startActivity(intent);
	}
	
	public void onClickListPerson(View view) {
		Intent intent = new Intent(getApplicationContext(), ListPersonActivity.class);
		startActivity(intent);
	}

	public void onClickListPlayerInvite(View view) {
		Intent intent = new Intent(getApplicationContext(), ListPlayerActivity.class);
		intent.putExtra(ListPlayerActivity.EXTRA_MODE, MODE.INVITE);
		intent = new Intent(getApplicationContext(), MatchActivity.class);
		startActivity(intent);
	}

	public void onClickMatch(View view) {
		Intent intent = new Intent(getApplicationContext(), InviteActivity.class);
		intent.putExtra(InviteActivity.EXTRA_PLAYER_ID, business.getUnknownPlayerId());
		intent.putExtra(InviteActivity.EXTRA_MODE, InviteActivity.MODE.INVITE_SIMPLE);
		startActivity(intent);
	}

	public void onClickListInvite(View view) {
		Intent intent = null;
		switch(TypeManager.getInstance().getType()) {
			case COMPETITION:
				intent = new Intent(getApplicationContext(), ListCompetitionActivity.class);
			break;
			case TRAINING:
			default:
				intent = new Intent(getApplicationContext(), ListInviteActivity.class);
				break;
		}
		startActivity(intent);
	}

	public void onClickListStatistic(View view) {
		Intent intent = new Intent(getApplicationContext(), PieChartActivity.class);
		startActivity(intent);
	}

	public void onClickRotating(View view) {
		Intent intent = new Intent(getApplicationContext(), RotatingButtons.class);
		startActivity(intent);
	}

	public void onClickSendApk(View view) {
		OnClickSendApkListenerOk listener = new OnClickSendApkListenerOk(this);
		FactoryDialog.getInstance()
			.buildOkCancelDialog(business.getContext(), listener, R.string.dialog_send_apk_title, R.string.dialog_send_apk_message)
			.show();
	}

	public void onClickSendDb(View view) {
		OnClickSendDBListenerOk listener = new OnClickSendDBListenerOk(this);
		FactoryDialog.getInstance()
			.buildOkCancelDialog(business.getContext(), listener, R.string.dialog_send_db_title, R.string.dialog_send_db_message)
			.show();
	}
	
	public void onClickDBBackup(View view) {
		OnClickDBBackupListenerOk listener = new OnClickDBBackupListenerOk(this);
		FactoryDialog.getInstance()
			.buildOkCancelDialog(business.getContext(), listener, R.string.dialog_backup_title, R.string.dialog_backup_message)
			.show();
	}
	
	public void onClickDBRestore(View view) {
		OnClickDBRestoreListenerOk listener = new OnClickDBRestoreListenerOk(this);
		FactoryDialog.getInstance()
			.buildOkCancelDialog(business.getContext(), listener, R.string.dialog_restore_title, R.string.dialog_restore_message)
			.show();
	}

	public void onClickMenuOverFlow(View view) {
		int visibility = (menuOverFlowContent.getVisibility()==View.GONE) ? View.VISIBLE : View.GONE;
		menuOverFlowContent.setVisibility(visibility);
	}

	public void onClickSaisonAdd(View view) {
		OnClickViewListener onClickOkListener = new OnClickViewListener() {

			@Override
			public void onClick(DialogInterface dialog, View view, int which) {
				CheckBox cbActivate = (CheckBox) view.findViewById(R.id.cb_activate);
				DatePicker datePicker = (DatePicker) view.findViewById(R.id.dp_saison_year);
				int year = datePicker.getYear();
				if (!business.isExistSaison(year)) {
					boolean active = cbActivate.isChecked();
					Saison saison = business.createSaison(year, active);
					typeManager.setSaison(saison);

					business.initializeDataSaison();
					notiferSaison.initializeSaison();
				} else {
					Toast.makeText(MainActivity.this, R.string.error_message_saison_already_exist, Toast.LENGTH_LONG).show();
				}
			}
		};

		FactoryDialog.getInstance()
			.buildLayoutDialog(this, onClickOkListener, null, R.string.dialog_saison_add_title, R.layout.dialog_saison_year_picker, R.id.ll_main)
			.show();
	}

	public void onClickSaisonDel(View view) {
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Saison saison = typeManager.getSaison();
				if (!business.isEmptySaison(saison)) {
					if (!business.isExistInviteSaison(saison)) {
						business.deleteSaison(saison);
						typeManager.reinitialize(MainActivity.this.getApplicationContext(), MainActivity.this);

						business.initializeDataSaison();
						notiferSaison.initializeSaison();
					} else {
						Toast.makeText(MainActivity.this, R.string.error_message_invite_exist_saison, Toast.LENGTH_LONG).show();
					}
				}
			}
		};
		FactoryDialog.getInstance()
			.buildOkCancelDialog(business.getContext(), listener , R.string.dialog_saison_add_title, R.string.dialog_saison_del_message)
			.show();
	}

	public void onClickTypeTraining(View view) {
		typeManager.setType(TYPE.TRAINING);
		initializeLayoutType(view);
//		Intent intent = new Intent(getApplicationContext(), MatchActivity.class);
//		startActivity(intent);
	}

	public void onClickTypeMatch(View view) {
		typeManager.setType(TYPE.COMPETITION);
		initializeLayoutType(view);
	}

	private void handleSendMultipleDb(Intent intent) {
		ArrayList<Uri> uriList = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
		for(Uri uri : uriList) {
			String filePath = uri.getPath();
			Log.i(TAG, "handleSendMultipleDb filePath:'" + filePath + "'");
			Toast.makeText(this, filePath, Toast.LENGTH_SHORT).show();
			handleSendDb(filePath);
		}
	}

	private void handleSendDb(String filePath) {
		File file = new File(filePath);
		String databaseName = file.getAbsolutePath();
		int idx = databaseName.lastIndexOf(File.separatorChar);
		if (idx >= 0) {
			databaseName = databaseName.substring(idx + 1);
		}
		GenericDBHelper helper = business.getDBHelper(databaseName);
		if (helper != null) {
			Log.i(TAG, "handleSendDb DB Helper found for databaseName:'"+databaseName+"'");
			try {
				helper.restoreDbFromFile(file);
				file.delete();
			} catch (IOException e) {
				Log.e(TAG, "Restore '"+filePath+"' error", e);
			}
		} else {
			Log.w(TAG, "handleSendDb No DB Helper found for databaseName:'"+databaseName+"'");
		}
	}

	private final class NavigationDrawerTypeNotifer implements NavigationDrawerNotifer {

		@Override
		public void onCreateView(View view) {
			initializeLayoutType(view);
		}
	}

	private final class NavigationDrawerSaisonNotifer implements NavigationDrawerNotifer {
		private CustomArrayAdapter<String> adpSaison;
		private Spinner spSaison;
		private Context context;

		@Override
		public void onCreateView(View view) {
			context = view.getContext().getApplicationContext();

			spSaison = (Spinner)view.findViewById(R.id.sp_saison);
			initializeSaisonList();
			initializeSaison();
		}

		private void initializeSaisonList() {
			Log.d(TAG, "initializeSaisonList");
			adpSaison = new CustomArrayAdapter<String>(context, business.getListTxtSaisons());
			spSaison.setAdapter(adpSaison);

			spSaison.setOnItemSelectedListener(adpSaison.new OnItemSelectedListener<Saison>() {
				@Override
				public Saison getItem(int position) {
					return business.getListSaison().get(position);
				}

				@Override
				public boolean isHintItemSelected(Saison item) {
					return business.isEmptySaison(item);
				}

				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id, Saison item) {
					typeManager.setSaison(business.getListSaison().get(position));
				}
			});
		}

		public void initializeSaison() {
			Log.d(TAG, "initializeSaison");
			Saison saison = typeManager.getSaison();
			int position = 0;
			List<Saison> listSaison = business.getListSaison();
			for(Saison item : listSaison) {
				if (item.equals(saison)) {
					spSaison.setSelection(position, true);
					break;
				} else {
					position++;
				}
			}
			adpSaison.notifyDataSetChanged();
		}
	}
}