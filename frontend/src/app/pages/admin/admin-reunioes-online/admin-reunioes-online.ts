import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, ElementRef, OnInit, PLATFORM_ID, ViewChild, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { SeletorHora12h } from '../../../components/seletor-hora-12h/seletor-hora-12h';
import { mapearErrosCampo, mensagemDeErro } from '../../../services/api-envelope';
import { compararHorarios } from '../../../services/reuniao/reuniao-horario';
import { DiaSemanaAgendaReuniao } from '../../../services/reuniao/reuniao.model';
import { ReuniaoOnlineService } from '../../../services/reuniao-online/reuniao-online';
import {
  AgendaItemReuniaoOnline,
  CriarReuniaoOnlineRequest,
  ReuniaoOnline,
} from '../../../services/reuniao-online/reuniao-online.model';

interface FormularioReuniaoOnline {
  nome: string;
  telefone: string;
  agenda: AgendaFormularioItem[];
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

function formularioVazio(): FormularioReuniaoOnline {
  return {
    nome: '',
    telefone: '',
    agenda: agendaFormularioVazia(),
  };
}

const TELEFONE_DIGITOS_REGEX = /^(?:55)?\d{10,11}$/;

function telefoneFormatoValido(valor: string): boolean {
  const digitos = valor.replace(/\D/g, '');
  return TELEFONE_DIGITOS_REGEX.test(digitos);
}

@Component({
  selector: 'app-admin-reunioes-online',
  standalone: true,
  imports: [CommonModule, FormsModule, SeletorHora12h],
  templateUrl: './admin-reunioes-online.html',
  styleUrl: './admin-reunioes-online.css',
})
export class AdminReunioesOnline implements OnInit {
  private readonly reuniaoOnlineService = inject(ReuniaoOnlineService);
  private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));

  carregando = true;
  erroCarregamento = '';
  reunioesOnline: ReuniaoOnline[] = [];

  modo: 'lista' | 'formulario' = 'lista';
  reuniaoOnlineEmEdicao: ReuniaoOnline | null = null;
  formulario: FormularioReuniaoOnline = formularioVazio();
  salvando = false;
  erroFormulario = '';
  errosCampo: Record<string, string> = {};

  confirmandoExclusao: string | null = null;
  excluindo = false;
  erroExclusao = '';

  @ViewChild('formularioSecao') formularioSecaoRef?: ElementRef<HTMLElement>;
  @ViewChild('campoNome') campoNomeRef?: ElementRef<HTMLInputElement>;

  ngOnInit(): void {
    this.carregarReunioesOnline();
  }

  private carregarReunioesOnline(): void {
    this.carregando = true;
    this.erroCarregamento = '';

    this.reuniaoOnlineService.listarReunioesOnlineAdmin().subscribe((envelope) => {
      this.carregando = false;

      if (envelope.success && envelope.data) {
        this.reunioesOnline = envelope.data;
      } else {
        this.reunioesOnline = [];
        this.erroCarregamento = envelope.error?.message ?? 'Não foi possível carregar as reuniões online.';
      }
    });
  }

  abrirNovo(): void {
    this.reuniaoOnlineEmEdicao = null;
    this.formulario = formularioVazio();
    this.erroFormulario = '';
    this.errosCampo = {};
    this.modo = 'formulario';
    this.focarFormulario();
  }

  abrirEdicao(reuniaoOnline: ReuniaoOnline): void {
    this.reuniaoOnlineEmEdicao = reuniaoOnline;
    this.formulario = {
      nome: reuniaoOnline.nome,
      telefone: reuniaoOnline.telefone ?? '',
      agenda: this.agendaParaFormulario(reuniaoOnline),
    };
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
      secao?.scrollIntoView?.({ behavior: reducedMotion ? 'auto' : 'smooth', block: 'start' });
      this.campoNomeRef?.nativeElement.focus({ preventScroll: true });
    });
  }

  cancelar(): void {
    this.modo = 'lista';
    this.reuniaoOnlineEmEdicao = null;
    this.erroFormulario = '';
    this.errosCampo = {};
  }

  validarTelefone(): void {
    const valor = this.formulario.telefone.trim();
    if (!valor) {
      delete this.errosCampo['telefone'];
      return;
    }
    if (!telefoneFormatoValido(valor)) {
      this.errosCampo['telefone'] = 'Número inválido — use DDD + número, com ou sem o 55 do Brasil.';
      return;
    }
    delete this.errosCampo['telefone'];
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

  resumoAgenda(reuniaoOnline: ReuniaoOnline): string {
    return this.agendaAgrupada(reuniaoOnline.agenda)
      .map((item) => `${ROTULO_DIA[item.diaSemana]} ${item.horarios.join(', ')}`)
      .join(' · ');
  }

  private construirErroFormulario(): string {
    const rotulos: Record<string, string> = {
      nome: 'Nome do grupo',
      agenda: 'Agenda (dias e horários)',
      telefone: 'Telefone',
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

    if (!this.formulario.nome.trim()) {
      this.errosCampo['nome'] = 'Nome do grupo é obrigatório.';
    }
    const agenda: AgendaItemReuniaoOnline[] = this.formulario.agenda
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
    if (Object.keys(this.errosCampo).length > 0) {
      this.erroFormulario = this.construirErroFormulario();
      return;
    }

    this.validarTelefone();
    if (this.errosCampo['telefone']) {
      this.erroFormulario = `Corrija o campo "Telefone" destacado abaixo: ${this.errosCampo['telefone']}`;
      return;
    }

    this.salvando = true;
    this.erroFormulario = '';

    const request: CriarReuniaoOnlineRequest = {
      nome: this.formulario.nome.trim(),
      telefone: this.formulario.telefone.trim() || undefined,
      agenda,
    };

    const acao = this.reuniaoOnlineEmEdicao
      ? this.reuniaoOnlineService.editarReuniaoOnlineAdmin(this.reuniaoOnlineEmEdicao.id, request)
      : this.reuniaoOnlineService.criarReuniaoOnlineAdmin(request);

    acao.subscribe((envelope) => {
      this.salvando = false;

      if (envelope.success && envelope.data) {
        const salva = envelope.data;
        const reunioesOnline = this.reuniaoOnlineEmEdicao
          ? this.reunioesOnline.map((r) => (r.id === salva.id ? salva : r))
          : [...this.reunioesOnline, salva];
        reunioesOnline.sort((a, b) => a.nome.localeCompare(b.nome));
        this.reunioesOnline = reunioesOnline;
        this.modo = 'lista';
        this.reuniaoOnlineEmEdicao = null;
      } else {
        this.erroFormulario = mensagemDeErro(envelope.error, 'Não foi possível salvar a reunião online.');
        this.errosCampo = mapearErrosCampo(envelope.error?.fields);
      }
    });
  }

  private agendaAgrupada(agenda: AgendaItemReuniaoOnline[]): Array<{ diaSemana: DiaSemanaAgendaReuniao; horarios: string[] }> {
    return DIAS_AGENDA.flatMap(({ diaSemana }) => {
      const horarios = agenda
        .filter((item) => item.diaSemana === diaSemana)
        .map((item) => item.horario)
        .sort(compararHorarios);
      return horarios.length ? [{ diaSemana, horarios }] : [];
    });
  }

  private agendaParaFormulario(reuniaoOnline: ReuniaoOnline): AgendaFormularioItem[] {
    return DIAS_AGENDA.map((dia) => {
      const horarios = reuniaoOnline.agenda
        .filter((item) => item.diaSemana === dia.diaSemana)
        .map((item) => item.horario)
        .sort(compararHorarios);
      return { ...dia, selecionado: horarios.length > 0, horarios };
    });
  }

  pedirConfirmacaoExclusao(reuniaoOnline: ReuniaoOnline): void {
    this.confirmandoExclusao = reuniaoOnline.id;
    this.erroExclusao = '';
  }

  cancelarExclusao(): void {
    this.confirmandoExclusao = null;
    this.erroExclusao = '';
  }

  confirmarExclusao(reuniaoOnline: ReuniaoOnline): void {
    this.excluindo = true;
    this.erroExclusao = '';

    this.reuniaoOnlineService.excluirReuniaoOnlineAdmin(reuniaoOnline.id).subscribe((envelope) => {
      this.excluindo = false;
      this.confirmandoExclusao = null;

      if (envelope.success) {
        this.reunioesOnline = this.reunioesOnline.filter((r) => r.id !== reuniaoOnline.id);
      } else {
        this.erroExclusao = envelope.error?.message ?? 'Não foi possível excluir a reunião online.';
      }
    });
  }
}
