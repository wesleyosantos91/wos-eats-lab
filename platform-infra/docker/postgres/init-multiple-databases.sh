#!/bin/bash
set -e

echo "Creating multiple databases..."

for db in keycloak kong; do
  echo "Creating database: $db"
  createdb -U infra $db || echo "Database $db already exists"

  # Opcional: criar schema padrão em cada database
  psql -v ON_ERROR_STOP=1 --username infra -d $db <<-EOSQL
    CREATE SCHEMA IF NOT EXISTS ${db}_schema;
    GRANT ALL ON SCHEMA ${db}_schema TO infra;
    GRANT CREATE ON SCHEMA ${db}_schema TO infra;
EOSQL
done

echo "All databases created successfully"