#!/bin/bash

# Définition des chemins
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
REPO_DIR="$SCRIPT_DIR/repo"
REPO_URL="https://github.com/repo/test"
PATCHES_DIR="$SCRIPT_DIR/patches/core"

# S'assurer que le répertoire des patches existe
mkdir -p "$PATCHES_DIR"

# Fonction pour recloner le dépôt
reclone_repo() {
    echo "Suppression du dépôt local..."
    rm -rf "$REPO_DIR"
    echo "Clonage du dépôt..."
    git clone "$REPO_URL" "$REPO_DIR"
    echo "Le dépôt a été recloné."
}

# Fonction pour créer des patches
create_patches() {
    cd "$REPO_DIR" || exit
    echo "Création des patches..."
    git format-patch -o "$PATCHES_DIR" origin/master
    echo "Les patches ont été créés dans $PATCHES_DIR"
}

# Fonction pour appliquer des patches
apply_patches() {
    cd "$REPO_DIR" || exit
    echo "Application des patches..."
    for patch in "$PATCHES_DIR"/*.patch; do
        git apply "$patch"
        patch_name=$(basename "$patch")
        git add .
        git commit -m $patch
        echo "Patch $patch_name appliqué."
    done
}

# Vérification des arguments pour choisir l'action
case "$1" in
    updateUpstream)
        reclone_repo
        ;;
    createPatches)
        create_patches
        ;;
    applyPatches)
        apply_patches
        ;;
    *)
        echo "Utilisation possible du script:
            $0 updateUpstream
                Permet de mettre à jour le code source en supprimant et en reclonant le dossier source
            $0 createPatches
                Permet de créer les patches
            $0 applyPatches
                Permet d'appliquer les patches"
        exit 1
        ;;
esac
