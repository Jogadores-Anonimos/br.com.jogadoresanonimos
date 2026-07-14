# Backend — Jogadores Anônimos do Brasil

Spring Boot 3.2+ / Java 21+, PostgreSQL via Flyway.

## Ambiente local

O ambiente sobe via `docker compose up` a partir da raiz do projeto
(`postgres`, `backend`, `frontend`). O container `backend` usa bind mount
de `backend/src`, então alterações de código Java/recursos aparecem no
container sem rebuild — **exceto migrations do Flyway**, que só rodam no
boot da JVM (ver seção abaixo).

## Checar se o schema do container está desatualizado

Problema comum: alguém adiciona uma migration nova em
`backend/src/main/resources/db/migration`, mas o container `backend` já
está de pé há um tempo. Como o processo Java não reinicia sozinho quando
um arquivo novo aparece no bind mount, o Flyway não roda de novo — o
schema do banco fica desatualizado e aparecem erros confusos (500
genérico, dado ausente) até perceber que era preciso reiniciar o
container.

Antes de considerar o ambiente saudável (principalmente depois de um
`git pull` ou de criar uma migration nova), rode a partir de `backend/`:

```bash
# Linux/macOS/WSL/Git Bash
./check-migrations.sh
```

```powershell
# Windows PowerShell
.\check-migrations.ps1
```

O script compara os arquivos `V*.sql` de
`backend/src/main/resources/db/migration` com o que está registrado em
`flyway_schema_history` no Postgres do `docker compose` (via
`docker compose exec postgres psql`). Requer o `docker compose up` já
rodando.

- Saída `OK` (exit code 0): schema em dia, nada a fazer.
- Saída `ATENÇÃO` com lista de migrations pendentes (exit code 3): rode
  `docker compose up -d --build backend` para a JVM reiniciar e o Flyway
  aplicar o que falta.
- Erro de conexão (exit code 2): confira se `docker compose up` está
  rodando e se `.env` (raiz do projeto) está preenchido.

Esse script é apenas para uso local/manual — não faz parte de nenhum
pipeline de CI.
