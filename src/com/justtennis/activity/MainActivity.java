package com.justtennis.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gdocument.gtracergps.launcher.log.Logger;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
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
import com.justtennis.adapter.CustomArrayAdapter;
import com.justtennis.business.MainBusiness;
import com.justtennis.domain.Saison;
import com.justtennis.listener.ok.OnClickDBBackupListenerOk;
import com.justtennis.listener.ok.OnClickDBRestoreListenerOk;
import com.justtennis.listener.ok.OnClickExitListenerOk;
import com.justtennis.listener.ok.OnClickSendApkListenerOk;
import com.justtennis.listener.ok.OnClickSendDBListenerOk;
import com.justtennis.manager.TypeManager;
import com.justtennis.manager.TypeManager.TYPE;

public class MainActivity extends GenericActivity implements INotifierMessage {

	private static final String TAG = MainActivity.class.getSimpleName();
	private static final int RESULT_CODE_QRCODE_SCAN = 0;
	private MainBusiness business;
	private Dialog dialogExit;

	private RelativeLayout layoutMain;
	private LinearLayout llTypeEntrainement;
	private LinearLayout llTypeMatch;
	private TypeManager typeManager;
	private View menuOverFlowContent;
	private ImageView ivPlay;
	private ImageView ivMatch;
	private Spinner spSaison;
	private CustomArrayAdapter<String> adpSaison;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_01);

		layoutMain = (RelativeLayout)findViewById(R.id.layout_main);
		llTypeEntrainement = (LinearLayout)findViewById(R.id.ll_type_training);
		llTypeMatch = (LinearLayout)findViewById(R.id.ll_type_match);
		ivPlay = (ImageView)findViewById(R.id.iv_play);
		ivMatch = (ImageView)findViewById(R.id.iv_match);
		menuOverFlowContent = findViewById(R.id.ll_menu_overflow_content);
		spSaison = (Spinner)findViewById(R.id.sp_saison);

		business = new MainBusiness(this, this);
		typeManager = TypeManager.getInstance(this.getApplicationContext(), this);

		dialogExit = FactoryDialog.getInstance().buildYesNoDialog(
			this, new OnClickExitListenerOk(this), R.string.dialog_exit_title, R.string.dialog_exit_message);

		typeManager.initializeActivity(layoutMain, true);

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
		initializeLayoutType();

		if (business.getUserCount()==0) {
			Intent intent = new Intent(getApplicationContext(), UserActivity.class);
			startActivity(intent);
			
			finish();
		}
	}

	private void initializeData() {
		initializeSaisonList();
		initializeSaison();
	}

	private void initializeLayoutType() {
		switch(typeManager.getType()) {
			case COMPETITION: {
				layoutMain.setBackgroundResource(R.drawable.background_01_orange);
				llTypeMatch.setAlpha(1f);
				llTypeEntrainement.setAlpha(.2f);
				ivMatch.setVisibility(View.VISIBLE);
				ivPlay.setVisibility(View.GONE);
			}
			break;

			case TRAINING:
			default: {
				layoutMain.setBackgroundResource(R.drawable.background_01);
				llTypeEntrainement.setAlpha(1f);
				llTypeMatch.setAlpha(.2f);
				ivPlay.setVisibility(View.VISIBLE);
				ivMatch.setVisibility(View.GONE);
			}
			break;
		}
	}

	private void initializeSaisonList() {
		Log.d(TAG, "initializeSaisonList");
		adpSaison = new CustomArrayAdapter<String>(this, business.getListTxtSaisons());
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

	private void initializeSaison() {
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		dialogExit.show();
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
		startActivity(intent);
	}

	public void onClickMatch(View view) {
		Intent intent = new Intent(getApplicationContext(), InviteDemandeActivity.class);
		intent.putExtra(InviteActivity.EXTRA_PLAYER_ID, business.getUnknownPlayerId());
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
					adpSaison.notifyDataSetChanged();
					initializeSaison();
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
						adpSaison.notifyDataSetChanged();
						initializeSaison();
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
		initializeLayoutType();
	}
	
	public void onClickTypeMatch(View view) {
		typeManager.setType(TYPE.COMPETITION);
		initializeLayoutType();
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
}