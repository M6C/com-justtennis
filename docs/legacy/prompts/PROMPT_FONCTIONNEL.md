# PROMPT — Spécification Fonctionnelle & UX/UI pour une nouvelle application Android de gestion de club de tennis

> **À copier/coller dans une nouvelle session Claude Code** pour lancer la phase de spécification fonctionnelle et UX/UI d'une application Android **entièrement nouvelle**. Ce prompt ne fait référence à aucune application existante : il décrit un besoin produit à concevoir depuis une page blanche.

---

## Rôle et posture attendus

Tu interviens ici avec une double casquette :

1. **Product Owner senior**, garant de la valeur métier, de la clarté du besoin utilisateur et de la priorisation.
2. **UI/UX Designer senior**, garant de la cohérence visuelle, de l'ergonomie mobile (Material Design 3) et de l'accessibilité.

**Avant de commencer**, vérifie si ton environnement Claude Code dispose de skills ou d'agents installés pertinents pour ces rôles (design produit, design UI/frontend, écriture de user stories, système de design). Si c'est le cas, charge-les et utilise-les. Si aucun skill dédié n'est disponible, adopte directement les deux personas décrites ci-dessus par instruction, sans attendre d'outil externe — ce prompt doit rester exploitable tel quel dans n'importe quel environnement.

Le livrable final attendu de cette phase est un **cahier des charges fonctionnel et UX complet** (backlog priorisé en epics/user stories avec critères d'acceptation, parcours utilisateurs, wireframes ou description d'écrans détaillée, système de design), qui servira ensuite de base à l'implémentation technique (traitée dans un prompt séparé, à vocation purement technique).

---

## 1. Vision produit

Concevoir une application mobile Android destinée aux **joueurs de tennis amateurs membres d'un club**, leur permettant de :
- gérer un répertoire de partenaires/adversaires de jeu,
- organiser des matchs de compétition ou des séances d'entraînement,
- suivre les scores et l'historique des rencontres,
- calculer un **classement interne au club**, propre à cette communauté (indépendant d'un classement fédéral officiel), basé sur un barème de points par victoire pondéré par le niveau de l'adversaire,
- simuler des scénarios de progression dans ce classement,
- synchroniser les rendez-vous avec le calendrier du téléphone,
- consulter des statistiques personnelles (palmarès, répartition victoires/défaites).

L'application doit fonctionner **hors-ligne en priorité** (les données du club vivent sur l'appareil), avec des mécanismes explicites d'export/sauvegarde et, à terme, de partage entre appareils.

---

## 2. Utilisateurs cibles

- **Le joueur** (utilisateur principal, unique par installation) : gère son propre profil, ses matchs, consulte son classement et ses statistiques.
- **Les partenaires/adversaires** : ne sont pas des utilisateurs de l'app (pas de compte), mais des fiches gérées par le joueur principal — un partenaire peut lui-même être un utilisateur d'une autre installation de l'app.
- Usage prévu en solo (chaque joueur a sa propre app, sa propre vision de son historique et de son classement personnel), pas un backend partagé multi-utilisateurs pour cette première version.

---

## 3. Périmètre fonctionnel — Epics & User Stories

> Chaque section ci-dessous renvoie vers des captures d'écran de l'app legacy, réparties entre
> mode **Entraînement** et mode **Compétition** (les 2 usages proposés à l'utilisateur, cf. Epic A
> et §4). La [galerie interactive](../screenshots/gallery.html) regroupe l'ensemble de ces
> captures avec un sélecteur Entraînement/Compétition qui bascule les images correspondantes tout
> en conservant le même ordre d'écrans dans les deux modes — plus pratique pour comparer visuellement
> les deux thèmes que les liens individuels ci-dessous.

### Epic A — Profil du joueur (utilisateur de l'app)
- En tant que joueur, je crée mon profil (prénom, nom, date de naissance, téléphone, localisation/adresse) lors du premier lancement de l'app (parcours d'onboarding obligatoire — l'app ne peut pas être utilisée sans profil créé).
- Je renseigne mon classement actuel (sur l'échelle du club, voir Epic F) et, optionnellement, un classement « estimé » différent (utilisé pour les simulations).
- Je précise le mode d'usage principal de mon profil : **Compétition** ou **Entraînement** (ce choix influence l'ensemble de la navigation et de l'identité visuelle de l'app, voir §5).
- Je peux modifier mon profil à tout moment.

