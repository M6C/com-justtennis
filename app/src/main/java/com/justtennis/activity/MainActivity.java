package com.justtennis.activity;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.cameleon.common.android.db.sqlite.helper.GenericDBHelper;
import com.cameleon.common.android.factory.FactoryDialog;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.R;
import com.justtennis.activity.notifier.NavigationDrawerCompetitionNotifer;
import com.justtennis.activity.notifier.NavigationDrawerTrainingNotifer;
import com.justtennis.activity.notifier.NavigationDrawerUserNotifer;
import com.justtennis.business.MainBusiness;
import com.justtennis.domain.Saison;
import com.justtennis.drawer.data.NavigationDrawerData;
import com.justtennis.drawer.manager.DrawerManager;
import com.justtennis.drawer.manager.notifier.IDrawerLayoutSaisonNotifier;
import com.justtennis.drawer.manager.notifier.IDrawerLayoutTypeNotifier;
import com.justtennis.listener.ok.OnClickDBBackupListenerOk;
import com.justtennis.listener.ok.OnClickDBRestoreListenerOk;
import com.justtennis.listener.ok.OnClickSendApkListenerOk;
import com.justtennis.listener.ok.OnClickSendDBListenerOk;
import com.justtennis.manager.TypeManager;
import com.justtennis.manager.TypeManager.TYPE;
import com.justtennis.tool.ExitTool;
import com.justtennis.tool.ToolPermission;
import com.justtennis.ui.common.CommonEnum;

