package com.justtennis.activity;

import java.io.Serializable;
import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.cameleon.common.android.factory.FactoryDialog;
import com.justtennis.R;
import com.justtennis.adapter.CustomArrayAdapter;
import com.justtennis.business.LocationBusiness;
import com.justtennis.domain.Address;
import com.justtennis.domain.Club;
import com.justtennis.domain.Tournament;
import com.justtennis.manager.TypeManager;
import com.justtennis.notifier.NotifierMessageLogger;

public class LocationActivity extends GenericActivity {

	@SuppressWarnings("unused")
	private static final String TAG = LocationActivity.class.getSimpleName();

	public static final String EXTRA_INVITE = "EXTRA_INVITE";
	public static final String EXTRA_OUT_LOCATION = "EXTRA_OUT_LOCATION";

	private LocationBusiness business;

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
	private EditText etTournamentName;
	private EditText etClubName;
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
	private View ivAddressDelete;
	private View ivClubDelete;
	private View ivClubAddressDelete;
	private View ivTournamentDelete;
	private View ivTournamentClubDelete;

	private CustomArrayAdapter<String> adapterClubAddress;
	private CustomArrayAdapter<String> adapterAddress;

	private CustomArrayAdapter<String> adapterTournamentClub;
	private CustomArrayAdapter<String> adapterClub;

	private CustomArrayAdapter<String> adapterTournament;

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
		etClubName = (EditText)findViewById(R.id.et_club_name);
		spClub = (Spinner)findViewById(R.id.sp_club_list);
		spClubAddress = (Spinner)findViewById(R.id.sp_club_address_list);

		llTournamentContent = (LinearLayout)findViewById(R.id.ll_tournament_content);
		llTournamentCollapser = (LinearLayout)findViewById(R.id.ll_tournament_collapser);
		llTournamentAdd = findViewById(R.id.ll_tournament_add);
		llTournamentSelection = findViewById(R.id.ll_tournament_selection);
		etTournamentName = (EditText)findViewById(R.id.et_tournament_name);
		spTournament = (Spinner)findViewById(R.id.sp_tournament_list);
		spTournamentClub = (Spinner)findViewById(R.id.sp_tournament_club_list);

		ivAddressDelete = findViewById(R.id.iv_address_delete);
		ivClubDelete = findViewById(R.id.iv_club_delete);
		ivTournamentDelete = findViewById(R.id.iv_tournament_delete);

		ivTournamentClubDelete = findViewById(R.id.iv_tournament_club_delete);
		ivClubAddressDelete = findViewById(R.id.iv_club_address_delete);
		
