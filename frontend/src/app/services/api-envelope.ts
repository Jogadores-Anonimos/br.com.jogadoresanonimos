export interface ApiFieldError {
  campo: string;
  mensagem: string;
}

export interface ApiError {
  code: string;
  message: string;
  fields?: ApiFieldError[];
  traceId?: string;
}

export interface ApiEnvelope<T> {
  success: boolean;
  data: T | null;
  error: ApiError | null;
}

export function apiSuccess<T>(data: T): ApiEnvelope<T> {
  return { success: true, data, error: null };
}

export function apiError<T>(code: string, message: string, fields?: ApiFieldError[]): ApiEnvelope<T> {
  return { success: false, data: null, error: { code, message, fields } };
}

export function mapearErrosCampo(fields?: ApiFieldError[]): Record<string, string> {
  return (fields ?? []).reduce<Record<string, string>>((mapa, item) => {
    mapa[item.campo] = item.mensagem;
    return mapa;
  }, {});
}

export function apiErrorDeHttpError<T>(erro: unknown, codigoFallback: string, mensagemFallback: string): ApiEnvelope<T> {
  const corpo = (erro as { error?: Partial<ApiEnvelope<unknown>> } | null)?.error;
  if (corpo?.error?.message) {
    return { success: false, data: null, error: corpo.error };
  }
  return apiError<T>(codigoFallback, mensagemFallback);
}

export const MFA_CODIGO_INVALIDO_MENSAGEM =
  'Código incorreto ou expirado. O código muda a cada 30 segundos — confira o código mais recente no seu aplicativo autenticador e tente de novo.';

export const ERROR_CODE_MENSAGENS: Record<string, string> = {
  VALIDATION_ERROR: 'Verifique os campos informados.',
  UPLOAD_TOO_LARGE: 'Arquivo excede o tamanho máximo permitido.',
  AUTH_UNAUTHENTICATED: 'Sessão expirada. Faça login novamente.',
  AUTH_FORBIDDEN: 'Você não tem permissão para acessar este recurso.',
  INTERNAL_ERROR: 'Ocorreu um erro inesperado. Tente novamente mais tarde.',
  AUTH_INVALID_CREDENTIALS: 'Apelido/e-mail ou senha inválidos.',
  AUTH_APELIDO_EM_USO: 'Esse apelido já está em uso, escolha outro.',
  AUTH_EMAIL_EM_USO: 'Esse e-mail já está cadastrado.',
  AUTH_CADASTRO_CONFLITO: 'Não foi possível concluir o cadastro. Tente novamente.',
  AUTH_SENHA_CONFIRMACAO_DIVERGENTE: 'As senhas informadas não coincidem.',
  AUTH_RESET_TOKEN_INVALIDO: 'Link inválido ou expirado.',
  ADMIN_RESET_CODIGO_INVALIDO: 'Código inválido, expirado ou já utilizado.',
  AUTH_TOKEN_AUSENTE_OU_INVALIDO: 'Sessão expirada. Faça login novamente.',
  AUTH_RECAPTCHA_INVALIDO: 'Não foi possível validar o reCAPTCHA. Tente novamente.',
  AUTH_RECAPTCHA_UPSTREAM_INDISPONIVEL: 'Não foi possível validar o reCAPTCHA no momento. Tente novamente.',
  AUTH_RATE_LIMITED: 'Muitas tentativas. Aguarde alguns instantes e tente novamente.',
  MFA_JA_ATIVO: 'O MFA já está ativo nesta conta.',
  MFA_ENROLLMENT_INEXISTENTE: 'Configuração de MFA não iniciada. Comece o processo novamente.',
  MFA_CODIGO_INVALIDO: MFA_CODIGO_INVALIDO_MENSAGEM,
  MFA_SENHA_INCORRETA: 'Senha atual incorreta.',
  MFA_USUARIO_NAO_ENCONTRADO: 'Usuário não encontrado.',
  MFA_RATE_LIMITED: 'Muitas tentativas. Aguarde alguns instantes e tente novamente.',
  USUARIO_NAO_ENCONTRADO: 'Usuário não encontrado.',
  USUARIO_AUTO_REBAIXAMENTO_ADMIN: 'Não é possível remover sua própria permissão de administrador.',
  USUARIO_ULTIMO_ADMIN: 'Não é possível remover o último administrador do sistema.',
  USUARIO_DADOS_EM_USO: 'Não foi possível salvar os dados informados.',
  USUARIO_SENHA_CONFIRMACAO_DIVERGENTE: 'As senhas informadas não coincidem.',
  USUARIO_SENHA_ATUAL_INCORRETA: 'Senha atual incorreta.',
  USUARIO_RATE_LIMITED: 'Muitas tentativas. Aguarde alguns instantes e tente novamente.',
};

export function mensagemDeErro(error: ApiError | null | undefined, mensagemFallback: string): string {
  if (!error) {
    return mensagemFallback;
  }
  return ERROR_CODE_MENSAGENS[error.code] ?? error.message ?? mensagemFallback;
}
