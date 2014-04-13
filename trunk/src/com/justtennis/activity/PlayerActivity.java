
package com.justtennis.activity;

import java.io.Serializable;
import java.util.List;

import org.gdocument.gtracergps.launcher.log.Logger;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cameleon.common.android.adapter.BaseViewAdapter;
import com.cameleon.common.android.factory.FactoryDialog;
import com.justtennis.R;
import com.justtennis.business.PlayerBusiness;
import com.justtennis.domain.Club;
import com.justtennis.domain.Player;
import com.justtennis.domain.Player.PLAYER_TYPE;
import com.justtennis.domain.Ranking;
import com.justtennis.domain.Tournament;
import com.justtennis.listener.action.TextWatcherFieldEnableView;
import com.justtennis.listener.ok.OnClickPlayerCreateListenerOk;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.parser.PlayerParser;

public class PlayerActivity extends Activity {

	public enum MODE {
		CREATE,
		MODIFY,
		DEMANDE_ADD,
		FOR_RESULT
	};

	private static final String TAG = PlayerActivity.class.getSimpleName();
	private static final int RESULT_CODE_QRCODE_SCAN = 0;
	private static final int RESULT_CODE_GOOGLE = 1;
	private static final int RESULT_LOCATION = 2;
	public static final String EXTRA_PLAYER = "PLAYER";
	public static final String EXTRA_PLAYER_ID = "PLAYER_ID";
	public static final String EXTRA_INVITE = "INVITE";
	public static final String EXTRA_MODE = "MODE";
	public static final String EXTRA_TYPE = "TYPE";
	public static final String EXTRA_RANKING = "RANKING";

	private final Integer[] drawableType = new Integer[] {R.layout.element_invite_type_entrainement, R.layout.element_invite_type_match};

	private PlayerBusiness business;

	private TextView tvFirstname;
	private TextView tvLastname;
	private TextView tvBirthday;
	private TextView tvPhonenumber;
	private EditText etFirstname;
	private EditText etLastname;
	private EditText etBirthday;
	private EditText etPhonenumber;
	private BaseViewAdapter adapterType;
	private Spinner spRanking;
	private Spinner spType;
	private LinearLayout llLastname;
	private LinearLayout llBirthday;
	private LinearLayout llPhonenumber;
	private LinearLayout llRanking;
	private LinearLayout llType;
	private LinearLayout llCreate;
	private LinearLayout llModify;
	private LinearLayout llAddDemande;

	private TextView tvLocation;
	private TextView tvLocationEmpty;
	private LinearLayout llLocationDetail;
	private TextView tvLocationName;
	private TextView tvLocationLine1;
	private TextView tvLocationLine2;

