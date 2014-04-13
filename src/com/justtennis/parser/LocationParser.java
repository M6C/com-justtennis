package com.justtennis.parser;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.service.AddressService;
import com.justtennis.db.service.ClubService;
import com.justtennis.db.service.TournamentService;
import com.justtennis.domain.Address;
import com.justtennis.domain.Club;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Invite.INVITE_TYPE;
import com.justtennis.domain.Player;
import com.justtennis.domain.Tournament;

public class LocationParser extends GenericParser {
	
	private static LocationParser instance = null;
	
	private AddressService addressService;
	private ClubService clubService;
	private TournamentService tournamentService;

	private LocationParser() {
		
	}
	private LocationParser(Context context, INotifierMessage notificationMessage) {
		addressService = new AddressService(context, notificationMessage);
		clubService = new ClubService(context, notificationMessage);
		tournamentService = new TournamentService(context, notificationMessage);
	}

	public static LocationParser getInstance(Context context, INotifierMessage notificationMessage) {
		if (instance==null)
			instance = new LocationParser(context, notificationMessage);
		return instance;
	}

	public String[] toAddress(Invite invite) {
		return invite == null ? null : getLocationLine(invite);
	}

	public String[] toAddress(Player player) {
		return player == null || player.getIdClub() == null ? null : getAddress(player.getIdClub());
	}

	public void setAddress(Invite invite, Address address) {
		if (invite != null && address != null) {
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
	}

	private String[] getLocationLine(Invite invite) {
		if (invite != null) {
			if (invite.getType() == INVITE_TYPE.ENTRAINEMENT) {
				if (invite.getClub() != null && invite.getClub().getId() != null) {
					return getAddress(invite.getClub().getId());
				}
			} else {
				if (invite.getTournament() != null) {
					Tournament tournament = tournamentService.find(invite.getTournament().getId());
					if (tournament != null && tournament.getSubId() != null) {
						return getAddress(tournament.getSubId());
					}
				}
			}
		}
		return null;
	}
	
	private String[] getAddress(long idClub) {
		String name = "";
		String line1 = "";
		String line2 = "";
		Club club = clubService.find(idClub);
		if (club != null) {
			if (club.getName()!=null) {
				name = club.getName();
			}
			if (club.getSubId() != null) {
				Address address = addressService.find(club.getSubId());
				if (address != null) {
					if (address.getLine1() != null) {
						line1 = address.getLine1();
					}
					if (address.getPostalCode() != null) {
						line2 += address.getPostalCode();
					}
					if (address.getCity() != null) {
						line2 += " " + address.getCity();
					}
					line2 = line2.trim();
				}
			}
		}
		if ("".equals(name) && "".equals(line1) && "".equals(line2)) {
			return null;
		} else {
			return new String[] {name, line1, line2};
		}
	}
}