#!/bin/bash
set -e 

CYAN='\033[0;36m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

ICON_SUCCESS="✅"
ICON_PROCESS="🚀"
ICON_INFO="ℹ️"

echo -e "${CYAN}🚀 Configurando repositorio oficial de gopass...${NC}"

curl -fsSL https://packages.gopass.pw/repos/gopass/gopass-archive-keyring.gpg | sudo tee /usr/share/keyrings/gopass-archive-keyring.gpg >/dev/null

cat << EOF | sudo tee /etc/apt/sources.list.d/gopass.sources
Types: deb
URIs: https://packages.gopass.pw/repos/gopass
Suites: stable
Architectures: all amd64 arm64 armhf
Components: main
Signed-By: /usr/share/keyrings/gopass-archive-keyring.gpg
EOF

sudo apt-get update
sudo apt-get install -y gopass gnupg2 vim ca-certificates curl jq
echo -e "${GREEN}${ICON_SUCCESS} ¡Gopass instalado con éxito!${NC}"

curl https://mise.run | sh

if ! grep -q "mise activate zsh" ~/.zshrc; then
    echo 'eval "$(/home/vscode/.local/bin/mise activate zsh)"' >> ~/.zshrc
fi

echo -e "${CYAN}🎯 Configurando alias personalizados de Unix (Modo Maven)...${NC}"

cat << 'EOF' >> ~/.zshrc

# --- Alias Personalizados LabMetricsIA ---
alias ..="cd .."
alias ...="cd ../.."
alias ....="cd ../../.."
alias ~="cd ~"
alias home="cd ~"

alias pass='gopass '
alias k='kill -9'
alias up='sudo apt-get update -y && sudo apt-get upgrade -y'
alias sdk='mise '

# Ajustado a tu ecosistema Maven real
alias m="mvn"
alias mbuild="mvn clean package -DskipTests"
alias mtest="mvn test"
alias mrun="mvn spring-boot:run"

alias gaa="git add ."
alias gca="git add --all && git commit --amend --no-edit"
alias gco="git checkout"
alias gd='git diff'
alias gs="git status -sb"
alias gf="git fetch --all -p"
alias gps="git push"
alias gpsf="git push --force"
alias gpl="git pull --rebase --autostash"
alias gb="git branch"
alias glg="git log --oneline --graph --decorate"
# --------------------------------------
EOF

echo -e "${GREEN}${ICON_SUCCESS} ¡Alias inyectados con éxito!${NC}"
