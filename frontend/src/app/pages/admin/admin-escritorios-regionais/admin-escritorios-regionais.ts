import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { catchError, of } from 'rxjs';

import { EscritorioRegionalService } from '../../../services/escritorio-regional';
import {
  DiaSemana,
  EscritorioServico,
  EscritoriosRegionais,
  EstadoIntergrupo,
  GrupoIntergrupo,
  RegiaoIntergrupo,
  UNIDADES_FEDERATIVAS,
} from '../../../services/escritorio-regional.model';

type Nivel = 'regioes' | 'estados' | 'grupos';

interface FormularioRegiao {
  nome: string;
}

interface FormularioEstado {
  uf: string;
}

interface FormularioTelefone {
  id: string;
  label: string;
  telefone: string;
}

interface FormularioGrupo {
  nome: string;
  endereco: string;
  bairro: string;
  cidade: string;
  diasSemana: DiaSemana[];
  horario: string;
  telefones: FormularioTelefone[];
}

interface FormularioEscritorio {
  nome: string;
  endereco: string;
  complemento: string;
  bairro: string;
  cidade: string;
  emailsTexto: string;
  telefones: FormularioTelefone[];
}

function formularioRegiaoVazio(): FormularioRegiao {
  return { nome: '' };
}

function formularioEstadoVazio(): FormularioEstado {
  return { uf: '' };
}

function formularioGrupoVazio(): FormularioGrupo {
  return { nome: '', endereco: '', bairro: '', cidade: '', diasSemana: [], horario: '', telefones: [] };
}

function formularioEscritorioVazio(): FormularioEscritorio {
  return { nome: '', endereco: '', complemento: '', bairro: '', cidade: '', emailsTexto: '', telefones: [] };
}

type ExclusaoAlvo =
  | { tipo: 'regiao'; regiao: RegiaoIntergrupo }
  | { tipo: 'estado'; estado: EstadoIntergrupo }
  | { tipo: 'grupo'; grupo: GrupoIntergrupo }
  | { tipo: 'escritorio'; escritorio: EscritorioServico };

interface EnderecoViaCep {
  logradouro?: string;
  bairro?: string;
  localidade?: string;
  uf?: string;
  erro?: boolean;
}

const DIAS_SEMANA: { valor: DiaSemana; rotulo: string }[] = [
  { valor: 'DOMINGO', rotulo: 'Domingo' },
  { valor: 'SEGUNDA', rotulo: 'Segunda' },
  { valor: 'TERCA', rotulo: 'Terça' },
  { valor: 'QUARTA', rotulo: 'Quarta' },
  { valor: 'QUINTA', rotulo: 'Quinta' },
  { valor: 'SEXTA', rotulo: 'Sexta' },
  { valor: 'SABADO', rotulo: 'Sábado' },
];

// Dropdown de horário de 30 em 30 min, 00:00–23:30 — mesmo padrão usado em admin-reunioes.
function gerarHorarios(): string[] {
  const horarios: string[] = [];
  for (let h = 0; h < 24; h++) {
    for (const m of ['00', '30']) {
      horarios.push(`${String(h).padStart(2, '0')}:${m}`);
    }
  }
  return horarios;
}

let contadorTelefoneFormulario = 0;
function proximoIdTelefoneFormulario(): string {
  contadorTelefoneFormulario += 1;
  return `form-telefone-${contadorTelefoneFormulario}`;
}

@Component({
  selector: 'app-admin-escritorios-regionais',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-escritorios-regionais.html',
  styleUrl: './admin-escritorios-regionais.css',
})
export class AdminEscritoriosRegionais implements OnInit {
  private readonly service = inject(EscritorioRegionalService);
  private readonly http = inject(HttpClient);

  readonly unidadesFederativas = UNIDADES_FEDERATIVAS;
  readonly diasSemana = DIAS_SEMANA;
  readonly horarios = gerarHorarios();

  carregando = true;
  erroCarregamento = '';
  dados: EscritoriosRegionais = { escritorios: [], regioes: [] };

