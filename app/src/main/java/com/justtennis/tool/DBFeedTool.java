package com.justtennis.tool;

import android.content.Context;

import com.justtennis.ApplicationConfig;
import com.justtennis.db.service.AddressService;
import com.justtennis.db.service.ClubService;
import com.justtennis.db.service.InviteService;
import com.justtennis.db.service.PlayerService;
import com.justtennis.db.service.SaisonService;
import com.justtennis.db.service.ScoreSetService;
import com.justtennis.db.service.TournamentService;
import com.justtennis.db.service.UserService;
import com.justtennis.domain.Address;
import com.justtennis.domain.Club;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Player;
import com.justtennis.domain.Saison;
import com.justtennis.domain.ScoreSet;
import com.justtennis.domain.Tournament;
import com.justtennis.domain.User;
import com.justtennis.manager.TypeManager;
import com.justtennis.notifier.NotifierMessageLogger;

import org.gdocument.gtracergps.launcher.log.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

public class DBFeedTool {

    private static final String TAG = DBFeedTool.class.getCanonicalName();

    private DBFeedTool() {
    }

    public static void feed(Context context) {

        logMe("Feed database:" + ApplicationConfig.FEED_DATABASE);
        if (!ApplicationConfig.FEED_DATABASE) {
            return;
        }

        Random rnd = new Random(1);

        Saison saison = feedSaison(context);
        User user = feedUser(context, saison);

        List<Tournament> listTournament = new ArrayList<>();
        List<Club> listClub = new ArrayList<>();
        List<Address> listAddress = new ArrayList<>();

        feedAddress(context, listAddress);
        feedClub(context, rnd, listClub, listAddress);
        feedTournament(context, rnd, saison, listTournament, listClub);

        List<Player> listPlayer = new ArrayList<>();
        for (TypeManager.TYPE type : TypeManager.TYPE.values()) {
            TypeManager.getInstance().setType(type);

            feedPlayer(context, rnd, listClub, listPlayer, type);
            feedInvite(context, rnd, saison, user, listTournament, listClub, listPlayer);
        }
    }

    private static User feedUser(Context context, Saison saison) {
        NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
        UserService userService = new UserService(context, notifier);
        User user = userService.findFirst();
        if (user == null) {
            user = createUser(saison);
            userService.createOrUpdate(user);
        }
        return user;
    }

    private static Saison feedSaison(Context context) {
        NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
        SaisonService saisonService = new SaisonService(context, notifier);
        Saison saison = saisonService.getSaisonActiveOrFirst();
        if (saison == null) {
            saison = SaisonService.build(Calendar.getInstance());
            saisonService.createOrUpdate(saison);
        }
        return saison;
    }

    private static void feedInvite(Context context, Random rnd, Saison saison, User user, List<Tournament> listTournament, List<Club> listClub, List<Player> listPlayer) {
        NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
        InviteService inviteService = new InviteService(context, notifier);
        if (inviteService.getCount() <= 0) {
            TypeManager.TYPE type = TypeManager.getInstance().getType();
            int nbPlayer = listPlayer.size();
            int nbClub = listClub.size();
            int nbTournament = listTournament.size();
            for (int i = 0; i < 10; i++) {
                int idxClub = rnd.nextInt(nbClub);
                int idxTournament = rnd.nextInt(nbTournament);
                int index = rnd.nextInt(nbPlayer);
                Player player = listPlayer.get(index > 0 ? index - 1 : index);
                Invite invite = createInvite(saison, user, player, type);
                switch (type) {
                    case COMPETITION:
                        invite.setClub(listClub.get(idxClub));
                        break;
                    case TRAINING:
                    default:
                        invite.setTournament(listTournament.get(idxTournament));
                }

                // Add Score on Not Randomly
                initInviteScoreSet(context, rnd, invite);

                inviteService.createOrUpdate(invite);
            }
        }
    }

