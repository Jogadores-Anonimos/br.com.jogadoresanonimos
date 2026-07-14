// Envelope de paginação comum a endpoints que usam Page<T> no backend —
// campos em português (pagina/tamanho), ver contrato-relatos.md
export interface Pagina<T> {
  conteudo: T[];
  pagina: number;
  tamanho: number;
  totalElementos: number;
  totalPaginas: number;
}