  nivel: Nivel = 'regioes';
  regiaoAtual: RegiaoIntergrupo | null = null;
  estadoAtual: EstadoIntergrupo | null = null;

  salvandoRegiao = false;
  erroFormularioRegiao = '';
  modoFormularioRegiao: 'fechado' | 'novo' | 'edicao' = 'fechado';
  regiaoEmEdicao: RegiaoIntergrupo | null = null;
  formularioRegiao: FormularioRegiao = formularioRegiaoVazio();

  salvandoEstado = false;
  erroFormularioEstado = '';
  modoFormularioEstado: 'fechado' | 'novo' | 'edicao' = 'fechado';
  estadoEmEdicao: EstadoIntergrupo | null = null;
  formularioEstado: FormularioEstado = formularioEstadoVazio();

  salvandoGrupo = false;
  erroFormularioGrupo = '';
  modoFormularioGrupo: 'fechado' | 'novo' | 'edicao' = 'fechado';
  grupoEmEdicao: GrupoIntergrupo | null = null;
  formularioGrupo: FormularioGrupo = formularioGrupoVazio();

  cepGrupo = '';
  numeroGrupo = '';
  buscandoCepGrupo = false;
  erroCepGrupo = '';

  salvandoEscritorio = false;
  erroFormularioEscritorio = '';
  modoFormularioEscritorio: 'fechado' | 'novo' | 'edicao' = 'fechado';
  escritorioEmEdicao: EscritorioServico | null = null;
  formularioEscritorio: FormularioEscritorio = formularioEscritorioVazio();

  cepEscritorio = '';
  numeroEscritorio = '';
  buscandoCepEscritorio = false;
  erroCepEscritorio = '';

  exclusaoAlvo: ExclusaoAlvo | null = null;
  excluindo = false;
  erroExclusao = '';

  // Getters de apoio pro template: discriminação de union type não
  // estreita corretamente em templates Angular (diferente de código TS
  // normal), então a narrowing é feita aqui e o template só lê o valor
  // já resolvido.
  get alvoRegiao(): RegiaoIntergrupo | null {
    return this.exclusaoAlvo?.tipo === 'regiao' ? this.exclusaoAlvo.regiao : null;
  }

  get alvoEstado(): EstadoIntergrupo | null {
    return this.exclusaoAlvo?.tipo === 'estado' ? this.exclusaoAlvo.estado : null;
  }

  get alvoGrupo(): GrupoIntergrupo | null {
    return this.exclusaoAlvo?.tipo === 'grupo' ? this.exclusaoAlvo.grupo : null;
  }

  get alvoEscritorio(): EscritorioServico | null {
    return this.exclusaoAlvo?.tipo === 'escritorio' ? this.exclusaoAlvo.escritorio : null;
  }

  ngOnInit(): void {
    this.carregarEstrutura();
  }

  private carregarEstrutura(): void {
    this.carregando = true;
    this.erroCarregamento = '';

    this.service.listarEstruturaAdmin().subscribe((envelope) => {
      this.carregando = false;

      if (envelope.success && envelope.data) {
        this.dados = envelope.data;
        this.resincronizarSelecaoAtual();
      } else {
        this.erroCarregamento = envelope.error?.message ?? 'Não foi possível carregar os escritórios regionais.';
      }
    });
  }

  private resincronizarSelecaoAtual(): void {
    if (this.regiaoAtual) {
      this.regiaoAtual = this.dados.regioes.find((r) => r.id === this.regiaoAtual!.id) ?? null;
    }
    if (this.estadoAtual) {
      this.estadoAtual = this.regiaoAtual?.estados.find((e) => e.id === this.estadoAtual!.id) ?? null;
    }
    if (!this.regiaoAtual) {
      this.nivel = 'regioes';
    } else if (!this.estadoAtual && this.nivel === 'grupos') {
      this.nivel = 'estados';
    }
  }

  get regioesOrdenadas(): RegiaoIntergrupo[] {
    return [...this.dados.regioes].sort((a, b) => a.ordem - b.ordem);
  }

