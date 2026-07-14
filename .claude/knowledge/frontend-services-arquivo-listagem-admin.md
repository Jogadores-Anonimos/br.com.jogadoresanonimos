# frontend-services: listagem admin de arquivos

- Adicionado `listarArquivosAdmin()` em `frontend/src/app/services/arquivo.ts`,
  espelhando o novo endpoint backend `GET /api/v1/admin/arquivos`
  (`hasRole('ADMIN')`, resposta `ApiResponse<List<ArquivoResponse>>`).
- Assinatura: `Observable<ApiEnvelope<ArquivoResponse[]>>`. Reaproveita a
  interface `ArquivoResponse` já existente em `arquivo.model.ts` — nenhum
  campo novo foi necessário porque o DTO backend
  (`ArquivoResponse.java`) já era 1:1 com o que o frontend já tipava.
- Mock: reutiliza `ARQUIVOS_MOCK` de `arquivo.mock.ts` (mesmo array usado
  por upload/editar/deletar), ordenando por `criadoEm` desc numa cópia do
  array (evita mutar o mock compartilhado). Segue o padrão de
  `environment.useMocks` + `catchError` + comentário TODO já usado nos
  outros métodos do service.
- Motivo de não criar um mock separado: o domínio já tinha um arquivo de
  mock único para todo o CRUD de Arquivo; centralizar evita duplicidade
  de estado mockado entre métodos de leitura e escrita.
- Consumidor esperado: página admin em
  `frontend/src/app/pages/admin/admin-literatura/` (ainda não construída
  no momento deste registro).
