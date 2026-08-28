import { CommonModule, isPlatformBrowser } from '@angular/common';
import {
  Component,
  ElementRef,
  HostListener,
  OnDestroy,
  OnInit,
  PLATFORM_ID,
  ViewChild,
  computed,
  effect,
  inject,
  signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import type {
  GeoJSONSource,
  LngLatLike,
  Map as MapLibreMap,
  StyleSpecification,
} from 'maplibre-gl';
import type { Feature, FeatureCollection, Point } from 'geojson';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { EscritorioRegionalService, derivarUfsFaltantesPorRegiao } from '../../services/escritorio-regional/escritorio-regional';
import {
  DiaSemana,
  EstadoIntergrupo,
  GrupoIntergrupo,
  RegiaoIntergrupo,
  UnidadeFederativaComRegiao,
} from '../../services/escritorio-regional/escritorio-regional.model';
import { ReuniaoService } from '../../services/reuniao/reuniao';
import { compararHorarios } from '../../services/reuniao/reuniao-horario';
import { AgendaItemReuniao, DiaSemanaAgendaReuniao, Reuniao } from '../../services/reuniao/reuniao.model';
import { WHATSAPP_PHONE_NUMBER, montarLinkWhatsapp } from '../../config/whatsapp.config';
import { configurarMapLibreWorker } from '../../config/maplibre-worker.config';

interface EstadoRegiaoItemComGrupo {
  tipo: 'com-grupo';
  nome: string;
  uf: string;
  estado: EstadoIntergrupo;
}

interface EstadoRegiaoItemSemGrupo {
  tipo: 'sem-grupo';
  nome: string;
  uf: string;
  unidade: UnidadeFederativaComRegiao;
}

type EstadoRegiaoItem = EstadoRegiaoItemComGrupo | EstadoRegiaoItemSemGrupo;

type DiaSemanaCurto = 'Segunda' | 'Terça' | 'Quarta' | 'Quinta' | 'Sexta' | 'Sábado' | 'Domingo';

interface AgendaDiaAgrupada {
  diaSemana: DiaSemanaAgendaReuniao;
  horarios: string[];
}

interface PropriedadesPontoReuniao {
  id: string;
  nome: string;
  dia: string;
  horario: string;
  local: string;
}

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

const RAIO_TERRA_KM = 6371;

const CENTRO_PADRAO: LngLatLike = [-46.62, -23.55];
const ZOOM_PADRAO = 4;
const ZOOM_REGIAO_USUARIO = 10;
const ZOOM_MINIMO_BOUNDS = 11;
const ZOOM_MAXIMO_BOUNDS = 13;
const PADDING_BOUNDS_PX = 48;

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

const ROTULO_DIA: Record<DiaSemanaAgendaReuniao, DiaSemanaCurto> = Object.fromEntries(
  DIAS_AGENDA.map((dia) => [dia.diaSemana, dia.rotulo]),
) as Record<DiaSemanaAgendaReuniao, DiaSemanaCurto>;

const FONTE_REUNIOES = 'reunioes';
const CAMADA_PONTOS_REUNIOES = 'reunioes-pontos';
const FONTE_USUARIO = 'usuario';
const CAMADA_PONTO_USUARIO = 'usuario-ponto';

const ESTILO_OSM: StyleSpecification = {
  version: 8,
  sources: {
    osm: {
      type: 'raster',
      tiles: [
        'https://a.tile.openstreetmap.org/{z}/{x}/{y}.png',
        'https://b.tile.openstreetmap.org/{z}/{x}/{y}.png',
        'https://c.tile.openstreetmap.org/{z}/{x}/{y}.png',
      ],
      tileSize: 256,
      attribution: '&copy; colaboradores do OpenStreetMap',
    },
  },
  layers: [
    {
      id: 'osm-tiles',
      type: 'raster',
      source: 'osm',
    },
  ],
};

function montarFeatureCollection(reunioes: Reuniao[]): FeatureCollection<Point, PropriedadesPontoReuniao> {
  const features: Feature<Point, PropriedadesPontoReuniao>[] = [];

  for (const reuniao of reunioes) {
    if (reuniao.latitude === null || reuniao.longitude === null) {
      continue;
    }

    features.push({
      type: 'Feature',
      geometry: {
        type: 'Point',
        coordinates: [reuniao.longitude, reuniao.latitude],
      },
      properties: {
        id: reuniao.id,
        nome: reuniao.nome,
        dia: reuniao.dia,
        horario: reuniao.horario,
        local: reuniao.local,
      },
    });
  }

  return { type: 'FeatureCollection', features };
}

function montarFeatureUsuario(coords: { latitude: number; longitude: number }): FeatureCollection<Point, Record<string, never>> {
  return {
    type: 'FeatureCollection',
    features: [
      {
        type: 'Feature',
        geometry: {
          type: 'Point',
          coordinates: [coords.longitude, coords.latitude],
        },
        properties: {},
      },
    ],
  };
}

function corPadraoJa(): string {
  return getComputedStyle(document.documentElement).getPropertyValue('--cor-padrao-ja').trim();
}

function corSucesso(): string {
  return getComputedStyle(document.documentElement).getPropertyValue('--color-success').trim();
}

function corBranca(): string {
  return getComputedStyle(document.documentElement).getPropertyValue('--white').trim();
}

@Component({
  selector: 'app-enderecos',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, ResourceLayout],
  templateUrl: './enderecos.html',
  styleUrl: './enderecos.css',
})
export class Enderecos implements OnInit, OnDestroy {
  private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));
  private readonly escritorioRegionalService = inject(EscritorioRegionalService);
  private readonly reuniaoService = inject(ReuniaoService);

  readonly regioes = signal<RegiaoIntergrupo[]>([]);
  readonly ufsFaltantesPorRegiao = signal<Map<string, UnidadeFederativaComRegiao[]>>(new Map());
  readonly reunioes = signal<Reuniao[]>([]);
  readonly carregando = signal(true);
  readonly erro = signal(false);

  readonly mapaAberto = signal(false);

  readonly filtroTexto = signal('');
  private readonly filtroTextoDebounced = signal('');
  private debounceFiltroTextoId?: ReturnType<typeof setTimeout>;
  readonly filtroUf = signal<string | 'todas'>('todas');

  readonly regiaoSelecionada = signal<string | null>(null);

  reuniaoDetalhe: Reuniao | null = null;
  grupoDetalhe: GrupoIntergrupo | null = null;

  private readonly userCoords = signal<{ latitude: number; longitude: number } | null>(null);
  localizacaoPermitida = signal(false);
  localizacaoIndisponivel = signal(false);
  localizacaoCarregando = signal(false);

  private maplibregl?: typeof import('maplibre-gl');
  private mapa?: MapLibreMap;
  private resizeObserver?: ResizeObserver;

  private mapaContainerAtual?: HTMLDivElement;

  @ViewChild('mapaContainer')
  set mapaContainerRef(ref: ElementRef<HTMLDivElement> | undefined) {
    if (!this.isBrowser) {
      return;
    }
    if (ref) {
      if (this.mapaContainerAtual === ref.nativeElement) {
        return;
      }
      this.mapaContainerAtual = ref.nativeElement;
      this.carregarMapLibreECriarMapa(ref.nativeElement);
    } else {
      this.mapaContainerAtual = undefined;
      this.destruirMapa();
    }
  }

  constructor() {
    effect(() => {
      this.filtroTextoDebounced();
      this.filtroUf();
      this.atualizarDadosMapa();
    });
  }

  ngOnInit(): void {
    forkJoin({
      estrutura: this.escritorioRegionalService.listarEstrutura(),
      unidadesFederativas: this.escritorioRegionalService.listarUnidadesFederativas(),
      reunioes: this.reuniaoService.listarReunioes(),
    }).subscribe({
      next: ({ estrutura, unidadesFederativas, reunioes }) => {
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

        if (reunioes.success && reunioes.data) {
          this.reunioes.set(reunioes.data);
        }
      },
      error: () => {
        this.carregando.set(false);
        this.erro.set(true);
      },
    });
  }

  ngOnDestroy(): void {
    clearTimeout(this.debounceFiltroTextoId);
    this.destruirMapa();
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

  private reuniaoOnlineCorrespondeAoFiltro(reuniao: Reuniao, texto: string): boolean {
    if (texto === '') {
      return true;
    }
    return (
      reuniao.nome.toLowerCase().includes(texto) ||
      reuniao.local.toLowerCase().includes(texto) ||
      reuniao.descricao.toLowerCase().includes(texto)
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
      .map((estado) => ({
        tipo: 'com-grupo' as const,
        nome: estado.nome,
        uf: estado.uf,
        estado: {
          ...estado,
          grupos: estado.grupos.filter((grupo) => this.grupoCorrespondeAoFiltro(grupo, textoFiltro)),
        },
      }))
      .filter((item) => {
        const temGrupo = item.estado.grupos.length > 0;
        const temOnline = this.reunioesOnlineDaUf(item.uf).length > 0;
        return textoFiltro === '' || temGrupo || temOnline;
      });

    const semGrupo: EstadoRegiaoItem[] = (this.ufsFaltantesPorRegiao().get(regiao.codigo) ?? [])
      .filter((unidade) => ufFiltro === 'todas' || unidade.uf === ufFiltro)
      .filter((unidade) => textoFiltro === '' || this.reunioesOnlineDaUf(unidade.uf).length > 0)
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

  reunioesOnlineDaUf(uf: string): Reuniao[] {
    const textoFiltro = this.filtroTextoDebounced().trim().toLowerCase();
    return this.reunioes().filter(
      (r) => r.categoria === 'virtual' && r.estado === uf && this.reuniaoOnlineCorrespondeAoFiltro(r, textoFiltro),
    );
  }

  linkWhatsappGrupoInexistente(): string {
    return montarLinkWhatsapp(WHATSAPP_PHONE_NUMBER, 'Olá! Gostaria de saber sobre grupos na minha região.');
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

  toggleMapa(): void {
    this.mapaAberto.update((valor) => !valor);
  }

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

  rotuloDiaAgenda(item: { diaSemana: DiaSemanaAgendaReuniao }): DiaSemanaCurto {
    return ROTULO_DIA[item.diaSemana];
  }

  whatsappLink(reuniao: Reuniao): string {
    return montarLinkWhatsapp(reuniao.whatsapp || WHATSAPP_PHONE_NUMBER);
  }

  whatsappLinkGrupo(grupo: GrupoIntergrupo): string {
    const telefoneGrupo = grupo.telefones[0]?.telefone;
    return montarLinkWhatsapp(telefoneGrupo || WHATSAPP_PHONE_NUMBER);
  }

  enderecoCompletoGrupo(grupo: GrupoIntergrupo): string {
    return `${grupo.endereco}${grupo.bairro ? `, ${grupo.bairro}` : ''}, ${grupo.cidade}`;
  }

  abrirDetalhes(reuniao: Reuniao): void {
    this.reuniaoDetalhe = reuniao;
  }

  abrirDetalhesGrupo(grupo: GrupoIntergrupo): void {
    this.grupoDetalhe = grupo;
  }

  fecharDetalhes(): void {
    this.reuniaoDetalhe = null;
    this.grupoDetalhe = null;
  }

  fecharDetalhesSeCliqueForaDoPainel(evento: MouseEvent): void {
    if (evento.target === evento.currentTarget) {
      this.fecharDetalhes();
    }
  }

  @HostListener('document:keydown.escape')
  onEscape(): void {
    if (this.reuniaoDetalhe || this.grupoDetalhe) {
      this.fecharDetalhes();
    }
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
        this.atualizarMarcadorUsuario(coords);
        this.centralizarMapaNoUsuario(coords);
      },
      () => {
        this.localizacaoIndisponivel.set(true);
        this.localizacaoCarregando.set(false);
      },
      { enableHighAccuracy: false, timeout: 8000 },
    );
  }

  private grupoIdsVisiveisAposFiltro(): Set<string> | null {
    const textoFiltro = this.filtroTextoDebounced().trim().toLowerCase();
    const ufFiltro = this.filtroUf();
    if (textoFiltro === '' && ufFiltro === 'todas') {
      return null;
    }

    const ids = new Set<string>();
    for (const regiao of this.regioes()) {
      for (const estado of regiao.estados) {
        if (ufFiltro !== 'todas' && estado.uf !== ufFiltro) {
          continue;
        }
        for (const grupo of estado.grupos) {
          if (this.grupoCorrespondeAoFiltro(grupo, textoFiltro)) {
            ids.add(grupo.id);
          }
        }
      }
    }
    return ids;
  }

  private reunioesPresenciaisComCoordenadas(): Reuniao[] {
    const idsVisiveis = this.grupoIdsVisiveisAposFiltro();
    return this.reunioes().filter(
      (r) =>
        r.categoria === 'presencial' &&
        !!r.grupoIntergrupoId &&
        (idsVisiveis === null || idsVisiveis.has(r.grupoIntergrupoId)),
    );
  }

  private atualizarDadosMapa(): void {
    if (!this.mapa || !this.mapa.getSource(FONTE_REUNIOES)) {
      return;
    }

    const featureCollection = montarFeatureCollection(this.reunioesPresenciaisComCoordenadas());
    (this.mapa.getSource(FONTE_REUNIOES) as GeoJSONSource).setData(featureCollection);
    this.centralizarMapa(featureCollection);
  }

  private centralizarMapa(featureCollection?: FeatureCollection<Point, PropriedadesPontoReuniao>): void {
    if (!this.mapa || !this.maplibregl) {
      return;
    }

    const features = featureCollection ?? montarFeatureCollection(this.reunioesPresenciaisComCoordenadas());

    if (features.features.length === 0) {
      this.mapa.setCenter(CENTRO_PADRAO);
      this.mapa.setZoom(ZOOM_PADRAO);
      return;
    }

    if (features.features.length === 1) {
      const [longitude, latitude] = features.features[0].geometry.coordinates;
      this.mapa.setCenter([longitude, latitude]);
      this.mapa.setZoom(ZOOM_MAXIMO_BOUNDS);
      return;
    }

    const bounds = new this.maplibregl.LngLatBounds();
    for (const feature of features.features) {
      bounds.extend(feature.geometry.coordinates as LngLatLike);
    }
    this.mapa.fitBounds(bounds, {
      padding: PADDING_BOUNDS_PX,
      minZoom: ZOOM_MINIMO_BOUNDS,
      maxZoom: ZOOM_MAXIMO_BOUNDS,
    });
  }

  private centralizarMapaNoUsuario(coords: { latitude: number; longitude: number }): void {
    if (!this.mapa) {
      return;
    }
    this.mapa.setCenter([coords.longitude, coords.latitude]);
    this.mapa.setZoom(ZOOM_REGIAO_USUARIO);
  }

  private atualizarMarcadorUsuario(coords: { latitude: number; longitude: number }): void {
    if (!this.mapa || !this.mapa.getSource(FONTE_USUARIO)) {
      return;
    }
    (this.mapa.getSource(FONTE_USUARIO) as GeoJSONSource).setData(montarFeatureUsuario(coords));
  }

  private async carregarMapLibreECriarMapa(container: HTMLDivElement): Promise<void> {
    if (!this.maplibregl) {
      const mod = await import('maplibre-gl');
      this.maplibregl = (mod as unknown as { default?: typeof import('maplibre-gl') }).default ?? mod;
      configurarMapLibreWorker(this.maplibregl);
    }
    if (this.mapaContainerAtual !== container) {
      return;
    }
    this.criarMapa(container);
  }

  private criarMapa(container: HTMLDivElement): void {
    if (!this.maplibregl) {
      return;
    }
    const maplibregl = this.maplibregl;

    this.destruirMapa();

    this.mapa = new maplibregl.Map({
      container,
      style: ESTILO_OSM,
      center: CENTRO_PADRAO,
      zoom: ZOOM_PADRAO,
      attributionControl: { compact: true },
    });

    this.mapa.addControl(new maplibregl.NavigationControl({ showCompass: false }));

    this.mapa.on('load', () => {
      if (!this.mapa) {
        return;
      }

      this.mapa.resize();

      const featureCollection = montarFeatureCollection(this.reunioesPresenciaisComCoordenadas());

      this.mapa.addSource(FONTE_REUNIOES, {
        type: 'geojson',
        data: featureCollection,
      });

      this.mapa.addLayer({
        id: CAMADA_PONTOS_REUNIOES,
        type: 'circle',
        source: FONTE_REUNIOES,
        paint: {
          'circle-radius': 7,
          'circle-color': corPadraoJa(),
          'circle-stroke-width': 2,
          'circle-stroke-color': corBranca(),
        },
      });

      this.mapa.on('click', CAMADA_PONTOS_REUNIOES, (e) => {
        const feature = e.features?.[0];
        if (!feature || feature.geometry.type !== 'Point') {
          return;
        }

        const propriedades = feature.properties as PropriedadesPontoReuniao;
        const reuniao = this.reunioesPresenciaisComCoordenadas().find((r) => r.id === propriedades.id);
        if (reuniao) {
          this.abrirDetalhes(reuniao);
        }
      });

      this.mapa.on('mouseenter', CAMADA_PONTOS_REUNIOES, () => {
        if (this.mapa) {
          this.mapa.getCanvas().style.cursor = 'pointer';
        }
      });

      this.mapa.on('mouseleave', CAMADA_PONTOS_REUNIOES, () => {
        if (this.mapa) {
          this.mapa.getCanvas().style.cursor = '';
        }
      });

      const userCoords = this.userCoords();

      this.mapa.addSource(FONTE_USUARIO, {
        type: 'geojson',
        data: userCoords ? montarFeatureUsuario(userCoords) : { type: 'FeatureCollection', features: [] },
      });

      this.mapa.addLayer({
        id: CAMADA_PONTO_USUARIO,
        type: 'circle',
        source: FONTE_USUARIO,
        paint: {
          'circle-radius': 9,
          'circle-color': corSucesso(),
          'circle-stroke-width': 3,
          'circle-stroke-color': corBranca(),
        },
      });

      const popupUsuario = new maplibregl.Popup({ closeButton: false, closeOnClick: false, offset: 14 })
        .setText('Você (localização aproximada)');

      this.mapa.on('mouseenter', CAMADA_PONTO_USUARIO, (e) => {
        if (!this.mapa) {
          return;
        }
        this.mapa.getCanvas().style.cursor = 'pointer';
        const feature = e.features?.[0];
        if (feature && feature.geometry.type === 'Point') {
          popupUsuario.setLngLat(feature.geometry.coordinates as LngLatLike).addTo(this.mapa);
        }
      });

      this.mapa.on('mouseleave', CAMADA_PONTO_USUARIO, () => {
        if (this.mapa) {
          this.mapa.getCanvas().style.cursor = '';
        }
        popupUsuario.remove();
      });

      this.mapa.resize();

      if (userCoords) {
        this.centralizarMapaNoUsuario(userCoords);
      } else {
        this.centralizarMapa(featureCollection);
      }
    });

    this.resizeObserver = new ResizeObserver(() => {
      this.mapa?.resize();
    });
    this.resizeObserver.observe(container);
  }

  private destruirMapa(): void {
    this.resizeObserver?.disconnect();
    this.resizeObserver = undefined;
    this.mapa?.remove();
    this.mapa = undefined;
  }
}
