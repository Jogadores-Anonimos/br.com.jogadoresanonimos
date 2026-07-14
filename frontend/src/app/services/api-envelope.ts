// Envelope de resposta comum a toda a API — ver backend/docs/api/contrato-auth-cms-upload.md
export interface ApiError {
  code: string;
  message: string;
  fields?: Record<string, string>;
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

export function apiError<T>(code: string, message: string, fields?: Record<string, string>): ApiEnvelope<T> {
  return { success: false, data: null, error: { code, message, fields } };
}

// O backend sempre responde erro de negócio (400/404/409/415 etc.) com
// corpo `{ success:false, error:{ message, code, ... } }` — HttpClient
// trata qualquer status não-2xx como erro e joga fora do fluxo normal,
// então sem isso o `catchError` de cada service perderia a mensagem
// real (ex: "PDF rejeitado — contém OpenAction") e mostraria só um
// genérico, escondendo do usuário o motivo de verdade.
export function apiErrorDeHttpError<T>(erro: unknown, codigoFallback: string, mensagemFallback: string): ApiEnvelope<T> {
  const corpo = (erro as { error?: Partial<ApiEnvelope<unknown>> } | null)?.error;
  if (corpo?.error?.message) {
    return { success: false, data: null, error: corpo.error };
  }
  return apiError<T>(codigoFallback, mensagemFallback);
}
