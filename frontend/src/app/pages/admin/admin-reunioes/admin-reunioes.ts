import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, ElementRef, OnInit, PLATFORM_ID, ViewChild, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import type * as L from 'leaflet';

import { mapearErrosCampo } from '../../../services/api-envelope';
import { EnderecoLookupService } from '../../../services/endereco-lookup/endereco-lookup';
import { ReuniaoService } from '../../../services/reuniao/reuniao';
import { CategoriaReuniao, CriarReuniaoRequest, Reuniao } from '../../../services/reuniao/reuniao.model';

interface FormularioReuniao {
  nome: string;
  categoria: CategoriaReuniao;
  descricao: string;
  horario: string;
  local: string;
  whatsapp: string;
  diaSemana: string;
  latitude: string;
  longitude: string;
  cep: string;
  numero: string;
  logradouro: string;
  bairro: string;
  cidade: string;
  estado: string;
  linkReuniao: string;
}

function formularioVazio(): FormularioReuniao {
  return {
    nome: '',
    categoria: 'presencial',
    descricao: '',
    horario: '',
    local: '',
    whatsapp: '',
    diaSemana: '',
    latitude: '',
    longitude: '',
    cep: '',
    numero: '',
    logradouro: '',
    bairro: '',
    cidade: '',
    estado: '',
    linkReuniao: '',
  };
}

function gerarHorarios(): string[] {
  const horarios: string[] = [];
  for (let h = 0; h < 24; h++) {
    for (const m of ['00', '30']) {
      horarios.push(`${String(h).padStart(2, '0')}:${m}`);
    }
  }
  return horarios;
}

function periodoParaHorario(horario: string): 'Manhã' | 'Tarde' | 'Noite' {
  const hora = Number(horario.split(':')[0]);
  if (hora >= 5 && hora < 12) return 'Manhã';
  if (hora >= 12 && hora < 18) return 'Tarde';
  return 'Noite';
}

const CENTRO_PADRAO: L.LatLngTuple = [-23.55, -46.62];

const DIAS_SEMANA = ['Segunda', 'Terça', 'Quarta', 'Quinta', 'Sexta', 'Sábado', 'Domingo'];

const WHATSAPP_DIGITOS_REGEX = /^(?:55)?\d{10,11}$/;

function whatsappFormatoValido(valor: string): boolean {
  const digitos = valor.replace(/\D/g, '');
  return WHATSAPP_DIGITOS_REGEX.test(digitos);
}

