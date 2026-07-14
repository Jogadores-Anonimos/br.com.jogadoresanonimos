import { CommonModule, isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, ElementRef, OnInit, PLATFORM_ID, ViewChild, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { catchError, of } from 'rxjs';
import * as L from 'leaflet';

import { ReuniaoService } from '../../../services/reuniao';
import { CategoriaReuniao, CriarReuniaoRequest, Reuniao } from '../../../services/reuniao.model';

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
  };
}

// Dropdown de horário de 30 em 30 min, 00:00–23:30.
function gerarHorarios(): string[] {
  const horarios: string[] = [];
  for (let h = 0; h < 24; h++) {
    for (const m of ['00', '30']) {
      horarios.push(`${String(h).padStart(2, '0')}:${m}`);
    }
  }
  return horarios;
}

// Mesmas 3 faixas que a página pública usa pra filtrar (sem "Madrugada" —
// horários de madrugada caem em "Noite", já que só existem essas 3 opções
// no filtro público hoje).
function periodoParaHorario(horario: string): 'Manhã' | 'Tarde' | 'Noite' {
  const hora = Number(horario.split(':')[0]);
  if (hora >= 5 && hora < 12) return 'Manhã';
  if (hora >= 12 && hora < 18) return 'Tarde';
  return 'Noite';
}

interface EnderecoViaCep {
  logradouro?: string;
  bairro?: string;
  localidade?: string;
  uf?: string;
  erro?: boolean;
}

interface ResultadoNominatim {
  lat: string;
  lon: string;
}

const CENTRO_PADRAO: L.LatLngTuple = [-23.55, -46.62];

