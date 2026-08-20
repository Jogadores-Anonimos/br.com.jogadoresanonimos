import {
  Component,
  ElementRef,
  HostListener,
  OnDestroy,
  OnInit,
  PLATFORM_ID,
  ViewChild,
  computed,
  inject,
  signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { isPlatformBrowser } from '@angular/common';
import type {
  GeoJSONSource,
  LngLatLike,
  Map as MapLibreMap,
  StyleSpecification,
} from 'maplibre-gl';
import type { Feature, FeatureCollection, Point } from 'geojson';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { ReuniaoService } from '../../services/reuniao/reuniao';
import { CategoriaReuniao, Reuniao } from '../../services/reuniao/reuniao.model';
import { montarLinkWhatsapp } from '../../config/whatsapp.config';
import { configurarMapLibreWorker } from '../../config/maplibre-worker.config';

type DiaSemana = 'Segunda' | 'Terça' | 'Quarta' | 'Quinta' | 'Sexta' | 'Sábado' | 'Domingo';

interface FiltroCategoria {
  valor: CategoriaReuniao;
  titulo: string;
  descricao: string;
}

interface GrupoDiaVirtual {
  dia: DiaSemana | 'Sem dia definido';
  reunioes: Reuniao[];
}

interface PropriedadesPontoReuniao {
  id: string;
  nome: string;
  dia: string;
  horario: string;
  local: string;
}

const RAIO_TERRA_KM = 6371;

const CENTRO_PADRAO: LngLatLike = [-46.62, -23.55];
const ZOOM_PADRAO = 4;
const ZOOM_REGIAO_USUARIO = 10;
const ZOOM_MINIMO_BOUNDS = 11;
const ZOOM_MAXIMO_BOUNDS = 13;
const PADDING_BOUNDS_PX = 48;
const ITENS_POR_PAGINA = 10;

const WHATSAPP_PLACEHOLDER = '5599999999999';
const DEBOUNCE_FILTRO_TEXTO_MS = 200;

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

  readonly diasSemana: DiaSemana[] = [
    'Segunda',
    'Terça',
    'Quarta',
    'Quinta',
    'Sexta',
    'Sábado',
    'Domingo',
  ];

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
        this.atualizarDadosMapa();
      } else {
        this.erroCarregamentoReunioes = envelope.error?.message ?? 'Não foi possível carregar as reuniões agora.';
      }
    });
  }

  ngOnDestroy(): void {
    clearTimeout(this.debounceFiltroTextoId);
    this.destruirMapa();
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
    this.atualizarDadosMapa();
  }

  aplicarFiltroCidade(valor: string | 'todos'): void {
    this.filtroCidade.set(valor);
    this.itensVisiveis.set(ITENS_POR_PAGINA);
    this.atualizarDadosMapa();
  }

  aplicarFiltroTexto(valor: string): void {
    this.filtroTexto.set(valor);
    this.itensVisiveis.set(ITENS_POR_PAGINA);
    clearTimeout(this.debounceFiltroTextoId);
    this.debounceFiltroTextoId = setTimeout(() => {
      this.filtroTextoDebounced.set(valor);
      this.atualizarDadosMapa();
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
    return montarLinkWhatsapp(reuniao.whatsapp || WHATSAPP_PLACEHOLDER);
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
          r.descricao.toLowerCase().includes(textoFiltro),
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

  private comparaHorario(a: Reuniao, b: Reuniao): number {
    return a.horario.localeCompare(b.horario);
  }

  readonly reunioesVirtuaisAgrupadas = computed<GrupoDiaVirtual[]>(() => {
    if (this.categoriaSelecionada() !== 'virtual') {
      return [];
    }

    const grupos: GrupoDiaVirtual[] = [];
    for (const dia of this.diasSemana) {
      const reunioesDoDia = this.reunioesFiltradas()
        .filter((r) => r.diaSemana === dia)
        .sort((a, b) => this.comparaHorario(a, b));
      if (reunioesDoDia.length > 0) {
        grupos.push({ dia, reunioes: reunioesDoDia });
      }
    }

    const semDiaDefinido = this.reunioesFiltradas()
      .filter((r) => !r.diaSemana)
      .sort((a, b) => this.comparaHorario(a, b));
    if (semDiaDefinido.length > 0) {
      grupos.push({ dia: 'Sem dia definido', reunioes: semDiaDefinido });
    }

    return grupos;
  });

  private atualizarDadosMapa(): void {
    if (!this.mapa || !this.mapa.getSource(FONTE_REUNIOES)) {
      return;
    }

    const featureCollection = montarFeatureCollection(this.reunioesFiltradas());
    (this.mapa.getSource(FONTE_REUNIOES) as GeoJSONSource).setData(featureCollection);
    this.centralizarMapa(featureCollection);
  }

  private centralizarMapa(featureCollection?: FeatureCollection<Point, PropriedadesPontoReuniao>): void {
    if (!this.mapa || !this.maplibregl) {
      return;
    }

    const features = featureCollection ?? montarFeatureCollection(this.reunioesFiltradas());

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

      const featureCollection = montarFeatureCollection(this.reunioesFiltradas());

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
        const reuniao = this.reunioesFiltradas().find((r) => r.id === propriedades.id);
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
