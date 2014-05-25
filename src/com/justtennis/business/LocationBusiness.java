package com.justtennis.business;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.R;
import com.justtennis.activity.LocationActivity;
import com.justtennis.db.service.AddressService;
import com.justtennis.db.service.ClubService;
import com.justtennis.db.service.InviteService;
import com.justtennis.db.service.PojoNamedService;
import com.justtennis.db.service.TournamentService;
import com.justtennis.domain.Address;
import com.justtennis.domain.Club;
import com.justtennis.domain.GenericDBPojo;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Tournament;
import com.justtennis.manager.TypeManager;

public class LocationBusiness {

	@SuppressWarnings("unused")
	private static final String TAG = LocationBusiness.class.getSimpleName();

	private Context context;

	private InviteService inviteService;
	private AddressService addressService;
	private ClubService clubService;
	private TournamentService tournamentService;
	private PojoNamedService pojoNamedService;

	private Invite invite;
	private List<Address> listAddress = new ArrayList<Address>();
	private List<String> listTxtAddress = new ArrayList<String>();
	private List<Club> listClub = new ArrayList<Club>();
	private List<String> listTxtClub = new ArrayList<String>();
	private List<Tournament> listTournament = new ArrayList<Tournament>();
	private List<String> listTxtTournament = new ArrayList<String>();

	public LocationBusiness(Context context, INotifierMessage notificationMessage) {
		this.context = context;
		inviteService = new InviteService(context, notificationMessage);
		addressService = new AddressService(context, notificationMessage);
		clubService = new ClubService(context, notificationMessage);
		tournamentService = new TournamentService(context, notificationMessage);
		pojoNamedService = new PojoNamedService();
	}

	public void initializeData(Intent intent) {
		invite = (Invite) intent.getSerializableExtra(LocationActivity.EXTRA_INVITE);

		initializeDataAddress();
		initializeDataClub();
		initializeDataTournament();
	}

	public void initializeDataAddress() {
		listAddress.clear();
		listAddress.addAll(addressService.getList());
		pojoNamedService.order(listAddress);

		listAddress.add(0, addressService.getEmptyAddress());
		listAddress.get(0).setName(context.getString(R.string.txt_address));

		initializeTxtAddress();
	}

	public void initializeTxtAddress() {
		listTxtAddress.clear();
		listTxtAddress.addAll(pojoNamedService.getNames(listAddress));
	}
	
	public void initializeDataClub() {
		listClub.clear();
		listClub.addAll(clubService.getList());
		pojoNamedService.order(listClub);

		listClub.add(0, clubService.getEmptyClub());
		listClub.get(0).setName(context.getString(R.string.txt_club));

		initializeTxtClub();
	}

	public void initializeTxtClub() {
		listTxtClub.clear();
		listTxtClub.addAll(pojoNamedService.getNames(listClub));
	}

	public void initializeDataTournament() {
		listTournament.clear();
		listTournament.addAll(tournamentService.getList());
		pojoNamedService.order(listTournament);

		listTournament.add(0, tournamentService.getEmptyTournament());
		listTournament.get(0).setName(context.getString(R.string.txt_tournament));

		initializeTxtTournament();
	}

	public void initializeTxtTournament() {
		listTxtTournament.clear();
		listTxtTournament.addAll(pojoNamedService.getNames(listTournament));
	}

	public Address addAddress(String name, String line1, String postalCode, String city) {
		Address address = null;
		if (getAddress() != null && getAddress() != null && addressService.isRealAddress(getAddress())) {
			address = getAddress();
		} else {
			address = new Address();
		}
		address.setName(name);
		address.setLine1(line1);
		address.setPostalCode(postalCode);
		address.setCity(city);
		addressService.createOrUpdate(address);
		return address;
	}
	
	public Club addClub(String name, Long idAddress) {
		Club club = null;
		if (getClub() != null && getClub().getId() != null && clubService.isRealClub(getClub())) {
			club = getClub();
		} else {
			club = new Club();
		}
		club.setName(name);
		club.setSubId(idAddress);
		clubService.createOrUpdate(club);
		return club;
	}
	
