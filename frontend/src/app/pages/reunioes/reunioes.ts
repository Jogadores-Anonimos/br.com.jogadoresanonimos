import {
  ApplicationRef,
  Component,
  ElementRef,
  HostListener,
  OnDestroy,
  OnInit,
  ViewChild,
  inject,
} from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import * as L from 'leaflet';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';

type CategoriaReuniao = 'presencial' | 'virtual';
type DiaSemana = 'Segunda' | 'Terça' | 'Quarta' | 'Quinta' | 'Sexta' | 'Sábado' | 'Domingo';
type Periodo = 'Manhã' | 'Tarde' | 'Noite';
type FiltroDia = DiaSemana | 'todos' | 'hoje';

interface FiltroCategoria {
  valor: CategoriaReuniao;
  titulo: string;
  descricao: string;
}

interface Reuniao {
  nome: string;
  categoria: CategoriaReuniao;
  descricao: string;
  dia: string;
  horario: string;
  local: string;
  whatsapp?: string;
  diaSemana?: DiaSemana;
  periodo?: Periodo;
  latitude?: number;
  longitude?: number;
}

const RAIO_TERRA_KM = 6371;

const CENTRO_PADRAO: L.LatLngTuple = [-23.55, -46.62];

const WHATSAPP_PLACEHOLDER = '5599999999999';

const ICONE_GRUPO = L.divIcon({
  className: 'mapa-marcador-grupo',
  html: '<img src="/images/header/logo-jogadores-anonimos-monograma.png" alt="" />',
  iconSize: [34, 34],
  iconAnchor: [17, 34],
});

const ICONE_USUARIO = L.divIcon({
  className: 'mapa-marcador-voce',
  html: '',
  iconSize: [16, 16],
  iconAnchor: [8, 8],
});

@Component({
  selector: 'app-reunioes',
  standalone: true,
  imports: [RouterLink, FormsModule, ResourceLayout],
  templateUrl: './reunioes.html',
  styleUrl: './reunioes.css',
})
export class Reunioes implements OnInit, OnDestroy {
  private readonly appRef = inject(ApplicationRef);

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

  readonly reunioes: Reuniao[] = [
    {
      nome: 'Grupo [a definir] — Centro',
      categoria: 'presencial',
      descricao: 'Grupo aberto, ambiente acolhedor, para quem está começando a se recuperar.',
      dia: 'Segunda-feira',
      horario: '19h30',
      local: '[endereço a definir], [cidade a definir]',
      whatsapp: WHATSAPP_PLACEHOLDER,
      diaSemana: 'Segunda',
      periodo: 'Noite',
      latitude: -23.5505,
      longitude: -46.6333,
    },
    {
      nome: 'Grupo [a definir] — Zona Norte',
      categoria: 'presencial',
      descricao: 'Reuniões mais curtas, focadas em quem tem pouca disponibilidade durante a semana.',
      dia: 'Quarta-feira',
      horario: '14h00',
      local: '[endereço a definir], [cidade a definir]',
      whatsapp: WHATSAPP_PLACEHOLDER,
      diaSemana: 'Quarta',
      periodo: 'Tarde',
      latitude: -23.5089,
      longitude: -46.6249,
    },
    {
      nome: 'Grupo [a definir] — Zona Sul',
      categoria: 'presencial',
      descricao: 'Encontro de fim de semana, com espaço pra novos membros contarem sua história.',
      dia: 'Sábado',
      horario: '09h00',
      local: '[endereço a definir], [cidade a definir]',
      whatsapp: WHATSAPP_PLACEHOLDER,
      diaSemana: 'Sábado',
      periodo: 'Manhã',
      latitude: -23.6206,
      longitude: -46.6988,
    },
    {
      nome: 'Grupo [a definir] — Zona Leste',
      categoria: 'presencial',
      descricao: 'Grupo tradicional da região, reuniões seguindo os 12 Passos da JA.',
      dia: 'Quinta-feira',
      horario: '19h00',
      local: '[endereço a definir], [cidade a definir]',
      whatsapp: WHATSAPP_PLACEHOLDER,
      diaSemana: 'Quinta',
      periodo: 'Noite',
      latitude: -23.5445,
      longitude: -46.4732,
    },
    {
      nome: 'Grupo [a definir] — Campinas',
      categoria: 'presencial',
      descricao:
        'Grupo da região de Campinas, mais distante dos demais mocks — bom exemplo pra conferir se o mapa ajusta o zoom certinho.',
      dia: 'Terça-feira',
      horario: '20h00',
      local: '[endereço a definir], Campinas - SP',
      whatsapp: WHATSAPP_PLACEHOLDER,
      diaSemana: 'Terça',
      periodo: 'Noite',
      latitude: -22.9099,
      longitude: -47.0626,
    },
    {
      nome: 'Grupo [a definir] — Encontro da Noite',
      categoria: 'virtual',
      descricao: 'Reunião por videochamada, sem precisar sair de casa.',
      dia: 'Segunda-feira',
      horario: '20h00',
      local: 'Link de acesso [a definir]',
      whatsapp: WHATSAPP_PLACEHOLDER,
    },
    {
      nome: 'Grupo [a definir] — Roda de Apoio',
      categoria: 'virtual',
      descricao: 'Encontro virtual voltado a quem está começando a jornada de recuperação.',
      dia: 'Quarta-feira',
      horario: '19h00',
      local: 'Link de acesso [a definir]',
      whatsapp: WHATSAPP_PLACEHOLDER,
    },
    {
      nome: 'Grupo [a definir] — Fim de Semana',
      categoria: 'virtual',
      descricao: 'Reunião online de sábado, com espaço para novos membros compartilharem.',
      dia: 'Sábado',
      horario: '10h00',
      local: 'Link de acesso [a definir]',
      whatsapp: WHATSAPP_PLACEHOLDER,
    },
  ];

