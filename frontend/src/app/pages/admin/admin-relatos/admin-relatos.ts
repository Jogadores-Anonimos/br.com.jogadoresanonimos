import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { RelatosService, StatusRelato } from '../../../services/relatos/relatos';
import { RelatoAdmin } from '../../../services/relatos/relato.model';

const TEXTO_TRUNCADO_LIMITE = 320;
const TEXTO_MIN_LENGTH = 50;
const TEXTO_MAX_LENGTH = 10000;

interface AbaStatus {
  status: StatusRelato;
  label: string;
}

@Component({
  selector: 'app-admin-relatos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-relatos.html',
  styleUrl: './admin-relatos.css',
})
export class AdminRelatos implements OnInit {
  private readonly relatosService = inject(RelatosService);

  readonly textoTruncadoLimite = TEXTO_TRUNCADO_LIMITE;
  readonly textoMinLength = TEXTO_MIN_LENGTH;
  readonly textoMaxLength = TEXTO_MAX_LENGTH;

  readonly abas: AbaStatus[] = [
    { status: 'pending', label: 'Pendentes' },
    { status: 'approved', label: 'Aprovados' },
    { status: 'rejected', label: 'Rejeitados' },
  ];

  abaAtiva: StatusRelato = 'pending';
  carregando = true;
  erroCarregamento = '';
  relatos: RelatoAdmin[] = [];

  private readonly expandidos = new Set<string>();
  private readonly emEdicao = new Map<string, string>();
  private readonly processando = new Set<string>();

  erroPorRelato = new Map<string, string>();

  confirmandoExclusao: string | null = null;

  ngOnInit(): void {
    this.carregarRelatos();
  }

  selecionarAba(status: StatusRelato): void {
    if (this.abaAtiva === status) {
      return;
    }
    this.abaAtiva = status;
    this.confirmandoExclusao = null;
    this.carregarRelatos();
  }

  private carregarRelatos(): void {
    this.carregando = true;
    this.erroCarregamento = '';
    this.expandidos.clear();
    this.emEdicao.clear();
    this.erroPorRelato.clear();

    this.relatosService.listarPorStatusAdmin(this.abaAtiva).subscribe((envelope) => {
      this.carregando = false;

      if (envelope.success && envelope.data) {
        this.relatos = envelope.data.conteudo;
      } else {
        this.relatos = [];
        this.erroCarregamento = envelope.error?.message ?? 'Não foi possível carregar os relatos.';
      }
    });
  }

  nomeParaExibir(relato: RelatoAdmin): string {
    const nome = relato.nomeExibicao?.trim();
    return nome ? nome : 'Anônimo';
  }

  precisaTruncar(relato: RelatoAdmin): boolean {
    return relato.texto.length > this.textoTruncadoLimite && !this.estaEditando(relato);
  }

  estaExpandido(relato: RelatoAdmin): boolean {
    return this.expandidos.has(relato.id);
  }

  textoExibido(relato: RelatoAdmin): string {
    if (!this.precisaTruncar(relato) || this.estaExpandido(relato)) {
      return relato.texto;
    }
    return relato.texto.slice(0, this.textoTruncadoLimite).trimEnd() + '…';
  }

  alternarExpandido(relato: RelatoAdmin): void {
    if (this.expandidos.has(relato.id)) {
      this.expandidos.delete(relato.id);
    } else {
      this.expandidos.add(relato.id);
    }
  }

  estaEditando(relato: RelatoAdmin): boolean {
    return this.emEdicao.has(relato.id);
  }

  textoEdicao(relato: RelatoAdmin): string {
    return this.emEdicao.get(relato.id) ?? relato.texto;
  }

  onTextoEdicaoChange(relato: RelatoAdmin, valor: string): void {
    this.emEdicao.set(relato.id, valor);
  }

  iniciarEdicao(relato: RelatoAdmin): void {
    this.emEdicao.set(relato.id, relato.texto);
    this.expandidos.add(relato.id);
    this.erroPorRelato.delete(relato.id);
  }

  cancelarEdicao(relato: RelatoAdmin): void {
    this.emEdicao.delete(relato.id);
    this.erroPorRelato.delete(relato.id);
  }

  estaProcessando(relato: RelatoAdmin): boolean {
    return this.processando.has(relato.id);
  }

  aprovar(relato: RelatoAdmin): void {
    this.executar(relato, this.relatosService.aprovarRelatoAdmin(relato.id));
  }

  rejeitar(relato: RelatoAdmin): void {
    this.executar(relato, this.relatosService.rejeitarRelatoAdmin(relato.id));
  }

  reverterParaPendente(relato: RelatoAdmin): void {
    this.executar(relato, this.relatosService.reverterRelatoAdmin(relato.id));
  }

  salvarEAprovar(relato: RelatoAdmin): void {
    const texto = this.textoEdicao(relato).trim();

    if (texto.length < this.textoMinLength || texto.length > this.textoMaxLength) {
      this.erroPorRelato.set(
        relato.id,
        `O texto deve ter entre ${this.textoMinLength} e ${this.textoMaxLength} caracteres.`,
      );
      return;
    }

    this.processando.add(relato.id);
    this.erroPorRelato.delete(relato.id);

    this.relatosService.editarRelatoAdmin(relato.id, { texto }).subscribe((envelopeEdicao) => {
      if (!envelopeEdicao.success) {
        this.processando.delete(relato.id);
        this.erroPorRelato.set(relato.id, envelopeEdicao.error?.message ?? 'Não foi possível salvar as alterações.');
        return;
      }

      this.relatosService.aprovarRelatoAdmin(relato.id).subscribe((envelopeAprovar) => {
        this.processando.delete(relato.id);

        if (envelopeAprovar.success) {
          this.emEdicao.delete(relato.id);
          this.removerDaListaAtual(relato.id);
        } else {
          this.erroPorRelato.set(relato.id, envelopeAprovar.error?.message ?? 'Não foi possível aprovar o relato.');
        }
      });
    });
  }

  pedirConfirmacaoExclusao(relato: RelatoAdmin): void {
    this.confirmandoExclusao = relato.id;
  }

  cancelarExclusao(): void {
    this.confirmandoExclusao = null;
  }

  confirmarExclusao(relato: RelatoAdmin): void {
    this.processando.add(relato.id);

    this.relatosService.excluirRelatoAdmin(relato.id).subscribe((envelope) => {
      this.processando.delete(relato.id);
      this.confirmandoExclusao = null;

      if (envelope.success) {
        this.removerDaListaAtual(relato.id);
      } else {
        this.erroPorRelato.set(relato.id, envelope.error?.message ?? 'Não foi possível excluir o relato.');
      }
    });
  }

  private executar(relato: RelatoAdmin, acao: ReturnType<RelatosService['aprovarRelatoAdmin']>): void {
    this.processando.add(relato.id);
    this.erroPorRelato.delete(relato.id);

    acao.subscribe((envelope) => {
      this.processando.delete(relato.id);

      if (envelope.success) {
        this.removerDaListaAtual(relato.id);
      } else {
        this.erroPorRelato.set(relato.id, envelope.error?.message ?? 'Não foi possível atualizar o relato.');
      }
    });
  }

  private removerDaListaAtual(id: string): void {
    this.relatos = this.relatos.filter((r) => r.id !== id);
    this.expandidos.delete(id);
    this.emEdicao.delete(id);
    this.erroPorRelato.delete(id);
  }
}
