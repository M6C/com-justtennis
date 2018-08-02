package com.justtennis.tool;

import android.content.Context;

import com.justtennis.ApplicationConfig;
import com.justtennis.db.service.AddressService;
import com.justtennis.db.service.ClubService;
import com.justtennis.db.service.InviteService;
import com.justtennis.db.service.PlayerService;
import com.justtennis.db.service.RankingService;
import com.justtennis.db.service.SaisonService;
import com.justtennis.db.service.ScoreSetService;
import com.justtennis.db.service.TournamentService;
import com.justtennis.db.service.UserService;
import com.justtennis.domain.Address;
import com.justtennis.domain.Club;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Player;
import com.justtennis.domain.Ranking;
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

    private RankingService rankingService;
    private SaisonService saisonService;
    private UserService userService;
    private InviteService inviteService;
    private ScoreSetService scoreSetService;
    private PlayerService playerService;
    private TournamentService tournamentService;
    private ClubService clubService;
    private AddressService addressService;

    private Ranking ranking;
    private Saison saison = null;
    private User user = null;
    private List<Ranking> listRanking;
    private List<Tournament> listTournament = new ArrayList<>();
    private List<Club> listClub = new ArrayList<>();
    private List<Address> listAddress = new ArrayList<>();
    private List<Player> listPlayer = new ArrayList<>();
    private int nbRanking;
    private int nbTournament;
    private int nbClub;
    private int nbAddress;
    private int nbPlayer;
    private Random rnd = new Random(1);

    private static DBFeedTool instance;

    private DBFeedTool(Context context) {
        initializeService(context);
    }

    public static DBFeedTool getInstance(Context context) {
        if (instance == null) {
            instance = new DBFeedTool(context);
        }
        return instance;
    }

    public void feed() {

        logMe("Feed database:" + ApplicationConfig.FEED_DATABASE);
        if (!ApplicationConfig.FEED_DATABASE) {
            return;
        }

        initRanking();

        feedSaison();

        if (!feedUser()) {
            logMe("Feed database:user already exist. Stop process.");
            return;
        }

        feedAddress();
        feedClub();
        feedTournament();

        for (TypeManager.TYPE type : TypeManager.TYPE.values()) {
            feedPlayer(type);
            feedInvite(type);
        }
    }

    private void initRanking() {
        listRanking = rankingService.getList();
        int idx = rnd.nextInt(listRanking.size());
        int nb = 3;
        int start = (idx <= nb) ? 0 : idx - nb;
        int end = (idx >= (listRanking.size() - nb)) ? listRanking.size() : idx + nb;
        ranking = listRanking.get(idx);
        listRanking = listRanking.subList(start, end);
        nbRanking = listRanking.size();
    }

    private void initializeService(Context context) {
        NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
        rankingService = new RankingService(context, notifier);
        userService = new UserService(context, notifier);
        saisonService = new SaisonService(context, notifier);
        inviteService = new InviteService(context, notifier);
        scoreSetService = new ScoreSetService(context, notifier);
        playerService = new PlayerService(context, notifier);
        tournamentService = new TournamentService(context, notifier);
        clubService = new ClubService(context, notifier);
        addressService = new AddressService(context, notifier);
    }

    private boolean feedUser() {
        user = userService.findFirst();
        if (user == null) {
            user = createUser();
            userService.createOrUpdate(user);
            return true;
        }
        return false;
    }

    private void feedSaison() {
        saison = saisonService.getSaisonActiveOrFirst();
        if (saison == null) {
            saison = SaisonService.build(Calendar.getInstance());
            saisonService.createOrUpdate(saison);
        }
    }

    private void feedInvite(TypeManager.TYPE type) {
        if (inviteService.getCount() <= 0) {
            for (int i = 0; i < 10; i++) {
                int idxClub = rnd.nextInt(nbClub);
                int idxTournament = rnd.nextInt(nbTournament);
                int index = rnd.nextInt(nbPlayer);
                Player player = listPlayer.get(index > 0 ? index - 1 : index);
                Invite invite = createInvite(player, type);
                switch (type) {
                    case COMPETITION:
                        invite.setClub(listClub.get(idxClub));
                        break;
                    case TRAINING:
                    default:
                        invite.setTournament(listTournament.get(idxTournament));
                }
                inviteService.createOrUpdate(invite);

                initInviteScoreSet(rnd, invite);
            }
        }
    }

    private void initInviteScoreSet(Random rnd, Invite invite) {
        // Add Score on Not Randomly
        if (rnd.nextInt(100) % 3 != 0) {
            List<ScoreSet> score = new ArrayList<>();
            for (int nb = 0; nb < rnd.nextInt(3); nb++) {
                int iScore = rnd.nextInt(100);
                // Win or Loose Score Randomly
                int[] val = (iScore % 2 == 0) ? new int[]{6, rnd.nextInt(5)} : new int[]{rnd.nextInt(5), 6};

                ScoreSet scoreSet = new ScoreSet(invite, nb+1, val[0], val[1]);
                scoreSetService.createOrUpdate(scoreSet);
                score.add(scoreSet);
            }
            invite.setListScoreSet(score);
            invite.setScoreResult(ScoreSetService.getInviteScoreResult(score));
            inviteService.createOrUpdate(invite);
        }
    }

    private void feedPlayer(TypeManager.TYPE type) {
        listPlayer.clear();
        if (playerService.getCount() <= 0) {
            listPlayer.add(playerService.getUnknownPlayer());
            for (int i = 0; i < 10; i++) {
                int idxClub = rnd.nextInt(nbClub);

                Player player = new Player();
                player.setFirstName("Player " + i);
                player.setLastName("Name" + i);
                player.setType(type);
                player.setIdClub(listClub.get(idxClub).getId());
                player.setIdRanking(listRanking.get(rnd.nextInt(nbRanking)).getId());
                if (i % 2 == 0) {
                    player.setIdRankingEstimate(listRanking.get(rnd.nextInt(nbRanking)).getId());
                }
                playerService.createOrUpdate(player);
                listPlayer.add(player);
            }
        } else {
            listPlayer.addAll(playerService.getList());
        }
        nbPlayer = listPlayer.size();
    }

    private void feedTournament() {
        listTournament.clear();
        listTournament.addAll(tournamentService.getList());
        if (listTournament.isEmpty()) {
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
        nbTournament = listTournament.size();
    }

    private void feedClub() {
        listClub.clear();
        listClub.addAll(clubService.getList());
        if (listClub.isEmpty()) {
            for (int i = 0; i < 10; i++) {
                int index = rnd.nextInt(nbAddress);
                Club club = new Club();
                club.setName("Club " + i);
                club.setSubId(listAddress.get(index > 0 ? index - 1 : index).getId());
                clubService.createOrUpdate(club);
                listClub.add(club);
            }
        }
        nbClub = listClub.size();
    }

    private void feedAddress() {
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
        nbAddress = listAddress.size();
    }

    private User createUser() {
        User ret  = new User();
        ret.setBirthday("01/02/2003");
        ret.setFirstName("Current");
        ret.setLastName("User");
        ret.setPhonenumber("0123456789");
        ret.setAddress("1 rue de la rivière");
        ret.setLocality("Ville");
        ret.setPostalCode("01234");
        ret.setIdSaison(saison.getId());
        ret.setIdRanking(ranking.getId());
        return ret;
    }

    private Invite createInvite(Player player, TypeManager.TYPE type) {
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

        if (playerService.getUnknownPlayer().equals(player)) {
            invite.setIdRanking(listRanking.get(rnd.nextInt(nbRanking)).getId());
            invite.setIdRankingEstimate(listRanking.get(rnd.nextInt(nbRanking)).getId());
        }
        return invite;
    }

    private static void logMe(String msg) {
        com.crashlytics.android.Crashlytics.log(msg);
        Logger.logMe(TAG, msg);
    }
}