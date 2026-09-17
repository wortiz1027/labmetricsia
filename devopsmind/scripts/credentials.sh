#!/usr/bin/env bash

# Detener el script inmediatamente si ocurre algún error inesperado
set -euo pipefail

# Evitar que el script se ejecute directamente con './' para que impacte la terminal actual
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    echo "❌ Error: Exec this script using 'source' o '.' to export variables to hte current terminal." >&2
    echo "👉 Use: source ./setup-env.sh" >&2
    exit 1
fi

echo "🔐 Extract credentials directly from VAULT to memory..."

# Definir las rutas en VAULT
VAULT_API_OLLAMA_SECRET_KEY=$(gopass show -o ai/api/auth/secret-key)
VAULT_OLLAMA_SERVER_HOSTNAME=$(gopass show -o ai/api/ollama/hostname)
VAULT_LLM_MODEL_NAME=$(gopass show -o ai/api/llm/model/name)
VAULT_DB_MYSQL_ROOT_PASSWORD=$(gopass show -o ai/database/mysql/password)
VAULT_DB_MYSQL_USERNAME=$(gopass show -o ai/database/mysql/username)
VAULT_DB_MYSQL_PASSWORD=$(gopass show -o ai/database/mysql/password)
VAULT_DB_MYSQL_HOSTNAME=$(gopass show -o ai/database/mysql/hostname)
VAULT_DB_MYSQL_DATABASE_NAME=$(gopass show -o ai/database/mysql/database)
VAULT_DB_MYSQL_SCHEMA_NAME=$(gopass show -o ai/database/mysql/schema)
VAULT_DB_POSTGRES_USERNAME=$(gopass show -o ai/database/postgresql/username)
VAULT_DB_POSTGRES_PASSWORD=$(gopass show -o ai/database/postgresql/password)
VAULT_DB_POSTGRES_HOSTNAME=$(gopass show -o ai/database/postgresql/hostname)
VAULT_DB_POSTGRES_VECTOR_NAME=$(gopass show -o ai/database/postgresql/database)
VAULT_DB_PGADMIN_EMAIL=$(gopass show -o ai/database/pgadmin/email)
VAULT_DB_PGADMIN_PASSWORD=$(gopass show -o ai/database/pgadmin/password)
VAULT_DB_MONGO_ROOT_USERNAME=$(gopass show -o ai/database/mongo/username)
VAULT_DB_MONGO_ROOT_PASSWORD=$(gopass show -o ai/database/mongo/password)
VAULT_DB_MONGO_HOSTNAME=$(gopass show -o ai/database/mongo/hostname)
VAULT_DB_MONGO_INITIAL_DB=$(gopass show -o ai/database/mongo/database)
VAULT_DB_MONGOEX_ADMIN_USERNAME=$(gopass show -o ai/database/mongoex/username)
VAULT_DB_MONGOEX_ADMIN_PASSWROD=$(gopass show -o ai/database/mongoex/password)

if ! command -v gopass &> /dev/null; then
    echo "❌ Error: gopass is not installed in the host machine." >&2
    return 1
fi

# Exportar directamente a las variables de entorno locales
export API_OLLAMA_SECRET_KEY=$(gopass show -o $VAULT_API_OLLAMA_SECRET_KEY)

export OLLAMA_SERVER_HOSTNAME=$(gopass show -o $VAULT_OLLAMA_SERVER_HOSTNAME)
export LLM_MODEL_NAME=$(gopass show -o $VAULT_LLM_MODEL_NAME)

export DB_MYSQL_ROOT_PASSWORD=$(gopass show -o $VAULT_DB_MYSQL_ROOT_PASSWORD)
export DB_MYSQL_USERNAME=$(gopass show -o $VAULT_DB_MYSQL_USERNAME)
export DB_MYSQL_PASSWORD=$(gopass show -o $VAULT_DB_MYSQL_PASSWORD)
export DB_MYSQL_HOSTNAME=$(gopass show -o $VAULT_DB_MYSQL_HOSTNAME)
export DB_MYSQL_DATABASE_NAME=$(gopass show -o $VAULT_DB_MYSQL_DATABASE_NAME)
export DB_MYSQL_SCHEMA_NAME=$(gopass show -o $VAULT_DB_MYSQL_SCHEMA_NAME)
export DB_POSTGRES_USERNAME=$(gopass show -o $VAULT_DB_POSTGRES_USERNAME)
export DB_POSTGRES_PASSWORD=$(gopass show -o $VAULT_DB_POSTGRES_PASSWORD)
export DB_POSTGRES_HOSTNAME=$(gopass show -o $VAULT_DB_POSTGRES_HOSTNAME)
export DB_POSTGRES_VECTOR_NAME=$(gopass show -o $VAULT_DB_POSTGRES_VECTOR_NAME)
export DB_PGADMIN_EMAIL=$(gopass show -o $VAULT_DB_PGADMIN_EMAIL)
export DB_PGADMIN_PASSWORD=$(gopass show -o $VAULT_DB_PGADMIN_PASSWORD)
export DB_MONGO_ROOT_USERNAME=$(gopass show -o $VAULT_DB_MONGO_ROOT_USERNAME)
export DB_MONGO_ROOT_PASSWORD=$(gopass show -o $VAULT_DB_MONGO_ROOT_PASSWORD)
export DB_MONGO_HOSTNAME=$(gopass show -o $VAULT_DB_MONGO_HOSTNAME)
export DB_MONGO_INITIAL_DB=$(gopass show -o $VAULT_DB_MONGO_INITIAL_DB)
export DB_MONGOEX_ADMIN_USERNAME=$(gopass show -o $VAULT_DB_MONGOEX_ADMIN_USERNAME)
export DB_MONGOEX_ADMIN_PASSWROD=$(gopass show -o $VAULT_DB_MONGOEX_ADMIN_PASSWROD)

echo "✅ Variables [DB_USERNAME, DB_PASSWORD, DB_HOSTNAME, DB_NAME, DB_SCHEMA, API_SECRET_KEY] have been loaded succesfully in terminal environment..."