	private boolean fromQrCode = false;
	private Serializable locationFromResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);

		tvFirstname = (TextView)findViewById(R.id.tv_firstname);
		tvLastname = (TextView)findViewById(R.id.tv_lastname);
		tvBirthday = (TextView)findViewById(R.id.tv_birthday);
		tvPhonenumber = (TextView)findViewById(R.id.tv_phonenumber);
		etFirstname = (EditText)findViewById(R.id.et_firstname);
		etLastname = (EditText)findViewById(R.id.et_lastname);
		etBirthday = (EditText)findViewById(R.id.et_birthday);
		etPhonenumber = (EditText)findViewById(R.id.et_phonenumber);
		spRanking = (Spinner)findViewById(R.id.sp_ranking);
		spType = (Spinner)findViewById(R.id.sp_type);
		llLastname = (LinearLayout)findViewById(R.id.ll_lastname);
		llBirthday = (LinearLayout)findViewById(R.id.ll_birthday);
		llPhonenumber = (LinearLayout)findViewById(R.id.ll_phonenumber);
		llRanking = (LinearLayout)findViewById(R.id.ll_ranking);
		llType = (LinearLayout)findViewById(R.id.ll_type);
		llCreate = (LinearLayout)findViewById(R.id.ll_create);
		llModify = (LinearLayout)findViewById(R.id.ll_modify);
		llAddDemande = (LinearLayout)findViewById(R.id.ll_add_demande);
		tvLocation = ((TextView)findViewById(R.id.tv_location));
		tvLocationEmpty = ((TextView)findViewById(R.id.et_location));
		llLocationDetail = (LinearLayout)findViewById(R.id.ll_location_detail);
		tvLocationName = ((TextView)findViewById(R.id.tv_location_name));
		tvLocationLine1 = ((TextView)findViewById(R.id.tv_location_line1));
		tvLocationLine2 = ((TextView)findViewById(R.id.tv_location_line2));

		initializeListener();
		
		business = new PlayerBusiness(this, NotifierMessageLogger.getInstance());
	}

	@Override
	protected void onResume() {
		super.onResume();
		initialize();

		initializeRankingList();
		initializeRanking();

		initializeListType();
		initializeType();

		initializeLocation();
	}

	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode==RESULT_CODE_QRCODE_SCAN) {
			if (resultCode == RESULT_OK) {
				String qrcodeData = data.getStringExtra("SCAN_RESULT");
//				String format = data.getStringExtra("SCAN_RESULT_FORMAT");
				// Handle successful scan
				Logger.logMe(TAG, qrcodeData);
				
				Player player = PlayerParser.getInstance().fromData(qrcodeData);

				initializeView(player);
				
				fromQrCode = true;
			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
			}
		} else if (requestCode==RESULT_CODE_GOOGLE) {
			if (resultCode == RESULT_OK) {
				Player player = (Player) data.getSerializableExtra(ListPersonActivity.EXTRA_PLAYER);

				initializeView(player);
				
				fromQrCode = false;
			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
			}
		} else if (requestCode==RESULT_LOCATION) {
			if (data != null) {
				locationFromResult = data.getSerializableExtra(LocationActivity.EXTRA_OUT_LOCATION);
			}
		}
	}

	public void onClickCreate(View view) {
		buildPlayer();

		MODE mode = business.getMode();
		switch (mode) {
			case FOR_RESULT:
				business.create(false);
				Intent intent = new Intent();
				intent.putExtra(EXTRA_PLAYER_ID, business.getPlayer().getId());
				setResult(0, intent);
				finish();
				break;
			default:
				if (fromQrCode) {
					business.create(true);
					finish();
				}
				else {
					OnClickPlayerCreateListenerOk listener = new OnClickPlayerCreateListenerOk(this, business);
					FactoryDialog.getInstance()
						.buildYesNoDialog(this, listener, R.string.dialog_player_create_confirmation_title, R.string.dialog_player_create_confirmation_message)
						.show();
				}
			}
	}
	
	public void onClickModify(View view) {
		buildPlayer();

		business.modify();
		
		finish();
	}
	
	public void onClickQRCode(View view) {
		buildPlayer();

		String qrcodeData = business.toQRCode();

		Intent intent = new Intent(getApplicationContext(), QRCodeActivity.class);
		intent.putExtra(QRCodeActivity.EXTRA_QRCODE_DATA, qrcodeData);
		startActivity(intent);
	}

	public void onClickImport(View view) {
		String[] listPhonenumber = new String[] {
				getString(R.string.button_text_scan),
				getString(R.string.txt_google)
		};
		Dialog dialog = FactoryDialog.getInstance().buildListView(this, R.string.txt_import, listPhonenumber, new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch(position) {
					case 0:
						importScan();
						break;
					case 1:
						importGoogle();
						break;
				}
			}
		});
