import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, ElementRef, OnInit, PLATFORM_ID, ViewChild, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import type { LngLatLike, Map as MapLibreMap, Marker as MapLibreMarker, StyleSpecification } from 'maplibre-gl';

import { SeletorHora12h } from '../../../components/seletor-hora-12h/seletor-hora-12h';
import { mapearErrosCampo, mensagemDeErro } from '../../../services/api-envelope';
import { EnderecoLookupService } from '../../../services/endereco-lookup/endereco-lookup';
import { ReuniaoService } from '../../../services/reuniao/reuniao';
import { compararHorarios } from '../../../services/reuniao/reuniao-horario';
import {
  AgendaItemReuniao,
  CategoriaReuniao,
  CriarReuniaoRequest,
  DiaSemanaAgendaReuniao,
  Reuniao,
} from '../../../services/reuniao/reuniao.model';
import { ReuniaoOnlineService } from '../../../services/reuniao-online/reuniao-online';
import { CriarReuniaoOnlineRequest, ReuniaoOnline } from '../../../services/reuniao-online/reuniao-online.model';
import { configurarMapLibreWorker } from '../../../config/maplibre-worker.config';
import { WHATSAPP_PHONE_NUMBER } from '../../../config/whatsapp.config';
import { Imagem } from '../../../services/imagem/imagem';
import { extrairYoutubeVideoId, paraYoutubeEmbedUrl } from '../../../services/mural-de-apoio/youtube.util';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

const CODIGO_AVISO_ENDERECO_LOCAL_DIVERGENTE = 'ENDERECO_LOCAL_DIVERGENTE';

type OrigemReuniao = 'presencial' | 'virtual';

interface ReuniaoListaItem {
  origem: OrigemReuniao;
  id: string;
  nome: string;
  descricao: string;
  local: string;
  resumoAgenda: string;
  agendaOrdenacao: Array<{ diaSemana: DiaSemanaAgendaReuniao; horarios: string[] }>;
  cep: string | null;
  bairro: string | null;
  cidade: string | null;
  estado: string | null;
  reuniao?: Reuniao;
  reuniaoOnline?: ReuniaoOnline;
}

interface FormularioReuniao {
  nome: string;
  categoria: CategoriaReuniao;
  descricao: string;
  agenda: AgendaFormularioItem[];
  local: string;
  whatsapp: string;
  latitude: string;
  longitude: string;
  cep: string;
  numero: string;
  logradouro: string;
  bairro: string;
  cidade: string;
  estado: string;
  videoUrl: string;
  panfletoImagemId: string;
}

interface AgendaFormularioItem {
  diaSemana: DiaSemanaAgendaReuniao;
  rotulo: string;
  selecionado: boolean;
  horarios: string[];
}

const DIAS_AGENDA: ReadonlyArray<{ diaSemana: DiaSemanaAgendaReuniao; rotulo: string }> = [
  { diaSemana: 'SEGUNDA', rotulo: 'Segunda' },
  { diaSemana: 'TERCA', rotulo: 'Terça' },
  { diaSemana: 'QUARTA', rotulo: 'Quarta' },
  { diaSemana: 'QUINTA', rotulo: 'Quinta' },
  { diaSemana: 'SEXTA', rotulo: 'Sexta' },
  { diaSemana: 'SABADO', rotulo: 'Sábado' },
  { diaSemana: 'DOMINGO', rotulo: 'Domingo' },
];

const ROTULO_DIA: Record<DiaSemanaAgendaReuniao, string> = Object.fromEntries(
  DIAS_AGENDA.map((dia) => [dia.diaSemana, dia.rotulo]),
) as Record<DiaSemanaAgendaReuniao, string>;

function agendaFormularioVazia(): AgendaFormularioItem[] {
  return DIAS_AGENDA.map((dia) => ({ ...dia, selecionado: false, horarios: [] }));
}

function formularioVazio(): FormularioReuniao {
  return {
    nome: '',
    categoria: 'presencial',
    descricao: '',
    agenda: agendaFormularioVazia(),
    local: '',
    whatsapp: '',
    latitude: '',
    longitude: '',
    cep: '',
    numero: '',
    logradouro: '',
    bairro: '',
    cidade: '',
    estado: '',
    videoUrl: '',
    panfletoImagemId: '',
  };
}

function periodoParaHorario(horario: string): 'Manhã' | 'Tarde' | 'Noite' {
  const hora = Number(horario.split(':')[0]);
  if (hora >= 5 && hora < 12) return 'Manhã';
  if (hora >= 12 && hora < 18) return 'Tarde';
  return 'Noite';
}

const CENTRO_PADRAO: LngLatLike = [-46.62, -23.55];
const ZOOM_PADRAO = 11;
const ZOOM_MARCADOR = 14;

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

const DIAS_SEMANA = DIAS_AGENDA.map((dia) => dia.rotulo);

