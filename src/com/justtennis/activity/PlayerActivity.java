
package com.justtennis.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.gdocument.gtracergps.launcher.log.Logger;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.cameleon.common.android.adapter.BaseViewAdapter;
import com.cameleon.common.android.factory.FactoryDialog;
import com.justtennis.ApplicationConfig;
import com.justtennis.adapter.CustomArrayAdapter;
import com.justtennis.adapter.NavigationDrawerAdapter;
import com.justtennis.adapter.NavigationDrawerAdapter.NavigationDrawerData;
import com.justtennis.adapter.NavigationDrawerAdapter.NavigationDrawerNotifer;
import com.justtennis.adapter.manager.RankingListManager;
import com.justtennis.business.PlayerBusiness;
import com.justtennis.db.service.RechercheService;
import com.justtennis.domain.Club;
import com.justtennis.domain.Player;
import com.justtennis.domain.RechercheResult;
import com.justtennis.domain.Saison;
import com.justtennis.domain.Tournament;
import com.justtennis.listener.action.TextWatcherFieldEnableView;
import com.justtennis.listener.ok.OnClickPlayerCreateListenerOk;
import com.justtennis.manager.DrawerManager;
import com.justtennis.manager.DrawerManager.IDrawerLayoutSaisonNotifier;
import com.justtennis.manager.DrawerManager.IDrawerLayoutTypeNotifier;
import com.justtennis.manager.TypeManager;
import com.justtennis.manager.TypeManager.TYPE;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.parser.PlayerParser;
import com.justtennis.R;

public class PlayerActivity extends GenericActivity implements IDrawerLayoutTypeNotifier, IDrawerLayoutSaisonNotifier {

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
	public static final String EXTRA_FIND = "EXTRA_FIND";

	private final Integer[] drawableType = new Integer[] {R.layout.element_invite_type_entrainement, R.layout.element_invite_type_match};
	private List<NavigationDrawerData> navigationDrawer = new ArrayList<NavigationDrawerAdapter.NavigationDrawerData>();

	private Bundle savedInstanceState;
	private PlayerBusiness business;
	private DrawerManager drawerManager;
	private RankingListManager rankingListManager;

	private TextView tvFirstname;
	private TextView tvLastname;
	private TextView tvBirthday;
	private TextView tvPhonenumber;
	private EditText etFirstname;
	private EditText etLastname;
	private EditText etBirthday;
	private EditText etPhonenumber;
	private BaseViewAdapter adapterType;
	private Spinner spType;
	private Spinner spSaison;
	private LinearLayout llLastname;
	private LinearLayout llBirthday;
	private LinearLayout llPhonenumber;
	private LinearLayout llRanking;
	private LinearLayout llRankingEstimate;
	private LinearLayout llType;
	private LinearLayout llCreate;
	private LinearLayout llModify;
	private LinearLayout llAddDemande;
	private LinearLayout llMessage;

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
		if (this.savedInstanceState==null) {
			this.savedInstanceState = savedInstanceState;
		}
		NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
		drawerManager = new DrawerManager(this, notifier);
		navigationDrawer.add(new NavigationDrawerData(0, R.layout.fragment_navigation_player_drawer_element_recherche, new NavigationDrawerRechercheNotifer(this, notifier)));

		initializeLayoutView();
		initializeViewById();
		
		business = createBusiness();
		rankingListManager = RankingListManager.getInstance(this, notifier);

		initializeListener();
		initialize();

