import {
  Component,
  ElementRef,
  HostListener,
  NgZone,
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
  LngLatLike,
  Map as MapLibreMap,
  Marker as MapLibreMarker,
  StyleSpecification,
} from 'maplibre-gl';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { ReuniaoService } from '../../services/reuniao/reuniao';
import { CategoriaReuniao, Reuniao } from '../../services/reuniao/reuniao.model';
import { montarLinkWhatsapp } from '../../config/whatsapp.config';

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

const RAIO_TERRA_KM = 6371;

const CENTRO_PADRAO: LngLatLike = [-46.62, -23.55];
const ZOOM_PADRAO = 12;
const ZOOM_PADRAO_MARCADOR_UNICO = 13;
const ZOOM_MAXIMO_BOUNDS = 13;
const PADDING_BOUNDS_PX = 56;
const ITENS_POR_PAGINA = 10;

const WHATSAPP_PLACEHOLDER = '5599999999999';
const DEBOUNCE_FILTRO_TEXTO_MS = 200;

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

function criarElementoMarcadorGrupo(): HTMLDivElement {
  const elemento = document.createElement('div');
  elemento.className = 'mapa-marcador-grupo';
  const imagem = document.createElement('img');
  imagem.src = '/images/header/logo-jogadores-anonimos-monograma.png';
  imagem.alt = '';
  elemento.appendChild(imagem);
  return elemento;
}

function criarElementoMarcadorUsuario(): HTMLDivElement {
  const elemento = document.createElement('div');
  elemento.className = 'mapa-marcador-voce';
  return elemento;
}

function criarElementoTooltipUsuario(): HTMLDivElement {
  const elemento = document.createElement('div');
  elemento.className = 'mapa-tooltip-voce';
  elemento.textContent = 'Você';
  return elemento;
}

function criarElementoPopupReuniao(reuniao: Reuniao): HTMLDivElement {
  const elemento = document.createElement('div');
  elemento.className = 'mapa-popup-reuniao';

  const titulo = document.createElement('strong');
  titulo.textContent = reuniao.nome;
  elemento.appendChild(titulo);

  const dia = document.createElement('p');
  dia.textContent = `${reuniao.dia} · ${reuniao.horario}`;
  elemento.appendChild(dia);

  const local = document.createElement('p');
  local.textContent = reuniao.local;
  elemento.appendChild(local);

  return elemento;
}

