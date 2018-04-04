package com.justtennis.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gdocument.gtracergps.launcher.log.Logger;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cameleon.common.android.db.sqlite.helper.GenericDBHelper;
import com.cameleon.common.android.factory.FactoryDialog;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.activity.ListPlayerActivity.MODE;
import com.justtennis.adapter.manager.RankingListManager;
import com.justtennis.adapter.manager.RankingListManager.IRankingListListener;
import com.justtennis.business.MainBusiness;
import com.justtennis.domain.ComputeDataRanking;
import com.justtennis.domain.Ranking;
import com.justtennis.domain.Saison;
import com.justtennis.domain.User;
import com.justtennis.drawer.adapter.NavigationDrawerAdapter.NavigationDrawerNotifer;
import com.justtennis.drawer.data.NavigationDrawerData;
import com.justtennis.drawer.manager.DrawerManager;
import com.justtennis.drawer.manager.DrawerManager.IDrawerLayoutSaisonNotifier;
import com.justtennis.drawer.manager.DrawerManager.IDrawerLayoutTypeNotifier;
import com.justtennis.listener.ok.OnClickDBBackupListenerOk;
import com.justtennis.listener.ok.OnClickDBRestoreListenerOk;
import com.justtennis.listener.ok.OnClickSendApkListenerOk;
import com.justtennis.listener.ok.OnClickSendDBListenerOk;
import com.justtennis.manager.TypeManager;
import com.justtennis.manager.TypeManager.TYPE;
import com.justtennis.R;

public class MainActivity extends GenericActivity implements INotifierMessage, IDrawerLayoutTypeNotifier, IDrawerLayoutSaisonNotifier {

	private static final String TAG = MainActivity.class.getSimpleName();
	private static final int RESULT_CODE_QRCODE_SCAN = 0;
	private static final int RESULT_CODE_USER = 1;
	private MainBusiness business;

	private View menuOverFlowContent;

	private boolean backPressedToExitOnce = false;
	private Toast toast = null;
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

		navigationDrawerTraining.add(new NavigationDrawerData(0, R.layout.fragment_navigation_main_drawer_element_user, new NavigationDrawerUserNotifer()));
		navigationDrawerTraining.add(new NavigationDrawerData(1, R.layout.fragment_navigation_main_drawer_element_training, new NavigationDrawerTrainingNotifer()));
		navigationDrawerCompetition.add(new NavigationDrawerData(10, R.layout.fragment_navigation_main_drawer_element_user, new NavigationDrawerUserNotifer()));
		navigationDrawerCompetition.add(new NavigationDrawerData(11, R.layout.fragment_navigation_main_drawer_element_competition, new NavigationDrawerCompetitionNotifer()));

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

	private void refreshDrawer() {
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
		} else if (requestCode == RESULT_CODE_USER) {
			business.initializeData();
			refreshDrawer();
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
		startActivityForResult(intent, RESULT_CODE_USER);
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

	private final class NavigationDrawerTrainingNotifer implements NavigationDrawerNotifer {

		private TextView tvMatchValue;
		private TextView tvMatchMax;
		private ProgressBar pbMatch;

		@Override
		public void onCreateView(View view) {
			tvMatchValue = (TextView) view.findViewById(R.id.tv_match_value);
			tvMatchMax = (TextView) view.findViewById(R.id.tv_match_max);
			pbMatch = (ProgressBar) view.findViewById(R.id.pb_match);

			initiazeView(view);
		}

		@Override
		public void onUpdateView(View view) {
			initiazeView(view);
		}

		private void initiazeView(View view) {
			ComputeDataRanking dataRanking = business.getDataRanking();

			tvMatchMax.setText(Integer.toString(dataRanking.getNbMatch()));
			tvMatchValue.setText(Integer.toString(dataRanking.getNbVictoryCalculate()));
			pbMatch.setMax(dataRanking.getNbMatch());
			pbMatch.setProgress(dataRanking.getNbVictoryCalculate());
		}
	}

	private final class NavigationDrawerUserNotifer implements NavigationDrawerNotifer {

		private RankingListManager rankingListManager;
		private TextView tvName;

		@Override
		public void onCreateView(View view) {
			rankingListManager = RankingListManager.getInstance(MainActivity.this, MainActivity.this);
			tvName = (TextView) view.findViewById(R.id.tv_name);

			User user = business.getUser();
			if (user != null) {
				tvName.setText(user.getFullName());
			}
			manageRanking(MainActivity.this, view, user, true);
			manageRanking(MainActivity.this, view, user, false);
		}

		@Override
		public void onUpdateView(View view) {
//			User user = business.getUser();
//			tvName.setText(user.getFullName());
//			rankingListManager.initializeRankingSpinner(view, user, true);
//			rankingListManager.initializeRankingSpinner(view, user, false);
		}

		private void manageRanking(MainActivity mainActivity, View view, User user, final boolean estimate) {
			IRankingListListener listener = new IRankingListListener() {
				@Override
				public void onRankingSelected(Ranking ranking) {
					Long oldId = null;
					User user = business.getUser();
					if (user == null) {
						return;
					}
					if (estimate) {
						oldId = user.getIdRankingEstimate();
						if (ranking.equals(business.getRankingNC())) {
							user.setIdRankingEstimate(null);
						} else {
							user.setIdRankingEstimate(ranking.getId());
						}
					} else {
						oldId = user.getIdRanking();
						if (ranking.equals(business.getRankingNC())) {
							user.setIdRanking(null);
						} else {
							user.setIdRanking(ranking.getId());
						}
					}
					if (oldId != null && !oldId.equals(ranking.getId())) {
						refreshDrawer();
					}
				}
			};
			rankingListManager.manageRanking(MainActivity.this, view, listener, user, estimate);
		}
	}

	private final class NavigationDrawerCompetitionNotifer implements NavigationDrawerNotifer {

		private TextView tvMatchValue;
		private TextView tvMatchMax;
		private ProgressBar pbMatch;
		private TextView tvPointValue;
		private TextView tvPointMax;
		private ProgressBar pbPoint;

		@Override
		public void onCreateView(View view) {
			tvMatchValue = (TextView) view.findViewById(R.id.tv_match_value);
			tvMatchMax = (TextView) view.findViewById(R.id.tv_match_max);
			pbMatch = (ProgressBar) view.findViewById(R.id.pb_match);
			tvPointValue = (TextView) view.findViewById(R.id.tv_point_value);
			tvPointMax = (TextView) view.findViewById(R.id.tv_point_max);
			pbPoint = (ProgressBar) view.findViewById(R.id.pb_point);

			initiazeView(view);
		}

		@Override
		public void onUpdateView(View view) {
//			initiazeView(view);
		}

		private void initiazeView(View view) {
			ComputeDataRanking dataRanking = business.getDataRanking();
			int point = dataRanking.getPointCalculate() + dataRanking.getPointBonus();
			int nbVictory = dataRanking.getListInviteCalculed().size() + dataRanking.getListInviteNotUsed().size();

			tvMatchMax.setText(Integer.toString(dataRanking.getNbMatch()));
			tvMatchValue.setText(Integer.toString(nbVictory));
			pbMatch.setMax(dataRanking.getNbMatch());
			pbMatch.setProgress(nbVictory);
			tvPointMax.setText(Integer.toString(dataRanking.getPointObjectif()));
			tvPointValue.setText(Integer.toString(point));
			pbPoint.setMax(point > dataRanking.getPointObjectif() ? point : dataRanking.getPointObjectif());
			pbPoint.setProgress(point);
		}
	}
}