const UFS_BRASIL: ReadonlyArray<{ sigla: string; nome: string }> = [
  { sigla: 'AC', nome: 'Acre' },
  { sigla: 'AL', nome: 'Alagoas' },
  { sigla: 'AP', nome: 'Amapá' },
  { sigla: 'AM', nome: 'Amazonas' },
  { sigla: 'BA', nome: 'Bahia' },
  { sigla: 'CE', nome: 'Ceará' },
  { sigla: 'DF', nome: 'Distrito Federal' },
  { sigla: 'ES', nome: 'Espírito Santo' },
  { sigla: 'GO', nome: 'Goiás' },
  { sigla: 'MA', nome: 'Maranhão' },
  { sigla: 'MT', nome: 'Mato Grosso' },
  { sigla: 'MS', nome: 'Mato Grosso do Sul' },
  { sigla: 'MG', nome: 'Minas Gerais' },
  { sigla: 'PA', nome: 'Pará' },
  { sigla: 'PB', nome: 'Paraíba' },
  { sigla: 'PR', nome: 'Paraná' },
  { sigla: 'PE', nome: 'Pernambuco' },
  { sigla: 'PI', nome: 'Piauí' },
  { sigla: 'RJ', nome: 'Rio de Janeiro' },
  { sigla: 'RN', nome: 'Rio Grande do Norte' },
  { sigla: 'RS', nome: 'Rio Grande do Sul' },
  { sigla: 'RO', nome: 'Rondônia' },
  { sigla: 'RR', nome: 'Roraima' },
  { sigla: 'SC', nome: 'Santa Catarina' },
  { sigla: 'SP', nome: 'São Paulo' },
  { sigla: 'SE', nome: 'Sergipe' },
  { sigla: 'TO', nome: 'Tocantins' },
];

const WHATSAPP_DIGITOS_REGEX = /^(?:55)?\d{10,11}$/;

function whatsappFormatoValido(valor: string): boolean {
  const digitos = valor.replace(/\D/g, '');
  return WHATSAPP_DIGITOS_REGEX.test(digitos);
}

const CONECTIVOS_MINUSCULOS = new Set(['de', 'da', 'do', 'das', 'dos', 'e', 'em', 'a', 'o', 'no', 'na']);

function paraTitleCase(valor: string): string {
  return valor
    .toLowerCase()
    .split(' ')
    .map((palavra, indice) => {
      if (!palavra) return palavra;
      if (indice > 0 && CONECTIVOS_MINUSCULOS.has(palavra)) return palavra;
      return palavra.charAt(0).toUpperCase() + palavra.slice(1);
    })
    .join(' ');
}

function normalizarTextoLivre(valor: string): string {
  const semEspacosExtras = valor.trim().replace(/\s+/g, ' ');
  if (!semEspacosExtras) return semEspacosExtras;
  const proporcaoMaiusculas =
    (semEspacosExtras.match(/[A-ZÀ-Ú]/g) ?? []).length / (semEspacosExtras.match(/[A-Za-zÀ-ÿ]/g) ?? ['']).length;
  if (proporcaoMaiusculas < 0.6) return semEspacosExtras;
  return paraTitleCase(semEspacosExtras);
}

function corPadraoJa(): string {
  return getComputedStyle(document.documentElement).getPropertyValue('--cor-padrao-ja').trim();
}

function formatarWhatsapp(numeroCru: string): string {
  const digitos = numeroCru.replace(/\D/g, '').replace(/^55/, '');
  const ddd = digitos.slice(0, 2);
  const restante = digitos.slice(2);
  const parteFinal = restante.length > 4 ? `${restante.slice(0, -4)}-${restante.slice(-4)}` : restante;
  return `(${ddd}) ${parteFinal}`;
}

