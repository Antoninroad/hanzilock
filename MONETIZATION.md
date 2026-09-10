# HanziLock — modèle gratuit / payant & offre de lancement

## Principe

Le gratuit doit suffire à **prendre l'habitude** (widget + révisions quotidiennes sur un
socle de vocabulaire), le payant débloque **le volume et la profondeur** (tout le HSK,
illimité, suivi détaillé). C'est le modèle de Daily Hanzi, HelloChinese, Du Chinese.

## Gratuit (sans abonnement)

| Fonction | Détail |
|---|---|
| Vocabulaire | **HSK 1 et 2** (~300 mots) — de quoi tenir 3-4 semaines |
| Widget écran d'accueil | Caractère du jour, toujours dispo |
| Révisions / jour | **20** (largement de quoi progresser ; au-delà → Pro) |
| Progression | Compteur global « X / total » |
| Thème clair / sombre | Oui |
| Rappel quotidien | Oui (sert la rétention, donc gratuit) |
| Publicité | **Aucune**, jamais |

## HanziLock Pro (abonnement)

| Fonction | Détail |
|---|---|
| Vocabulaire | **HSK 1 à 6 complet** (~5000 mots) — le vrai argument |
| Révisions / jour | **Illimitées** + réglage de la limite |
| Progression détaillée | Répartition par niveau, série (streak), mots maîtrisés par thème |
| Priorité nouveautés | Widget « phrase d'exemple », packs thématiques à venir |

## Prix (modifiables dans la Play Console / App Store Connect)

| Offre | Prix | Équivalent | Rôle |
|---|---|---|---|
| Mensuel | **4,99 €** | — | entrée, faible engagement |
| **Annuel** | **29,99 €** | 2,50 €/mois (**-50 %**) | l'offre à pousser (mise en avant) |
| À vie | **49,99 €** (achat unique) | — | capte ceux qui refusent l'abonnement |

- **Essai gratuit de 7 jours** sur l'annuel (double typiquement le taux de conversion).
- Une seule zone de prix au départ (Europe), Apple/Google convertissent le reste.

## Offre de lancement (3 premiers mois)

> **« Offre de lancement : -40 % sur HanziLock Pro annuel — 17,99 € la première année. »**

- Dans la Play Console : *Abonnements → pro_yearly → Offres → offre d'introduction*
  (prix réduit sur la 1re période, réservée aux nouveaux abonnés).
- Sur l'App Store : *Offre d'introduction* du même type sur `...pro.yearly`.
- Bandeau dans l'app en haut du paywall : « Lancement · -40 % la 1re année » (déjà câblé,
  texte à activer via `PromoConfig`).

## Texte du paywall (déjà en place dans l'app)

Titre : **Débloque HanziLock Pro**
Avantages affichés avant l'achat :
- HSK 1 à 6 — ~5000 caractères
- Révisions illimitées chaque jour
- Progression détaillée par niveau

+ prix, durée, « résiliable à tout moment », lien CGU + confidentialité (obligatoire).

## Ce qu'il ne faut PAS mettre payant

- Le widget de base (c'est le crochet d'acquisition).
- Le rappel quotidien (c'est ce qui fait revenir).
- HSK 1 (tout le monde doit pouvoir tester le concept en entier sur un niveau).

## À faire côté stores

1. Play Console → créer l'abonnement `pro_lifetime` en **produit unique** (pas un abo) si
   tu veux l'option à vie — sinon garde juste mensuel + annuel.
2. Activer l'**essai gratuit 7 j** sur `pro_yearly`.
3. Créer l'**offre d'introduction -40 %** sur `pro_yearly`.
4. Screenshot du paywall (obligatoire Apple, recommandé Google).
