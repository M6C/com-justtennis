package com.justtennis.ui.fragment;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cameleon.common.android.factory.FactoryDialog;
import com.cameleon.common.tool.StringTool;
import com.justtennis.ApplicationConfig;
import com.justtennis.R;
import com.justtennis.activity.ListPlayerActivity;
import com.justtennis.activity.LocationActivity;
import com.justtennis.activity.PlayerActivity;
import com.justtennis.activity.ScoreActivity;
import com.justtennis.adapter.CustomArrayAdapter;
import com.justtennis.adapter.manager.BonusListManager;
import com.justtennis.business.InviteBusiness;
import com.justtennis.db.service.PlayerService;
import com.justtennis.domain.Club;
import com.justtennis.domain.Player;
import com.justtennis.domain.Ranking;
import com.justtennis.domain.Saison;
import com.justtennis.domain.ScoreSet;
import com.justtennis.listener.action.TextWatcherFieldEnableView;
import com.justtennis.manager.ContactManager;
import com.justtennis.manager.TypeManager;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.tool.FragmentTool;
import com.justtennis.tool.ToolPermission;
import com.justtennis.ui.common.CommonEnum;
import com.justtennis.ui.rxjava.RxFragment;
import com.justtennis.ui.viewmodel.ClubViewModel;
import com.justtennis.ui.viewmodel.PlayerViewModel;
import com.justtennis.ui.viewmodel.ScoreViewModel;

import org.gdocument.gtracergps.launcher.log.Logger;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class InviteFragment extends Fragment {

	public static final String TAG = InviteFragment.class.getSimpleName();
	@SuppressWarnings("unused")
	private static final String KEY_LOCATION_FROM_RESULT = "KEY_LOCATION_FROM_RESULT";

	public static final String EXTRA_MODE = "INVITE_MODE";
	public static final String EXTRA_INVITE = "INVITE";
	public static final String EXTRA_USER = "USER";
	public static final String EXTRA_PLAYER_ID = "PLAYER_ID";
	private static final int RESULT_PLAYER = 1;
	private static final int RESULT_LOCATION = 2;
	private static final int RESULT_LOCATION_DETAIL = 3;
	private static final int RESULT_SCORE = 4;

	private InviteBusiness business;
	private NotifierMessageLogger notifier;
	private Long idPlayerFromResult = null;
	private Serializable locationFromResult;
	private Serializable locationClubFromResult;
	private PlayerViewModel modelPlayer;

	private Context context;
	private FragmentActivity activity;
	private View rootView;

	private TextView tvFirstname;
	private TextView tvLastname;
	private TextView edDate;
	private TextView edTime;
	private LinearLayout llPhoto;
	private ImageView ivPhoto;
	private Switch swType;
	private Spinner spRanking;
	private Spinner spSaison;
//	private TextView tvLocation;
	private TextView tvLocationEmpty;
	private Button btnDetail;

	private LinearLayout llLocation;
	private LinearLayout llLocationDetail;
	private TextView tvLocationName;
	private TextView tvLocationLine1;
	private TextView tvLocationLine2;
	private ImageView ivLocationMap;
	private LinearLayout llDetail;
	private LinearLayout llBonusPoint;

	// SCORE
	private LinearLayout llScore;
	private TextView tvScore;
	private EditText etScore;
	private BonusListManager bonusListManager;
	private LinearLayout llPlayer;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_invite, container, false);

		activity = getActivity();
		context = activity.getApplicationContext();

		notifier = NotifierMessageLogger.getInstance();
		business = new InviteBusiness(context, notifier);

		if (savedInstanceState != null) {
			business.initializeData(savedInstanceState);
		}
		else if (getArguments() != null) {
			business.initializeData(getArguments());
		}
		else {
			business.initializeData(activity.getIntent());
		}

		initializeContentPlayer();

		initializeContentPlayerView();

		btnDetail = rootView.findViewById(R.id.btn_detail);
		edDate = rootView.findViewById(R.id.inviteDate);
		edTime = rootView.findViewById(R.id.inviteTime);
		swType = rootView.findViewById(R.id.sw_type);
