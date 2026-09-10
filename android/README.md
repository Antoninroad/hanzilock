# HanziLock — version Android (Kotlin + Jetpack Compose)

Portage Android natif de l'app iOS. Caractère HSK du jour, widget écran d'accueil,
répétition espacée (Leitner), abonnement Pro, réglages (niveaux, thème, rappel).

**Vocabulaire : HSK 1 à 6 complet (~5000 mots)** dans `app/src/main/assets/hsk_full.json`.
HSK 1-3 (~480 mots courants) sont traduits/relus à la main ; le reste est traduit
automatiquement de l'anglais (OPUS-MT hors-ligne) — qualité correcte mais perfectible
sur les verbes isolés. Regénérer : `CT2_USE_MKL=0 python tools/build_hsk.py`.

## Prérequis

- Android Studio (Ladybug ou plus récent) **ou** JDK 17+ et le SDK Android (API 36).
- Rien d'autre : pas de Mac, pas de compte payant pour développer.
- Pour publier : compte **Google Play Console** (25 $ une seule fois).

## Compiler / lancer

```bash
cd android
./gradlew assembleDebug          # APK debug -> app/build/outputs/apk/debug/
./gradlew installDebug           # installe sur un appareil/émulateur branché
```

Ou ouvre le dossier `android/` dans Android Studio et lance `app`.

`local.properties` pointe vers le SDK de cette machine — régénéré par Android Studio si absent.

## Ce qui est inclus

| Élément | Fichier |
|---|---|
| Vocabulaire HSK 1-6 (~5000 mots) | `assets/hsk_full.json` (généré par `tools/build_hsk.py`) + fallback `data/HskData.kt` (HSK 1-3 traduits main) |
| Répétition espacée Leitner + limite quotidienne réglable | `data/ReviewStore.kt` |
| Réglages (niveaux, limite, thème, rappel, reset) | `ui/SettingsScreen.kt`, `data/AppSettings.kt` |
| Rappel quotidien (notification, AlarmManager) | `notif/Reminder.kt` |
| Onboarding 3 écrans | `ui/OnboardingScreen.kt` |
| Écrans Aujourd'hui / Cartes / Progrès | `ui/*Screen.kt` |
| Paywall (titre, durée, prix, avantages, liens légaux) | `ui/PaywallScreen.kt` |
| Abonnement Google Play Billing v7 | `billing/BillingManager.kt` |
| Widget écran d'accueil (Glance) | `widget/HanziWidget.kt` |
| Icône adaptative + icône Play 512 | `tools/generate_icons.py` → `res/`, `play/` |
| Regénérer le vocabulaire HSK 1-6 | `python tools/build_hsk.py` (Pillow + argostranslate ; ~15 min, hors-ligne) |

## Différences avec iOS

- **Pas de widget d'écran verrouillé** : Android ne le permet plus depuis Android 5
  (revenu très partiellement sur tablettes en Android 14). Le widget est sur l'écran d'accueil.
- Le widget se rafraîchit toutes les 6 h + à chaque ouverture de l'app (le caractère du jour
  est déterministe par date, donc jamais faux plus de quelques heures).
- Abonnements = **produits Play** `pro_monthly` / `pro_yearly` (voir `billing/BillingManager.kt`),
  à créer dans la Play Console. Le prix affiché vient de Play, pas codé en dur.

## Publier sur le Play Store

1. Play Console → créer l'app, package `com.antoninclouet.hanzilock`.
2. Monétisation → Produits → Abonnements : créer `pro_monthly` (P1M) et `pro_yearly` (P1Y).
3. Générer un **App Bundle signé** : `./gradlew bundleRelease` (configure d'abord une
   `signingConfig` release, ou utilise « Générer un bundle signé » dans Android Studio).
4. Politique de confidentialité : réutilise `../docs/privacy.html` (héberge le dossier `docs/`
   via GitHub Pages). URL à renseigner dans la Play Console.
5. Fiche Store : voir `fastlane/metadata/android/` (textes fr-FR + en-US) et `play/play_store_512.png`.
6. Déclaration « Sécurité des données » : **aucune donnée collectée / partagée** (tout est local,
   paiements gérés par Google Play).
7. Captures d'écran : 2 minimum (téléphone), 1080×1920 ou plus.
