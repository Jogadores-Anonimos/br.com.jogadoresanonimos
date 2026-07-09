import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, delay } from 'rxjs/operators';

// TODO: substituir por chamada real (GET/POST '/api/relatos') mantendo o contrato público
//
// Quando a chamada real existir, o envio deve levar um identificador de sessão opaco anti-abuso
// (rate limiting no backend, ver .claude/knowledge/seguranca-depoimentos.md item 2) — nunca campo visível do formulário.

// só 'approved' pode ser retornado pela listagem pública (ver seguranca-depoimentos.md)
export type StatusRelato = 'pending' | 'approved' | 'rejected';

// deliberadamente sem e-mail, nome completo, telefone, CPF, geolocalização ou anexo — política de segurança
export interface Relato {
  id: string;
  texto: string;
  nomeExibicao: string | null; // null/vazio -> exibir como "Anônimo" na UI
  status: StatusRelato;
  dataCriacao: string; // ISO
}

export interface RelatosResponse {
  success: boolean;
  data?: Relato[];
  error?: string;
}

export interface EnviarRelatoRequest {
  texto: string;
  nomeExibicao?: string;
}

export interface EnviarRelatoResponse {
  success: boolean;
  data?: { recebido: boolean };
  error?: string;
}

const TEXTO_MIN_LENGTH = 50;
const TEXTO_MAX_LENGTH = 4000;
const NOME_EXIBICAO_MAX_LENGTH = 30;

// relatos fictícios e genéricos, sem nenhum dado sensível real
const RELATOS_MOCK: Relato[] = [
  {
    id: 'relato-1',
    texto:
      'Cheguei ao grupo sem acreditar que conseguiria parar. Hoje, com a ajuda dos encontros semanais e do apoio dos outros membros, completei alguns meses de recuperação. Cada reunião me lembra que não preciso enfrentar isso sozinho.',
    nomeExibicao: 'Marcos',
    status: 'approved',
    dataCriacao: '2026-07-05T13:20:00.000Z',
  },
  {
    id: 'relato-2',
    texto:
      'Demorei anos para admitir que precisava de ajuda. Encontrar um espaço onde eu podia falar abertamente, sem julgamento, mudou minha forma de encarar a recuperação. Ainda é um processo diário, mas hoje sinto que tenho ferramentas para seguir em frente.',
    nomeExibicao: null,
    status: 'approved',
    dataCriacao: '2026-07-02T09:45:00.000Z',
  },
  {
    id: 'relato-3',
    texto:
      'O primeiro passo foi o mais difícil, mas participar das reuniões me deu uma rotina e pessoas em quem confiar. Reconstruir a confiança da minha família está sendo gradual, e sei que não estou sozinho nessa caminhada.',
    nomeExibicao: 'J.',
    status: 'approved',
    dataCriacao: '2026-06-27T18:10:00.000Z',
  },
  {
    id: 'relato-4',
    texto:
      'Nunca imaginei que dividir minha história com estranhos pudesse trazer tanto alívio. O grupo me ensinou que recaídas fazem parte do processo e que o importante é continuar tentando, um dia de cada vez.',
    nomeExibicao: 'Ana',
    status: 'approved',
    dataCriacao: '2026-06-18T11:00:00.000Z',
  },
];

@Injectable({ providedIn: 'root' })
export class RelatosService {
  listarRelatosAprovados(): Observable<RelatosResponse> {
    const aprovados = RELATOS_MOCK.filter((r) => r.status === 'approved').sort(
      (a, b) => new Date(b.dataCriacao).getTime() - new Date(a.dataCriacao).getTime(),
    );

    return of<RelatosResponse>({ success: true, data: aprovados }).pipe(
      delay(500),
      catchError(() =>
        of<RelatosResponse>({
          success: false,
          error: 'Não foi possível carregar os relatos no momento.',
        }),
      ),
    );
  }

  // não há confirmação de aprovação instantânea: todo relato entra 'pending' e só aparece após moderação
  enviarRelato(req: EnviarRelatoRequest): Observable<EnviarRelatoResponse> {
    const texto = req.texto?.trim() ?? '';
    const nomeExibicao = req.nomeExibicao?.trim();

    if (texto.length < TEXTO_MIN_LENGTH || texto.length > TEXTO_MAX_LENGTH) {
      return of<EnviarRelatoResponse>({
        success: false,
        error: `O relato deve ter entre ${TEXTO_MIN_LENGTH} e ${TEXTO_MAX_LENGTH} caracteres.`,
      }).pipe(delay(300));
    }

    if (nomeExibicao && nomeExibicao.length > NOME_EXIBICAO_MAX_LENGTH) {
      return of<EnviarRelatoResponse>({
        success: false,
        error: `O nome de exibição deve ter no máximo ${NOME_EXIBICAO_MAX_LENGTH} caracteres.`,
      }).pipe(delay(300));
    }

    return of<EnviarRelatoResponse>({ success: true, data: { recebido: true } }).pipe(
      delay(500),
      catchError(() =>
        of<EnviarRelatoResponse>({
          success: false,
          error: 'Não foi possível enviar seu relato agora. Tente novamente em instantes.',
        }),
      ),
    );
  }
}