    private static void initInviteScoreSet(Context context, Random rnd, Invite invite) {
        if (rnd.nextInt(100) % 3 != 0) {
            NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
            ScoreSetService scoreSetService = new ScoreSetService(context, notifier);
            List<ScoreSet> score = new ArrayList<>();
            for (int nb = 0; nb < rnd.nextInt(3); nb++) {
                int iScore = rnd.nextInt(100);
                // Win or Loose Score Randomly
                int[] val = (iScore % 2 == 0) ? new int[]{6, rnd.nextInt(5)} : new int[]{rnd.nextInt(5), 6};

                ScoreSet scoreSet = new ScoreSet(invite, nb, val[0], val[1]);
                scoreSetService.createOrUpdate(scoreSet);
                score.add(scoreSet);
            }
            invite.setListScoreSet(score);
        }
    }

    private static void feedPlayer(Context context, Random rnd, List<Club> listClub, List<Player> listPlayer, TypeManager.TYPE type) {
        NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
        PlayerService playerService = new PlayerService(context, notifier);

        listPlayer.clear();
        if (playerService.getCount() <= 0) {
            int nbClub = listClub.size();
            listPlayer.add(playerService.getUnknownPlayer());
            for (int i = 0; i < 10; i++) {
                int idxClub = rnd.nextInt(nbClub);

                Player player = new Player();
                player.setFirstName("Player " + i);
                player.setLastName("Name" + i);
                player.setType(type);
                player.setIdClub(listClub.get(idxClub).getId());
                playerService.createOrUpdate(player);
                listPlayer.add(player);
            }
        } else {
            listPlayer.addAll(playerService.getList());
        }
    }

    private static void feedTournament(Context context, Random rnd, Saison saison, List<Tournament> listTournament, List<Club> listClub) {
        NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
        TournamentService tournamentService = new TournamentService(context, notifier);
        listTournament.clear();
        listTournament.addAll(tournamentService.getList());
        if (listTournament.isEmpty()) {
            int nbClub = listClub.size();
            for (int i = 0; i < 10; i++) {
                int index = rnd.nextInt(nbClub);
                Tournament tournament = new Tournament();
                tournament.setName("Tournament " + i);
                tournament.setSaison(saison);
                tournament.setSubId(listClub.get(index > 0 ? index - 1 : index).getId());
                tournamentService.createOrUpdate(tournament);
                listTournament.add(tournament);
            }
        }
    }

    private static void feedClub(Context context, Random rnd, List<Club> listClub, List<Address> listAddress) {
        NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
        ClubService clubService = new ClubService(context, notifier);
        listClub.clear();
        listClub.addAll(clubService.getList());
        if (listClub.isEmpty()) {
            int nbAddress = listAddress.size();
            for (int i = 0; i < 10; i++) {
                int index = rnd.nextInt(nbAddress);
                Club club = new Club();
                club.setName("Club " + i);
                club.setSubId(listAddress.get(index > 0 ? index - 1 : index).getId());
                clubService.createOrUpdate(club);
                listClub.add(club);
            }
        }
    }

    private static void feedAddress(Context context, List<Address> listAddress) {
        NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
        AddressService addressService = new AddressService(context, notifier);
        listAddress.clear();
        listAddress.addAll(addressService.getList());
        if (listAddress.isEmpty()) {
            for (int i = 0; i < 10; i++) {
                Address address = new Address();
                address.setName("Address " + i);
                address.setLine1(i + " street of stars");
                address.setPostalCode(Integer.toString(i));
                address.setCity("Sky City " + i);
                addressService.createOrUpdate(address);
                listAddress.add(address);
            }
        }
    }

    private static User createUser(Saison saison) {
        User ret  = new User();
        ret.setBirthday("01/02/2003");
        ret.setFirstName("Current");
        ret.setLastName("User");
        ret.setPhonenumber("0123456789");
        ret.setAddress("1 rue de la rivière");
        ret.setLocality("Ville");
        ret.setPostalCode("01234");
        ret.setIdSaison(saison.getId());
        return ret;
    }

    private static Invite createInvite(Saison saison, User user, Player player, TypeManager.TYPE type) {
        Invite invite = new Invite();
        invite.setSaison(saison);
        invite.setUser(user);
        invite.setType(type);
        invite.setPlayer(player);

        Calendar calendar = GregorianCalendar.getInstance(ApplicationConfig.getLocal());
        calendar.setTime(new Date());
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        invite.setDate(calendar.getTime());

        return invite;
    }

    private static void logMe(String msg) {
        com.crashlytics.android.Crashlytics.log(msg);
        Logger.logMe(TAG, msg);
    }
}