		business = new LocationBusiness(this, NotifierMessageLogger.getInstance());
		TypeManager.getInstance().initializeActivity(findViewById(R.id.layout_main), false);
	}

	@Override
	protected void onResume() {
		super.onResume();
		initializeData();

		visibilityTournamentCollapser = (business.getType() == TypeManager.TYPE.MATCH) ? View.VISIBLE : View.GONE;
		visibilityClubCollapser = (business.getType() == TypeManager.TYPE.MATCH) ? View.GONE : View.VISIBLE;

		llAddressCollapser.setVisibility(visibilityAddressCollapser);
		llAddressContent.setVisibility(visibilityAddressContent);
		llClubCollapser.setVisibility(visibilityClubCollapser);
		llClubContent.setVisibility(visibilityClubContent);
		llTournamentCollapser.setVisibility(visibilityTournamentCollapser);
		llTournamentContent.setVisibility(visibilityTournamentContent);
	}

	@Override
	public void onBackPressed() {
		if (llAddressContent.getVisibility() == View.VISIBLE) {
			manageVisibility(false, false, false, false, true, false);
		} else if (llAddressSelection.getVisibility() == View.VISIBLE) {
			manageVisibility(false, false, false, true, false, false);
		} else {
			onClickCancel(null);
			super.onBackPressed();
		}
	}

	public void onClickModify(View view) {
		saveAddress();
		saveClub();
		saveTournament();

		returnResult();
	}

	private void returnResult() {
		Serializable out = null;
		if (business.getType() == TypeManager.TYPE.MATCH) {
			out = business.getTournament();
		} else {
			out = business.getClub();
		}
		Intent data = new Intent();
		data.putExtra(EXTRA_OUT_LOCATION, out);
		setResult(Activity.RESULT_OK, data);

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

	public void onClickAddressDelete(View view) {
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				business.deleteAddress();

				business.initializeDataAddress();
				adapterClubAddress.notifyDataSetChanged();
				adapterAddress.notifyDataSetChanged();
			}
		};
		FactoryDialog.getInstance()
			.buildOkCancelDialog(this, listener, R.string.dialog_location_address_delete_title, R.string.dialog_location_address_delete_message)
			.show();
	}

	public void onClickClubDelete(View view) {
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				business.deleteClub();

				business.initializeDataClub();
				adapterTournamentClub.notifyDataSetChanged();
				adapterClub.notifyDataSetChanged();
			}
		};
		FactoryDialog.getInstance()
			.buildOkCancelDialog(this, listener, R.string.dialog_location_club_delete_title, R.string.dialog_location_club_delete_message)
			.show();
	}
	
	public void onClickClubAddressDelete(View view) {
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				business.deleteAddress();

				business.initializeDataAddress();
				adapterClubAddress.notifyDataSetChanged();
				adapterAddress.notifyDataSetChanged();
			}
		};
		FactoryDialog.getInstance()
			.buildOkCancelDialog(this, listener, R.string.dialog_location_address_delete_title, R.string.dialog_location_address_delete_message)
			.show();
	}
	
	public void onClickTournamentDelete(View view) {
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				business.deleteTournament();

				business.initializeDataTournament();
				adapterTournament.notifyDataSetChanged();
			}
		};
		FactoryDialog.getInstance()
			.buildOkCancelDialog(this, listener, R.string.dialog_location_tournament_delete_title, R.string.dialog_location_tournament_delete_message)
			.show();
	}
	
	public void onClickTournamentClubDelete(View view) {
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				business.deleteClub();

				business.initializeDataClub();
				adapterClub.notifyDataSetChanged();
				adapterTournamentClub.notifyDataSetChanged();
			}
		};
		FactoryDialog.getInstance()
			.buildOkCancelDialog(this, listener, R.string.dialog_location_club_delete_title, R.string.dialog_location_club_delete_message)
			.show();
	}

	public void onClickAddressAddValidate(View view) {
		Address address = business.addAddress(getText(etAddressName), getText(etAddressLine1), getText(etAddressPostalCode), getText(etAddressCity));
		business.setAddress(address);

		business.initializeDataAddress();
		adapterClubAddress.notifyDataSetChanged();
		adapterAddress.notifyDataSetChanged();
		manageVisibility(false, false, false, true, false, false);

		spClubAddress.setSelection(business.getAddressPosition(address), true);
		spAddress.setSelection(business.getAddressPosition(address), true);
	}
	
	public void onClickClubAdd(View view) {
		llClubAdd.setVisibility(View.VISIBLE);
		llClubSelection.setVisibility(View.GONE);
	}
	
	public void onClickClubAddValidate(View view) {
		Club club = business.addClub(getText(etClubName), business.getAddress().getId());
		business.setClub(club);

		if (business.getType() == TypeManager.TYPE.MATCH) {
			business.initializeDataClub();
			adapterClub.notifyDataSetChanged();
			adapterTournamentClub.notifyDataSetChanged();

			manageVisibility(false, true, false, false, false, false);
			spTournamentClub.setSelection(business.getClubPosition(club), true);
			spClub.setSelection(business.getClubPosition(club), true);
		} else {
			returnResult();
		}
	}
	
	public void onClickClubAddressAdd(View view) {
		manageVisibility(false, false, false, false, false, true);
	}
	
	public void onClickTournamentAdd(View view) {
		llTournamentAdd.setVisibility(View.VISIBLE);
		llTournamentSelection.setVisibility(View.GONE);
	}
	
	public void onClickTournamentAddValidate(View view) {
		Tournament tournament = business.addTournament(getText(etTournamentName), business.getClub().getId());
		business.setTournament(tournament);

		if (business.getType() == TypeManager.TYPE.MATCH) {
			business.initializeDataTournament();
			adapterTournament.notifyDataSetChanged();
			manageVisibility(true, false, false, false, false, false);

			spTournament.setSelection(business.getTournamentPosition(tournament), true);
		} else {
			returnResult();
		}
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
		initializeAddressListener();
		initializeClubListener();
		initializeTournamentListener();
	}

	private void initializeAddressList() {
		adapterAddress = new CustomArrayAdapter<String>(this, business.getListTxtAddress());
		adapterAddress.setDropDownViewResource(R.layout.spinner_dropdown_item);
		spAddress.setAdapter(adapterAddress);
	}

	private void initializeClubList() {
		adapterClub = new CustomArrayAdapter<String>(this, business.getListTxtClub());
		spClub.setAdapter(adapterClub);
	}
	
	private void initializeTournamentList() {
		adapterTournament = new CustomArrayAdapter<String>(this, business.getListTxtTournament());
		spTournament.setAdapter(adapterTournament);
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
			etAddressName.setText(address.getName());
			etAddressLine1.setText(address.getLine1());
			etAddressPostalCode.setText(address.getPostalCode());
			etAddressCity.setText(address.getCity());
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
			etClubName.setText(club.getName());
			if (club.getSubId() != null) {
				List<Address> listClubAddress = business.getListAddress();
				for(Address item : listClubAddress) {
					if (item.getId().equals(club.getSubId())) {
						spClubAddress.setSelection(position, true);
						break;
					} else {
						position++;
					}
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
			etTournamentName.setText(tournament.getName());
			List<Club> listTournamentClub = business.getListClub();
			for(Club item : listTournamentClub) {
				if (item.getId().equals(tournament.getSubId())) {
					spTournamentClub.setSelection(position, true);
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

	private void initializeAddressListener() {
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
				business.setAddress(address);
				ivAddressDelete.setEnabled(address != null && !business.isEmptyAddress(address));
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
				business.setAddress(address);
				ivClubAddressDelete.setEnabled(address != null && !business.isEmptyAddress(address));
			}
		});
	}

	private void initializeClubListener() {
		spClub.setOnItemSelectedListener(adapterClub.new OnItemSelectedListener<Club>() {
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
				ivClubDelete.setEnabled(club != null && !business.isEmptyClub(club));
				returnResult();
			}
		});

		adapterTournamentClub = new CustomArrayAdapter<String>(this, business.getListTxtClub());
		adapterTournamentClub.setDropDownViewResource(R.layout.spinner_dropdown_item);
		spTournamentClub.setAdapter(adapterTournamentClub);
		spTournamentClub.setOnItemSelectedListener(adapterTournamentClub.new OnItemSelectedListener<Club>() {
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
				ivTournamentClubDelete.setEnabled(club != null && !business.isEmptyClub(club));
			}
		});
	}

	private void initializeTournamentListener() {
		spTournament.setOnItemSelectedListener(adapterTournament.new OnItemSelectedListener<Tournament>() {
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
				ivTournamentDelete.setEnabled(tournament != null && !business.isEmptyTournament(tournament));
				returnResult();
			}
		});
	}
}