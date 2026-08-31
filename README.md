Build / Environnement de developpement
=======================================

Ce projet est fige sur une stack ancienne (AGP 3.2.1 / Gradle 4.6, compileSdk 27)
qui ne fonctionne plus telle quelle sur un poste de dev moderne sans quelques
ajustements. Voir aussi CLAUDE.md pour le contexte fonctionnel complet.

Documentation (reecriture) - `docs/legacy/`
--------------------------------------------
- `docs/legacy/prompts/` - les 3 prompts de reecriture a copier/coller dans une
  nouvelle session Claude Code : `PROMPT_FONCTIONNEL.md` (cahier des charges
  fonctionnel/UX), `PROMPT_TECHNIQUE.md` (conception technique/implementation)
  et `PROMPT_DESIGN.md` (design visuel), a executer dans cet ordre.
- `docs/legacy/screenshots/` - galerie de captures d'ecran de l'app legacy
  (`gallery.html`, a ouvrir directement dans un navigateur), avec un
  selecteur Entrainement/Competition qui bascule entre les sous-dossiers
  `training/` et `match/` (memes noms de fichiers dans les deux, pour
  comparer visuellement les 2 themes). `PROMPT_FONCTIONNEL.md` renvoie vers
  ces captures pour illustrer chaque Epic du backlog.

Pourquoi Gradle 4.6 ne compile plus "out of the box"
-----------------------------------------------------
- Gradle 4.6 ne supporte que Java 8. Sur une machine avec Java 11/17 par
  defaut (ou un JBR fourni par Android Studio), il plante immediatement avec
  `Could not determine java version from '...'`.
- JCenter (jcenter.bintray.com) et Fabric (maven.fabric.io) ont ete fermes
  respectivement en 2021 et 2020. Les plugins/dependances suivants ont ete
  retires du projet car definitivement introuvables :
  - `io.fabric.tools:gradle` / `com.crashlytics.sdk.android:crashlytics`
    (Crashlytics) - 42 appels `Crashlytics.log(...)` supprimes du code source,
    le logging applicatif interne (`Logger.logMe`) reste actif.
  - `com.github.triplet.gradle:play-publisher:2.0.0-SNAPSHOT` (auto-publish
    Play Store).
  - `com.google.android:flexbox:0.2.3` - remplace par un `LinearLayout`
    classique dans les 2 layouts qui l'utilisaient (list_player_row.xml,
    element_invite.xml), usage XML pur sans code Java associe.
- Import residuel `androidx.databinding.DataBindingUtil` dans 2 fichiers
  (ClubFragment.java, ScoreFragment.java) alors que le reste du projet est
  reste sur `android.databinding.*` (migration AndroidX jamais terminee,
  voir CLAUDE.md §10.3) - corrige en `android.databinding.DataBindingUtil`.

Compiler / installer en ligne de commande
-------------------------------------------
Utiliser `./gradlew-local` (et non `./gradlew` directement) : il force un JDK 8
et un `GRADLE_USER_HOME` dedie (`~/.gradle-justtennis`), car la config Gradle
globale de la machine (`~/.gradle/gradle.properties`, partagee avec d'autres
projets) pointe `org.gradle.java.home` vers le JBR (Java 17) d'Android Studio,
incompatible avec Gradle 4.6.

    ./gradlew-local assembleDebug     # build seul
    ./gradlew-local installDebug      # build + installe sur le device/emulateur branche
    ./gradlew-local clean             # etc. - tous les tasks Gradle habituels
    adb shell am start -n com.justtennis/.ui.activity.ItemDetailActivity

`gradlew-local` n'est pas portable tel quel (chemin JDK code en dur pour cette
machine) - a adapter si utilise sur un autre poste.

Signing config
--------------
`app/build.gradle` lit un fichier local (non versionne) via la propriete
`signingConfigsFile` (definie dans le `gradle.properties` du projet, lui aussi
non versionne) pour signer le build release. En l'absence du vrai keystore de
prod, un `app/signing.properties` + `app/placeholder-release.keystore`
factices ont ete generes pour permettre au moins la compilation/le sync.
**Un `assembleRelease`/`bundleRelease` avec ce placeholder ne produit pas un
APK/AAB publiable sur le Play Store.**

Sync Android Studio
--------------------
Le sync Gradle des IDE recents (ex: Android Studio 2026.1.x) echoue avec
`Could not determine the class-path for class com.android.Version` /
`java.lang.IllegalArgumentException` dans `org.objectweb.asm.ClassReader`.
Cause : l'ASM embarque dans Gradle 4.6 (2018) ne sait pas parser ce dont a
besoin le protocole Tooling API d'un IDE aussi recent lors de l'extraction du
modele de projet - un build en ligne de commande (`gradlew`) n'utilise pas ce
mecanisme et fonctionne normalement. A ce jour, pas de fix trouve cote
config projet ; options possibles si le sync IDE est necessaire :
- ouvrir le projet avec une version plus ancienne d'Android Studio
  (`~/bin/android-studio_giraffe`, 2022.3.1, non testee) ;
