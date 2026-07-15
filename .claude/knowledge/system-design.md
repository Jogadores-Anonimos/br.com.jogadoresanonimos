### CDN para storage de imagem/arquivo (pendente)
- **Onde**: `Decisoes de Sistemas.md` (seção 5, trade-off 4), `PENDENCIAS.md` (seção "Infraestrutura / Deploy")
- **Propósito**: distribuição geográfica/cache para arquivos servidos por `LocalDiskStorageClient` (ver `.claude/knowledge/backend.md`)
- **Como**: solução prevista é Cloudflare (ou equivalente) na frente do domínio, origin apontando direto pro backend/disco local, sem bucket S3 externo
- **Trade-off**: não implementado — bloqueado por hospedagem/domínio de produção ainda não escolhidos pelo cliente, não por volume de tráfego
