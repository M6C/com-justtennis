package com.justtennis.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.justtennis.R;
import com.justtennis.adapter.CustomArrayAdapter;
import com.justtennis.business.InviteLocationBusiness;
import com.justtennis.domain.Address;
import com.justtennis.domain.Club;
import com.justtennis.domain.Invite.INVITE_TYPE;
import com.justtennis.domain.Tournament;
import com.justtennis.notifier.NotifierMessageLogger;

public class InviteLocationActivity extends Activity {

	@SuppressWarnings("unused")
	private static final String TAG = InviteLocationActivity.class.getSimpleName();

	public static final String EXTRA_INVITE = "EXTRA_INVITE";

	private InviteLocationBusiness business;

	private int visibilityTournamentSelection = View.GONE;
	private int visibilityClubSelection = View.GONE;
	private int visibilityAddressSelection = View.GONE;
	private int visibilityAddressCollapser = View.GONE;
	private int visibilityAddressContent = View.GONE;
	private int visibilityClubCollapser = View.GONE;
	private int visibilityClubContent = View.VISIBLE;
	private int visibilityTournamentCollapser = View.GONE;
	private int visibilityTournamentContent = View.VISIBLE;

	private LinearLayout llAddressCollapser;
	private LinearLayout llAddressContent;
	private LinearLayout llClubCollapser;
	private LinearLayout llClubContent;
	private LinearLayout llTournamentCollapser;
	private LinearLayout llTournamentContent;

	// ADDRESS
	private Spinner spAddress;
	private Spinner spClub;
	private Spinner spClubAddress;
	private Spinner spTournament;
	private Spinner spTournamentClub;
	private EditText etAddressName;
	private EditText etAddressLine1;
	private EditText etAddressPostalCode;
	private EditText etAddressCity;
	private View llTournamentAdd;
	private View llTournamentSelection;
	private View llClubAdd;
	private View llClubSelection;
	private View llAddressAdd;
	private View llAddressSelection;

