import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { EscritorioRegionalService } from '../../services/escritorio-regional/escritorio-regional';
import {
  DiaSemana,
  EscritorioServico,
  EstadoIntergrupo,
  RegiaoIntergrupo,
} from '../../services/escritorio-regional/escritorio-regional.model';

const LABEL_DIA_SEMANA: Record<DiaSemana, string> = {
  DOMINGO: 'Dom',
  SEGUNDA: 'Seg',
  TERCA: 'Ter',
  QUARTA: 'Qua',
  QUINTA: 'Qui',
  SEXTA: 'Sex',
  SABADO: 'Sáb',
};

@Component({
  selector: 'app-enderecos',
  standalone: true,
  imports: [CommonModule, RouterLink, ResourceLayout],
  templateUrl: './enderecos.html',
  styleUrl: './enderecos.css',
})
export class Enderecos implements OnInit {
  private readonly escritorioRegionalService = inject(EscritorioRegionalService);

  readonly escritorios = signal<EscritorioServico[]>([]);
  readonly regioes = signal<RegiaoIntergrupo[]>([]);
  readonly carregando = signal(true);
  readonly erro = signal(false);

  regiaoSelecionada: string | null = null;

  ngOnInit(): void {
    this.escritorioRegionalService.listarEstrutura().subscribe({
      next: (resposta) => {
        this.carregando.set(false);
        if (resposta.success && resposta.data) {
          this.escritorios.set(resposta.data.escritorios);
          this.regioes.set(resposta.data.regioes);
          this.regiaoSelecionada = resposta.data.regioes[0]?.id ?? null;
        } else {
          this.erro.set(true);
        }
      },
      error: () => {
        this.carregando.set(false);
        this.erro.set(true);
      },
    });
  }

  get regiaoAtual(): RegiaoIntergrupo | undefined {
    return this.regioes().find((r) => r.id === this.regiaoSelecionada);
  }

  selecionarRegiao(regiao: RegiaoIntergrupo): void {
    this.regiaoSelecionada = regiao.id;
  }

  formatarAgenda(agenda: { diaSemana: DiaSemana; horario: string }[]): string {
    return agenda.map((item) => `${LABEL_DIA_SEMANA[item.diaSemana]} ${item.horario}`).join(' · ');
  }

  formatarTelefones(telefones: { label: string | null; telefone: string }[]): string {
    return telefones.map((t) => (t.label ? `${t.label}: ${t.telefone}` : t.telefone)).join(' / ');
  }
}
