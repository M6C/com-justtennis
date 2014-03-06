package com.justtennis.business;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.R;
import com.justtennis.activity.InviteLocationActivity;
import com.justtennis.db.service.AddressService;
import com.justtennis.db.service.ClubService;
import com.justtennis.db.service.PojoNamedService;
import com.justtennis.db.service.TournamentService;
import com.justtennis.domain.Address;
import com.justtennis.domain.Club;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Invite.INVITE_TYPE;
import com.justtennis.domain.Tournament;

public class InviteLocationBusiness {

	private static final String TAG = InviteLocationBusiness.class.getSimpleName();

	private Context context;

	private AddressService addressService;
	private ClubService clubService;
	private TournamentService tournamentService;
	private PojoNamedService pojoNamedService;

	private Invite invite;
	private List<Address> listAddress = new ArrayList<Address>();
	private String[] listTxtAddress;
	private List<Club> listClub = new ArrayList<Club>();
	private String[] listTxtClub;
	private List<Tournament> listTournament = new ArrayList<Tournament>();
	private String[] listTxtTournament;

	public InviteLocationBusiness(Context context, INotifierMessage notificationMessage) {
		this.context = context;
		addressService = new AddressService(context, notificationMessage);
		clubService = new ClubService(context, notificationMessage);
		tournamentService = new TournamentService(context, notificationMessage);
		pojoNamedService = new PojoNamedService();
	}

	public void initializeData(Intent intent) {
		invite = (Invite) intent.getSerializableExtra(InviteLocationActivity.EXTRA_INVITE);

		initializeDataAddress();
		initializeDataClub();
		initializeDataTournament();
	}

	public void initializeDataAddress() {
		listAddress.clear();
		listAddress.add(addressService.getEmptyAddress());
		listAddress.get(0).setName(context.getString(R.string.txt_address));
		listAddress.addAll(addressService.getList());
		pojoNamedService.order(listAddress);
		setListTxtAddress(pojoNamedService.getNames(listAddress));
	}
	
	public void initializeDataClub() {
		listClub.clear();
		listClub.add(clubService.getEmptyClub());
		listClub.get(0).setName(context.getString(R.string.txt_club));
		listClub.addAll(clubService.getList());
		pojoNamedService.order(listClub);
		setListTxtClub(pojoNamedService.getNames(listClub));
	}

	public void initializeDataTournament() {
		listTournament.clear();
		listTournament.add(tournamentService.getEmptyTournament());
		listTournament.get(0).setName(context.getString(R.string.txt_tournament));
		listTournament.addAll(tournamentService.getList());
		pojoNamedService.order(getListTournament());
		setListTxtTournament(pojoNamedService.getNames(getListTournament()));
	}

	public void addAddress(String name, String line1, String postalCode, String city) {
		Address address = new Address();
		address.setName(name);
		address.setLine1(line1);
		address.setPostalCode(postalCode);
		address.setCity(city);
		addressService.createOrUpdate(address);
	}
	
	public void addClub(String name, Long idAddress) {
		Club club = new Club();
		club.setName(name);
		club.setIdAddress(idAddress);
		clubService.createOrUpdate(club);
	}
	
	public void addTournament(String name, Long idClub) {
		Tournament tournament = new Tournament();
		tournament.setName(name);
		tournament.setIdClub(idClub);
		tournamentService.createOrUpdate(tournament);
	}

	public void save() {
	}

	public INVITE_TYPE getType() {
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

	public String[] getListTxtAddress() {
		return listTxtAddress;
	}

	public void setListTxtAddress(String[] listTxtAddress) {
		this.listTxtAddress = listTxtAddress;
	}

	public String[] getListTxtClub() {
		return listTxtClub;
	}

	public void setListTxtClub(String[] listTxtClub) {
		this.listTxtClub = listTxtClub;
	}

	public String[] getListTxtTournament() {
		return listTxtTournament;
	}

	public void setListTxtTournament(String[] listTxtTournament) {
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
}