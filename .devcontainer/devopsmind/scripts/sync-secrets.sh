#!/usr/bin/env bash

[[ -f "$HOME/.bashrc" ]] && source "$HOME/.bashrc"
[[ -f "$HOME/.zshrc" ]] && source "$HOME/.zshrc"

set -e

echo "🔐 Extrayendo secretos de gopass hacia archivos temporales (Monorrepo y /tmp)..."

if ! command -v gopass &> /dev/null; then
    if [ -f "$HOME/.local/bin/gopass" ]; then
        alias gopass="$HOME/.local/bin/gopass"
    elif [ -f "/usr/local/bin/gopass" ]; then
        alias gopass="/usr/local/bin/gopass"
    else
        echo "❌ Error: gopass no se encontró en el PATH ni en rutas locales del host." >&2
        exit 1
    fi
fi

export GOPASS_NO_INTERACTIVE=true

# 🎯 LA MEJORA CLAVE: Usamos 'tee' para redirigir el bloque de texto simultáneamente 
# hacia la carpeta de tu monorrepo y hacia el directorio /tmp global de tu máquina host.
cat << EOF | tee .devcontainer/devopsmind/containers/.env /tmp/.env > /dev/null
SECRET_MANAGER_HOSTNAME=$(gopass show -o ai/api/auth/secret-vault-host || echo "localhost")
TOKEN_SECRET_MANAGER=$(gopass show -o ai/api/auth/secret-vault-token || echo "default-token-fail")
API_OLLAMA_SECRET_KEY=$(gopass show -o ai/api/auth/secret-key)

DB_MYSQL_ROOT_PASSWORD=$(gopass show -o ai/database/mysql/password)
DB_MYSQL_USERNAME=$(gopass show -o ai/database/mysql/username)
DB_MYSQL_PASSWORD=$(gopass show -o ai/database/mysql/password)
DB_MYSQL_HOSTNAME=$(gopass show -o ai/database/mysql/hostname)
DB_MYSQL_DATABASE_NAME=$(gopass show -o ai/database/mysql/database)
DB_MYSQL_SCHEMA_NAME=$(gopass show -o ai/database/mysql/schema)

DB_POSTGRES_USERNAME=$(gopass show -o ai/database/postgresql/username)
DB_POSTGRES_PASSWORD=$(gopass show -o ai/database/postgresql/password)
DB_POSTGRES_HOSTNAME=$(gopass show -o ai/database/postgresql/hostname)
DB_POSTGRES_VECTOR_NAME=$(gopass show -o ai/database/postgresql/database)

DB_PGADMIN_EMAIL=$(gopass show -o ai/database/pgadmin/email)
DB_PGADMIN_PASSWORD=$(gopass show -o ai/database/pgadmin/password)

DB_MONGO_ROOT_USERNAME=$(gopass show -o ai/database/mongo/username)
DB_MONGO_ROOT_PASSWORD=$(gopass show -o ai/database/mongo/password)
DB_MONGO_HOSTNAME=$(gopass show -o ai/database/mongo/hostname)
DB_MONGO_INITIAL_DB=$(gopass show -o ai/database/mongo/database)

DB_MONGOEX_ADMIN_USERNAME=$(gopass show -o ai/database/mongoex/username)
DB_MONGOEX_ADMIN_PASSWROD=$(gopass show -o ai/database/mongoex/password)
EOF

echo "✅ Archivos de secretos generados con éxito en el monorrepo y en /tmp/.env"