//		tvLocation = rootView.findViewById(R.id.tv_location);
		tvLocationEmpty = rootView.findViewById(R.id.et_location);
		llLocation = rootView.findViewById(R.id.ll_location);
		llLocationDetail = rootView.findViewById(R.id.ll_location_detail);
		tvLocationName = rootView.findViewById(R.id.tv_location_name);
		tvLocationLine1 = rootView.findViewById(R.id.tv_location_line1);
		tvLocationLine2 = rootView.findViewById(R.id.tv_location_line2);
		ivLocationMap = rootView.findViewById(R.id.iv_location_map);
		llScore = rootView.findViewById(R.id.ll_score);
		tvScore = rootView.findViewById(R.id.tv_score);
		etScore = rootView.findViewById(R.id.et_score);
		llDetail = rootView.findViewById(R.id.ll_detail);
		llBonusPoint = rootView.findViewById(R.id.ll_bonus_point);

		bonusListManager = BonusListManager.getInstance(context, notifier);

		etScore.addTextChangedListener(new TextWatcherFieldEnableView(tvScore, View.GONE));

		initializeVisibility();
		initializeListener();
		initializeFab();

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();

		if (ToolPermission.checkPermissionCALENDAR(activity, true)) {
			initialize();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case ToolPermission.MY_PERMISSIONS_REQUEST: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					initialize();
				} else {
					logMe("Permission Denied ! Cancel initialization");
//					activity.finish();
					activity.getSupportFragmentManager().popBackStackImmediate();
				}
				break;
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case RESULT_PLAYER:
				if (data!=null) {
					long id = data.getLongExtra(ListPlayerActivity.EXTRA_PLAYER_ID, PlayerService.ID_EMPTY_PLAYER);
					if (id != PlayerService.ID_EMPTY_PLAYER) {
						idPlayerFromResult = id;
					}
				}
				break;
			case RESULT_LOCATION:
				if (data != null) {
					locationFromResult = data.getSerializableExtra(LocationActivity.EXTRA_OUT_LOCATION);
				}
				break;
			case RESULT_LOCATION_DETAIL:
				if (data != null) {
					locationClubFromResult = data.getSerializableExtra(LocationActivity.EXTRA_OUT_LOCATION);
				}
				break;

			case RESULT_SCORE:
				String[][] score;
				if (resultCode == Activity.RESULT_OK) {
					score = deSerializeScore(data);
					business.setScores(score);
				} else {
					score = business.getScores();
				}
				List<ScoreSet> listScoreSet = business.computeScoreSet(score);
				business.getInvite().setListScoreSet(listScoreSet);
				business.getInvite().setScoreResult(business.computeScoreResult(listScoreSet));
				initializeDataScore();
				break;

			default:
				super.onActivityResult(requestCode, resultCode, data);
				break;
		}
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		business.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

	public void onClickOk(View view) {
		business.modify();
//		activity.finish();
		activity.getSupportFragmentManager().popBackStackImmediate();

	}

    public void onClickInviteConfirmeYes(View view) {
        business.confirmYes();
//        activity.finish();
		activity.getSupportFragmentManager().popBackStackImmediate();
    }

    public void onClickInviteConfirmeNo(View view) {
        business.confirmNo();
//        activity.finish();
		activity.getSupportFragmentManager().popBackStackImmediate();
    }

	public void onClickInviteDate(final View view) {
		FactoryDialog.getInstance().buildDatePickerDialog(activity, (dialog, view2, which) -> {
			DatePicker datePicker = (DatePicker)view2;

			Calendar calendar = GregorianCalendar.getInstance(ApplicationConfig.getLocal());
			calendar.setTime(business.getDate());
			calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
			business.setDate(calendar.getTime());

			DateFormat sdf = new SimpleDateFormat(getString(R.string.msg_common_format_date), ApplicationConfig.getLocal());
			((EditText)view).setText(sdf.format(business.getDate()));
		}, -1, business.getDate()).show();
	}

	public void onClickInviteTime(final View view) {

		FactoryDialog.getInstance().buildTimePickerDialog(activity, (dialog, view2, which) -> {
			TimePicker timePicker = (TimePicker)view2;

			Calendar calendar = GregorianCalendar.getInstance(ApplicationConfig.getLocal());
			calendar.setTime(business.getDate());
			calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
			calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
			business.setDate(calendar.getTime());

			DateFormat sdf = new SimpleDateFormat(getString(R.string.msg_common_format_time), ApplicationConfig.getLocal());
			((EditText)view).setText(sdf.format(business.getDate()));
		}, -1, business.getDate()).show();
	}

	public void onClickInviteScore(View view) {
		ScoreFragment fragment = ScoreFragment.build(business.getScores());

		Bundle args = fragment.getArguments();
		if (args == null) {
			args = new Bundle();
			fragment.setArguments(args);
		}

		ScoreViewModel model = new ViewModelProvider.NewInstanceFactory().create(ScoreViewModel.class);
		model.getSelected().observe(this, (score) -> {
			business.setScores(score);
			List<ScoreSet> listScoreSet = business.computeScoreSet(score);
			business.getInvite().setListScoreSet(listScoreSet);
			business.getInvite().setScoreResult(business.computeScoreResult(listScoreSet));
			initializeDataScore();
		});
		args.putSerializable(ScoreFragment.EXTRA_VIEW_MODEL, model);

		FragmentTool.replaceFragment(activity, fragment);
	}

	public void onClickPlayer(View view) {
		saveInstanceState();

		ListPlayerFragment fragment = ListPlayerFragment.build(CommonEnum.LIST_FRAGMENT_MODE.FOR_RESULT_FRAGMENT);

		Bundle args = fragment.getArguments();
		if (args == null) {
			args = new Bundle();
			fragment.setArguments(args);
		}

        modelPlayer = new ViewModelProvider.NewInstanceFactory().create(PlayerViewModel.class);
		modelPlayer.getSelected().observe(this, (Player player) -> {
			business.getInvite().setPlayer(player);
			initializeDataPlayer();
		});

        args.putSerializable(ListPlayerFragment.EXTRA_VIEW_MODEL, modelPlayer);
        args.putSerializable(PlayerActivity.EXTRA_TYPE, business.getType());
        args.putLong(PlayerActivity.EXTRA_RANKING, business.getIdRanking());

		FragmentTool.replaceFragment(activity, fragment);
	}