import org.gdocument.gtracergps.launcher.log.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends GenericActivity implements INotifierMessage, IDrawerLayoutTypeNotifier, IDrawerLayoutSaisonNotifier {

	private static final String TAG = MainActivity.class.getSimpleName();
	private static final int RESULT_CODE_QRCODE_SCAN = 0;
	private static final int RESULT_CODE_USER = 1;
	private MainBusiness business;

	private View menuOverFlowContent;

	private DrawerManager drawerManager;
	private View layoutRoot;
	private List<NavigationDrawerData> navigationDrawerTraining = new ArrayList<NavigationDrawerData>();
	private List<NavigationDrawerData> navigationDrawerCompetition = new ArrayList<NavigationDrawerData>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		business = new MainBusiness(this, this);
		drawerManager = new DrawerManager(this, this);

		drawerManager.setContentView(R.layout.main_01);
		drawerManager.setDrawerLayoutTypeNotifier(this);
		drawerManager.setDrawerLayoutSaisonNotifier(this);
		layoutRoot = findViewById(R.id.root);

		navigationDrawerTraining.add(new NavigationDrawerData(0, R.layout.fragment_navigation_main_drawer_element_user, new NavigationDrawerUserNotifer(this)));
		navigationDrawerTraining.add(new NavigationDrawerData(1, R.layout.fragment_navigation_main_drawer_element_training, new NavigationDrawerTrainingNotifer(this)));
		navigationDrawerCompetition.add(new NavigationDrawerData(10, R.layout.fragment_navigation_main_drawer_element_user, new NavigationDrawerUserNotifer(this)));
		navigationDrawerCompetition.add(new NavigationDrawerData(11, R.layout.fragment_navigation_main_drawer_element_competition, new NavigationDrawerCompetitionNotifer(this)));

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

		if (business.getUser()==null) {
			Intent intent = new Intent(getApplicationContext(), UserActivity.class);
			startActivity(intent);

			finish();
		}
		drawerManager.onResume();
	}

	@Override
	public void onDrawerLayoutTypeChange(TYPE type) {
		initializeLayoutType(type);
	}

	@Override
	public void onDrawerLayoutSaisonChange(AdapterView<?> parent, View view, int position, long id, Saison item) {
		business.initializeData();
		refreshDrawer();
	}

	private void initializeLayoutType(TYPE type) {
		View root = layoutRoot;
		switch(type) {
			case COMPETITION: {
				if (root.findViewById(R.id.iv_match) != null) {
					((ImageView)root.findViewById(R.id.iv_match)).setVisibility(View.VISIBLE);
				}
				if (root.findViewById(R.id.iv_play) != null) {
					((ImageView)root.findViewById(R.id.iv_play)).setVisibility(View.GONE);
				}
				drawerManager.setValue(navigationDrawerCompetition);
			}
			break;

			case TRAINING:
			default: {
				if (root.findViewById(R.id.iv_match) != null) {
					((ImageView)root.findViewById(R.id.iv_match)).setVisibility(View.GONE);
				}
				if (root.findViewById(R.id.iv_play) != null) {
					((ImageView)root.findViewById(R.id.iv_play)).setVisibility(View.VISIBLE);
				}
				drawerManager.setValue(navigationDrawerTraining);
			}
			break;
		}
	}

	public void refreshDrawer() {
		drawerManager.updValue();
	}

	private void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!drawerManager.isDrawerOpen()) {
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
		if (ExitTool.onBackPressed(this)) {
			super.onBackPressed();
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
		} else if (requestCode == RESULT_CODE_USER) {
			business.initializeData();
			refreshDrawer();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case ToolPermission.MY_PERMISSIONS_REQUEST: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					if (permissions.length >0 && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
						doDBRestore();
					} else if (permissions.length >0 && permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
						doDBBackup();
					} else {
						logMe("Permission Unknown! permissions:" + permissions);
					}
				} else {
					logMe("Permission Denied ! Cancel initialization");
				}
				return;
			}
		}
	}

	private void doDBBackup() {
		OnClickDBBackupListenerOk listener = new OnClickDBBackupListenerOk(this);
		FactoryDialog.getInstance()
                .buildOkCancelDialog(business.getContext(), listener, R.string.dialog_backup_title, R.string.dialog_backup_message)
                .show();
	}

	private void doDBRestore() {
		OnClickDBRestoreListenerOk listener = new OnClickDBRestoreListenerOk(this);
		FactoryDialog.getInstance()
                .buildOkCancelDialog(business.getContext(), listener, R.string.dialog_restore_title, R.string.dialog_restore_message)
                .show();
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
		startActivityForResult(intent, RESULT_CODE_USER);
	}

	public void onClickListPlayer(View view) {
		Intent intent = new Intent(getApplicationContext(), ListPlayerActivity.class);
		intent.putExtra(ListPlayerActivity.EXTRA_MODE, CommonEnum.LIST_FRAGMENT_MODE.EDIT);
		startActivity(intent);
	}

	public void onClickListPlayerInvite(View view) {
		Intent intent = new Intent(getApplicationContext(), ListPlayerActivity.class);
		intent.putExtra(ListPlayerActivity.EXTRA_MODE, CommonEnum.LIST_FRAGMENT_MODE.INVITE);
//		intent = new Intent(getApplicationContext(), MatchActivity.class);
		startActivity(intent);
	}

	public void onClickMatch(View view) {
		Intent intent = new Intent(getApplicationContext(), InviteActivity.class);
		intent.putExtra(InviteActivity.EXTRA_PLAYER_ID, business.getUnknownPlayerId());
		intent.putExtra(InviteActivity.EXTRA_MODE, CommonEnum.INVITE_MODE.INVITE_SIMPLE);
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

	public void onClickFastPalmares(View view) {
		Intent intent = new Intent(this, PalmaresFastActivity.class);
		startActivity(intent);
	}

	public void onClickMessage(View view) {
		Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
		startActivity(intent);
	}

	public void onClickListPerson(View view) {
		Intent intent = new Intent(getApplicationContext(), ListPersonActivity.class);
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
		if (ToolPermission.checkPermissionWRITE_EXTERNAL_STORAGE(this)) {
			doDBBackup();
		}
	}

	public void onClickDBRestore(View view) {
		if (ToolPermission.checkPermissionREAD_EXTERNAL_STORAGE(this)) {
			doDBRestore();
		}
	}

	public void onClickMenuOverFlow(View view) {
		int visibility = (menuOverFlowContent.getVisibility()==View.GONE) ? View.VISIBLE : View.GONE;
		menuOverFlowContent.setVisibility(visibility);
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

	protected void logMe(String msg, Date dateStart) {
		logMe("ListPlayerActivity time:" + (new Date().getTime() - dateStart.getTime()) + " millisecond - " + msg);
	}

	protected static void logMe(String msg) {
		Logger.logMe(TAG, msg);
	}

	public MainBusiness getBusiness() {
		return business;
	}
}