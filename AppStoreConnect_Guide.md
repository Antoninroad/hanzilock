# Guide App Store Connect — abonnements, confidentialité, soumission

## 1. Tester les abonnements sans compte développeur

Avant de payer les 99$/an, teste tout en local avec le fichier `StoreKit/Configuration.storekit` fourni :

1. Dans Xcode, sélectionne ton scheme de l'app → *Edit Scheme* → onglet *Run* → *Options* → *StoreKit Configuration* → choisis `Configuration.storekit`.
2. Build & run : `PurchaseManager` va charger les 2 produits factices (mensuel/annuel) et tu peux acheter/restaurer sans vraie carte bancaire ni compte Apple Developer.
3. Si Xcode refuse le JSON fourni (le format `.storekit` change parfois d'une version d'Xcode à l'autre), le plus fiable est de le régénérer toi-même : File → New → File → *StoreKit Configuration File*, puis ajoute les 2 abonnements via l'UI Xcode (bouton `+` → *Add Subscription*) avec les mêmes `productID` que dans `ProductID.swift`.

## 2. Créer les vrais produits (une fois inscrit au Apple Developer Program)

1. [developer.apple.com](https://developer.apple.com) → Certificates, Identifiers & Profiles → crée un **App ID** avec les capabilities *App Groups* et *In-App Purchase* cochées.
2. [App Store Connect](https://appstoreconnect.apple.com) → *Mes apps* → `+` → crée l'enregistrement de l'app (nom, bundle ID, langue principale — **choisis un nom différent de "Daily Hanzi"**).
3. Onglet *Fonctionnalités de l'app* → *Achats intégrés* → crée un **groupe d'abonnement** (ex. "HanziLock Pro"), puis les 2 abonnements avec exactement les `productID` de `ProductID.swift` (`com.tonnom.hanzilock.pro.monthly` / `.yearly`). Renseigne prix, durée, et les métadonnées de révision (nom d'affichage, description, capture d'écran du paywall requise par Apple).

## 3. Conformité Guideline 3.1.2 (obligatoire pour être accepté)

Le paywall (`PaywallView.swift`) doit afficher, avant tout achat :
- [x] Titre de l'abonnement
- [x] Durée (mensuel/annuel)
- [x] Prix (et prix à l'unité si applicable)
- [x] Fonctionnalités débloquées
- [x] Lien vers les Conditions d'utilisation (EULA)
- [x] Lien vers la Politique de confidentialité

C'est déjà câblé dans le code fourni — remplace juste `termsURL` et `privacyURL` par tes vraies adresses une fois hébergées (étape 4).

## 4. Politique de confidentialité (obligatoire, même app simple)

Apple exige une URL publique vers une politique de confidentialité, même si tu ne collectes presque rien. Héberge le texte ci-dessous (GitHub Pages, Notion public, ou une page statique) et mets l'URL dans App Store Connect (*Informations sur l'app* → *Politique de confidentialité*) ET dans `PaywallView.swift`.

> **⚠️ Vérifie et adapte** ce template si tu ajoutes des SDK tiers (analytics, publicité, crash reporting) — dans ce MVP, aucune donnée personnelle n'est collectée : tout est stocké localement (App Group UserDefaults) et les paiements sont traités uniquement par Apple.

```markdown
# Politique de confidentialité — [Nom de ton app]

Dernière mise à jour : [date]

[Nom de ton app] ne collecte, ne stocke sur des serveurs distants, ni ne partage
aucune donnée personnelle identifiable.

## Données stockées localement
Ta progression d'apprentissage (caractères révisés, historique de révision) est
stockée uniquement sur ton appareil, via le stockage partagé d'Apple (App Group),
afin que l'application et le widget d'écran verrouillé puissent y accéder. Ces
données ne quittent jamais ton appareil et ne sont transmises à aucun serveur.

## Achats intégrés
Les abonnements sont traités entièrement par Apple via StoreKit. [Nom de ton app]
ne voit ni ne stocke tes informations de paiement — consulte la politique de
confidentialité d'Apple pour les données traitées par l'App Store.

## Contact
Pour toute question : [ton email de contact]
```

## 5. Checklist de soumission App Store Connect

- [ ] Icône de l'app 1024×1024 px (sans coins arrondis, sans transparence)
- [ ] Captures d'écran aux tailles requises (6,7" et 6,5" minimum pour iPhone ; iPad si tu supportes iPadOS)
- [ ] Description, mots-clés, catégorie (Éducation), URL de support
- [ ] URL de la politique de confidentialité (étape 4)
- [ ] Questionnaire *App Privacy* (Nutrition Label) : dans ce MVP → "Aucune donnée collectée". Réévalue si tu ajoutes analytics/pub/compte utilisateur.
- [ ] Classification d'âge (questionnaire dans App Store Connect)
- [ ] Groupe d'abonnement + 2 produits créés et en statut "Prêt à être soumis" (étape 2)
- [ ] Notes pour le reviewer Apple : mentionne que c'est une app de flashcards + widget de répétition espacée, explique brièvement comment tester le widget (ajouter à l'écran verrouillé) et l'abonnement (compte sandbox fourni si besoin)
- [ ] Build archivé et téléversé via Xcode (Product → Archive → Distribute App → App Store Connect)

## 6. Différences restantes avec l'app originale

Même une fois tout ça fait, tu n'auras qu'un MVP proche du concept — pas un clone pixel-perfect. Ce qui manque encore si tu veux vraiment concurrencer Daily Hanzi : onboarding multi-écrans avant le paywall (le vrai app attend ~2 min avant de le montrer), sélecteur de niveau HSK 1-6, thèmes visuels multiples, widget "phrase d'exemple" séparé, paragraphes quotidiens générés dynamiquement (probablement via une API IA côté serveur dans l'app originale).
