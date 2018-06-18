package com.justtennis.tool;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.justtennis.ApplicationConfig;
import com.justtennis.db.service.InviteService;
import com.justtennis.db.service.PlayerService;
import com.justtennis.db.service.SaisonService;
import com.justtennis.db.service.UserService;
import com.justtennis.db.sqlite.helper.DBSaisonHelper;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Player;
import com.justtennis.domain.Saison;
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

        if (!ApplicationConfig.FEED_DATABASE) {
            return;
        }

        SaisonService saisonService = new SaisonService(context, NotifierMessageLogger.getInstance());
        UserService userService = new UserService(context, NotifierMessageLogger.getInstance());
        PlayerService playerService = new PlayerService(context, NotifierMessageLogger.getInstance());
        InviteService inviteService = new InviteService(context, NotifierMessageLogger.getInstance());

        Saison saison = saisonService.getSaisonActiveOrFirst();
        if (saison == null) {
            saison = SaisonService.build(Calendar.getInstance());
            saisonService.createOrUpdate(saison);
        }
        User user = userService.findFirst();
        if (user == null) {
            user = createUser(saison);
            userService.createOrUpdate(user);
        }
        List<Player> listPlayer;
        if (playerService.getCount() <= 0) {
            listPlayer = new ArrayList<>();
            listPlayer.add(playerService.getUnknownPlayer());
            for (int i=0 ; i<10 ; i++) {
                Player player = new Player();
                player.setFirstName("Player " + i);
                player.setLastName("Name" + i);
                player.setType(TypeManager.getInstance().getType());
                playerService.createOrUpdate(player);
                listPlayer.add(player);
            }
        } else {
            listPlayer = playerService.getList();
        }
        int nbPlayer = listPlayer.size();
        Random rnd = new Random();
        if (inviteService.getCount() < 10) {
            for(int i=0 ; i<10 ; i++) {
                Player player = listPlayer.get(rnd.nextInt(nbPlayer - 1));
                Invite invite = createInvite(saison, user, player);
                inviteService.createOrUpdate(invite);
            }
/*
        } else {
            for(Invite invite : inviteService.getList()) {
                inviteService.delete(invite);
            }
*/
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

    private static Invite createInvite(Saison saison, User user, Player player) {
        Invite invite = new Invite();
        invite.setSaison(saison);
        invite.setUser(user);
        invite.setType(TypeManager.getInstance().getType());
        invite.setPlayer(player);

        Calendar calendar = GregorianCalendar.getInstance(ApplicationConfig.getLocal());
        calendar.setTime(new Date());
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        invite.setDate(calendar.getTime());

        return invite;
    }
    public static void feedold(SQLiteDatabase database) {
        Saison[] rows = new Saison[]{
                feedBuild()
        };

        try {
            database.beginTransaction();
            database.delete(DBSaisonHelper.TABLE_NAME, null, null);
            for (int row = 0; row < rows.length; row++) {
                Saison saison = rows[row];
                logMe("insert row:" + saison);
                ContentValues values = new ContentValues();
                values.put(DBSaisonHelper.COLUMN_NAME, saison.getName());
                values.put(DBSaisonHelper.COLUMN_BEGIN, saison.getBegin().getTime());
                values.put(DBSaisonHelper.COLUMN_END, saison.getEnd().getTime());
                values.put(DBSaisonHelper.COLUMN_ACTIVE, saison.isActive() ? 1 : 0);

                long id = database.insert(DBSaisonHelper.TABLE_NAME, null, values);
                saison.setId(id);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public static Saison feedBuild() {
        return SaisonService.build(Calendar.getInstance());
    }

    private static void logMe(String msg) {
        Logger.logMe(TAG, msg);
    }
}