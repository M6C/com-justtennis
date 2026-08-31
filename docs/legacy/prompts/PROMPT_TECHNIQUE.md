# PROMPT — Architecture Technique & Implémentation Android (Développeur Android Senior)

> **À copier/coller dans une nouvelle session Claude Code** pour lancer la conception technique et l'implémentation d'une application Android **entièrement nouvelle**, à partir d'un cahier des charges fonctionnel déjà validé (voir `PROMPT_FONCTIONNEL.md`). Ce prompt ne fait référence à aucune application existante : il s'agit d'un développement neuf, appliquant l'état de l'art actuel du développement Android.

---

## Rôle et posture attendus

Tu interviens en tant que **développeur Android Senior**, expert des bonnes pratiques actuelles recommandées par Google (Modern Android Development), garant de :
- une architecture propre, testable et évolutive,
- des choix technologiques à jour (pas de dépendances obsolètes ou dépréciées),
- la sécurité et la robustesse du code (gestion des permissions, du stockage, des accès concurrents),
- une couverture de tests rigoureuse, en particulier sur la logique métier critique détaillée en §4.

**Avant de commencer**, vérifie si ton environnement Claude Code dispose de skills ou d'agents installés pertinents (revue de code, sécurité, design d'architecture, génération de tests). Si c'est le cas, charge-les et utilise-les au fil de l'implémentation (ex. une revue de sécurité avant de finaliser la couche de persistance, une revue de code avant chaque commit significatif). Si aucun skill dédié n'est disponible dans l'environnement cible, applique directement les exigences ci-dessous sans attendre d'outil externe.

---

## 1. Stack technique cible

| Domaine | Choix imposé |
|---|---|
| Langage | Kotlin exclusivement (pas de Java) |
| UI | Jetpack Compose + Material 3 |
| Navigation | Navigation Compose |
| Asynchrone | Coroutines + Flow / StateFlow (pas de RxJava) |
| Persistance | Room (base SQLite unique, pas de fichier par entité) |
| Injection de dépendances | Hilt |
| Réseau (si besoin ponctuel, ex. image distante) | Retrofit + OkHttp |
| Chargement d'images | Coil |
| Sérialisation | kotlinx.serialization |
| Scan QR Code | CameraX + ML Kit Barcode Scanning (pas de lib tierce en `.jar` manuel) |
| Graphiques/statistiques | Compose Charts natif ou lib maintenue compatible Compose (ex. Vico) |
| Tests unitaires | JUnit5 + Turbine (Flow) + MockK |
| Tests UI | Compose UI Testing |
| Qualité de code | ktlint + detekt, activés en CI dès le départ |
| Build | R8/ProGuard activé en release avec règles réelles, `abortOnError` du lint actif en CI |
| Crash reporting | Firebase Crashlytics (SDK natif, pas le plugin Fabric historique) |
| minSdk / targetSdk / compileSdk | À définir selon le parc cible actuel (versions récentes), en cohérence avec la couverture des API utilisées (Calendar Provider, Contacts, permissions runtime) |

**Interdits explicites** (à ne jamais introduire) :
- Support Library (`android.support.*`) — AndroidX uniquement.
- `Uri.fromFile()` pour tout partage de fichier via `Intent` — utiliser exclusivement `FileProvider` (`androidx.core.content.FileProvider`) avec des URI `content://`.
- Construction de requêtes SQL par concaténation de chaînes avec une entrée utilisateur, y compris via `SQLiteDatabase.query()` sans whitelist de projection — utiliser systématiquement les requêtes paramétrées de Room (`@Query` avec `:param`).
- `ContentProvider` exporté (`exported="true"`) sans besoin métier explicite et validé ; si un tel besoin existe, exposer uniquement des requêtes fixes et typées, jamais de projection/selection dynamique fournie par l'appelant.
- Tout mécanisme de « chiffrement » par substitution de caractères ou décalage arithmétique simple — si un besoin de confidentialité est identifié (ex. sauvegarde exportée), utiliser une primitive cryptographique standard (`EncryptedFile`/Tink, ou déléguer au chiffrement natif du support de stockage).
- Dépendances embarquées en `.jar` binaire committé dans le repo — toute dépendance doit provenir de Maven Central/Google Maven via Gradle.

---

## 2. Architecture