  get estadosOrdenados(): EstadoIntergrupo[] {
    return this.regiaoAtual ? [...this.regiaoAtual.estados].sort((a, b) => a.ordem - b.ordem) : [];
  }

  get gruposOrdenados(): GrupoIntergrupo[] {
    return this.estadoAtual ? [...this.estadoAtual.grupos].sort((a, b) => a.ordem - b.ordem) : [];
  }

  get escritoriosOrdenados(): EscritorioServico[] {
    return [...this.dados.escritorios].sort((a, b) => a.ordem - b.ordem);
  }

  rotuloDiasSemana(grupo: GrupoIntergrupo): string {
    const ordem: DiaSemana[] = ['DOMINGO', 'SEGUNDA', 'TERCA', 'QUARTA', 'QUINTA', 'SEXTA', 'SABADO'];
    return ordem
      .filter((dia) => grupo.diasSemana.includes(dia))
      .map((dia) => this.diasSemana.find((d) => d.valor === dia)?.rotulo ?? dia)
      .join(', ');
  }

  // --- navegação drill-down ---

  verEstados(regiao: RegiaoIntergrupo): void {
    this.regiaoAtual = regiao;
    this.estadoAtual = null;
    this.nivel = 'estados';
  }

  verGrupos(estado: EstadoIntergrupo): void {
    this.estadoAtual = estado;
    this.nivel = 'grupos';
  }

  irParaRegioes(): void {
    this.nivel = 'regioes';
    this.regiaoAtual = null;
    this.estadoAtual = null;
  }

  irParaEstados(): void {
    this.nivel = 'estados';
    this.estadoAtual = null;
  }

  // --- formulário região ---

  abrirNovaRegiao(): void {
    this.regiaoEmEdicao = null;
    this.formularioRegiao = formularioRegiaoVazio();
    this.erroFormularioRegiao = '';
    this.modoFormularioRegiao = 'novo';
  }

  abrirEdicaoRegiao(regiao: RegiaoIntergrupo): void {
    this.regiaoEmEdicao = regiao;
    this.formularioRegiao = { nome: regiao.nome };
    this.erroFormularioRegiao = '';
    this.modoFormularioRegiao = 'edicao';
  }

  cancelarFormularioRegiao(): void {
    this.modoFormularioRegiao = 'fechado';
    this.regiaoEmEdicao = null;
    this.erroFormularioRegiao = '';
  }

  salvarRegiao(): void {
    if (!this.formularioRegiao.nome.trim()) {
      this.erroFormularioRegiao = 'Nome é obrigatório.';
      return;
    }

    this.salvandoRegiao = true;
    this.erroFormularioRegiao = '';

    const request = { nome: this.formularioRegiao.nome.trim() };
    const acao = this.regiaoEmEdicao
      ? this.service.editarRegiaoAdmin(this.regiaoEmEdicao.id, request)
      : this.service.criarRegiaoAdmin(request);

    acao.subscribe((envelope) => {
      this.salvandoRegiao = false;

      if (envelope.success) {
        this.modoFormularioRegiao = 'fechado';
        this.regiaoEmEdicao = null;
        this.carregarEstrutura();
      } else {
        this.erroFormularioRegiao = envelope.error?.message ?? 'Não foi possível salvar a região.';
      }
    });
  }

  moverRegiao(regiao: RegiaoIntergrupo, direcao: 'cima' | 'baixo'): void {
    const acao =
      direcao === 'cima' ? this.service.moverRegiaoParaCima(regiao.id) : this.service.moverRegiaoParaBaixo(regiao.id);
    acao.subscribe((envelope) => {
      if (envelope.success) {
        this.carregarEstrutura();
      }
    });
  }

  ehPrimeiraRegiao(regiao: RegiaoIntergrupo): boolean {
    return this.regioesOrdenadas[0]?.id === regiao.id;
  }

