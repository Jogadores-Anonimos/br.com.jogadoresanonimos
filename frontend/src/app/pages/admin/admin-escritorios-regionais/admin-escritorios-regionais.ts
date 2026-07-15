import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { catchError, of } from 'rxjs';

import { mapearErrosCampo } from '../../../services/api-envelope';
import { EscritorioRegionalService } from '../../../services/escritorio-regional/escritorio-regional';
import {
  AgendaItemGrupo,
  CriarEnderecoGrupoRequest,
  DiaSemana,
  EscritorioServico,
  EscritoriosRegionais,
  EstadoIntergrupo,
  GrupoIntergrupo,
  RegiaoIntergrupo,
  UNIDADES_FEDERATIVAS,
} from '../../../services/escritorio-regional/escritorio-regional.model';

type Aba = 'escritorios' | 'enderecos';

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

interface FormularioAgendaItem {
  id: string;
  diaSemana: DiaSemana | '';
  horario: string;
}

interface FormularioGrupo {
  nome: string;
  endereco: string;
  bairro: string;
  cidade: string;
  agenda: FormularioAgendaItem[];
  telefones: FormularioTelefone[];
}

interface FormularioEndereco {
  regiaoId: string;
  uf: string;
  nome: string;
  endereco: string;
  bairro: string;
  cidade: string;
  agenda: FormularioAgendaItem[];
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
  return { nome: '', endereco: '', bairro: '', cidade: '', agenda: [], telefones: [] };
}

function formularioEnderecoVazio(): FormularioEndereco {
  return { regiaoId: '', uf: '', nome: '', endereco: '', bairro: '', cidade: '', agenda: [], telefones: [] };
}

function formularioEscritorioVazio(): FormularioEscritorio {
  return { nome: '', endereco: '', complemento: '', bairro: '', cidade: '', emailsTexto: '', telefones: [] };
}

type ExclusaoAlvo =
  | { tipo: 'estado'; estado: EstadoIntergrupo }
  | { tipo: 'grupo'; grupo: GrupoIntergrupo };

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

