# Publication Play Store — HanziLock Android

## Identifiants
| Élément | Valeur |
|---|---|
| Package | `com.antoninclouet.hanzilock` |
| versionCode / versionName | `1` / `1.0.0` |
| Abonnements | `pro_monthly` (P1M), `pro_yearly` (P1Y) |
| minSdk / targetSdk | 26 / 36 |

## Étapes

1. **Play Console** (25 $ une fois) → Créer une application → nom « HanziLock », package ci-dessus.
2. **Signature** : configure une `signingConfig` release dans `app/build.gradle.kts`
   (ou Android Studio → Build → Generate Signed App Bundle, crée un keystore `.jks` — **garde-le**).
3. **Build** : `./gradlew bundleRelease` → `app/build/outputs/bundle/release/app-release.aab`.
4. **Abonnements** : Monétisation → Produits → Abonnements → créer `pro_monthly` et `pro_yearly`
   (mêmes IDs que `billing/BillingManager.kt`), définir prix + période de facturation.
5. **Fiche Store** :
   - Textes : `fastlane/metadata/android/{fr-FR,en-US}/`
   - Icône : `play/play_store_512.png`
   - Bannière 1024×500 + 2 captures téléphone min. (à faire depuis l'émulateur)
6. **Confidentialité** : URL `https://antoninroad.github.io/hanzilock/privacy.html` (dossier `../docs/`).
7. **Sécurité des données** (questionnaire Play) :
   - Données collectées : **Aucune**
   - Données partagées : **Aucune**
   - Justification : progression stockée en local (SharedPreferences), paiements gérés par Google Play,
     aucun SDK analytics/pub, aucun appel réseau applicatif.
8. **Contenu** : classification PEGI/IARC → tout « Non » → tous publics. Catégorie : Éducation.
9. Envoyer en test interne d'abord, puis production.

## Optionnel : `fastlane supply`
`fastlane supply init` puis `fastlane supply --aab app/build/outputs/bundle/release/app-release.aab`
pousse le bundle + les métadonnées de `fastlane/metadata/android/`.
