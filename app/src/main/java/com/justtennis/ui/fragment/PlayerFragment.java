
package com.justtennis.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cameleon.common.android.adapter.BaseViewAdapter;
import com.cameleon.common.android.factory.FactoryDialog;
import com.justtennis.ApplicationConfig;
import com.justtennis.R;
import com.justtennis.activity.GenericSpinnerFormActivity;
import com.justtennis.activity.ListPersonActivity;
import com.justtennis.activity.PlayerActivity;
import com.justtennis.activity.QRCodeActivity;
import com.justtennis.adapter.CustomArrayAdapter;
import com.justtennis.adapter.manager.RankingListManager;
import com.justtennis.business.PlayerBusiness;
import com.justtennis.domain.Club;
import com.justtennis.domain.Player;
import com.justtennis.domain.Saison;
import com.justtennis.drawer.manager.notifier.IDrawerLayoutSaisonNotifier;
import com.justtennis.drawer.manager.notifier.IDrawerLayoutTypeNotifier;
import com.justtennis.listener.ok.OnClickPlayerCreateListenerOk;
import com.justtennis.manager.TypeManager.TYPE;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.parser.PlayerParser;
import com.justtennis.tool.FragmentTool;
import com.justtennis.ui.common.CommonEnum;
import com.justtennis.ui.viewmodel.ClubViewModel;

import org.gdocument.gtracergps.launcher.log.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class PlayerFragment extends Fragment implements IDrawerLayoutTypeNotifier, IDrawerLayoutSaisonNotifier {

	public static final String TAG = PlayerFragment.class.getSimpleName();
	private static final int RESULT_CODE_QRCODE_SCAN = 0;
	private static final int RESULT_CODE_GOOGLE = 1;
	private static final int RESULT_LOCATION = 2;
	public static final String EXTRA_PLAYER = "PLAYER";
	public static final String EXTRA_PLAYER_ID = "PLAYER_ID";
	public static final String EXTRA_INVITE = "INVITE";
	public static final String EXTRA_MODE = "INVITE_MODE";
	public static final String EXTRA_TYPE = "TYPE";
	public static final String EXTRA_RANKING = "RANKING";
	public static final String EXTRA_FIND = "EXTRA_FIND";

	protected View rootView;
	protected FragmentActivity activity;
	protected Context context;

	private final Integer[] drawableType = new Integer[] {R.layout.element_invite_type_entrainement, R.layout.element_invite_type_match};

	private Bundle savedInstanceState;
	private PlayerBusiness business;
	private RankingListManager rankingListManager;

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
	private LinearLayout llCreate;
	private LinearLayout llModify;
	private LinearLayout llAddDemande;

	private TextView tvLocationEmpty;
	private LinearLayout llLocationDetail;
	private TextView tvLocationName;
	private TextView tvLocationLine1;
	private TextView tvLocationLine2;

	private boolean fromQrCode = false;
//	private Serializable locationFromResult;
	private Button btnCreate;
	private Button btnImport;
	private Button btnModify;
	private Button btnAddDemandeYes;
	private Button btnAddDemandeNo;
    private Button btnQrCode;

	public static PlayerFragment build(Bundle args) {
		PlayerFragment fragment = new PlayerFragment();
		fragment.setArguments(args);
		return fragment;
	}

	public static PlayerFragment build(long idPlayer) {
		PlayerFragment fragment = new PlayerFragment();
		Bundle args = new Bundle();
		args.putLong(PlayerActivity.EXTRA_PLAYER_ID, idPlayer);
		fragment.setArguments(args);
		return fragment;
	}

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_player, container, false);
		if (this.savedInstanceState==null) {
			this.savedInstanceState = savedInstanceState;
		}

		activity = getActivity();
		context = activity.getApplicationContext();

		NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();

