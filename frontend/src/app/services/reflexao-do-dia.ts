import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';

// TODO: substituir por chamada real (this.http.get<ReflexaoDoDiaResponse>('/api/reflexao-do-dia/...')) mantendo o contrato público

// modelada por mês+dia sem ano, pois a reflexão se repete anualmente (como o calendário do GA americano)
export interface ReflexaoDoDia {
  mes: number; // 1-12
  dia: number; // 1-31, conforme o mês
  texto: string;
}

export interface ReflexaoDoDiaResponse {
  success: boolean;
  data?: ReflexaoDoDia;
  error?: string;
}

const NOMES_MESES = [
  'janeiro',
  'fevereiro',
  'março',
  'abril',
  'maio',
  'junho',
  'julho',
  'agosto',
  'setembro',
  'outubro',
  'novembro',
  'dezembro',
];

// índice 0 = janeiro; fevereiro sempre 28 dias (sem considerar ano bissexto) já que a reflexão é só mês+dia
const DIAS_POR_MES: number[] = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];

function textoPlaceholder(dia: number, mes: number): string {
  return `Reflexão do dia ${dia} de ${NOMES_MESES[mes - 1]} — texto de exemplo enquanto o conteúdo real não é cadastrado.`;
}

// conteúdo variado só para o mês em que este mock foi escrito (julho); demais meses usam textoPlaceholder(). Indexado por dia (posição 0 = dia 1).
const TEXTOS_JULHO: string[] = [
  'Hoje escolho lembrar que a recuperação é construída um dia de cada vez, não em uma corrida contra o tempo.',
  'A honestidade comigo mesmo é o primeiro passo para qualquer mudança duradoura.',
  'Quando sinto vontade de jogar, posso parar e perguntar: do que eu realmente preciso agora?',
  'Pedir ajuda não é fraqueza — é a decisão mais forte que posso tomar hoje.',
  'Minha história não define meu futuro; minhas escolhas de hoje sim.',
  'Compartilhar minha experiência pode ser exatamente o que outra pessoa precisa ouvir.',
  'A recaída não apaga o progresso já feito — ela é informação, não sentença.',
  'Hoje pratico paciência comigo mesmo, sabendo que a recuperação não é linear.',
  'Cada reunião que participo é um tijolo a mais na construção da minha estabilidade.',
  'Posso substituir a urgência do jogo pela urgência de cuidar de mim.',
  'A gratidão pelo que já conquistei me dá forças para continuar.',
  'Não preciso enfrentar sozinho os pensamentos difíceis — o grupo existe para isso.',
  'Hoje escolho a verdade, mesmo quando ela é desconfortável.',
  'Meu valor não é medido por vitórias ou perdas no jogo, mas por quem eu sou.',
  'A recuperação também é feita de pequenos momentos de autocuidado.',
  'Posso perdoar meus erros passados sem esquecer as lições que eles trouxeram.',
  'Hoje escolho ocupar minha mente com algo que me fortalece, não que me consome.',
  'A confiança se reconstrói aos poucos, com ações consistentes ao longo do tempo.',
  'Não estou sozinho nessa caminhada — outros já passaram por onde estou passando.',
  'Hoje é um bom dia para reconhecer o quanto já caminhei, mesmo com tropeços.',
  'A vontade de jogar é temporária; a decisão de recomeçar pode ser permanente.',
  'Posso pedir apoio antes de a situação piorar — isso também é recuperação.',
  'Hoje escolho falar sobre o que sinto, em vez de guardar tudo para mim.',
  'Celebro cada dia de recuperação, por menor que ele pareça.',
  'A mudança começa com um pensamento diferente sobre mim mesmo.',
  'Hoje entendo que buscar ajuda profissional é parte do cuidado comigo.',
  'Meus dias difíceis também ensinam — não preciso ser perfeito, só constante.',
  'Reconheço meus gatilhos e escolho me afastar deles quando possível.',
  'A recuperação também se constrói em comunidade, não apenas sozinho.',
  'Hoje escolho confiar no processo, mesmo sem ver o resultado final ainda.',
  'Um dia de cada vez: essa é a medida certa para minha recuperação.',
];

function gerarMesesDoAno(mesAtual: number): ReflexaoDoDia[][] {
  return DIAS_POR_MES.map((totalDias, indice) => {
    const mes = indice + 1;
    const dias: ReflexaoDoDia[] = [];

    for (let dia = 1; dia <= totalDias; dia++) {
      const texto =
        mes === mesAtual && TEXTOS_JULHO[dia - 1] ? TEXTOS_JULHO[dia - 1] : textoPlaceholder(dia, mes);

      dias.push({ mes, dia, texto });
    }

    return dias;
  });
}

const REFLEXOES_MOCK: ReflexaoDoDia[][] = gerarMesesDoAno(new Date().getMonth() + 1);

@Injectable({ providedIn: 'root' })
export class ReflexaoDoDiaService {
  obterDiasNoMes(): number[] {
    return [...DIAS_POR_MES];
  }

  obterReflexaoDeHoje(): Observable<ReflexaoDoDiaResponse> {
    const hoje = new Date();
    return this.obterReflexaoPorData(hoje.getMonth() + 1, hoje.getDate());
  }

  obterReflexaoPorData(mes: number, dia: number): Observable<ReflexaoDoDiaResponse> {
    if (mes < 1 || mes > 12) {
      return of<ReflexaoDoDiaResponse>({ success: false, error: 'Mês inválido.' }).pipe(delay(300));
    }

    const diasDoMes = REFLEXOES_MOCK[mes - 1];
    const reflexao = diasDoMes.find((r) => r.dia === dia);

    if (!reflexao) {
      return of<ReflexaoDoDiaResponse>({ success: false, error: 'Dia inválido para o mês informado.' }).pipe(
        delay(300),
      );
    }

    return of<ReflexaoDoDiaResponse>({ success: true, data: reflexao }).pipe(delay(500));
  }
}
