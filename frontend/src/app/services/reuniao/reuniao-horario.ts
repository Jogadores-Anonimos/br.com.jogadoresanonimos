function minutosIniciais(horario: string): number | null {
  const correspondencia = horario.trim().match(/^(\d{1,2})(?::(\d{2})|h\s*(\d{2})?)/i);
  if (!correspondencia) return null;

  const hora = Number(correspondencia[1]);
  const minuto = Number(correspondencia[2] ?? correspondencia[3] ?? 0);
  return hora <= 23 && minuto <= 59 ? hora * 60 + minuto : null;
}

/** Ordena formatos atuais e legados pela hora inicial do texto. */
export function compararHorarios(a: string, b: string): number {
  const minutosA = minutosIniciais(a);
  const minutosB = minutosIniciais(b);

  if (minutosA !== null && minutosB !== null && minutosA !== minutosB) return minutosA - minutosB;
  if (minutosA !== null && minutosB === null) return -1;
  if (minutosA === null && minutosB !== null) return 1;
  return a.localeCompare(b, 'pt-BR', { numeric: true });
}
