import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { EscritorioRegionalService, derivarUfsFaltantesPorRegiao } from '../../services/escritorio-regional/escritorio-regional';
import {
  DiaSemana,
  EscritorioServico,
  EstadoIntergrupo,
  RegiaoIntergrupo,
  UnidadeFederativaComRegiao,
} from '../../services/escritorio-regional/escritorio-regional.model';
import { montarLinkWhatsapp } from '../../config/whatsapp.config';

interface EstadoRegiaoItemComGrupo {
  tipo: 'com-grupo';
  nome: string;
  estado: EstadoIntergrupo;
}

interface EstadoRegiaoItemSemGrupo {
  tipo: 'sem-grupo';
  nome: string;
  unidade: UnidadeFederativaComRegiao;
}

type EstadoRegiaoItem = EstadoRegiaoItemComGrupo | EstadoRegiaoItemSemGrupo;

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
  readonly ufsFaltantesPorRegiao = signal<Map<string, UnidadeFederativaComRegiao[]>>(new Map());
  readonly carregando = signal(true);
  readonly erro = signal(false);

  regiaoSelecionada: string | null = null;

  ngOnInit(): void {
    forkJoin({
      estrutura: this.escritorioRegionalService.listarEstrutura(),
      unidadesFederativas: this.escritorioRegionalService.listarUnidadesFederativas(),
    }).subscribe({
      next: ({ estrutura, unidadesFederativas }) => {
        this.carregando.set(false);
        if (estrutura.success && estrutura.data) {
          this.escritorios.set(estrutura.data.escritorios);
          this.regioes.set(estrutura.data.regioes);
          this.regiaoSelecionada = estrutura.data.regioes[0]?.id ?? null;
          if (unidadesFederativas.success && unidadesFederativas.data) {
            this.ufsFaltantesPorRegiao.set(
              derivarUfsFaltantesPorRegiao(estrutura.data.regioes, unidadesFederativas.data),
            );
          }
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

  get estadosRegiaoAtual(): EstadoRegiaoItem[] {
    const regiao = this.regiaoAtual;
    if (!regiao) {
      return [];
    }
    const comGrupo: EstadoRegiaoItem[] = regiao.estados.map((estado) => ({ tipo: 'com-grupo', nome: estado.nome, estado }));
    const semGrupo: EstadoRegiaoItem[] = (this.ufsFaltantesPorRegiao().get(regiao.codigo) ?? []).map((unidade) => ({
      tipo: 'sem-grupo',
      nome: unidade.nome,
      unidade,
    }));
    return [...comGrupo, ...semGrupo].sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR'));
  }

  linkWhatsappGrupoInexistente(): string {
    return montarLinkWhatsapp('21994721933', 'Olá! Gostaria de saber sobre grupos na minha região.');
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
