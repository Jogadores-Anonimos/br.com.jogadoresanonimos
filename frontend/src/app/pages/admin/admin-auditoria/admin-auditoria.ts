import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AuditoriaService } from '../../../services/auditoria/auditoria';
import { AcaoAuditoria, AuditoriaFiltros, AuditoriaLogAdmin } from '../../../services/auditoria/auditoria.model';

const TAMANHO_PAGINA_PADRAO = 20;

interface OpcaoAcao {
  valor: AcaoAuditoria;
  label: string;
}

@Component({
  selector: 'app-admin-auditoria',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-auditoria.html',
  styleUrl: './admin-auditoria.css',
})
export class AdminAuditoria implements OnInit {
  private readonly auditoriaService = inject(AuditoriaService);

  readonly acoes: OpcaoAcao[] = [
    { valor: 'APROVAR', label: 'Aprovar' },
    { valor: 'REJEITAR', label: 'Rejeitar' },
    { valor: 'REVERTER', label: 'Reverter' },
    { valor: 'REMOVER', label: 'Remover' },
    { valor: 'EDITAR', label: 'Editar' },
  ];

  carregando = true;
  erroCarregamento = '';
  registros: AuditoriaLogAdmin[] = [];

  pagina = 0;
  tamanho = TAMANHO_PAGINA_PADRAO;
  totalElementos = 0;
  totalPaginas = 0;

  filtroEntidade = '';
  filtroAcao: AcaoAuditoria | '' = '';
  filtroDataInicio = '';
  filtroDataFim = '';

  ngOnInit(): void {
    this.carregarRegistros();
  }

  aplicarFiltros(): void {
    this.pagina = 0;
    this.carregarRegistros();
  }

  limparFiltros(): void {
    this.filtroEntidade = '';
    this.filtroAcao = '';
    this.filtroDataInicio = '';
    this.filtroDataFim = '';
    this.aplicarFiltros();
  }

  irParaPagina(pagina: number): void {
    if (pagina < 0 || pagina >= this.totalPaginas || pagina === this.pagina) {
      return;
    }
    this.pagina = pagina;
    this.carregarRegistros();
  }

  labelAcao(acao: AcaoAuditoria): string {
    return this.acoes.find((a) => a.valor === acao)?.label ?? acao;
  }

  private carregarRegistros(): void {
    this.carregando = true;
    this.erroCarregamento = '';

    const filtros: AuditoriaFiltros = {
      entidade: this.filtroEntidade.trim() || undefined,
      acao: this.filtroAcao || undefined,
      dataInicio: this.filtroDataInicio || undefined,
      dataFim: this.filtroDataFim || undefined,
    };

    this.auditoriaService.listarAdmin(filtros, this.pagina, this.tamanho).subscribe((envelope) => {
      this.carregando = false;

      if (envelope.success && envelope.data) {
        this.registros = envelope.data.conteudo;
        this.totalElementos = envelope.data.totalElementos;
        this.totalPaginas = envelope.data.totalPaginas;
      } else {
        this.registros = [];
        this.totalElementos = 0;
        this.totalPaginas = 0;
        this.erroCarregamento = envelope.error?.message ?? 'Não foi possível carregar o log de auditoria.';
      }
    });
  }
}