function ufValidaEndereco(uf: string): boolean {
  return UNIDADES_FEDERATIVAS.some((u) => u.sigla === uf);
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

let contadorTelefoneFormulario = 0;
function proximoIdTelefoneFormulario(): string {
  contadorTelefoneFormulario += 1;
  return `form-telefone-${contadorTelefoneFormulario}`;
}

let contadorAgendaFormulario = 0;
function proximoIdAgendaFormulario(): string {
  contadorAgendaFormulario += 1;
  return `form-agenda-${contadorAgendaFormulario}`;
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

  aba: Aba = 'escritorios';

  nivel: Nivel = 'regioes';
  regiaoAtual: RegiaoIntergrupo | null = null;
  estadoAtual: EstadoIntergrupo | null = null;

  salvandoRegiao = false;
  erroFormularioRegiao = '';
  errosCampoRegiao: Record<string, string> = {};
  modoFormularioRegiao: 'fechado' | 'edicao' = 'fechado';
  regiaoEmEdicao: RegiaoIntergrupo | null = null;
  formularioRegiao: FormularioRegiao = formularioRegiaoVazio();

  salvandoEstado = false;
  erroFormularioEstado = '';
  errosCampoEstado: Record<string, string> = {};
  modoFormularioEstado: 'fechado' | 'novo' | 'edicao' = 'fechado';
  estadoEmEdicao: EstadoIntergrupo | null = null;
  formularioEstado: FormularioEstado = formularioEstadoVazio();

  salvandoGrupo = false;
  erroFormularioGrupo = '';
  errosCampoGrupo: Record<string, string> = {};
  modoFormularioGrupo: 'fechado' | 'novo' | 'edicao' = 'fechado';
  grupoEmEdicao: GrupoIntergrupo | null = null;
  formularioGrupo: FormularioGrupo = formularioGrupoVazio();

  cepGrupo = '';
  numeroGrupo = '';
  buscandoCepGrupo = false;
  erroCepGrupo = '';

  salvandoEndereco = false;
  erroFormularioEndereco = '';
  errosCampoEndereco: Record<string, string> = {};
  formularioEnderecoAberto = false;
  formularioEndereco: FormularioEndereco = formularioEnderecoVazio();

  cepEndereco = '';
  numeroEndereco = '';
  buscandoCepEndereco = false;
  erroCepEndereco = '';

  salvandoEscritorio = false;
  erroFormularioEscritorio = '';
  errosCampoEscritorio: Record<string, string> = {};
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

  get alvoEstado(): EstadoIntergrupo | null {
    return this.exclusaoAlvo?.tipo === 'estado' ? this.exclusaoAlvo.estado : null;
  }

  get alvoGrupo(): GrupoIntergrupo | null {
    return this.exclusaoAlvo?.tipo === 'grupo' ? this.exclusaoAlvo.grupo : null;
  }

  ngOnInit(): void {
    this.carregarEstrutura();
  }

  selecionarAba(aba: Aba): void {
    this.aba = aba;
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

  rotuloAgenda(grupo: GrupoIntergrupo): string {
    const ordem: DiaSemana[] = ['DOMINGO', 'SEGUNDA', 'TERCA', 'QUARTA', 'QUINTA', 'SEXTA', 'SABADO'];
    return ordem
      .filter((dia) => grupo.agenda.some((item) => item.diaSemana === dia))
      .map((dia) => {
        const rotuloDia = this.diasSemana.find((d) => d.valor === dia)?.rotulo ?? dia;
        const horario = grupo.agenda.find((item) => item.diaSemana === dia)?.horario ?? '';
        return `${rotuloDia}: ${horario}`;
      })
      .join(' · ');
  }

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

  abrirEdicaoRegiao(regiao: RegiaoIntergrupo): void {
    this.regiaoEmEdicao = regiao;
    this.formularioRegiao = { nome: regiao.nome };
    this.erroFormularioRegiao = '';
    this.errosCampoRegiao = {};
    this.modoFormularioRegiao = 'edicao';
  }

  cancelarFormularioRegiao(): void {
    this.modoFormularioRegiao = 'fechado';
    this.regiaoEmEdicao = null;
    this.erroFormularioRegiao = '';
    this.errosCampoRegiao = {};
  }

  salvarRegiao(): void {
    this.errosCampoRegiao = {};
    if (!this.formularioRegiao.nome.trim()) {
      this.erroFormularioRegiao = 'Nome é obrigatório.';
      this.errosCampoRegiao['nome'] = this.erroFormularioRegiao;
      return;
    }

    this.salvandoRegiao = true;
    this.erroFormularioRegiao = '';

    if (!this.regiaoEmEdicao) {
      this.salvandoRegiao = false;
      return;
    }

    const request = { nome: this.formularioRegiao.nome.trim() };
    this.service.editarRegiaoAdmin(this.regiaoEmEdicao.id, request).subscribe((envelope) => {
      this.salvandoRegiao = false;

      if (envelope.success && envelope.data) {
        const salva = envelope.data;
        this.dados = {
          ...this.dados,
          regioes: this.dados.regioes.map((r) => (r.id === salva.id ? { ...salva, estados: r.estados } : r)),
        };
        this.resincronizarSelecaoAtual();
        this.modoFormularioRegiao = 'fechado';
        this.regiaoEmEdicao = null;
      } else {
        this.erroFormularioRegiao = envelope.error?.message ?? 'Não foi possível salvar a região.';
        this.errosCampoRegiao = mapearErrosCampo(envelope.error?.fields);
      }
    });
  }

  moverRegiao(regiao: RegiaoIntergrupo, direcao: 'cima' | 'baixo'): void {
    const acao =
      direcao === 'cima' ? this.service.moverRegiaoParaCima(regiao.id) : this.service.moverRegiaoParaBaixo(regiao.id);
    acao.subscribe((envelope) => {
      if (!envelope.success) {
        return;
      }
      const ordenadas = this.regioesOrdenadas;
      const indiceAtual = ordenadas.findIndex((r) => r.id === regiao.id);
      const indiceVizinho = direcao === 'cima' ? indiceAtual - 1 : indiceAtual + 1;
      if (indiceAtual === -1 || indiceVizinho < 0 || indiceVizinho >= ordenadas.length) {
        return;
      }
      const ordemAtual = ordenadas[indiceAtual].ordem;
      const ordemVizinho = ordenadas[indiceVizinho].ordem;
      this.dados = {
        ...this.dados,
        regioes: this.dados.regioes.map((r) => {
          if (r.id === ordenadas[indiceAtual].id) return { ...r, ordem: ordemVizinho };
          if (r.id === ordenadas[indiceVizinho].id) return { ...r, ordem: ordemAtual };
          return r;
        }),
      };
    });
  }

  ehPrimeiraRegiao(regiao: RegiaoIntergrupo): boolean {
    return this.regioesOrdenadas[0]?.id === regiao.id;
  }

  ehUltimaRegiao(regiao: RegiaoIntergrupo): boolean {
    const lista = this.regioesOrdenadas;
    return lista[lista.length - 1]?.id === regiao.id;
  }

  abrirNovoEstado(): void {
    if (!this.regiaoAtual) {
      return;
    }
    this.estadoEmEdicao = null;
    this.formularioEstado = formularioEstadoVazio();
    this.erroFormularioEstado = '';
    this.errosCampoEstado = {};
    this.modoFormularioEstado = 'novo';
  }

  abrirEdicaoEstado(estado: EstadoIntergrupo): void {
    this.estadoEmEdicao = estado;
    this.formularioEstado = { uf: estado.uf };
    this.erroFormularioEstado = '';
    this.errosCampoEstado = {};
    this.modoFormularioEstado = 'edicao';
  }

  cancelarFormularioEstado(): void {
    this.modoFormularioEstado = 'fechado';
    this.estadoEmEdicao = null;
    this.erroFormularioEstado = '';
    this.errosCampoEstado = {};
  }

  salvarEstado(): void {
    if (!this.regiaoAtual) {
      return;
    }
    this.errosCampoEstado = {};
    if (!this.formularioEstado.uf) {
      this.erroFormularioEstado = 'Selecione uma UF.';
      this.errosCampoEstado['uf'] = this.erroFormularioEstado;
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

      if (envelope.success && envelope.data) {
        const salvo = envelope.data;
        const regiaoId = this.regiaoAtual!.id;
        this.dados = {
          ...this.dados,
          regioes: this.dados.regioes.map((r) => {
            if (r.id !== regiaoId) return r;
            const estadoExiste = r.estados.some((e) => e.id === salvo.id);
            return {
              ...r,
              estados: estadoExiste
                ? r.estados.map((e) => (e.id === salvo.id ? { ...salvo, grupos: e.grupos } : e))
                : [...r.estados, salvo],
            };
          }),
        };
        this.resincronizarSelecaoAtual();
        this.modoFormularioEstado = 'fechado';
        this.estadoEmEdicao = null;
      } else if (envelope.error?.code === 'CONFLICT') {
        this.erroFormularioEstado =
          'Já existe um estado com esta UF nesta região. Edite o estado existente em vez de criar outro.';
        this.errosCampoEstado['uf'] = this.erroFormularioEstado;
      } else {
        this.erroFormularioEstado = envelope.error?.message ?? 'Não foi possível salvar o estado.';
        this.errosCampoEstado = mapearErrosCampo(envelope.error?.fields);
      }
    });
  }

  moverEstado(estado: EstadoIntergrupo, direcao: 'cima' | 'baixo'): void {
    if (!this.regiaoAtual) {
      return;
    }
    const regiaoId = this.regiaoAtual.id;
    const acao =
      direcao === 'cima'
        ? this.service.moverEstadoParaCima(regiaoId, estado.id)
        : this.service.moverEstadoParaBaixo(regiaoId, estado.id);
    acao.subscribe((envelope) => {
      if (!envelope.success) {
        return;
      }
      const ordenados = this.estadosOrdenados;
      const indiceAtual = ordenados.findIndex((e) => e.id === estado.id);
      const indiceVizinho = direcao === 'cima' ? indiceAtual - 1 : indiceAtual + 1;
      if (indiceAtual === -1 || indiceVizinho < 0 || indiceVizinho >= ordenados.length) {
        return;
      }
      const ordemAtual = ordenados[indiceAtual].ordem;
      const ordemVizinho = ordenados[indiceVizinho].ordem;
      this.dados = {
        ...this.dados,
        regioes: this.dados.regioes.map((r) => {
          if (r.id !== regiaoId) return r;
          return {
            ...r,
            estados: r.estados.map((e) => {
              if (e.id === ordenados[indiceAtual].id) return { ...e, ordem: ordemVizinho };
              if (e.id === ordenados[indiceVizinho].id) return { ...e, ordem: ordemAtual };
              return e;
            }),
          };
        }),
      };
      this.resincronizarSelecaoAtual();
    });
  }

  ehPrimeiroEstado(estado: EstadoIntergrupo): boolean {
    return this.estadosOrdenados[0]?.id === estado.id;
  }

  ehUltimoEstado(estado: EstadoIntergrupo): boolean {
    const lista = this.estadosOrdenados;
    return lista[lista.length - 1]?.id === estado.id;
  }

  abrirFormularioEndereco(): void {
    this.formularioEndereco = formularioEnderecoVazio();
    this.cepEndereco = '';
    this.numeroEndereco = '';
    this.erroCepEndereco = '';
    this.erroFormularioEndereco = '';
    this.errosCampoEndereco = {};
    this.formularioEnderecoAberto = true;
  }

  cancelarFormularioEndereco(): void {
    this.formularioEnderecoAberto = false;
    this.erroFormularioEndereco = '';
    this.errosCampoEndereco = {};
  }

  diaSelecionadoEndereco(dia: DiaSemana): boolean {
    return this.formularioEndereco.agenda.some((item) => item.diaSemana === dia);
  }

  horarioDoDiaEndereco(dia: DiaSemana): string {
    return this.formularioEndereco.agenda.find((item) => item.diaSemana === dia)?.horario ?? '';
  }

  alternarDiaSemanaEndereco(dia: DiaSemana, selecionado: boolean): void {
    if (selecionado) {
      if (this.diaSelecionadoEndereco(dia)) {
        return;
      }
      this.formularioEndereco.agenda = [
        ...this.formularioEndereco.agenda,
        { id: proximoIdAgendaFormulario(), diaSemana: dia, horario: '' },
      ];
    } else {
      this.formularioEndereco.agenda = this.formularioEndereco.agenda.filter((item) => item.diaSemana !== dia);
    }
  }

  definirHorarioDoDiaEndereco(dia: DiaSemana, horario: string): void {
    this.formularioEndereco.agenda = this.formularioEndereco.agenda.map((item) =>
      item.diaSemana === dia ? { ...item, horario } : item,
    );
  }

  adicionarTelefoneEndereco(): void {
    this.formularioEndereco.telefones = [
      ...this.formularioEndereco.telefones,
      { id: proximoIdTelefoneFormulario(), label: '', telefone: '' },
    ];
  }

  removerTelefoneEndereco(id: string): void {
    this.formularioEndereco.telefones = this.formularioEndereco.telefones.filter((t) => t.id !== id);
  }

  buscarCepEndereco(): void {
    this.buscarEnderecoPorCep(
      this.cepEndereco,
      this.numeroEndereco,
      (valor) => (this.buscandoCepEndereco = valor),
      (valor) => (this.erroCepEndereco = valor),
      (endereco, logradouroComNumero) => {
        this.formularioEndereco.endereco = logradouroComNumero || this.formularioEndereco.endereco;
        this.formularioEndereco.bairro = endereco.bairro ?? this.formularioEndereco.bairro;
        this.formularioEndereco.cidade = [endereco.localidade, endereco.uf].filter(Boolean).join(' - ') || this.formularioEndereco.cidade;
        this.formularioEndereco.uf = endereco.uf ?? this.formularioEndereco.uf;
      },
    );
  }

  salvarEndereco(): void {
    this.errosCampoEndereco = {};

    if (!this.formularioEndereco.regiaoId) {
      this.errosCampoEndereco['regiaoId'] = 'Selecione uma região.';
    }
    if (!ufValidaEndereco(this.formularioEndereco.uf)) {
      this.errosCampoEndereco['uf'] = 'Informe uma UF válida (preencha pelo CEP ou digite manualmente).';
    }
    if (!this.formularioEndereco.nome.trim()) {
      this.errosCampoEndereco['nome'] = 'Nome do grupo é obrigatório.';
    }
    if (!this.formularioEndereco.endereco.trim()) {
      this.errosCampoEndereco['endereco'] = 'Endereço é obrigatório.';
    }
    if (!this.formularioEndereco.cidade.trim()) {
      this.errosCampoEndereco['cidade'] = 'Cidade é obrigatória.';
    }
    if (this.formularioEndereco.agenda.length === 0) {
      this.errosCampoEndereco['agenda'] = 'Selecione ao menos um dia da semana.';
    }
    if (Object.keys(this.errosCampoEndereco).length > 0) {
      this.erroFormularioEndereco = 'Preencha todos os campos obrigatórios, incluindo ao menos um dia da semana.';
      return;
    }

    const horarioAusente = this.formularioEndereco.agenda.some((item) => !item.horario.trim());
    if (horarioAusente) {
      this.erroFormularioEndereco = 'Selecione um horário para cada dia da semana marcado.';
      this.errosCampoEndereco['agenda'] = this.erroFormularioEndereco;
      return;
    }

    const telefonesInvalidos = this.formularioEndereco.telefones.some((t) => !t.telefone.trim());
    if (telefonesInvalidos) {
      this.erroFormularioEndereco = 'Remova ou preencha os telefones incompletos.';
      this.errosCampoEndereco['telefones'] = this.erroFormularioEndereco;
      return;
    }

    this.salvandoEndereco = true;
    this.erroFormularioEndereco = '';

    const agenda: AgendaItemGrupo[] = this.formularioEndereco.agenda.map((item) => ({
      diaSemana: item.diaSemana as DiaSemana,
      horario: item.horario.trim(),
    }));

    const request: CriarEnderecoGrupoRequest = {
      uf: this.formularioEndereco.uf,
      nome: this.formularioEndereco.nome.trim(),
      endereco: this.formularioEndereco.endereco.trim(),
      bairro: this.formularioEndereco.bairro.trim() || undefined,
      cidade: this.formularioEndereco.cidade.trim(),
      agenda,
      telefones: this.formularioEndereco.telefones.map((t) => ({
        label: t.label.trim() || undefined,
        telefone: t.telefone.trim(),
      })),
    };

    const regiaoId = this.formularioEndereco.regiaoId;
    const uf = this.formularioEndereco.uf;

    this.service.criarEnderecoAdmin(regiaoId, request).subscribe((envelope) => {
      this.salvandoEndereco = false;

      if (envelope.success && envelope.data) {
        const grupoCriado = envelope.data;
        const regiao = this.dados.regioes.find((r) => r.id === regiaoId);
        const estadoExistente = regiao?.estados.find((e) => e.uf === uf);

        if (estadoExistente) {
          this.dados = {
            ...this.dados,
            regioes: this.dados.regioes.map((r) =>
              r.id !== regiaoId
                ? r
                : {
                    ...r,
                    estados: r.estados.map((e) =>
                      e.id === estadoExistente.id ? { ...e, grupos: [...e.grupos, grupoCriado] } : e,
                    ),
                  },
            ),
          };
          this.resincronizarSelecaoAtual();
          this.formularioEnderecoAberto = false;
        } else {
          this.carregarEstrutura();
          this.formularioEnderecoAberto = false;
        }
      } else {
        this.erroFormularioEndereco = envelope.error?.message ?? 'Não foi possível salvar o endereço.';
        this.errosCampoEndereco = mapearErrosCampo(envelope.error?.fields);
      }
    });
  }

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
    this.errosCampoGrupo = {};
    this.modoFormularioGrupo = 'novo';
  }

  abrirEdicaoGrupo(grupo: GrupoIntergrupo): void {
    this.grupoEmEdicao = grupo;
    this.formularioGrupo = {
      nome: grupo.nome,
      endereco: grupo.endereco,
      bairro: grupo.bairro ?? '',
      cidade: grupo.cidade,
      agenda: grupo.agenda.map((item) => ({ id: proximoIdAgendaFormulario(), diaSemana: item.diaSemana, horario: item.horario })),
      telefones: grupo.telefones.map((t) => ({ id: proximoIdTelefoneFormulario(), label: t.label ?? '', telefone: t.telefone })),
    };
    this.cepGrupo = '';
    this.numeroGrupo = '';
    this.erroCepGrupo = '';
    this.erroFormularioGrupo = '';
    this.errosCampoGrupo = {};
    this.modoFormularioGrupo = 'edicao';
  }

  cancelarFormularioGrupo(): void {
    this.modoFormularioGrupo = 'fechado';
    this.grupoEmEdicao = null;
    this.erroFormularioGrupo = '';
    this.errosCampoGrupo = {};
  }

  diaSelecionado(dia: DiaSemana): boolean {
    return this.formularioGrupo.agenda.some((item) => item.diaSemana === dia);
  }

  horarioDoDia(dia: DiaSemana): string {
    return this.formularioGrupo.agenda.find((item) => item.diaSemana === dia)?.horario ?? '';
  }

  alternarDiaSemana(dia: DiaSemana, selecionado: boolean): void {
    if (selecionado) {
      if (this.diaSelecionado(dia)) {
        return;
      }
      this.formularioGrupo.agenda = [
        ...this.formularioGrupo.agenda,
        { id: proximoIdAgendaFormulario(), diaSemana: dia, horario: '' },
      ];
    } else {
      this.formularioGrupo.agenda = this.formularioGrupo.agenda.filter((item) => item.diaSemana !== dia);
    }
  }

  definirHorarioDoDia(dia: DiaSemana, horario: string): void {
    this.formularioGrupo.agenda = this.formularioGrupo.agenda.map((item) =>
      item.diaSemana === dia ? { ...item, horario } : item,
    );
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
    this.errosCampoGrupo = {};

    if (!this.formularioGrupo.nome.trim()) {
      this.errosCampoGrupo['nome'] = 'Nome é obrigatório.';
    }
    if (!this.formularioGrupo.endereco.trim()) {
      this.errosCampoGrupo['endereco'] = 'Endereço é obrigatório.';
    }
    if (!this.formularioGrupo.cidade.trim()) {
      this.errosCampoGrupo['cidade'] = 'Cidade é obrigatória.';
    }
    if (this.formularioGrupo.agenda.length === 0) {
      this.errosCampoGrupo['agenda'] = 'Selecione ao menos um dia da semana.';
    }
    if (Object.keys(this.errosCampoGrupo).length > 0) {
      this.erroFormularioGrupo = 'Preencha todos os campos obrigatórios, incluindo ao menos um dia da semana.';
      return;
    }

    const horarioAusente = this.formularioGrupo.agenda.some((item) => !item.horario.trim());
    if (horarioAusente) {
      this.erroFormularioGrupo = 'Selecione um horário para cada dia da semana marcado.';
      this.errosCampoGrupo['agenda'] = this.erroFormularioGrupo;
      return;
    }

    const telefonesInvalidos = this.formularioGrupo.telefones.some((t) => !t.telefone.trim());
    if (telefonesInvalidos) {
      this.erroFormularioGrupo = 'Remova ou preencha os telefones incompletos.';
      this.errosCampoGrupo['telefones'] = this.erroFormularioGrupo;
      return;
    }

    this.salvandoGrupo = true;
    this.erroFormularioGrupo = '';

    const agenda: AgendaItemGrupo[] = this.formularioGrupo.agenda.map((item) => ({
      diaSemana: item.diaSemana as DiaSemana,
      horario: item.horario.trim(),
    }));

    const request = {
      nome: this.formularioGrupo.nome.trim(),
      endereco: this.formularioGrupo.endereco.trim(),
      bairro: this.formularioGrupo.bairro.trim() || undefined,
      cidade: this.formularioGrupo.cidade.trim(),
      agenda,
      telefones: this.formularioGrupo.telefones.map((t) => ({
        label: t.label.trim() || undefined,
        telefone: t.telefone.trim(),
      })),
    };

    const estadoId = this.estadoAtual.id;
    const acao = this.grupoEmEdicao
      ? this.service.editarGrupoAdmin(estadoId, this.grupoEmEdicao.id, request)
      : this.service.criarGrupoAdmin(estadoId, request);

    acao.subscribe((envelope) => {
      this.salvandoGrupo = false;

      if (envelope.success && envelope.data) {
        const salvo = envelope.data;
        this.dados = this.atualizarGruposDoEstado(estadoId, (grupos) => {
          const existe = grupos.some((g) => g.id === salvo.id);
          return existe ? grupos.map((g) => (g.id === salvo.id ? salvo : g)) : [...grupos, salvo];
        });
        this.resincronizarSelecaoAtual();
        this.modoFormularioGrupo = 'fechado';
        this.grupoEmEdicao = null;
      } else {
        this.erroFormularioGrupo = envelope.error?.message ?? 'Não foi possível salvar o grupo.';
        this.errosCampoGrupo = mapearErrosCampo(envelope.error?.fields);
      }
    });
  }

  private atualizarGruposDoEstado(
    estadoId: string,
    transformar: (grupos: GrupoIntergrupo[]) => GrupoIntergrupo[],
  ): EscritoriosRegionais {
    return {
      ...this.dados,
      regioes: this.dados.regioes.map((r) => ({
        ...r,
        estados: r.estados.map((e) => (e.id === estadoId ? { ...e, grupos: transformar(e.grupos) } : e)),
      })),
    };
  }

  moverGrupo(grupo: GrupoIntergrupo, direcao: 'cima' | 'baixo'): void {
    if (!this.estadoAtual) {
      return;
    }
    const estadoId = this.estadoAtual.id;
    const acao =
      direcao === 'cima'
        ? this.service.moverGrupoParaCima(estadoId, grupo.id)
        : this.service.moverGrupoParaBaixo(estadoId, grupo.id);
    acao.subscribe((envelope) => {
      if (!envelope.success) {
        return;
      }
      const ordenados = this.gruposOrdenados;
      const indiceAtual = ordenados.findIndex((g) => g.id === grupo.id);
      const indiceVizinho = direcao === 'cima' ? indiceAtual - 1 : indiceAtual + 1;
      if (indiceAtual === -1 || indiceVizinho < 0 || indiceVizinho >= ordenados.length) {
        return;
      }
      const ordemAtual = ordenados[indiceAtual].ordem;
      const ordemVizinho = ordenados[indiceVizinho].ordem;
      this.dados = this.atualizarGruposDoEstado(estadoId, (grupos) =>
        grupos.map((g) => {
          if (g.id === ordenados[indiceAtual].id) return { ...g, ordem: ordemVizinho };
          if (g.id === ordenados[indiceVizinho].id) return { ...g, ordem: ordemAtual };
          return g;
        }),
      );
      this.resincronizarSelecaoAtual();
    });
  }

  ehPrimeiroGrupo(grupo: GrupoIntergrupo): boolean {
    return this.gruposOrdenados[0]?.id === grupo.id;
  }

  ehUltimoGrupo(grupo: GrupoIntergrupo): boolean {
    const lista = this.gruposOrdenados;
    return lista[lista.length - 1]?.id === grupo.id;
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
    this.errosCampoEscritorio = {};
    this.modoFormularioEscritorio = 'edicao';
  }

  cancelarFormularioEscritorio(): void {
    this.modoFormularioEscritorio = 'fechado';
    this.escritorioEmEdicao = null;
    this.erroFormularioEscritorio = '';
    this.errosCampoEscritorio = {};
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
    this.errosCampoEscritorio = {};

    if (!this.formularioEscritorio.nome.trim()) {
      this.errosCampoEscritorio['nome'] = 'Nome é obrigatório.';
    }
    if (!this.formularioEscritorio.endereco.trim()) {
      this.errosCampoEscritorio['endereco'] = 'Endereço é obrigatório.';
    }
    if (!this.formularioEscritorio.cidade.trim()) {
      this.errosCampoEscritorio['cidade'] = 'Cidade é obrigatória.';
    }
    if (Object.keys(this.errosCampoEscritorio).length > 0) {
      this.erroFormularioEscritorio = 'Preencha todos os campos obrigatórios.';
      return;
    }

    const telefonesInvalidos = this.formularioEscritorio.telefones.some((t) => !t.telefone.trim());
    if (telefonesInvalidos) {
      this.erroFormularioEscritorio = 'Remova ou preencha os telefones incompletos.';
      this.errosCampoEscritorio['telefones'] = this.erroFormularioEscritorio;
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

    if (!this.escritorioEmEdicao) {
      this.salvandoEscritorio = false;
      return;
    }

    this.service.editarEscritorioServicoAdmin(this.escritorioEmEdicao.id, request).subscribe((envelope) => {
      this.salvandoEscritorio = false;

      if (envelope.success && envelope.data) {
        const salvo = envelope.data;
        this.dados = {
          ...this.dados,
          escritorios: this.dados.escritorios.map((e) => (e.id === salvo.id ? salvo : e)),
        };
        this.modoFormularioEscritorio = 'fechado';
        this.escritorioEmEdicao = null;
      } else {
        this.erroFormularioEscritorio = envelope.error?.message ?? 'Não foi possível salvar o escritório.';
        this.errosCampoEscritorio = mapearErrosCampo(envelope.error?.fields);
      }
    });
  }

  pedirConfirmacaoExclusaoEstado(estado: EstadoIntergrupo): void {
    this.exclusaoAlvo = { tipo: 'estado', estado };
    this.erroExclusao = '';
  }

  pedirConfirmacaoExclusaoGrupo(grupo: GrupoIntergrupo): void {
    this.exclusaoAlvo = { tipo: 'grupo', grupo };
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

    if (this.exclusaoAlvo.tipo === 'estado') {
      const estado = this.exclusaoAlvo.estado;
      const totalGrupos = estado.grupos.length;
      if (totalGrupos === 0) {
        return `Excluir o estado "${estado.nome}" permanentemente?`;
      }
      return `Excluir o estado "${estado.nome}" vai remover também ${totalGrupos} grupo(s) associados. Esta ação não pode ser desfeita. Confirma?`;
    }

    return `Excluir o grupo "${this.exclusaoAlvo.grupo.nome}" permanentemente?`;
  }

  confirmarExclusao(): void {
    if (!this.exclusaoAlvo) {
      return;
    }

    this.excluindo = true;
    this.erroExclusao = '';

    if (this.exclusaoAlvo.tipo === 'estado') {
      const estado = this.exclusaoAlvo.estado;
      if (!this.regiaoAtual) {
        this.excluindo = false;
        return;
      }
      const regiaoId = this.regiaoAtual.id;
      this.service.excluirEstadoAdmin(regiaoId, estado.id).subscribe((envelope) => {
        this.excluindo = false;
        if (envelope.success) {
          this.exclusaoAlvo = null;
          this.dados = {
            ...this.dados,
            regioes: this.dados.regioes.map((r) =>
              r.id !== regiaoId ? r : { ...r, estados: r.estados.filter((e) => e.id !== estado.id) },
            ),
          };
          if (this.estadoAtual?.id === estado.id) {
            this.irParaEstados();
          }
          this.resincronizarSelecaoAtual();
        } else {
          this.erroExclusao = envelope.error?.message ?? 'Não foi possível excluir o estado.';
        }
      });
      return;
    }

    const grupo = this.exclusaoAlvo.grupo;
    if (!this.estadoAtual) {
      this.excluindo = false;
      return;
    }
    const estadoId = this.estadoAtual.id;
    this.service.excluirGrupoAdmin(estadoId, grupo.id).subscribe((envelope) => {
      this.excluindo = false;
      if (envelope.success) {
        this.exclusaoAlvo = null;
        this.dados = this.atualizarGruposDoEstado(estadoId, (grupos) => grupos.filter((g) => g.id !== grupo.id));
        this.resincronizarSelecaoAtual();
      } else {
        this.erroExclusao = envelope.error?.message ?? 'Não foi possível excluir o grupo.';
      }
    });
  }
}
