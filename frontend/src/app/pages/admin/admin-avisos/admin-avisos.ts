import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AvisosService } from '../../../services/avisos';
import { AvisoAdmin, CriarAvisoRequest } from '../../../services/aviso.model';

interface FormularioAviso {
  titulo: string;
  corpo: string;
  fixado: boolean;
  dataExpiracao: string;
}

function formularioVazio(): FormularioAviso {
  return { titulo: '', corpo: '', fixado: false, dataExpiracao: '' };
}

// yyyy-MM-dd -> dd/mm/aaaa, pra exibir no input mascarado.
function paraExibicaoBr(isoData: string): string {
  const [ano, mes, dia] = isoData.split('-');
  return ano && mes && dia ? `${dia}/${mes}/${ano}` : '';
}

@Component({
  selector: 'app-admin-avisos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-avisos.html',
  styleUrl: './admin-avisos.css',
})
export class AdminAvisos implements OnInit {
  private readonly avisosService = inject(AvisosService);

  carregando = true;
  erroCarregamento = '';
  avisos: AvisoAdmin[] = [];

  modo: 'lista' | 'formulario' = 'lista';
  avisoEmEdicao: AvisoAdmin | null = null;
  formulario: FormularioAviso = formularioVazio();
  salvando = false;
  erroFormulario = '';

  confirmandoExclusao: string | null = null;
  excluindo = false;
  erroExclusao = '';

  ngOnInit(): void {
    this.carregarAvisos();
  }

  private carregarAvisos(): void {
    this.carregando = true;
    this.erroCarregamento = '';

    this.avisosService.listarTodosAdmin().subscribe((envelope) => {
      this.carregando = false;

      if (envelope.success && envelope.data) {
        this.avisos = envelope.data;
      } else {
        this.avisos = [];
        this.erroCarregamento = envelope.error?.message ?? 'Não foi possível carregar os avisos.';
      }
    });
  }

  dataExpiracaoTexto = '';

  abrirNovo(): void {
    this.avisoEmEdicao = null;
    this.formulario = formularioVazio();
    this.dataExpiracaoTexto = '';
    this.erroFormulario = '';
    this.modo = 'formulario';
  }

  abrirEdicao(aviso: AvisoAdmin): void {
    this.avisoEmEdicao = aviso;
    this.formulario = {
      titulo: aviso.titulo,
      corpo: aviso.corpo,
      fixado: aviso.fixado,
      dataExpiracao: aviso.dataExpiracao ? aviso.dataExpiracao.slice(0, 10) : '',
    };
    this.dataExpiracaoTexto = paraExibicaoBr(this.formulario.dataExpiracao);
    this.erroFormulario = '';
    this.modo = 'formulario';
  }

  // Máscara dd/mm/aaaa: insere as barras conforme digita e só grava em
  // formulario.dataExpiracao (yyyy-MM-dd) quando os 3 grupos estiverem
  // completos e formarem uma data válida — evita depender do formato
  // nativo do <input type="date">, que segue o idioma do SO/navegador.
  onDataExpiracaoTextoChange(valor: string): void {
    const digitos = valor.replace(/\D/g, '').slice(0, 8);
    let formatado = digitos;
    if (digitos.length > 4) {
      formatado = `${digitos.slice(0, 2)}/${digitos.slice(2, 4)}/${digitos.slice(4)}`;
    } else if (digitos.length > 2) {
      formatado = `${digitos.slice(0, 2)}/${digitos.slice(2)}`;
    }
    this.dataExpiracaoTexto = formatado;

    if (digitos.length === 8) {
      const dia = digitos.slice(0, 2);
      const mes = digitos.slice(2, 4);
      const ano = digitos.slice(4, 8);
      const data = new Date(`${ano}-${mes}-${dia}T00:00:00`);
      const valida =
        !Number.isNaN(data.getTime()) &&
        data.getDate() === Number(dia) &&
        data.getMonth() + 1 === Number(mes);
      this.formulario.dataExpiracao = valida ? `${ano}-${mes}-${dia}` : '';
    } else {
      this.formulario.dataExpiracao = '';
    }
  }

  cancelar(): void {
    this.modo = 'lista';
    this.avisoEmEdicao = null;
    this.dataExpiracaoTexto = '';
    this.erroFormulario = '';
  }

  salvar(): void {
    if (!this.formulario.titulo.trim() || !this.formulario.corpo.trim()) {
      this.erroFormulario = 'Título e corpo são obrigatórios.';
      return;
    }

    this.salvando = true;
    this.erroFormulario = '';

    const request: CriarAvisoRequest = {
      titulo: this.formulario.titulo.trim(),
      corpo: this.formulario.corpo.trim(),
      fixado: this.formulario.fixado,
      // Backend espera Instant (ISO-8601 com hora); <input type="date">
      // só dá "yyyy-MM-dd" — expira no fim daquele dia (horário local).
      dataExpiracao: this.formulario.dataExpiracao
        ? new Date(`${this.formulario.dataExpiracao}T23:59:59`).toISOString()
        : null,
    };

    const acao = this.avisoEmEdicao
      ? this.avisosService.editarAvisoAdmin(this.avisoEmEdicao.id, request)
      : this.avisosService.criarAvisoAdmin(request);

    acao.subscribe((envelope) => {
      this.salvando = false;

      if (envelope.success) {
        this.modo = 'lista';
        this.avisoEmEdicao = null;
        this.carregarAvisos();
      } else {
        this.erroFormulario = envelope.error?.message ?? 'Não foi possível salvar o aviso.';
      }
    });
  }

  pedirConfirmacaoExclusao(aviso: AvisoAdmin): void {
    this.confirmandoExclusao = aviso.id;
    this.erroExclusao = '';
  }

  cancelarExclusao(): void {
    this.confirmandoExclusao = null;
    this.erroExclusao = '';
  }

  confirmarExclusao(aviso: AvisoAdmin): void {
    this.excluindo = true;
    this.erroExclusao = '';

    this.avisosService.excluirAvisoAdmin(aviso.id).subscribe((envelope) => {
      this.excluindo = false;
      this.confirmandoExclusao = null;

      if (envelope.success) {
        this.avisos = this.avisos.filter((a) => a.id !== aviso.id);
      } else {
        this.erroExclusao = envelope.error?.message ?? 'Não foi possível excluir o aviso.';
      }
    });
  }
}
