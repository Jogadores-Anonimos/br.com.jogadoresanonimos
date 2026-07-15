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
