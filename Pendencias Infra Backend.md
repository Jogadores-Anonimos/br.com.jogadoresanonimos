# Pendências de infra — decisão do usuário

> Gerado em 2026-07-12. Bloqueia integração real de `devops`/`backend`
> (hoje: banco sem conexão real definida, bucket stub, e-mail só loga).

## 1. Postgres real
- ~~Dev~~ **Decidido em 2026-07-12**: dev fica local via `docker-compose.yml`
  (Postgres em container), sem host externo por ora.
- **Prod — pendente, adiada de propósito**: cliente já tem hospedagem própria
  (usuário mencionou HostGator ou similar, nome não confirmado). Migração pra
  lá **ainda não vai acontecer agora** — revisar quando o usuário confirmar
  provedor e decidir seguir com a migração.

## 2. ~~Bucket S3-compatible~~ — encerrada em 2026-07-12
- **Decisão do usuário: abandonado.** Storage de imagem/PDF de produção
  passa a ser disco local persistente do próprio servidor, não bucket
  externo — evita decidir titularidade de conta terceira (cliente vs
  dev) antes de precisar; volume do projeto (~300 pedidos/dia, ~2.500
  requisições/dia) permite. Ver `Decisoes de Sistemas.md` seção 5 para
  os trade-offs formalizados por `system-design`.
- `backend` promoveu `StubStorageClient` (dev/teste) a
  `LocalDiskStorageClient` (produção real), interface `StorageClient`
  inalterada; env vars mantidas (`STORAGE_STUB_DIR`,
  `STORAGE_PUBLIC_BASE_URL`). `devops` ajustou volume Docker nomeado
  persistente no `docker-compose.yml` para esse diretório (backup do
  diretório continua pendência bloqueante para produção — ver seção 5
  de `Decisoes de Sistemas.md`, trade-off 2).

## 3. Provedor de e-mail
- **Adiada de propósito, junto com a decisão de hospedagem** (item 1 —
  Postgres/prod): decisão depende de saber se o cliente já tem e-mail
  corporativo/SMTP disponível na hospedagem que ele já tem (HostGator ou
  similar) — reaproveitar isso é o caminho mais simples, sem conta nova.
  Revisitar junto com a migração de hospedagem.
- Opções se precisar de serviço à parte: SMTP genérico, SendGrid, SES,
  Mailgun.

## 4. Onde guardar secrets
- **Adiada junto com hospedagem**: `JWT_SECRET`/`LOG_HMAC_SECRET` — backend
  pode gerar, mas onde ficam (`.env` local vs secret do provedor de
  hospedagem) só faz sentido decidir sabendo onde vai hospedar.

## 5. Admin bootstrap
- **Adiada junto com hospedagem**: dados do primeiro admin (apelido,
  e-mail, senha) — não bloqueia dev local, só faz sentido fechar quando
  o ambiente de produção real estiver decidido.

---

**Resumo do estado**: únicos itens fechados por ora são Postgres dev
(local via compose) e storage (disco local, não mais bucket). Tudo que
depende de saber onde o site vai hospedar (Postgres prod, e-mail,
secrets, admin bootstrap) fica junto, aguardando o usuário decidir/
confirmar provedor de hospedagem (mencionado: HostGator ou similar).