  ehUltimaRegiao(regiao: RegiaoIntergrupo): boolean {
    const lista = this.regioesOrdenadas;
    return lista[lista.length - 1]?.id === regiao.id;
  }

  // --- formulário estado ---

  abrirNovoEstado(): void {
    if (!this.regiaoAtual) {
      return;
    }
    this.estadoEmEdicao = null;
    this.formularioEstado = formularioEstadoVazio();
    this.erroFormularioEstado = '';
    this.modoFormularioEstado = 'novo';
  }

  abrirEdicaoEstado(estado: EstadoIntergrupo): void {
    this.estadoEmEdicao = estado;
    this.formularioEstado = { uf: estado.uf };
    this.erroFormularioEstado = '';
    this.modoFormularioEstado = 'edicao';
  }

  cancelarFormularioEstado(): void {
    this.modoFormularioEstado = 'fechado';
    this.estadoEmEdicao = null;
    this.erroFormularioEstado = '';
  }

  salvarEstado(): void {
    if (!this.regiaoAtual) {
      return;
    }
    if (!this.formularioEstado.uf) {
      this.erroFormularioEstado = 'Selecione uma UF.';
      return;
    }

    this.salvandoEstado = true;
    this.erroFormularioEstado = '';

    const request = { uf: this.formularioEstado.uf };
    const acao = this.estadoEmEdicao
      ? this.service.editarEstadoAdmin(this.regiaoAtual.id, this.estadoEmEdicao.id, request)
      : this.service.criarEstadoAdmin(this.regiaoAtual.id, request);

    acao.subscribe((envelope) => {
      this.salvandoEstado = false;

      if (envelope.success) {
        this.modoFormularioEstado = 'fechado';
        this.estadoEmEdicao = null;
        this.carregarEstrutura();
      } else if (envelope.error?.code === 'CONFLICT') {
        this.erroFormularioEstado =
          'Já existe um estado com esta UF nesta região. Edite o estado existente em vez de criar outro.';
      } else {
        this.erroFormularioEstado = envelope.error?.message ?? 'Não foi possível salvar o estado.';
      }
    });
  }

  moverEstado(estado: EstadoIntergrupo, direcao: 'cima' | 'baixo'): void {
    if (!this.regiaoAtual) {
      return;
    }
    const acao =
      direcao === 'cima'
        ? this.service.moverEstadoParaCima(this.regiaoAtual.id, estado.id)
        : this.service.moverEstadoParaBaixo(this.regiaoAtual.id, estado.id);
    acao.subscribe((envelope) => {
      if (envelope.success) {
        this.carregarEstrutura();
      }
    });
  }

  ehPrimeiroEstado(estado: EstadoIntergrupo): boolean {
    return this.estadosOrdenados[0]?.id === estado.id;
  }

  ehUltimoEstado(estado: EstadoIntergrupo): boolean {
    const lista = this.estadosOrdenados;
    return lista[lista.length - 1]?.id === estado.id;
  }

  // --- formulário grupo ---

  abrirNovoGrupo(): void {
    if (!this.estadoAtual) {
      return;
    }
    this.grupoEmEdicao = null;
    this.formularioGrupo = formularioGrupoVazio();
    this.cepGrupo = '';
    this.numeroGrupo = '';
    this.erroCepGrupo = '';
    this.erroFormularioGrupo = '';
    this.modoFormularioGrupo = 'novo';
  }

  abrirEdicaoGrupo(grupo: GrupoIntergrupo): void {
    this.grupoEmEdicao = grupo;
    this.formularioGrupo = {
      nome: grupo.nome,
      endereco: grupo.endereco,
      bairro: grupo.bairro ?? '',
      cidade: grupo.cidade,
      diasSemana: [...grupo.diasSemana],
      horario: grupo.horario,
      telefones: grupo.telefones.map((t) => ({ id: proximoIdTelefoneFormulario(), label: t.label ?? '', telefone: t.telefone })),
    };
    this.cepGrupo = '';
    this.numeroGrupo = '';
    this.erroCepGrupo = '';
    this.erroFormularioGrupo = '';
    this.modoFormularioGrupo = 'edicao';
  }

