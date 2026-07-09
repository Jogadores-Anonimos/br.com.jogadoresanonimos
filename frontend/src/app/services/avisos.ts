import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { delay, map } from 'rxjs/operators';

// TODO: substituir por chamada real quando o endpoint estiver disponível
// (ex: this.http.get<AvisosResponse>('/api/avisos')). O contrato público
// deste service (nome do método listarAvisos, tipo de retorno
// Observable<AvisosResponse>) não deve mudar nessa troca — apenas a
// implementação interna do método.

export interface Aviso {
  id: string;
  titulo: string;
  corpo: string;
  dataPublicacao: string; // ISO
  fixado: boolean; // aviso importante/destacado — deve aparecer primeiro na listagem
  dataExpiracao?: string; // ISO, opcional
}

export interface AvisosResponse {
  success: boolean;
  data?: Aviso[];
  error?: string;
}

// Dados mockados centralizados aqui por simplicidade (arquivo único e
// pequeno). Caso a lista cresça, mover para avisos.mock.ts.
const AVISOS_MOCK: Aviso[] = [
  {
    id: 'aviso-1',
    titulo: 'Canal de apoio disponível 24 horas',
    corpo:
      'Lembramos que nosso canal de apoio por chat está disponível 24 horas por dia, todos os dias, para quem precisar conversar com alguém a qualquer momento.',
    dataPublicacao: '2026-07-01T09:00:00.000Z',
    fixado: true,
  },
  {
    id: 'aviso-2',
    titulo: 'Novo grupo de apoio na região Sul',
    corpo:
      'Um novo grupo de apoio presencial foi criado na região Sul do país. Entre em contato pelos nossos canais para saber o horário e o local das reuniões.',
    dataPublicacao: '2026-06-28T14:30:00.000Z',
    fixado: false,
  },
  {
    id: 'aviso-3',
    titulo: 'Mudança no horário das reuniões de quarta-feira',
    corpo:
      'As reuniões que aconteciam às 19h nas quartas-feiras passam a acontecer às 20h a partir da próxima semana, para facilitar a participação de mais pessoas.',
    dataPublicacao: '2026-06-20T11:00:00.000Z',
    fixado: false,
  },
  {
    id: 'aviso-4',
    titulo: 'Manutenção programada do site',
    corpo:
      'No próximo sábado, das 2h às 4h da manhã, o site passará por uma manutenção programada e poderá ficar indisponível por alguns minutos.',
    dataPublicacao: '2026-06-15T08:00:00.000Z',
    fixado: false,
    dataExpiracao: '2026-07-15T00:00:00.000Z',
  },
  {
    id: 'aviso-5',
    titulo: 'Encontro regional de recuperação em setembro',
    corpo:
      'Estamos organizando um encontro regional de recuperação para o mês de setembro, reunindo diversos grupos de apoio. Mais informações em breve.',
    dataPublicacao: '2026-06-10T16:45:00.000Z',
    fixado: false,
  },
  {
    id: 'aviso-6',
    titulo: 'Reforço: seu anonimato é prioridade',
    corpo:
      'Reforçamos que nenhuma informação pessoal é solicitada ou compartilhada em nossos grupos e canais de apoio. Sua identidade está sempre protegida.',
    dataPublicacao: '2026-05-30T10:15:00.000Z',
    fixado: false,
  },
];

@Injectable({ providedIn: 'root' })
export class AvisosService {
  /**
   * Lista os avisos ordenados por: fixado primeiro, depois por
   * dataPublicacao decrescente (mais recente primeiro).
   *
   * Mock temporário: retorna dados estáticos simulando latência de rede.
   * Quando o endpoint de backend existir, a implementação interna passa
   * a chamar `this.http.get<AvisosResponse>('/api/avisos')`, mantendo a
   * mesma assinatura pública.
   */
  listarAvisos(): Observable<AvisosResponse> {
    return of<AvisosResponse>({ success: true, data: AVISOS_MOCK }).pipe(
      delay(500),
      map((resposta) => {
        if (!resposta.data) {
          return resposta;
        }

        const avisosOrdenados = [...resposta.data].sort((a, b) => {
          if (a.fixado !== b.fixado) {
            return a.fixado ? -1 : 1;
          }
          return new Date(b.dataPublicacao).getTime() - new Date(a.dataPublicacao).getTime();
        });

        return { ...resposta, data: avisosOrdenados };
      }),
    );
  }
}
