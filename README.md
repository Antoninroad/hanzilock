# HanziLock — app d'apprentissage du chinois (iOS + Android)

Deux implémentations du même concept (widget « caractère du jour » + flashcards + répétition espacée + abonnement Pro) :

| Plateforme | Dossier | Statut | Prérequis |
|---|---|---|---|
| **iOS** (SwiftUI / WidgetKit) | racine + `App/`, `Widget/`, `Shared/` | code complet, projet généré par XcodeGen | Mac + Xcode, compte Apple Developer (99 $/an) |
| **Android** (Kotlin / Compose / Glance) | [`android/`](android/) | **app native complète et compilable sur Windows** | Android Studio ou JDK 17 + SDK 36 ; Play Console (25 $ une fois) |

👉 **Sans Mac : commence par la version Android** — voir [android/README.md](android/README.md) et [android/PLAY_STORE.md](android/PLAY_STORE.md).

---

## Version iOS

Ceci n'est **pas** un projet Xcode complet (impossible à générer sans macOS), mais l'ensemble des fichiers Swift/SwiftUI/WidgetKit + un `project.yml` XcodeGen. Prérequis : un **Mac avec Xcode** et, pour publier, un compte **Apple Developer Program** (99$/an).

⚠️ **Important — propriété intellectuelle** : n'utilise pas le nom "Daily Hanzi", son logo, ni ses textes marketing. Ce code recrée le *concept* (widget lock screen + flashcards + SRS), pas l'app elle-même. Choisis ton propre nom, ton propre bundle ID, ta propre identité visuelle avant soumission à l'App Store.

## Étapes de mise en place

Le projet Xcode est maintenant **généré automatiquement** via [XcodeGen](https://github.com/yonaskolb/XcodeGen) à partir de `project.yml` — plus besoin de recréer les targets à la main.

Sur un **Mac avec Xcode** :

```
./Scripts/bootstrap.sh      # installe XcodeGen si besoin, (re)génère l'icône, génère HanziLock.xcodeproj
open HanziLock.xcodeproj
```

Ce que le script met en place tout seul : les 2 targets (app + `HanziWidgetExtension`), l'App Group `group.com.antoninclouet.hanzilock` sur les deux, les Info.plist, l'icône 1024, la StoreKit config liée au scheme.

Il te reste seulement à :
1. Mettre ton **Team ID** dans `project.yml` (`DEVELOPMENT_TEAM`), puis relancer le script.
2. Dans Xcode, vérifier *Signing & Capabilities* sur les 2 targets (compte, App Groups cochée).
3. Build & run sur un **iPhone réel** de préférence. Sur l'appareil : appui long sur l'écran verrouillé → *Personnaliser* → *Écran verrouillé* → ajouter un widget → « HanziLock ».

👉 Suite complète (hébergement des pages, App Store Connect, captures, soumission) : **[SUBMISSION.md](SUBMISSION.md)**.

## Portée de ce squelette (MVP)

Inclus :
- Widget écran verrouillé (rectangulaire, inline, circulaire) + widget écran d'accueil, qui affiche le "caractère du jour"
- Deck HSK1 complet (~150 mots) embarqué en dur
- Flashcards avec système de répétition espacée simple (Leitner, 6 boîtes)
- Écran de progression (nombre de caractères maîtrisés)

Inclus aussi maintenant :
- Identité visuelle appliquée dans le code : `Shared/BrandColors.swift` (palette + police hanzi), `App/Brand/SealMark.swift` (le logo sceau, réutilisé dans le paywall) et `App/Brand/MasteredStamp.swift` (l'estampille "已" qui apparaît sur une carte marquée "Je connais"). Voir le canvas "HanziLock — Identité visuelle" pour la charte complète (logo, palette, typographie, icône iOS, applications).
- Pour l'icône App Store (1024×1024) : exporte en PNG le SVG du sceau (planche "Icône d'app" du canvas — variantes claire/sombre/tintée) et dépose-le dans `Assets.xcassets/AppIcon.appiconset`.
- Abonnement Pro (mensuel/annuel) via StoreKit 2 — `App/Store/` (`ProductID.swift`, `PurchaseManager.swift`, `PaywallView.swift`)
- Limite de 5 révisions/jour en gratuit, débloquée par l'abonnement (`ReviewStore.swift`)
- Fichier `StoreKit/Configuration.storekit` pour tester les achats sans compte développeur
- `AppStoreConnect_Guide.md` : conformité Guideline 3.1.2, template de politique de confidentialité, checklist de soumission

Inclus depuis la préparation App Store :
- Onboarding 3 écrans avant le paywall (`App/OnboardingView.swift`)
- Projet Xcode généré (`project.yml`), Info.plist, entitlements, icône 1024, colorsets
- Pages web (confidentialité / support / landing) dans `docs/`, métadonnées App Store dans `fastlane/metadata/`

Pas inclus (à ajouter toi-même si tu veux vraiment concurrencer Daily Hanzi) :
- Sélecteur de niveau HSK 1-6, thèmes visuels personnalisables
- Widget "phrase d'exemple", paragraphes quotidiens générés
- Icônes, écrans de lancement, captures d'écran App Store réelles

## Cible iOS

Ce code cible **iOS 17+** pour rester simple (`containerBackground` sur les widgets). Le vrai Daily Hanzi supporte iOS 15+ ; pour descendre en dessous d'iOS 17 il faut adapter le fond du widget avec l'ancienne API (`background(_:)` conditionné par `#available`) — je peux le faire si tu veux élargir la compatibilité.
