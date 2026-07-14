import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { ReflexaoDoDiaService } from '../../../services/reflexao-do-dia';
import { ReflexaoDiaDoMes } from '../../../services/reflexao-dia.model';

interface MesOpcao {
  numero: number;
  nome: string;
}

const NOMES_MESES: string[] = [
  'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
  'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro',
];

@Component({
  selector: 'app-admin-reflexao-do-dia',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-reflexao-do-dia.html',
  styleUrl: './admin-reflexao-do-dia.css',
})
export class AdminReflexaoDoDia implements OnInit {
  private readonly reflexaoService = inject(ReflexaoDoDiaService);

  readonly meses: MesOpcao[] = NOMES_MESES.map((nome, indice) => ({ numero: indice + 1, nome }));

  mesSelecionado = new Date().getMonth() + 1;
  dias: ReflexaoDiaDoMes[] = [];

  carregando = true;
  erroCarregamento = '';

  diaEmEdicao: ReflexaoDiaDoMes | null = null;
  textoFormulario = '';
  salvando = false;
  erroFormulario = '';

  ngOnInit(): void {
    this.carregarMes();
  }

  selecionarMes(numeroMes: number): void {
    if (numeroMes === this.mesSelecionado) {
      return;
    }
    this.mesSelecionado = numeroMes;
    this.fecharFormulario();
    this.carregarMes();
  }

  private carregarMes(): void {
    this.carregando = true;
    this.erroCarregamento = '';

    this.reflexaoService.listarMesAdmin(this.mesSelecionado).subscribe((envelope) => {
      this.carregando = false;

      if (envelope.success && envelope.data) {
        this.dias = envelope.data;
      } else {
        this.dias = [];
        this.erroCarregamento = envelope.error?.message ?? 'Não foi possível carregar o calendário do mês.';
      }
    });
  }

  abrirDia(dia: ReflexaoDiaDoMes): void {
    this.diaEmEdicao = dia;
    this.textoFormulario = dia.texto ?? '';
    this.erroFormulario = '';
  }

  fecharFormulario(): void {
    this.diaEmEdicao = null;
    this.textoFormulario = '';
    this.erroFormulario = '';
  }

  salvar(): void {
    if (!this.diaEmEdicao) {
      return;
    }
    const texto = this.textoFormulario.trim();
    if (!texto) {
      this.erroFormulario = 'O texto da reflexão é obrigatório.';
      return;
    }

    this.salvando = true;
    this.erroFormulario = '';

    const dia = this.diaEmEdicao.dia;
    this.reflexaoService.salvarReflexaoAdmin(this.mesSelecionado, dia, { texto }).subscribe((envelope) => {
      this.salvando = false;

      if (envelope.success && envelope.data) {
        const indice = this.dias.findIndex((d) => d.dia === dia);
        if (indice !== -1) {
          this.dias[indice] = { dia, texto: envelope.data.texto };
        }
        this.fecharFormulario();
      } else {
        this.erroFormulario = envelope.error?.message ?? 'Não foi possível salvar a reflexão.';
      }
    });
  }

  nomeMesSelecionado(): string {
    return this.meses.find((m) => m.numero === this.mesSelecionado)?.nome ?? '';
  }
}
