#!/usr/bin/env bash
# Génère le projet Xcode et l'ouvre. À lancer sur macOS avec Xcode installé.
set -euo pipefail
cd "$(dirname "$0")/.."

if ! command -v xcodegen >/dev/null 2>&1; then
  echo "XcodeGen manquant. Installation via Homebrew…"
  if command -v brew >/dev/null 2>&1; then
    brew install xcodegen
  else
    echo "Homebrew absent. Installe-le (https://brew.sh) puis relance, ou :"
    echo "  mint install yonaskolb/xcodegen"
    exit 1
  fi
fi

echo "Regénération de l'icône (nécessite Pillow : pip3 install Pillow)…"
python3 Brand/generate_icon.py || echo "  (icône non regénérée — le PNG existant est conservé)"

echo "Génération de HanziLock.xcodeproj…"
xcodegen generate

echo
echo "OK. Ouvre le projet :  open HanziLock.xcodeproj"
echo "Avant d'archiver :"
echo "  1. project.yml → DEVELOPMENT_TEAM : mets ton Team ID, puis relance ./Scripts/bootstrap.sh"
echo "  2. Xcode → target HanziLock → Signing & Capabilities : vérifie App Groups = group.com.antoninclouet.hanzilock"
echo "  3. Même chose sur la target HanziWidgetExtension"
echo "  4. Scheme HanziLock → Edit Scheme → Run → Options → StoreKit Configuration = StoreKit/Configuration.storekit"
