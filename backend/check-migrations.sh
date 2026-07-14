#!/usr/bin/env bash
# Verifica se ha migration nova em backend/src/main/resources/db/migration
# que ainda nao foi aplicada no container Postgres do docker compose.
#
# Motivo: o container backend (Dockerfile.dev) roda "mvnw spring-boot:run"
# uma unica vez no "docker compose up". Como backend/src e bind mount, editar
# arquivos no host (inclusive uma migration nova) NAO reinicia a JVM sozinho,
# entao o Flyway nao roda de novo ate um restart/rebuild do container. Isso
# gera schema desatualizado e erros confusos (500 generico, dado ausente).
#
# Uso: ./check-migrations.sh   (rodar a partir de backend/, com docker compose up)

set -euo pipefail

cd "$(dirname "$0")"
MIGRATION_DIR="src/main/resources/db/migration"
COMPOSE_FILE="../docker-compose.yml"

if [ ! -d "$MIGRATION_DIR" ]; then
  echo "Diretorio de migrations nao encontrado: $MIGRATION_DIR" >&2
  exit 1
fi

DB_NAME="$(grep -m1 '^DB_NAME=' ../.env 2>/dev/null | cut -d= -f2-)"
DB_USERNAME="$(grep -m1 '^DB_USERNAME=' ../.env 2>/dev/null | cut -d= -f2-)"
DB_NAME="${DB_NAME:-jogadoresanonimos}"
DB_USERNAME="${DB_USERNAME:-postgres}"

APPLIED="$(docker compose -f "$COMPOSE_FILE" exec -T postgres \
  psql -U "$DB_USERNAME" -d "$DB_NAME" -tAc \
  "select version from flyway_schema_history where success = true order by installed_rank;" 2>/dev/null || true)"

if [ -z "$APPLIED" ]; then
  echo "Nao foi possivel consultar flyway_schema_history. O container postgres esta rodando (docker compose up)?" >&2
  exit 2
fi

PENDING=()
for file in "$MIGRATION_DIR"/V*.sql; do
  [ -e "$file" ] || continue
  base="$(basename "$file")"
  version="$(echo "$base" | sed -E 's/^V([0-9]+(_[0-9]+)*)__.*/\1/')"
  if ! echo "$APPLIED" | tr -d ' ' | grep -qx "$version"; then
    PENDING+=("$base")
  fi
done

if [ ${#PENDING[@]} -eq 0 ]; then
  echo "OK: schema do container esta em dia com as migrations em $MIGRATION_DIR."
  exit 0
else
  echo "ATENCAO: existem migrations no host ainda NAO aplicadas no container:"
  for f in "${PENDING[@]}"; do
    echo "  - $f"
  done
  echo
  echo "Rode: docker compose up -d --build backend"
  exit 3
fi
