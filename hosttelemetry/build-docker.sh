#!/usr/bin/env bash

# ==============================================================================
# 🚦 CONFIGURACIÓN Y PROTOCOLO DE ERRORES (Estilo Fail-Fast)
# ==============================================================================
set -euo pipefail
IFS=$'\n\t'

# 🔥 Colores ANSI para una salida visual interactiva en tu DevContainer
readonly CLR_GREEN='\033[0;32m'
readonly CLR_CYAN='\033[0;36m'
readonly CLR_YELLOW='\033[1;33m'
readonly CLR_RED='\033[0;31m'
readonly CLR_RESET='\033[0s'

# 📝 Variables globales estáticas de la imagen
readonly REGISTRY_USER="wortiz1027"
readonly IMAGE_NAME="mcp-server-host-telemetry"

# ==============================================================================
# 🛠️ FUNCIONES UTILLITARIAS Y DE CÁLCULO DINÁMICO
# ==============================================================================

log_info() { echo -e "${CLR_CYAN}[INFO]${CLR_RESET} $1"; }
log_success() { echo -e "${CLR_GREEN}[SUCCESS]${CLR_RESET} $1"; }
log_warn() { echo -e "${CLR_YELLOW}[WARN]${CLR_RESET} $1"; }
log_error() { echo -e "${CLR_RED}[ERROR]${CLR_RESET} $1"; exit 1; }

get_build_date() {
    date -u +'%Y-%m-%dT%H:%M:%SZ'
}

get_build_revision() {
    git rev-parse --short HEAD 2>/dev/null || echo "unknown"
}

# ==============================================================================
# 👑 MOTOR SEMVER AUTÓNOMO (Conventional Commits Parser nativo en Bash):
# Analiza de forma inteligente el historial de commits desde el último tag
# para calcular el salto matemático exacto de la nueva versión.
# ==============================================================================
get_build_version() {
    local latest_tag
    # Buscamos el último tag que cumpla con el patrón v*.*.* (ej: v1.0.2)
    latest_tag=$(git describe --tags --abbrev=0 --match "v*.*.*" 2>/dev/null || echo "v0.0.0")

    # Removemos la 'v' inicial si existe para procesar los números limpios
    local clean_version="${latest_tag#v}"

    # Dividimos la versión actual en sus tres componentes: X.Y.Z
    local major; local minor; local patch
    IFS='.' read -r major minor patch <<< "${clean_version}"

    # Definimos los flags de incremento
    local inc_major=false
    local inc_minor=false
    local inc_patch=false

    # 🎯 Recuperamos todos los commits desde el último tag hasta el HEAD actual
    local commit_range
    if [ "${latest_tag}" = "v0.0.0" ]; then
        commit_range="HEAD" # Si no hay tags previos, escanea toda la historia
    else
        commit_range="${latest_tag}..HEAD"
    fi

    # Leemos los mensajes de los commits en el rango seleccionado
    local commit_messages
    commit_messages=$(git log "${commit_range}" --format="%s" 2>/dev/null || echo "")

    # Si no hay commits nuevos, mantenemos la versión actual del tag
    if [ -z "${commit_messages}" ]; then
        echo "${clean_version}"
        return
    fi

    # 🕵️ ESCANEO DE CONVENTIONAL COMMITS:
    while IFS= read -r msg; do
        if [[ "${msg}" == *"BREAKING CHANGE"* || "${msg}" =~ ^[a-z]+\([a-z0-9_-]+\)!: || "${msg}" =~ ^[a-z]+!: ]]; then
            inc_major=true
        elif [[ "${msg}" =~ ^feat(\([a-z0-9_-]+\))?: ]]; then
            inc_minor=true
        elif [[ "${msg}" =~ ^(fix|docs|style|refactor|test|chore)(\([a-z0-9_-]+\))?: ]]; then
            inc_patch=true
        fi
    done <<< "${commit_messages}"

    # 💥 APLICACIÓN DE PRIORIDAD MATEMÁTICA SEMVER:
    if [ "${inc_major}" = true ]; then
        major=$((major + 1))
        minor=0
        patch=0
    elif [ "${inc_minor}" = true ]; then
        minor=$((minor + 1))
        patch=0
    elif [ "${inc_patch}" = true ]; then
        patch=$((patch + 1))
    else
        # Si los commits no siguen el estándar, aplicamos un patch de fallback por seguridad
        patch=$((patch + 1))
    fi

    # Retornamos la nueva versión semántica calculada de forma pura
    echo "${major}.${minor}.${patch}"
}

# ==============================================================================
# 🏗️ FUNCIÓN CORE: CONSTRUCCIÓN DE LA IMAGEN DOCKER
# ==============================================================================
execute_docker_build() {
    log_info "Calculando metadatos y estrategias de versionado dinámico..."

    local build_date
    local build_revision
    local build_version

    build_date=$(get_build_date)
    build_revision=$(get_build_revision)
    build_version=$(get_build_version)

    # Definimos las variables de los nombres de etiquetas de Docker Hub
    local target_tag_version="${REGISTRY_USER}/${IMAGE_NAME}:${build_version}"
    local target_tag_revision="${REGISTRY_USER}/${IMAGE_NAME}:${build_revision}"
    local target_tag_latest="${REGISTRY_USER}/${IMAGE_NAME}:latest"

    log_info "--------------------------------------------------------"
    log_info "📊 METADATOS INYECTADOS POR BUILD-ARGS:"
    log_info "   • Build Date:     ${build_date}"
    log_info "   • Build Version:  ${build_version}"
    log_info "   • Build Revision: ${build_revision}"
    log_info "🏷️ ESTRATEGIA DE ETIQUETADO (TAGS):"
    log_info "   • Tag SemVer:     ${target_tag_version}"
    log_info "   • Tag Git Hash:   ${target_tag_revision}"
    log_info "   • Tag Latest:     ${target_tag_latest}"
    log_info "--------------------------------------------------------"

    log_info "Iniciando compilación multi-stage de Docker libre de caché..."

    docker build \
      --no-cache \
      --build-arg BUILD_DATE="${build_date}" \
      --build-arg BUILD_VERSION="${build_version}" \
      --build-arg BUILD_REVISION="${build_revision}" \
      -t "${target_tag_version}" \
      -t "${target_tag_revision}" \
      -t "${target_tag_latest}" .

    log_success "¡Imagen Docker compilada exitosamente con todos sus metadatos!"
}

# ==============================================================================
# 🏃 FLUJO PRINCIPAL DE EJECUCIÓN
# ==============================================================================
main() {
    log_info "=== 🛠️ AUTOMATIZACIÓN DE PIPELINE DOCKER PARA SERVIDOR MCP ==="

    if [[ ! -f "Dockerfile" ]]; then
        log_error "No se encontró el Dockerfile físico en el directorio actual ($(pwd))."
    fi

    if [[ ! -d "target" ]]; then
        log_warn "La carpeta 'target/' no existe. Se requiere compilar el empaquetado de Maven."
        log_info "Ejecutando empaquetado automático de Maven..."
        mvn clean package -DskipTests
    fi

    execute_docker_build

    log_success "Proceso de automatización finalizado con éxito absoluto."
    echo -e "\nPara subir las imágenes a Docker Hub ejecuta el comando:"
    echo -e "${CLR_YELLOW}docker push ${REGISTRY_USER}/${IMAGE_NAME} --all-tags${CLR_RESET}\n"
}

main