  cancelarFormularioGrupo(): void {
    this.modoFormularioGrupo = 'fechado';
    this.grupoEmEdicao = null;
    this.erroFormularioGrupo = '';
  }

  diaSelecionado(dia: DiaSemana): boolean {
    return this.formularioGrupo.diasSemana.includes(dia);
  }

  alternarDiaSemana(dia: DiaSemana, marcado: boolean): void {
    if (marcado) {
      if (!this.formularioGrupo.diasSemana.includes(dia)) {
        this.formularioGrupo.diasSemana = [...this.formularioGrupo.diasSemana, dia];
      }
    } else {
      this.formularioGrupo.diasSemana = this.formularioGrupo.diasSemana.filter((d) => d !== dia);
    }
  }

  adicionarTelefoneGrupo(): void {
    this.formularioGrupo.telefones = [
      ...this.formularioGrupo.telefones,
      { id: proximoIdTelefoneFormulario(), label: '', telefone: '' },
    ];
  }

  removerTelefoneGrupo(id: string): void {
    this.formularioGrupo.telefones = this.formularioGrupo.telefones.filter((t) => t.id !== id);
  }

  buscarCepGrupo(): void {
    this.buscarEnderecoPorCep(
      this.cepGrupo,
      this.numeroGrupo,
      (valor) => (this.buscandoCepGrupo = valor),
      (valor) => (this.erroCepGrupo = valor),
      (endereco, logradouroComNumero) => {
        this.formularioGrupo.endereco = logradouroComNumero || this.formularioGrupo.endereco;
        this.formularioGrupo.bairro = endereco.bairro ?? this.formularioGrupo.bairro;
        this.formularioGrupo.cidade = [endereco.localidade, endereco.uf].filter(Boolean).join(' - ') || this.formularioGrupo.cidade;
      },
    );
  }

  buscarCepEscritorio(): void {
    this.buscarEnderecoPorCep(
      this.cepEscritorio,
      this.numeroEscritorio,
      (valor) => (this.buscandoCepEscritorio = valor),
      (valor) => (this.erroCepEscritorio = valor),
      (endereco, logradouroComNumero) => {
        this.formularioEscritorio.endereco = logradouroComNumero || this.formularioEscritorio.endereco;
        this.formularioEscritorio.bairro = endereco.bairro ?? this.formularioEscritorio.bairro;
        this.formularioEscritorio.cidade =
          [endereco.localidade, endereco.uf].filter(Boolean).join(' - ') || this.formularioEscritorio.cidade;
      },
    );
  }

  private buscarEnderecoPorCep(
    cep: string,
    numero: string,
    setBuscando: (valor: boolean) => void,
    setErro: (valor: string) => void,
    aoEncontrar: (endereco: EnderecoViaCep, logradouroComNumero: string) => void,
  ): void {
    const cepLimpo = cep.replace(/\D/g, '');
    if (cepLimpo.length !== 8) {
      setErro('CEP inválido — precisa ter 8 dígitos.');
      return;
    }

    setBuscando(true);
    setErro('');

    this.http
      .get<EnderecoViaCep>(`https://viacep.com.br/ws/${cepLimpo}/json/`)
      .pipe(catchError(() => of(null)))
      .subscribe((endereco) => {
        setBuscando(false);

        if (!endereco || endereco.erro) {
          setErro('CEP não encontrado.');
          return;
        }

        const logradouroComNumero = [endereco.logradouro, numero.trim()].filter(Boolean).join(', ');
        aoEncontrar(endereco, logradouroComNumero);
      });
  }

