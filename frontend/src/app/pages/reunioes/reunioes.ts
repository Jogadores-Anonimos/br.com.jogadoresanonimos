import {
  ApplicationRef,
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
import type * as L from 'leaflet';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { ReuniaoService } from '../../services/reuniao/reuniao';
import { CategoriaReuniao, Reuniao } from '../../services/reuniao/reuniao.model';
import { montarLinkWhatsapp } from '../../config/whatsapp.config';

type DiaSemana = 'Segunda' | 'Terça' | 'Quarta' | 'Quinta' | 'Sexta' | 'Sábado' | 'Domingo';
type Periodo = 'Manhã' | 'Tarde' | 'Noite';
type FiltroDia = DiaSemana | 'todos' | 'hoje';

interface FiltroCategoria {
  valor: CategoriaReuniao;
  titulo: string;
  descricao: string;
}

const RAIO_TERRA_KM = 6371;

const CENTRO_PADRAO: L.LatLngTuple = [-23.55, -46.62];
const ZOOM_PADRAO = 12;
const ZOOM_LOCALIZACAO_USUARIO = 11;
const ZOOM_MAXIMO_LOCALIZACAO_USUARIO = 14;
const RAIO_VIZINHANCA_KM = 100;
const MAXIMO_REUNIOES_VIZINHANCA = 15;
const ITENS_POR_PAGINA = 10;

const WHATSAPP_PLACEHOLDER = '5599999999999';

let ICONE_GRUPO: L.DivIcon;
let ICONE_USUARIO: L.DivIcon;

function criarIcones(leaflet: typeof L): void {
  if (ICONE_GRUPO && ICONE_USUARIO) {
    return;
  }

  ICONE_GRUPO = leaflet.divIcon({
    className: 'mapa-marcador-grupo',
    html: '<img src="/images/header/logo-jogadores-anonimos-monograma.png" alt="" />',
    iconSize: [34, 34],
    iconAnchor: [17, 34],
  });

  ICONE_USUARIO = leaflet.divIcon({
    className: 'mapa-marcador-voce',
    html: '',
    iconSize: [16, 16],
    iconAnchor: [8, 8],
  });
}

@Component({
  selector: 'app-reunioes',
  standalone: true,
  imports: [FormsModule, ResourceLayout],
  templateUrl: './reunioes.html',
  styleUrl: './reunioes.css',
})
export class Reunioes implements OnInit, OnDestroy {
  private readonly appRef = inject(ApplicationRef);
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

  readonly periodos: Periodo[] = ['Manhã', 'Tarde', 'Noite'];

  readonly raiosKm: number[] = [5, 10, 20, 50];

  private readonly diasPorIndiceJs: DiaSemana[] = [
    'Domingo',
    'Segunda',
    'Terça',
    'Quarta',
    'Quinta',
    'Sexta',
    'Sábado',
  ];

  readonly reunioes = signal<Reuniao[]>([]);
  carregandoReunioes = true;
  erroCarregamentoReunioes = '';

  readonly categoriaSelecionada = signal<CategoriaReuniao | null>(null);

  readonly filtroDiaSemana = signal<FiltroDia>('todos');
  readonly filtroPeriodo = signal<Periodo | 'todos'>('todos');
  readonly filtroRaioKm = signal<number | 'todos'>('todos');

  readonly itensVisiveis = signal(ITENS_POR_PAGINA);

  private readonly userCoords = signal<{ latitude: number; longitude: number } | null>(null);

  localizacaoPermitida = signal(false);
  localizacaoIndisponivel = signal(false);
  localizacaoCarregando = signal(false);

  reuniaoDetalhe: Reuniao | null = null;

  private leaflet?: typeof L;
  private mapa?: L.Map;
  private marcadoresGrupo: L.Marker[] = [];
  private marcadorUsuario?: L.Marker;

  @ViewChild('mapaContainer')
  set mapaContainerRef(ref: ElementRef<HTMLDivElement> | undefined) {
    if (!this.isBrowser) {
      return;
    }
    if (ref) {
      this.carregarLeafletECriarMapa(ref.nativeElement);
    } else {
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
    this.destruirMapa();
  }

  selecionarCategoria(categoria: CategoriaReuniao): void {
    this.categoriaSelecionada.set(categoria);
    this.filtroDiaSemana.set('todos');
    this.filtroPeriodo.set('todos');
    this.filtroRaioKm.set('todos');
    this.itensVisiveis.set(ITENS_POR_PAGINA);
    this.fecharDetalhes();
  }

  aplicarFiltroDiaSemana(valor: FiltroDia): void {
    this.filtroDiaSemana.set(valor);
    this.itensVisiveis.set(ITENS_POR_PAGINA);
    this.atualizarMarcadores();
  }

  aplicarFiltroPeriodo(valor: Periodo | 'todos'): void {
    this.filtroPeriodo.set(valor);
    this.itensVisiveis.set(ITENS_POR_PAGINA);
    this.atualizarMarcadores();
  }

  aplicarFiltroRaioKm(valor: number | 'todos'): void {
    this.filtroRaioKm.set(valor);
    this.itensVisiveis.set(ITENS_POR_PAGINA);
    this.atualizarMarcadores();
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
        this.atualizarMapaComLocalizacao();
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

  private get diaSemanaAtual(): DiaSemana {
    return this.diasPorIndiceJs[new Date().getDay()];
  }

  readonly reunioesFiltradas = computed<Reuniao[]>(() => {
    const categoriaSelecionada = this.categoriaSelecionada();
    if (!categoriaSelecionada) {
      return [];
    }

    const userCoords = this.userCoords();
    let resultado = this.reunioes().filter((r) => r.categoria === categoriaSelecionada);

    if (categoriaSelecionada === 'presencial') {
      const filtroDiaSemana = this.filtroDiaSemana();
      const filtroPeriodo = this.filtroPeriodo();
      const filtroRaioKm = this.filtroRaioKm();

      if (filtroDiaSemana !== 'todos') {
        const diaAlvo = filtroDiaSemana === 'hoje' ? this.diaSemanaAtual : filtroDiaSemana;
        resultado = resultado.filter((r) => r.diaSemana === diaAlvo);
      }
      if (filtroPeriodo !== 'todos') {
        resultado = resultado.filter((r) => r.periodo === filtroPeriodo);
      }
      if (filtroRaioKm !== 'todos' && userCoords) {
        const raioMaximo = filtroRaioKm;
        resultado = resultado.filter((r) => (this.distanciaKm(r) ?? Infinity) <= raioMaximo);
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

  atualizarMarcadores(): void {
    if (!this.mapa || !this.leaflet) {
      return;
    }

    this.marcadoresGrupo.forEach((marcador) => marcador.remove());
    this.marcadoresGrupo = [];

    for (const reuniao of this.reunioesFiltradas()) {
      if (reuniao.latitude === null || reuniao.longitude === null) {
        continue;
      }

      const marcador = this.leaflet.marker([reuniao.latitude, reuniao.longitude], {
        icon: ICONE_GRUPO,
        title: reuniao.nome,
        alt: reuniao.nome,
        keyboard: false,
      });

      marcador.on('click', () => {
        this.abrirDetalhes(reuniao);
        this.appRef.tick();
      });

      marcador.addTo(this.mapa);
      this.marcadoresGrupo.push(marcador);
    }

    this.ajustarBounds();
  }

  private pontosVizinhancaUsuario(): L.LatLngExpression[] {
    const userCoords = this.userCoords();
    if (!userCoords) {
      return [];
    }

    return this.reunioes()
      .filter((r) => r.latitude !== null && r.longitude !== null)
      .map((r) => ({ reuniao: r, distancia: this.distanciaKm(r) ?? Infinity }))
      .filter((item) => item.distancia <= RAIO_VIZINHANCA_KM)
      .sort((a, b) => a.distancia - b.distancia)
      .slice(0, MAXIMO_REUNIOES_VIZINHANCA)
      .map((item) => [item.reuniao.latitude as number, item.reuniao.longitude as number]);
  }

  private ajustarBounds(): void {
    if (!this.mapa || !this.leaflet) {
      return;
    }

    const marcadorUsuario = this.marcadorUsuario;
    if (marcadorUsuario) {
      const raioKm = this.filtroRaioKm();
      if (raioKm === 'todos') {
        const pontosVizinhanca = this.pontosVizinhancaUsuario();
        if (pontosVizinhanca.length === 0) {
          this.mapa.setView(marcadorUsuario.getLatLng(), ZOOM_LOCALIZACAO_USUARIO);
          return;
        }

        this.mapa.fitBounds(
          this.leaflet.latLngBounds([marcadorUsuario.getLatLng(), ...pontosVizinhanca]),
          { padding: [32, 32], maxZoom: ZOOM_MAXIMO_LOCALIZACAO_USUARIO },
        );
        return;
      }

      const pontosProximos: L.LatLngExpression[] = this.marcadoresGrupo
        .filter((marcador) => marcadorUsuario.getLatLng().distanceTo(marcador.getLatLng()) <= raioKm * 1000)
        .map((marcador) => marcador.getLatLng());
      pontosProximos.push(marcadorUsuario.getLatLng());

      this.mapa.fitBounds(this.leaflet.latLngBounds(pontosProximos), {
        padding: [32, 32],
        maxZoom: 13,
      });
      return;
    }

    const pontos: L.LatLngExpression[] = this.marcadoresGrupo.map((marcador) =>
      marcador.getLatLng(),
    );

    if (pontos.length === 0) {
      this.mapa.setView(CENTRO_PADRAO, ZOOM_PADRAO);
      return;
    }

    if (pontos.length === 1) {
      this.mapa.setView(pontos[0], 13);
      return;
    }

    this.mapa.fitBounds(this.leaflet.latLngBounds(pontos), { padding: [32, 32], maxZoom: 13 });
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

  private async carregarLeafletECriarMapa(container: HTMLDivElement): Promise<void> {
    if (!this.leaflet) {
      const mod = await import('leaflet');
      this.leaflet = (mod as unknown as { default?: typeof L }).default ?? (mod as unknown as typeof L);
    }
    this.criarMapa(container);
  }

  private criarMapa(container: HTMLDivElement): void {
    if (!this.leaflet) {
      return;
    }

    this.destruirMapa();
    criarIcones(this.leaflet);

    const userCoords = this.userCoords();
    const centroInicial: L.LatLngTuple = userCoords
      ? [userCoords.latitude, userCoords.longitude]
      : CENTRO_PADRAO;
    const zoomInicial = userCoords ? ZOOM_LOCALIZACAO_USUARIO : ZOOM_PADRAO;

    this.mapa = this.leaflet.map(container, {
      center: centroInicial,
      zoom: zoomInicial,
      scrollWheelZoom: false,
    });

    this.leaflet
      .tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 18,
        attribution: '&copy; colaboradores do OpenStreetMap',
      })
      .addTo(this.mapa);

    container.addEventListener('click', () => this.mapa?.scrollWheelZoom.enable());
    container.addEventListener('focus', () => this.mapa?.scrollWheelZoom.enable());
    container.addEventListener('mouseleave', () => this.mapa?.scrollWheelZoom.disable());
    container.addEventListener('blur', () => this.mapa?.scrollWheelZoom.disable());

    this.atualizarMarcadores();

    setTimeout(() => this.mapa?.invalidateSize(), 0);

    if (this.userCoords()) {
      this.atualizarMapaComLocalizacao();
    }
  }

  private destruirMapa(): void {
    this.marcadoresGrupo.forEach((marcador) => marcador.remove());
    this.marcadoresGrupo = [];
    this.marcadorUsuario?.remove();
    this.marcadorUsuario = undefined;
    this.mapa?.remove();
    this.mapa = undefined;
  }

  private atualizarMapaComLocalizacao(): void {
    const userCoords = this.userCoords();
    if (!this.mapa || !this.leaflet || !userCoords) {
      return;
    }

    this.marcadorUsuario?.remove();
    this.marcadorUsuario = this.leaflet
      .marker([userCoords.latitude, userCoords.longitude], {
        icon: ICONE_USUARIO,
        title: 'Você',
        alt: 'Você',
        keyboard: false,
      })
      .addTo(this.mapa);

    this.marcadorUsuario
      .bindTooltip('Você', {
        permanent: true,
        direction: 'top',
        offset: [0, -4],
        className: 'mapa-tooltip-voce',
      })
      .openTooltip();

    this.ajustarBounds();
  }
}