@Component({
  selector: 'app-admin-reunioes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-reunioes.html',
  styleUrl: './admin-reunioes.css',
})
export class AdminReunioes implements OnInit {
  private readonly reuniaoService = inject(ReuniaoService);
  private readonly enderecoLookupService = inject(EnderecoLookupService);
  private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));

  readonly horarios = gerarHorarios();
  readonly diasSemanaFiltro = DIAS_SEMANA;

  carregando = true;
  erroCarregamento = '';
  readonly reunioes = signal<Reuniao[]>([]);

  readonly filtroTexto = signal('');
  readonly filtroCategoria = signal<CategoriaReuniao | ''>('');
  readonly filtroDiaSemana = signal('');

  readonly filtroAtivo = computed(
    () => this.filtroTexto().trim() !== '' || this.filtroCategoria() !== '' || this.filtroDiaSemana() !== '',
  );

  readonly reunioesFiltradas = computed(() => {
    const texto = this.filtroTexto().trim().toLowerCase();
    const digitosTexto = texto.replace(/\D/g, '');
    const categoria = this.filtroCategoria();
    const diaSemana = this.filtroDiaSemana();

    return this.reunioes().filter((reuniao) => {
      const combinaTexto =
        texto === '' ||
        reuniao.nome.toLowerCase().includes(texto) ||
        reuniao.local.toLowerCase().includes(texto) ||
        (reuniao.logradouro ?? '').toLowerCase().includes(texto) ||
        (reuniao.bairro ?? '').toLowerCase().includes(texto) ||
        (reuniao.cidade ?? '').toLowerCase().includes(texto) ||
        (reuniao.estado ?? '').toLowerCase().includes(texto) ||
        (digitosTexto !== '' && (reuniao.cep ?? '').replace(/\D/g, '').includes(digitosTexto));
      const combinaCategoria = categoria === '' || reuniao.categoria === categoria;
      const combinaDiaSemana = diaSemana === '' || reuniao.diaSemana === diaSemana;
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
  formulario: FormularioReuniao = formularioVazio();
  salvando = false;
  erroFormulario = '';
  errosCampo: Record<string, string> = {};

  buscandoCep = false;
  erroCep = '';

  confirmandoExclusao: string | null = null;
  excluindo = false;
  erroExclusao = '';

  private leaflet?: typeof L;
  private mapaPreview?: L.Map;
  private marcadorPreview?: L.Marker;
  private resizeObserver?: ResizeObserver;
  private mapaInitToken = 0;

  @ViewChild('formularioSecao') formularioSecaoRef?: ElementRef<HTMLElement>;
  @ViewChild('campoNome') campoNomeRef?: ElementRef<HTMLInputElement>;

  @ViewChild('mapaPreviewContainer')
  set mapaPreviewContainerRef(ref: ElementRef<HTMLDivElement> | undefined) {
    if (!this.isBrowser) {
      return;
    }
    if (ref) {
      this.carregarLeafletECriarMapaPreview(ref.nativeElement);
    } else {
      this.destruirMapaPreview();
    }
  }

  ngOnInit(): void {
    this.carregarReunioes();
  }

  private carregarReunioes(): void {
    this.carregando = true;
    this.erroCarregamento = '';

    this.reuniaoService.listarReunioesAdmin().subscribe((envelope) => {
      this.carregando = false;

      if (envelope.success && envelope.data) {
        this.reunioes.set(envelope.data);
      } else {
        this.reunioes.set([]);
        this.erroCarregamento = envelope.error?.message ?? 'Não foi possível carregar as reuniões.';
      }
    });
  }

  abrirNovo(): void {
    this.reuniaoEmEdicao = null;
    this.formulario = formularioVazio();
    this.erroCep = '';
    this.erroFormulario = '';
    this.errosCampo = {};
    this.modo = 'formulario';
    this.focarFormulario();
  }

  abrirEdicao(reuniao: Reuniao): void {
    this.reuniaoEmEdicao = reuniao;
    this.formulario = {
      nome: reuniao.nome,
      categoria: reuniao.categoria,
      descricao: reuniao.descricao,
      horario: reuniao.horario,
      local: reuniao.local,
      whatsapp: reuniao.whatsapp ?? '',
      diaSemana: reuniao.diaSemana ?? '',
      latitude: reuniao.latitude !== null ? String(reuniao.latitude) : '',
      longitude: reuniao.longitude !== null ? String(reuniao.longitude) : '',
      cep: reuniao.cep ?? '',
      numero: reuniao.numero ?? '',
      logradouro: reuniao.logradouro ?? '',
      bairro: reuniao.bairro ?? '',
      cidade: reuniao.cidade ?? '',
      estado: reuniao.estado ?? '',
      linkReuniao: reuniao.linkReuniao ?? '',
    };
    this.erroCep = '';
    this.erroFormulario = '';
    this.errosCampo = {};
    this.modo = 'formulario';
    this.focarFormulario();
  }

  private focarFormulario(): void {
    if (!this.isBrowser) {
      return;
    }
    setTimeout(() => {
      const secao = this.formularioSecaoRef?.nativeElement;
      const reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
      secao?.scrollIntoView({ behavior: reducedMotion ? 'auto' : 'smooth', block: 'start' });
      this.campoNomeRef?.nativeElement.focus({ preventScroll: true });
    });
  }

  cancelar(): void {
    this.modo = 'lista';
    this.reuniaoEmEdicao = null;
    this.erroFormulario = '';
    this.errosCampo = {};
  }

  buscarCep(): void {
    const cepLimpo = this.formulario.cep.replace(/\D/g, '');
    if (cepLimpo.length !== 8) {
      this.erroCep = 'CEP inválido — precisa ter 8 dígitos.';
      return;
    }

    this.buscandoCep = true;
    this.erroCep = '';

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
        this.atualizarMapaPreview();
      });
    });
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

  atualizarMapaPreview(): void {
    if (!this.isBrowser || !this.mapaPreview || !this.leaflet) {
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
      this.mapaPreview.setView(CENTRO_PADRAO, 11);
      return;
    }

    const posicao: L.LatLngTuple = [lat, lng];

    if (this.marcadorPreview) {
      this.marcadorPreview.setLatLng(posicao);
    } else {
      this.marcadorPreview = this.leaflet.marker(posicao).addTo(this.mapaPreview);
    }

    this.mapaPreview.setView(posicao, 14);
  }

  salvar(): void {
    this.errosCampo = {};

    if (!this.formulario.nome.trim()) {
      this.errosCampo['nome'] = 'Nome é obrigatório.';
    }
    if (!this.formulario.descricao.trim()) {
      this.errosCampo['descricao'] = 'Descrição é obrigatória.';
    }
    if (!this.formulario.diaSemana.trim()) {
      this.errosCampo['diaSemana'] = 'Dia da semana é obrigatório.';
    }
    if (!this.formulario.horario.trim()) {
      this.errosCampo['horario'] = 'Horário é obrigatório.';
    }
    if (this.formulario.categoria === 'presencial' && !this.formulario.local.trim()) {
      this.errosCampo['local'] = 'Local é obrigatório.';
    }
    if (Object.keys(this.errosCampo).length > 0) {
      this.erroFormulario =
        this.formulario.categoria === 'presencial'
          ? 'Nome, descrição, dia da semana, horário e local são obrigatórios.'
          : 'Nome, descrição, dia da semana e horário são obrigatórios.';
      return;
    }

    this.validarWhatsapp();
    if (this.errosCampo['whatsapp']) {
      this.erroFormulario = this.errosCampo['whatsapp'];
      return;
    }

    const latitude = this.formulario.latitude.trim() ? Number(this.formulario.latitude) : undefined;
    const longitude = this.formulario.longitude.trim() ? Number(this.formulario.longitude) : undefined;

    if (latitude !== undefined && Number.isNaN(latitude)) {
      this.errosCampo['latitude'] = 'Latitude precisa ser um número válido.';
    }
    if (longitude !== undefined && Number.isNaN(longitude)) {
      this.errosCampo['longitude'] = 'Longitude precisa ser um número válido.';
    }
    if (Object.keys(this.errosCampo).length > 0) {
      this.erroFormulario = 'Latitude e longitude precisam ser números válidos.';
      return;
    }

    this.salvando = true;
    this.erroFormulario = '';

    const diaSemana = this.formulario.diaSemana.trim();
    const horario = this.formulario.horario.trim();

    const request: CriarReuniaoRequest = {
      nome: this.formulario.nome.trim(),
      categoria: this.formulario.categoria,
      descricao: this.formulario.descricao.trim(),
      dia: diaSemana,
      horario,
      local: this.formulario.local.trim(),
      whatsapp: this.formulario.whatsapp.trim() || undefined,
      diaSemana,
      periodo: periodoParaHorario(horario),
      latitude,
      longitude,
      cep: this.formulario.cep.trim() || undefined,
      numero: this.formulario.numero.trim() || undefined,
      logradouro: this.formulario.logradouro.trim() || undefined,
      bairro: this.formulario.bairro.trim() || undefined,
      cidade: this.formulario.cidade.trim() || undefined,
      estado: this.formulario.estado.trim() || undefined,
      linkReuniao: this.formulario.linkReuniao.trim() || undefined,
    };

    const acao = this.reuniaoEmEdicao
      ? this.reuniaoService.editarReuniaoAdmin(this.reuniaoEmEdicao.id, request)
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
        this.modo = 'lista';
        this.reuniaoEmEdicao = null;
      } else {
        this.erroFormulario = envelope.error?.message ?? 'Não foi possível salvar a reunião.';
        this.errosCampo = mapearErrosCampo(envelope.error?.fields);
      }
    });
  }

  pedirConfirmacaoExclusao(reuniao: Reuniao): void {
    this.confirmandoExclusao = reuniao.id;
    this.erroExclusao = '';
  }

  cancelarExclusao(): void {
    this.confirmandoExclusao = null;
    this.erroExclusao = '';
  }

  confirmarExclusao(reuniao: Reuniao): void {
    this.excluindo = true;
    this.erroExclusao = '';

    this.reuniaoService.excluirReuniaoAdmin(reuniao.id).subscribe((envelope) => {
      this.excluindo = false;
      this.confirmandoExclusao = null;

      if (envelope.success) {
        this.reunioes.update((atuais) => atuais.filter((r) => r.id !== reuniao.id));
      } else {
        this.erroExclusao = envelope.error?.message ?? 'Não foi possível excluir a reunião.';
      }
    });
  }

  private async carregarLeafletECriarMapaPreview(container: HTMLDivElement): Promise<void> {
    const token = ++this.mapaInitToken;
    if (!this.leaflet) {
      const mod = await import('leaflet');
      this.leaflet = (mod as unknown as { default?: typeof L }).default ?? (mod as unknown as typeof L);
    }
    if (token !== this.mapaInitToken) {
      return;
    }
    this.criarMapaPreview(container);
  }

  private criarMapaPreview(container: HTMLDivElement): void {
    if (!this.leaflet) {
      return;
    }

    this.destruirMapaPreview();

    this.mapaPreview = this.leaflet.map(container, {
      center: CENTRO_PADRAO,
      zoom: 11,
      scrollWheelZoom: false,
      dragging: false,
      zoomControl: false,
    });

    this.leaflet
      .tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 18,
        attribution: '&copy; colaboradores do OpenStreetMap',
      })
      .addTo(this.mapaPreview);

    this.atualizarMapaPreview();

    this.resizeObserver = new ResizeObserver(() => {
      if (!this.mapaPreview) {
        return;
      }
      this.mapaPreview.invalidateSize();
      this.mapaPreview.panBy([1, 0], { animate: false });
      this.mapaPreview.panBy([-1, 0], { animate: false });
    });
    this.resizeObserver.observe(container);
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