  salvarGrupo(): void {
    if (!this.estadoAtual) {
      return;
    }
    if (
      !this.formularioGrupo.nome.trim() ||
      !this.formularioGrupo.endereco.trim() ||
      !this.formularioGrupo.cidade.trim() ||
      !this.formularioGrupo.horario.trim() ||
      this.formularioGrupo.diasSemana.length === 0
    ) {
      this.erroFormularioGrupo = 'Preencha todos os campos obrigatórios, incluindo ao menos um dia da semana.';
      return;
    }

    const telefonesInvalidos = this.formularioGrupo.telefones.some((t) => !t.telefone.trim());
    if (telefonesInvalidos) {
      this.erroFormularioGrupo = 'Remova ou preencha os telefones incompletos.';
      return;
    }

    this.salvandoGrupo = true;
    this.erroFormularioGrupo = '';

    const request = {
      nome: this.formularioGrupo.nome.trim(),
      endereco: this.formularioGrupo.endereco.trim(),
      bairro: this.formularioGrupo.bairro.trim() || undefined,
      cidade: this.formularioGrupo.cidade.trim(),
      diasSemana: this.formularioGrupo.diasSemana,
      horario: this.formularioGrupo.horario.trim(),
      telefones: this.formularioGrupo.telefones.map((t) => ({
        label: t.label.trim() || undefined,
        telefone: t.telefone.trim(),
      })),
    };

    const acao = this.grupoEmEdicao
      ? this.service.editarGrupoAdmin(this.estadoAtual.id, this.grupoEmEdicao.id, request)
      : this.service.criarGrupoAdmin(this.estadoAtual.id, request);

    acao.subscribe((envelope) => {
      this.salvandoGrupo = false;

      if (envelope.success) {
        this.modoFormularioGrupo = 'fechado';
        this.grupoEmEdicao = null;
        this.carregarEstrutura();
      } else {
        this.erroFormularioGrupo = envelope.error?.message ?? 'Não foi possível salvar o grupo.';
      }
    });
  }

  moverGrupo(grupo: GrupoIntergrupo, direcao: 'cima' | 'baixo'): void {
    if (!this.estadoAtual) {
      return;
    }
    const acao =
      direcao === 'cima'
        ? this.service.moverGrupoParaCima(this.estadoAtual.id, grupo.id)
        : this.service.moverGrupoParaBaixo(this.estadoAtual.id, grupo.id);
    acao.subscribe((envelope) => {
      if (envelope.success) {
        this.carregarEstrutura();
      }
    });
  }

  ehPrimeiroGrupo(grupo: GrupoIntergrupo): boolean {
    return this.gruposOrdenados[0]?.id === grupo.id;
  }

  ehUltimoGrupo(grupo: GrupoIntergrupo): boolean {
    const lista = this.gruposOrdenados;
    return lista[lista.length - 1]?.id === grupo.id;
  }

  // --- formulário escritório de serviço ---

  abrirNovoEscritorio(): void {
    this.escritorioEmEdicao = null;
    this.formularioEscritorio = formularioEscritorioVazio();
    this.cepEscritorio = '';
    this.numeroEscritorio = '';
    this.erroCepEscritorio = '';
    this.erroFormularioEscritorio = '';
    this.modoFormularioEscritorio = 'novo';
  }

  abrirEdicaoEscritorio(escritorio: EscritorioServico): void {
    this.escritorioEmEdicao = escritorio;
    this.formularioEscritorio = {
      nome: escritorio.nome,
      endereco: escritorio.endereco,
      complemento: escritorio.complemento ?? '',
      bairro: escritorio.bairro ?? '',
      cidade: escritorio.cidade,
      emailsTexto: escritorio.emails.join(', '),
      telefones: escritorio.telefones.map((t) => ({ id: proximoIdTelefoneFormulario(), label: t.label ?? '', telefone: t.telefone })),
    };
    this.cepEscritorio = '';
    this.numeroEscritorio = '';
    this.erroCepEscritorio = '';
    this.erroFormularioEscritorio = '';
    this.modoFormularioEscritorio = 'edicao';
  }

  cancelarFormularioEscritorio(): void {
    this.modoFormularioEscritorio = 'fechado';
    this.escritorioEmEdicao = null;
    this.erroFormularioEscritorio = '';
  }