  categoriaSelecionada: CategoriaReuniao | null = null;

  filtroDiaSemana: FiltroDia = 'todos';
  filtroPeriodo: Periodo | 'todos' = 'todos';
  filtroRaioKm: number | 'todos' = 'todos';

  private userCoords: { latitude: number; longitude: number } | null = null;

  localizacaoPermitida = false;
  localizacaoIndisponivel = false;
  localizacaoCarregando = false;

  reuniaoDetalhe: Reuniao | null = null;
  reservaConfirmada = false;

  private mapa?: L.Map;
  private marcadoresGrupo: L.Marker[] = [];
  private marcadorUsuario?: L.Marker;

  @ViewChild('mapaContainer')
  set mapaContainerRef(ref: ElementRef<HTMLDivElement> | undefined) {
    if (ref) {
      this.criarMapa(ref.nativeElement);
    } else {
      this.destruirMapa();
    }
  }

  ngOnInit(): void {
    this.solicitarLocalizacao();
  }

  ngOnDestroy(): void {
    this.destruirMapa();
  }

  selecionarCategoria(categoria: CategoriaReuniao): void {
    this.categoriaSelecionada = categoria;
    this.filtroDiaSemana = 'todos';
    this.filtroPeriodo = 'todos';
    this.filtroRaioKm = 'todos';
    this.fecharDetalhes();
  }

  solicitarLocalizacao(): void {
    if (!('geolocation' in navigator)) {
      this.localizacaoIndisponivel = true;
      return;
    }

    this.localizacaoCarregando = true;
    this.localizacaoIndisponivel = false;

    navigator.geolocation.getCurrentPosition(
      (posicao) => {
        this.userCoords = {
          latitude: posicao.coords.latitude,
          longitude: posicao.coords.longitude,
        };
        this.localizacaoPermitida = true;
        this.localizacaoCarregando = false;
        this.atualizarMapaComLocalizacao();
        this.appRef.tick();
      },
      () => {
        this.localizacaoIndisponivel = true;
        this.localizacaoCarregando = false;
        this.appRef.tick();
      },
      { enableHighAccuracy: false, timeout: 8000 },
    );
  }

  distanciaKm(reuniao: Reuniao): number | null {
    if (!this.userCoords || reuniao.latitude === undefined || reuniao.longitude === undefined) {
      return null;
    }

    const dLat = this.paraRadianos(reuniao.latitude - this.userCoords.latitude);
    const dLon = this.paraRadianos(reuniao.longitude - this.userCoords.longitude);
    const lat1 = this.paraRadianos(this.userCoords.latitude);
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
    const digitos = reuniao.whatsapp?.replace(/\D/g, '') || WHATSAPP_PLACEHOLDER;
    return `https://wa.me/${digitos}`;
  }

