import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, delay } from 'rxjs/operators';

// TODO: substituir por chamada real quando o endpoint estiver disponível
// (ex: this.http.get<DepoimentosResponse>('/api/depoimentos') para listar
// e this.http.post<EnviarDepoimentoResponse>('/api/depoimentos', req) para
// enviar). O contrato público deste service (nomes dos métodos
// listarDepoimentosAprovados/enviarDepoimento e os tipos de retorno
// Observable<DepoimentosResponse>/Observable<EnviarDepoimentoResponse>)
// não deve mudar nessa troca — apenas a implementação interna dos métodos.
//
// Quando a chamada real existir, o envio deve levar um identificador de
// sessão opaco anti-abuso (ex: header tipo `X-Session-Token`, gerado no
// client) para rate limiting no backend — ver política de segurança em
// .claude/knowledge/seguranca-depoimentos.md, item 2. Esse identificador
// é técnico/operacional, nunca um campo visível do formulário nem parte
// do contrato `EnviarDepoimentoRequest` abaixo.

/**
 * Status de moderação do depoimento. Só depoimentos com status
 * 'approved' podem ser retornados pela listagem pública (ver política
 * de segurança em .claude/knowledge/seguranca-depoimentos.md).
 */
export type StatusDepoimento = 'pending' | 'approved' | 'rejected';

/**
 * Contrato de dados de um depoimento. Deliberadamente NÃO possui campos
 * de e-mail, nome completo, telefone, CPF, geolocalização ou anexo —
 * ver política de segurança do agente `security`. O identificador de
 * sessão anti-abuso (hash) é um detalhe operacional do backend e não
 * faz parte deste contrato voltado à UI.
 */
export interface Depoimento {
  id: string;
  texto: string;
  nomeExibicao: string | null; // null/vazio -> exibir como "Anônimo" na UI
  status: StatusDepoimento;
  dataCriacao: string; // ISO
}

export interface DepoimentosResponse {
  success: boolean;
  data?: Depoimento[];
  error?: string;
}

/**
 * Payload para envio de um novo depoimento. Somente os dois campos
 * definidos pela política de segurança — nada de dados de contato.
 */
export interface EnviarDepoimentoRequest {
  texto: string;
  nomeExibicao?: string;
}

export interface EnviarDepoimentoResponse {
  success: boolean;
  data?: { recebido: boolean };
  error?: string;
}

const TEXTO_MIN_LENGTH = 50;
const TEXTO_MAX_LENGTH = 4000;
const NOME_EXIBICAO_MAX_LENGTH = 30;

// Dados mockados centralizados aqui por simplicidade (arquivo único e
// pequeno, mesmo padrão de avisos.ts). Caso a lista cresça, mover para
// depoimentos.mock.ts. Todos os relatos abaixo são fictícios e genéricos,
// sem nenhum dado sensível real.
const DEPOIMENTOS_MOCK: Depoimento[] = [
  {
    id: 'depoimento-1',
    texto:
      'Cheguei ao grupo sem acreditar que conseguiria parar. Hoje, com a ajuda dos encontros semanais e do apoio dos outros membros, completei alguns meses de recuperação. Cada reunião me lembra que não preciso enfrentar isso sozinho.',
    nomeExibicao: 'Marcos',
    status: 'approved',
    dataCriacao: '2026-07-05T13:20:00.000Z',
  },
  {
    id: 'depoimento-2',
    texto:
      'Demorei anos para admitir que precisava de ajuda. Encontrar um espaço onde eu podia falar abertamente, sem julgamento, mudou minha forma de encarar a recuperação. Ainda é um processo diário, mas hoje sinto que tenho ferramentas para seguir em frente.',
    nomeExibicao: null,
    status: 'approved',
    dataCriacao: '2026-07-02T09:45:00.000Z',
  },
  {
    id: 'depoimento-3',
    texto:
      'O primeiro passo foi o mais difícil, mas participar das reuniões me deu uma rotina e pessoas em quem confiar. Reconstruir a confiança da minha família está sendo gradual, e sei que não estou sozinho nessa caminhada.',
    nomeExibicao: 'J.',
    status: 'approved',
    dataCriacao: '2026-06-27T18:10:00.000Z',
  },
  {
    id: 'depoimento-4',
    texto:
      'Nunca imaginei que dividir minha história com estranhos pudesse trazer tanto alívio. O grupo me ensinou que recaídas fazem parte do processo e que o importante é continuar tentando, um dia de cada vez.',
    nomeExibicao: 'Ana',
    status: 'approved',
    dataCriacao: '2026-06-18T11:00:00.000Z',
  },
];

@Injectable({ providedIn: 'root' })
export class DepoimentosService {
  /**
   * Lista os depoimentos já aprovados, mais recentes primeiro. Somente
   * depoimentos com status 'approved' são retornados (a mesma regra que
   * o backend real deve aplicar na listagem pública).
   *
   * Mock temporário: retorna dados estáticos simulando latência de rede.
   */
  listarDepoimentosAprovados(): Observable<DepoimentosResponse> {
    const aprovados = DEPOIMENTOS_MOCK.filter((d) => d.status === 'approved').sort(
      (a, b) => new Date(b.dataCriacao).getTime() - new Date(a.dataCriacao).getTime(),
    );

    return of<DepoimentosResponse>({ success: true, data: aprovados }).pipe(
      delay(500),
      catchError(() =>
        of<DepoimentosResponse>({
          success: false,
          error: 'Não foi possível carregar os depoimentos no momento.',
        }),
      ),
    );
  }

  /**
   * Envia um novo depoimento para moderação. Não há confirmação de
   * aprovação instantânea (decisão de UX): o retorno apenas confirma o
   * recebimento, já que todo depoimento novo entra como 'pending' e só
   * aparece na listagem pública depois de moderado como 'approved'.
   *
   * Mock temporário: valida os limites de tamanho localmente e simula
   * sucesso após um delay, sem persistir nada de fato.
   */
  enviarDepoimento(req: EnviarDepoimentoRequest): Observable<EnviarDepoimentoResponse> {
    const texto = req.texto?.trim() ?? '';
    const nomeExibicao = req.nomeExibicao?.trim();

    if (texto.length < TEXTO_MIN_LENGTH || texto.length > TEXTO_MAX_LENGTH) {
      return of<EnviarDepoimentoResponse>({
        success: false,
        error: `O relato deve ter entre ${TEXTO_MIN_LENGTH} e ${TEXTO_MAX_LENGTH} caracteres.`,
      }).pipe(delay(300));
    }

    if (nomeExibicao && nomeExibicao.length > NOME_EXIBICAO_MAX_LENGTH) {
      return of<EnviarDepoimentoResponse>({
        success: false,
        error: `O nome de exibição deve ter no máximo ${NOME_EXIBICAO_MAX_LENGTH} caracteres.`,
      }).pipe(delay(300));
    }

    return of<EnviarDepoimentoResponse>({ success: true, data: { recebido: true } }).pipe(
      delay(500),
      catchError(() =>
        of<EnviarDepoimentoResponse>({
          success: false,
          error: 'Não foi possível enviar seu depoimento agora. Tente novamente em instantes.',
        }),
      ),
    );
  }
}