  adicionarTelefoneEscritorio(): void {
    this.formularioEscritorio.telefones = [
      ...this.formularioEscritorio.telefones,
      { id: proximoIdTelefoneFormulario(), label: '', telefone: '' },
    ];
  }

  removerTelefoneEscritorio(id: string): void {
    this.formularioEscritorio.telefones = this.formularioEscritorio.telefones.filter((t) => t.id !== id);
  }

  salvarEscritorio(): void {
    if (
      !this.formularioEscritorio.nome.trim() ||
      !this.formularioEscritorio.endereco.trim() ||
      !this.formularioEscritorio.cidade.trim()
    ) {
      this.erroFormularioEscritorio = 'Preencha todos os campos obrigatórios.';
      return;
    }

    const telefonesInvalidos = this.formularioEscritorio.telefones.some((t) => !t.telefone.trim());
    if (telefonesInvalidos) {
      this.erroFormularioEscritorio = 'Remova ou preencha os telefones incompletos.';
      return;
    }

    this.salvandoEscritorio = true;
    this.erroFormularioEscritorio = '';

    const emails = this.formularioEscritorio.emailsTexto
      .split(',')
      .map((email) => email.trim())
      .filter((email) => email.length > 0);

    const request = {
      nome: this.formularioEscritorio.nome.trim(),
      endereco: this.formularioEscritorio.endereco.trim(),
      complemento: this.formularioEscritorio.complemento.trim() || undefined,
      bairro: this.formularioEscritorio.bairro.trim() || undefined,
      cidade: this.formularioEscritorio.cidade.trim(),
      emails,
      telefones: this.formularioEscritorio.telefones.map((t) => ({
        label: t.label.trim() || undefined,
        telefone: t.telefone.trim(),
      })),
    };

    const acao = this.escritorioEmEdicao
      ? this.service.editarEscritorioServicoAdmin(this.escritorioEmEdicao.id, request)
      : this.service.criarEscritorioServicoAdmin(request);

    acao.subscribe((envelope) => {
      this.salvandoEscritorio = false;

      if (envelope.success) {
        this.modoFormularioEscritorio = 'fechado';
        this.escritorioEmEdicao = null;
        this.carregarEstrutura();
      } else {
        this.erroFormularioEscritorio = envelope.error?.message ?? 'Não foi possível salvar o escritório.';
      }
    });
  }

  moverEscritorio(escritorio: EscritorioServico, direcao: 'cima' | 'baixo'): void {
    const acao =
      direcao === 'cima'
        ? this.service.moverEscritorioServicoParaCima(escritorio.id)
        : this.service.moverEscritorioServicoParaBaixo(escritorio.id);
    acao.subscribe((envelope) => {
      if (envelope.success) {
        this.carregarEstrutura();
      }
    });
  }

  ehPrimeiroEscritorio(escritorio: EscritorioServico): boolean {
    return this.escritoriosOrdenados[0]?.id === escritorio.id;
  }

  ehUltimoEscritorio(escritorio: EscritorioServico): boolean {
    const lista = this.escritoriosOrdenados;
    return lista[lista.length - 1]?.id === escritorio.id;
  }

  // --- exclusão com confirmação em cascata ---

  pedirConfirmacaoExclusaoRegiao(regiao: RegiaoIntergrupo): void {
    this.exclusaoAlvo = { tipo: 'regiao', regiao };
    this.erroExclusao = '';
  }

  pedirConfirmacaoExclusaoEstado(estado: EstadoIntergrupo): void {
    this.exclusaoAlvo = { tipo: 'estado', estado };
    this.erroExclusao = '';
  }

  pedirConfirmacaoExclusaoGrupo(grupo: GrupoIntergrupo): void {
    this.exclusaoAlvo = { tipo: 'grupo', grupo };
    this.erroExclusao = '';
  }

  pedirConfirmacaoExclusaoEscritorio(escritorio: EscritorioServico): void {
    this.exclusaoAlvo = { tipo: 'escritorio', escritorio };
    this.erroExclusao = '';
  }

