# PROMPT — Navigation et enchaînement des écrans (app legacy JustTennis)

> À copier/coller dans une nouvelle session Claude Code pour donner le contexte de navigation de
> l'app legacy avant de concevoir la navigation de la nouvelle application. Chaque écran est lié à
> une capture réelle (mode Entraînement) dans `docs/legacy/screenshots/` — la
> [galerie interactive](../screenshots/gallery.html) permet de basculer en mode Compétition.

## Entrée dans l'app

- **Moderne (LAUNCHER réel)** : [ItemDetailActivity](../screenshots/training/ItemDetailActivity.jpg)
  → onboarding (création du profil) au 1er lancement → 3 onglets en bas :
  [Joueurs](../screenshots/training/ItemDetailActivity_Tab_Player.jpg),
  [Invitations](../screenshots/training/ItemDetailActivity_Tab_Invite.jpg),
  [Statistiques](../screenshots/training/ItemDetailActivity_Tab_Statistic_Rich.jpg)
  + un [tiroir de navigation](../screenshots/training/ItemDetailActivity_Drawer_Open.jpg)
  (saison, bascule Entraînement/Compétition, accès aux écrans ci-dessous).
- **Legacy** (menu "Ancienne IHM" depuis le tiroir moderne) :
  [MainActivity](../screenshots/training/MainActivity.jpg) — menu complet, point d'entrée de
  tout l'enchaînement décrit ci-dessous.

## Enchaînement des écrans (depuis MainActivity)

- **Mon profil** : MainActivity → [UserActivity](../screenshots/training/UserActivity.jpg)
  (fiche du joueur = utilisateur courant) →
  [QRCodeActivity](../screenshots/training/QRCodeActivity.jpg) (partage de la fiche)

- **Répertoire de partenaires** : MainActivity →
  [ListPlayerActivity](../screenshots/training/ListPlayerActivity.jpg) →
  - [PlayerActivity](../screenshots/training/PlayerActivity.jpg) (création) ou
    [PlayerActivity (modifier)](../screenshots/training/PlayerActivity_Modify.jpg)
  - → [ListPersonActivity](../screenshots/training/ListPersonActivity.jpg) (import depuis les
    contacts du téléphone)
  - → QRCodeActivity (échange de fiche par QR code)

- **Invitations (matchs/entraînements)** : MainActivity →
  [ListInviteActivity](../screenshots/training/ListInviteActivity.jpg) →
  [InviteActivity](../screenshots/training/InviteActivity_Create.jpg) (création/détail) →
  [ScoreActivity](../screenshots/training/ScoreActivity.jpg) (saisie du score, retour à
  InviteActivity)

- **Compétitions par tournoi** : MainActivity/ListInviteActivity →
  [ListCompetitionActivity](../screenshots/training/ListCompetitionActivity.jpg) →
  [ComputeRankingActivity](../screenshots/training/ComputeRankingActivity.jpg) (calcul du
  classement à partir de l'historique réel)

- **Simulation de classement** : MainActivity →
  [PalmaresFastActivity](../screenshots/training/PalmaresFastActivity.jpg) → saisie manuelle de
  victoires/défaites par niveau →
  [résultat simulé](../screenshots/training/PalmaresFastActivity_WithData.jpg)

- **Statistiques** : MainActivity →
  [PieChartActivity](../screenshots/training/PieChartActivity_WithData.jpg) (camembert des
  invitations par classement)

- **Lieux** (depuis PlayerActivity ou InviteActivity) : cascade
  [Tournoi](../screenshots/training/LocationTournamentActivity.jpg) →
  [Club](../screenshots/training/LocationClubActivity.jpg) →
  [Adresse](../screenshots/training/LocationAddressActivity.jpg), chacun avec un formulaire de
  saisie accessible via le bouton "+" :
  [saisie Tournoi](../screenshots/training/LocationTournamentActivity_Add.jpg),
  [saisie Club](../screenshots/training/LocationClubActivity_Add.jpg),
  [saisie Adresse](../screenshots/training/LocationAddressActivity_Add.jpg)

- **Message** : MainActivity → [MessageActivity](../screenshots/training/MessageActivity.jpg)
  (édition du modèle de SMS commun)

## Écrans hors flux principal

- [RotatingButtons](../screenshots/training/RotatingButtons.jpg) : démo technique isolée, sans
  lien avec le reste de l'app — à ne pas reprendre dans la réécriture.
- `MatchActivity` : écran quasi vestige (drawer saison seul, aucune donnée affichée) — non
  capturé, à ne pas reprendre.

## Consigne

À partir de cet enchaînement, propose une structure de navigation moderne (Navigation Compose ou
équivalent) qui couvre les mêmes destinations et les mêmes points d'entrée, sans reproduire les 2
présentations parallèles (legacy/moderne) de l'existant — une seule arborescence de navigation.