- ou upgrader AGP/Gradle par paliers (3.2.1 -> 4.x -> 7.x -> 8.x en lockstep
  avec Gradle, migration AndroidX, remontee compileSdk/targetSdk) - chantier
  separe, plus consequent.
En attendant, editer dans l'IDE (sans autocompletion/bouton Run) et
compiler/installer via `./gradlew-local` fonctionne sans probleme.

To Do

* Fix - Tournement voir le probl�me avec la selection de saison
Fix - Revoir la police des combo Ranking (Invite/Player)
Evol- Ajouter un champ de rechche au lieu de la combo pour filtrer la liste de Player
Evol- Pouvoir cr�� un Player par duplication d'un ancien en changeant la saison
Evol- Pouvoir changer des proprietes � plusieurs Player/Invite. Trouver un moyen de faire une dialog generic.
Evol- Cr�er un ecran pour gerer les Tournement/Club/Address (Menu Overflow de MainActivity ?)

 - Faire un fichier strings.xml par �cran
 
Ecran Accueil

Ecran Utilisateur
 - Ajout d'un calendrier pour selectionner la date de naissance

Ecran Player
 - Ajout d'un calendrier pour selectionner la date de naissance
 - Ajout du club
 - Pouvoir importer une selection multiple de contacts google

Invite
 - Saisir une adresse

Invite Demande

List Person

List Invite

Saisie de Message


********************* DONE *****************************
Invite
[20141124] - Revoir quel classement est utilis� pour la cr�ation d'une invite
[20141003] - Ajout notion de Saison
[20140508] - Affichage de l'itineraire via GoogleMap
[20140302] - D�but ajout de la notion d'adresse, club et tournoi
[20140301] - Ajout des champs de saisie pour l'adresse
[20140301] - Suppression de la saisie du status et clean de code
[20140226] - Utilisation d'un switch pour le type d'invite
[20140223] - Recr�� l'�venement calendar de l'Invite quelque soit la donn�e modifi�e
[20140221] - Fix - NPE lors du changement du Player
[20140220] - Fix - UnkownPlayer sans classement
[20140217] - Revu du Design
[20140216] - Fix Modifier
[20140216] - Fix Adapter
[20140216] - Ajout de la notion de victoire, defaite ou indefini
[20140212] - Ajout d'un TextWatcher sur les champs du score pour mettre en Bold les champs avec une valeur superieur � la valeur de l'autre champ du m�me set
[20140211] - Pouvoir saisir une Score
[20140209] - Modifier l'event GCalendar quand on modifi une invite
[20140209] - Supprimer l'invite GCalendar quand on supprime une invite
[20140122] - Pouvoir modifier la coche qui indique si la r�ponse et ok (vert) ou non (rouge)
[20131226] - Pouvoir modifier le joueur
[20131224] - Donner la possibilit� de ne pas envoyer demande de confirmation pour les joueurs qui n'ont pas l'appli. Mais cr�� quant m�me l'�v�nement GCalendar.
[20131224] - Ne pas selectionner d'adversaire pour les matchs
[20131224] - Ne pas envoyer de sms pour les matchs ou entrainements sans adversaire
[20131221] - Gerer une date relative pour aujourd'hui et demain
[20131221] - Sauvegarder le type d'invite en base
[20131221] - Gerer l'affichage du type d'invite dans la liste

List Player
[20141124] - Cacher 'Unknow Player'
[20141004] - Ajout notion de Saison
[20140507] - Affichage de la location de chaque player
[20140222] - Emp�che la suppression d'un joueur si il a au moins une invite
[20140216] - Sort Player List
[20140212] - Ajout d'un bouton Close

Ecran Utilisateur
[20141124] - Cacher Match/Entrainement dans UserActivity
[20141123] - Ajout notion de Saison
[20140513] - Recupertion du code pour pouvoir selectionner une adresse differente du club
[20140511] - Pouvoir selectionner une adresse differente du club
[20140413] - Saisie de la localisation
[20140214] - Integration de la gestion du Message dans l'�cran User
[20140206] - Revu du design
[20140105] - Ajout du classement

Ecran Player
[20141004] - Ajout notion de Saison
[20140512] - Fix sur le onSaveInstanceState
[20140507] - Fix sur l'import d'un player
[20140430] - Fix sur la selection de la localisation
[20140414] - Fix sur la creation d'un player
[20140413] - Saisie de la localisation
[20140223] - Ajout la notion de type de Player
[20140206] - Revu du design
[20140110] - Trier les invites par date descendante
[20140109] - Afficher la liste des invites en mode modification
[20140108] - Ajout du classement
[20131229] - Passer par un startActivityForResult pour importer un contact google (comme le scan QrCode)
[20131228] - Recuperer les infos 'Prenom' et 'Nom' d�coup�

