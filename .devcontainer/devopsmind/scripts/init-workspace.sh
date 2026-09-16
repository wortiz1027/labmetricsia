#!/usr/bin/env bash
set -e

export PATH="/home/vscode/.local/bin:$PATH"

export GIT_SAFE_DIRECTORIES="/workspaces"

cd /workspaces
echo "🚀 Instalando herramientas globales de la raíz (Linters, Seguridad)..."
mise install --yes

echo "📦 Escribiendo el Git Hook de pre-commit unificado..."
cat << 'EOF' > /workspaces/.git/hooks/pre-commit
#!/usr/bin/env bash
export PATH="$HOME/.local/bin:$PATH"
if ! command -v mise &> /dev/null; then
    exit 0
fi
cd /workspaces
mise run lint
EOF
chmod +x /workspaces/.git/hooks/pre-commit

echo "🔍 Escaneando el monorrepo en busca de subproyectos con Mise..."

for config_file in $(find /workspaces -maxdepth 3 -name "mise.toml" -not -path "/workspaces/mise.toml"); do
    
    [ -f "$config_file" ] || continue

    subproject_dir=$(dirname "$config_file")
    subproject_name=$(basename "$subproject_dir")
    
    echo ""
    echo "📦 Detectado subproyecto: [ ${subproject_name} ]"
    echo "⚙️  Instalando Toolchain específico en: ${subproject_dir}..."
    
    (
        cd "$subproject_dir"
        /home/vscode/.local/bin/mise trust
        /home/vscode/.local/bin/mise install --yes
    )
done

echo ""
echo "⚙️  Regenerando los Shims globales del contenedor..."
/home/vscode/.local/bin/mise reshim

cd /workspaces
echo -e "\n🎉 ¡Ecosistema Monorrepo LabMetricsIA inicializado por completo de forma dinámica!"
