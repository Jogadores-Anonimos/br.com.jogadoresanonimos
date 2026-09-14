import {
  Component,
  HostListener,
  OnDestroy,
  OnInit,
  PLATFORM_ID,
  computed,
  inject,
  signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { isPlatformBrowser } from '@angular/common';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { ReuniaoService } from '../../services/reuniao/reuniao';
import { compararHorarios } from '../../services/reuniao/reuniao-horario';
import {
  AgendaItemReuniao,
  CategoriaReuniao,
  DiaSemanaAgendaReuniao,
  Reuniao,
} from '../../services/reuniao/reuniao.model';
import { WHATSAPP_PHONE_NUMBER, montarLinkWhatsapp } from '../../config/whatsapp.config';

type DiaSemana = 'Segunda' | 'Terça' | 'Quarta' | 'Quinta' | 'Sexta' | 'Sábado' | 'Domingo';

interface FiltroCategoria {
  valor: CategoriaReuniao;
  titulo: string;
  descricao: string;
}

interface GrupoDiaVirtual {
  dia: DiaSemana;
  itens: Array<{ reuniao: Reuniao; horarios: string[] }>;
}

interface AgendaDiaAgrupada {
  diaSemana: DiaSemanaAgendaReuniao;
  horarios: string[];
}

const RAIO_TERRA_KM = 6371;

const ITENS_POR_PAGINA = 10;

const DEBOUNCE_FILTRO_TEXTO_MS = 200;

const DIAS_AGENDA: ReadonlyArray<{ diaSemana: DiaSemanaAgendaReuniao; rotulo: DiaSemana }> = [
  { diaSemana: 'SEGUNDA', rotulo: 'Segunda' },
  { diaSemana: 'TERCA', rotulo: 'Terça' },
  { diaSemana: 'QUARTA', rotulo: 'Quarta' },
  { diaSemana: 'QUINTA', rotulo: 'Quinta' },
  { diaSemana: 'SEXTA', rotulo: 'Sexta' },
  { diaSemana: 'SABADO', rotulo: 'Sábado' },
  { diaSemana: 'DOMINGO', rotulo: 'Domingo' },
];

const ROTULO_DIA: Record<DiaSemanaAgendaReuniao, DiaSemana> = Object.fromEntries(
  DIAS_AGENDA.map((dia) => [dia.diaSemana, dia.rotulo]),
) as Record<DiaSemanaAgendaReuniao, DiaSemana>;

@Component({
  selector: 'app-reunioes',
  standalone: true,
  imports: [FormsModule, ResourceLayout],
  templateUrl: './reunioes.html',
  styleUrl: './reunioes.css',
})
export class Reunioes implements OnInit, OnDestroy {
  private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));
  private readonly reuniaoService = inject(ReuniaoService);

  readonly categorias: FiltroCategoria[] = [
    {
      valor: 'presencial',
      titulo: 'Presencial',
      descricao: 'Reuniões em grupos físicos perto de você.',
    },
    {
      valor: 'virtual',
      titulo: 'Virtual',
      descricao: 'Reuniões por videochamada, de onde você estiver.',
    },
  ];

  readonly diasSemana: DiaSemana[] = DIAS_AGENDA.map((dia) => dia.rotulo);

  readonly reunioes = signal<Reuniao[]>([]);
  carregandoReunioes = true;
  erroCarregamentoReunioes = '';

  readonly categoriaSelecionada = signal<CategoriaReuniao | null>(null);

  readonly filtroEstado = signal<string | 'todos'>('todos');
  readonly filtroCidade = signal<string | 'todos'>('todos');
  readonly filtroTexto = signal('');
  private readonly filtroTextoDebounced = signal('');
  private debounceFiltroTextoId?: ReturnType<typeof setTimeout>;

  readonly itensVisiveis = signal(ITENS_POR_PAGINA);

  private readonly userCoords = signal<{ latitude: number; longitude: number } | null>(null);

  localizacaoPermitida = signal(false);
  localizacaoIndisponivel = signal(false);
  localizacaoCarregando = signal(false);

  reuniaoDetalhe: Reuniao | null = null;

  ngOnInit(): void {
    this.carregarReunioes();

    if (!this.isBrowser) {
      return;
    }
    this.solicitarLocalizacao();
  }

  private carregarReunioes(): void {
    this.carregandoReunioes = true;
    this.erroCarregamentoReunioes = '';

    this.reuniaoService.listarReunioes().subscribe((envelope) => {
      this.carregandoReunioes = false;

      if (envelope.success && envelope.data) {
        this.reunioes.set(envelope.data);
      } else {
        this.erroCarregamentoReunioes = envelope.error?.message ?? 'Não foi possível carregar as reuniões agora.';
      }
    });
  }

  ngOnDestroy(): void {
    clearTimeout(this.debounceFiltroTextoId);
  }

  selecionarCategoria(categoria: CategoriaReuniao): void {
    this.categoriaSelecionada.set(categoria);
    this.filtroEstado.set('todos');
    this.filtroCidade.set('todos');
    this.filtroTexto.set('');
    this.filtroTextoDebounced.set('');
    clearTimeout(this.debounceFiltroTextoId);
    this.itensVisiveis.set(ITENS_POR_PAGINA);
    this.fecharDetalhes();
  }

  aplicarFiltroEstado(valor: string | 'todos'): void {
    this.filtroEstado.set(valor);
    this.filtroCidade.set('todos');
    this.itensVisiveis.set(ITENS_POR_PAGINA);
  }

  aplicarFiltroCidade(valor: string | 'todos'): void {
    this.filtroCidade.set(valor);
    this.itensVisiveis.set(ITENS_POR_PAGINA);
  }

  aplicarFiltroTexto(valor: string): void {
    this.filtroTexto.set(valor);
    this.itensVisiveis.set(ITENS_POR_PAGINA);
    clearTimeout(this.debounceFiltroTextoId);
    this.debounceFiltroTextoId = setTimeout(() => {
      this.filtroTextoDebounced.set(valor);
    }, DEBOUNCE_FILTRO_TEXTO_MS);
  }

  carregarMaisReunioes(): void {
    this.itensVisiveis.update((valor) => valor + ITENS_POR_PAGINA);
  }

  solicitarLocalizacao(): void {
    if (!('geolocation' in navigator)) {
      this.localizacaoIndisponivel.set(true);
      return;
    }

    this.localizacaoCarregando.set(true);
    this.localizacaoIndisponivel.set(false);

    navigator.geolocation.getCurrentPosition(
      (posicao) => {
        const coords = {
          latitude: posicao.coords.latitude,
          longitude: posicao.coords.longitude,
        };
        this.userCoords.set(coords);
        this.localizacaoPermitida.set(true);
        this.localizacaoCarregando.set(false);
      },
      () => {
        this.localizacaoIndisponivel.set(true);
        this.localizacaoCarregando.set(false);
      },
      { enableHighAccuracy: false, timeout: 8000 },
    );
  }

  distanciaKm(reuniao: Reuniao): number | null {
    const userCoords = this.userCoords();
    if (!userCoords || reuniao.latitude === null || reuniao.longitude === null) {
      return null;
    }

    const dLat = this.paraRadianos(reuniao.latitude - userCoords.latitude);
    const dLon = this.paraRadianos(reuniao.longitude - userCoords.longitude);
    const lat1 = this.paraRadianos(userCoords.latitude);
    const lat2 = this.paraRadianos(reuniao.latitude);

    const a =
      Math.sin(dLat / 2) ** 2 + Math.sin(dLon / 2) ** 2 * Math.cos(lat1) * Math.cos(lat2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return Math.round(RAIO_TERRA_KM * c * 10) / 10;
  }

  private paraRadianos(graus: number): number {
    return (graus * Math.PI) / 180;
  }

  whatsappLink(reuniao: Reuniao): string {
    return montarLinkWhatsapp(reuniao.whatsapp || WHATSAPP_PHONE_NUMBER);
  }

  private readonly reunioesPresenciais = computed<Reuniao[]>(() =>
    this.reunioes().filter((r) => r.categoria === 'presencial'),
  );

  readonly estadosDisponiveis = computed<string[]>(() => {
    const valores = new Set<string>();
    for (const reuniao of this.reunioesPresenciais()) {
      if (reuniao.estado) {
        valores.add(reuniao.estado);
      }
    }
    return [...valores].sort((a, b) => a.localeCompare(b, 'pt-BR'));
  });

  readonly cidadesDisponiveis = computed<string[]>(() => {
    const filtroEstado = this.filtroEstado();
    const valores = new Set<string>();
    for (const reuniao of this.reunioesPresenciais()) {
      if (!reuniao.cidade) {
        continue;
      }
      if (filtroEstado !== 'todos' && reuniao.estado !== filtroEstado) {
        continue;
      }
      valores.add(reuniao.cidade);
    }
    return [...valores].sort((a, b) => a.localeCompare(b, 'pt-BR'));
  });

  readonly reunioesFiltradas = computed<Reuniao[]>(() => {
    const categoriaSelecionada = this.categoriaSelecionada();
    if (!categoriaSelecionada) {
      return [];
    }

    const userCoords = this.userCoords();
    let resultado = this.reunioes().filter((r) => r.categoria === categoriaSelecionada);

    const textoFiltro = this.filtroTextoDebounced().trim().toLowerCase();
    if (textoFiltro !== '') {
      resultado = resultado.filter(
        (r) =>
          r.nome.toLowerCase().includes(textoFiltro) ||
          r.local.toLowerCase().includes(textoFiltro) ||
          r.descricao.toLowerCase().includes(textoFiltro) ||
          this.agendaDaReuniao(r).some(
            (item) =>
              ROTULO_DIA[item.diaSemana].toLowerCase().includes(textoFiltro) ||
              item.horario.toLowerCase().includes(textoFiltro),
          ),
      );
    }

    if (categoriaSelecionada === 'presencial') {
      const filtroEstado = this.filtroEstado();
      const filtroCidade = this.filtroCidade();

      if (filtroEstado !== 'todos') {
        resultado = resultado.filter((r) => r.estado === filtroEstado);
      }
      if (filtroCidade !== 'todos') {
        resultado = resultado.filter((r) => r.cidade === filtroCidade);
      }

      if (userCoords) {
        resultado = [...resultado].sort(
          (a, b) => (this.distanciaKm(a) ?? Infinity) - (this.distanciaKm(b) ?? Infinity),
        );
      }
    }

    return resultado;
  });

  readonly reunioesPaginadas = computed<Reuniao[]>(() =>
    this.reunioesFiltradas().slice(0, this.itensVisiveis()),
  );

  readonly reunioesVirtuaisAgrupadas = computed<GrupoDiaVirtual[]>(() => {
    if (this.categoriaSelecionada() !== 'virtual') {
      return [];
    }

    const grupos: GrupoDiaVirtual[] = [];
    for (const dia of DIAS_AGENDA) {
      const itens = this.reunioesFiltradas()
        .flatMap((reuniao) => {
          const agendaDia = this.agendaAgrupadaDaReuniao(reuniao).find((item) => item.diaSemana === dia.diaSemana);
          return agendaDia ? [{ reuniao, horarios: agendaDia.horarios }] : [];
        })
        .sort(
          (a, b) =>
            compararHorarios(a.horarios[0], b.horarios[0]) || a.reuniao.nome.localeCompare(b.reuniao.nome),
        );
      if (itens.length > 0) {
        grupos.push({ dia: dia.rotulo, itens });
      }
    }

    return grupos;
  });

  agendaDaReuniao(reuniao: Reuniao): AgendaItemReuniao[] {
    if (reuniao.agenda?.length) {
      return reuniao.agenda;
    }
    const valorLegado = (reuniao.diaSemana || reuniao.dia)
      ?.normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .toLowerCase()
      .replace(/-feira$/, '')
      .trim();
    const dia = DIAS_AGENDA.find((item) => item.rotulo.normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase() === valorLegado);
    return dia && reuniao.horario ? [{ diaSemana: dia.diaSemana, horario: reuniao.horario }] : [];
  }

  resumoAgenda(reuniao: Reuniao): string {
    return this.agendaAgrupadaDaReuniao(reuniao)
      .map((item) => `${ROTULO_DIA[item.diaSemana]} ${item.horarios.join(', ')}`)
      .join(' · ');
  }

  agendaAgrupadaDaReuniao(reuniao: Reuniao): AgendaDiaAgrupada[] {
    return DIAS_AGENDA.flatMap(({ diaSemana }) => {
      const horarios = [...new Set(
        this.agendaDaReuniao(reuniao)
          .filter((item) => item.diaSemana === diaSemana)
          .map((item) => item.horario),
      )].sort(compararHorarios);
      return horarios.length ? [{ diaSemana, horarios }] : [];
    });
  }

  rotuloDiaAgenda(item: { diaSemana: DiaSemanaAgendaReuniao }): DiaSemana {
    return ROTULO_DIA[item.diaSemana];
  }

  abrirDetalhes(reuniao: Reuniao): void {
    this.reuniaoDetalhe = reuniao;
  }

  fecharDetalhes(): void {
    this.reuniaoDetalhe = null;
  }

  fecharDetalhesSeCliqueForaDoPainel(evento: MouseEvent): void {
    if (evento.target === evento.currentTarget) {
      this.fecharDetalhes();
    }
  }

  @HostListener('document:keydown.escape')
  onEscape(): void {
    if (this.reuniaoDetalhe) {
      this.fecharDetalhes();
    }
  }
}