//		initializeLayoutView();
		initializeViewById();

		business = createBusiness();
		rankingListManager = RankingListManager.getInstance(context, notifier);

		initializeListener();
		initialize();

		initializeListType();

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();

        initializeFab();
		initializeData(true);
		//RxFragment.publish(RxFragment.SUBJECT_ON_SHOW, TAG);
	}

	@Override
	public void onDrawerLayoutSaisonChange(AdapterView<?> parent, View view, int position, long id, Saison item) {
		business.initialize(new Bundle());

		initializeData(false);
	}

	@Override
	public void onDrawerLayoutTypeChange(TYPE type) {
		//TODO Useless - Fix refresh button color
		int style = (type == TYPE.COMPETITION ? R.style.StyleButton_Competition : R.style.StyleButton);
		((Button)rootView.findViewById(R.id.btn_import)).setTypeface(Typeface.DEFAULT, style);
		((Button)rootView.findViewById(R.id.btn_qrcode)).setTypeface(Typeface.DEFAULT, style);
		((Button)rootView.findViewById(R.id.btn_add_demande_yes)).setTypeface(Typeface.DEFAULT, style);
		((Button)rootView.findViewById(R.id.btn_add_demande_no)).setTypeface(Typeface.DEFAULT, style);

		business.initialize(new Bundle());

		initializeData(false);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode==RESULT_CODE_QRCODE_SCAN) {
			if (resultCode == Activity.RESULT_OK && data != null) {
				String qrcodeData = data.getStringExtra("SCAN_RESULT");
//				String format = data.getStringExtra("SCAN_RESULT_FORMAT");
				// Handle successful scan
				Logger.logMe(TAG, qrcodeData);

				Player player = PlayerParser.getInstance().fromData(qrcodeData);
				business.initializePlayerSaison(player);
				business.setPlayer(player);

				fromQrCode = true;
			} else if (resultCode == Activity.RESULT_CANCELED) {
				// Handle cancel
			}
		} else if (requestCode==RESULT_CODE_GOOGLE) {
			if (resultCode == Activity.RESULT_OK && data != null) {
				Player player = (Player) data.getSerializableExtra(ListPersonActivity.EXTRA_PLAYER);
				business.initializePlayerSaison(player);
				business.setPlayer(player);

				fromQrCode = false;
			} else if (resultCode == Activity.RESULT_CANCELED) {
				// Handle cancel
			}
//		} else if (requestCode==RESULT_LOCATION) {
//			if (resultCode == Activity.RESULT_OK && data != null) {
//				locationFromResult = data.getSerializableExtra(LocationActivity.EXTRA_OUT_LOCATION);
//			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
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
		etFirstname = rootView.findViewById(R.id.et_firstname);
		etLastname = rootView.findViewById(R.id.et_lastname);
		etBirthday = rootView.findViewById(R.id.et_birthday);
		etPhonenumber = rootView.findViewById(R.id.et_phonenumber);
		spType = rootView.findViewById(R.id.sp_type);
		spSaison = rootView.findViewById(R.id.sp_saison);
		llLastname = rootView.findViewById(R.id.ll_lastname);
		llBirthday = rootView.findViewById(R.id.ll_birthday);
		llPhonenumber = rootView.findViewById(R.id.ll_phonenumber);
		llRanking = rootView.findViewById(R.id.ll_ranking);
		llRankingEstimate = rootView.findViewById(R.id.ll_ranking_estimate);
		llCreate = rootView.findViewById(R.id.ll_create);
		llModify = rootView.findViewById(R.id.ll_modify);
		llAddDemande = rootView.findViewById(R.id.ll_add_demande);
		tvLocationEmpty = rootView.findViewById(R.id.et_location);
		llLocationDetail = rootView.findViewById(R.id.ll_location_detail);
		tvLocationName = rootView.findViewById(R.id.tv_location_name);
		tvLocationLine1 = rootView.findViewById(R.id.tv_location_line1);
		tvLocationLine2 = rootView.findViewById(R.id.tv_location_line2);
		btnCreate = rootView.findViewById(R.id.btn_create);
		btnImport = rootView.findViewById(R.id.btn_import);
		btnModify = rootView.findViewById(R.id.btn_modify);
		btnAddDemandeYes = rootView.findViewById(R.id.btn_add_demande_yes);
		btnAddDemandeNo = rootView.findViewById(R.id.btn_add_demande_no);
		btnQrCode = rootView.findViewById(R.id.btn_qrcode);
	}

	public void onClickCreate(View view) {
		updatePlayerData();

		CommonEnum.PLAYER_MODE mode = business.getMode();
		switch (mode) {
			case FOR_RESULT:
				business.create(false);
				Intent intent = new Intent();
				intent.putExtra(EXTRA_PLAYER_ID, business.getPlayer().getId());
//				setResult(0, intent);
				finish();
				break;
			default:
				if (fromQrCode) {
					business.create(true);
					finish();
				}
				else {
					if (business.sendMessageConfirmation()) {
						OnClickPlayerCreateListenerOk listener = new OnClickPlayerCreateListenerOk(activity, business);
						FactoryDialog.getInstance()
							.buildYesNoDialog(activity, listener, R.string.dialog_player_create_confirmation_title, R.string.dialog_player_create_confirmation_message)
							.show();
					} else {
						business.create(false);
						finish();
					}
				}
			}
	}

	private void finish() {
		FragmentTool.finish(activity);
	}

	public void onClickModify(View view) {
		updatePlayerData();

		business.modify();

		finish();
	}

	public void onClickQRCode(View view) {
		updatePlayerData();

		String qrcodeData = business.toQRCode();

		Intent intent = new Intent(context, QRCodeActivity.class);
		intent.putExtra(QRCodeActivity.EXTRA_QRCODE_DATA, qrcodeData);
		startActivity(intent);
	}

	public void onClickImport(View view) {
		String[] listPhonenumber = new String[] {
				getString(R.string.button_text_scan),
				getString(R.string.txt_google)
		};
		Dialog dialog = FactoryDialog.getInstance().buildListView(activity, R.string.txt_import, listPhonenumber, new OnItemClickListener() {
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
//		Intent intent = null;
//		switch(getType()) {
//			case TRAINING:
//				intent = new Intent(context, LocationClubActivity.class);
//				if (business.getPlayer().getIdClub() != null) {
//					intent.putExtra(GenericSpinnerFormActivity.EXTRA_DATA, new Club(business.getPlayer().getIdClub()));
//				}
//				break;
//			case COMPETITION:
//				intent = new Intent(context, LocationTournamentActivity.class);
//				if (business.getPlayer().getIdTournament() != null) {
//					intent.putExtra(GenericSpinnerFormActivity.EXTRA_DATA, new Tournament(business.getPlayer().getIdTournament()));
//				}
//				break;
//		}
//		if (intent != null) {
//			startActivityForResult(intent, RESULT_LOCATION);
//		}

		ListClubFragment fragment = ListClubFragment.build(CommonEnum.LIST_FRAGMENT_MODE.FOR_RESULT_FRAGMENT);
		Bundle args = fragment.getArguments();
		if (args == null) {
			args = new Bundle();
			fragment.setArguments(args);
		}

		ClubViewModel  modelClub = new ViewModelProvider.NewInstanceFactory().create(ClubViewModel.class);
		modelClub.getSelected().observe(this, (Club club) -> {
			business.setLocation(club);
			initializeLocation();
		});

		args.putSerializable(GenericSpinnerFormActivity.EXTRA_DATA, new Club(business.getPlayer().getIdClub()));
		args.putSerializable(ListPlayerFragment.EXTRA_VIEW_MODEL, modelClub);

		FragmentTool.replaceFragment(activity, fragment);
	}

	public void onClickLocationDetail(View view) {
		onClickLocation(view);
	}

	protected PlayerBusiness createBusiness() {
		return new PlayerBusiness(context, NotifierMessageLogger.getInstance());
	}

//	protected void initializeLayoutView() {
//		drawerManager.setContentView(R.layout.player);
//	}

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
		Intent intent = new Intent(context, ListPersonActivity.class);
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
		Intent intent = activity.getIntent();
		if (savedInstanceState!=null) {
			business.initializeSavedState(savedInstanceState);
		}
		else {
			Bundle bundle = (getArguments() != null) ? getArguments() : intent.getExtras();
			business.initialize(bundle);
		}

		CommonEnum.PLAYER_MODE mode = business.getMode();

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

	private void initializeFab() {
		CommonEnum.PLAYER_MODE mode = business.getMode();

		FragmentTool.initializeFabDrawable(activity, FragmentTool.INIT_FAB_IMAGE.VALIDATE);
		switch (mode) {
			case FOR_RESULT:
			case CREATE:
				FragmentTool.onClickFab(activity, this::onClickCreate);
				break;
			case MODIFY:
				FragmentTool.onClickFab(activity, this::onClickModify);
				break;
			case DEMANDE_ADD:
				FragmentTool.onClickFab(activity, this::onClickDemandeAddYes);
				break;
		}
	}

	protected void initializeListener() {
		btnCreate.setOnClickListener(this::onClickCreate);
		btnImport.setOnClickListener(this::onClickImport);
		btnModify.setOnClickListener(this::onClickModify);
		btnAddDemandeYes.setOnClickListener(this::onClickDemandeAddYes);
		btnAddDemandeNo.setOnClickListener(this::onClickDemandeAddNo);
		btnQrCode.setOnClickListener(this::onClickQRCode);
		llLocationDetail.setOnClickListener(this::onClickLocationDetail);
		tvLocationEmpty.setOnClickListener(this::onClickLocationDetail);
		etBirthday.setOnClickListener(this::onClickBirthday);
	}

	private void onClickBirthday(View view) {
		FactoryDialog.getInstance().buildDatePickerDialog(activity, (dialog, view2, which) -> {
			DatePicker datePicker = (DatePicker)view2;

			Calendar calendar = GregorianCalendar.getInstance(ApplicationConfig.getLocal());
			calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());

			DateFormat sdf = new SimpleDateFormat(getString(R.string.msg_common_format_date), ApplicationConfig.getLocal());
			etBirthday.setText(sdf.format(calendar.getTime()));
		}, -1, new Date()).show();
	}

	protected void initializeLocation() {
		Log.d(TAG, "initializeDataLocation");
//		if (locationFromResult != null) {
//			business.setLocation(locationFromResult);
//			locationFromResult = null;
//		}

		String[] location = business.getLocationLine();
		if (getType() == TYPE.COMPETITION) {
			tvLocationEmpty.setText(getString(R.string.txt_tournament));
		} else {
			tvLocationEmpty.setText(getString(R.string.txt_club));
		}

		if (location != null) {
			tvLocationName.setText(location[0]);
			tvLocationLine1.setText(location[1]);
			tvLocationLine2.setText(location[2]);
			llLocationDetail.setVisibility(View.VISIBLE);
			tvLocationEmpty.setVisibility(View.GONE);
		} else {
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
					player.setType((TYPE) view.getTag());
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
				TextView textView = (TextView)rootView.findViewById(R.id.tv_firstname);
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

		etFirstname.setText(firstname);
		etLastname.setText(lastname);
		etBirthday.setText(birthday);
		etPhonenumber.setText(phonenumber);
	}

	private void initializeListType() {
		adapterType = new BaseViewAdapter(activity, drawableType);
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
		rankingListManager.manageRanking(activity, business.getPlayer(), false);
	}

	private void initializeRankingEstimateList() {
		rankingListManager.manageRanking(activity, business.getPlayer(), true);
	}

	protected void initializeSaisonList() {
		Log.d(TAG, "initializeSaisonList");
		CustomArrayAdapter<String> dataAdapter = new CustomArrayAdapter<String>(context, business.getListTxtSaisons());
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
		if (position < spSaison.getCount()) {
			spSaison.setSelection(position, true);
		}
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

	public PlayerBusiness getBusiness() {
    	return business;
	}

	private TYPE getType() {
//		return business.getPlayer() != null ? business.getPlayer().getType() : null;
		return business.getPlayerType();
	}

	private TYPE getType(Integer position) {
		switch(position) {
		case 0:
			return TYPE.TRAINING;
		case 1:
		default:
			return TYPE.COMPETITION;
		}
	}
}