//		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.show();
	}
	
	public void onClickDemandeAddYes(View view) {
		business.demandeAddYes();
		finish();
	}
	
	public void onClickDemandeAddNo(View view) {
		business.demandeAddNo();
		finish();
	}
	
	public void onClickLocation(View view) {
		Intent intent = null;
		switch(business.getPlayer().getType()) {
			case ENTRAINEMENT:
				intent = new Intent(this, LocationClubActivity.class);
				if (business.getPlayer().getIdClub() != null) {
					intent.putExtra(GenericSpinnerFormActivity.EXTRA_DATA, new Club(business.getPlayer().getIdClub()));
				}
				break;
			case MATCH:
				intent = new Intent(this, LocationTournamentActivity.class);
				if (business.getPlayer().getIdClub() != null) {
					intent.putExtra(GenericSpinnerFormActivity.EXTRA_DATA, new Tournament(business.getPlayer().getIdClub()));
				}
				break;
		}
		if (intent != null) {
			startActivityForResult(intent, RESULT_LOCATION);
		}
	}

	private void importScan() {
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

	private void importGoogle() {
		Intent intent = new Intent(getApplicationContext(), ListPersonActivity.class);
		startActivityForResult(intent, RESULT_CODE_GOOGLE);
	}

	private void buildPlayer() {
		Player player = business.initializePlayer();

		player.setFirstName(etFirstname.getText().toString());
		player.setLastName(etLastname.getText().toString());
		player.setBirthday(etBirthday.getText().toString());
		player.setPhonenumber(etPhonenumber.getText().toString());
	}

	private void initialize() {
		Intent intent = getIntent();
		business.initialize(intent);

		Player player = business.getPlayer();
		MODE mode = business.getMode();

		switch (mode) {
			case FOR_RESULT:
			case CREATE:
				llCreate.setVisibility(View.VISIBLE);
				llModify.setVisibility(View.GONE);
				llAddDemande.setVisibility(View.GONE);
				break;
			case MODIFY:
				llCreate.setVisibility(View.GONE);
				llModify.setVisibility(View.VISIBLE);
				llAddDemande.setVisibility(View.GONE);
				break;
			case DEMANDE_ADD:
				llCreate.setVisibility(View.GONE);
				llModify.setVisibility(View.GONE);
				llAddDemande.setVisibility(View.VISIBLE);
				
				player = business.getInvite().getUser();
				break;
		}

		if (player!=null) {
			initializeView(player);
		}
	}

	private void initializeListener() {
		etFirstname.addTextChangedListener(new TextWatcherFieldEnableView(tvFirstname, View.GONE));
		etLastname.addTextChangedListener(new TextWatcherFieldEnableView(tvLastname, View.GONE));
		etBirthday.addTextChangedListener(new TextWatcherFieldEnableView(tvBirthday, View.GONE));
		etPhonenumber.addTextChangedListener(new TextWatcherFieldEnableView(tvPhonenumber, View.GONE));
	}

	private void initializeView(Player player) {
		boolean bEditable = !business.isUnknownPlayer(player);
		int iVisibility = (business.isUnknownPlayer(player) ? View.GONE : View.VISIBLE);

		etFirstname.setEnabled(bEditable);
		llLastname.setVisibility(iVisibility);
		llBirthday.setVisibility(iVisibility);
		llPhonenumber.setVisibility(iVisibility);
		llRanking.setVisibility(iVisibility);
		llType.setVisibility(iVisibility);

		etFirstname.setText(player.getFirstName());
		etLastname.setText(player.getLastName());
		etBirthday.setText(player.getBirthday());
		etPhonenumber.setText(player.getPhonenumber());
	}

	private void initializeListType() {
		adapterType = new BaseViewAdapter(this, drawableType);
		adapterType.setViewBinder(new BaseViewAdapter.ViewBinder() {
			
			@Override
			public boolean setViewValue(int position, View view) {
				view.setTag(getType(position));
				return true;
			}
		});
		spType.setAdapter(adapterType);

		spType.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (view != null) {
					Player player = business.initializePlayer();
					player.setType((PLAYER_TYPE) view.getTag());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	private void initializeRankingList() {
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, business.getListTxtRankings());
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spRanking.setAdapter(dataAdapter);

		spRanking.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Ranking ranking = business.getListRanking().get(position);
				Player player = business.initializePlayer();
				player.setIdRanking(ranking.getId());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	private void initializeRanking() {
		Player player = business.getPlayer();
		if (player!=null) {
			Long id = player.getIdRanking();
			int position = 0;
			List<Ranking> listRanking = business.getListRanking();
			for(Ranking ranking : listRanking) {
				if (ranking.getId().equals(id)) {
					spRanking.setSelection(position, true);
					break;
				} else {
					position++;
				}
			}
		}
	}

	private void initializeType() {
		spType.setSelection(getTypePosition());
	}

	private void initializeLocation() {
		Log.d(TAG, "initializeDataLocation");
		if (locationFromResult != null) {
			business.setLocation(locationFromResult);
			locationFromResult = null;
		}

		String[] location = business.getLocationLine();
		if (getType() == PLAYER_TYPE.ENTRAINEMENT) {
			tvLocation.setText(getString(R.string.txt_club));
			tvLocationEmpty.setText(getString(R.string.txt_club));
		} else {
			tvLocation.setText(getString(R.string.txt_tournament));
			tvLocationEmpty.setText(getString(R.string.txt_tournament));
		}

		if (location != null) {
			tvLocationName.setText(location[0]);
			tvLocationLine1.setText(location[1]);
			tvLocationLine2.setText(location[2]);
			tvLocation.setVisibility(View.VISIBLE);
			llLocationDetail.setVisibility(View.VISIBLE);
			tvLocationEmpty.setVisibility(View.GONE);
		} else {
			tvLocation.setVisibility(View.GONE);
			llLocationDetail.setVisibility(View.GONE);
			tvLocationEmpty.setVisibility(View.VISIBLE);
		}
	}

	private int getTypePosition() {
		if(business.getPlayer() != null) {
			switch(business.getPlayer().getType()) {
				case ENTRAINEMENT:
					return 0;
				case MATCH:
				default:
					return 1;
			}
		} else {
			return 0;
		}
	}

	private PLAYER_TYPE getType() {
		return business.getPlayer().getType();
	}
	
	private PLAYER_TYPE getType(Integer position) {
		switch(position) {
		case 0:
			return PLAYER_TYPE.ENTRAINEMENT;
		case 1:
		default:
			return PLAYER_TYPE.MATCH;
		}
	}
}