package com.justtennis;

import android.app.Instrumentation;
import android.app.UiAutomation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.justtennis.activity.ComputeRankingActivity;
import com.justtennis.activity.GenericSpinnerFormActivity;
import com.justtennis.activity.InviteActivity;
import com.justtennis.ui.common.CommonEnum;
import com.justtennis.activity.ListCompetitionActivity;
import com.justtennis.activity.ListInviteActivity;
import com.justtennis.activity.ListPersonActivity;
import com.justtennis.activity.ListPlayerActivity;
import com.justtennis.activity.LocationAddressActivity;
import com.justtennis.activity.LocationClubActivity;
import com.justtennis.activity.LocationTournamentActivity;
import com.justtennis.activity.MainActivity;
import com.justtennis.activity.MatchActivity;
import com.justtennis.activity.MessageActivity;
import com.justtennis.activity.PalmaresFastActivity;
import com.justtennis.activity.PieChartActivity;
import com.justtennis.activity.PlayerActivity;
import com.justtennis.activity.QRCodeActivity;
import com.justtennis.activity.RotatingButtons;
import com.justtennis.activity.ScoreActivity;
import com.justtennis.activity.UserActivity;
import com.justtennis.business.PalmaresFastBusiness;
import com.justtennis.domain.PalmaresFastValue;
import com.justtennis.manager.TypeManager;
import com.justtennis.tool.DBFeedTool;
import com.justtennis.ui.activity.ItemDetailActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class ScreenshotAllActivitiesTest {

    // Chaque ecran a sa propre methode de test : un "am instrument" separe est lance
    // par ecran, donc un crash sur l'un d'eux n'empeche pas de capturer les autres.

    // Repasse Training/Match : "-e mode match" (argument d'instrumentation) bascule
    // TypeManager.TYPE en COMPETITION avant chaque test, et suffixe les captures en "_Match".
    private boolean isMatchMode() {
        return "match".equalsIgnoreCase(InstrumentationRegistry.getArguments().getString("mode", "training"));
    }

    @Before
    public void setUpTypeManagerMode() {
        TypeManager.getInstance().setType(isMatchMode() ? TypeManager.TYPE.COMPETITION : TypeManager.TYPE.TRAINING);
        if (isMatchMode()) {
            ensureCompetitionSeed(InstrumentationRegistry.getTargetContext());
        }
    }

    // Bug reel trouve dans DBFeedTool.feedPlayer()/feedInvite() : la garde
    // "if (xxxService.getCount() <= 0)" est un compteur GLOBAL (filtre implicitement par le TYPE
    // courant de TypeManager, jamais change entre les 2 passes TRAINING/COMPETITION de la boucle
    // for (TYPE type : TYPE.values())) - resultat, seul le PREMIER type traite (TRAINING) cree
    // reellement des joueurs/invites ; le second (COMPETITION) voit "count > 0" (les joueurs
    // Training) et ne cree jamais rien. La base ne contient donc aucune donnee Competition.
    // On reproduit ici la meme logique de seed que DBFeedTool, mais correctement isolee par type
    // (TypeManager.setType(COMPETITION) est deja actif a cet instant, donc getCount()/getList()
    // filtrent correctement sur ce type).
    private void ensureCompetitionSeed(Context context) {
        com.justtennis.notifier.NotifierMessageLogger notifier = com.justtennis.notifier.NotifierMessageLogger.getInstance();
        com.justtennis.db.service.PlayerService playerService = new com.justtennis.db.service.PlayerService(context, notifier);
        com.justtennis.db.service.InviteService inviteService = new com.justtennis.db.service.InviteService(context, notifier);
        com.justtennis.db.service.ScoreSetService scoreSetService = new com.justtennis.db.service.ScoreSetService(context, notifier);
        com.justtennis.db.service.RankingService rankingService = new com.justtennis.db.service.RankingService(context, notifier);
        com.justtennis.db.service.ClubService clubService = new com.justtennis.db.service.ClubService(context, notifier);
        com.justtennis.db.service.UserService userService = new com.justtennis.db.service.UserService(context, notifier);
        com.justtennis.db.service.SaisonService saisonService = new com.justtennis.db.service.SaisonService(context, notifier);

        List<com.justtennis.domain.Ranking> listRanking = rankingService.getList();
        List<com.justtennis.domain.Club> listClub = clubService.getList();
        com.justtennis.domain.User user = userService.find();
        com.justtennis.domain.Saison saison = saisonService.getSaisonActiveOrFirst();
        if (listRanking.isEmpty() || listClub.isEmpty() || user == null) {
            return;
        }

        java.util.Random rnd = new java.util.Random(2);

        // Joueurs Competition : guard independant (deja crees lors d'un run precedent -> skip).
        List<com.justtennis.domain.Player> created;
        if (playerService.getCount() > 0) {
            created = playerService.getList();
        } else {
            created = new java.util.ArrayList<>();
            for (int i = 0; i < 10; i++) {
                com.justtennis.domain.Player player = new com.justtennis.domain.Player();
                player.setFirstName("PlayerC " + i);
                player.setLastName("Name" + i);
                player.setType(TypeManager.TYPE.COMPETITION);
                player.setIdClub(listClub.get(rnd.nextInt(listClub.size())).getId());
                player.setIdRanking(listRanking.get(rnd.nextInt(listRanking.size())).getId());
                if (i % 2 == 0) {
                    player.setIdRankingEstimate(listRanking.get(rnd.nextInt(listRanking.size())).getId());
                }
                playerService.createOrUpdate(player);
                created.add(player);
            }
        }
        if (created.isEmpty()) {
            return;
        }

        // Invites Competition : guard SEPARE (ID_SAISON doit correspondre exactement, cf.
        // DBInviteDataSource.customizeWhereSaison - pas de repli "OR IS NULL" comme pour Player).
        if (!inviteService.getList().isEmpty()) {
            return;
        }

        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.HOUR_OF_DAY, -1);
        java.util.Date pastDate = cal.getTime();

        for (int i = 0; i < 10; i++) {
            com.justtennis.domain.Player player = created.get(rnd.nextInt(created.size()));
            com.justtennis.domain.Invite invite = new com.justtennis.domain.Invite();
            invite.setUser(user);
            invite.setPlayer(player);
            invite.setType(TypeManager.TYPE.COMPETITION);
            invite.setSaison(saison);
            invite.setDate(pastDate);
            invite.setClub(listClub.get(rnd.nextInt(listClub.size())));
            invite.setIdRanking(player.getIdRanking());
            invite.setIdRankingEstimate(player.getIdRankingEstimate() != null ? player.getIdRankingEstimate() : player.getIdRanking());
            inviteService.createOrUpdate(invite);

            if (rnd.nextInt(100) % 3 != 0) {
                List<com.justtennis.domain.ScoreSet> score = new java.util.ArrayList<>();
                int nbSet = 1 + rnd.nextInt(2);
                for (int s = 0; s < nbSet; s++) {
                    int iScore = rnd.nextInt(100);
                    int[] val = (iScore % 2 == 0) ? new int[]{6, rnd.nextInt(5)} : new int[]{rnd.nextInt(5), 6};
                    com.justtennis.domain.ScoreSet scoreSet = new com.justtennis.domain.ScoreSet(invite, s + 1, val[0], val[1]);
                    scoreSetService.createOrUpdate(scoreSet);
                    score.add(scoreSet);
                }
                invite.setListScoreSet(score);
                invite.setScoreResult(com.justtennis.db.service.ScoreSetService.getInviteScoreResult(score));
                inviteService.createOrUpdate(invite);
            }
        }
    }

    @Test public void screen01_ItemDetailActivity() { shoot(ItemDetailActivity.class); }
    @Test public void screen02_MainActivity() { shoot(MainActivity.class); }
    @Test public void screen03_UserActivity() { shoot(UserActivity.class); }
    @Test public void screen04_ListPlayerActivity() { shoot(ListPlayerActivity.class); }
    @Test public void screen05_ListPersonActivity() { shoot(ListPersonActivity.class); }
    @Test public void screen06_ListInviteActivity() { shoot(ListInviteActivity.class); }
    @Test public void screen07_ListCompetitionActivity() { shoot(ListCompetitionActivity.class); }
    @Test public void screen08_ComputeRankingActivity() { shoot(ComputeRankingActivity.class); }
    @Test public void screen09_PalmaresFastActivity() { shoot(PalmaresFastActivity.class); }
    @Test public void screen10_PlayerActivity() { shoot(PlayerActivity.class); }
    @Test public void screen11_MessageActivity() { shoot(MessageActivity.class); }
    @Test public void screen12_QRCodeActivity() { shoot(QRCodeActivity.class); }
    @Test public void screen13_InviteActivity() { shoot(InviteActivity.class); }
    @Test public void screen14_ScoreActivity() { shoot(ScoreActivity.class); }
    @Test public void screen15_LocationTournamentActivity() { shoot(LocationTournamentActivity.class); }
    @Test public void screen16_LocationClubActivity() { shoot(LocationClubActivity.class); }
    @Test public void screen17_LocationAddressActivity() { shoot(LocationAddressActivity.class); }

    // Ces 3 ecrans demarrent par defaut sur le selecteur (spinner + bouton "+") ; le vrai
    // formulaire de saisie (nom, etc.) n'apparait qu'en mode ADD (normalement declenche par un
    // clic sur "+"). GenericSpinnerFormActivity accepte cet etat directement via un extra d'Intent.
    @Test public void screen15b_LocationTournamentActivity_Add() { shootAdd(LocationTournamentActivity.class); }
    @Test public void screen16b_LocationClubActivity_Add() { shootAdd(LocationClubActivity.class); }
    @Test public void screen17b_LocationAddressActivity_Add() { shootAdd(LocationAddressActivity.class); }
    @Test public void screen18_RotatingButtons() { shoot(RotatingButtons.class); }
    @Test public void screen19_PieChartActivity() { shoot(PieChartActivity.class); }
    @Test public void screen20_MatchActivity() { shoot(MatchActivity.class); }

    // PlayerActivity a 3 barres de boutons differentes selon le mode (CommonEnum.PLAYER_MODE) :
    // CREATE (deja capture en screen10), MODIFY (boutons Modifier/QRCode/Supprimer) et
    // DEMANDE_ADD (confirmation Oui/Non d'une demande d'ajout recue par SMS).
    @Test public void screen10b_PlayerActivity_Modify() {
        shoot(PlayerActivity.class, "PlayerActivity_Modify",
                intent -> intent.putExtra(PlayerActivity.EXTRA_MODE, CommonEnum.PLAYER_MODE.MODIFY));
    }
    // PLAYER_MODE.DEMANDE_ADD n'est jamais utilise en production : seul SmsReceiver.java
    // (recepteur SMS desactive, cf. CLAUDE.md §4.4/§11.4) le declenche. Pas de capture pertinente
    // possible pour un ecran de code mort.

    // InviteActivity plantait (NPE) car intent.getExtras() est null sans le moindre extra.
    // Sans joueur associe a l'invite, initializeRankingList() plante aussi (NPE reel, non lie a
    // notre methode de capture) : on fournit donc l'ID sentinelle "Unknown Player" (-1), exactement
    // ce que fait l'app en navigation normale pour un match sans adversaire connu (cf. CLAUDE.md).
    @Test public void screen13b_InviteActivity_Create() {
        shoot(InviteActivity.class, "InviteActivity_Create", intent -> {
            intent.putExtra(InviteActivity.EXTRA_MODE, CommonEnum.INVITE_MODE.INVITE_SIMPLE);
            intent.putExtra(InviteActivity.EXTRA_PLAYER_ID, -1L);
        });
    }

    // TypeManager (singleton statique) pilote le theme visuel Entrainement (bleu, defaut) vs
    // Competition (orange) - bascule globale accessible depuis le drawer sur la plupart des
    // ecrans legacy (cf. PROMPT_FONCTIONNEL.md §4 "identite visuelle contextuelle").
    @Test public void screen02b_MainActivity_ThemeCompetition() {
        TypeManager.getInstance().setType(TypeManager.TYPE.COMPETITION);
        shoot(MainActivity.class, "MainActivity_ThemeCompetition", null);
    }

    // ItemDetailActivity (ecran moderne) affiche un onboarding tant qu'aucun User n'existe en
    // base - les 3 onglets (Joueurs/Invitations/Statistiques) sont donc invisibles sur une base
    // vide. DBFeedTool.doFeed() est le mecanisme de seed de donnees de test deja integre a l'app
    // (habituellement gate par ApplicationConfig.FEED_DATABASE=false) : on l'appelle directement
    // pour creer un User + des Players/Invites de demonstration, puis on bascule sur chaque onglet
    // via le BottomNavigationView (equivalent programmatique d'un tap).
    @Test public void screen01b_ItemDetailActivity_Tabs() throws Throwable {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        Context targetContext = InstrumentationRegistry.getTargetContext();

        DBFeedTool.getInstance(targetContext).doFeed();

        Intent intent = new Intent(targetContext, ItemDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        android.app.Activity activity = instrumentation.startActivitySync(intent);
        instrumentation.waitForIdleSync();
        Thread.sleep(1500);
        closeDrawer(instrumentation, activity);
        takeScreenshot(instrumentation, "ItemDetailActivity_Tab_Invite");

        int[] tabIds = new int[]{
                com.justtennis.R.id.navigation_player,
                com.justtennis.R.id.navigation_statistic,
        };
        String[] tabNames = new String[]{
                "ItemDetailActivity_Tab_Player",
                "ItemDetailActivity_Tab_Statistic",
        };
        for (int i = 0; i < tabIds.length; i++) {
            int tabId = tabIds[i];
            instrumentation.runOnMainSync(() -> {
                android.support.design.widget.BottomNavigationView nav =
                        activity.findViewById(com.justtennis.R.id.navigationView);
                if (nav != null) {
                    nav.setSelectedItemId(tabId);
                }
            });
            instrumentation.waitForIdleSync();
            Thread.sleep(1200);
            closeDrawer(instrumentation, activity);
            if (tabId == com.justtennis.R.id.navigation_statistic) {
                forcePieChartRender(instrumentation, activity);
            }
            takeScreenshot(instrumentation, tabNames[i]);
        }
        activity.finish();
    }

    // Diagnostic (garde a titre de reference) : les invites generees par DBFeedTool pour un
    // adversaire connu ne recoivent JAMAIS de idRanking a la creation (seul le cas "Unknown
    // Player" en recoit un). En usage reel, ce champ n'est backfill qu'a l'ouverture individuelle
    // de l'invite dans InviteActivity. Resultat : le graphique groupe par classement
    // (PieChartBusiness/GROUP BY ID_RANKING) n'a quasiment aucune donnee exploitable sur des
    // invites "fraiches", d'ou l'unique tranche "15/5" vue precedemment.
    //
    // Pour une capture honnete montrant une vraie variete de types/resultats, on reproduit ici le
    // backfill que ferait normalement l'app (idRanking = classement reel ou estime du partenaire)
    // sur toutes les invites deja seedees, puis on force une date passee pour qu'elles soient
    // bien comptees (le filtre SQL exclut les invites futures).
    @Test public void screen01d_ItemDetailActivity_Statistic_Rich() throws Throwable {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        Context targetContext = InstrumentationRegistry.getTargetContext();
        DBFeedTool.getInstance(targetContext).doFeed();

        com.justtennis.notifier.NotifierMessageLogger notifier = com.justtennis.notifier.NotifierMessageLogger.getInstance();
        com.justtennis.db.service.InviteService inviteService = new com.justtennis.db.service.InviteService(targetContext, notifier);
        com.justtennis.db.service.PlayerService playerService = new com.justtennis.db.service.PlayerService(targetContext, notifier);

        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DAY_OF_YEAR, -1);
        java.util.Date pastDate = cal.getTime();

        for (com.justtennis.domain.Invite invite : inviteService.getList()) {
            if (invite.getIdRanking() == null && invite.getPlayer() != null && invite.getPlayer().getId() != null) {
                com.justtennis.domain.Player player = playerService.find(invite.getPlayer().getId());
                if (player != null) {
                    Long idRanking = player.getIdRanking() != null ? player.getIdRanking() : player.getIdRankingEstimate();
                    if (idRanking != null) {
                        invite.setIdRanking(idRanking);
                        invite.setIdRankingEstimate(player.getIdRankingEstimate() != null ? player.getIdRankingEstimate() : idRanking);
                    }
                }
            }
            invite.setDate(pastDate);
            inviteService.createOrUpdate(invite);
        }

        Intent intent = new Intent(targetContext, ItemDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        android.app.Activity activity = instrumentation.startActivitySync(intent);
        instrumentation.waitForIdleSync();
        Thread.sleep(1500);
        instrumentation.runOnMainSync(() -> {
            android.support.design.widget.BottomNavigationView nav =
                    activity.findViewById(com.justtennis.R.id.navigationView);
            if (nav != null) {
                nav.setSelectedItemId(com.justtennis.R.id.navigation_statistic);
            }
        });
        instrumentation.waitForIdleSync();
        Thread.sleep(1000);
        closeDrawer(instrumentation, activity);

        instrumentation.runOnMainSync(() -> {
            try {
                android.support.v4.app.FragmentActivity fragmentActivity = (android.support.v4.app.FragmentActivity) activity;
                android.support.v4.app.Fragment fragment = fragmentActivity.getSupportFragmentManager()
                        .findFragmentById(com.justtennis.R.id.item_detail_container);

                Field businessField = fragment.getClass().getDeclaredField("business");
                businessField.setAccessible(true);
                com.justtennis.business.PieChartBusiness business =
                        (com.justtennis.business.PieChartBusiness) businessField.get(fragment);
                java.util.HashMap<String, Double> data = business.getData(com.justtennis.business.PieChartBusiness.CHART_DATA_TYPE.ALL);
                android.util.Log.e("ScreenshotDiag", "rich data=" + data);

                // Pas d'invalidate()/requestLayout() supplementaire ici : addValue() appelle deja
                // mChartView.repaint() en interne, suffisant dans ce conteneur (contrairement a
                // PieChartActivity standalone, casse par un bug de ScrollView). Un invalidate en
                // plus produit un rendu "double expose" (chevauchement de deux dessins).
                Method addValue = fragment.getClass().getDeclaredMethod("addValue", String.class, double.class);
                addValue.setAccessible(true);
                for (String name : data.keySet()) {
                    Double value = data.get(name);
                    if (value != null) {
                        addValue.invoke(fragment, name, value);
                    }
                }
            } catch (Exception e) {
                android.util.Log.e("ScreenshotDiag", "error", e);
            }
        });
        instrumentation.waitForIdleSync();
        Thread.sleep(800);
        takeScreenshot(instrumentation, "ItemDetailActivity_Tab_Statistic_Rich");
        activity.finish();
    }

    // PieChartFragment.getData(ALL) attend le callback initial du Spinner (position 0), qui ne
    // se declenche pas toujours a temps sur nos captures automatisees. On force explicitement un
    // changement de selection (0 -> 1 -> 0) sur les 2 spinners pour garantir que le graphique se
    // peuple avec les vraies donnees (toutes categories confondues).
    private void forcePieChartRender(Instrumentation instrumentation, android.app.Activity activity) {
        instrumentation.runOnMainSync(() -> {
            android.widget.Spinner spType = activity.findViewById(com.justtennis.R.id.sp_type_data);
            android.widget.Spinner spResult = activity.findViewById(com.justtennis.R.id.sp_score_result_data);
            if (spType != null && spType.getAdapter() != null && spType.getAdapter().getCount() > 1) {
                spType.setSelection(1, false);
                spType.setSelection(0, false);
            }
            if (spResult != null && spResult.getAdapter() != null && spResult.getAdapter().getCount() > 1) {
                spResult.setSelection(1, false);
                spResult.setSelection(0, false);
            }
        });
        instrumentation.waitForIdleSync();
        try {
            Thread.sleep(800);
        } catch (InterruptedException ignored) {
        }
    }

    // PieChartActivity (legacy, standalone) heberge le meme PieChartFragment. Piloter les
    // Spinners (comme pour l'onglet Statistiques) s'est avere peu fiable ici (callback initial
    // du Spinner qui ne se declenche pas de façon deterministe) : on remplit directement le
    // graphique par reflexion sur les champs prives du fragment (business/mSeries/mRenderer/
    // mChartView + methode privee addValue), comme pour PalmaresFastActivity_WithData.
    @Test public void screen19b_PieChartActivity_WithData() throws Throwable {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        Context targetContext = InstrumentationRegistry.getTargetContext();
        Intent intent = new Intent(targetContext, PieChartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        android.app.Activity activity = instrumentation.startActivitySync(intent);
        instrumentation.waitForIdleSync();
        Thread.sleep(1200);

        instrumentation.runOnMainSync(() -> {
            try {
                android.support.v4.app.FragmentActivity fragmentActivity = (android.support.v4.app.FragmentActivity) activity;
                android.support.v4.app.Fragment fragment = fragmentActivity.getSupportFragmentManager()
                        .findFragmentById(com.justtennis.R.id.main_container);

                Field businessField = fragment.getClass().getDeclaredField("business");
                businessField.setAccessible(true);
                com.justtennis.business.PieChartBusiness business =
                        (com.justtennis.business.PieChartBusiness) businessField.get(fragment);

                java.util.HashMap<String, Double> data = business.getData(com.justtennis.business.PieChartBusiness.CHART_DATA_TYPE.ALL);

                Method addValue = fragment.getClass().getDeclaredMethod("addValue", String.class, double.class);
                addValue.setAccessible(true);
                for (String name : data.keySet()) {
                    Double value = data.get(name);
                    if (value != null) {
                        addValue.invoke(fragment, name, value);
                    }
                }

                // GraphicalView.repaint() (appele par addValue) ne suffit pas a forcer un
                // redessin quand on injecte les donnees hors du pipeline normal (Spinner ->
                // onItemSelected) : on force explicitement un invalidate() Android standard.
                Field chartViewField = fragment.getClass().getDeclaredField("mChartView");
                chartViewField.setAccessible(true);
                View chartView = (View) chartViewField.get(fragment);
                if (chartView != null) {
                    chartView.invalidate();
                    chartView.requestLayout();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        instrumentation.waitForIdleSync();
        Thread.sleep(800);
        takeScreenshot(instrumentation, "PieChartActivity_WithData");
        activity.finish();
    }

    // PalmaresFastActivity est un simulateur a saisie 100% manuelle (pas de lecture DB) : on
    // injecte quelques valeurs via reflexion sur le champ prive "business" de PalmaresFastFragment
    // puis on appelle refreshData() (methode publique) pour rafraichir l'affichage, exactement
    // comme le ferait la saisie utilisateur dans les EditText de la liste.
    @Test public void screen09b_PalmaresFastActivity_WithData() throws Throwable {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        Context targetContext = InstrumentationRegistry.getTargetContext();
        Intent intent = new Intent(targetContext, PalmaresFastActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        android.app.Activity activity = instrumentation.startActivitySync(intent);
        instrumentation.waitForIdleSync();
        Thread.sleep(1000);

        instrumentation.runOnMainSync(() -> {
            try {
                android.support.v4.app.FragmentActivity fragmentActivity = (android.support.v4.app.FragmentActivity) activity;
                android.support.v4.app.Fragment fragment = fragmentActivity.getSupportFragmentManager()
                        .findFragmentById(com.justtennis.R.id.main_container);
                Field businessField = fragment.getClass().getDeclaredField("business");
                businessField.setAccessible(true);
                PalmaresFastBusiness business = (PalmaresFastBusiness) businessField.get(fragment);
                List<PalmaresFastValue> list = business.getList();
                int n = Math.min(4, list.size());
                for (int i = 0; i < n; i++) {
                    list.get(i).setNbVictory(3 + i);
                    list.get(i).setNbDefeat(i);
                }
                Method refreshData = fragment.getClass().getMethod("refreshData");
                refreshData.invoke(fragment);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        instrumentation.waitForIdleSync();
        Thread.sleep(800);
        takeScreenshot(instrumentation, "PalmaresFastActivity_WithData");
        activity.finish();
    }

    // Le drawer de navigation s'ouvre automatiquement au premier affichage (aide visuelle) et
    // masque le contenu de chaque onglet sur la capture.
    private void closeDrawer(Instrumentation instrumentation, android.app.Activity activity) {
        instrumentation.runOnMainSync(() -> {
            android.support.v4.widget.DrawerLayout drawer = activity.findViewById(com.justtennis.R.id.drawer_layout);
            if (drawer != null) {
                drawer.closeDrawers();
            }
        });
        instrumentation.waitForIdleSync();
        try {
            Thread.sleep(600);
        } catch (InterruptedException ignored) {
        }
    }

    // Plusieurs ecrans a base de DrawerManager (recherche live) donnent le focus clavier a un
    // champ de recherche des l'ouverture, cachant une partie du contenu sur la capture. On masque
    // le clavier via InputMethodManager (sans jamais naviguer/quitter l'ecran, contrairement a un
    // BACK qui serait risque sur les ecrans sans clavier ouvert).
    private void hideKeyboard(Instrumentation instrumentation, android.app.Activity activity) {
        instrumentation.runOnMainSync(() -> {
            View focused = activity.getCurrentFocus();
            if (focused != null) {
                android.view.inputmethod.InputMethodManager imm =
                        (android.view.inputmethod.InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(focused.getWindowToken(), 0);
                }
                focused.clearFocus();
            }
        });
        instrumentation.waitForIdleSync();
        try {
            Thread.sleep(400);
        } catch (InterruptedException ignored) {
        }
    }

    private interface IntentCustomizer {
        void customize(Intent intent);
    }

    private void shoot(Class<?> activityClass) {
        shoot(activityClass, activityClass.getSimpleName(), null);
    }

    private void shootAdd(Class<?> activityClass) {
        shoot(activityClass, activityClass.getSimpleName() + "_Add",
                intent -> intent.putExtra(GenericSpinnerFormActivity.EXTRA_MODE, GenericSpinnerFormActivity.MODE.ADD));
    }

    private void shoot(Class<?> activityClass, String screenshotName, IntentCustomizer customizer) {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        Context targetContext = InstrumentationRegistry.getTargetContext();
        android.app.Activity activity = null;
        try {
            Intent intent = new Intent(targetContext, activityClass);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (customizer != null) {
                customizer.customize(intent);
            }
            activity = instrumentation.startActivitySync(intent);
            instrumentation.waitForIdleSync();
            Thread.sleep(1200);
            hideKeyboard(instrumentation, activity);
            closeDrawer(instrumentation, activity);
        } catch (Throwable ignored) {
            // On capture quand meme ce qui est affiche a l'ecran.
        }
        takeScreenshot(instrumentation, screenshotName);
        if (activity != null) {
            try {
                activity.finish();
            } catch (Throwable ignored) {
            }
        }
    }

    private void takeScreenshot(Instrumentation instrumentation, String name) {
        try {
            UiAutomation uiAutomation = instrumentation.getUiAutomation();
            Bitmap bitmap = uiAutomation.takeScreenshot();
            if (bitmap == null) {
                return;
            }
            String finalName = isMatchMode() ? name + "_Match" : name;
            Context targetContext = InstrumentationRegistry.getTargetContext();
            java.io.File dir = new java.io.File(targetContext.getExternalFilesDir(null), "screenshots");
            dir.mkdirs();
            java.io.File file = new java.io.File(dir, finalName + ".png");
            try (OutputStream out = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            }
            bitmap.recycle();
        } catch (Throwable ignored) {
        }
    }
}
