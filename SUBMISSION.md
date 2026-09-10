# HanziLock — état de préparation App Store (iOS)

Généré le 2026-09-09. Ce fichier remplace la checklist de `AppStoreConnect_Guide.md`
(qui reste utile pour le détail des abonnements).

> 📱 **Pas de Mac ?** La version **Android** est complète et **compile déjà** (APK debug produit
> le 2026-09-09). Voir [android/README.md](android/README.md) et [android/PLAY_STORE.md](android/PLAY_STORE.md).

## Identifiants figés dans le code

| Élément | Valeur |
|---|---|
| Bundle ID app | `com.antoninclouet.hanzilock` |
| Bundle ID widget | `com.antoninclouet.hanzilock.HanziWidget` |
| App Group | `group.com.antoninclouet.hanzilock` |
| Produit abo mensuel | `com.antoninclouet.hanzilock.pro.monthly` |
| Produit abo annuel | `com.antoninclouet.hanzilock.pro.yearly` |
| Groupe d'abonnement | `HanziLock Pro` |
| Version marketing / build | `1.0.0` / `1` |
| Cible | iOS 17.0, iPhone uniquement |

> Si tu changes `antoninclouet` (préfixe bundle / URL GitHub), fais-le dans :
> `project.yml`, `Config/*.entitlements`, `Shared/ReviewStore.swift`, `App/Store/ProductID.swift`,
> `StoreKit/Configuration.storekit`, `App/Store/PaywallView.swift`, `fastlane/Deliverfile`,
> `fastlane/metadata/**`, `docs/*.html`, `Brand/generate_icon.py` (rien à changer ici).

## ✅ Fait (par ce dépôt)

- [x] Code app + widget + StoreKit 2 complet
- [x] Parcours d'onboarding 3 écrans avant paywall (`App/OnboardingView.swift`)
- [x] Placeholders `com.tonnom` remplacés partout
- [x] `project.yml` XcodeGen → 2 targets, entitlements App Groups, StoreKit config liée
- [x] `Config/HanziLock-Info.plist` + `HanziWidget-Info.plist` (+ `ITSAppUsesNonExemptEncryption = NO`)
- [x] `Config/*.entitlements` avec l'App Group
- [x] Icône App Store 1024×1024 générée : `App/Resources/Assets.xcassets/AppIcon.appiconset/icon-1024.png`
      (regénérable : `python Brand/generate_icon.py`)
- [x] `AccentColor` + `LaunchBackground` colorsets
- [x] Politique de confidentialité + page support + landing : `docs/` (prêt pour GitHub Pages)
- [x] Métadonnées App Store fr-FR + en-US : `fastlane/metadata/`
- [x] Notes pour le reviewer : `fastlane/metadata/review_information/notes.txt`

## ⛔ À faire par toi — nécessite un Mac + compte Apple Developer

### 1. Générer et builder le projet (Mac + Xcode)
```
./Scripts/bootstrap.sh      # installe XcodeGen si besoin, génère HanziLock.xcodeproj
open HanziLock.xcodeproj
```
- Renseigne `DEVELOPMENT_TEAM` dans `project.yml` (Team ID : App Store Connect → Membership), relance le script.
- Build & run sur un iPhone réel. Teste : onboarding → app → ajout du widget à l'écran verrouillé → paywall (StoreKit config locale).
- Police hanzi : le code utilise une approximation système (serif black). Pour Noto Serif SC exact,
  ajoute `NotoSerifSC-Black.ttf` à la target app + `UIAppFonts` dans l'Info.plist, puis change `Brand.hanziFont`.

### 2. Héberger les pages web
- Pousse ce dépôt sur GitHub → Settings → Pages → *Deploy from a branch* → branche `main`, dossier `/docs`.
- Vérifie que `https://antoninroad.github.io/hanzilock/privacy.html` répond.
- Si ton user GitHub ≠ `antoninclouet`, corrige l'URL dans `App/Store/PaywallView.swift` et `fastlane/metadata/**`.

### 3. Apple Developer (99 $/an) + App Store Connect
- App ID avec capabilities **App Groups** + **In-App Purchase**.
- Crée l'App Group `group.com.antoninclouet.hanzilock`.
- App Store Connect → Mes apps → `+` → nom **HanziLock**, bundle `com.antoninclouet.hanzilock`, langue principale Français.
- Achats intégrés → groupe d'abonnement `HanziLock Pro` → 2 abonnements avec les productID exacts ci-dessus,
  prix (par défaut testés : 4,99 €/mois, 29,99 €/an), + capture d'écran du paywall (obligatoire Apple).

### 4. Métadonnées + assets dans App Store Connect
- Métadonnées : copie depuis `fastlane/metadata/` (ou `fastlane deliver`).
- **Politique de confidentialité (URL)** : `https://antoninroad.github.io/hanzilock/privacy.html`
- **App Privacy (nutrition label)** : *Aucune donnée collectée* (voir §6 ci-dessous).
- **Classification d'âge** : questionnaire → tout « Aucun/Non » → **4+**.
- **Droit de licence (EULA)** : EULA standard Apple (déjà lié dans le paywall).
- Catégorie : Éducation (secondaire : Référence).

### 5. Captures d'écran (obligatoire, à faire sur simulateur/appareil)
Tailles minimales requises par Apple (2026) :
| Appareil | Résolution portrait | Exemple |
|---|---|---|
| iPhone 6,9" (15 Pro Max / 16 Pro Max) | 1290 × 2796 | **requis** |
| iPhone 6,5" (11 Pro Max / XS Max) | 1242 × 2688 | **requis** |
| iPhone 6,7" (14 Pro Max) | 1290 × 2796 | accepté = 6,9" |
- 3 à 5 captures : onglet Aujourd'hui, carte de révision révélée, dashboard, paywall, widget sur écran verrouillé.
- Astuce : `Cmd+S` dans le simulateur, ou `fastlane snapshot` si tu ajoutes un plan de test UI.
- Dépose-les dans `fastlane/screenshots/fr-FR/` et `/en-US/`, puis `skip_screenshots(false)` dans le Deliverfile.

### 6. Réponses au questionnaire App Privacy
> Data Collection : **No, we do not collect data from this app.**
Justification : progression stockée uniquement en local (App Group UserDefaults), paiements gérés
exclusivement par Apple/StoreKit, aucun SDK analytics/pub/crash, aucun appel réseau applicatif.
À réévaluer si tu ajoutes un backend, des analytics ou un compte utilisateur.

### 7. Archive & upload
- Xcode → *Product → Archive* → *Distribute App → App Store Connect → Upload*.
- Ou CLI : `xcodebuild -project HanziLock.xcodeproj -scheme HanziLock -archivePath build/HanziLock.xcarchive archive`
  puis `xcodebuild -exportArchive …` + `xcrun altool`/`notarytool`.
- Sélectionne le build dans App Store Connect, réponds au reste, **Submit for Review**.

## Différences restantes avec un concurrent type « Daily Hanzi »
Sélecteur HSK 1-6, thèmes visuels multiples, widget « phrase d'exemple » séparé,
paragraphes quotidiens générés. Hors périmètre de cette v1.