	private CustomArrayAdapter<String> adapterClubAddress;
	private CustomArrayAdapter<String> adapterAddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.invite_location);

		llAddressContent = (LinearLayout)findViewById(R.id.ll_address_content);
		llAddressCollapser = (LinearLayout)findViewById(R.id.ll_address_collapser);
		llAddressAdd = findViewById(R.id.ll_address_add);
		llAddressSelection = findViewById(R.id.ll_address_selection);
		spAddress = (Spinner)findViewById(R.id.sp_address_list);
		etAddressName = (EditText)findViewById(R.id.et_address_name);
		etAddressLine1 = (EditText)findViewById(R.id.et_address_line_1);
		etAddressPostalCode = (EditText)findViewById(R.id.et_address_postal_code);
		etAddressCity = (EditText)findViewById(R.id.et_address_city);

		llClubContent = (LinearLayout)findViewById(R.id.ll_club_content);
		llClubCollapser = (LinearLayout)findViewById(R.id.ll_club_collapser);
		llClubAdd = findViewById(R.id.ll_club_add);
		llClubSelection = findViewById(R.id.ll_club_selection);
		spClub = (Spinner)findViewById(R.id.sp_club_list);
		spClubAddress = (Spinner)findViewById(R.id.sp_club_address_list);

		llTournamentContent = (LinearLayout)findViewById(R.id.ll_tournament_content);
		llTournamentCollapser = (LinearLayout)findViewById(R.id.ll_tournament_collapser);
		llTournamentAdd = findViewById(R.id.ll_tournament_add);
		llTournamentSelection = findViewById(R.id.ll_tournament_selection);
		spTournament = (Spinner)findViewById(R.id.sp_tournament_list);
		spTournamentClub = (Spinner)findViewById(R.id.sp_tournament_club_list);

		business = new InviteLocationBusiness(this, NotifierMessageLogger.getInstance());
	}

	@Override
	protected void onResume() {
		super.onResume();
		initializeData();

		visibilityTournamentCollapser = (business.getType() == INVITE_TYPE.MATCH) ? View.VISIBLE : View.GONE;
		visibilityClubCollapser = (business.getType() == INVITE_TYPE.MATCH) ? View.GONE : View.VISIBLE;

		llAddressCollapser.setVisibility(visibilityAddressCollapser);
		llAddressContent.setVisibility(visibilityAddressContent);
		llClubCollapser.setVisibility(visibilityClubCollapser);
		llClubContent.setVisibility(visibilityClubContent);
		llTournamentCollapser.setVisibility(visibilityTournamentCollapser);
		llTournamentContent.setVisibility(visibilityTournamentContent);
	}

	@Override
	public void onBackPressed() {
		onClickCancel(null);
		super.onBackPressed();
	}

	public void onClickModify(View view) {
		saveAddress();
		saveClub();
		saveTournament();

		business.save();
		
		finish();
	}
	
	public void onClickCancel(View view) {
		finish();
	}

	public void onClickAddressCollapser(View view) {
		visibilityAddressContent = (visibilityAddressContent == View.GONE) ? View.VISIBLE : View.GONE;
		llAddressContent.setVisibility(visibilityAddressContent);
		llAddressAdd.setVisibility(View.GONE);
		llAddressSelection.setVisibility(View.VISIBLE);
	}
	
	public void onClickClubCollapser(View view) {
		visibilityClubContent = (visibilityClubContent == View.GONE) ? View.VISIBLE : View.GONE;
		llClubContent.setVisibility(visibilityClubContent);
		llClubAdd.setVisibility(View.GONE);
		llClubSelection.setVisibility(View.VISIBLE);
	}
	
	public void onClickTournamentCollapser(View view) {
		visibilityTournamentContent = (visibilityTournamentContent == View.GONE) ? View.VISIBLE : View.GONE;
		llTournamentContent.setVisibility(visibilityTournamentContent);
		llTournamentAdd.setVisibility(View.GONE);
		llTournamentSelection.setVisibility(View.VISIBLE);
	}
	
	public void onClickAddressAdd(View view) {
		llAddressAdd.setVisibility(View.VISIBLE);
		llAddressSelection.setVisibility(View.GONE);
	}

	public void onClickAddressAddValidate(View view) {
		business.addAddress(getText(etAddressName), getText(etAddressLine1), getText(etAddressPostalCode), getText(etAddressCity));
		business.initializeDataAddress();
		adapterClubAddress.notifyDataSetChanged();
		adapterAddress.notifyDataSetChanged();
		manageVisibility(false, false, false, true, false, false);
	}
	
	public void onClickClubAdd(View view) {
		llClubAdd.setVisibility(View.VISIBLE);
		llClubSelection.setVisibility(View.GONE);
	}
	
	public void onClickClubAddValidate(View view) {
	}
	
	public void onClickClubAddressAdd(View view) {
		manageVisibility(false, false, false, false, false, true);
	}
	
	public void onClickTournamentAdd(View view) {
		llTournamentAdd.setVisibility(View.VISIBLE);
		llTournamentSelection.setVisibility(View.GONE);
	}
	
	public void onClickTournamentAddValidate(View view) {
	}

	public void onClickTournamentClubAdd(View view) {
		visibilityTournamentCollapser = View.GONE;
		visibilityTournamentContent = View.GONE;
		visibilityClubCollapser = View.VISIBLE;
		visibilityClubContent = View.VISIBLE;
		visibilityAddressCollapser = View.GONE;
		visibilityAddressContent = View.GONE;
		llTournamentCollapser.setVisibility(visibilityTournamentCollapser);
		llTournamentContent.setVisibility(visibilityTournamentContent);
		llTournamentAdd.setVisibility(visibilityTournamentContent);
		llTournamentSelection.setVisibility(View.GONE);
		llClubCollapser.setVisibility(visibilityClubCollapser);
		llClubContent.setVisibility(visibilityClubContent);
		llClubAdd.setVisibility(visibilityClubContent);
		llClubSelection.setVisibility(View.GONE);
		llAddressCollapser.setVisibility(visibilityAddressCollapser);
		llAddressContent.setVisibility(visibilityAddressContent);
		llAddressAdd.setVisibility(visibilityAddressContent);
		llAddressSelection.setVisibility(View.GONE);
	}

	private void initializeData() {
		Intent intent = getIntent();
		business.initializeData(intent);

		initializeAddressList();
		initializeClubList();
		initializeTournamentList();
		initializeAddress();
		initializeClub();
		initializeTournament();
	}

	private void initializeAddressList() {
		adapterAddress = new CustomArrayAdapter<String>(this, business.getListTxtAddress());
		adapterAddress.setDropDownViewResource(R.layout.spinner_dropdown_item);
		spAddress.setAdapter(adapterAddress);
		spAddress.setOnItemSelectedListener(adapterAddress.new OnItemSelectedListener<Address>() {
			@Override
			public Address getItem(int position) {
				return business.getListAddress().get(position);
			}

			@Override
				public boolean isHintItemSelected(Address address) {
				return business.isEmptyAddress(address);
			}
	
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id, Address address) {
				super.onItemSelected(parent, view, position, id);
				business.setAddress(address);
			}
		});

		adapterClubAddress = new CustomArrayAdapter<String>(this, business.getListTxtAddress());
		spClubAddress.setAdapter(adapterClubAddress);
		spClubAddress.setOnItemSelectedListener(adapterAddress.new OnItemSelectedListener<Address>() {
			@Override
			public Address getItem(int position) {
				return business.getListAddress().get(position);
			}

			@Override
				public boolean isHintItemSelected(Address address) {
				return business.isEmptyAddress(address);
			}
	
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id, Address address) {
			}
		});
	}

	private void initializeClubList() {
		CustomArrayAdapter<String> dataAdapter = null;

		dataAdapter = new CustomArrayAdapter<String>(this, business.getListTxtClub());
		spClub.setAdapter(dataAdapter);
		spClub.setOnItemSelectedListener(dataAdapter.new OnItemSelectedListener<Club>() {
			@Override
			public Club getItem(int position) {
				return business.getListClub().get(position);
			}

			@Override
				public boolean isHintItemSelected(Club club) {
				return business.isEmptyClub(club);
			}
	
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id, Club club) {
				business.setClub(club);
			}
		});

		dataAdapter = new CustomArrayAdapter<String>(this, business.getListTxtClub());
		dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		spTournamentClub.setAdapter(dataAdapter);
	}
	
	private void initializeTournamentList() {
		CustomArrayAdapter<String> dataAdapter = null;

		dataAdapter = new CustomArrayAdapter<String>(this, business.getListTxtTournament());
		spTournament.setAdapter(dataAdapter);

		spTournament.setOnItemSelectedListener(dataAdapter.new OnItemSelectedListener<Tournament>() {
			@Override
			public Tournament getItem(int position) {
				return business.getListTournament().get(position);
			}

			@Override
				public boolean isHintItemSelected(Tournament tournament) {
				return business.isEmptyTournament(tournament);
			}
	
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id, Tournament tournament) {
				business.setTournament(tournament);
			}
		});
	}

	private void initializeAddress() {
		Address address = business.getAddress();
		if (address!=null) {
			int position = 0;
			List<Address> list = business.getListAddress();
			for(Address item : list) {
				if (item.getId().equals(address.getId())) {
					spAddress.setSelection(position, true);
					break;
				} else {
					position++;
				}
			}
		}
	}

	private void initializeClub() {
		Club club = business.getClub();
		if (club!=null) {
			int position = 0;
			List<Club> list = business.getListClub();
			for(Club item : list) {
				if (item.getId().equals(club.getId())) {
					spClub.setSelection(position, true);
					break;
				} else {
					position++;
				}
			}
		}
	}
	
	private void initializeTournament() {
		Tournament tournament = business.getTournament();
		if (tournament!=null) {
			int position = 0;
			List<Tournament> list = business.getListTournament();
			for(Tournament item : list) {
				if (item.getId().equals(tournament.getId())) {
					spTournament.setSelection(position, true);
					break;
				} else {
					position++;
				}
			}
		}
	}

	private void saveAddress() {
		if (visibilityAddressContent == View.VISIBLE) {
			Address address = new Address();
			address.setLine1(getText(etAddressLine1));
			address.setPostalCode(getText(etAddressPostalCode));
			address.setCity(getText(etAddressCity));
			business.setAddress(address);
		}
	}

	private void saveClub() {
		if (visibilityClubContent == View.VISIBLE) {
			Club club = new Club();
			business.setClub(club);
		}
	}

	private void saveTournament() {
		if (visibilityTournamentContent == View.VISIBLE) {
			Tournament tournament = new Tournament();
			business.setTournament(tournament);
		}
	}
	
	private String getText(EditText editText) {
		String text = editText.getText().toString();
		if ("".equals(text)) {
			text = null;
		}
		return text;
	}

	private void manageVisibility(
			boolean tournamentSelection, boolean tournamentContent, 
			boolean clubSelection, boolean clubContent, 
			boolean addressSelection, boolean addressContent) {
		visibilityTournamentCollapser = tournamentContent ? View.VISIBLE : View.GONE;
		visibilityTournamentSelection = tournamentSelection ? View.VISIBLE : View.GONE;
		visibilityTournamentContent = tournamentContent ? View.VISIBLE : View.GONE;
		visibilityClubCollapser = clubContent ? View.VISIBLE : View.GONE;
		visibilityClubSelection = clubSelection ? View.VISIBLE : View.GONE;
		visibilityClubContent = clubContent ? View.VISIBLE : View.GONE;
		visibilityAddressCollapser = addressContent ? View.VISIBLE : View.GONE;
		visibilityAddressSelection = addressSelection ? View.VISIBLE : View.GONE;
		visibilityAddressContent = addressContent ? View.VISIBLE : View.GONE;

		llTournamentCollapser.setVisibility(visibilityTournamentCollapser);
		llTournamentContent.setVisibility(visibilityTournamentContent);
		llTournamentAdd.setVisibility(visibilityTournamentContent);
		llTournamentSelection.setVisibility(visibilityTournamentSelection);
		llClubCollapser.setVisibility(visibilityClubCollapser);
		llClubContent.setVisibility(visibilityClubContent);
		llClubAdd.setVisibility(visibilityClubContent);
		llClubSelection.setVisibility(visibilityClubSelection);
		llAddressCollapser.setVisibility(visibilityAddressCollapser);
		llAddressContent.setVisibility(visibilityAddressContent);
		llAddressAdd.setVisibility(visibilityAddressContent);
		llAddressSelection.setVisibility(visibilityAddressSelection);
	}
}