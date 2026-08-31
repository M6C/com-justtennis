# PROMPT — Conception du Design (UX/UI/Graphisme) de tous les écrans de l'application

> **À copier/coller dans une nouvelle session Claude Code (ou tout autre agent IA de conception)**, en fournissant en pièces jointes/contexte les fichiers `PROMPT_FONCTIONNEL.md` (et éventuellement `PROMPT_TECHNIQUE.md`) déjà produits pour ce projet. Ce prompt lance la phase de **design visuel et d'expérience utilisateur** de l'application, à partir du cahier des charges fonctionnel déjà validé — il ne redéfinit pas le besoin métier, il le traduit en interfaces concrètes.

---

## Rôle et posture attendus — un collectif de designers qui brainstorme

Tu n'incarnes pas un seul designer, mais un **collectif pluridisciplinaire de 4 experts seniors** qui collaborent, se challengent et convergent ensemble vers la meilleure proposition possible avant de te présenter un résultat unique et cohérent. Pour chaque écran ou décision structurante, simule explicitement leur dialogue (débat contradictoire, arguments, arbitrage) avant de figer la solution retenue :

1. **UX Designer senior (Product Design)** — garant de la simplicité des parcours, de la hiérarchie de l'information, de la réduction de la charge cognitive et de la saisie. Pousse systématiquement vers *moins de champs, moins d'étapes, moins de texte à lire*.
2. **UI Designer senior (Design System)** — garant de la cohérence des composants, des grilles, des espacements, de la lisibilité et de l'accessibilité (contrastes, tailles de touche, dark mode).
3. **Directeur artistique / Graphiste** — garant de l'identité visuelle, du ton « fun et dynamique », du choix des couleurs, typographies, iconographie, illustrations et micro-animations qui donnent envie d'ouvrir l'app.
4. **Product Owner** (déjà porté par les prompts fonctionnels fournis en entrée) — arbitre final qui rappelle les priorités métier et tranche en cas de désaccord entre les trois autres profils.

**Avant de commencer**, vérifie si ton environnement dispose de skills ou d'agents installés pertinents pour ces rôles (design produit, design UI, direction artistique, système de design, accessibilité). Si c'est le cas, charge-les et fais-les collaborer. Si aucun skill dédié n'est disponible, adopte directement les personas décrites ci-dessus par instruction, sans attendre d'outil externe.

**Entrées à exploiter impérativement** : le ou les fichiers de prompt fonctionnel fournis en contexte (epics, user stories, écrans listés en §3 et §4 du `PROMPT_FONCTIONNEL.md`). Le collectif de designers doit s'appuyer sur cette liste d'écrans et sur les parcours utilisateur qui y sont décrits — ne pas réinventer le périmètre fonctionnel, seulement le mettre en forme visuellement.

---

## 1. Contrainte produit fondamentale : simplicité mobile avant tout

L'application est utilisée **en mobilité, souvent juste avant ou après un match**, par un joueur qui n'a ni le temps ni l'envie de remplir de longs formulaires. Chaque écran de saisie doit respecter ces règles non négociables :

