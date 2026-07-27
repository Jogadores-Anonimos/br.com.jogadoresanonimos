# Contrato de erro padrão da API

Aplica-se a toda resposta de erro de todo endpoint do backend
(`{ success, data, error }` com `success = false`, `data = null`).

## Formato

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "AUTH_INVALID_CREDENTIALS",
    "message": "Apelido ou senha incorretos.",
    "fields": null,
    "details": null,
    "traceId": "3f9b2b3a-..."
  }
}
```

- `error.code`: string estável, namespaced por domínio
  (`AUTH_`, `MFA_`, `USUARIO_`, `RELATO_`, `UPLOAD_`, `ARQUIVO_`,
  `IMAGEM_`, `CMS_`, `MURAL_`, `AVISO_`, `FAQ_`, `REFLEXAO_`,
  `VINTE_PERGUNTAS_`, `REUNIAO_`, `ESCRITORIO_`, `HOTLINE_`,
  `BIBLIOTECA_`, `AUDITORIA_`, `VALIDATION_ERROR`,
  `UPLOAD_TOO_LARGE`, `INTERNAL_ERROR`). Contrato real: o frontend
  decide o comportamento (mensagem customizada, campo em foco, retry)
  com base neste campo, **nunca** fazendo `string match`/`includes` em
  `error.message`.
- `error.message`: fallback em pt-BR, pronto para exibição direta se o
  frontend não tiver tratamento específico para o `code`. Nunca contém
  interpolação de dado variável (isso vai para `details`).
- `error.fields`: mantido como já existia — lista de
  `{ campo, mensagem }` para erro de validação por campo de formulário
  (`VALIDATION_ERROR` e alguns `CONFLICT`/`409` que já apontavam campo,
  ex. cadastro).
- `error.details`: objeto estruturado, opcional, com dado variável que
  antes era interpolado na mensagem (ex: `{ "tipo": "xyz" }`,
  `{ "ordem": 3 }`, `{ "retryAfterSeconds": 42 }`). Nunca string
  pré-formatada — o frontend decide como exibir.
- `error.traceId`: já existia, correlação com log do backend.

## Migração (breaking change de contrato)

Antes, `error.code` já existia mas era um valor genérico por família de
status HTTP (`VALIDATION_ERROR`, `NOT_FOUND`, `CONFLICT`,
`UNAUTHENTICATED`, `FORBIDDEN`, `RATE_LIMITED`, `UNSUPPORTED_MEDIA`,
`UPSTREAM_ERROR`, `INTERNAL_ERROR`) — o mesmo `code` cobria situações
de negócio completamente diferentes (ex: usuário não encontrado,
reunião não encontrada e FAQ não encontrada todos caíam em
`NOT_FOUND`). Isso mudou: `code` agora é específico por situação de
negócio, namespaced por domínio. `error.message` continua igual
(mesmo texto de antes). **Frontend precisa atualizar qualquer lógica
que dependa dos valores antigos e genéricos de `code`.**
`VALIDATION_ERROR`, `UPLOAD_TOO_LARGE` e `INTERNAL_ERROR` continuam
genéricos de propósito (cobrem, respectivamente, bind/validação de DTO
do Spring, limite de tamanho de multipart do Spring, e erro interno
não mapeado — nenhum dos três tem contexto de negócio específico para
granularizar).

## Catálogo de codes

### Genéricos (sem domínio de negócio específico)
| code | HTTP | quando |
|---|---|---|
| `VALIDATION_ERROR` | 400 | bind/validação de DTO (Bean Validation) |
| `UPLOAD_TOO_LARGE` | 400 | corpo multipart excede limite configurado no Spring |
| `AUTH_UNAUTHENTICATED` | 401 | `BadCredentialsException` do Spring Security (fallback, não usado nos fluxos de auth próprios) |
| `AUTH_FORBIDDEN` | 403 | `AccessDeniedException` do Spring Security |
| `INTERNAL_ERROR` | 500 | exceção não mapeada |

### Auth (`AUTH_`)
| code | HTTP | quando |
|---|---|---|
| `AUTH_INVALID_CREDENTIALS` | 401 | login/login-mfa com apelido/e-mail ou senha incorretos (mensagem sempre genérica, anti-enumeration) |
| `AUTH_APELIDO_EM_USO` | 409 | cadastro: apelido já cadastrado |
| `AUTH_EMAIL_EM_USO` | 409 | cadastro: e-mail já cadastrado |
| `AUTH_CADASTRO_CONFLITO` | 409 | cadastro: corrida concorrente pega pelo `DataIntegrityViolationException` no save |
| `AUTH_SENHA_CONFIRMACAO_DIVERGENTE` | 400 | cadastro ou redefinição de senha: senha != confirmação |
| `AUTH_RESET_TOKEN_INVALIDO` | 401 | redefinir senha: token ausente, expirado ou usuário do token não existe mais |
| `AUTH_TOKEN_AUSENTE_OU_INVALIDO` | 401 | logout sem JWT válido, ou `/usuarios/me` e afins sem `principal` |
| `AUTH_RECAPTCHA_INVALIDO` | 400 | reCAPTCHA ausente/expirado/inválido (`error.fields` aponta `recaptchaToken`) |
| `AUTH_RECAPTCHA_UPSTREAM_INDISPONIVEL` | 502 | falha de comunicação com o Google ao validar reCAPTCHA |
| `AUTH_RATE_LIMITED` | 429 | rate limit de login, login-mfa, registro (por conta e varredura global), esqueci-senha, redefinir-senha |

### MFA (`MFA_`)
| code | HTTP | quando |
|---|---|---|
| `MFA_JA_ATIVO` | 409 | enroll/confirmar quando MFA já está ativo |
| `MFA_ENROLLMENT_INEXISTENTE` | 404 | confirmar sem enrollment pendente |
| `MFA_CODIGO_INVALIDO` | 401 | código TOTP incorreto (confirmar ou desativar) |
| `MFA_SENHA_INCORRETA` | 401 | desativar com senha incorreta |
| `MFA_USUARIO_NAO_ENCONTRADO` | 404 | usuário do MFA não encontrado |
| `MFA_RATE_LIMITED` | 429 | rate limit de enroll/confirmar/desativar |

### Usuário (`USUARIO_`)
| code | HTTP | quando |
|---|---|---|
| `USUARIO_NAO_ENCONTRADO` | 404 | admin edita role de usuário inexistente |
| `USUARIO_AUTO_REBAIXAMENTO_ADMIN` | 409 | admin tenta remover a própria role de admin |
| `USUARIO_ULTIMO_ADMIN` | 409 | tentativa de remover o último admin do sistema |
| `USUARIO_DADOS_EM_USO` | 409 | atualizar conta com apelido/e-mail já usado por outro usuário |
| `USUARIO_SENHA_CONFIRMACAO_DIVERGENTE` | 400 | alterar senha: nova senha != confirmação |
| `USUARIO_SENHA_ATUAL_INCORRETA` | 401 | alterar senha: senha atual incorreta |
| `USUARIO_RATE_LIMITED` | 429 | rate limit de escrita admin/atualizar conta/alterar senha |
| `USUARIO_LIMITE_ADMIN_ATINGIDO` | 409 | promoção a admin excederia `ADMIN_LIMITE_MAXIMO`; mensagem genérica, não expõe limite configurado nem contagem atual |

### Relato (`RELATO_`)
| code | HTTP | quando |
|---|---|---|
| `RELATO_NAO_ENCONTRADO` | 404 | relato inexistente (público aprovado ou admin) |
| `RELATO_RATE_LIMITED` | 429 | rate limit de envio (participante) ou escrita (admin) |

### Upload (`UPLOAD_`, compartilhado entre imagem/arquivo)
| code | HTTP | quando |
|---|---|---|
| `UPLOAD_TITULO_OBRIGATORIO` | 400 | upload de arquivo sem título |
| `UPLOAD_ALT_OBRIGATORIO` | 400 | upload de imagem sem texto alternativo |
| `UPLOAD_ARQUIVO_OBRIGATORIO` | 400 | multipart sem arquivo ou vazio |
| `UPLOAD_TAMANHO_EXCEDIDO` | 400 | arquivo/imagem excede limite de negócio (20MB arquivo, 5MB imagem) |
| `UPLOAD_LEITURA_FALHOU` | 400 | falha de I/O ao ler bytes do multipart |
| `UPLOAD_TIPO_NAO_PERMITIDO` | 415 | magic bytes não correspondem a PDF/jpeg/png/webp, ou PDF rejeitado por conteúdo malicioso (OpenAction, /AA, /JavaScript, /EmbeddedFile, polyglot) |
| `UPLOAD_RATE_LIMITED` | 429 | rate limit de upload (por admin ou global) |
| `UPLOAD_STORAGE_INDISPONIVEL` | 502 | falha de I/O no disco local (upload, delete, download) |

### Arquivo / Imagem (`ARQUIVO_`, `IMAGEM_`)
| code | HTTP | quando |
|---|---|---|
| `ARQUIVO_NAO_ENCONTRADO` | 404 | arquivo inexistente (editar, remover, download) |
| `ARQUIVO_EM_USO` | 409 | remoção de arquivo referenciado em página CMS ou biblioteca pessoal |
| `IMAGEM_NAO_ENCONTRADA` | 404 | imagem inexistente |
| `IMAGEM_EM_USO` | 409 | remoção de imagem referenciada em página CMS |

### CMS (`CMS_`)
| code | HTTP | quando |
|---|---|---|
| `CMS_PAGINA_NAO_ENCONTRADA` | 404 | página inexistente (pública ou admin) |
| `CMS_SLUG_EM_USO` | 409 | criar página com slug já existente |
| `CMS_BLOCO_REFERENCIA_INVALIDA` | 400 | bloco de imagem/arquivo referencia upload inexistente |
| `CMS_BLOCO_ORDEM_DUPLICADA` | 400 | dois blocos com a mesma ordem (`details.ordem`) |
| `CMS_BLOCO_TIPO_INVALIDO` | 400 | tipo de bloco desconhecido (`details.tipo`) |
| `CMS_HTML_REJEITADO` | 400 | sanitização OWASP rejeitou tag/atributo (`details.tag` quando aplicável) |

### Mural de apoio (`MURAL_`)
| code | HTTP | quando |
|---|---|---|
| `MURAL_BANNER_NAO_ENCONTRADO` | 404 | banner inexistente |
| `MURAL_BANNER_MIDIA_INVALIDA` | 400 | banner sem mídia válida (nem imagem nem vídeo, ambos, imagem inexistente, ou vídeo fora do YouTube) |

### Aviso, FAQ, Reflexão do dia, Vinte perguntas, Reunião
| code | HTTP | quando |
|---|---|---|
| `AVISO_NAO_ENCONTRADO` | 404 | aviso inexistente |
| `FAQ_NAO_ENCONTRADA` | 404 | pergunta de FAQ inexistente |
| `REFLEXAO_NAO_ENCONTRADA` | 404 | reflexão do dia não cadastrada para a data |
| `REFLEXAO_DATA_INVALIDA` | 400 | mês/dia fora do intervalo válido |
| `VINTE_PERGUNTAS_NAO_ENCONTRADA` | 404 | pergunta inexistente |
| `VINTE_PERGUNTAS_ULTIMA_PERGUNTA` | 400 | tentativa de remover a última pergunta restante |
| `REUNIAO_NAO_ENCONTRADA` | 404 | reunião inexistente |

### Escritório regional (`ESCRITORIO_`)
| code | HTTP | quando |
|---|---|---|
| `ESCRITORIO_REGIAO_NAO_ENCONTRADA` | 404 | região inexistente |
| `ESCRITORIO_REGIAO_NOME_EM_USO` | 400 | editar região com nome já usado por outra |
| `ESCRITORIO_ESTADO_NAO_ENCONTRADO` | 404 | estado inexistente ou não pertence à região informada |
| `ESCRITORIO_ESTADO_UF_EM_USO` | 409 | criar/editar estado com UF já cadastrada na região |
| `ESCRITORIO_GRUPO_NAO_ENCONTRADO` | 404 | grupo inexistente ou não pertence ao estado informado |
| `ESCRITORIO_SERVICO_NAO_ENCONTRADO` | 404 | escritório de serviço inexistente |
| `ESCRITORIO_UF_INVALIDA` | 400 | sigla de UF fora das 27 válidas |
| `ESCRITORIO_DIA_SEMANA_INVALIDO` | 400 | dia da semana da agenda de grupo inválido (`details.diaSemana`) |
| `ESCRITORIO_DIA_SEMANA_DUPLICADO` | 400 | dia da semana repetido na agenda de grupo (`details.diaSemana`) |

### Hotline (`HOTLINE_`)
| code | HTTP | quando |
|---|---|---|
| `HOTLINE_REGIAO_NAO_ENCONTRADA` | 404 | região inexistente |
| `HOTLINE_TELEFONE_NAO_ENCONTRADO` | 404 | telefone inexistente |
| `HOTLINE_REGIAO_EM_USO` | 409 | criar/editar região com UF+cidade já cadastrada |
| `HOTLINE_UF_INVALIDA` | 400 | sigla de UF fora das 27 válidas |

### Biblioteca pessoal (`BIBLIOTECA_`)
| code | HTTP | quando |
|---|---|---|
| `BIBLIOTECA_ARQUIVO_NAO_ENCONTRADO` | 404 | adicionar arquivo inexistente à biblioteca pessoal |
| `BIBLIOTECA_RATE_LIMITED` | 429 | rate limit de adição à biblioteca pessoal |

### Auditoria admin (`AUDITORIA_`)
| code | HTTP | quando |
|---|---|---|
| `AUDITORIA_INTERVALO_INVALIDO` | 400 | `dataInicio` posterior a `dataFim` no filtro de listagem |

## Cabeçalho `Retry-After`
Toda resposta `429` (qualquer `*_RATE_LIMITED`) continua enviando o
header HTTP `Retry-After` em segundos, e o mesmo valor agora também
aparece em `error.details.retryAfterSeconds` — o frontend pode ler de
qualquer um dos dois; o header é a fonte primária (padrão HTTP), o
`details` existe para consumo direto do corpo JSON sem parsear header.