  cancelarExclusao(): void {
    this.exclusaoAlvo = null;
    this.erroExclusao = '';
  }

  mensagemConfirmacaoExclusao(): string {
    if (!this.exclusaoAlvo) {
      return '';
    }

    if (this.exclusaoAlvo.tipo === 'regiao') {
      const regiao = this.exclusaoAlvo.regiao;
      const totalEstados = regiao.estados.length;
      const totalGrupos = regiao.estados.reduce((soma, estado) => soma + estado.grupos.length, 0);
      if (totalEstados === 0) {
        return `Excluir a região "${regiao.nome}" permanentemente?`;
      }
      return `Excluir a região "${regiao.nome}" vai remover também ${totalEstados} estado(s) e ${totalGrupos} grupo(s) associados. Esta ação não pode ser desfeita. Confirma?`;
    }

    if (this.exclusaoAlvo.tipo === 'estado') {
      const estado = this.exclusaoAlvo.estado;
      const totalGrupos = estado.grupos.length;
      if (totalGrupos === 0) {
        return `Excluir o estado "${estado.nome}" permanentemente?`;
      }
      return `Excluir o estado "${estado.nome}" vai remover também ${totalGrupos} grupo(s) associados. Esta ação não pode ser desfeita. Confirma?`;
    }

    if (this.exclusaoAlvo.tipo === 'grupo') {
      return `Excluir o grupo "${this.exclusaoAlvo.grupo.nome}" permanentemente?`;
    }

    return `Excluir o escritório de serviço "${this.exclusaoAlvo.escritorio.nome}" permanentemente?`;
  }

  confirmarExclusao(): void {
    if (!this.exclusaoAlvo) {
      return;
    }

    this.excluindo = true;
    this.erroExclusao = '';

    if (this.exclusaoAlvo.tipo === 'regiao') {
      const regiao = this.exclusaoAlvo.regiao;
      this.service.excluirRegiaoAdmin(regiao.id).subscribe((envelope) => {
        this.excluindo = false;
        if (envelope.success) {
          this.exclusaoAlvo = null;
          if (this.regiaoAtual?.id === regiao.id) {
            this.irParaRegioes();
          }
          this.carregarEstrutura();
        } else {
          this.erroExclusao = envelope.error?.message ?? 'Não foi possível excluir a região.';
        }
      });
      return;
    }

    if (this.exclusaoAlvo.tipo === 'estado') {
      const estado = this.exclusaoAlvo.estado;
      if (!this.regiaoAtual) {
        this.excluindo = false;
        return;
      }
      this.service.excluirEstadoAdmin(this.regiaoAtual.id, estado.id).subscribe((envelope) => {
        this.excluindo = false;
        if (envelope.success) {
          this.exclusaoAlvo = null;
          if (this.estadoAtual?.id === estado.id) {
            this.irParaEstados();
          }
          this.carregarEstrutura();
        } else {
          this.erroExclusao = envelope.error?.message ?? 'Não foi possível excluir o estado.';
        }
      });
      return;
    }

    if (this.exclusaoAlvo.tipo === 'grupo') {
      const grupo = this.exclusaoAlvo.grupo;
      if (!this.estadoAtual) {
        this.excluindo = false;
        return;
      }
      this.service.excluirGrupoAdmin(this.estadoAtual.id, grupo.id).subscribe((envelope) => {
        this.excluindo = false;
        if (envelope.success) {
          this.exclusaoAlvo = null;
          this.carregarEstrutura();
        } else {
          this.erroExclusao = envelope.error?.message ?? 'Não foi possível excluir o grupo.';
        }
      });
      return;
    }

    const escritorio = this.exclusaoAlvo.escritorio;
    this.service.excluirEscritorioServicoAdmin(escritorio.id).subscribe((envelope) => {
      this.excluindo = false;
      if (envelope.success) {
        this.exclusaoAlvo = null;
        this.carregarEstrutura();
      } else {
        this.erroExclusao = envelope.error?.message ?? 'Não foi possível excluir o escritório.';
      }
    });
  }
}
