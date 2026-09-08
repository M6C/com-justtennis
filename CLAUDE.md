# JustTennis — Documentation de référence (app legacy) pour réécriture

> Ce document résulte d'une analyse exhaustive du code source legacy de **JustTennis** (`com.justtennis`), une application Android de gestion de club de tennis amateur (française), écrite entre 2013 et 2019. Il sert de **référence factuelle unique** pour une réécriture complète de l'application dans une stack Android moderne (Kotlin, Jetpack Compose, Room, architecture recommandée par Google), en préservant **tout le comportement métier** existant.
>
> Toutes les règles, formules, valeurs numériques et noms de classes cités ici proviennent d'une lecture directe du code source réel (317 fichiers Java, 343 commits Git). Les zones d'ombre du code (comportements ambigus, bugs historiques, code mort) sont signalées explicitement — **ne jamais deviner ni "corriger" silencieusement une règle métier non documentée ici sans validation produit préalable.**

---

## 1. Vue d'ensemble fonctionnelle

JustTennis permet à un joueur de tennis amateur de :
- gérer une fiche **Joueur/Adversaire** (`Player`) et sa propre fiche **Utilisateur** (`User`), avec import possible depuis les contacts Android ou par **échange QR Code** entre deux téléphones ;
- proposer et enregistrer des **matchs/entraînements** (`Invite`, type `COMPETITION` ou `TRAINING`) avec un adversaire, une date, un lieu (club/adresse/tournoi) ;
- saisir le **score** du match (jusqu'à 5 sets) ;
- calculer un **classement custom du club** (barème inspiré du classement FFT) à partir de l'historique des victoires/défaites, avec un système de points par écart de classement, de bonus, et un système de pénalité de défaites ("V-E-2I-5G") ;
- **simuler** un classement hypothétique (écran "Calcul Classement" / "Palmarès rapide") ;
- synchroniser chaque match avec le **calendrier Google** de l'appareil (création/modification/suppression d'événements) ;
- consulter des **statistiques** (palmarès, camembert de répartition des invitations) ;
- **sauvegarder/restaurer/partager** la base de données SQLite via le stockage externe.

Un canal **SMS** propriétaire (chiffrement maison, protocole `JUST_TENNIS|...`) existait pour échanger automatiquement demandes d'ajout de joueur et invitations entre deux installations de l'app, mais **il est désactivé dans le build actuel** (permissions et `<receiver>` commentés dans le manifest) — voir §11.4.

---

## 2. Architecture UI — un seul cœur métier, deux présentations

Il existe **deux styles de présentation UI** dans le code, mais **une seule couche métier/données**, partagée :

1. **UI "legacy"** (package `com.justtennis.activity.*` + `com.justtennis.business.*`) : Activities classiques en layout XML plein écran, avec une classe `XxxBusiness` par écran assurant la logique de présentation et l'accès aux `Service`/DataSource. C'est la présentation historique, encore l'écran d'accueil accessible (`MainActivity`).
2. **UI "modernisée partielle"** (package `com.justtennis.ui.*`) : `ItemDetailActivity` est **l'activité `LAUNCHER` réelle** de l'app (déclarée dans le manifest, pleinement fonctionnelle — ce n'est PAS le template Android Studio mort qu'on pourrait croire à première vue). Elle héberge une `BottomNavigationView` (Joueurs / Invitations / Statistiques) et un `NavigationDrawerFragment`, pilotés par RxJava (`RxFragment`, `RxNavigationDrawer`). Plusieurs Activities legacy (`UserActivity`, `PlayerActivity`, `ListPlayerActivity`, `InviteActivity`, `PalmaresFastActivity`, `PieChartActivity`) sont en réalité de simples coquilles (`AbsctractFragmentActivity`) hébergeant un Fragment `ui.fragment.*` qui instancie directement le même `XxxBusiness` que l'UI legacy. **Il n'y a donc qu'une seule couche métier**, réutilisée par les deux présentations.

Un menu explicite (`action_old_ihm` dans `ItemDetailActivity`) relance l'arborescence 100% legacy via `MainActivity`.

### Code mort confirmé (ne pas reporter) dans la couche UI
- `ItemListActivity`, `UiMainActivity`, `ui.fragment.placeholder.*`, `ui.activity.dummy.DummyContent` : commentés dans le manifest, aucun appelant — reliquat du template Android Studio "Master/Detail Flow".
- `InviteDemandeActivity` / `InviteDemandeBusiness`, `LocationActivity` / `LocationBusiness` : non déclarées dans le manifest, remplacées respectivement par `InviteFragment`/`InviteBusiness` et par `GenericSpinnerFormActivity` + `LocationClubBusiness`/`LocationAddressBusiness`/`LocationTournamentBusiness`.
- `PieChartActivity2` (doublon achartengine de `PieChartActivity`), `ui.fragment.ListPlayerFragment2` (variante orpheline de `ListPlayerFragment`).
- Package `org.gdocument.gtracergps.*` (lib GPS tracker tierce copiée-collée) : seules `GpsConstant`, `launcher.log.Logger`, `launcher.util.{FileUtil,GpsUtil,NumberUtil}` sont détournées comme utilitaires génériques ailleurs dans le code ; `launcher.domain.{Distance,DistanceTotal,Localisation,Session}` et `launcher.util.HTMLEncoder` sont **entièrement morts**.
- `receiver/SmsReceiver.java` : `<receiver>` commenté dans le manifest → jamais déclenché.
- Menu `action_send_apk` / `action_send_db` : passés à `visible="false"` pour contourner un crash (`Uri.fromFile` cassé sur Android 7+), code toujours présent mais inatteignable.