**Clean Architecture** en 3 couches, avec **MVVM** côté présentation (ViewModel + `StateFlow<UiState>` exposé à des écrans Compose stateless) :

```
:app
:core:designsystem      (thème Material 3, composants Compose partagés)
:core:common             (utilitaires transverses, sans dépendance Android si possible)
:data                    (Room, DAO, Repository impl, DataStore)
:domain                  (modèles métier, UseCases, interfaces Repository — module Kotlin pur, sans dépendance Android)
:feature:player          (répertoire joueurs, profil)
:feature:invite          (invitations, score)
:feature:ranking         (classement, simulateur, palmarès)
:feature:location        (adresses, clubs, tournois)
:feature:settings        (saison, sauvegarde/restauration, message template)
```

- Le module `:domain` doit être **le seul lieu d'implémentation de l'algorithme de classement** (§4) — testable unitairement sans aucune dépendance Android (pas de `Context`, pas de Room direct), via des interfaces `Repository` injectées.
- Chaque Repository expose des `Flow<T>` (lecture réactive) et des `suspend fun` (écriture), jamais de callback ni d'accès direct à `SQLiteDatabase`.
- Pas de couche « DAO générique » type `GenericDBDataSource<T>` — chaque entité a son propre `@Dao` Room avec des requêtes explicites et typées (plus de sécurité, plus de lisibilité, meilleure détection d'erreur à la compilation).

---

## 3. Modèle de données (Room)

Concevoir **une seule base Room** (pas une base par entité) avec de vraies contraintes `@ForeignKey` (et `onDelete` explicite : `CASCADE` pour les enfants obligatoires comme les sets d'un match, `SET_NULL` ou `RESTRICT` selon le cas pour les références optionnelles).

Entités principales à modéliser (nommage et types à moderniser/clarifier, mais champs et relations fonctionnellement équivalents au besoin métier) :

- `SeasonEntity` (nom, dateDébut, dateFin, active)
- `AddressEntity` (nom, ligne1, codePostal, ville, gps)
- `ClubEntity` (nom, addressId FK)
- `TournamentEntity` (nom, clubId FK, seasonId FK)
- `RankingLevelEntity` (référentiel des niveaux : libellé, série, ordre, objectifPoints, objectifVictoires — un seul barème, pas de doublon homme/femme sauf décision produit contraire, voir décision ouverte §7 du prompt fonctionnel)
- `BonusEntity` (référentiel des valeurs de bonus possibles — ou constante applicative si la liste n'a pas vocation à être éditable)
- `PlayerEntity` (identité, téléphone, seasonId FK, clubId FK, addressId FK, tournamentId FK, rankingLevelId FK, estimatedRankingLevelId FK, usageType [COMPETITION/TRAINING], externalContactId, googleContactId)
- `UserProfileEntity` (profil du joueur propriétaire de l'app — envisager une table à ligne unique plutôt qu'une table dupliquée de `PlayerEntity` comme dans le besoin d'origine ; à trancher selon simplicité d'implémentation, le comportement fonctionnel attendu est un profil unique)
- `InviteEntity` (seasonId FK, playerId FK, date, statusAcceptation [PENDING/ACCEPTED/REFUSED], usageType, calendarEventId, rankingLevelId FK, estimatedRankingLevelId FK, matchResult [VICTORY/DEFEAT/UNFINISHED/WO_VICTORY/WO_DEFEAT], addressId FK, clubId FK, tournamentId FK, bonusPoints)
- `ScoreSetEntity` (inviteId FK **NOT NULL** avec `onDelete=CASCADE**`, numéro de set, score joueur, score adversaire — valeur spéciale réservée pour encoder un forfait, à définir proprement, ex. enum plutôt qu'un entier magique `-1`)
- `MessageTemplateEntity` (texte du modèle, playerId FK nullable = modèle commun)

**Points de correction explicites par rapport au besoin fonctionnel brut** (à documenter dans le code, ce sont des améliorations techniques assumées, pas des évolutions produit) :
- Remplacer les filtres implicites de type/saison (injectés silencieusement dans une couche DAO générique) par des **paramètres de requête Room explicites** (`@Query("SELECT * FROM invite WHERE usageType = :type AND (seasonId = :seasonId OR seasonId IS NULL)")`).
- Vraies contraintes `FOREIGN KEY` partout où une relation logique existe.
- Un seul fichier de base Room avec `Migration` versionnées et testées (`MigrationTestHelper`), pas de stratégie destructive silencieuse par défaut.

---

## 4. ⭐ Algorithme métier critique — calcul du classement (spécification à implémenter à l'identique)

Cette section décrit une **règle métier figée**, indépendante de toute application existante — c'est la spécification fonctionnelle du système de classement du club telle que validée par le Product Owner (voir `PROMPT_FONCTIONNEL.md`, Epic F). Implémente-la dans le module `:domain`, avec une **couverture de tests unitaires exhaustive** (cas nominaux + cas limites listés en §4.6) avant tout branchement à l'UI — une régression sur cette logique changerait silencieusement le classement de tous les joueurs du club.

### 4.1 Référentiel des niveaux de classement

19 niveaux ordonnés (du plus faible au plus fort), chacun avec : `libellé`, `série` (1 à 4), `ordre` (position, 0 à 18), `objectifPoints`, `objectifVictoires`.

```
NC     série=4  ordre=0   objectifPoints=0    objectifVictoires=6
40     série=4  ordre=1   objectifPoints=0    objectifVictoires=6
30/5   série=4  ordre=2   objectifPoints=6    objectifVictoires=6
30/4   série=4  ordre=3   objectifPoints=70   objectifVictoires=6
30/3   série=4  ordre=4   objectifPoints=120  objectifVictoires=6
30/2   série=4  ordre=5   objectifPoints=170  objectifVictoires=6
30/1   série=4  ordre=6   objectifPoints=210  objectifVictoires=6
30     série=3  ordre=7   objectifPoints=280  objectifVictoires=8
15/5   série=3  ordre=8   objectifPoints=300  objectifVictoires=8
15/4   série=3  ordre=9   objectifPoints=310  objectifVictoires=8
15/3   série=3  ordre=10  objectifPoints=320  objectifVictoires=8
15/2   série=3  ordre=11  objectifPoints=340  objectifVictoires=8
15/1   série=3  ordre=12  objectifPoints=370  objectifVictoires=8
15     série=2  ordre=13  objectifPoints=430  objectifVictoires=9
5/6    série=2  ordre=14  objectifPoints=430  objectifVictoires=9
4/6    série=2  ordre=15  objectifPoints=430  objectifVictoires=9
3/6    série=2  ordre=16  objectifPoints=430  objectifVictoires=10
2/6    série=2  ordre=17  objectifPoints=490  objectifVictoires=10
1/6    série=2  ordre=18  objectifPoints=540  objectifVictoires=11
```

*(Le référentiel prévoit historiquement des colonnes distinctes « objectif points/victoires femme », non exploitées par le calcul ci-dessous — se référer à la décision produit ouverte correspondante avant d'introduire ou non un second barème.)*

### 4.2 Constantes du moteur

```kotlin
const val RANKING_WINDOW_SIZE = 3      // fenêtre de tolérance de niveau (positions en dessous du niveau du joueur)
const val BONUS_POINTS_CAP = 45        // plafond de points bonus cumulables
const val MAX_POINTS_PER_VICTORY = 120 // points au-delà de +2 positions d'écart favorable
```

### 4.3 Barème de points par victoire selon l'écart de niveau

`ecart = ordre(niveau adversaire) − ordre(niveau du joueur)`

| écart | ≤ -4 | -3 | -2 | -1 | 0 | +1 | +2 | ≥ +3 |
|---|---|---|---|---|---|---|---|---|
| points | **0** | 15 | 20 | 30 | 60 | 90 | 120 | 120 |

⚠️ **Asymétrie à respecter exactement** : au-delà de +2 (adversaire beaucoup mieux classé), les points plafonnent à 120 ; en revanche en-deçà de -3 (adversaire beaucoup moins bien classé), la victoire ne rapporte **aucun point** (0), ce n'est pas symétrique. Couvrir ce cas par un test dédié.

### 4.4 Algorithme complet

```
fonction calculerClassement(victoires: List<Match>, defaites: List<Match>, niveauCible: RankingLevel, utiliserNiveauEstime: Boolean): ResultatClassement:

    # 1. Tri : les victoires contre les adversaires les MIEUX classés sont traitées EN PREMIER.
    #    Cet ordre de tri détermine l'ordre de consommation du quota de victoires ET l'ordre
    #    de cumul du bonus plafonné (§4.3bis) — NE PAS changer l'ordre de traitement.
    victoiresTriees = victoires.trierPar { niveauAdversaire(it, utiliserNiveauEstime).ordre } décroissant

    fenetreMin = max(0, niveauCible.ordre - RANKING_WINDOW_SIZE)
    quotaVictoiresRestant = niveauCible.objectifVictoires
    objectifPoints = niveauCible.objectifPoints
    pointsCumules = 0
    bonusCumule = 0
    matchsComptabilises = mutableListOf<MatchAvecPoints>()
    matchsNonComptabilises = mutableListOf<Match>()

    pour chaque match dans victoiresTriees:
        niveauAdversaire = resoudreNiveauAdversaire(match, utiliserNiveauEstime)   # voir §4.5

        # Cumul du bonus : plafonné, appliqué sur TOUTES les victoires dans l'ordre de tri,
        # indépendamment du fait que le match soit comptabilisé ci-dessous.
        si bonusCumule >= BONUS_POINTS_CAP:
            bonusEffectif(match) = 0
        sinon:
            bonusCumule += match.bonusPoints

        si niveauAdversaire.ordre >= fenetreMin ET quotaVictoiresRestant > 0:
            ecart = niveauAdversaire.ordre - niveauCible.ordre
            points = baremePoints(ecart)   # §4.3
            matchsComptabilises.ajouter(MatchAvecPoints(match, points))
            pointsCumules += points
            quotaVictoiresRestant -= 1
        sinon:
            matchsNonComptabilises.ajouter(match)

    matchsComptabilises.trierPar { it.points } décroissant
    matchsNonComptabilises.trierPar { it.date } décroissant

    penaliteDefaites = calculerPenaliteDefaites(defaites, niveauCible, utiliserNiveauEstime)   # §4.5bis
    victoiresBonusProgression = calculerVictoiresBonusProgression(niveauCible.serie, penaliteDefaites)  # §4.6

    retourner ResultatClassement(
        objectifPoints = objectifPoints,
        pointsCalcules = pointsCumules,
        pointsBonus = bonusCumule,
        nombreVictoiresRestantes = quotaVictoiresRestant,
        nombreVictoiresComptabilisees = matchsComptabilises.taille,
        victoiresBonusProgression = victoiresBonusProgression,
        indicateurPenaliteDefaites = penaliteDefaites,
        matchsComptabilises = matchsComptabilises,
        matchsNonComptabilises = matchsNonComptabilises,
    )
```

**Affichage attendu côté UI** (spécification fonctionnelle du résultat, l'implémentation visuelle est du ressort du prompt fonctionnel/design) :
- Points : `pointsCalcules + pointsBonus` sur `objectifPoints` (+ mention du détail bonus si > 0).
- Victoires : `nombreVictoiresComptabilisees` sur `(nombreVictoiresComptabilisees + nombreVictoiresRestantes + victoiresBonusProgression)`.

### 4.5 Résolution du niveau d'un adversaire pour un match donné

```
fonction resoudreNiveauAdversaire(match, utiliserNiveauEstime):
    si match.adversaire est "inconnu":
        retourner match.niveauSaisiManuel ?? niveauNC
    sinon:
        niveau = si utiliserNiveauEstime:
                     match.adversaire.niveauEstime ?? match.adversaire.niveau
                 sinon:
                     match.adversaire.niveau ?? match.adversaire.niveauEstime
        retourner niveau ?? niveauNC
```

### 4.6 Pénalité de défaites contre des adversaires moins bien classés

```
fonction calculerPenaliteDefaites(defaites, niveauCible, utiliserNiveauEstime):
    nbDefaitesEcart1 = 0      # défaite contre un adversaire 1 niveau en dessous
    nbDefaitesEcartFort = 0   # défaite contre un adversaire ≥2 niveaux en dessous

    pour chaque match dans defaites (EXCLURE les défaites par forfait) :
        niveauAdversaire = resoudreNiveauAdversaire(match, utiliserNiveauEstime)
        ecart = niveauCible.ordre - niveauAdversaire.ordre   # positif si adversaire moins bien classé
        si ecart > 0:
            si ecart == 1: nbDefaitesEcart1 += 1
            sinon (ecart >= 2): nbDefaitesEcartFort += 1

    retourner nombreDeVictoires - (nbDefaitesEcart1 * 2) - (nbDefaitesEcartFort * 5)
```

*(Note d'implémentation : la spécification d'origine de ce calcul prévoit un troisième cas — une pénalité pour « écart nul » — qui s'avère mathématiquement inatteignable dans la formule telle que définie ci-dessus (elle nécessiterait `ecart > 0 ET ecart == 0` simultanément). Ne pas chercher à « corriger » ce cas de façon créative : implémente la formule exactement telle que décrite, ce troisième cas ne contribuera jamais, et documente ce fait dans un commentaire de code et dans les tests.)*

### 4.7 Victoires bonus de progression

Tables de correspondance selon la **série** du niveau cible, en fonction de l'indicateur de pénalité de défaites (§4.6) :

**Série 4** : `]-∞,-1[→0  [0,4]→1  [5,9]→2  [10,14]→3  [15,19]→4  [20,24]→5  [25,+∞[→6`
**Série 3** : `]-∞,-1[→0  [0,7]→1  [8,14]→2  [15,22]→3  [23,29]→4  [30,39]→5  [40,+∞[→6`
**Série 2** : `]-∞,-41]→-3  [-40,-31]→-2  [-30,-21]→-1  [-20,-1]→0  [0,7]→1  [8,14]→2  [15,22]→3  [23,29]→4  [30,39]→5  [40,+∞[→6`
**Série 1** : aucune table définie → retourne 0.

### 4.8 Cas limites obligatoires à couvrir par des tests unitaires

1. Écart de niveau exactement à la borne (-3, -4, +2, +3).
2. Bonus cumulé atteignant exactement le plafond de 45 sur le match en cours de traitement.
3. Quota de victoires atteint en cours de boucle (les victoires suivantes doivent basculer en « non comptabilisées » même si elles seraient dans la fenêtre de niveau).
4. Adversaire « inconnu » sans niveau saisi manuellement → repli sur NC.
5. Absence de niveau réel ET estimé sur un adversaire → repli sur NC.
6. Défaite par forfait → exclue du calcul de pénalité.
7. Victoire par forfait → à confirmer avec le Product Owner si elle doit être traitée comme une victoire standard dans le barème de points (comportement à spécifier explicitement, zone grise fonctionnelle).
8. Série de niveau cible = 1 → victoires bonus toujours à 0.
9. Ordre de tri des victoires par niveau adversaire décroissant : un test doit vérifier que changer l'ordre de traitement change le résultat (pour garantir que l'implémentation respecte bien le tri prescrit).

### 4.9 Règles de saisie et de calcul du résultat d'un match

```
fonction calculerResultatMatch(sets: List<ScoreSet>): MatchResult:
    si sets.estVide(): retourner UNFINISHED
    dernierSet = sets.dernier()
    si dernierSet.scoreJoueur == FORFAIT: retourner WO_VICTORY
    si dernierSet.scoreAdversaire == FORFAIT: retourner WO_DEFEAT
    si dernierSet.scoreJoueur > dernierSet.scoreAdversaire: retourner VICTORY
    si dernierSet.scoreJoueur < dernierSet.scoreAdversaire: retourner DEFEAT
    sinon: retourner UNFINISHED
```

Seul le **dernier set renseigné** détermine le résultat (pas de vérification du nombre de sets gagnés). Confirme avec le Product Owner (décision ouverte §7 du prompt fonctionnel) si ce comportement doit être conservé tel quel ou remplacé par un vrai moteur de règles du tennis avant de figer l'implémentation — dans le doute, implémente la version décrite ci-dessus (comportement de référence connu et éprouvé en usage réel) derrière une interface `ScoreValidator` interchangeable, pour permettre un remplacement ultérieur sans casser le reste du domaine.

---

## 5. Intégrations plateforme

| Intégration | API Android moderne | Points d'attention |
|---|---|---|
| Calendrier | `CalendarContract` via `ContentResolver`, dans un `Repository` dédié (`CalendarRepository`) | Vérifier `READ_CALENDAR`/`WRITE_CALENDAR` avant toute écriture (pas de `@SuppressLint("MissingPermission")` pour masquer une vérification manquante) ; opérations idempotentes (update réel de l'événement plutôt que delete+recreate systématique si possible) |
| Contacts | `ContactsContract` via `ContentResolver`, `READ_CONTACTS` runtime | Prévoir une pagination/lazy loading si le carnet de contacts est volumineux |
| Scan QR Code | CameraX + ML Kit Barcode Scanning, `CAMERA` runtime | Gérer le cycle de vie de la caméra proprement (pas de fuite), fallback UX clair si permission refusée |
| Stockage / sauvegarde | Storage Access Framework (SAF, `ACTION_CREATE_DOCUMENT`/`ACTION_OPEN_DOCUMENT`) plutôt que l'accès direct à `Environment.getExternalStorageDirectory()` (déprécié) | Format d'export versionné (ex. archive unique contenant un export structuré des tables, pas un dump brut de fichiers `.db` multiples) |
| Partage de fichier | `FileProvider` + `Intent.ACTION_SEND` avec `FLAG_GRANT_READ_URI_PERMISSION` | Jamais `Uri.fromFile()` |
| Permissions runtime | `ActivityResultContracts.RequestPermission` (pas l'ancien `requestPermissions`/`onRequestPermissionsResult`) | Demander chaque permission au moment précis de son usage (voir exigence UX du prompt fonctionnel) |

---

## 6. Sécurité — exigences non négociables

1. **Aucune requête SQL construite par concaténation avec une entrée utilisateur.** Toute recherche texte (ex. recherche de partenaire par nom) utilise les paramètres liés Room (`@Query("... WHERE name LIKE '%' || :query || '%'")`).
2. **Aucun `ContentProvider` exporté** sauf besoin explicitement validé par le Product Owner ; si un tel besoin existe, définir des requêtes fixes typées (jamais de projection/selection dynamique côté appelant), avec tests d'intégration dédiés simulant un appelant malveillant (colonnes/expressions arbitraires).
3. **Partage de fichiers exclusivement via `FileProvider`**, jamais d'URI `file://` exposée hors du processus.
4. **Sauvegarde exportée** : si elle contient des données personnelles (identité, téléphone, historique), soit elle est chiffrée (ex. `androidx.security.crypto.EncryptedFile`), soit l'utilisateur est informé explicitement dans l'UI qu'elle ne l'est pas avant l'export (exigence à faire trancher avec le Product Owner, cf. Epic K du prompt fonctionnel).
5. **ProGuard/R8 actif en release**, avec règles réelles couvrant Room/Hilt/kotlinx.serialization.
6. **Aucune dépendance en version `SNAPSHOT` ou binaire committé** en dépendance de production.

---

## 7. Qualité, tests et definition of done

- **Pyramide de tests** : tests unitaires massifs sur `:domain` (en particulier §4, viser 100 % de couverture de branches sur l'algorithme de classement), tests d'intégration Room (`@Database` en mémoire) sur `:data`, tests Compose UI ciblés sur les écrans à logique conditionnelle complexe (formulaires en cascade, filtres).
- **CI** : lint + detekt/ktlint + tests unitaires + tests d'instrumentation bloquants sur chaque pull request, build release avec R8 activé vérifié régulièrement (pas seulement en fin de projet).
- **Revue de code systématique** avant fusion, avec attention particulière portée à toute modification touchant `:domain` (algorithme de classement) — exiger une justification et des tests mis à jour pour tout changement de comportement dans cette zone.
- **Accessibilité** : `contentDescription` sur tous les éléments interactifs non textuels, navigation clavier/TalkBack testée sur les parcours principaux (création de profil, saisie de score, consultation du classement).
- **Definition of Done** d'une fonctionnalité : code + tests unitaires associés + vérification manuelle du parcours + mise à jour de la documentation d'architecture si un nouveau module/pattern est introduit.

---

## 8. Point de départ recommandé pour l'implémentation

1. Poser le squelette multi-module et la configuration Gradle (version catalog, conventions de build partagées).
2. Implémenter `:domain` avec l'algorithme de classement (§4) et sa suite de tests exhaustive — **avant tout écran UI**, car c'est le risque technique et métier le plus élevé du projet.
3. Implémenter `:data` (schéma Room, migrations, repositories) en s'appuyant sur les interfaces définies par `:domain`.
4. Construire les écrans Compose feature par feature, en suivant les epics du prompt fonctionnel dans l'ordre : profil joueur → répertoire partenaires → invitations/score → classement/simulateur → intégrations (calendrier, contacts, QR code) → sauvegarde/export.
5. Intégrer les vérifications de sécurité (§6) et la CI qualité (§7) dès les premiers modules, pas en fin de projet.
