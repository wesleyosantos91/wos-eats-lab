#!/bin/bash
set -e

echo "Creating multiple databases..."

for db in catalog; do
  echo "Creating database: $db"
  createdb -U postgres $db || echo "Database $db already exists"

  # Opcional: criar schema padrão em cada database
  psql -v ON_ERROR_STOP=1 --username postgres -d $db <<-EOSQL
    CREATE SCHEMA IF NOT EXISTS ${db}_schema;
    GRANT ALL ON SCHEMA ${db}_schema TO postgres;
    GRANT CREATE ON SCHEMA ${db}_schema TO postgres;
EOSQL
done

echo "All databases created successfully"