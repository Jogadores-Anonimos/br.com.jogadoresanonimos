#!/usr/bin/env bash
set -euo pipefail

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
: "${RCLONE_REMOTE:?}"

STAMP="$(date -u +%Y%m%d-%H%M%S)"
WORKDIR="$(mktemp -d)"
trap 'rm -rf "$WORKDIR"' EXIT

DUMP_FILE="$WORKDIR/postgres-$STAMP.sql.gz"
STORAGE_TAR="$WORKDIR/storage-$STAMP.tar.gz"
BUNDLE="$WORKDIR/jaa-backup-$STAMP.tar"
ENCRYPTED="$WORKDIR/jaa-backup-$STAMP.tar.gpg"

POSTGRES_CONTAINER="$(docker ps -q -f name=jaa_postgres | head -n1)"
if [ -z "$POSTGRES_CONTAINER" ]; then
  echo "container jaa_postgres nao encontrado, abortando" >&2
  exit 1
fi

docker exec -e PGPASSWORD="$DB_PASSWORD" "$POSTGRES_CONTAINER" \
  pg_dump -U "$DB_USERNAME" -d "$DB_NAME" --format=plain | gzip -9 > "$DUMP_FILE"

STORAGE_VOLUME_NAME="${STORAGE_VOLUME_NAME:-jaa_backend_storage}"
docker run --rm \
  -v "$STORAGE_VOLUME_NAME:/data:ro" \
  -v "$WORKDIR:/backup-out" \
  alpine \
  tar -czf "/backup-out/$(basename "$STORAGE_TAR")" -C /data .

tar -cf "$BUNDLE" -C "$WORKDIR" "$(basename "$DUMP_FILE")" "$(basename "$STORAGE_TAR")"

gpg --batch --yes --passphrase "$BACKUP_GPG_PASSPHRASE" --symmetric --cipher-algo AES256 -o "$ENCRYPTED" "$BUNDLE"

LOCAL_BACKUP_DIR="${LOCAL_BACKUP_DIR:-$HOME/backups}"
mkdir -p "$LOCAL_BACKUP_DIR"
cp "$ENCRYPTED" "$LOCAL_BACKUP_DIR/"

rclone copy "$ENCRYPTED" "$RCLONE_REMOTE" --quiet

RETENTION_DAYS="${RETENTION_DAYS:-7}"
find "$LOCAL_BACKUP_DIR" -name 'jaa-backup-*.tar.gpg' -mtime +"$RETENTION_DAYS" -delete
rclone delete "$RCLONE_REMOTE" --min-age "${RETENTION_DAYS}d" --quiet

echo "backup ok: $(basename "$ENCRYPTED")"
