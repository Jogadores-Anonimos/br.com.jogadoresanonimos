### Auditoria de performance 2026-07-17 — achados sem ação (volume não justifica)
- **Onde**: `reuniao/controller` (`GET /api/v1/reunioes`), tabela `reuniao` (`dia_semana`/`periodo`)
- **Propósito**: registrar decisão consciente de não agir em 2 pontos levantados na auditoria pós-conteúdo real
- **Como**: `/api/v1/reunioes` sem paginação (94 registros, endpoint alimenta mapa que precisa do conjunto completo — paginação quebraria a UX); índice composto `dia_semana`/`periodo` não ajudaria (filtro é 100% client-side hoje)

### CDN para storage de imagem/arquivo (pendente)
- **Onde**: `Decisoes de Sistemas.md` (seção 5, trade-off 4), `PENDENCIAS.md` (seção "Infraestrutura / Deploy")
- **Propósito**: distribuição geográfica/cache para arquivos servidos por `LocalDiskStorageClient` (ver `.claude/knowledge/backend.md`)
- **Como**: solução prevista é Cloudflare (ou equivalente) na frente do domínio, origin apontando direto pro backend/disco local, sem bucket S3 externo
- **Trade-off**: não implementado — bloqueado por hospedagem/domínio de produção ainda não escolhidos pelo cliente, não por volume de tráfego
