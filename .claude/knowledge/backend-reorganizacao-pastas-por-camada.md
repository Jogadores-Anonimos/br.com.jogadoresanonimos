# Backend — reorganização de pastas: domínio + subpasta técnica

**Data**: 2026-07-12
**Agente**: backend

## O que foi feito

A estrutura inicial do backend (ver `backend-implementacao-inicial.md`)
organizava por domínio, mas com todas as classes soltas direto na pasta
do domínio (ex: `auth/AuthController.java`, `auth/AuthService.java`,
`auth/PasswordResetToken.java` todos no mesmo nível). Isso foi revisto:
agora cada domínio tem subpastas técnicas explícitas dentro dele.

Continua sendo separação por domínio/feature (não por camada pura tipo
`controllers/`, `services/` na raiz) — só que agora com um nível a mais
de subpasta técnica dentro de cada domínio. Ou seja, o padrão é:
`<dominio>/<camada-tecnica>/Classe.java`.

## Mapeamento final por domínio

```
auth/
├── controller/  AuthController
├── service/      AuthService, EmailService
├── repository/   PasswordResetTokenRepository
├── entity/       PasswordResetToken
└── dto/          (já existia, não mudou)

cms/
├── controller/  CmsAdminController, CmsPublicController
├── service/      CmsService
├── repository/   CmsAlteracaoLogRepository, CmsBlocoRepository, CmsPaginaRepository
├── entity/       CmsAlteracaoLog, CmsBloco, CmsPagina, TipoBloco
├── util/         HtmlSanitizer
└── dto/          (já existia, não mudou)

common/
├── util/         HmacHasher
├── dto/          (já existia)
├── exception/    (já existia)
└── ratelimit/    (já existia)

imagem/
├── controller/  ImagemController
├── service/      ImagemService, ImagemProcessor, StorageClient,
│                  StubStorageClient, ImagemOrfaGcJob
├── repository/   ImagemRepository
├── entity/       Imagem
└── dto/          (já existia, não mudou)

usuario/
├── controller/  UsuarioController
├── service/      AdminBootstrap
├── repository/   UsuarioRepository
├── entity/       Usuario, Role
└── dto/          (já existia, não mudou)
```

`config/` e `security/` (nível raiz, transversais a todos os domínios)
não foram mexidos — permanecem como estavam, por decisão explícita da
tarefa (não são de um domínio específico).

## Decisões de bom senso para casos ambíguos

- **Enums de domínio** (`Role`, `TipoBloco`): foram para `entity/`, não
  ganharam pasta própria. Motivo: representam conceito de domínio/dado
  persistido (`Role` é coluna do `Usuario`, `TipoBloco` é coluna do
  `CmsBloco`), não são utilitário genérico.
- **Utilitário sem estado ligado a lógica pura** (`HtmlSanitizer`,
  `HmacHasher`): foram para `util/`. Motivo: não dependem de repository/
  banco, são função pura reutilizável, mais parecido com helper do que
  com "service" que orquestra regra de negócio.
- **Processor/client técnico usado só por um service**
  (`ImagemProcessor`, `StorageClient`/`StubStorageClient`): foram para
  `service/` junto do `ImagemService`, não para `util/`. Motivo: são
  colaboradores diretos e específicos de um único service (não são
  reusados por múltiplos domínios como `HmacHasher` é), então ficou mais
  coeso mantê-los perto de quem os usa.
- **Job agendado** (`ImagemOrfaGcJob`) e **bootstrap de inicialização**
  (`AdminBootstrap`, um `CommandLineRunner`): foram para `service/`.
  Motivo: tecnicamente são só um `@Component`/`@Scheduled` que orquestra
  regra de negócio (GC de imagem órfã, criação do admin inicial) — mesmo
  papel arquitetural de um service, só que disparado por scheduler/
  bootstrap do Spring em vez de um controller.

## Impacto de imports

Mover uma classe de `dominio/Classe.java` para
`dominio/subpasta/Classe.java` muda o `package` dela. Isso quebra
qualquer outro arquivo do mesmo domínio que a referenciava sem import
explícito (Java só dispensa import para classes do *mesmo* pacote — ao
criar subpacotes, esse acoplamento implícito se perde). Foi necessário:
- Atualizar a declaração `package` em cada arquivo movido.
- Adicionar imports explícitos em quem referenciava essas classes
  (inclusive dentro do próprio domínio — ex: `CmsService` agora precisa
  importar `CmsPagina`, `CmsBloco` etc. que antes estavam no mesmo
  pacote implícito).
- O mesmo vale para `Repository<Entity, ID>` do Spring Data: toda
  interface repository precisou de import explícito da entidade que
  referencia no generic (`JpaRepository<Usuario, UUID>` etc.), pois
  entidade e repository agora estão em subpacotes diferentes
  (`entity/` vs `repository/`).

Testes em `backend/src/test/java/...` foram movidos espelhando a mesma
estrutura (`auth/service/AuthServiceTest.java`,
`cms/service/CmsServiceTest.java`, `cms/util/HtmlSanitizerTest.java`) e
tiveram os mesmos ajustes de import.

## Resultado

Build (`./mvnw test`, modo offline `-o`) compila limpo. 23 testes
unitários (Mockito, sem banco) passam: `AuthServiceTest` (11),
`CmsServiceTest` (6), `HtmlSanitizerTest` (6). O teste de contexto
`JogadoresAnonimosApplicationTests.contextLoads` falha por falta de
Postgres rodando localmente (erro de autenticação SCRAM na conexão) —
isso é uma condição de ambiente (precisa `docker compose up` primeiro),
não uma regressão da reorganização de pastas.

## Onde no projeto

`backend/src/main/java/br/com/jogadoresanonimos/{auth,cms,common,imagem,usuario}/`
e `backend/src/test/java/br/com/jogadoresanonimos/{auth,cms}/`.
