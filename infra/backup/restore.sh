#!/usr/bin/env bash
set -euo pipefail

ENCRYPTED_FILE="${1:?uso: restore.sh caminho/para/jaa-backup-TIMESTAMP.tar.gpg}"

ENV_FILE="${BACKUP_ENV_FILE:-$HOME/.env.backup}"
if [ -f "$ENV_FILE" ]; then
  set -a
  source "$ENV_FILE"
  set +a
fi

: "${DB_NAME:?}"
: "${DB_USERNAME:?}"
: "${DB_PASSWORD:?}"
: "${BACKUP_GPG_PASSPHRASE:?}"

WORKDIR="$(mktemp -d)"
trap 'rm -rf "$WORKDIR"' EXIT

BUNDLE="$WORKDIR/bundle.tar"
gpg --batch --yes --passphrase "$BACKUP_GPG_PASSPHRASE" --decrypt -o "$BUNDLE" "$ENCRYPTED_FILE"

tar -xf "$BUNDLE" -C "$WORKDIR"

DUMP_FILE="$(find "$WORKDIR" -name 'postgres-*.sql.gz' | head -n1)"
STORAGE_TAR="$(find "$WORKDIR" -name 'storage-*.tar.gz' | head -n1)"

echo "1/3 restaurando Postgres a partir de $DUMP_FILE"
POSTGRES_CONTAINER="$(docker ps -q -f name=jaa_postgres | head -n1)"
if [ -z "$POSTGRES_CONTAINER" ]; then
  echo "container jaa_postgres nao encontrado, abortando" >&2
  exit 1
fi

gunzip -c "$DUMP_FILE" | docker exec -i -e PGPASSWORD="$DB_PASSWORD" "$POSTGRES_CONTAINER" \
  psql -U "$DB_USERNAME" -d "$DB_NAME"

echo "2/3 restaurando storage de imagens a partir de $STORAGE_TAR"
STORAGE_VOLUME_NAME="${STORAGE_VOLUME_NAME:-jaa_backend_storage}"
docker run --rm \
  -v "$STORAGE_VOLUME_NAME:/data" \
  -v "$WORKDIR:/backup-in" \
  alpine \
  tar -xzf "/backup-in/$(basename "$STORAGE_TAR")" -C /data

echo "3/3 concluido. Reinicie o servico backend se necessario: docker service update --force jaa_backend"
