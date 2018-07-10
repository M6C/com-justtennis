package com.justtennis.parser;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.ApplicationConfig;
import com.justtennis.db.service.AddressService;
import com.justtennis.db.service.ClubService;
import com.justtennis.db.service.TournamentService;
import com.justtennis.domain.Address;
import com.justtennis.domain.Club;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Player;
import com.justtennis.domain.Tournament;
import com.justtennis.manager.TypeManager;

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
		String[] ret = null;
		if (invite != null) {
			if (invite.getType() == TypeManager.TYPE.TRAINING) {
				if (invite.getClub() != null && invite.getClub().getId() != null) {
					ret = getAddress(new Club(invite.getClub().getId()));
				}
			} else {
				if (invite.getTournament() != null) {
					Tournament tournament = tournamentService.find(invite.getTournament().getId());
					if (tournament != null) {
						ret = getAddress(tournament);
					}
				}
				if (invite.getClub() != null && invite.getClub().getId() != null) {
					String[] clubAddress = getAddress(new Club(invite.getClub().getId()));
					if (clubAddress != null) {
						if (ret == null) {
							ret = clubAddress;
						} else {
							if (ret[0].isEmpty()) {
								ret[0] = clubAddress[0];
							}
							ret[1] = clubAddress[1];
							ret[2] = clubAddress[2];
						}
					}
				}
			}
		}
		return ret;
	}

	public String[] toAddress(Club club) {
		return getAddress(club);
	}

	public String[] toAddress(Player player) {
		String[] ret = null;
		if (player != null) {
			switch (player.getType()) {
				case TRAINING:
					if (player.getIdClub() != null) {
						ret = getAddress(new Club(player.getIdClub()));
					}
					break;
				case COMPETITION:
					if (player.getIdClub() != null) {
						ret = getAddress(new Club(player.getIdClub()));
					} else if (player.getIdTournament() != null) {
						Tournament tournament = tournamentService.find(player.getIdTournament());
						if (tournament != null) {
							ret = getAddress(tournament);
							if (ret != null && ret.length >= 3) {
								if (ret[0] != null && ret[1] == null && ret[2] == null) {
									String[] address = getAddress(new Club(player.getIdClub()));
									if (address != null && address.length >= 3) {
										ret[1] = address[1];
										ret[2] = address[2];
									}
								}
							}
						}
					}
					break;
			}

			if (player.getIdAddress() != null) {
				String[] addressLine = getAddress(new Address(player.getIdAddress()));
				if (addressLine != null) {
					if (ret == null) {
						ret = addressLine;
					} else {
						ret[1] = addressLine[1];
						ret[2] = addressLine[2];
					}
				}
			}
		}
		return ret;
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
			if (invite.getType() == TypeManager.TYPE.TRAINING) {
				if (invite.getClub() != null && invite.getClub().getId() != null) {
					return getAddress(new Club(invite.getClub().getId()));
				}
			} else {
				if (invite.getTournament() != null) {
					Tournament tournament = tournamentService.find(invite.getTournament().getId());
					if (tournament != null) {
						return getAddress(tournament);
					}
				}
			}
		}
		return null;
	}

	private String[] getAddress(Tournament tournament) {
		String[] address = null;
		if (address == null && tournament != null && tournament.getName() != null && !tournament.getName().equals("")) {
			address = new String[]{tournament.getName(), "", ""};
		}
		if (tournament != null && tournament.getSubId() != null) {
			String[] clubAddress = getAddress(new Club(tournament.getSubId()));
			if (clubAddress != null) {
				if (address == null) {
					address = clubAddress;
				} else {
					if (address[0].isEmpty()) {
						address[0] = clubAddress[0];
					}
					address[1] = clubAddress[1];
					address[2] = clubAddress[2];
				}
			}
		}
		if (ApplicationConfig.SHOW_ID) {
			if (address != null && address.length == 3) {
				address[0] = "[" + tournament.getId() + "] " + address[0];
			}
		}
		return address;
	}

	private String[] getAddress(Club club) {
		String name = "";
		String line1 = "";
		String line2 = "";
		if (club != null && club.getId() != null) {
			club = clubService.find(club.getId());
			if (club != null) {
				if (club.getName()!=null) {
					name = club.getName();
				}
				if (club.getSubId() != null) {
					String[] addressLine = getAddress(new Address(club.getSubId()));
					if (addressLine != null) {
						if (name == null || "".equals(name)) {
							name = addressLine[0];
						}
						line1 = addressLine[1];
						line2 = addressLine[2];
					}
				}
				if (ApplicationConfig.SHOW_ID) {
					name = "[" + club.getId() + "] " + name;
				}
			}
		}
		if ("".equals(name) && "".equals(line1) && "".equals(line2)) {
			return null;
		} else {
			return new String[] {name, line1, line2};
		}
	}
	
	private String[] getAddress(Address address) {
		String name = "";
		String line1 = "";
		String line2 = "";
		if (address != null && address.getId() != null) {
			address = addressService.find(address.getId());
			if (address != null) {
				if (address.getName() != null) {
					name = address.getName();
				}
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
				if (ApplicationConfig.SHOW_ID) {
					name = "[" + address.getId() + "] " + name;
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