@Component({
  selector: 'app-admin-reunioes',
  standalone: true,
  imports: [CommonModule, FormsModule, SeletorHora12h],
  templateUrl: './admin-reunioes.html',
  styleUrl: './admin-reunioes.css',
})
export class AdminReunioes implements OnInit {
  private readonly reuniaoService = inject(ReuniaoService);
  private readonly reuniaoOnlineService = inject(ReuniaoOnlineService);
  private readonly enderecoLookupService = inject(EnderecoLookupService);
  private readonly imagemService = inject(Imagem);
  private readonly sanitizer = inject(DomSanitizer);
  private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));

  readonly diasSemanaFiltro = DIAS_SEMANA;
  readonly ufsBrasil = UFS_BRASIL;
  readonly whatsappInstitucionalFormatado = formatarWhatsapp(WHATSAPP_PHONE_NUMBER);

  carregando = true;
  erroCarregamento = '';
  private readonly reunioes = signal<Reuniao[]>([]);
  private readonly reunioesOnline = signal<ReuniaoOnline[]>([]);
  conflitoVersao = false;
  avisoGeocodificacao = '';

  readonly filtroTexto = signal('');
  readonly filtroCategoria = signal<CategoriaReuniao | ''>('');
  readonly filtroDiaSemana = signal('');

  readonly filtroAtivo = computed(
    () => this.filtroTexto().trim() !== '' || this.filtroCategoria() !== '' || this.filtroDiaSemana() !== '',
  );

  readonly itensLista = computed<ReuniaoListaItem[]>(() => {
    const presenciais = this.reunioes().map((reuniao) => this.reuniaoParaItemLista(reuniao));
    const virtuais = this.reunioesOnline().map((reuniaoOnline) => this.reuniaoOnlineParaItemLista(reuniaoOnline));
    return [...presenciais, ...virtuais].sort((a, b) => a.nome.localeCompare(b.nome));
  });

  readonly itensFiltrados = computed(() => {
    const texto = this.filtroTexto().trim().toLowerCase();
    const digitosTexto = texto.replace(/\D/g, '');
    const categoria = this.filtroCategoria();
    const diaSemana = this.filtroDiaSemana();

    return this.itensLista().filter((item) => {
      const combinaTexto =
        texto === '' ||
        item.nome.toLowerCase().includes(texto) ||
        item.local.toLowerCase().includes(texto) ||
        (item.bairro ?? '').toLowerCase().includes(texto) ||
        (item.cidade ?? '').toLowerCase().includes(texto) ||
        (item.estado ?? '').toLowerCase().includes(texto) ||
        (digitosTexto !== '' && (item.cep ?? '').replace(/\D/g, '').includes(digitosTexto));
      const combinaCategoria = categoria === '' || item.origem === categoria;
      const combinaDiaSemana =
        diaSemana === '' || item.agendaOrdenacao.some((agendaItem) => ROTULO_DIA[agendaItem.diaSemana] === diaSemana);
      return combinaTexto && combinaCategoria && combinaDiaSemana;
    });
  });

  limparFiltros(): void {
    this.filtroTexto.set('');
    this.filtroCategoria.set('');
    this.filtroDiaSemana.set('');
  }

  modo: 'lista' | 'formulario' = 'lista';
  reuniaoEmEdicao: Reuniao | null = null;
  reuniaoOnlineEmEdicao: ReuniaoOnline | null = null;
  formulario: FormularioReuniao = formularioVazio();
  salvando = false;
  erroFormulario = '';
  errosCampo: Record<string, string> = {};
  avisoEnderecoLocalDivergente = false;

  buscandoCep = false;
  erroCep = '';

  panfletoImagemUrl: string | null = null;
  enviandoPanfleto = false;
  erroUploadPanfleto = '';

  confirmandoExclusao: string | null = null;
  excluindo = false;
  erroExclusao = '';

  preenchendoGeolocalizacao = false;
  mensagemGeolocalizacao = '';
  erroGeolocalizacao = '';

  private maplibregl?: typeof import('maplibre-gl');
  private mapaPreview?: MapLibreMap;
  private marcadorPreview?: MapLibreMarker;
  private resizeObserver?: ResizeObserver;
  private mapaInitToken = 0;
  private mapaPreviewContainerAtual?: HTMLDivElement;

  @ViewChild('formularioSecao') formularioSecaoRef?: ElementRef<HTMLElement>;
  @ViewChild('campoNome') campoNomeRef?: ElementRef<HTMLInputElement>;

  @ViewChild('mapaPreviewContainer')
  set mapaPreviewContainerRef(ref: ElementRef<HTMLDivElement> | undefined) {
    if (!this.isBrowser) {
      return;
    }
    if (ref) {
      if (this.mapaPreviewContainerAtual === ref.nativeElement) {
        return;
      }
      this.mapaPreviewContainerAtual = ref.nativeElement;
      this.carregarMapLibreECriarMapaPreview(ref.nativeElement);
    } else {
      this.mapaPreviewContainerAtual = undefined;
      this.destruirMapaPreview();
    }
  }

  ngOnInit(): void {
    this.carregarReunioes();
  }

  private carregarReunioes(): void {
    this.carregando = true;
    this.erroCarregamento = '';

    let presenciaisCarregadas = false;
    let virtuaisCarregadas = false;
    let erro = '';

    const finalizarSeCompleto = () => {
      if (presenciaisCarregadas && virtuaisCarregadas) {
        this.carregando = false;
        this.erroCarregamento = erro;
      }
    };

    this.reuniaoService.listarReunioesAdmin().subscribe((envelope) => {
      presenciaisCarregadas = true;
      if (envelope.success && envelope.data) {
        this.reunioes.set(envelope.data);
      } else {
        this.reunioes.set([]);
        erro = envelope.error?.message ?? 'Não foi possível carregar as reuniões.';
      }
      finalizarSeCompleto();
    });

    this.reuniaoOnlineService.listarReunioesOnlineAdmin().subscribe((envelope) => {
      virtuaisCarregadas = true;
      if (envelope.success && envelope.data) {
        this.reunioesOnline.set(envelope.data);
      } else {
        this.reunioesOnline.set([]);
        erro = erro || envelope.error?.message || 'Não foi possível carregar as reuniões online.';
      }
      finalizarSeCompleto();
    });
  }

  private reuniaoParaItemLista(reuniao: Reuniao): ReuniaoListaItem {
    return {
      origem: 'presencial',
      id: reuniao.id,
      nome: reuniao.nome,
      descricao: reuniao.descricao,
      local: reuniao.local,
      resumoAgenda: this.resumoAgendaDeItens(this.agendaAgrupada(this.agendaDaReuniao(reuniao))),
      agendaOrdenacao: this.agendaAgrupada(this.agendaDaReuniao(reuniao)),
      cep: reuniao.cep,
      bairro: reuniao.bairro,
      cidade: reuniao.cidade,
      estado: reuniao.estado,
      reuniao,
    };
  }

  private reuniaoOnlineParaItemLista(reuniaoOnline: ReuniaoOnline): ReuniaoListaItem {
    return {
      origem: 'virtual',
      id: reuniaoOnline.id,
      nome: reuniaoOnline.nome,
      descricao: reuniaoOnline.descricao ?? '',
      local: '',
      resumoAgenda: this.resumoAgendaDeItens(this.agendaAgrupada(reuniaoOnline.agenda)),
      agendaOrdenacao: this.agendaAgrupada(reuniaoOnline.agenda),
      cep: null,
      bairro: null,
      cidade: null,
      estado: null,
      reuniaoOnline,
    };
  }

  preencherGeolocalizacao(): void {
    if (this.preenchendoGeolocalizacao) {
      return;
    }

    this.preenchendoGeolocalizacao = true;
    this.mensagemGeolocalizacao = '';
    this.erroGeolocalizacao = '';

    this.reuniaoService.backfillGeolocalizacao().subscribe((envelope) => {
      this.preenchendoGeolocalizacao = false;

      if (envelope.success && envelope.data) {
        const { atualizadas, falhas, totalElegiveis } = envelope.data;
        this.mensagemGeolocalizacao =
          totalElegiveis === 0
            ? 'Nenhuma reunião pendente de geolocalização — todas já estão preenchidas.'
            : `${atualizadas} reunião(ões) atualizada(s), ${falhas} falha(s) de ${totalElegiveis} elegível(is).`;
      } else {
        this.erroGeolocalizacao = mensagemDeErro(envelope.error, 'Não foi possível preencher a geolocalização agora.');
      }
    });
  }

  abrirNovo(): void {
    this.reuniaoEmEdicao = null;
    this.reuniaoOnlineEmEdicao = null;
    this.formulario = formularioVazio();
    this.erroCep = '';
    this.erroFormulario = '';
    this.errosCampo = {};
    this.avisoEnderecoLocalDivergente = false;
    this.conflitoVersao = false;
    this.avisoGeocodificacao = '';
    this.panfletoImagemUrl = null;
    this.erroUploadPanfleto = '';
    this.modo = 'formulario';
    this.focarFormulario();
  }

  abrirEdicao(item: ReuniaoListaItem): void {
    if (item.origem === 'presencial' && item.reuniao) {
      const reuniao = item.reuniao;
      this.reuniaoEmEdicao = reuniao;
      this.reuniaoOnlineEmEdicao = null;
      this.formulario = {
        nome: reuniao.nome,
        categoria: 'presencial',
        descricao: reuniao.descricao,
        agenda: this.agendaParaFormulario(this.agendaDaReuniao(reuniao)),
        local: reuniao.local,
        whatsapp: reuniao.whatsapp ?? '',
        latitude: reuniao.latitude !== null ? String(reuniao.latitude) : '',
        longitude: reuniao.longitude !== null ? String(reuniao.longitude) : '',
        cep: reuniao.cep ?? '',
        numero: reuniao.numero ?? '',
        logradouro: reuniao.logradouro ?? '',
        bairro: reuniao.bairro ?? '',
        cidade: reuniao.cidade ?? '',
        estado: reuniao.estado ?? '',
        videoUrl: '',
        panfletoImagemId: '',
      };
      this.panfletoImagemUrl = null;
    } else if (item.origem === 'virtual' && item.reuniaoOnline) {
      const reuniaoOnline = item.reuniaoOnline;
      this.reuniaoOnlineEmEdicao = reuniaoOnline;
      this.reuniaoEmEdicao = null;
      this.formulario = {
        ...formularioVazio(),
        nome: reuniaoOnline.nome,
        categoria: 'virtual',
        descricao: reuniaoOnline.descricao ?? '',
        agenda: this.agendaParaFormulario(reuniaoOnline.agenda),
        videoUrl: reuniaoOnline.videoUrl ?? '',
        panfletoImagemId: reuniaoOnline.panfletoImagemId ?? '',
      };
      this.panfletoImagemUrl = reuniaoOnline.panfletoImagemUrl ?? null;
    } else {
      return;
    }
    this.erroCep = '';
    this.erroFormulario = '';
    this.errosCampo = {};
    this.avisoEnderecoLocalDivergente = false;
    this.conflitoVersao = false;
    this.avisoGeocodificacao = '';
    this.erroUploadPanfleto = '';
    this.modo = 'formulario';
    this.focarFormulario();
  }

  alternarCategoriaFormulario(categoria: CategoriaReuniao): void {
    if (this.reuniaoEmEdicao || this.reuniaoOnlineEmEdicao) {
      return;
    }
    this.formulario = { ...formularioVazio(), categoria };
    this.erroCep = '';
    this.erroFormulario = '';
    this.errosCampo = {};
    this.avisoEnderecoLocalDivergente = false;
    this.avisoGeocodificacao = '';
    this.panfletoImagemUrl = null;
    this.erroUploadPanfleto = '';
  }

  private focarFormulario(): void {
    if (!this.isBrowser) {
      return;
    }
    setTimeout(() => {
      const secao = this.formularioSecaoRef?.nativeElement;
      const reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
      secao?.scrollIntoView?.({ behavior: reducedMotion ? 'auto' : 'smooth', block: 'start' });
      this.campoNomeRef?.nativeElement.focus({ preventScroll: true });
    });
  }

  cancelar(): void {
    this.modo = 'lista';
    this.reuniaoEmEdicao = null;
    this.reuniaoOnlineEmEdicao = null;
    this.erroFormulario = '';
    this.errosCampo = {};
    this.avisoEnderecoLocalDivergente = false;
  }

  limparAvisoEnderecoLocalDivergente(): void {
    this.avisoEnderecoLocalDivergente = false;
  }

  buscarCep(): void {
    const cepLimpo = this.formulario.cep.replace(/\D/g, '');
    if (cepLimpo.length !== 8) {
      this.erroCep = 'CEP inválido — precisa ter 8 dígitos.';
      return;
    }

    this.buscandoCep = true;
    this.erroCep = '';
    this.limparCoordenadas('O marcador anterior foi removido enquanto o novo endereço é validado.');

    this.enderecoLookupService.buscarCep(cepLimpo).subscribe((endereco) => {
      if (!endereco || endereco.erro) {
        this.buscandoCep = false;
        this.erroCep = 'CEP não encontrado.';
        return;
      }

      const logradouroComNumero = [endereco.logradouro, this.formulario.numero.trim()].filter(Boolean).join(', ');
      const enderecoBase = [logradouroComNumero, endereco.bairro, endereco.localidade, endereco.uf]
        .filter(Boolean)
        .join(', ');
      this.formulario.local = enderecoBase || this.formulario.local;
      this.formulario.logradouro = endereco.logradouro ?? '';
      this.formulario.bairro = endereco.bairro ?? '';
      this.formulario.cidade = endereco.localidade ?? '';
      this.formulario.estado = endereco.uf ?? '';

      this.enderecoLookupService.geocodificar(enderecoBase).subscribe((resultados) => {
        this.buscandoCep = false;
        const primeiro = resultados[0];
        if (!primeiro) {
          this.erroCep = 'Endereço encontrado, mas não foi possível obter as coordenadas — preencha manualmente se precisar.';
          return;
        }
        this.formulario.latitude = primeiro.lat;
        this.formulario.longitude = primeiro.lon;
        this.avisoGeocodificacao = 'Localização atualizada pelo endereço informado.';
        this.atualizarMapaPreview();
      });
    });
  }

  onArquivoPanfletoSelecionado(event: Event): void {
    const input = event.target as HTMLInputElement;
    const arquivo = input.files && input.files.length > 0 ? input.files[0] : null;
    if (!arquivo) return;

    this.enviandoPanfleto = true;
    this.erroUploadPanfleto = '';

    this.imagemService
      .uploadImagem({ arquivo, alt: `Panfleto da reunião ${this.formulario.nome.trim() || 'online'}` })
      .subscribe((envelope) => {
        this.enviandoPanfleto = false;

        if (envelope.success && envelope.data) {
          this.formulario.panfletoImagemId = envelope.data.id;
          this.panfletoImagemUrl = envelope.data.url;
        } else {
          this.erroUploadPanfleto = envelope.error?.message ?? 'Não foi possível enviar o panfleto.';
        }

        input.value = '';
      });
  }

  removerPanfleto(): void {
    this.formulario.panfletoImagemId = '';
    this.panfletoImagemUrl = null;
    this.erroUploadPanfleto = '';
  }

  videoUrlInvalida(): boolean {
    const valor = this.formulario.videoUrl.trim();
    return valor.length > 0 && !extrairYoutubeVideoId(valor);
  }

  embedPreviewUrl(): SafeResourceUrl | null {
    const embedUrl = paraYoutubeEmbedUrl(this.formulario.videoUrl.trim());
    return embedUrl ? this.sanitizer.bypassSecurityTrustResourceUrl(embedUrl) : null;
  }

  validarWhatsapp(): void {
    const valor = this.formulario.whatsapp.trim();
    if (!valor) {
      delete this.errosCampo['whatsapp'];
      return;
    }
    if (!whatsappFormatoValido(valor)) {
      this.errosCampo['whatsapp'] = 'Número de WhatsApp inválido — use DDD + número, com ou sem o 55 do Brasil.';
      return;
    }
    delete this.errosCampo['whatsapp'];
  }

  limparCoordenadas(aviso = 'Endereço alterado. Busque o CEP novamente para atualizar o marcador.'): void {
    if (this.formulario.latitude || this.formulario.longitude) {
      this.formulario.latitude = '';
      this.formulario.longitude = '';
      this.avisoGeocodificacao = aviso;
      this.atualizarMapaPreview();
    }
  }

  alternarDiaAgenda(item: AgendaFormularioItem): void {
    item.horarios = item.selecionado ? (item.horarios.length ? item.horarios : ['19:00']) : [];
    delete this.errosCampo['agenda'];
    delete this.errosCampo[`agenda.${item.diaSemana}`];
  }

  adicionarHorario(item: AgendaFormularioItem): void {
    item.horarios.push('19:00');
    delete this.errosCampo[`agenda.${item.diaSemana}`];
  }

  removerHorario(item: AgendaFormularioItem, indice: number): void {
    item.horarios.splice(indice, 1);
    if (item.horarios.length === 0) {
      item.selecionado = false;
      queueMicrotask(() => {
        this.formularioSecaoRef?.nativeElement
          .querySelector<HTMLInputElement>(`#agenda-dia-${item.diaSemana}`)
          ?.focus();
      });
    }
    delete this.errosCampo[`agenda.${item.diaSemana}`];
  }

  atualizarHorario(item: AgendaFormularioItem): void {
    item.horarios.sort(compararHorarios);
    delete this.errosCampo[`agenda.${item.diaSemana}`];
  }

  private resumoAgendaDeItens(agendaAgrupada: Array<{ diaSemana: DiaSemanaAgendaReuniao; horarios: string[] }>): string {
    return agendaAgrupada.map((item) => `${ROTULO_DIA[item.diaSemana]} ${item.horarios.join(', ')}`).join(' · ');
  }

  atualizarMapaPreview(): void {
    if (!this.isBrowser || !this.mapaPreview || !this.maplibregl) {
      return;
    }

    const lat = Number(this.formulario.latitude);
    const lng = Number(this.formulario.longitude);
    const coordenadasValidas =
      this.formulario.latitude.trim() !== '' &&
      this.formulario.longitude.trim() !== '' &&
      !Number.isNaN(lat) &&
      !Number.isNaN(lng);

    if (!coordenadasValidas) {
      this.marcadorPreview?.remove();
      this.marcadorPreview = undefined;
      this.mapaPreview.setCenter(CENTRO_PADRAO);
      this.mapaPreview.setZoom(ZOOM_PADRAO);
      return;
    }

    const posicao: LngLatLike = [lng, lat];

    if (this.marcadorPreview) {
      this.marcadorPreview.setLngLat(posicao);
    } else {
      this.marcadorPreview = new this.maplibregl.Marker({ color: corPadraoJa() }).setLngLat(posicao).addTo(this.mapaPreview);
    }

    this.mapaPreview.setCenter(posicao);
    this.mapaPreview.setZoom(ZOOM_MARCADOR);
  }

  private construirErroFormulario(): string {
    const rotulos: Record<string, string> = {
      nome: 'Nome do grupo',
      descricao: 'Descrição',
      agenda: 'Agenda (dias e horários)',
      local: 'Endereço',
      estado: 'Estado (UF)',
      videoUrl: 'URL do vídeo',
    };
    const camposComErro = Object.keys(this.errosCampo)
      .filter((campo) => !campo.startsWith('agenda.'))
      .map((campo) => rotulos[campo] ?? campo);
    const temErroDeAgenda = Object.keys(this.errosCampo).some((campo) => campo.startsWith('agenda.'));
    if (temErroDeAgenda && !camposComErro.includes(rotulos['agenda'])) {
      camposComErro.push(rotulos['agenda']);
    }
    if (camposComErro.length === 1) {
      return `Corrija o campo "${camposComErro[0]}" destacado abaixo.`;
    }
    return `Corrija os campos destacados abaixo: ${camposComErro.join(', ')}.`;
  }

  salvar(): void {
    this.errosCampo = {};
    const virtual = this.formulario.categoria === 'virtual';

    if (!this.formulario.nome.trim()) {
      this.errosCampo['nome'] = 'Nome do grupo é obrigatório.';
    }
    if (!this.formulario.descricao.trim()) {
      this.errosCampo['descricao'] = 'Descrição é obrigatória.';
    }
    const agenda = this.formulario.agenda
      .filter((item) => item.selecionado)
      .flatMap(({ diaSemana, horarios }) =>
        horarios.map((horario) => ({ diaSemana, horario: horario.trim() })),
      );
    if (agenda.length === 0) {
      this.errosCampo['agenda'] = 'Selecione ao menos um dia da semana e informe o horário.';
    }
    for (const dia of this.formulario.agenda.filter((item) => item.selecionado)) {
      const horarios = dia.horarios.map((horario) => horario.trim());
      if (horarios.length === 0 || horarios.some((horario) => !horario)) {
        this.errosCampo[`agenda.${dia.diaSemana}`] = `Informe o horário de todas as linhas marcadas em ${ROTULO_DIA[dia.diaSemana]}.`;
      } else if (new Set(horarios).size !== horarios.length) {
        this.errosCampo[`agenda.${dia.diaSemana}`] = `Há horários repetidos em ${ROTULO_DIA[dia.diaSemana]} — remova a duplicata.`;
      }
    }
    if (!virtual) {
      if (!this.formulario.local.trim()) {
        this.errosCampo['local'] = 'Endereço é obrigatório para reunião presencial.';
      }
      if (!this.formulario.estado.trim()) {
        this.errosCampo['estado'] = 'Selecione o estado (UF) da reunião.';
      }
    }
    if (virtual && this.videoUrlInvalida()) {
      this.errosCampo['videoUrl'] = 'URL do YouTube inválida.';
    }
    if (Object.keys(this.errosCampo).length > 0) {
      this.erroFormulario = this.construirErroFormulario();
      return;
    }

    if (!virtual) {
      this.validarWhatsapp();
      if (this.errosCampo['whatsapp']) {
        this.erroFormulario = `Corrija o campo "WhatsApp" destacado abaixo: ${this.errosCampo['whatsapp']}`;
        return;
      }
    }

    const latitude = this.formulario.latitude.trim() ? Number(this.formulario.latitude) : undefined;
    const longitude = this.formulario.longitude.trim() ? Number(this.formulario.longitude) : undefined;

    if (!virtual) {
      if (latitude !== undefined && Number.isNaN(latitude)) {
        this.errosCampo['latitude'] = 'Latitude precisa ser um número válido (ex: -23.55).';
      }
      if (longitude !== undefined && Number.isNaN(longitude)) {
        this.errosCampo['longitude'] = 'Longitude precisa ser um número válido (ex: -46.63).';
      }
      if (Object.keys(this.errosCampo).length > 0) {
        const camposInvalidos = ['latitude', 'longitude'].filter((campo) => this.errosCampo[campo]);
        this.erroFormulario =
          camposInvalidos.length === 1
            ? `Corrija o campo "${camposInvalidos[0] === 'latitude' ? 'Latitude' : 'Longitude'}" destacado abaixo.`
            : 'Corrija os campos "Latitude" e "Longitude" destacados abaixo.';
        return;
      }
    }

    this.salvando = true;
    this.erroFormulario = '';

    if (virtual) {
      this.salvarVirtual(agenda);
      return;
    }

    const primeiroItem = agenda[0];
    const diaSemana = ROTULO_DIA[primeiroItem.diaSemana];
    const horario = primeiroItem.horario;

    const request: CriarReuniaoRequest = {
      nome: normalizarTextoLivre(this.formulario.nome),
      categoria: 'presencial',
      descricao: normalizarTextoLivre(this.formulario.descricao),
      agenda,
      dia: diaSemana,
      horario,
      local: normalizarTextoLivre(this.formulario.local),
      whatsapp: this.formulario.whatsapp.trim() || undefined,
      diaSemana,
      periodo: periodoParaHorario(horario),
      latitude,
      longitude,
      cep: this.formulario.cep.trim() || undefined,
      numero: this.formulario.numero.trim() || undefined,
      logradouro: normalizarTextoLivre(this.formulario.logradouro) || undefined,
      bairro: normalizarTextoLivre(this.formulario.bairro) || undefined,
      cidade: normalizarTextoLivre(this.formulario.cidade) || undefined,
      estado: this.formulario.estado.trim() || undefined,
    };

    const acao = this.reuniaoEmEdicao
      ? this.reuniaoService.editarReuniaoAdmin(this.reuniaoEmEdicao.id, {
          ...request,
          atualizadoEm: this.reuniaoEmEdicao.atualizadoEm ?? undefined,
        })
      : this.reuniaoService.criarReuniaoAdmin(request);

    acao.subscribe((envelope) => {
      this.salvando = false;

      if (envelope.success && envelope.data) {
        const salva = envelope.data;
        const reunioes = this.reuniaoEmEdicao
          ? this.reunioes().map((r) => (r.id === salva.id ? salva : r))
          : [...this.reunioes(), salva];
        reunioes.sort((a, b) => a.nome.localeCompare(b.nome));
        this.reunioes.set(reunioes);
        const editando = this.reuniaoEmEdicao !== null;
        this.avisoEnderecoLocalDivergente = (salva.avisos ?? []).some(
          (aviso) => aviso.codigo === CODIGO_AVISO_ENDERECO_LOCAL_DIVERGENTE,
        );
        if (editando && this.avisoEnderecoLocalDivergente) {
          this.reuniaoEmEdicao = salva;
        } else {
          this.modo = 'lista';
          this.reuniaoEmEdicao = null;
        }
      } else {
        this.conflitoVersao = envelope.error?.code === 'REUNIAO_VERSAO_CONFLITO';
        this.erroFormulario = mensagemDeErro(envelope.error, 'Não foi possível salvar a reunião.');
        this.errosCampo = mapearErrosCampo(envelope.error?.fields);
      }
    });
  }

  private salvarVirtual(agenda: AgendaItemReuniao[]): void {
    const request: CriarReuniaoOnlineRequest = {
      nome: normalizarTextoLivre(this.formulario.nome),
      descricao: normalizarTextoLivre(this.formulario.descricao),
      agenda,
      videoUrl: this.formulario.videoUrl.trim() || undefined,
      panfletoImagemId: this.formulario.panfletoImagemId.trim() || undefined,
    };

    const acao = this.reuniaoOnlineEmEdicao
      ? this.reuniaoOnlineService.editarReuniaoOnlineAdmin(this.reuniaoOnlineEmEdicao.id, request)
      : this.reuniaoOnlineService.criarReuniaoOnlineAdmin(request);

    acao.subscribe((envelope) => {
      this.salvando = false;

      if (envelope.success && envelope.data) {
        const salva = envelope.data;
        const reunioesOnline = this.reuniaoOnlineEmEdicao
          ? this.reunioesOnline().map((r) => (r.id === salva.id ? salva : r))
          : [...this.reunioesOnline(), salva];
        reunioesOnline.sort((a, b) => a.nome.localeCompare(b.nome));
        this.reunioesOnline.set(reunioesOnline);
        this.modo = 'lista';
        this.reuniaoOnlineEmEdicao = null;
      } else {
        this.erroFormulario = mensagemDeErro(envelope.error, 'Não foi possível salvar a reunião online.');
        this.errosCampo = mapearErrosCampo(envelope.error?.fields);
      }
    });
  }

  recarregarAposConflito(): void {
    const reuniaoId = this.reuniaoEmEdicao?.id;
    this.reuniaoService.listarReunioesAdmin().subscribe((envelope) => {
      if (envelope.success && envelope.data) {
        this.reunioes.set(envelope.data);
        const atualizada = envelope.data.find((item) => item.id === reuniaoId);
        if (atualizada) this.reuniaoEmEdicao = atualizada;
      }
    });
    this.conflitoVersao = false;
    this.erroFormulario = 'Dados recarregados. Seu rascunho foi preservado; revise e salve novamente.';
  }

  private agendaDaReuniao(reuniao: Reuniao): AgendaItemReuniao[] {
    if (reuniao.agenda?.length) {
      return reuniao.agenda;
    }
    const dia = DIAS_AGENDA.find((item) => item.rotulo === (reuniao.diaSemana || reuniao.dia));
    return dia && reuniao.horario ? [{ diaSemana: dia.diaSemana, horario: reuniao.horario }] : [];
  }

  private agendaAgrupada(agenda: AgendaItemReuniao[]): Array<{ diaSemana: DiaSemanaAgendaReuniao; horarios: string[] }> {
    return DIAS_AGENDA.flatMap(({ diaSemana }) => {
      const horarios = agenda
        .filter((item) => item.diaSemana === diaSemana)
        .map((item) => item.horario)
        .sort(compararHorarios);
      return horarios.length ? [{ diaSemana, horarios }] : [];
    });
  }

  private agendaParaFormulario(agenda: AgendaItemReuniao[]): AgendaFormularioItem[] {
    return DIAS_AGENDA.map((dia) => {
      const horarios = agenda
        .filter((item) => item.diaSemana === dia.diaSemana)
        .map((item) => item.horario)
        .sort(compararHorarios);
      return { ...dia, selecionado: horarios.length > 0, horarios };
    });
  }

  pedirConfirmacaoExclusao(item: ReuniaoListaItem): void {
    this.confirmandoExclusao = item.id;
    this.erroExclusao = '';
  }

  cancelarExclusao(): void {
    this.confirmandoExclusao = null;
    this.erroExclusao = '';
  }

  confirmarExclusao(item: ReuniaoListaItem): void {
    this.excluindo = true;
    this.erroExclusao = '';

    const acao =
      item.origem === 'presencial'
        ? this.reuniaoService.excluirReuniaoAdmin(item.id)
        : this.reuniaoOnlineService.excluirReuniaoOnlineAdmin(item.id);

    acao.subscribe((envelope) => {
      this.excluindo = false;
      this.confirmandoExclusao = null;

      if (envelope.success) {
        if (item.origem === 'presencial') {
          this.reunioes.update((atuais) => atuais.filter((r) => r.id !== item.id));
        } else {
          this.reunioesOnline.update((atuais) => atuais.filter((r) => r.id !== item.id));
        }
      } else {
        this.erroExclusao = envelope.error?.message ?? 'Não foi possível excluir a reunião.';
      }
    });
  }

  private async carregarMapLibreECriarMapaPreview(container: HTMLDivElement): Promise<void> {
    const token = ++this.mapaInitToken;
    if (!this.maplibregl) {
      const mod = await import('maplibre-gl');
      this.maplibregl = (mod as unknown as { default?: typeof import('maplibre-gl') }).default ?? mod;
      configurarMapLibreWorker(this.maplibregl);
    }
    if (token !== this.mapaInitToken) {
      return;
    }
    this.criarMapaPreview(container);
  }

  private criarMapaPreview(container: HTMLDivElement): void {
    if (!this.maplibregl) {
      return;
    }

    this.destruirMapaPreview();

    this.mapaPreview = new this.maplibregl.Map({
      container,
      style: ESTILO_OSM,
      center: CENTRO_PADRAO,
      zoom: ZOOM_PADRAO,
      scrollZoom: false,
      dragRotate: false,
      dragPan: false,
      attributionControl: { compact: true },
    });

    this.mapaPreview.on('load', () => {
      this.mapaPreview?.resize();
      this.atualizarMapaPreview();
    });

    this.resizeObserver = new ResizeObserver(() => {
      this.mapaPreview?.resize();
    });
    this.resizeObserver.observe(container);

    setTimeout(() => this.mapaPreview?.resize(), 200);
  }

  private destruirMapaPreview(): void {
    this.mapaInitToken++;
    this.resizeObserver?.disconnect();
    this.resizeObserver = undefined;
    this.marcadorPreview?.remove();
    this.marcadorPreview = undefined;
    this.mapaPreview?.remove();
    this.mapaPreview = undefined;
  }
}
