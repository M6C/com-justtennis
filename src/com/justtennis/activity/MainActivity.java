package com.justtennis.activity;

import org.gdocument.gtracergps.launcher.log.Logger;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.cameleon.common.android.factory.FactoryDialog;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.R;
import com.justtennis.activity.ListPlayerActivity.MODE;
import com.justtennis.business.MainBusiness;
import com.justtennis.listener.ok.OnClickDBBackupListenerOk;
import com.justtennis.listener.ok.OnClickDBRestoreListenerOk;
import com.justtennis.listener.ok.OnClickExitListenerOk;

public class MainActivity extends Activity implements INotifierMessage {

	private static final String TAG = MainActivity.class.getSimpleName();
	private static final int RESULT_CODE_QRCODE_SCAN = 0;
	private MainBusiness business;
	private Dialog dialogExit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_01);
		
		business = new MainBusiness(this, this);

		dialogExit = FactoryDialog.getInstance().buildYesNoDialog(
			this, new OnClickExitListenerOk(this), R.string.dialog_exit_title, R.string.dialog_exit_message);
	}

	@Override
	protected void onResume() {
		super.onResume();

		business.onResume();

		if (business.getUserCount()==0) {
			Intent intent = new Intent(getApplicationContext(), UserActivity.class);
			startActivity(intent);
			
			finish();
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
				Logger.logMe(TAG, qrcodeData);
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
	
	public void onClickListInvite(View view) {
		Intent intent = new Intent(getApplicationContext(), ListInviteActivity.class);
		startActivity(intent);
	}
	
	public void onClickRotating(View view) {
		Intent intent = new Intent(getApplicationContext(), RotatingButtons.class);
		startActivity(intent);
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
		View menuOverFlowContent = findViewById(R.id.ll_menu_overflow_content);
		int visibility = (menuOverFlowContent.getVisibility()==View.GONE) ? View.VISIBLE : View.GONE;
		menuOverFlowContent.setVisibility(visibility);
	}
}