	public Tournament addTournament(String name, Long idClub) {
		Tournament tournament = null;
		if (getTournament() != null && getTournament().getId()!=null && tournamentService.isRealTournament(getTournament())) {
			tournament = getTournament();
		} else {
			tournament = new Tournament();
		}
		tournament.setName(name);
		tournament.setSubId(idClub);
		tournamentService.createOrUpdate(tournament);
		return tournament;
	}

	public void deleteAddress() {
		Address address = getAddress();
		if (address!=null && !addressService.isEmptyAddress(address)) {
			addressService.delete(address);
		}
	}

	public void deleteClub() {
		Club address = getClub();
		if (address!=null && !clubService.isEmptyClub(address)) {
			clubService.delete(address);
		}
	}
	
	public void deleteTournament() {
		Tournament address = getTournament();
		if (address!=null && !tournamentService.isEmptyTournament(address)) {
			tournamentService.delete(address);
		}
	}

	public int getAddressPosition(Address address) {
		return getPojoPosition(listAddress, address);
	}
	
	public int getClubPosition(Club club) {
		return getPojoPosition(listClub, club);
	}

	public int getTournamentPosition(Tournament tournament) {
		return getPojoPosition(listTournament, tournament);
	}

	public boolean isEmptyTournament(Tournament tournament) {
		return tournamentService.isEmptyTournament(tournament);
	}

	public boolean isEmptyClub(Club club) {
		return clubService.isEmptyClub(club);
	}
	
	public boolean isEmptyAddress(Address address) {
		return addressService.isEmptyAddress(address);
	}

	public void save() {
		inviteService.createOrUpdate(invite);
	}

	public TypeManager.TYPE getType() {
		return invite.getType();
	}

	public Address getAddress() {
		return invite.getAddress() == null ? null : invite.getAddress();
	}

	public Club getClub() {
		return invite.getClub() == null ? null : invite.getClub();
	}
	
	public Tournament getTournament() {
		return invite.getTournament() == null ? null : invite.getTournament();
	}

	public List<Address> getListAddress() {
		return listAddress;
	}

	public void setListAddress(List<Address> listAddress) {
		this.listAddress = listAddress;
	}

	public List<Club> getListClub() {
		return listClub;
	}

	public void setListClub(List<Club> listClub) {
		this.listClub = listClub;
	}

	public List<Tournament> getListTournament() {
		return listTournament;
	}

	public void setListTournament(List<Tournament> listTournament) {
		this.listTournament = listTournament;
	}

	public List<String> getListTxtAddress() {
		return listTxtAddress;
	}

	public void setListTxtAddress(List<String> listTxtAddress) {
		this.listTxtAddress = listTxtAddress;
	}

	public List<String> getListTxtClub() {
		return listTxtClub;
	}

	public void setListTxtClub(List<String> listTxtClub) {
		this.listTxtClub = listTxtClub;
	}

	public List<String> getListTxtTournament() {
		return listTxtTournament;
	}

	public void setListTxtTournament(List<String> listTxtTournament) {
		this.listTxtTournament = listTxtTournament;
	}

	public void setAddress(Address address) {
		if (address.getId() != null) {
			invite.setAddress(address);
		}
		else if (address.getLine1() != null ||
			address.getPostalCode() != null ||
			address.getCity() != null) {
			if (invite.getAddress() != null) {
				invite.getAddress().setLine1(address.getLine1());
				invite.getAddress().setPostalCode(address.getPostalCode());
				invite.getAddress().setCity(address.getCity());
			} else {
				invite.setAddress(address);
			}
		}
	}

	public void setClub(Club club) {
		invite.setClub(club);
	}
	
	public void setTournament(Tournament tournament) {
		invite.setTournament(tournament);
	}

	private <P extends GenericDBPojo<Long>> int getPojoPosition(List<P> listPojo, P pojo) {
		int ret=-1;
		for(int i=0 ; i<listPojo.size() ; i++) {
			if (listPojo.get(i).getId().equals(pojo.getId())) {
				ret = i;
				break;
			}
		}
		return ret;
	}
}