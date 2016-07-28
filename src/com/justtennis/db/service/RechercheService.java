package com.justtennis.db.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.gdocument.gtracergps.launcher.log.Logger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.business.LocationAddressBusiness;
import com.justtennis.db.sqlite.datasource.DBAddressDataSource;
import com.justtennis.db.sqlite.datasource.DBClubDataSource;
import com.justtennis.db.sqlite.datasource.DBTournamentDataSource;
import com.justtennis.domain.Address;
import com.justtennis.domain.Club;
import com.justtennis.domain.RechercheResult;
import com.justtennis.domain.Tournament;

public class RechercheService {

	private static final String TAG = RechercheService.class.getCanonicalName();

	public enum TYPE {
		CLUB, TOURNAMENT, ADDRESS
	};

	protected SQLiteDatabase db;
	private DBClubDataSource dbClubDataSource;
	private DBTournamentDataSource dbTournamentDataSource;
	private DBAddressDataSource dbAddressDataSource;

	public RechercheService(Context context, INotifierMessage notificationMessage) {
		dbClubDataSource = new DBClubDataSource(context, notificationMessage);
		dbTournamentDataSource = new DBTournamentDataSource(context, notificationMessage);
		dbAddressDataSource = new DBAddressDataSource(context, notificationMessage);
	}

	public List<RechercheResult> find(TYPE[] type, String data) {
		Date dateStart = new Date();
		List<TYPE> listType = Arrays.asList(type);
		List<RechercheResult> ret = new ArrayList<RechercheResult>();

		if (listType.contains(TYPE.TOURNAMENT)) {
			List<Tournament> listTournament = dbTournamentDataSource.getLikeByName(data);
			for(Tournament tournament : listTournament) {
				ret.add(new RechercheResult(TYPE.TOURNAMENT, tournament.getId(), tournament.getName()));
			}
			logMe("find(data:" + data + ", tournament.size:"+listTournament.size()+")", dateStart);
		}

		if (listType.contains(TYPE.CLUB)) {
			List<Club> listClub = dbClubDataSource.getLikeByName(data);
			for(Club club : listClub) {
				ret.add(new RechercheResult(TYPE.CLUB, club.getId(), club.getName()));
			}
			logMe("find(data:" + data + ", club.size:"+listClub.size()+")", dateStart);
		}

		if (listType.contains(TYPE.ADDRESS)) {
			List<Address> listAddress = dbAddressDataSource.getLikeByLine(data);
			for(Address address : listAddress) {
				ret.add(new RechercheResult(TYPE.ADDRESS, address.getId(), LocationAddressBusiness.formatAddressName(address)));
			}
			logMe("find(data:" + data + ", address.size:"+listAddress.size()+")", dateStart);
		}

		logMe("find(data:" + data + ", ret.size:"+ret.size()+")", dateStart);

		return ret;
	}

	protected String getTag() {
		return TAG;
	}

	protected void logMe(String msg, Date dateStart) {
		logMe("DB Execution time:" + (new Date().getTime() - dateStart.getTime()) + "millisecond - " + msg);
    }

	protected void logMe(String msg) {
		Logger.logMe(getTag(), msg);
    }
}