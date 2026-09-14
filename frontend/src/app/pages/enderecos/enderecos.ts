import { CommonModule } from '@angular/common';
import {
  Component,
  HostListener,
  OnDestroy,
  OnInit,
  computed,
  inject,
  signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { TituloGrupoPipe } from '../../pipes/titulo-grupo/titulo-grupo';
import { EscritorioRegionalService, derivarUfsFaltantesPorRegiao } from '../../services/escritorio-regional/escritorio-regional';
import {
  DiaSemana,
  EstadoIntergrupo,
  GrupoIntergrupo,
  RegiaoIntergrupo,
  UnidadeFederativaComRegiao,
} from '../../services/escritorio-regional/escritorio-regional.model';
import { compararHorarios } from '../../services/reuniao/reuniao-horario';
import { DiaSemanaAgendaReuniao } from '../../services/reuniao/reuniao.model';
import { ReuniaoOnlineService } from '../../services/reuniao-online/reuniao-online';
import { ReuniaoOnline } from '../../services/reuniao-online/reuniao-online.model';
import { WHATSAPP_PHONE_NUMBER, montarLinkWhatsapp } from '../../config/whatsapp.config';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

interface CidadeComGrupos {
  cidade: string;
  grupos: GrupoIntergrupo[];
}

interface EstadoRegiaoItemComGrupo {
  tipo: 'com-grupo';
  nome: string;
  uf: string;
  estado: EstadoIntergrupo;
  cidades: CidadeComGrupos[];
}

interface EstadoRegiaoItemSemGrupo {
  tipo: 'sem-grupo';
  nome: string;
  uf: string;
  unidade: UnidadeFederativaComRegiao;
}

type EstadoRegiaoItem = EstadoRegiaoItemComGrupo | EstadoRegiaoItemSemGrupo;

type ModoExibicaoEnderecos = 'presencial' | 'online';

type DiaSemanaCurto = 'Segunda' | 'Terça' | 'Quarta' | 'Quinta' | 'Sexta' | 'Sábado' | 'Domingo';

interface ItemGradeOnline {
  horario: string;
  nomeGrupo: string;
}

interface DiaGradeOnline {
  diaSemana: DiaSemanaAgendaReuniao;
  rotulo: DiaSemanaCurto;
  itens: ItemGradeOnline[];
}

const NOME_GRUPO_PROPRIO_ONLINE = 'Grupo Salvador';
const YOUTUBE_ID_COMO_ACESSAR = 'ETBCaVzBpbc';

const LABEL_DIA_SEMANA: Record<DiaSemana, string> = {
  DOMINGO: 'Dom',
  SEGUNDA: 'Seg',
  TERCA: 'Ter',
  QUARTA: 'Qua',
  QUINTA: 'Qui',
  SEXTA: 'Sex',
  SABADO: 'Sáb',
};

const ORDEM_REGIOES = ['SUDESTE', 'SUL', 'CENTRO_OESTE', 'NORTE', 'NORDESTE'];

const ORDEM_UF_PRIORITARIA_POR_REGIAO: Record<string, string[]> = {
  SUDESTE: ['SP', 'RJ'],
};

const DEBOUNCE_FILTRO_TEXTO_MS = 200;

const DIAS_AGENDA: ReadonlyArray<{ diaSemana: DiaSemanaAgendaReuniao; rotulo: DiaSemanaCurto }> = [
  { diaSemana: 'SEGUNDA', rotulo: 'Segunda' },
  { diaSemana: 'TERCA', rotulo: 'Terça' },
  { diaSemana: 'QUARTA', rotulo: 'Quarta' },
  { diaSemana: 'QUINTA', rotulo: 'Quinta' },
  { diaSemana: 'SEXTA', rotulo: 'Sexta' },
  { diaSemana: 'SABADO', rotulo: 'Sábado' },
  { diaSemana: 'DOMINGO', rotulo: 'Domingo' },
];

@Component({
  selector: 'app-enderecos',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, ResourceLayout, TituloGrupoPipe],
  templateUrl: './enderecos.html',
  styleUrl: './enderecos.css',
})
export class Enderecos implements OnInit, OnDestroy {
  private readonly escritorioRegionalService = inject(EscritorioRegionalService);
  private readonly reuniaoOnlineService = inject(ReuniaoOnlineService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly sanitizer = inject(DomSanitizer);

  readonly regioes = signal<RegiaoIntergrupo[]>([]);
  readonly ufsFaltantesPorRegiao = signal<Map<string, UnidadeFederativaComRegiao[]>>(new Map());
  readonly reunioesOnline = signal<ReuniaoOnline[]>([]);
  readonly carregando = signal(true);
  readonly erro = signal(false);

  readonly filtroTexto = signal('');
  private readonly filtroTextoDebounced = signal('');
  private debounceFiltroTextoId?: ReturnType<typeof setTimeout>;
  readonly filtroUf = signal<string | 'todas'>('todas');

  readonly regiaoSelecionada = signal<string | null>(null);

  readonly modoExibicao = signal<ModoExibicaoEnderecos>('presencial');

  grupoDetalhe: GrupoIntergrupo | null = null;

  readonly embedVideoComoAcessarOnline: SafeResourceUrl = this.sanitizer.bypassSecurityTrustResourceUrl(
    `https://www.youtube-nocookie.com/embed/${YOUTUBE_ID_COMO_ACESSAR}`,
  );

  ngOnInit(): void {
    if (this.activatedRoute.snapshot.queryParamMap.get('tipo') === 'online') {
      this.modoExibicao.set('online');
    }
    forkJoin({
      estrutura: this.escritorioRegionalService.listarEstrutura(),
      unidadesFederativas: this.escritorioRegionalService.listarUnidadesFederativas(),
      reunioesOnline: this.reuniaoOnlineService.listarReunioesOnline(),
    }).subscribe({
      next: ({ estrutura, unidadesFederativas, reunioesOnline }) => {
        this.carregando.set(false);
        if (estrutura.success && estrutura.data) {
          const regioesOrdenadas = [...estrutura.data.regioes].sort(
            (a, b) => ORDEM_REGIOES.indexOf(a.codigo) - ORDEM_REGIOES.indexOf(b.codigo),
          );
          this.regioes.set(regioesOrdenadas);
          this.regiaoSelecionada.set(regioesOrdenadas[0]?.id ?? null);
          if (unidadesFederativas.success && unidadesFederativas.data) {
            this.ufsFaltantesPorRegiao.set(
              derivarUfsFaltantesPorRegiao(regioesOrdenadas, unidadesFederativas.data),
            );
          }
        } else {
          this.erro.set(true);
        }

        if (reunioesOnline.success && reunioesOnline.data) {
          this.reunioesOnline.set(reunioesOnline.data);
        }
      },
      error: () => {
        this.carregando.set(false);
        this.erro.set(true);
      },
    });
  }

  selecionarModoExibicao(modo: ModoExibicaoEnderecos): void {
    this.modoExibicao.set(modo);
  }

  ngOnDestroy(): void {
    clearTimeout(this.debounceFiltroTextoId);
  }

  get regiaoAtual(): RegiaoIntergrupo | undefined {
    return this.regioes().find((r) => r.id === this.regiaoSelecionada());
  }

  aplicarFiltroTexto(valor: string): void {
    this.filtroTexto.set(valor);
    clearTimeout(this.debounceFiltroTextoId);
    this.debounceFiltroTextoId = setTimeout(() => {
      this.filtroTextoDebounced.set(valor);
    }, DEBOUNCE_FILTRO_TEXTO_MS);
  }

  aplicarFiltroUf(valor: string | 'todas'): void {
    this.filtroUf.set(valor);
  }

  readonly ufsDisponiveis = computed<{ uf: string; nome: string }[]>(() => {
    const regiao = this.regiaoAtual;
    if (!regiao) {
      return [];
    }
    const valores = new Map<string, string>();
    for (const estado of regiao.estados) {
      valores.set(estado.uf, estado.nome);
    }
    return [...valores.entries()]
      .map(([uf, nome]) => ({ uf, nome }))
      .sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR'));
  });

  private grupoCorrespondeAoFiltro(grupo: GrupoIntergrupo, texto: string): boolean {
    if (texto === '') {
      return true;
    }
    return (
      grupo.nome.toLowerCase().includes(texto) ||
      grupo.cidade.toLowerCase().includes(texto) ||
      (grupo.bairro ?? '').toLowerCase().includes(texto) ||
      grupo.endereco.toLowerCase().includes(texto)
    );
  }

  readonly estadosRegiaoAtual = computed<EstadoRegiaoItem[]>(() => {
    const regiao = this.regiaoAtual;
    if (!regiao) {
      return [];
    }

    const textoFiltro = this.filtroTextoDebounced().trim().toLowerCase();
    const ufFiltro = this.filtroUf();

    const comGrupo: EstadoRegiaoItemComGrupo[] = regiao.estados
      .filter((estado) => ufFiltro === 'todas' || estado.uf === ufFiltro)
      .map((estado) => {
        const gruposFiltrados = estado.grupos.filter((grupo) => this.grupoCorrespondeAoFiltro(grupo, textoFiltro));
        return {
          tipo: 'com-grupo' as const,
          nome: estado.nome,
          uf: estado.uf,
          estado: {
            ...estado,
            grupos: gruposFiltrados,
          },
          cidades: this.agruparGruposPorCidade(gruposFiltrados),
        };
      })
      .filter((item) => textoFiltro === '' || item.estado.grupos.length > 0);

    const semGrupo: EstadoRegiaoItem[] = (this.ufsFaltantesPorRegiao().get(regiao.codigo) ?? [])
      .filter((unidade) => ufFiltro === 'todas' || unidade.uf === ufFiltro)
      .filter((unidade) => textoFiltro === '')
      .map((unidade) => ({
        tipo: 'sem-grupo' as const,
        nome: unidade.nome,
        uf: unidade.uf,
        unidade,
      }));

    const ordemPrioritaria = ORDEM_UF_PRIORITARIA_POR_REGIAO[regiao.codigo] ?? [];
    return [...comGrupo, ...semGrupo].sort((a, b) => {
      const prioridadeA = ordemPrioritaria.indexOf(a.uf);
      const prioridadeB = ordemPrioritaria.indexOf(b.uf);
      if (prioridadeA !== -1 || prioridadeB !== -1) {
        if (prioridadeA === -1) {
          return 1;
        }
        if (prioridadeB === -1) {
          return -1;
        }
        return prioridadeA - prioridadeB;
      }
      return a.nome.localeCompare(b.nome, 'pt-BR');
    });
  });

  readonly nenhumResultadoFiltro = computed<boolean>(() => {
    const temFiltroAtivo = this.filtroTextoDebounced().trim() !== '' || this.filtroUf() !== 'todas';
    return temFiltroAtivo && this.regiaoAtual !== undefined && this.estadosRegiaoAtual().length === 0;
  });

  temPresencialNoBloco(item: EstadoRegiaoItem): boolean {
    return item.tipo === 'com-grupo' && item.estado.grupos.length > 0;
  }

  private agruparGruposPorCidade(grupos: GrupoIntergrupo[]): CidadeComGrupos[] {
    const porCidade = new Map<string, GrupoIntergrupo[]>();
    for (const grupo of grupos) {
      const chave = grupo.cidade.trim();
      const grupoExistente = porCidade.get(chave);
      if (grupoExistente) {
        grupoExistente.push(grupo);
      } else {
        porCidade.set(chave, [grupo]);
      }
    }
    return [...porCidade.entries()].map(([cidade, gruposDaCidade]) => ({ cidade, grupos: gruposDaCidade }));
  }

  readonly gradeOnlinePorDia = computed<DiaGradeOnline[]>(() => {
    const reunioesOnline = this.reunioesOnline();
    return DIAS_AGENDA.map(({ diaSemana, rotulo }) => {
      const itens: ItemGradeOnline[] = reunioesOnline
        .flatMap((reuniao) =>
          reuniao.agenda
            .filter((item) => item.diaSemana === diaSemana)
            .map((item) => ({ horario: item.horario, nomeGrupo: reuniao.nome })),
        )
        .sort((a, b) => compararHorarios(a.horario, b.horario));
      return { diaSemana, rotulo, itens };
    }).filter((dia) => dia.itens.length > 0);
  });

  readonly grupoProprioOnline = computed<ReuniaoOnline | null>(() => {
    return this.reunioesOnline().find((reuniao) => reuniao.nome.trim() === NOME_GRUPO_PROPRIO_ONLINE) ?? null;
  });

  readonly nomesGruposOnline = computed<string[]>(() => {
    return [...new Set(this.reunioesOnline().map((reuniao) => reuniao.nome))].sort((a, b) =>
      a.localeCompare(b, 'pt-BR'),
    );
  });

  ehGrupoProprioOnline(nomeGrupo: string): boolean {
    return nomeGrupo.trim() === NOME_GRUPO_PROPRIO_ONLINE;
  }

  linkWhatsappGrupoInexistente(): string {
    return montarLinkWhatsapp(WHATSAPP_PHONE_NUMBER, 'Olá! Gostaria de saber sobre grupos na minha região.');
  }

  linkWhatsappPedirLinkReuniaoOnline(): string {
    return montarLinkWhatsapp(WHATSAPP_PHONE_NUMBER, 'Olá! Gostaria de pedir o link de uma reunião online.');
  }

  selecionarRegiao(regiao: RegiaoIntergrupo): void {
    this.regiaoSelecionada.set(regiao.id);
    this.filtroUf.set('todas');
  }

  formatarAgenda(agenda: { diaSemana: DiaSemana; horario: string }[]): string {
    return agenda.map((item) => `${LABEL_DIA_SEMANA[item.diaSemana]} ${item.horario}`).join(' · ');
  }

  formatarTelefones(telefones: { label: string | null; telefone: string }[]): string {
    return telefones.map((t) => (t.label ? `${t.label}: ${t.telefone}` : t.telefone)).join(' / ');
  }

  whatsappLinkGrupo(grupo: GrupoIntergrupo): string {
    const telefoneGrupo = grupo.telefones[0]?.telefone;
    return montarLinkWhatsapp(telefoneGrupo || WHATSAPP_PHONE_NUMBER);
  }

  enderecoCompletoGrupo(grupo: GrupoIntergrupo): string {
    return `${grupo.endereco}${grupo.bairro ? `, ${grupo.bairro}` : ''}, ${grupo.cidade}`;
  }

  abrirDetalhesGrupo(grupo: GrupoIntergrupo): void {
    this.grupoDetalhe = grupo;
  }

  fecharDetalhes(): void {
    this.grupoDetalhe = null;
  }

  fecharDetalhesSeCliqueForaDoPainel(evento: MouseEvent): void {
    if (evento.target === evento.currentTarget) {
      this.fecharDetalhes();
    }
  }

  @HostListener('document:keydown.escape')
  onEscape(): void {
    if (this.grupoDetalhe) {
      this.fecharDetalhes();
    }
  }
}