//	public void onClickLocation(View view) {
//		Intent intent;
//		switch(business.getType()) {
//			case COMPETITION:
//				intent = new Intent(context, LocationTournamentActivity.class);
//				if (business.getInvite().getTournament() != null) {
//					intent.putExtra(GenericSpinnerFormActivity.EXTRA_DATA, business.getInvite().getTournament());
//				}
//				break;
//			case TRAINING:
//			default:
//				intent = new Intent(context, LocationClubActivity.class);
//				if (business.getInvite().getClub() != null) {
//					intent.putExtra(GenericSpinnerFormActivity.EXTRA_DATA, business.getInvite().getClub());
//				}
//				break;
//		}
//		intent.putExtra(LocationActivity.EXTRA_INVITE, business.getInvite());
//		startActivityForResult(intent, RESULT_LOCATION);
//	}

	public void onClickLocationDetail(View view) {
		saveInstanceState();

		ListClubFragment fragment = ListClubFragment.build(CommonEnum.LIST_FRAGMENT_MODE.FOR_RESULT_FRAGMENT);

		Bundle args = fragment.getArguments();
		if (args == null) {
			args = new Bundle();
			fragment.setArguments(args);
		}

		ClubViewModel modelClub = new ViewModelProvider.NewInstanceFactory().create(ClubViewModel.class);
		modelClub.getSelected().observe(this, (Club club) -> {
			business.getInvite().setClub(club);
			initializeDataPlayer();
		});

		args.putSerializable(ListPlayerFragment.EXTRA_VIEW_MODEL, modelClub);

		FragmentTool.replaceFragment(activity, fragment);
	}

	public void onClickLocationMap(View view) {
		String[] locationLineUser = business.getLocationLineUser();
		String[] locationLine = business.getLocationLine();

		if (locationLineUser != null && locationLine != null) {
			StringBuilder addressUser = null;
			StringBuilder address = null;
			String line;

			for(int i=1 ; i<locationLineUser.length ; i++) {
				line = locationLineUser[i];
				if (addressUser == null) {
					addressUser = new StringBuilder(line);
				} else {
					addressUser.append(",").append(line);
				}
			}

			for(int i=1 ; i<locationLine.length ; i++) {
				line = locationLine[i];
				if (address == null) {
					address = new StringBuilder(line);
				} else {
					address.append(",").append(line);
				}
			}

			assert addressUser != null;
			assert address != null;
			String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%s&daddr=%s", addressUser.toString(), address.toString());
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
			intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
			startActivity(intent);
		}
	}

	public void onClickDetail(View view) {
		business.setMode(business.getMode() == CommonEnum.INVITE_MODE.INVITE_DETAIL ? CommonEnum.INVITE_MODE.INVITE_SIMPLE : CommonEnum.INVITE_MODE.INVITE_DETAIL);

		initializeContentPlayer();
		initializeContentPlayerView();
		initializeVisibility();

		initializeDataPlayer();
		initializeRankingList();
		initializeRanking();
		initializeSaisonList();
		initializeSaison();
	}

	private void initializeContentPlayer() {
		CommonEnum.INVITE_MODE mode = business.getMode();

		if (CommonEnum.INVITE_MODE.INVITE_DETAIL == mode) {
			initializeContentViewPlayer(R.layout.element_invite_player_detail);
		} else {
			initializeContentViewPlayer(R.layout.element_invite_player);
		}
	}

	private void initialize() {
		if (idPlayerFromResult != null) {
			business.setPlayer(idPlayerFromResult);
			idPlayerFromResult = null;
		}

		if (locationFromResult != null) {
			business.setLocation(locationFromResult);
			locationFromResult = null;
		}

		if (locationClubFromResult != null) {
			business.setLocationClub(locationClubFromResult);
			locationFromResult = null;
		}
		initializeData();
		//RxFragment.publish(RxFragment.SUBJECT_ON_SHOW, TAG);
	}

	private void initializeContentPlayerView() {
		llPhoto = llPlayer.findViewById(R.id.ll_photo);
		ivPhoto = llPlayer.findViewById(R.id.iv_photo);
		tvFirstname = llPlayer.findViewById(R.id.tv_firstname);
		tvLastname = llPlayer.findViewById(R.id.tv_lastname);
		spRanking = llPlayer.findViewById(R.id.sp_ranking);
		spSaison = llPlayer.findViewById(R.id.sp_saison);
	}

	private void initializeContentViewPlayer(int idLayout) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View inflatedLayout = inflater.inflate(idLayout, null, false);

		llPlayer = rootView.findViewById(R.id.ll_player);
		llPlayer.removeAllViews();
		llPlayer.addView(inflatedLayout);
	}

	private void initializeVisibility() {
		CommonEnum.INVITE_MODE mode = business.getMode();

		switch(mode) {
			case INVITE_CONFIRM:
			case INVITE_SIMPLE:
				llDetail.setVisibility(View.VISIBLE);
				llLocation.setVisibility(View.GONE);
				llScore.setVisibility(View.GONE);
				llBonusPoint.setVisibility(View.GONE);
				rootView.findViewById(R.id.sv_content).setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));
				break;
			case INVITE_DETAIL:
			default:
				llDetail.setVisibility(View.GONE);
				llLocation.setVisibility(View.VISIBLE);
				llScore.setVisibility(View.VISIBLE);
				llBonusPoint.setVisibility(View.VISIBLE);
				rootView.findViewById(R.id.sv_content).setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
		}

		switch(mode) {
			case INVITE_CONFIRM:
				edDate.setEnabled(false);
				edTime.setEnabled(false);
				break;
			case INVITE_SIMPLE:
			case INVITE_DETAIL:
			default:
				edDate.setEnabled(true);
				edTime.setEnabled(true);
				break;
		}
	}

	private void initializeFab() {
		FragmentTool.initializeFabDrawable(activity, FragmentTool.INIT_FAB_IMAGE.VALIDATE);

		CommonEnum.INVITE_MODE mode = business.getMode();
		switch(mode) {
			case INVITE_CONFIRM:
				FragmentTool.onClickFab(activity, this::onClickInviteConfirmeYes);
				break;
			case INVITE_SIMPLE:
			case INVITE_DETAIL:
			default:
				FragmentTool.onClickFab(activity, this::onClickOk);
				break;
		}
	}

	private void initializeData() {
		initializeDataPlayer();
		initializeRankingList();
		initializeRanking();
		initializeSaisonList();
		initializeSaison();
		initializeDataType();
		initializeDataDateTime();
		initializeDataScore();
		initializeDataLocation();
		initializeBonus();
	}

	private void initializeDataPlayer() {
		Log.d(TAG, "initializeDataPlayer");
		Player player = business.getPlayer();
		if (player!=null) {
			tvFirstname.setText(player.getFirstName());
			tvLastname.setText(player.getLastName());
			if (player.getIdGoogle()!=null && player.getIdGoogle() > 0L) {
				ivPhoto.setImageBitmap(ContactManager.getInstance().getPhoto(activity, player.getIdGoogle()));
			} else {
				ivPhoto.setImageResource(business.getUnknownPlayerRandomRes());
			}
			if (StringTool.getInstance().isEmpty(player.getFirstName())) {
				tvFirstname.setVisibility(View.GONE);
			} else {
				tvFirstname.setVisibility(View.VISIBLE);
			}
			if (business.isUnknownPlayer() || StringTool.getInstance().isEmpty(player.getLastName())) {
				tvLastname.setVisibility(View.GONE);
			} else {
				tvLastname.setVisibility(View.VISIBLE);
			}
		}
	}

	private void initializeDataDateTime() {
		Log.d(TAG, "initializeDataTime");
		Date date = business.getDate();

		DateFormat sdfD = new SimpleDateFormat(getString(R.string.msg_common_format_date), ApplicationConfig.getLocal());
		DateFormat sdfT = new SimpleDateFormat(getString(R.string.msg_common_format_time), ApplicationConfig.getLocal());
		edDate.setText(sdfD.format(date));
		edTime.setText(sdfT.format(date));
	}

	private void initializeDataType() {
		Log.d(TAG, "initializeDataType");
		swType.setChecked(getTypePosition()==0);
	}

	private void initializeRankingList() {
		Log.d(TAG, "initializeRankingList");
		if (spRanking != null) {
			spRanking.setVisibility(View.VISIBLE);
			CustomArrayAdapter<String> dataAdapter = new CustomArrayAdapter<>(context, business.getListTxtRankings());
			spRanking.setAdapter(dataAdapter);

			spRanking.setOnItemSelectedListener(dataAdapter.new OnItemSelectedListener<Ranking>() {
				@Override
				public Ranking getItem(int position) {
					return business.getListRanking().get(position);
				}

				@Override
				public boolean isHintItemSelected(Ranking item) {
					return business.isEmptyRanking(item);
				}

				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id, Ranking item) {
					business.setIdRanking(item.getId());
				}
			});
		}
	}

	private void initializeSaisonList() {
		Log.d(TAG, "initializeSaisonList");
		if (spSaison != null) {
			CustomArrayAdapter<String> dataAdapter = new CustomArrayAdapter<>(context, business.getListTxtSaisons());
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
	}

	private void initializeSaison() {
		Log.d(TAG, "initializeSaison");
		if (spSaison != null) {
			Saison saison = business.getSaison();
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
	}

	private void initializeBonus() {
		Log.d(TAG, "initializeBonus");
		bonusListManager.manage(activity, business.getInvite());
	}

	private void initializeRanking() {
		Log.d(TAG, "initializeRanking");
		if (spRanking != null) {
			Long id = business.getIdRanking();
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

	private void initializeDataScore() {
		Log.d(TAG, "initializeDataScore");
		String textScore = business.getScoresText();
		etScore.setText(textScore == null ? "" : Html.fromHtml(textScore));
	}

	private void initializeDataLocation() {
		Log.d(TAG, "initializeDataLocation");
		String[] location = business.getLocationLine();
		if (business.getType() == TypeManager.TYPE.TRAINING) {
//			tvLocation.setText(getString(R.string.txt_club));
			tvLocationEmpty.setText(getString(R.string.txt_club));
		} else {
//			tvLocation.setText(getString(R.string.txt_tournament));
			tvLocationEmpty.setText(getString(R.string.txt_tournament));
		}
		tvLocationEmpty.setTextColor(tvLocationEmpty.getCurrentHintTextColor());

		if (location != null) {
			tvLocationName.setText(location[0]);
			tvLocationLine1.setText(location[1]);
			tvLocationLine2.setText(location[2]);

			tvLocationName.setVisibility(StringTool.getInstance().isEmpty(location[0]) ? View.GONE : View.VISIBLE);
			tvLocationLine1.setVisibility(StringTool.getInstance().isEmpty(location[1]) ? View.GONE : View.VISIBLE);
			tvLocationLine2.setVisibility(StringTool.getInstance().isEmpty(location[2]) ? View.GONE : View.VISIBLE);

//			tvLocation.setVisibility(View.VISIBLE);
			llLocationDetail.setVisibility(View.VISIBLE);
			tvLocationEmpty.setText("");
			tvLocationEmpty.setTextSize(2);
		} else {
//			tvLocation.setVisibility(View.GONE);
			llLocationDetail.setVisibility(View.GONE);
			tvLocationEmpty.setVisibility(View.VISIBLE);
			tvLocationEmpty.setTextSize(22);
		}
	}

	private void initializeListener() {
		edDate.setOnClickListener(this::onClickInviteDate);
		edTime.setOnClickListener(this::onClickInviteTime);
		etScore.setOnFocusChangeListener((v, hasFocus) -> {
			if (hasFocus) {
				onClickInviteScore(v);
			}
		});
		tvLocationEmpty.setOnFocusChangeListener((v, hasFocus) -> {
			if (hasFocus) {
				onClickLocationDetail(v);
			}
		});
		swType.setOnCheckedChangeListener((buttonView, isChecked) -> business.setType(isChecked ? TypeManager.TYPE.TRAINING : TypeManager.TYPE.COMPETITION));

		btnDetail.setOnClickListener(this::onClickDetail);
//		llLocation.setOnClickListener(this::onClickLocation);
        llLocationDetail.setOnClickListener(this::onClickLocationDetail);
        ivLocationMap.setOnClickListener(this::onClickLocationMap);
        tvLocationEmpty.setOnClickListener(this::onClickLocationDetail);
        etScore.setOnClickListener(this::onClickInviteScore);
        llPhoto.setOnClickListener(this::onClickPlayer);
	}

	private int getTypePosition() {
		switch(business.getInvite().getType()) {
			case COMPETITION:
				return 1;
			case TRAINING:
			default:
				return 0;
		}
	}

	private String[][] deSerializeScore(Intent data) {
		String[][] score = null;
		if (data != null) {
			Object[] d = (Object[]) data.getSerializableExtra(ScoreActivity.EXTRA_SCORE);
			if (d != null && d.length > 0) {
				score = new String[d.length][];
				for(int i=0 ; i<d.length ; i++) {
					score[i] = (String[]) d[i];
				}
			}
		}
		return score;
	}

	private void saveInstanceState() {
		Bundle bundle = getArguments();
		if (bundle != null) {
			bundle.clear();
			business.onSaveInstanceState(bundle);
		}
	}

	private static void logMe(String msg) {
		Logger.logMe(TAG, msg);
	}
}