---

## 3. Inventaire des écrans

| Écran (Activity) | Business associée | Rôle |
|---|---|---|
| `ItemDetailActivity` (LAUNCHER) | `MainBusiness` | Accueil moderne : onboarding (création `User` puis message SMS si 1er lancement), bottom nav Joueurs/Invitations/Stats, drawer saison/type |
| `MainActivity` | `MainBusiness` | Accueil legacy (menu complet) : scan QR, fiche user, listes, palmarès rapide, stats, messages, import contacts, partage APK/DB, backup/restore DB, réception de fichiers `.db` externes |
| `UserActivity` (= `PlayerActivity` + `UserFragment`) | `UserBusiness extends PlayerBusiness` | Fiche de l'utilisateur courant (table `USER`, entité unique) |
| `PlayerActivity` (+ `PlayerFragment`) | `PlayerBusiness` | Création/édition d'un joueur adverse : identité, classement réel+estimé, club/tournoi, import (scan QR ou contact Google), 3 modes (`FOR_RESULT`/`CREATE`/`MODIFY`/`DEMANDE_ADD`) |
| `ListPlayerActivity` (+ `ListPlayerFragment`) | `ListPlayerBusiness` | Liste des joueurs, recherche via drawer, suppression (bloquée si le joueur a des invites), mode `EDIT` vs `INVITE` |
| `ListPersonActivity` | `ListPersonBusiness` | Liste des contacts Android (`READ_CONTACTS`), recherche texte, sélection → conversion en `Player` |
| `ListInviteActivity` | `ListInviteBusiness` | Liste des invites (mode Entraînement), tri par date desc, filtre par joueur/type, suppression (+ event Calendar associé) |
| `InviteActivity` (+ `InviteFragment`) | `InviteBusiness` | Cycle complet d'une invite : création/modif, saisie score, confirmation Oui/Non (SMS), sync Calendar à chaque changement |
| `ScoreActivity` | `ScoreBusiness` | Saisie pure du score (5 sets), retourne `String[][]` en `RESULT_OK` |
| `ListCompetitionActivity` | `ListCompetitionBusiness` | `ExpandableListView` des compétitions groupées par `Tournament`, lien vers Calcul Classement |
| `ComputeRankingActivity` (+ `ComputeRankingFragment`) | `ComputeRankingBusiness` | Écran "Calcul Classement" (simulation paramétrable) — voir §6-7 |
| `PalmaresFastActivity` (+ `PalmaresFastFragment`) | `PalmaresFastBusiness` | Simulateur "palmarès rapide" : saisie manuelle victoires/défaites par classement |
| `MessageActivity` / `SmsMessageFragment` | `MessageBusiness` / `SmsMessageBusiness` | Édition du modèle de message SMS unique (`MessageService.getCommon()`), avec tags `[FIRSTNAME]`, `[LASTNAME]`, `[DATE]`, `[HOUR]`, `[DATE_RELATIVE]`, `[USER_FIRSTNAME]`, `[USER_LASTNAME]` |
| `QRCodeActivity` | `tool/QRCodeTool` | Génère et affiche le QR code de la fiche joueur courante (zxing `MultiFormatWriter`) |
| `LocationTournamentActivity` / `LocationClubActivity` / `LocationAddressActivity` | `LocationXxxBusiness extends GenericSpinnerFormActivity` | Formulaires CRUD génériques en cascade : Tournoi → Club (sous-formulaire) → Adresse (sous-formulaire). Reproduit à l'identique côté légal legacy uniquement — accessible depuis `PlayerActivity`/`InviteActivity`. |
| *(pas d'Activity dédiée)* `ListClubFragment` / `ClubFragment` | `ListClubBusiness` / `ClubBusiness` (package `ui.business`) | **2ᵉ pathway CRUD Club, indépendant du précédent** : entrée "Clubs" du drawer hamburger moderne (`menu_drawer_navigation.xml` → `R.id.nav_list_club`, géré par `NavigationDrawerFragment`), hébergée dans le conteneur `item_detail_container` d'`ItemDetailActivity`. `ListClubFragment` = liste plate (nom + adresse), FAB "+" → `ClubFragment` (form unique combinant nom du Club ET champs d'Adresse — ligne/CP/ville — sur le même écran, sans sous-formulaire séparé comme `LocationAddressActivity`). Pas de mode `MODIFY` dédié non plus : un clic sur une ligne rouvre `ClubFragment.build(club)`, pré-rempli via `ClubBusiness.onCreate()` qui résout l'Address associée par `club.getSubId()`. Suppression bloquée si le club a des invites (même règle que `LocationClubBusiness`). |
| `PieChartActivity` (+ `PieChartFragment`) | `PieChartBusiness` | Camembert de répartition des invites par classement, filtrable par type/résultat (`achartengine`) |
| `MatchActivity` | — | Écran quasi vestige (drawer saison seul, pas de données) |
| `RotatingButtons` | — | Démo technique (SeekBars pilotant rotation/scale d'un bouton), sans lien métier |

---

## 4. Intégrations externes

### 4.1 Google Calendar (`helper/GCalendarHelper.java`)
Intégration **100% locale** via `ContentResolver` sur `CalendarContract.{Events,Reminders,Attendees}` (pas d'appel réseau Google). `addEvent()` : titre "Just Tennis Match vs X" / "Just Tennis Entrainement vs X", lieu, date, rappel 60 min avant, statut participant (`UNKNOW`/`CONFIRMED`/`CANCELED`). À chaque `send()`/`modify()`/`confirmYes()`/`confirmNo()` sur une `Invite`, l'événement existant est **supprimé puis recréé** (pas d'update in-place, sauf `updateEventStatus` marqué `@Deprecated`). L'`idCalendar` résultant est stocké sur l'`Invite`. Suppression d'une invite → suppression de l'événement associé. Aucune vérification de permission `READ_CALENDAR`/`WRITE_CALENDAR` avant écriture (juste `@SuppressLint("MissingPermission")`) — bug potentiel de crash Android 6+, `ToolPermission.checkPermissionCALENDAR()` existe mais n'est jamais appelée.

### 4.2 Import de contacts Android
`ContactManager` (liste tous les contacts, `ContactsContract.Contacts`) pour `ListPersonActivity`. `ContactStructuredManager` (prénom/nom séparés via `StructuredName`) résout le contact sélectionné en détail. `PhoneManager` résout le(s) numéro(s) (dialogue de choix si plusieurs). Conversion finale via `PlayerParser.fromPersonForCreate`. Permission `READ_CONTACTS` vérifiée en runtime.

### 4.3 QR Code — échange P2P de fiche joueur (`tool/QRCodeTool.java`, `parser/PlayerParser.java`)
**Génération** : sérialisation texte `version!id!prénom!nom!naissance!tel!idClub!idRanking!localité!codePostal!adresse!idExternal` (`PlayerParser.toDataText`) → QR code zxing. **Lecture** : lance l'intent externe `com.google.zxing.client.android.SCAN` (activité `CaptureActivity` embarquée, lib `zxing-2.2.jar`), résultat parsé par `PlayerParser.fromData(...)` pour préremplir un nouveau `Player` (`fromQrCode=true`, court-circuite la confirmation SMS). ⚠️ Permission `CAMERA` **commentée** dans le manifest → scan probablement cassé sur Android 6+ dans le build actuel.

### 4.4 SMS — protocole propriétaire (désactivé en production)
`SmsManager`/`SmsReceiver`/`SmsParser`/`SmsMessageBusiness` implémentent un protocole complet `JUST_TENNIS|<payload chiffré via CryptoTool>` avec types `PA`/`PAY`/`PAN` (demande d'ajout joueur + réponses) et `PI`/`PIY`/`PIN` (invitation + réponses), incluant génération de notification et relance d'`InviteActivity` en mode confirmation. **Le `<receiver>` `SmsReceiver` est commenté dans le manifest et les permissions `RECEIVE_SMS`/`READ_SMS`/`SEND_SMS` sont commentées** → aucun SMS entrant n'est traité, et l'émission (`android.telephony.SmsManager.sendMultipartTextMessage`) échoue silencieusement faute de permission (exception catchée et logguée). Les écrans continuent de composer/afficher le texte du message normalement. **Pour la réécriture : décision produit à prendre** — réactiver un vrai canal SMS, migrer vers `Intent.ACTION_SEND` (partage générique), ou abandonner ce canal — mais conserver le modèle métier (templates, tags, statuts d'invitation `UNKNOW`/`ACCEPT`/`REFUSE`) indépendamment du transport choisi.
`CryptoTool` n'est utilisé que pour l'obfuscation de ce payload SMS — c'est une permutation caractère par caractère + décalage César, **pas une vraie primitive cryptographique**. Ne pas la reproduire comme mesure de sécurité réelle.

### 4.5 Navigation Drawer (filtre saison + type)
Deux implémentations parallèles fonctionnellement identiques (une par UI). En-tête = sélecteur de **saison** (`Spinner`, table `SAISON`) qui déclenche un rafraîchissement de l'écran courant. Pied de page = bascule **Entraînement/Compétition** (`TypeManager.TYPE`) qui change le thème visuel (orange en Compétition) et filtre les données affichées. Corps = recherche live (`RechercheService`, agrège Club/Tournoi/Adresse ou Joueurs) sur `PlayerActivity`/`ListPlayerActivity`.

### 4.6 Permissions runtime (`tool/ToolPermission.java`)
`READ_EXTERNAL_STORAGE`/`WRITE_EXTERNAL_STORAGE` (backup/restore DB), `READ_CONTACTS` (import contacts) sont demandées correctement (API ≥ 23, rationale dialog si refus). `CAMERA` et `RECEIVE_SMS`/`READ_SMS`/`SEND_SMS` sont **retirées du manifeste** alors que le code qui en dépend (scan QR, SMS) est toujours actif ailleurs — incohérence à corriger dans la réécriture (soit réactiver les permissions + fonctionnalités, soit retirer proprement le code correspondant).

---

## 5. Modèle de données (entités du domaine)

Toutes héritent de `GenericDBPojo<Long>.id` ; `GenericDBPojoNamed` ajoute `name` ; `GenericDBPojoNamedSubId` ajoute `subId` (FK parent implicite).

| Entité | Champs propres clés | Relations |
|---|---|---|
| `Saison` | `name, begin, end (Date), active (bool)` | Racine temporelle référencée par Player/User/Invite/Tournament ; une seule `active=true` à la fois |
| `Address` | `line1, postalCode, city, gps` | — |
| `Club` | (hérite name/subId) | `subId` = id Address |
| `Tournament` | `saison` | `subId` = id Club, FK explicite Saison |
| `Ranking` | `ranking (ex "30/2"), serie, order, rankingPointMan/Woman, VictoryMan/Woman` | Table de référence (barème FFT), référencée par Player/Invite |
| `Bonus` | `point (int)` | Table de référence (seed : 0 et 15) |
| `Person` (abstraite) | `firstName, lastName, birthday, phonenumber, address, postalCode, locality` | Mère de Player/User/Contact |
| `Player` extends Person | `idSaison, idTournament, idClub, idAddress, idRanking, idRankingEstimate, idExternal, idGoogle, type (TypeManager.TYPE)` | FK Saison/Tournament/Club/Address/Ranking(x2) |
| `User` extends Player | (aucun champ ajouté) | Entité unique représentant le propriétaire du téléphone, table séparée |
| `Contact` extends Person | `listPhone, photo, lookup, hasPhone` | Non persisté — pont vers ContactsContract |
| `Phone` | `number, type` | Rattaché à Contact |
| `Invite` (Serializable) | `saison, player (adversaire), user, date, status (STATUS), idExternal, idCalendar, type, idRanking, idRankingEstimate, scoreResult (SCORE_RESULT), listScoreSet, address, club, tournament, point (calculé, non persisté), bonusPoint` | FK Saison/Player/Address/Club/Tournament ; 1-N ScoreSet ; **`user` n'est PAS persisté en base** (reconstruit en mémoire, un seul User dans l'app) |
| `ScoreSet` (Serializable) | `invite, order, value1, value2 (-1 = forfait)` | FK obligatoire Invite |
| `Message` | `message, idPlayer (nullable = message commun)` | FK logique Player |
| `PalmaresFastValue` (Serializable) | `ranking, nbVictory, nbDefeat` | Calculé en mémoire, non persisté |
| `ComputeDataRanking` | voir §6.1 | DTO de calcul, non persisté |
| `RechercheResult` | `id, type (CLUB/TOURNAMENT/ADDRESS), data` | Non persisté |

`Invite.STATUS` = `UNKNOW / ACCEPT / REFUSE` (workflow social d'acceptation par SMS, indépendant du score).
`Invite.SCORE_RESULT` = `VICTORY / DEFEAT / UNFINISHED / WO_VICTORY / WO_DEFEAT` (résultat sportif, calculé — voir §8).
`TypeManager.TYPE` = `TRAINING / COMPETITION`.

---

## 6. Persistance — schéma SQLite reconstitué

⚠️ Particularité architecturale legacy : **une base SQLite par entité principale** (pas une seule DB unique), aucune contrainte `FOREIGN KEY` SQL réelle (tout est géré applicativement). **Dans la réécriture Room, fusionner en une seule base avec vraies FK et `ON DELETE` explicites.**

| Fichier .db | Version | Table(s) | PK |
|---|---|---|---|
| Address.db | 1 | `ADDRESS(NAME, LINE1, POSTAL_CODE, CITY, GPS)` | `_id` |
| Bonus.db | 1 | `BONUS(POINT)` | `_id` |
| Club.db | 1 | `CLUB(NAME, ID_ADDRESS)` | `_id` |
| Saison.db | 1 | `SAISON(NAME, BEGIN, END, ACTIVE)` | `_id` |
| Tournament.db | 2 | `TOURNAMENT(NAME, ID_CLUB, ID_SAISON)` | `_id` |
| Message.db | 5 | `MESSAGE(MESSAGE, ID_PLAYER)` | `_id` |
| Ranking.db | 3 | `RANKING(RANKING, SERIE, POSITION, RANK_POINT_MAN, RANK_POINT_WOMAN, RANK_VICTORY_MAN, RANK_VICTORY_WOMAN)` | `_id` |
| Player.db | 13 | `PLAYER(ID_SAISON, ID_TOURNAMENT, ID_CLUB, ID_ADDRESS, ID_RANKING, ID_RANKING_ESTIMAGE, FIRSTNAME, LASTNAME, BIRTHDAY, PHONENUMBER, ADDRESS, POSTALCODE, LOCALITY, ID_EXTERNAL, ID_GOOGLE, TYPE)` | `_id` |
| User.db | 9 | `USER(ID_SAISON, ID_TOURNAMENT, ID_CLUB, ID_ADDRESS, ID_RANKING, ID_RANKING_ESTIMAGE, FIRSTNAME, LASTNAME, BIRTHDAY, PHONENUMBER, ADDRESS, POSTALCODE, LOCALITY)` | `_id` |
| Invite.db | 15 | `INVITE(ID_SAISON, ID_PLAYER, TIME, STATUS, TYPE, ID_EXTERNAL, ID_CALENDAR, ID_RANKING, ID_RANKING_ESTIMATE, SCORE_RESULT, ID_ADDRESS, ID_CLUB, ID_TOURNAMENT, BONUS_POINT)` + `SCORE_SET(ID_INVITE, NUMBER, VALUE1, VALUE2)` | `_id` |

Notes :
- Coquille orthographique conservée en base : `ID_RANKING_ESTIMAGE` (Player/User) vs `ID_RANKING_ESTIMATE` (Invite, orthographe correcte) — à corriger dans la réécriture mais documenté ici pour compréhension historique.
- Seed `RANKING` (barème FFT complet, 19 lignes de "NC" à "1/6") : voir §7.1.
- Migrations : mix "incrémentale contrôlée" (`ALTER TABLE ADD COLUMN` + parfois backfill via un Service métier, ex. `SaisonService.getSaisonActiveOrFirst()` lors de l'ajout de `ID_SAISON` à `USER`) et "destructrice par défaut" (`DBAbstractHelper.onUpgrade` fait un backup SD puis `DROP TABLE` + recréation si aucune migration explicite n'est codée — utilisé par `RANKING` v2→v3).
- Filtres globaux implicites injectés automatiquement dans les requêtes (`customizeWhere()`) : **type courant** (`GenericDBDataSourceByType`, `AND TYPE = '<type>'`) et **saison courante** (`DBPersonDataSource`/`DBInviteDataSource`, `AND (ID_SAISON = <courante> OR ID_SAISON IS NULL)`). **À rendre explicite** dans la réécriture (paramètres de requête Room, pas un filtre caché de couche DAO générique).
- Le calcul de classement (§7) **ignore ce filtre de saison** : `InviteService.getByScoreResult()` porte sur tout l'historique, toutes saisons confondues.

### Pattern DAO générique legacy → équivalent Room recommandé

| Legacy | Équivalent moderne |
|---|---|
| `GenericDBHelper`/`DBXxxHelper` (CREATE TABLE + onUpgrade) | `@Entity` + `RoomDatabase` + `Migration` |
| `GenericDBDataSource`/`DBXxxDataSource` (cursorToPojo/putContentValue manuels) | `@Dao` avec `@Insert/@Update/@Delete/@Query`, mapping auto |
| `customizeWhere()` (filtre saison/type caché) | Paramètres explicites (`@Query("... WHERE type=:type AND ...")`) |
| `GenericService`/`XxxService` | `Repository` Kotlin (coroutines/Flow) |
| `DBDictionary` (registre statique des helpers) | Injection de dépendances (Hilt) |
| `RechercheService` (agrégation manuelle de 3 DataSources) | Requête Room unique (UNION) ou fusion de `Flow` |

---

## 7. ⭐ Algorithme central : calcul du classement (à préserver EXACTEMENT)

C'est le cœur métier de l'application — un barème maison inspiré du classement FFT. Moteur : `business/sub/ComputeRankSubService.computeDataRanking(listVictory, listDefeat, idRanking, estimate)`.

### 7.1 Barème de classement (table `RANKING`, seed complet)

Échelle ordonnée par `order` (0=NC à 18=1/6), chaque ligne = `{ranking, serie, order, pointMan, pointWoman, victoryMan, victoryWoman}` :
```
NC,4,0,0,0,6,6      40,4,1,0,0,6,6       30/5,4,2,6,6,6,6     30/4,4,3,70,70,6,6
30/3,4,4,120,120,6,6  30/2,4,5,170,170,6,6  30/1,4,6,210,210,6,6  30,3,7,280,260,8,8
15/5,3,8,300,290,8,8  15/4,3,9,310,300,8,8  15/3,3,10,320,310,8,8 15/2,3,11,340,330,8,8
15/1,3,12,370,350,8,8 15,2,13,430,400,9,9   5/6,2,14,430,400,9,9  4/6,2,15,430,430,9,9
3/6,2,16,430,490,10,10 2/6,2,17,490,550,10,11 1/6,2,18,540,600,11,12
```
Seuls `rankingPointMan` et `victoryMan` sont utilisés par le moteur (le barème "femme" existe en base mais n'est jamais lu par le code de calcul — zone grise, cf. §7.6).

### 7.2 Constantes du moteur
```java
NB_RANKING_ORDER_LOWER = 3   // fenêtre de tolérance de classement (positions en dessous)
BONUS_POINT_LIMIT      = 45  // plafond de points bonus cumulables
MAX_POINT               = 120 // points au-delà de +2 positions d'écart
```

### 7.3 Barème de points par victoire selon l'écart de classement (`RankingService.getNbPointDifference`)
`rankingDif = ranking.order(adversaire) − userRanking.order(joueur)`

| rankingDif | -3 | -2 | -1 | 0 | +1 | +2 | >+2 | <-3 |
|---|---|---|---|---|---|---|---|---|
| Points | 15 | 20 | 30 | 60 | 90 | 120 | 120 | **0** |

⚠️ Asymétrie à conserver : écarts `>+2` retombent à 120 (MAX_POINT), mais écarts `<-3` retombent à **0** (valeur par défaut, pas de branche explicite) — comportement différent pour les deux extrémités.

### 7.4 Algorithme complet (pseudo-code fidèle au code source)

```
FONCTION computeDataRanking(listVictory, listDefeat, idRanking, estimate):

    # Tri : les victoires contre les MIEUX classés sont traitées EN PREMIER
    # (InviteComparatorByRanking, inverse=true — l'ordre de tri détermine
    #  l'ordre de consommation du "crédit de victoires" ET l'ordre de cumul du bonus)
    listVictory = trier(listVictory, par order(adversaire) décroissant)

    userRanking = find(idRanking) ou getNC() si introuvable
    rankingPositionMin = max(0, userRanking.order - 3)
    nbVictory     = userRanking.victoryMan      # "crédit" de victoires à valider
    pointObjectif = userRanking.rankingPointMan
    sumPoint = 0 ; sumPointBonus = 0
    listInviteCalculed = [] ; listInviteNotUsed = []

    POUR CHAQUE invite DANS listVictory:
        ranking = classement adversaire (réel ou estimé selon `estimate`, voir §7.5)

        # Cumul bonus plafonné à 45, appliqué sur TOUTES les victoires dans l'ordre de tri
        # (indépendamment du fait que la victoire soit retenue ci-dessous)
        SI sumPointBonus >= 45:
            invite.bonusPoint = 0   # annulé en mémoire, pas persisté
        SINON:
            sumPointBonus += invite.bonusPoint

        SI ranking.order >= rankingPositionMin ET nbVictory > 0:
            listInviteCalculed.ajouter(invite)
            point = getNbPointDifference(ranking.order - userRanking.order)   # §7.3
            invite.point = point
            sumPoint += point
            nbVictory -= 1
        SINON:
            listInviteNotUsed.ajouter(invite)

    trier(listInviteCalculed, par point décroissant)
    trier(listInviteNotUsed, par date décroissante)

    data.nbMatch            = countByScoreResult(null)  # tous matchs passés, TOUTES saisons
    data.nbVictory           = nbVictory                 # crédit restant non consommé
    data.nbVictoryCalculate  = taille(listInviteCalculed)
    data.pointObjectif       = pointObjectif
    data.pointCalculate      = sumPoint
    data.pointBonus          = sumPointBonus
    data.listInviteCalculed  = listInviteCalculed
    data.listInviteNotUsed   = listInviteNotUsed

    computeVE2I5G(data, listVictory, listDefeat, idRanking, estimate)        # §7.5
    computeNbVitoryAdditional(data, idRanking, estimate)                     # §7.6

    RETOURNER data
```

**Affichage final** (`ComputeRankingActivity`) :
- Points : `pointCalculate + pointBonus` / `pointObjectif` (+ `[bonus:X]` si bonus>0)
- Victoires : `nbVictoryCalculate` / (`nbVictoryCalculate + nbVictory + nbVictoryAdditional`)
- Détail : `(nbVictoryAdditional) [V-E-2I-5G: vE2I5G]`

### 7.5 Système "V-E-2I-5G" (pénalité de défaites qualifiantes)

Nom = Victoires **-** Égalité **-** 2×Inférieur **-** 5×Grosse-défaite. Calculé sur `listDefeat` (défaites), en excluant les forfaits (`WO_DEFEAT`) :
```
POUR CHAQUE invite DANS listDefeat (hors WO_DEFEAT):
    ranking = classement adversaire
    diff = userRanking.order - ranking.order   # ordre joueur - ordre adversaire
    SI diff > 0:   # défaite contre un adversaire moins bien classé = défaite "gênante"
        SI diff == 1: i2I++
        SINON (diff >= 2): i5G++
        # note: `case 0: iE++` existe dans le switch mais est INATTEIGNABLE
        # (diff > 0 ET diff == 0 impossible) → iE toujours 0. Bug de code mort
        # à REPRODUIRE À L'IDENTIQUE sauf décision produit contraire.

vE2I5G = listVictory.size() - iE - (i2I * 2) - (i5G * 5)
```

### 7.6 Bonus de victoires additionnelles (`computeNbVitoryAdditional`)

Table de tranches selon la **série** du classement du joueur (`userRanking.serie`), en fonction de `vE2I5G` :

**Série 4** : `[0,4]→1  [5,9]→2  [10,14]→3  [15,19]→4  [20,24]→5  [25,∞)→6`
**Série 3** : `[0,7]→1  [8,14]→2  [15,22]→3  [23,29]→4  [30,39]→5  [40,∞)→6`
**Série 2** : `(-∞,-41]→-3  [-40,-31]→-2  [-30,-21]→-1  [-20,-1]→0  [0,7]→1  [8,14]→2  [15,22]→3  [23,29]→4  [30,39]→5  [40,∞)→6`
**Série 1** : aucune table définie → `nbVictoryAdditional = 0`.

### 7.7 Classement d'un adversaire pour une Invite (`RankingService.getRanking`)
```
SI joueur == "joueur inconnu" (Unknown Player, cas WO/palmarès rapide):
    classement = find(invite.idRanking) si non null, sinon NC
SINON:
    SI estimate: idRanking = player.idRankingEstimate si non null, sinon player.idRanking
    SINON:       idRanking = player.idRanking si non null, sinon player.idRankingEstimate
    SI toujours null: idRanking = NC
    classement = find(idRanking)
```

### 7.8 Zones grises du code (NE PAS deviner — à trancher côté produit avant réécriture)
1. `rankingPointWoman`/`VictoryWoman` : jamais lus par le moteur de calcul — barème "femme" en base mais non branché. Code mort ou fonctionnalité jamais terminée ?
2. `case 0: iE++` dans `computeVE2I5G` : logiquement inatteignable, `iE` toujours 0 en pratique.
3. Asymétrie `getNbPointDifference` : écarts `<-3` → 0 point (implicite), écarts `>+2` → 120 (explicite).
4. Le calcul de classement ignore la saison (porte sur tout l'historique) — à confirmer que c'est voulu.
5. Les valeurs réelles de la table `BONUS` en production peuvent différer du seed `{0, 15}` (table éditable) — extraire les valeurs réelles d'une base de prod si disponible avant de figer le barème dans la réécriture.

---

## 8. Cycle de vie d'une Invite (match) & validation du score

### 8.1 Calcul automatique de `SCORE_RESULT` (`ScoreSetService.getInviteScoreResult`)
```
SI listScoreSet vide → UNFINISHED
SINON (dernier set saisi = scoreLast, v1=value1 ou 0, v2=value2 ou 0):
    SI v1 == -1        → WO_VICTORY   (forfait, victoire)
    SINON SI v2 == -1   → WO_DEFEAT    (forfait, défaite)
    SINON SI v1 > v2    → VICTORY
    SINON SI v1 < v2    → DEFEAT
    SINON (v1 == v2)    → reste UNFINISHED
```
⚠️ Seul le **dernier set de la liste** détermine le résultat — **aucune vérification du décompte de sets gagnés (best of 3)**, aucun moteur de règles de tennis réel (pas de tie-break, pas de "6 jeux minimum avec 2 d'écart"). C'est à l'utilisateur de saisir les sets dans le bon ordre.

### 8.2 Validation de saisie (`InviteBusiness.checkScoreSet`)
- 5 sets maximum en saisie.
- Pour tous les sets **sauf le dernier** : chaque score doit être ≤ 7 (contrainte molle, un "7-4" est accepté sans blocage).
- Pour le **dernier set** : aucune borne vérifiée côté métier (seule l'UI limite via `maxLength`). La valeur spéciale `-1` sur le dernier set encode un forfait.
- Case vide des deux côtés → set ignoré ; un seul côté rempli → l'autre forcé à `"0"`.

**Conclusion pour la réécriture** : reproduire cette absence de validation stricte à l'identique (sauf décision produit explicite de durcir les règles), car aucun moteur de règles de tennis plus riche n'existe dans le code source.

### 8.3 Palmarès (agrégation par classement, pas par joueur)
Deux implémentations, agrégeant les victoires/défaites **par `Ranking` rencontré** (pas par joueur individuel) :
- `ComputeRankingBusiness.getPalmares()` : historique réel (`getByScoreResult(VICTORY/DEFEAT)`).
- `PalmaresFastBusiness` (simulateur) : l'utilisateur saisit directement N victoires/M défaites par classement (+ ligne spéciale "WO"), le business génère des `Invite` fictives datées d'aujourd'hui contre le "joueur inconnu" et les repasse dans **exactement le même moteur** `ComputeRankSubService.computeDataRanking` — permet de tester "si j'avais N victoires contre du 15/2, quel score j'obtiendrais ?".

Écran "Calcul Classement" : utilise systématiquement `estimate=true` (classement estimé des adversaires en priorité), cible par défaut `user.idRankingEstimate` (repli sur `idRanking` puis NC). Recalcul intégral (pas incrémental) à chaque modification d'invite.

---

## 9. Sauvegarde / Restauration / Partage de la base

- **Backup** (`DBAbstractHelper.backupDbToSdcard()`) : copie brute (non compressée, non chiffrée) de chaque fichier `.db` vers `/sdcard/<package>/<dbName>`. Déclenché par `MainActivity` sur 9 tables (Address, Bonus, Club, Invite, Message, Player, Ranking, Saison, Tournament, User — ScoreSet exclu car même fichier qu'Invite).
- **Restore** : copie inverse, écrase la base courante ; publie un événement RxJava pour rafraîchir l'UI. Variante "réception externe" : ouverture d'un fichier `.db` via Intent `VIEW`/`SEND`/`SEND_MULTIPLE` (mimetype `application/justtennisdb`).
- **Partage** (`OnClickSendDBListenerOk`) : `Intent.ACTION_SEND_MULTIPLE` avec `Uri.fromFile()` — **cassé sur Android 7+** (`FileUriExposedException`, jamais vraiment corrigé, juste contourné en masquant le menu, voir §10.2).
- **Aucun chiffrement** des fichiers `.db` sauvegardés — à traiter explicitement dans la réécriture si cette fonctionnalité est conservée (`EncryptedFile`, SQLCipher, ou backup cloud chiffré).

---

## 10. Bugs de sécurité/qualité historiques (leçons — ne pas reproduire)

### 10.1 SQL Injection via ContentProviders exportés (le plus critique)
7 `ContentProvider` (`ClubProvider`, `InviteProvider`, `PlayerProvider`, `RankingProvider`, `SaisonProvider`, `ScoreSetProvider`, `UserProvider`) déclarés `exported="true"` + `protectionLevel="signature"`.

**Faille** : le paramètre `projection` (colonnes demandées par l'appelant du ContentProvider, potentiellement une autre app signée avec la même clé) était transmis tel quel à `SQLiteDatabase.query()`, sans validation — un appelant pouvait injecter des expressions SQL arbitraires ou des sous-requêtes dans le tableau `projection` (attaque "ContentProvider projection injection"). `selection` (WHERE brut) posait le même risque combiné.

**Correctif final** (après plusieurs itérations hésitantes visibles dans l'historique Git — désactivation totale de `query()` en urgence, puis réactivation progressive) : `AbstractContentProvider` utilise `SQLiteQueryBuilder` + `setProjectionMap(whitelist des colonnes réelles de la table)` + `setStrict(true)`, qui rejette toute colonne hors whitelist. `insert()` réactivé via `ContentValues` (paramétré nativement). `delete()`/`update()` restent **non implémentés** (`UnsupportedOperationException`) — jamais réactivés.

**Leçon pour la réécriture** : ne jamais transmettre un `projection`/`selection` fourni par un appelant externe directement à une requête SQL, même via une API "sûre" comme `SQLiteDatabase.query()` (elle n'est sûre que si la liste de colonnes est validée côté serveur). Si un ContentProvider équivalent est nécessaire : whitelist stricte dès la conception, ou mieux, DAO Room avec requêtes `@Query` figées sans construction dynamique de SELECT. Éviter aussi les concaténations SQL non paramétrées trouvées ailleurs dans les DAO internes (`getLikeByLine`, `getLikeByName` — construites par concaténation `'%" + str + "%'`, à corriger en requêtes paramétrées).

### 10.2 `FileUriExposedException` — jamais vraiment corrigé, juste contourné
`Uri.fromFile()` utilisé pour partager l'APK/la DB via `Intent.ACTION_SEND`, interdit depuis Android 7 (`StrictMode`). Le "fix" historique n'a pas touché au code fautif : il a juste masqué les entrées de menu concernées (`android:visible="false"`). **Le code `Uri.fromFile` est toujours présent** dans l'état actuel. **Leçon** : utiliser `FileProvider` (URIs `content://`) dès qu'un fichier est partagé via Intent.

### 10.3 Autres bugs corrigés (contexte, moindre priorité)
- `ClassCastException` sur `DrawerLayout` : cast direct sans `instanceof`, corrigé.
- `NotSerializableException` sur `MutableLiveData` dans un ViewModel `Serializable` : un `ViewModel` AndroidX ne devrait jamais implémenter `Serializable` (il survit déjà nativement aux changements de config).
- Migration AndroidX **jamais terminée** : un seul import (`DataBindingUtil`) migré dans 2 fichiers, tout le reste du projet reste sur `android.support.*`.

---

## 11. Stack technique legacy & recommandations de modernisation

| Legacy | Version | Remplacement recommandé |
|---|---|---|
| Android Gradle Plugin | 3.2.1 | AGP récent |
| compileSdk/target/min | 27/27/19 | compileSdk/target récents, minSdk à redéfinir selon le parc cible |
| Support Library | 27.1.1 (pas AndroidX) | AndroidX complet |
| UI | XML/`findViewById`, `ListView` partiellement migré vers `RecyclerView`, `ConstraintLayout` déclaré mais jamais utilisé (0/107 layouts), DataBinding activé mais utilisé dans 2 layouts/107 seulement | **Jetpack Compose** entièrement |
| Réactif | RxJava2/RxAndroid (usage limité) | Coroutines + StateFlow |
| Réseau | OkHttp3 3.10 (utilisé seulement pour l'API Flickr, photo d'en-tête) | Retrofit/Ktor + OkHttp |
| Images | Picasso 2.71828 | Coil |
| JSON | Jackson 2.9 | kotlinx.serialization ou Moshi |
| Persistance | SQLite manuel (`SQLiteOpenHelper` + DAO maison "GenericDBDataSource") | **Room** |
| Firebase/Crash | firebase-core 16.0.4 + Fabric/Crashlytics 2.9.5 (plugin `io.fabric` déprécié) | Firebase Crashlytics SDK natif |
| QR Code | `zxing-2.2.jar` embarqué manuellement (~2013) | ML Kit Barcode Scanning ou zxing-android-embedded via Gradle |
| Graphiques | achartengine 1.1.0 (jar manuel, projet mort) | Vico, MPAndroidChart, ou Compose custom |
| Flexbox | `com.google.android:flexbox:0.2.3` | `FlowRow`/`FlowColumn` Compose natif |
| Partage fichiers | `Uri.fromFile()` (cassé Android 7+) | `FileProvider` |
| Multidex | activé manuellement | non nécessaire si dépendances legacy retirées |
| ProGuard/R8 | désactivé en release, règles vides | R8 activé + règles réelles dès le départ |
| Lint | désactivé en release (`abortOnError false`) | Lint strict activé |
| Tests | JUnit4 (3 classes, ~410 lignes, uniquement sur le parsing) + Espresso (5 classes, très incomplet) — couverture quasi nulle | Suite complète : tests unitaires Kotlin (logique métier/ranking en priorité absolue), tests Compose UI, tests d'intégration Room |
| `applicationId` | `justtennis.com.justtennis` incohérent avec `package="com.justtennis"` du manifest | Cohérence à rétablir avant toute migration d'ID |

### Ressources
- Découpage `values/*.xml` **par écran** (~40 fichiers : `strings_invite.xml`, `color_player.xml`, `dimens_location.xml`, etc.) — pattern modulaire mais fragmenté.
- **i18n complète et propre** : `values/` = anglais (défaut), `values-fr/` = traduction française complète et cohérente (14 paires de fichiers) — pattern à conserver conceptuellement, mais à consolider (moins de fragmentation fichier-par-fichier) dans la réécriture.
- 107 layouts XML, nommage incohérent (`element_*`, `fragment_*`, `list_*`, pas de convention `activity_*` uniforme).

---

## 12. Priorités pour la réécriture

1. **Priorité absolue : reproduire fidèlement l'algorithme de classement (§7)** — c'est la valeur métier différenciante de l'app, non standard, avec plusieurs comportements "bizarres mais intentionnels" (asymétrie du barème, bug `iE` mort, cumul de bonus avant filtrage) à couvrir par des tests unitaires exhaustifs AVANT toute réécriture UI.
2. Reproduire le schéma de données et les relations (§5-6) dans un modèle Room unique et cohérent, avec vraies FK — corriger les incohérences historiques (`ID_RANKING_ESTIMAGE`, absence de contrainte référentielle) sans changer le comportement fonctionnel.
3. Ne pas reproduire le pattern DAO générique maison ni les ContentProviders exportés sans whitelist — utiliser Room + Repository dès le départ (§10.1).
4. Trancher explicitement (décision produit) le sort du canal SMS (§4.4) avant de le réécrire — actuellement mort en production mais avec une logique métier riche (templates, statuts) à ne pas perdre si le canal est remplacé.
5. Conserver les intégrations Calendar (§4.1), Contacts (§4.2) et QR Code (§4.3), en corrigeant les permissions manquantes/incohérentes identifiées.
6. Ne pas reporter le code mort identifié en §2 (packages UI dupliqués, `org.gdocument.gtracergps`, `PieChartActivity2`, etc.).
7. Reconstruire une vraie suite de tests, en particulier sur `ComputeRankSubService` (§7) où la moindre régression change silencieusement le classement de tous les joueurs du club.
