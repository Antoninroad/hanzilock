# Publier HanziLock sur le Play Store — pas à pas

Tu as déjà le compte Play Console. Le fichier à téléverser est
`app-release.aab` (généré ; je te l'envoie dans le chat).

⚠️ **Garde précieusement** `android/hanzilock.jks` et le mot de passe
(`android/keystore.properties`). Sans eux, tu ne pourras plus jamais mettre l'app à jour.
Copie-les sur une clé USB / un gestionnaire de mots de passe.

---

## 1. Créer l'application

Play Console → bouton **« Créer une application »** (en haut à droite).

- Nom de l'application : **HanziLock**
- Langue par défaut : **Français (France) – fr-FR**
- Application ou jeu : **Application**
- Gratuite ou payante : **Gratuite**
- Coche les deux déclarations en bas → **Créer l'application**

---

## 2. Remplir « Configurer votre application »

Sur le tableau de bord, une liste de tâches apparaît. Fais-les toutes (sinon impossible de publier).

| Tâche | Quoi répondre |
|---|---|
| **Règles de confidentialité** | URL : `https://antoninroad.github.io/hanzilock/privacy.html` |
| **Accès à l'application** | « Toutes les fonctionnalités sont accessibles sans restriction » |
| **Annonces** | « Non, mon application ne contient pas d'annonces » |
| **Classification du contenu** | Lance le questionnaire → catégorie **Éducation / Référence** → réponds **Non** à tout (violence, sexe, etc.) → tu obtiens **PEGI 3 / Tous publics** |
| **Public cible et contenu** | Tranches d'âge : coche **13-17** et **18 et +**. Pas conçu pour les enfants. |
| **Application d'actualités** | Non |
| **Fonctionnalités COVID-19** | Non |
| **Sécurité des données** | « **Aucune donnée collectée ni partagée** ». Justification : tout est stocké en local, paiements gérés par Google Play, aucun SDK tiers. |
| **Publicités gouvernementales** | Non applicable |
| **Catégorie de l'app** | Catégorie : **Éducation**. Ajoute une adresse e-mail de contact : `antoninclouet922@gmail.com` |

---

## 3. Créer les abonnements

Menu de gauche → **Monétiser → Produits → Abonnements** → **Créer un abonnement**.

### Abonnement annuel (celui à mettre en avant)
- ID du produit : **`pro_yearly`** (exactement, ça ne se change plus après)
- Nom : **HanziLock Pro – annuel**
- **Créer un forfait de base** :
  - ID : `yearly`
  - Type : **renouvellement automatique**
  - Période de facturation : **1 an**
  - Prix : **29,99 €** (zone Europe ; Google convertit le reste)
- **Ajouter une offre → Essai gratuit** :
  - Éligibilité : **nouveaux clients**
  - Phase : **essai gratuit, 7 jours**
- **Ajouter une offre → Prix réduit** (offre de lancement) :
  - Éligibilité : nouveaux clients
  - Phase : **1 an à 17,99 €** puis prix normal
- **Activer** le forfait et les offres.

### Abonnement mensuel
- ID : **`pro_monthly`**
- Nom : HanziLock Pro – mensuel
- Forfait de base : ID `monthly`, renouvellement auto, période **1 mois**, prix **4,99 €**
- Activer.

> L'option « à vie » (achat unique 49,99 €) n'est **pas** gérée par le code actuel
> (abonnements uniquement). On l'ajoutera dans une mise à jour si tu la veux.

---

## 4. Fiche Play Store

Menu de gauche → **Développer votre audience → Présence sur le Store → Fiche du Store principale**.

- **Nom de l'application** : HanziLock
- **Description courte** : copie `fastlane/metadata/android/fr-FR/short_description.txt`
- **Description complète** : copie `fastlane/metadata/android/fr-FR/full_description.txt`
- **Icône de l'application** : `android/play/play_store_512.png` (512 × 512)
- **Image mise en avant** : `android/play/feature_graphic_1024x500.png` (1024 × 500)
- **Captures d'écran pour téléphone** (2 à 8, format portrait) :
  - Ouvre le projet `android/` dans Android Studio → lance sur un émulateur Pixel
  - Prends : onglet *Aujourd'hui*, une carte révélée, l'onglet *Progrès*, le paywall
  - Bouton appareil photo dans la barre latérale de l'émulateur

---

## 5. Choisir les pays

Menu → **Production** (ou **Test → Test interne**) → onglet **Pays / régions** →
ajoute les pays. Pour commencer, tu peux mettre **France + Union européenne**, ou **tous les pays**.

---

## 6. Téléverser l'AAB et publier

### D'abord en test interne (recommandé)
1. Menu → **Tester → Test interne** → **Créer une version**
2. À la 1re version, Google propose la **signature d'application Play** → **Accepter**
   (Google garde la clé de distribution ; ton `hanzilock.jks` devient la « clé d'importation »).
3. **Importer** `app-release.aab`
4. **Notes de version** : copie `fastlane/metadata/android/fr-FR/changelogs/1.txt`
5. **Enregistrer → Vérifier la version → Commencer le déploiement**
6. Onglet **Testeurs** : ajoute ton adresse Gmail, copie le **lien de test**,
   ouvre-le sur ton téléphone, installe → vérifie que le paywall affiche bien les prix.

### Puis en production
1. Menu → **Production** → **Créer une version**
2. Réutilise le même `app-release.aab` (ou « Ajouter depuis la bibliothèque »)
3. Notes de version → **Vérifier → Envoyer pour examen**
4. Délai d'examen Google : **1 à 3 jours** pour une première app.

---

## Récapitulatif des identifiants

| Élément | Valeur |
|---|---|
| Package | `com.antoninclouet.hanzilock` |
| Abonnements | `pro_monthly` (P1M, 4,99 €) · `pro_yearly` (P1Y, 29,99 €) |
| Forfaits de base | `monthly` · `yearly` |
| Catégorie | Éducation |
| Confidentialité | `https://antoninroad.github.io/hanzilock/privacy.html` |
| Sécurité des données | Aucune donnée collectée |
| Classification | Tous publics / PEGI 3 |
