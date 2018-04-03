package com.justtennis.activity;

import java.util.List;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.justtennis.R;
import com.justtennis.adapter.CustomArrayAdapter;
import com.justtennis.business.LocationTournamentBusiness;
import com.justtennis.domain.Saison;
import com.justtennis.domain.Tournament;
import com.justtennis.notifier.NotifierMessageLogger;

public class LocationTournamentActivity extends GenericSpinnerFormActivity<Tournament> {

	private static final String TAG = LocationTournamentActivity.class.getSimpleName();

	private LocationTournamentBusiness business;

	private Spinner spSaison;

	@Override
	public IGenericSpinnerFormResource getResource() {
		return new IGenericSpinnerFormResource() {
			@Override
			public int getTitleStringId() {
				return R.string.txt_tournament;
			}

			@Override
			public int getNameHintStringId() {
				return R.string.hint_tournament_name;
			}

			@Override
			public Class<LocationClubActivity> getFormListActivityClass() {
				return LocationClubActivity.class;
			}
		};
	}

	@Override
	protected LocationTournamentBusiness getBusiness() {
		if (business==null) {
			business = new LocationTournamentBusiness(this, NotifierMessageLogger.getInstance());
		}
		return business;
	}

	@Override
	protected View buildFormAdd(ViewGroup llForm) {
		View view = getLayoutInflater().inflate(R.layout.location_element_tournament, llForm, false);
		spSaison = (Spinner)view.findViewById(R.id.sp_saison);
		return view;
	}

	@Override
	protected void initializeData() {
		super.initializeData();
		initializeSaisonList();
		initializeSaison();
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
		if (saison != null) {
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
}