> **Capture app legacy** (base vide, 1er lancement) :
> [ItemDetailActivity](../screenshots/training/ItemDetailActivity.jpg) ·
> [MainActivity](../screenshots/training/MainActivity.jpg) ·
> [UserActivity](../screenshots/training/UserActivity.jpg)
>
> **En mode Compétition** : [UserActivity](../screenshots/match/UserActivity.jpg)

### Epic B — Répertoire de partenaires/adversaires
- Je crée une fiche « joueur » pour chaque partenaire potentiel : identité, téléphone, classement, club/localisation, type d'usage habituel (compétition/entraînement).
- Je peux **importer une fiche directement depuis mes contacts téléphone** (sélection dans la liste des contacts, avec recherche texte, résolution du bon numéro si le contact en a plusieurs).
- Je peux **échanger une fiche joueur avec un autre téléphone via QR code** : mon profil (ou celui d'un partenaire) est encodé en QR code, affiché plein écran, et scanné par l'autre appareil pour préremplir automatiquement une nouvelle fiche partenaire — sans ressaisie manuelle.
- Je peux lister, rechercher (par nom, en live) et supprimer un partenaire — **la suppression est bloquée si ce partenaire a au moins un match enregistré** (intégrité de l'historique), avec message explicite à l'utilisateur.
- Un partenaire dont je ne connais pas encore l'identité complète peut être désigné comme **« adversaire inconnu »** au moment de créer un match (cas d'un tournoi où l'on ne connaît pas encore le nom de l'adversaire, à compléter plus tard).

> **Capture app legacy** — création (mode par défaut) :
> [PlayerActivity](../screenshots/training/PlayerActivity.jpg) ·
> [ListPersonActivity](../screenshots/training/ListPersonActivity.jpg) (import contacts) ·
> [QRCodeActivity](../screenshots/training/QRCodeActivity.jpg) (échange QR code)
>
> **Capture app legacy** — modification d'une fiche existante (bouton "QRCODE" visible au lieu de
> "Create") : [PlayerActivity (modifier)](../screenshots/training/PlayerActivity_Modify.jpg)
>
> **En mode Compétition** :
> [PlayerActivity](../screenshots/match/PlayerActivity.jpg) ·
> [PlayerActivity (modifier)](../screenshots/match/PlayerActivity_Modify.jpg) ·
> [QRCodeActivity](../screenshots/match/QRCodeActivity.jpg)
>
> **Bug corrigé** — `ListPlayerActivity` plantait systématiquement à l'ouverture
> (`IllegalArgumentException: No view found for id ... main_container`) : `onCreate()` attachait
> d'abord un fragment (jamais utilisé) au container de `AbsctractFragmentActivity`, puis
> remplaçait immédiatement tout le layout via `drawerManager.setContentView(R.layout.list_player)`,
> qui ne contient pas ce container. Fix : `createFragment()` retourne `null` pour cet écran (le
> fragment n'a jamais été nécessaire), et `AbsctractFragmentActivity` ignore désormais un fragment
> nul au lieu de planter.
>
> **Second bug corrigé dans la foulée** — même une fois le crash résolu, la liste restait vide
> malgré 9 joueurs en base : `onCreate()` ne câblait en réalité jamais le `ListView` de
> `list_player.xml` à un adapter (ni `business.initialize()`/`refreshData()` n'étaient appelés).
> Un adapter prêt à l'emploi (`ListPlayerAdapter`, compatible `ListView`) existait déjà mais
> n'était utilisé que par `ListPlayerFragment2` (code mort, cf. §2). Fix : câblage complet dans
> `ListPlayerActivity` (initialisation, rafraîchissement, adapter, clic sur un joueur → édition).
>
> **Capture app legacy avec des données** :
> [ListPlayerActivity](../screenshots/training/ListPlayerActivity.jpg) — les 9 joueurs générés par
> le seed s'affichent désormais correctement ·
> [en mode Compétition](../screenshots/match/ListPlayerActivity.jpg) ("PlayerC 0-9").
>
> **Bug de seed découvert en préparant les captures mode Compétition** — `DBFeedTool.feedPlayer()`/
> `feedInvite()` protègent leur génération par un compteur global (`getCount() <= 0`), implicitement
> filtré par le `TYPE` courant de `TypeManager`, à l'intérieur d'une boucle qui itère les 2 types
> `TRAINING`/`COMPETITION` **sans jamais basculer** ce type entre les deux passages : seul le
> premier type traité (`TRAINING`) obtient réellement des données, le second voit un compteur > 0
> (comptant les données Training) et ne crée jamais rien. En production, la base ne contient donc
> **aucun** joueur/invitation de type Compétition tant que l'utilisateur ne les crée pas lui-même.
> Non corrigé dans `DBFeedTool.java` (hors scope, outil de démo désactivé en prod via
> `ApplicationConfig.FEED_DATABASE=false`) — reproduit uniquement côté test pour ces captures.

### Epic C — Organisation d'un match ou d'un entraînement (« Invitation »)
- Je crée une **invitation** : je choisis un partenaire (ou « adversaire inconnu »), une date/heure, un type (**Compétition** ou **Entraînement**), un lieu (voir Epic E).
- Le type d'invitation hérite par défaut du type habituel du partenaire choisi, mais reste modifiable.
- Chaque invitation a un **statut d'acceptation** : en attente / acceptée / refusée par le partenaire — visualisé par un indicateur visuel (coche verte/rouge).
- Une invitation peut être envoyée au partenaire via un canal de partage (message pré-rempli à partir d'un modèle personnalisable, voir Epic H) ; le partenaire peut confirmer ou décliner.
- Je peux modifier ou supprimer une invitation existante à tout moment.
- Je consulte la liste de toutes mes invitations, triée par date décroissante, avec **filtres** : par partenaire, par type (tout/compétition/entraînement), par saison (voir Epic G).
- Les compétitions peuvent en plus être regroupées et consultées **par tournoi** dans une vue dédiée.

> **Capture app legacy** :
> [ListInviteActivity](../screenshots/training/ListInviteActivity.jpg) ·
> [ListCompetitionActivity](../screenshots/training/ListCompetitionActivity.jpg) ·
> [InviteActivity (création, adversaire inconnu)](../screenshots/training/InviteActivity_Create.jpg)
>
> **En mode Compétition** :
> [ListInviteActivity](../screenshots/match/ListInviteActivity.jpg) ·
> [ListCompetitionActivity](../screenshots/match/ListCompetitionActivity.jpg) ·
> [InviteActivity](../screenshots/match/InviteActivity_Create.jpg)
>
> *Note technique : `InviteActivity` plante si l'Intent de lancement n'a aucun extra
> (`Objects.requireNonNull(intent.getExtras())`), et plante aussi si un player est associé sans
> classement défini. La capture ci-dessus a été obtenue avec l'ID sentinelle "Unknown Player"
> (`-1`), exactement le mécanisme utilisé par l'app pour un match sans adversaire connu.*
>
> **Second bug de seed découvert (spécifique aux invitations Compétition)** —
> `DBInviteDataSource.customizeWhereSaison()` filtre les invitations par
> `ID_SAISON = <saison courante>` en correspondance **exacte**, contrairement au filtre côté
> Player/Person qui accepte `OR ID_SAISON IS NULL`. Une invitation créée sans saison explicitement
> renseignée est donc invisible dans toutes les listes, silencieusement (pas d'erreur, juste
> absente). À reproduire à l'identique côté DAO Room (comportement voulu ou bug ? — la saison étant
> obligatoire fonctionnellement sur une invitation réelle, ceci n'affecte que des données mal
> formées) mais à ne pas laisser piéger une future migration de données historiques sans saison.*

### Epic D — Saisie et suivi du score
- Pour une invitation passée, je saisis le score set par set (jusqu'à 5 sets), avec possibilité d'indiquer un **forfait** (walkover) en faveur de l'un ou l'autre joueur.
- Le résultat du match (victoire/défaite/non terminé/forfait) est **déterminé automatiquement** à partir du score saisi (dernier set déterminant), sans ressaisie manuelle du résultat.
- Le score est affiché de façon lisible dans les listes d'invitations (ex. « 6-4 / 3-6 / **7-5** », set gagnant mis en évidence).
- *Note produit : la version actuelle ne valide pas les scores selon les règles strictes du tennis (nombre de jeux, écart de 2, tie-break) — décision à confirmer explicitement avec le porteur produit avant de choisir entre reproduire une saisie libre ou implémenter une validation stricte des règles du tennis (voir §7, décisions ouvertes).*

> **Capture app legacy** : [ScoreActivity](../screenshots/training/ScoreActivity.jpg) ·
> [en mode Compétition](../screenshots/match/ScoreActivity.jpg)

### Epic E — Lieux : clubs, adresses, tournois
- Je gère un référentiel d'**adresses** (nom, ligne d'adresse, code postal, ville, coordonnées GPS), de **clubs** (rattachés à une adresse) et de **tournois** (rattachés à un club et à une saison).
- Ces trois référentiels sont imbriqués en cascade dans les formulaires : je peux créer un club à la volée depuis le formulaire de tournoi, une adresse à la volée depuis le formulaire de club.
- Je peux associer un lieu (adresse libre, club, ou tournoi) à chaque invitation, avec affichage de l'itinéraire via une application de cartographie.
- Une fonction de **recherche transverse** (club / tournoi / adresse) est disponible pour retrouver rapidement un lieu existant plutôt que d'en recréer un doublon.

> **Capture app legacy** — état sélecteur (spinner + bouton "+", avant saisie) :
> [LocationTournamentActivity](../screenshots/training/LocationTournamentActivity.jpg) ·
> [LocationClubActivity](../screenshots/training/LocationClubActivity.jpg) ·
> [LocationAddressActivity](../screenshots/training/LocationAddressActivity.jpg)
>
> **Capture app legacy** — formulaire de saisie (après clic sur "+", cascade Tournoi → Club →
> Adresse) :
> [LocationTournamentActivity (saisie)](../screenshots/training/LocationTournamentActivity_Add.jpg) ·
> [LocationClubActivity (saisie)](../screenshots/training/LocationClubActivity_Add.jpg) ·
> [LocationAddressActivity (saisie)](../screenshots/training/LocationAddressActivity_Add.jpg)

### Epic F — Système de classement du club (fonctionnalité cœur)

Le club utilise une échelle de classement interne à 19 niveaux, allant de **NC** (non classé) au niveau le plus élevé **1/6**, en passant par des paliers intermédiaires (`40`, `30/5`…`30/1`, `30`, `15/5`…`15/1`, `15`, `5/6`…`1/6`). Chaque niveau appartient à une **série** (regroupement de niveaux, 1 à 4) et définit :
- un **objectif de points** à atteindre pour valider ce niveau,
- un **nombre de victoires** à comptabiliser pour valider ce niveau.

**Règle métier de calcul du classement** (à faire valider/ajuster par le Product Owner puis figer avant implémentation technique) :

1. Pour chaque victoire enregistrée, le joueur gagne un nombre de points dépendant de l'écart entre son propre niveau et celui de l'adversaire battu : plus l'adversaire est mieux classé que le joueur, plus la victoire rapporte de points (barème allant de 15 points pour une victoire contre un adversaire nettement moins bien classé, jusqu'à 120 points pour une victoire contre un adversaire nettement mieux classé).
2. Seules les victoires contre des adversaires d'un niveau proche du sien (une fenêtre de tolérance autour de son propre niveau) comptent réellement pour valider le nombre de victoires requis ; les autres victoires restent visibles dans l'historique mais ne sont pas comptabilisées dans la progression.
3. Un système de **points bonus** manuels (attribuables à la discrétion du joueur lors de la saisie du match, ex. victoire remarquable) s'ajoute aux points de classement, avec un **plafond cumulé**.
4. Les **défaites contre des adversaires moins bien classés** pénalisent la progression (système de pénalité proportionnel à l'écart de classement), et peuvent réduire le nombre de victoires supplémentaires nécessaires pour valider le niveau suivant.
5. Le classement se recalcule sur l'ensemble de l'historique des matchs enregistrés (toutes périodes confondues), pas seulement sur la saison en cours — **point à confirmer explicitement avec le Product Owner** : veut-on conserver ce comportement « classement all-time » ou introduire un classement par saison ?

> Cette règle métier est la fonctionnalité la plus différenciante et la plus sensible de l'application : elle doit être formalisée avec le Product Owner sous forme de **user stories testables avec exemples chiffrés** (tableaux d'entrée/sortie), avant toute implémentation technique. Le prompt technique associé à ce document contient la spécification algorithmique complète à faire valider puis implémenter.

### Epic G — Simulation de classement / palmarès
- Un écran dédié me permet de **simuler** mon classement à partir de mon historique réel de victoires/défaites, avec possibilité de choisir un niveau cible différent de mon niveau actuel pour voir ce qu'il me faudrait accomplir.
- Un mode **« palmarès rapide »** me permet de saisir directement un nombre hypothétique de victoires/défaites par niveau d'adversaire (sans ressaisir chaque match), pour tester des scénarios (« si je bats 3 joueurs de tel niveau, où en suis-je ? »).
- Un écran **statistiques** affiche la répartition de mes matchs (victoires/défaites/non terminés) par niveau d'adversaire, sous forme de graphique, filtrable par type (compétition/entraînement) et par résultat.

> **Bug corrigé** — `ComputeRankingActivity` plantait systématiquement à l'ouverture
> (`ClassCastException: RecyclerView cannot be cast to ListView`) : le layout partagé
> `compute_ranking_list_invite.xml` avait été migré vers un `RecyclerView` pour l'écran moderne
> `ComputeRankingFragment` (atteignable depuis le drawer moderne, menu "My Palmares"), mais
> l'écran legacy `ComputeRankingActivity` utilise toujours un adapter de type `ArrayAdapter` +
> `ListView`. Fix : layout dédié `compute_ranking_list_invite_legacy.xml` (avec `ListView`) pour
> l'écran legacy, sans toucher au layout partagé ni casser le chemin moderne.
>
> **Bug corrigé** — `PieChartActivity` restait vide même avec des données réelles en base
> (contrairement à l'onglet Statistiques moderne, qui affichait la même donnée correctement) : son
> conteneur de graphique (`LinearLayout` avec `layout_height="0dip"` + `layout_weight="1"`, dans
> `pie_chart.xml`) était hébergé dans un `NestedScrollView`
> (`AbsctractFragmentActivity`/`abstract_fragment.xml`), qui mesure ses enfants en hauteur
> "illimitée" — un poids (`layout_weight`) n'a alors aucun effet, le graphique se retrouvait avec
> une hauteur de 0px quelle que soit la donnée injectée. Fix : hauteur fixe (`400dp`) sur ce
> conteneur dans `pie_chart.xml`, au lieu du poids. Sans effet de bord constaté sur l'onglet
> Statistiques moderne (partage le même layout).
>
> **Capture app legacy** (après corrections) :
> [PalmaresFastActivity](../screenshots/training/PalmaresFastActivity.jpg) (écran vierge) ·
> [PalmaresFastActivity (simulation)](../screenshots/training/PalmaresFastActivity_WithData.jpg)
> (720/210, 6/6, V-E-2I-5G:18 après saisie de victoires/défaites factices) ·
> [PieChartActivity (avec données)](../screenshots/training/PieChartActivity_WithData.jpg)
> (graphique peuplé, légende et contrôles de zoom visibles) ·
> [ComputeRankingActivity](../screenshots/training/ComputeRankingActivity.jpg) (170/210, 3/7,
> détail par joueur avec points)
>
> **En mode Compétition** (mêmes écrans, données "PlayerC 0-9") :
> [PalmaresFastActivity](../screenshots/match/PalmaresFastActivity.jpg) ·
> [PalmaresFastActivity (simulation)](../screenshots/match/PalmaresFastActivity_WithData.jpg) ·
> [PieChartActivity](../screenshots/match/PieChartActivity_WithData.jpg) ·
> [ComputeRankingActivity](../screenshots/match/ComputeRankingActivity.jpg)
> (480/210, 4/6 — résultat réellement différent de la simulation Entraînement ci-dessus, confirmant
> que le classement calculé dépend bien du jeu de données réel utilisé).
>
> *Anomalie distincte (non corrigée) — sur cette même capture `PieChartActivity`, chaque tranche
> semble dessinée deux fois, en miroir à 180° (invisible avec une seule catégorie à 100%, cf.
> capture par défaut plus haut). Cette fois reproduite sur les deux écrans (legacy et moderne) via
> exactement la même boucle `for (name : data.keySet()) addValue(name, ...)` que le vrai listener
> `onItemSelected` du Spinner en production — ce n'est donc probablement pas un artefact de la
> méthode de capture mais un vrai bug de rendu de la bibliothèque `achartengine` (2013, déjà
> recommandée au remplacement complet, cf. §11), à confirmer par un test manuel (sélectionner un
> Type/Result avec plusieurs classements) avant de le traiter comme définitivement avéré.*

### Epic H — Communication et partage
- Un **modèle de message** personnalisable (texte libre avec balises dynamiques : prénom/nom du partenaire, date, heure, date relative « aujourd'hui »/« demain », mon propre prénom/nom) sert de base à l'envoi d'invitations et de demandes d'ajout de partenaire.
- Le canal d'envoi effectif (SMS, ou tout autre canal de partage natif Android) est un point ouvert à trancher avec le Product Owner (voir §7) — le besoin fonctionnel (proposer un match, confirmer/refuser) doit rester indépendant du canal technique choisi.

> **Capture app legacy** : [MessageActivity](../screenshots/training/MessageActivity.jpg) ·
> [en mode Compétition](../screenshots/match/MessageActivity.jpg)

### Epic I — Saisons
- L'activité du club est organisée en **saisons** (période avec date de début/fin, une seule saison « active » à un instant donné).
- Les profils joueurs, invitations et tournois sont rattachés à une saison ; je peux changer la saison active depuis n'importe quel écran principal (sélecteur permanent), ce qui filtre dynamiquement les listes affichées (sauf le calcul de classement, qui reste transverse à toutes les saisons — voir Epic F point 5).
- Une nouvelle saison peut être créée par duplication d'une saison existante (reprise des joueurs actifs).

> **Capture app legacy** :
> [Drawer moderne ouvert](../screenshots/training/ItemDetailActivity_Drawer_Open.jpg) — le
> sélecteur de saison ("2025/2026") en en-tête, le menu (Utilisateur/SMS Message/Clubs/My
> Palmares/Fast Palmares), et en pied de page la bascule Entraînement/Compétition
> ("TRAINING"/"MATCH", cf. `TypeManager` déjà documenté au §4 "identité visuelle contextuelle") —
> ce composant transverse n'est normalement pas une destination de navigation propre, mais cette
> capture (à l'état ouvert, superposé à l'onglet Invitations) illustre bien son intégration réelle.

### Epic J — Synchronisation calendrier
- Chaque invitation planifiée crée automatiquement un **événement dans le calendrier natif du téléphone** (titre incluant le partenaire et le type de match, lieu, rappel avant l'événement).
- Toute modification de l'invitation (date, statut d'acceptation, suppression) met à jour ou supprime l'événement calendrier correspondant, pour rester toujours synchronisé.

> **Capture app legacy** : aucune — effet de bord déclenché depuis InviteActivity, pas un écran
> en soi.

### Epic K — Sauvegarde, restauration et export des données
- Je peux **sauvegarder** l'intégralité de mes données (profil, partenaires, invitations, scores, référentiels) vers un emplacement de stockage du téléphone, et les **restaurer** ultérieurement (changement de téléphone, réinstallation).
- Je peux **partager/exporter** cette sauvegarde vers un autre support (email, messagerie, etc.).
- *Exigence non-fonctionnelle forte : ce mécanisme doit être chiffré ou à défaut clairement présenté comme non chiffré à l'utilisateur — voir prompt technique pour les implications de sécurité.*

> **Capture app legacy** : aucune — actions du menu overflow de MainActivity (backup/restore/
> partage DB), pas une Activity distincte.

---

## 4. Exigences UX/UI

- **Material Design 3**, thème clair/sombre, respect des tailles de zones tactiles et de l'accessibilité (contrastes, TalkBack, tailles de police dynamiques).
- **Identité visuelle contextuelle** : la bascule Compétition/Entraînement (réglage global, modifiable à tout instant depuis la navigation principale) doit changer visuellement l'application (couleur d'accent différenciée) pour que l'utilisateur sache toujours dans quel contexte il se trouve.

  > **Capture app legacy** (même écran, thème selon `TypeManager.TYPE`) :
  > [Entraînement — bleu, thème par défaut](../screenshots/training/MainActivity.jpg) ·
  > [Compétition — orange](../screenshots/match/MainActivity.jpg)
  >
  > **Capture app moderne** — même bascule via les boutons "TRAINING"/"MATCH" en pied du drawer
  > (capturée en conditions réelles sur device, pas via injection de test) :
  > [Match/Compétition — rose/magenta](../screenshots/match/ItemDetailActivity_Drawer_Open.jpg)
  > (thème, photo d'en-tête et bandeau différents de la version legacy — palette distincte, pas la
  > même couleur "Compétition" que côté legacy) ; on y voit aussi un état vide non traduit ("...
  > this time.", probablement une chaîne anglaise du type "No invitation to show at this time" côté
  > liste Invitations, à vérifier) et le drawer moderne ouvert (cf. Epic I).
- **Navigation principale** à définir : proposer une structure moderne (barre de navigation basse ou navigation par tiroir latéral) organisée autour de 3 grands pôles : *Joueurs* (répertoire + profil), *Invitations* (matchs/entraînements, création, historique), *Classement & Statistiques* (classement, simulateur, palmarès).

  > **Capture app legacy** — l'écran moderne `ItemDetailActivity` a déjà cette structure à 3
  > onglets (barre de navigation basse), avec données de démonstration (profil + 9 partenaires +
  > invitations générés via le mécanisme de seed interne de l'app, `DBFeedTool`) :
  > [Onglet Invitations](../screenshots/training/ItemDetailActivity_Tab_Invite.jpg) ·
  > [Onglet Joueurs](../screenshots/training/ItemDetailActivity_Tab_Player.jpg)
  >
  > **En mode Compétition** (mêmes onglets, données "PlayerC 0-9", thème rose/magenta) :
  > [Onglet Invitations](../screenshots/match/ItemDetailActivity_Tab_Invite.jpg) ·
  > [Onglet Joueurs](../screenshots/match/ItemDetailActivity_Tab_Player.jpg) ·
  > [Onglet Statistiques](../screenshots/match/ItemDetailActivity_Tab_Statistic_Rich.jpg)
  >
  > **Onglet Statistiques avec plusieurs classements/résultats représentés** :
  > [Onglet Statistiques (variété)](../screenshots/training/ItemDetailActivity_Tab_Statistic_Rich.jpg)
  > — 5 classements différents (30/4, 30/3, 30/1, 30, 15/5), obtenue en corrigeant au préalable une
  > lacune de données du seed de test (voir note ci-dessous) plutôt qu'en fabriquant un graphique
  > factice. **Anomalie visuelle** : chaque tranche semble dessinée deux fois, en miroir à 180°
  > (invisible avec une seule catégorie à 100%, comme dans la capture par défaut ci-dessus) —
  > reproduite à l'identique sur `PieChartActivity` (cf. Epic G) via exactement la même boucle que
  > le vrai listener `onItemSelected` en production, donc probablement un vrai bug `achartengine`
  > (2013, déjà recommandée au remplacement complet, cf. §11) plutôt qu'un artefact de capture — à
  > confirmer par un test manuel avant de le traiter comme définitivement avéré.
  >
  > *Note sur la qualité des données de test : les invites générées par `DBFeedTool` pour un
  > adversaire connu (hors "Unknown Player") ne reçoivent jamais de `idRanking` à la création — ce
  > champ n'est renseigné qu'à l'ouverture individuelle de l'invite dans `InviteActivity`
  > (`InviteBusiness.initializeInvite()`). Résultat : le graphique (qui groupe par
  > `ID_RANKING`) n'avait quasiment aucune donnée exploitable sur les invites "fraîches" du seed
  > initial, d'où la tranche unique "15/5" à 100% vue plus haut. Un backfill (classement réel/estimé
  > du partenaire, comme le ferait l'app) a été appliqué aux invites existantes pour cette capture.*
- **Sélecteur de saison** toujours accessible depuis la navigation principale (pas enfoui dans un sous-menu).
- **États vides** soignés pour chaque liste (aucun partenaire, aucune invitation, etc.) avec appel à l'action clair.
- **Parcours d'onboarding** guidé et minimal à la première ouverture (création du profil joueur obligatoire avant tout accès au reste de l'app).
- **Formulaires en cascade** (adresse → club → tournoi) doivent permettre la création d'une entité liée sans quitter le formulaire parent (pattern « ajout à la volée »).
- Prévoir des **maquettes ou wireframes décrits par écran** (liste des composants, hiérarchie d'information, actions principales/secondaires) pour chaque écran des epics A à K.

---

## 5. Exigences non fonctionnelles

- **Fonctionnement hors-ligne** total pour toutes les fonctionnalités cœur (aucune dépendance à un backend distant pour l'usage quotidien).
- **Internationalisation** : français et anglais au minimum, avec gestion propre des formats de date localisés.
- **Permissions** : accès aux contacts, au calendrier, à la caméra (scan QR code) et au stockage — chacune doit être demandée au moment précis où elle est nécessaire (jamais en bloc à l'ouverture), avec message explicatif si l'utilisateur refuse une première fois.
- **Confidentialité des données** : toutes les données (profils, historique de matchs) sont strictement locales à l'appareil sauf action explicite de sauvegarde/partage par l'utilisateur.
- **Performance perçue** : listes fluides même avec plusieurs saisons/centaines d'invitations cumulées.

---

## 6. Hors périmètre de cette v1 (à ne pas développer sans validation)

- Compte utilisateur en ligne / synchronisation cloud multi-appareils temps réel.
- Classement partagé entre plusieurs utilisateurs de l'app (chaque installation reste centrée sur son propre joueur).
- Paiement, réservation de court, ou toute fonctionnalité de billetterie.
- Écrans de l'app legacy sans user story associée, à ne pas reprendre :
  [RotatingButtons](../screenshots/training/RotatingButtons.jpg) (démo technique isolée, sans
  lien métier) et `MatchActivity` (écran quasi vestige : template Android Studio "Navigation
  Drawer Activity" jamais terminé — le code peuplant son conteneur de contenu est en commentaire
  dans le source, fonctionnalité déjà couverte par ListInviteActivity/InviteActivity ; pas de
  capture ici, l'écran n'affiche littéralement rien).

---

## 7. Décisions produit ouvertes (à trancher AVANT de figer le backlog)

1. **Canal d'envoi des invitations/demandes d'ajout** : SMS natif, partage générique Android (« Envoyer via… »), notification push si compte en ligne introduit plus tard, ou combinaison ? Impacte directement la faisabilité du workflow de confirmation automatique (Epic C).
2. **Portée temporelle du classement** : classement calculé sur tout l'historique (« all-time ») ou remis à zéro/recalculé par saison ?
3. **Barème « femme » séparé** : le référentiel de niveaux prévoit historiquement un objectif de points et un nombre de victoires différenciés par genre, mais le calcul n'exploite aujourd'hui qu'un seul barème. Faut-il introduire un vrai mode différencié, ou assumer un barème unique ?
4. **Rigueur de la validation du score** : saisie libre pilotée par le seul dernier set renseigné (comportement actuel du barème existant), ou véritable moteur de règles du tennis (sets, jeux, tie-break, cohérence globale) ?
5. **Partage/synchronisation de la base entre plusieurs appareils** (ex. entre deux joueurs du club) : fonctionnalité à concevoir proprement (avec un vrai mécanisme sécurisé) ou à abandonner au profit d'exports simples ?

---

## 8. Livrables attendus de cette phase

1. Backlog priorisé (epics → user stories avec critères d'acceptation Given/When/Then) couvrant les epics A à K.
2. Système de design (palette, typographie, composants réutilisables, thème Compétition vs Entraînement).
3. Description détaillée de chaque écran (wireframe ou description structurée : composants, actions, états).
4. Cartographie de navigation (flow entre écrans).
5. Document de synthèse des décisions produit tranchées sur les points ouverts du §7, à transmettre en entrée du prompt technique.
