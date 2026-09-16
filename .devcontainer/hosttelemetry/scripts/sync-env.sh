#!/usr/bin/env zsh

mkdir -p /home/vscode/.local/share/mise/installs/java
mkdir -p /home/vscode/.local/share/mise/installs/maven

CURRENT_JAVA="$(mise where java 2>/dev/null)"
if [ -z "$CURRENT_JAVA" ] && [ -d "/workspaces/devopsmind" ]; then
    CURRENT_JAVA="$(cd /workspaces/devopsmind && mise where java 2>/dev/null)"
fi

CURRENT_MAVEN="$(mise where maven 2>/dev/null)"
if [ -z "$CURRENT_MAVEN" ] && [ -d "/workspaces/devopsmind" ]; then
    CURRENT_MAVEN="$(cd /workspaces/devopsmind && mise where maven 2>/dev/null)"
fi

if [ -n "$CURRENT_JAVA" ] && [ "$(readlink /home/vscode/.local/share/mise/installs/java/latest)" != "$CURRENT_JAVA" ]; then
    rm -f /home/vscode/.local/share/mise/installs/java/latest
    ln -sf "$CURRENT_JAVA" /home/vscode/.local/share/mise/installs/java/latest
    echo "Mise redirigió reactivamente el enlace genérico a: $CURRENT_JAVA"
fi

if [ -n "$CURRENT_MAVEN" ] && [ "$(readlink /home/vscode/.local/share/mise/installs/maven/latest)" != "$CURRENT_MAVEN" ]; then
    rm -f /home/vscode/.local/share/mise/installs/maven/latest
    ln -sf "$CURRENT_MAVEN" /home/vscode/.local/share/mise/installs/maven/latest
fi

if ! grep -q "JAVA_HOME" ~/.zshrc; then
    cat << 'EOF' >> ~/.zshrc

# --- Ecosistema de Rutas LabMetricsIA ---
export JAVA_HOME="/home/vscode/.local/share/mise/installs/java/latest"
export M2_HOME="/home/vscode/.local/share/mise/installs/maven/latest"
export PATH="$PATH:$JAVA_HOME/bin:$M2_HOME/bin"
# ----------------------------------------
EOF
fi
