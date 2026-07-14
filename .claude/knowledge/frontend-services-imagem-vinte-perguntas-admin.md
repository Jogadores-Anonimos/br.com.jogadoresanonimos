# ImagemService e métodos admin de VintePerguntasService (2026-07-12)

## O que foi feito
Fechados os dois gaps identificados por outro agente ao construir telas de
admin: faltava um service Angular para upload/remoção de imagem (o backend já
tinha o endpoint pronto) e o `VintePerguntasService` só cobria a leitura
pública, sem os métodos admin de listar/editar. Segui o mesmo padrão já usado
em `arquivo.ts`/`arquivo.model.ts`/`arquivo.mock.ts` (envelope `ApiEnvelope<T>`,
flag `environment.useMocks`, mocks em arquivo `*.mock.ts` separado, comentário
`// TODO: substituir por chamada real quando o endpoint estiver disponível`).

### Arquivos criados
- `frontend/src/app/services/imagem.model.ts` — `ImagemResponse` (`id`,
  `nomeOriginal`, `tamanhoBytes`, `alt`, `url`, `criadoEm`), `ImagemUploadRequest`
  (`arquivo: File`, `alt: string`), `MensagemResponse`. Shape espelha
  `ImagemResponse` do backend (`imagem/dto/ImagemResponse.java`) e o multipart
  esperado por `ImagemController.upload` (campos `arquivo` + `alt`).
- `frontend/src/app/services/imagem.mock.ts` — um registro mockado.
- `frontend/src/app/services/imagem.ts` — classe `Imagem` (`providedIn: root`)
  com `uploadImagem(req: ImagemUploadRequest): Observable<ApiEnvelope<ImagemResponse>>`
  (monta `FormData` com `arquivo` + `alt`, `POST /admin/imagens`) e
  `removerImagem(id: string): Observable<ApiEnvelope<MensagemResponse>>`
  (`DELETE /admin/imagens/{id}`).

### Arquivos editados
- `frontend/src/app/services/vinte-perguntas.model.ts` — adicionado
  `VinteQuestaoUpdateRequest` (`{ texto: string }`), espelha
  `VinteQuestaoUpdateRequest.java` do backend.
- `frontend/src/app/services/vinte-perguntas.ts` — adicionados
  `listarAdmin(): Observable<ApiEnvelope<VinteQuestaoResponse[]>>`
  (`GET /admin/vinte-perguntas`) e
  `atualizarPergunta(numero: number, req: VinteQuestaoUpdateRequest): Observable<ApiEnvelope<VinteQuestaoResponse>>`
  (`PUT /admin/vinte-perguntas/{numero}`). Reusa o mesmo array
  `VINTE_PERGUNTAS_MOCK` já existente (mutação in-place do item encontrado por
  `numero`, mesmo padrão de "mock mutável só para dev local" já usado em CMS).

## Trade-offs e decisões
- Não criei `imagem.model.ts` com campo de `imagemId` referenciando CMS — o
  contrato de CMS (`CmsBlocoImagem.imagemId`) já existe separado e continua
  desacoplado deste service; a integração entre os dois é responsabilidade de
  quem monta o componente de editor, não deste service.
- `listarAdmin()` e `listar()` (público) têm assinatura idêntica hoje porque o
  backend devolve o mesmo shape (`numero`, `texto`) nos dois endpoints — mas
  mantive dois métodos/duas URLs distintas (`/vinte-perguntas` vs
  `/admin/vinte-perguntas`) em vez de reusar um único método com parâmetro,
  porque são endpoints com autenticação/autorização diferentes
  (`@PreAuthorize("hasRole('ADMIN')")` só no admin) e podem divergir de shape
  no futuro (ex: admin passar a incluir auditoria de quem editou por último).
- Não toquei em nenhum componente/página — escopo explícito era só a camada de
  services, conforme pedido.

## Validação
- `npx tsc --noEmit` rodado em `frontend/` — sem erros.

## Onde no projeto
- `frontend/src/app/services/imagem.ts`, `imagem.model.ts`, `imagem.mock.ts`
- `frontend/src/app/services/vinte-perguntas.ts`, `vinte-perguntas.model.ts`
- Contrato de referência no backend:
  `backend/src/main/java/br/com/jogadoresanonimos/imagem/controller/ImagemController.java`,
  `backend/src/main/java/br/com/jogadoresanonimos/vinteperguntas/controller/VinteQuestaoAdminController.java`

## Pendência para o próximo agente
`frontend/src/app/pages/admin/pagina-editor/` ainda tem aviso de "pendência"
no lugar da chamada real para bloco de imagem e seção de vinte-perguntas —
pode ser atualizado agora para consumir `Imagem.uploadImagem`/`removerImagem`
e `VintePerguntasService.listarAdmin`/`atualizarPergunta`.
