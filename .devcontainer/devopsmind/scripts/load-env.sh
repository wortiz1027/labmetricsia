#!/usr/bin/env bash

# 1. Localizador dinámico del archivo .env dentro de la estructura montada
ENV_TARGET=""

if [ -f "./.devcontainer/devopsmind/containers/.env" ]; then
    ENV_TARGET="./.devcontainer/devopsmind/containers/.env"
elif [ -f "../.devcontainer/devopsmind/containers/.env" ]; then
    ENV_TARGET="../.devcontainer/devopsmind/containers/.env"
elif [ -f "/workspaces/.devcontainer/devopsmind/containers/.env" ]; then
    ENV_TARGET="/workspaces/.devcontainer/devopsmind/containers/.env"
fi

# 2. Validar si encontramos el archivo físico en el disco
if [ -z "$ENV_TARGET" ]; then
    echo "❌ Error: No se localizó el archivo .env de secretos en las rutas del entorno."
    return 1 2>/dev/null || exit 1
fi

echo "🔐 [LabMetricsIA] Cargando secretos desde: ${ENV_TARGET}"

# 3. 🎯 PROCESAMIENTO COMPATIBLE (Inmune a errores de Sh/Dash):
# - grep -v '^#' -> Remueve las líneas de comentarios
# - grep -v '^$' -> Remueve las líneas vacías para evitar el error 'bad variable name'
# - xargs        -> Limpia espacios en blanco alrededor de los textos
# - export       -> Inyecta todas las variables en un solo pestañeo nativo de Unix
export $(grep -v '^#' "$ENV_TARGET" | grep -v '^$' | xargs)

echo "✅ ¡Variables de entorno exportadas con éxito en la sesión actual!"