		initializeListType();
	}

	@Override
	protected void onResume() {
		super.onResume();

		initializeData(true);

		drawerManager.onResume();
		drawerManager.setDrawerLayoutSaisonNotifier(this);
		drawerManager.setDrawerLayoutTypeNotifier(this);
		drawerManager.setValue(navigationDrawer);
	}

	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}

	@Override
	public void onDrawerLayoutSaisonChange(AdapterView<?> parent, View view, int position, long id, Saison item) {
		business.initialize(new Intent());

		initializeData(false);
	}

	@Override
	public void onDrawerLayoutTypeChange(TYPE type) {
		//TODO Useless - Fix refresh button color
		int style = (type == TYPE.COMPETITION ? R.style.StyleButton_Competition : R.style.StyleButton);
		((Button)findViewById(R.id.btn_import)).setTypeface(Typeface.DEFAULT, style);
		((Button)findViewById(R.id.btn_qrcode)).setTypeface(Typeface.DEFAULT, style);
		((Button)findViewById(R.id.btn_add_demande_yes)).setTypeface(Typeface.DEFAULT, style);
		((Button)findViewById(R.id.btn_add_demande_no)).setTypeface(Typeface.DEFAULT, style);

		business.initialize(new Intent());

		initializeData(false);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode==RESULT_CODE_QRCODE_SCAN) {
			if (resultCode == RESULT_OK && data != null) {
				String qrcodeData = data.getStringExtra("SCAN_RESULT");
//				String format = data.getStringExtra("SCAN_RESULT_FORMAT");
				// Handle successful scan
				Logger.logMe(TAG, qrcodeData);
				
				Player player = PlayerParser.getInstance().fromData(qrcodeData);
				business.initializePlayerSaison(player);
				business.setPlayer(player);
				
				fromQrCode = true;
			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
			}
		} else if (requestCode==RESULT_CODE_GOOGLE) {
			if (resultCode == RESULT_OK && data != null) {
				Player player = (Player) data.getSerializableExtra(ListPersonActivity.EXTRA_PLAYER);
				business.initializePlayerSaison(player);
				business.setPlayer(player);
				
				fromQrCode = false;
			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
			}
		} else if (requestCode==RESULT_LOCATION) {
			if (resultCode == RESULT_OK && data != null) {
				locationFromResult = data.getSerializableExtra(LocationActivity.EXTRA_OUT_LOCATION);
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		business.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

	private void initializeData(boolean listener) {
		initializeView();
		initializeRankingList();
		initializeRankingEstimateList();

		initializeType();

		if (listener) {
			initializeListenerListType();
		}
		initializeLocation();

		if (listener) {
			initializeSaisonList();
		}
		initializeSaison();
	}

	protected void initializeViewById() {
		tvFirstname = (TextView)findViewById(R.id.tv_firstname);
		tvLastname = (TextView)findViewById(R.id.tv_lastname);
		tvBirthday = (TextView)findViewById(R.id.tv_birthday);
		tvPhonenumber = (TextView)findViewById(R.id.tv_phonenumber);
		etFirstname = (EditText)findViewById(R.id.et_firstname);
		etLastname = (EditText)findViewById(R.id.et_lastname);
		etBirthday = (EditText)findViewById(R.id.et_birthday);
		etPhonenumber = (EditText)findViewById(R.id.et_phonenumber);
		spType = (Spinner)findViewById(R.id.sp_type);
		spSaison = (Spinner)findViewById(R.id.sp_saison);
		llLastname = (LinearLayout)findViewById(R.id.ll_lastname);
		llBirthday = (LinearLayout)findViewById(R.id.ll_birthday);
		llPhonenumber = (LinearLayout)findViewById(R.id.ll_phonenumber);
		llRanking = (LinearLayout)findViewById(R.id.ll_ranking);
		llRankingEstimate = (LinearLayout)findViewById(R.id.ll_ranking_estimate);
		llType = (LinearLayout)findViewById(R.id.ll_type);
		llCreate = (LinearLayout)findViewById(R.id.ll_create);
		llModify = (LinearLayout)findViewById(R.id.ll_modify);
		llAddDemande = (LinearLayout)findViewById(R.id.ll_add_demande);
		llMessage = (LinearLayout)findViewById(R.id.ll_message);
		tvLocation = ((TextView)findViewById(R.id.tv_location));
		tvLocationEmpty = ((TextView)findViewById(R.id.et_location));
		llLocationDetail = (LinearLayout)findViewById(R.id.ll_location_detail);
		tvLocationName = ((TextView)findViewById(R.id.tv_location_name));
		tvLocationLine1 = ((TextView)findViewById(R.id.tv_location_line1));
		tvLocationLine2 = ((TextView)findViewById(R.id.tv_location_line2));
	}

	public void onClickCreate(View view) {
		updatePlayerData();

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
					if (business.sendMessageConfirmation()) {
						OnClickPlayerCreateListenerOk listener = new OnClickPlayerCreateListenerOk(this, business);
						FactoryDialog.getInstance()
							.buildYesNoDialog(this, listener, R.string.dialog_player_create_confirmation_title, R.string.dialog_player_create_confirmation_message)
							.show();
					} else {
						business.create(false);
						finish();
					}
				}
			}
	}
	
	public void onClickModify(View view) {
		updatePlayerData();

		business.modify();
		
		finish();
	}
	
	public void onClickQRCode(View view) {
		updatePlayerData();

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
		updatePlayerData();
		Intent intent = null;
		switch(getType()) {
			case TRAINING:
				intent = new Intent(this, LocationClubActivity.class);
				if (business.getPlayer().getIdClub() != null) {
					intent.putExtra(GenericSpinnerFormActivity.EXTRA_DATA, new Club(business.getPlayer().getIdClub()));
				}
				break;
			case COMPETITION:
				intent = new Intent(this, LocationTournamentActivity.class);
				if (business.getPlayer().getIdTournament() != null) {
					intent.putExtra(GenericSpinnerFormActivity.EXTRA_DATA, new Tournament(business.getPlayer().getIdTournament()));
				}
				break;
		}
		if (intent != null) {
			startActivityForResult(intent, RESULT_LOCATION);
		}
	}

	public void onClickLocationDetail(View view) {
		onClickLocation(view);
	}

	protected PlayerBusiness createBusiness() {
		return new PlayerBusiness(this, NotifierMessageLogger.getInstance());
	}

	protected void initializeLayoutView() {
		drawerManager.setContentView(R.layout.player);
	}

	private void importScan() {
		updatePlayerData();
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
		updatePlayerData();
		Intent intent = new Intent(getApplicationContext(), ListPersonActivity.class);
		startActivityForResult(intent, RESULT_CODE_GOOGLE);
	}

	private void updatePlayerData() {
		Player player = business.getPlayer();

		player.setFirstName(etFirstname.getText().toString());
		player.setLastName(etLastname.getText().toString());
		player.setBirthday(etBirthday.getText().toString());
		player.setPhonenumber(etPhonenumber.getText().toString());
	}

	protected void initialize() {
		Intent intent = getIntent();
		if (savedInstanceState!=null) {
			business.initialize(savedInstanceState);
		}
		else {
			business.initialize(intent);
		}

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
				break;
		}
	}

	protected void initializeListener() {
		etFirstname.addTextChangedListener(new TextWatcherFieldEnableView(tvFirstname, View.GONE));
		etLastname.addTextChangedListener(new TextWatcherFieldEnableView(tvLastname, View.GONE));
		etBirthday.addTextChangedListener(new TextWatcherFieldEnableView(tvBirthday, View.GONE));
		etPhonenumber.addTextChangedListener(new TextWatcherFieldEnableView(tvPhonenumber, View.GONE));
	}
	
	protected void initializeLocation() {
		Log.d(TAG, "initializeDataLocation");
		if (locationFromResult != null) {
			business.setLocation(locationFromResult);
			locationFromResult = null;
		}
		
		String[] location = business.getLocationLine();
		if (getType() == TypeManager.TYPE.COMPETITION) {
			tvLocation.setText(getString(R.string.txt_tournament));
			tvLocationEmpty.setText(getString(R.string.txt_tournament));
		} else {
			tvLocation.setText(getString(R.string.txt_club));
			tvLocationEmpty.setText(getString(R.string.txt_club));
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

	private void initializeListenerListType() {
		spType.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (view != null) {
					Player player = business.getPlayer();
					player.setType((TypeManager.TYPE) view.getTag());
					player.setIdClub(null);
					player.setIdTournament(null);
					initializeLocation();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	protected void initializeView() {
		Player player = business.getPlayer();
		boolean bEditable = true;
		int iVisibility = View.VISIBLE;
		String firstname = "", lastname = "", birthday = "", phonenumber  = "";

		if (player!=null) {
			bEditable = !business.isUnknownPlayer(player);
			iVisibility = (business.isUnknownPlayer(player) ? View.GONE : View.VISIBLE);

			firstname = player.getFirstName();
			lastname = player.getLastName();
			birthday = player.getBirthday();
			phonenumber = player.getPhonenumber();

			if (ApplicationConfig.SHOW_ID) {
				TextView textView = (TextView)findViewById(R.id.tv_firstname);
				String text = textView.getText().toString();
				text += " [" + player.getId() + "]";
				textView.setText(text);
			}
		}
		etFirstname.setEnabled(bEditable);
		llLastname.setVisibility(iVisibility);
		llBirthday.setVisibility(iVisibility);
		llPhonenumber.setVisibility(iVisibility);
		llRanking.setVisibility(iVisibility);
		llRankingEstimate.setVisibility(iVisibility);
		llType.setVisibility(iVisibility);

		etFirstname.setText(firstname);
		etLastname.setText(lastname);
		etBirthday.setText(birthday);
		etPhonenumber.setText(phonenumber);
		llMessage.setVisibility(View.GONE);
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
	}

	private void initializeRankingList() {
		rankingListManager.manageRanking(this, business.getPlayer(), false);
	}
	
	private void initializeRankingEstimateList() {
		rankingListManager.manageRanking(this, business.getPlayer(), true);
	}

	protected void initializeSaisonList() {
		Log.d(TAG, "initializeSaisonList");
		CustomArrayAdapter<String> dataAdapter = new CustomArrayAdapter<String>(this, business.getListTxtSaisons());
		spSaison.setAdapter(dataAdapter);

		spSaison.setOnItemSelectedListener(dataAdapter.new OnItemSelectedListener<Saison>() {
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
				business.setSaison(business.getListSaison().get(position));
			}
		});
	}

	protected void initializeSaison() {
		Log.d(TAG, "initializeSaison");
		Saison saison = business.getSaison();
		int position = 0;
		if (saison != null) {
			List<Saison> listSaison = business.getListSaison();
			for(Saison item : listSaison) {
				if (item.equals(saison)) {
					break;
				} else {
					position++;
				}
			}
		}
		spSaison.setSelection(position, true);
	}

	private void initializeType() {
		spType.setSelection(getTypePosition(), false);
	}
		
	private int getTypePosition() {
		switch(getType()) {
			case TRAINING:
				return 0;
			case COMPETITION:
			default:
				return 1;
		}
	}

	private TypeManager.TYPE getType() {
//		return business.getPlayer() != null ? business.getPlayer().getType() : null;
		return business.getPlayerType();
	}
	
	private TypeManager.TYPE getType(Integer position) {
		switch(position) {
		case 0:
			return TypeManager.TYPE.TRAINING;
		case 1:
		default:
			return TypeManager.TYPE.COMPETITION;
		}
	}

	private final class NavigationDrawerRechercheNotifer implements NavigationDrawerNotifer {

		private final com.justtennis.db.service.RechercheService.TYPE[] typeRecherche = new com.justtennis.db.service.RechercheService.TYPE[] {
				com.justtennis.db.service.RechercheService.TYPE.CLUB,
				com.justtennis.db.service.RechercheService.TYPE.TOURNAMENT,
				com.justtennis.db.service.RechercheService.TYPE.ADDRESS
		};
		private Context context;
		private NotifierMessageLogger notifier;
		private RechercheService rechercheService;
		private EditText edtRecherche;
		private ListView listRecherche;
		private NavigationDrawerRechercheAdapter adpRecherche;
		private List<RechercheResult> list = new ArrayList<RechercheResult>();
		private Handler handler = new Handler();

		public NavigationDrawerRechercheNotifer(Context context, NotifierMessageLogger notifier) {
			this.context = context;
			this.notifier = notifier;
		}

		@Override
		public void onCreateView(View view) {
			rechercheService = new RechercheService(context, notifier);
			edtRecherche = (EditText) view.findViewById(R.id.edt_recherche);
			listRecherche = (ListView) view.findViewById(R.id.lst_recherche);
			adpRecherche = new NavigationDrawerRechercheAdapter(view.getContext(), list);
			listRecherche.setAdapter(adpRecherche);

			edtRecherche.setText(business.getFindText());
			edtRecherche.addTextChangedListener(
				    new TextWatcher() {
				        @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
				        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

				        private Timer timer=new Timer();
				        private final long DELAY = 1000; // milliseconds

				        @Override
				        public void afterTextChanged(Editable s) {
				        	final String text = s.toString();
				            timer.cancel();
				            timer = new Timer();
				            timer.schedule(
				                new TimerTask() {
				                    @Override
				                    public void run() {
				                    	business.setFindText(text);
				                    	list.clear();
				                    	if (text != null && !text.trim().isEmpty()) {
											list.addAll(rechercheService.find(typeRecherche, text));
				                    	}
				                    	handler.post(new Runnable() {
											
											@Override
											public void run() {
						                    	adpRecherche.notifyDataSetChanged();
											}
										});
				                    }
				                }, 
				                DELAY
				            );
				        }
				    }
				);
			}

		@Override
		public void onUpdateView(View view) {
		}
	}

	private final class NavigationDrawerRechercheAdapter extends BaseAdapter {

		private List<RechercheResult> list;
		private Context context;

		public NavigationDrawerRechercheAdapter(Context context, List<RechercheResult> list) {
			this.context = context;
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(R.layout.fragment_navigation_player_drawer_element_recherche_view, null);
			}
			ImageView ivRechercheType = (ImageView) convertView.findViewById(R.id.imv_recherche_type);
			TextView tvRechercheText = (TextView) convertView.findViewById(R.id.edt_recherche_text);

			RechercheResult item = (RechercheResult) getItem(position);
			switch (item.getType()) {
				case TOURNAMENT:
					ivRechercheType.setImageResource(R.drawable.ic_location_tournament);
					break;
	
				case CLUB:
					ivRechercheType.setImageResource(R.drawable.ic_location_club);
					break;

				default:
				case ADDRESS:
					ivRechercheType.setImageResource(R.drawable.ic_location_address_1);
					break;
			}
			tvRechercheText.setText(item.getData());
			return convertView;
		}
		
	}
}