@Component({
  selector: 'app-reunioes',
  standalone: true,
  imports: [FormsModule, ResourceLayout],
  templateUrl: './reunioes.html',
  styleUrl: './reunioes.css',
})
export class Reunioes implements OnInit, OnDestroy {
  private readonly ngZone = inject(NgZone);
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
  readonly filtroBairro = signal<string | 'todos'>('todos');
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
  private marcadoresGrupo: MapLibreMarker[] = [];
  private marcadorUsuario?: MapLibreMarker;
  private resizeObserver?: ResizeObserver;
  private mapaInitToken = 0;

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
        this.atualizarMarcadores();
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
    this.filtroBairro.set('todos');
    this.filtroTexto.set('');
    this.filtroTextoDebounced.set('');
    clearTimeout(this.debounceFiltroTextoId);
    this.itensVisiveis.set(ITENS_POR_PAGINA);
    this.fecharDetalhes();
  }

  aplicarFiltroEstado(valor: string | 'todos'): void {
    this.filtroEstado.set(valor);
    this.filtroCidade.set('todos');
    this.filtroBairro.set('todos');
    this.itensVisiveis.set(ITENS_POR_PAGINA);
    this.atualizarMarcadores();
  }

  aplicarFiltroCidade(valor: string | 'todos'): void {
    this.filtroCidade.set(valor);
    this.filtroBairro.set('todos');
    this.itensVisiveis.set(ITENS_POR_PAGINA);
    this.atualizarMarcadores();
  }

  aplicarFiltroBairro(valor: string | 'todos'): void {
    this.filtroBairro.set(valor);
    this.itensVisiveis.set(ITENS_POR_PAGINA);
    this.atualizarMarcadores();
  }

  aplicarFiltroTexto(valor: string): void {
    this.filtroTexto.set(valor);
    this.itensVisiveis.set(ITENS_POR_PAGINA);
    clearTimeout(this.debounceFiltroTextoId);
    this.debounceFiltroTextoId = setTimeout(() => {
      this.filtroTextoDebounced.set(valor);
      this.atualizarMarcadores();
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
        this.userCoords.set({
          latitude: posicao.coords.latitude,
          longitude: posicao.coords.longitude,
        });
        this.localizacaoPermitida.set(true);
        this.localizacaoCarregando.set(false);
        this.garantirMarcadorUsuario();
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

  readonly bairrosDisponiveis = computed<string[]>(() => {
    const filtroEstado = this.filtroEstado();
    const filtroCidade = this.filtroCidade();
    const valores = new Set<string>();
    for (const reuniao of this.reunioesPresenciais()) {
      if (!reuniao.bairro) {
        continue;
      }
      if (filtroEstado !== 'todos' && reuniao.estado !== filtroEstado) {
        continue;
      }
      if (filtroCidade !== 'todos' && reuniao.cidade !== filtroCidade) {
        continue;
      }
      valores.add(reuniao.bairro);
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
      const filtroBairro = this.filtroBairro();

      if (filtroEstado !== 'todos') {
        resultado = resultado.filter((r) => r.estado === filtroEstado);
      }
      if (filtroCidade !== 'todos') {
        resultado = resultado.filter((r) => r.cidade === filtroCidade);
      }
      if (filtroBairro !== 'todos') {
        resultado = resultado.filter((r) => r.bairro === filtroBairro);
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

  atualizarMarcadores(opcoes?: { ajustarBoundsAoFinal?: boolean }): void {
    if (!this.mapa || !this.maplibregl) {
      return;
    }

    this.marcadoresGrupo.forEach((marcador) => marcador.remove());
    this.marcadoresGrupo = [];

    for (const reuniao of this.reunioesFiltradas()) {
      if (reuniao.latitude === null || reuniao.longitude === null) {
        continue;
      }

      const popup = new this.maplibregl.Popup({ offset: 24, closeButton: false }).setDOMContent(
        criarElementoPopupReuniao(reuniao),
      );

      const marcador = new this.maplibregl.Marker({
        element: criarElementoMarcadorGrupo(),
        anchor: 'bottom',
      })
        .setLngLat([reuniao.longitude, reuniao.latitude])
        .setPopup(popup);

      const elementoMarcador = marcador.getElement();
      elementoMarcador.setAttribute('role', 'button');
      elementoMarcador.setAttribute('aria-label', reuniao.nome);
      elementoMarcador.addEventListener('click', () => {
        this.ngZone.run(() => this.abrirDetalhes(reuniao));
      });

      marcador.addTo(this.mapa);
      this.marcadoresGrupo.push(marcador);
    }

    if (opcoes?.ajustarBoundsAoFinal ?? true) {
      this.ajustarBounds();
    }
  }

  private ajustarBounds(): void {
    if (!this.mapa || !this.maplibregl) {
      return;
    }

    const pontos: LngLatLike[] = this.marcadoresGrupo.map((marcador) => marcador.getLngLat());

    if (pontos.length === 0) {
      this.mapa.setCenter(CENTRO_PADRAO);
      this.mapa.setZoom(ZOOM_PADRAO);
      return;
    }

    if (pontos.length === 1) {
      this.mapa.setCenter(pontos[0]);
      this.mapa.setZoom(ZOOM_PADRAO_MARCADOR_UNICO);
      return;
    }

    const bounds = new this.maplibregl.LngLatBounds();
    pontos.forEach((ponto) => bounds.extend(ponto));
    this.mapa.fitBounds(bounds, { padding: PADDING_BOUNDS_PX, maxZoom: ZOOM_MAXIMO_BOUNDS });
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
    const token = ++this.mapaInitToken;
    if (!this.maplibregl) {
      const mod = await import('maplibre-gl');
      this.maplibregl = (mod as unknown as { default?: typeof import('maplibre-gl') }).default ?? mod;
    }
    if (token !== this.mapaInitToken) {
      return;
    }
    this.criarMapa(container);
  }

  private criarMapa(container: HTMLDivElement): void {
    if (!this.maplibregl) {
      return;
    }

    this.destruirMapa();

    this.mapa = new this.maplibregl.Map({
      container,
      style: ESTILO_OSM,
      center: CENTRO_PADRAO,
      zoom: ZOOM_PADRAO,
      scrollZoom: false,
      attributionControl: { compact: true },
    });

    this.mapa.addControl(new this.maplibregl.NavigationControl({ showCompass: false }));

    container.addEventListener('click', () => this.mapa?.scrollZoom.enable());
    container.addEventListener('focus', () => this.mapa?.scrollZoom.enable());
    container.addEventListener('mouseleave', () => this.mapa?.scrollZoom.disable());
    container.addEventListener('blur', () => this.mapa?.scrollZoom.disable());

    this.mapa.on('load', () => {
      this.mapa?.resize();
      this.atualizarMarcadores({ ajustarBoundsAoFinal: false });
      this.garantirMarcadorUsuario();
      this.ajustarBounds();
    });

    this.resizeObserver = new ResizeObserver(() => {
      this.mapa?.resize();
    });
    this.resizeObserver.observe(container);

    setTimeout(() => this.mapa?.resize(), 200);
  }

  private destruirMapa(): void {
    this.mapaInitToken++;
    this.resizeObserver?.disconnect();
    this.resizeObserver = undefined;
    this.marcadoresGrupo.forEach((marcador) => marcador.remove());
    this.marcadoresGrupo = [];
    this.marcadorUsuario?.remove();
    this.marcadorUsuario = undefined;
    this.mapa?.remove();
    this.mapa = undefined;
  }

  private garantirMarcadorUsuario(): void {
    const userCoords = this.userCoords();
    if (!this.mapa || !this.maplibregl || !userCoords) {
      return;
    }

    this.marcadorUsuario?.remove();

    const elementoMarcadorUsuario = criarElementoMarcadorUsuario();
    const containerTooltip = document.createElement('div');
    containerTooltip.className = 'mapa-marcador-voce-wrapper';
    containerTooltip.appendChild(criarElementoTooltipUsuario());
    containerTooltip.appendChild(elementoMarcadorUsuario);

    this.marcadorUsuario = new this.maplibregl.Marker({
      element: containerTooltip,
      anchor: 'center',
    })
      .setLngLat([userCoords.longitude, userCoords.latitude])
      .addTo(this.mapa);
  }
}