  private get diaSemanaAtual(): DiaSemana {
    return this.diasPorIndiceJs[new Date().getDay()];
  }

  get reunioesFiltradas(): Reuniao[] {
    if (!this.categoriaSelecionada) {
      return [];
    }

    let resultado = this.reunioes.filter((r) => r.categoria === this.categoriaSelecionada);

    if (this.categoriaSelecionada === 'presencial') {
      if (this.filtroDiaSemana !== 'todos') {
        const diaAlvo = this.filtroDiaSemana === 'hoje' ? this.diaSemanaAtual : this.filtroDiaSemana;
        resultado = resultado.filter((r) => r.diaSemana === diaAlvo);
      }
      if (this.filtroPeriodo !== 'todos') {
        resultado = resultado.filter((r) => r.periodo === this.filtroPeriodo);
      }
      if (this.filtroRaioKm !== 'todos' && this.userCoords) {
        const raioMaximo = this.filtroRaioKm;
        resultado = resultado.filter((r) => (this.distanciaKm(r) ?? Infinity) <= raioMaximo);
      }

      if (this.userCoords) {
        resultado = [...resultado].sort(
          (a, b) => (this.distanciaKm(a) ?? Infinity) - (this.distanciaKm(b) ?? Infinity),
        );
      }
    }

    return resultado;
  }

  atualizarMarcadores(): void {
    if (!this.mapa) {
      return;
    }

    this.marcadoresGrupo.forEach((marcador) => marcador.remove());
    this.marcadoresGrupo = [];

    for (const reuniao of this.reunioesFiltradas) {
      if (reuniao.latitude === undefined || reuniao.longitude === undefined) {
        continue;
      }

      const marcador = L.marker([reuniao.latitude, reuniao.longitude], {
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

  private ajustarBounds(): void {
    if (!this.mapa) {
      return;
    }

    const pontos: L.LatLngExpression[] = this.marcadoresGrupo.map((marcador) =>
      marcador.getLatLng(),
    );
    if (this.marcadorUsuario) {
      pontos.push(this.marcadorUsuario.getLatLng());
    }

    if (pontos.length === 0) {
      this.mapa.setView(CENTRO_PADRAO, 12);
      return;
    }

    if (pontos.length === 1) {
      this.mapa.setView(pontos[0], 13);
      return;
    }

    this.mapa.fitBounds(L.latLngBounds(pontos), { padding: [32, 32], maxZoom: 13 });
  }

  abrirDetalhes(reuniao: Reuniao): void {
    this.reuniaoDetalhe = reuniao;
    this.reservaConfirmada = false;
  }

  fecharDetalhes(): void {
    this.reuniaoDetalhe = null;
    this.reservaConfirmada = false;
  }

  @HostListener('document:keydown.escape')
  onEscape(): void {
    if (this.reuniaoDetalhe) {
      this.fecharDetalhes();
    }
  }

  reservarLugarLocal(): void {
    this.reservaConfirmada = true;
  }

  private criarMapa(container: HTMLDivElement): void {
    this.destruirMapa();

    this.mapa = L.map(container, {
      center: CENTRO_PADRAO,
      zoom: 12,
      scrollWheelZoom: false,
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 18,
      attribution: '&copy; colaboradores do OpenStreetMap',
    }).addTo(this.mapa);

    container.addEventListener('click', () => this.mapa?.scrollWheelZoom.enable());
    container.addEventListener('focus', () => this.mapa?.scrollWheelZoom.enable());
    container.addEventListener('mouseleave', () => this.mapa?.scrollWheelZoom.disable());
    container.addEventListener('blur', () => this.mapa?.scrollWheelZoom.disable());

    this.atualizarMarcadores();

    setTimeout(() => this.mapa?.invalidateSize(), 0);

    if (this.userCoords) {
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
    if (!this.mapa || !this.userCoords) {
      return;
    }

    this.marcadorUsuario?.remove();
    this.marcadorUsuario = L.marker([this.userCoords.latitude, this.userCoords.longitude], {
      icon: ICONE_USUARIO,
      title: 'Você',
      alt: 'Você',
      keyboard: false,
    }).addTo(this.mapa);

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
