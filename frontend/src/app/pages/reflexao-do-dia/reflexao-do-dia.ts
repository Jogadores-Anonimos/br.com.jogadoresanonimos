import { Component, OnInit } from '@angular/core';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import {
  ReflexaoDoDia as ReflexaoDoDiaModel,
  ReflexaoDoDiaService,
} from '../../services/reflexao-do-dia';

interface OpcaoMes {
  numero: number; // 1-12
  nome: string; // ex: 'Janeiro'
}

const NOMES_MESES = [
  'Janeiro',
  'Fevereiro',
  'Março',
  'Abril',
  'Maio',
  'Junho',
  'Julho',
  'Agosto',
  'Setembro',
  'Outubro',
  'Novembro',
  'Dezembro',
];

/**
 * Página completa da "Reflexão do Dia": grade de meses -> grade de dias
 * -> texto, tudo na mesma tela (sem navegação de rota entre os passos).
 *
 * Estado inicial já resolvido: mês e dia de hoje vêm pré-selecionados,
 * então a reflexão de hoje é visível sem nenhuma interação do usuário
 * (requisito definido pelo ux-ui).
 */
@Component({
  selector: 'app-reflexao-do-dia',
  standalone: true,
  imports: [ResourceLayout],
  templateUrl: './reflexao-do-dia.html',
  styleUrl: './reflexao-do-dia.css',
})
export class ReflexaoDoDiaPage implements OnInit {
  readonly meses: OpcaoMes[] = NOMES_MESES.map((nome, indice) => ({
    numero: indice + 1,
    nome,
  }));

  diasDoMesSelecionado: number[] = [];

  mesSelecionado = 0;
  diaSelecionado = 0;

  reflexao: ReflexaoDoDiaModel | null = null;
  carregando = false;
  erro: string | null = null;

  constructor(private readonly reflexaoService: ReflexaoDoDiaService) {}

  ngOnInit(): void {
    const hoje = new Date();
    const mesAtual = hoje.getMonth() + 1;
    const diaAtual = hoje.getDate();

    this.montarDiasDoMes(mesAtual);
    this.selecionarDia(mesAtual, diaAtual);
  }

  get nomeMesSelecionado(): string {
    return this.meses.find((m) => m.numero === this.mesSelecionado)?.nome ?? '';
  }

  selecionarMes(mes: number): void {
    if (mes === this.mesSelecionado) {
      return;
    }

    this.montarDiasDoMes(mes);
    // Ao trocar de mês, ajusta o dia selecionado caso o mês novo tenha
    // menos dias que o dia atualmente escolhido (ex: vindo de um mês de
    // 31 dias com dia 31 selecionado para fevereiro).
    const diaAjustado = Math.min(this.diaSelecionado || 1, this.diasDoMesSelecionado.length);
    this.selecionarDia(mes, diaAjustado);
  }

  selecionarDia(mes: number, dia: number): void {
    this.mesSelecionado = mes;
    this.diaSelecionado = dia;
    this.carregando = true;
    this.erro = null;

    this.reflexaoService.obterReflexaoPorData(mes, dia).subscribe((resposta) => {
      this.carregando = false;

      if (!resposta.success || !resposta.data) {
        this.erro = resposta.error ?? 'Não foi possível carregar a reflexão deste dia.';
        this.reflexao = null;
        return;
      }

      this.reflexao = resposta.data;
    });
  }

  private montarDiasDoMes(mes: number): void {
    const diasPorMes = this.reflexaoService.obterDiasNoMes();
    const totalDias = diasPorMes[mes - 1] ?? 31;
    this.diasDoMesSelecionado = Array.from({ length: totalDias }, (_, indice) => indice + 1);
  }
}