@Component({
  selector: 'app-admin-reunioes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-reunioes.html',
  styleUrl: './admin-reunioes.css',
})
export class AdminReunioes implements OnInit {
  private readonly reuniaoService = inject(ReuniaoService);
  private readonly http = inject(HttpClient);
  private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));

  readonly horarios = gerarHorarios();

  carregando = true;
  erroCarregamento = '';
  reunioes: Reuniao[] = [];

  modo: 'lista' | 'formulario' = 'lista';
  reuniaoEmEdicao: Reuniao | null = null;
  formulario: FormularioReuniao = formularioVazio();
  salvando = false;
  erroFormulario = '';

  cep = '';
  numero = '';
  buscandoCep = false;
  erroCep = '';

  confirmandoExclusao: string | null = null;
  excluindo = false;
  erroExclusao = '';

  private mapaPreview?: L.Map;
  private marcadorPreview?: L.Marker;

  @ViewChild('mapaPreviewContainer')
  set mapaPreviewContainerRef(ref: ElementRef<HTMLDivElement> | undefined) {
    if (!this.isBrowser) {
      return;
    }
    if (ref) {
      this.criarMapaPreview(ref.nativeElement);
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
        this.reunioes = envelope.data;
      } else {
        this.reunioes = [];
        this.erroCarregamento = envelope.error?.message ?? 'Não foi possível carregar as reuniões.';
      }
    });
  }

  abrirNovo(): void {
    this.reuniaoEmEdicao = null;
    this.formulario = formularioVazio();
    this.cep = '';
    this.numero = '';
    this.erroCep = '';
    this.erroFormulario = '';
    this.modo = 'formulario';
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
    };
    this.cep = '';
    this.numero = '';
    this.erroCep = '';
    this.erroFormulario = '';
    this.modo = 'formulario';
  }

  cancelar(): void {
    this.modo = 'lista';
    this.reuniaoEmEdicao = null;
    this.erroFormulario = '';
  }

  // ViaCEP preenche o endereço, Nominatim (OpenStreetMap) resolve a
  // coordenada a partir do endereço completo — CEP sozinho não dá
  // precisão suficiente pro pino no mapa, só serve pra achar rua/bairro.
  buscarCep(): void {
    const cepLimpo = this.cep.replace(/\D/g, '');
    if (cepLimpo.length !== 8) {
      this.erroCep = 'CEP inválido — precisa ter 8 dígitos.';
      return;
    }

    this.buscandoCep = true;
    this.erroCep = '';

    this.http
      .get<EnderecoViaCep>(`https://viacep.com.br/ws/${cepLimpo}/json/`)
      .pipe(catchError(() => of(null)))
      .subscribe((endereco) => {
        if (!endereco || endereco.erro) {
          this.buscandoCep = false;
          this.erroCep = 'CEP não encontrado.';
          return;
        }

        const logradouroComNumero = [endereco.logradouro, this.numero.trim()].filter(Boolean).join(', ');
        const enderecoBase = [logradouroComNumero, endereco.bairro, endereco.localidade, endereco.uf]
          .filter(Boolean)
          .join(', ');
        this.formulario.local = enderecoBase || this.formulario.local;

        this.http
          .get<ResultadoNominatim[]>('https://nominatim.openstreetmap.org/search', {
            params: { format: 'json', q: enderecoBase, limit: '1' },
          })
          .pipe(catchError(() => of([])))
          .subscribe((resultados) => {
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

  atualizarMapaPreview(): void {
    if (!this.isBrowser || !this.mapaPreview) {
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
      this.marcadorPreview = L.marker(posicao).addTo(this.mapaPreview);
    }

    this.mapaPreview.setView(posicao, 14);
  }

  salvar(): void {
    if (
      !this.formulario.nome.trim() ||
      !this.formulario.descricao.trim() ||
      !this.formulario.diaSemana.trim() ||
      !this.formulario.horario.trim() ||
      !this.formulario.local.trim()
    ) {
      this.erroFormulario = 'Nome, descrição, dia da semana, horário e local são obrigatórios.';
      return;
    }

    const latitude = this.formulario.latitude.trim() ? Number(this.formulario.latitude) : undefined;
    const longitude = this.formulario.longitude.trim() ? Number(this.formulario.longitude) : undefined;

    if (
      (latitude !== undefined && Number.isNaN(latitude)) ||
      (longitude !== undefined && Number.isNaN(longitude))
    ) {
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
      // "Dia" (texto de exibição) e "Período" não são mais digitados à
      // parte — derivados de diaSemana/horario pra eliminar duplicidade
      // de campo que confundia no formulário.
      dia: diaSemana,
      horario,
      local: this.formulario.local.trim(),
      whatsapp: this.formulario.whatsapp.trim() || undefined,
      diaSemana,
      periodo: periodoParaHorario(horario),
      latitude,
      longitude,
    };

    const acao = this.reuniaoEmEdicao
      ? this.reuniaoService.editarReuniaoAdmin(this.reuniaoEmEdicao.id, request)
      : this.reuniaoService.criarReuniaoAdmin(request);

    acao.subscribe((envelope) => {
      this.salvando = false;

      if (envelope.success) {
        this.modo = 'lista';
        this.reuniaoEmEdicao = null;
        this.carregarReunioes();
      } else {
        this.erroFormulario = envelope.error?.message ?? 'Não foi possível salvar a reunião.';
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
        this.reunioes = this.reunioes.filter((r) => r.id !== reuniao.id);
      } else {
        this.erroExclusao = envelope.error?.message ?? 'Não foi possível excluir a reunião.';
      }
    });
  }

  private criarMapaPreview(container: HTMLDivElement): void {
    this.destruirMapaPreview();

    this.mapaPreview = L.map(container, {
      center: CENTRO_PADRAO,
      zoom: 11,
      scrollWheelZoom: false,
      dragging: false,
      zoomControl: false,
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 18,
      attribution: '&copy; colaboradores do OpenStreetMap',
    }).addTo(this.mapaPreview);

    setTimeout(() => this.mapaPreview?.invalidateSize(), 0);
    this.atualizarMapaPreview();
  }

  private destruirMapaPreview(): void {
    this.marcadorPreview?.remove();
    this.marcadorPreview = undefined;
    this.mapaPreview?.remove();
    this.mapaPreview = undefined;
  }
}