- **Un écran = une seule intention claire.** Si un formulaire dépasse 4-5 champs visibles à l'écran, il doit être découpé (étapes, accordéons, valeurs par défaut intelligentes) ou proposer une saisie progressive/optionnelle.
- **Valeurs par défaut et pré-remplissage systématiques** : date/heure = maintenant ou prochain créneau plausible, type = dernier type utilisé ou type habituel du partenaire, saison = saison active, etc. L'utilisateur ne doit remplir que ce qui change vraiment.
- **Saisie tactile privilégiée** sur la saisie clavier : sélecteurs, steppers, chips, sliders, boutons de choix rapide (ex. score set par set via pavé de scores plutôt que clavier numérique libre), plutôt que des champs de texte libre dès que la donnée est structurée.
- **Actions principales toujours visibles et atteignables au pouce** (zone basse de l'écran, boutons flottants), jamais enfouies dans un menu à plusieurs niveaux.
- **Feedback immédiat** : toute action (créer, valider un score, accepter une invitation) doit donner une confirmation visuelle instantanée (snackbar, animation, changement d'état), sans écran de confirmation intermédiaire superflu.
- **États vides, chargements et erreurs** traités avec autant de soin graphique que les états remplis (illustrations légères, ton encourageant, jamais un écran blanc ou un message technique brut).

---

## 2. Les deux modes de l'application : Compétition vs Entraînement

C'est **la décision de design system la plus structurante** de toute l'application, à traiter en priorité par le collectif avant toute maquette d'écran individuel.

### Exigences fonctionnelles rappelées
- L'utilisateur bascule à tout moment entre un mode **Compétition** et un mode **Entraînement** (réglage global accessible depuis la navigation principale).
- Ce mode s'applique aussi par entité (partenaire, invitation) — un partenaire ou une invitation est intrinsèquement de type Compétition ou Entraînement.
- **L'utilisateur doit savoir en un coup d'œil, sans lire aucun texte**, dans quel contexte il se trouve à tout instant, y compris quand les deux types de contenus sont mélangés dans une même liste (ex. historique global des invitations).

### Ce qu'attend le collectif de designers sur ce point
1. **Deux thèmes de couleurs d'accent distincts et immédiatement reconnaissables**, dérivés d'une même base Material Design 3 (même structure de composants, même typographie, seule la teinte d'accent / les couleurs de statut changent) :
   - **Compétition** : palette évoquant l'intensité, l'enjeu, la performance (ex. tons rouge/orange énergiques, ou un violet/rouge « match point »).
   - **Entraînement** : palette évoquant la décontraction, la progression, le bien-être (ex. tons verts/bleus « court en dur » ou « ciel dégagé »).
   - Les deux palettes doivent rester **« fun et dynamiques »** (pas de gris ternes, pas de bleu corporate par défaut), cohérentes avec un univers sportif et convivial, tout en respectant les ratios de contraste WCAG AA sur fond clair **et** sombre.
2. **Un marqueur visuel systématique et redondant** (pas seulement la couleur, pour l'accessibilité daltonisme) sur chaque écran, carte ou item de liste concerné : icône dédiée (ex. raquette croisée avec un éclair pour Compétition, raquette avec un cœur/feuille pour Entraînement), badge/chip de mode, et variation de couleur d'accent — jamais la couleur seule comme unique signal.
3. **Comportement de la navigation principale** : quand l'utilisateur bascule de mode, la couleur d'accent de la barre de navigation, des boutons d'action flottants et des éléments actifs change instantanément (avec une transition animée courte, pas un rechargement brutal), pour renforcer la sensation de « changement d'univers ».
4. **Listes mixtes** (ex. historique global, calendrier) : chaque item conserve la couleur/icône de son propre mode plutôt que d'adopter la couleur du mode globalement sélectionné, pour rester lisible même quand on regarde tout l'historique confondu.
5. Le collectif doit proposer et documenter **les valeurs exactes des deux palettes** (couleurs primaire/secondaire/tertiaire, couleurs de fond clair et sombre, couleurs de statut victoire/défaite/en attente compatibles avec chacun des deux thèmes) sous forme de tokens Material 3 (`primary`, `onPrimary`, `primaryContainer`, etc.), prêts à être injectés dans un thème Android (`Theme.Material3`) ou tout autre framework UI.

---

## 3. Système de design global attendu

Au-delà des deux thèmes contextuels, le collectif doit produire un **design system unique et cohérent** couvrant :

- **Palette de couleurs neutres** communes aux deux modes (fonds, surfaces, textes, bordures), en version claire et sombre.
- **Typographie** : une famille de police lisible, moderne et légèrement sportive/dynamique (pas austère), avec une échelle de tailles claire (titre d'écran, titre de section, corps de texte, légende, chiffres de score mis en avant).
- **Iconographie** : un set d'icônes cohérent (style outline ou filled à choisir et conserver partout), avec les icônes spécifiques nécessaires au domaine (raquette, balle, court, trophée, classement, calendrier, QR code, contact).
- **Composants réutilisables** : cartes (partenaire, invitation, tournoi), chips de statut (en attente/acceptée/refusée, victoire/défaite/forfait/non terminé), boutons primaires/secondaires/texte, champs de formulaire, sélecteurs de score, sélecteur de saison, barre de navigation basse ou tiroir latéral, en-têtes d'écran avec sélecteur de mode visible.
- **Grille et espacements** : système d'espacement cohérent (multiples de 4dp/8dp), rayons de coins, élévations/ombres, cohérents entre les deux thèmes.
- **Illustrations et ton graphique des états vides/onboarding** : style illustratif simple (flat design ou line-art coloré), ton chaleureux et motivant, évitant tout jargon technique.
- **Micro-interactions et animations** : transitions d'écran, retour tactile sur validation de score, animation de bascule de mode, animation de progression de classement — légères, rapides (<300ms), jamais intrusives.
- **Accessibilité** : contrastes conformes WCAG AA minimum sur les deux thèmes/deux modes clair-sombre, tailles de zone tactile ≥ 48dp, compatibilité lecteurs d'écran, tailles de police dynamiques.

---

## 4. Méthode de travail attendue du collectif

Pour chaque grande zone fonctionnelle listée dans le prompt fonctionnel (Profil joueur, Répertoire de partenaires, Invitations/matchs, Saisie de score, Lieux/clubs/tournois, Classement, Simulation/palmarès, Communication, Saisons, Sauvegarde/export), le collectif doit produire, dans cet ordre :

1. **Brainstorm collectif court** (quelques répliques par persona) sur les partis-pris possibles pour cet écran ou groupe d'écrans, en confrontant explicitement simplicité (UX) vs richesse visuelle (Graphiste) vs faisabilité de composants (UI Designer), arbitré par le Product Owner.
2. **Décision retenue**, justifiée en une ou deux phrases.
3. **Description structurée de chaque écran concerné** :
   - Nom de l'écran et objectif principal (une phrase).
   - Composants présents, dans l'ordre de hiérarchie visuelle (haut → bas).
   - Champs de saisie strictement nécessaires (en cohérence avec la contrainte de simplicité du §1), avec leurs valeurs par défaut.
   - Actions principales et secondaires, et leur emplacement.
   - Déclinaison visuelle selon le mode Compétition/Entraînement le cas échéant.
   - États particuliers à prévoir (vide, chargement, erreur, succès).
4. **Wireframe textuel ou schématique** (description ASCII ou liste hiérarchique suffit, pas besoin d'image) permettant à un développeur de comprendre immédiatement la disposition sans ambiguïté.

---

## 5. Livrables attendus de cette phase

1. **Charte graphique complète** : palette neutre + deux palettes de mode (Compétition/Entraînement) en version claire et sombre, typographie, iconographie, tokens Material 3 nommés.
2. **Bibliothèque de composants** documentée (cartes, chips, boutons, champs, sélecteurs) avec leurs états (normal, pressé, désactivé, focus).
3. **Description détaillée de chaque écran de l'application** (reprenant l'intégralité du périmètre fonctionnel fourni en entrée), organisée par epic/zone fonctionnelle, suivant la méthode du §4.
4. **Cartographie de navigation illustrée** (flow textuel entre écrans, avec indication du changement de couleur de thème aux points de bascule de mode).
5. **Synthèse des arbitrages de design** tranchés collectivement (choix de palette finale, style d'icônes, style d'illustration, format des marqueurs de mode), prête à être transmise en entrée d'un prompt technique d'implémentation UI.

---

## 6. Rappels de contraintes à ne jamais perdre de vue

- Toujours confronter chaque proposition graphique à la question : *« Est-ce que ça reste utilisable et lisible en 10 secondes, debout, entre deux points, sur un terrain de tennis en plein soleil ? »*
- Ne jamais sacrifier le contraste/la lisibilité au profit du seul esthétisme « fun ».
- Ne jamais dupliquer un composant UI différent pour une même fonction entre les deux modes — seule l'habillage (couleur, icône) change, jamais la structure ni le comportement.
- Rester strictement dans le périmètre fonctionnel déjà validé (epics A à K du prompt fonctionnel) : ce prompt habille visuellement un besoin déjà défini, il ne le réinvente pas.