List Invite
[20141004] - Ajout notion de Saison
[20140508] - Affichage de la location et classement de chaque invite
[20140223] - Possibilit� de filtre les Invite par le type
[20140216] - Revu du Design
[20140216] - Fixe Affichage du score
[20140216] - Affichage du score
[20140208] - Ajouter une filtre tous, pour filtrer les invites
[20140208] - Filtrer les invites par Player
[20140110] - Trier les invites par date descendante
[20131225] - Pouvoir modifier une invite
[20131221] - Gerer l'affichage du type d'invite

Ecran Calcul Classement
[20140914] - Modification du classement estim� d'une invite et recalcul du classement
[20140812] - Classement de simulation param�trable + divers fix
[20140622] - Creation de l'�cran

Ecran List Competition
* [20140907] - Affichage des competitions sans Tournament
[20140605] - Creation de l'�cran

Ecran Calcul Classement
[20140812] - Classement de simulation param�trable + divers fix
[20140622] - Creation de l'�cran

Common
[20140616] - Calcul des points par match par rapport au classement estimer
[20140615] - Affichage du palmares avec le calcul des points par match par rapport au classement
[20140614] - Calcul de des points par match pour le classement
[20140529] - Remplace le type 'MATCH' par 'COMPETITION' Fix
[20140529] - Remplace le type 'ENTRAINEMENT' par 'TRAINING'
[20140529] - Remplace le type 'MATCH' par 'COMPETITION'
[20140528] - Changement du style dynamiquement en fonction du type selectionn�.
[20140526] - Initialise le fond des activity en fonction du type de match selectionn�.
[20140525] - Rend parametrable les requ�tes en fonction du type de match selectionn� sur l'accueil
[20140309] - Creation d'un custom spinner pour armoniser le design avec les champs de saisie

Ecran Accueil
[20140529] - Comportement specifique pour le bouton Match
[20140525] - Change le background en fonction du type
[20140525] - Modification graphique de l'affiche du type
[20140226] - Ajout d'une confirmation pour le partage de l'application
[20140226] - Backup & Restore. Ajout des DBHelper manquant
[20140225] - Partage l'application sans faire de copie sur la sd
[20140223] - Ajout la possibilit� de partager l'application
[20140216] - Ajout Menu Statistique
[20140214] - Clean le menu overflow
[20140109] - Ajouter un menu pour sauvegarder la base 
[20140109] - Ajouter un menu pour restorer la base 

Ecran Utilisateur
[20140513] - Recupertion du code pour pouvoir selectionner une adresse differente du club
[20140511] - Pouvoir selectionner une adresse differente du club
[20140413] - Saisie de la localisation
[20140214] - Integration de la gestion du Message dans l'�cran User
[20140206] - Revu du design
[20140105] - Ajout du classement

Ecran Player & User
[20140512] - Utilisation de l'ecran Player pour l'ecran User
[20140207] - TextWatcher sur les EditText qui rend visible les TextView si non vide.
[20140207] - Mise en commun des champs de saisie avec le User

Invite Location
[20140502] - Pouvoir selectionner un club specifiquement pour une invite
[20140502] - Fix sur l'affichage de la liste des sous elements
[20140410] - Adapte l'affichage quant le clavier s'affiche
[20140409] - Fix Grise l'item empty des spinners
[20140409] - Fix Selection des sous element apr�s cr�ation ou suppression
[20140406] - Refactor Ecran location
[20140325] - Affichage de l'ecran location en mode ajout si on clique sur add du formulaire
[20140324] - Ajout d'ecrans generic
[20140313] - Utilisation dans l'�cran d'invite (Reste encore des bug)
[20140306] - Cr�ation de l'�cran

Invite Demande
[20140413] - Initialisation de la localisation pour les Matchs
[20140413] - Initialisation de la localisation pour les entrainements
[20140303] - Modification design de la notion d'adresse, club et tournoi
[20140302] - Clean
[20140226] - Utilisation d'un switch pour le type d'invite
[20140223] - Ne cr�� pas d'�venement calendar pour les Invites pass�es
[20140222] - Possibilit� de cr�� un nouveau Player si on a selectionn� "Unknow Player"
[20140222] - Possibilit� de saisir le classement
[20140211] - Creation d'une nouvelle activity pour faire seulement des invites

Statistique
[20140223] - Ne prend en compte que les matchs pass�s
[20140216] - Refactor
[20140213] - Revu du Design
[20140213] - Invite by Ranking ordred
[20140212] - Ajout Activity Chart

Saisie de Message
[20131229] - Pouvoir inserer le champ � la position courante du curseur

List Person
[20131226] - Ajout d'un champ de recherche pour l'iport Google

Saisie de Message
[20131221] - Pouvoir ajouter une champ via une dialog
