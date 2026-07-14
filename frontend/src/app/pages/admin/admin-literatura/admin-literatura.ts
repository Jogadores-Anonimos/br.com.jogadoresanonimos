import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { Arquivo } from '../../../services/arquivo';
import { ArquivoResponse } from '../../../services/arquivo.model';

interface FormularioArquivo {
  titulo: string;
  descricao: string;
}

function formularioVazio(): FormularioArquivo {
  return { titulo: '', descricao: '' };
}

@Component({
  selector: 'app-admin-literatura',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-literatura.html',
  styleUrl: './admin-literatura.css',
})
export class AdminLiteratura implements OnInit {
  private readonly arquivoService = inject(Arquivo);

  carregando = true;
  erroCarregamento = '';
  arquivos: ArquivoResponse[] = [];

  modo: 'lista' | 'formulario' | 'upload' = 'lista';
  arquivoEmEdicao: ArquivoResponse | null = null;
  formulario: FormularioArquivo = formularioVazio();
  salvando = false;
  erroFormulario = '';

  arquivoSelecionado: File | null = null;
  tituloUpload = '';
  descricaoUpload = '';
  enviando = false;
  erroUpload = '';

  confirmandoExclusao: string | null = null;
  excluindo = false;
  erroExclusao = '';

  ngOnInit(): void {
    this.carregarArquivos();
  }

  private carregarArquivos(): void {
    this.carregando = true;
    this.erroCarregamento = '';

    this.arquivoService.listarArquivosAdmin().subscribe((envelope) => {
      this.carregando = false;

      if (envelope.success && envelope.data) {
        this.arquivos = envelope.data;
      } else {
        this.arquivos = [];
        this.erroCarregamento = envelope.error?.message ?? 'Não foi possível carregar os arquivos.';
      }
    });
  }

  abrirNovoUpload(): void {
    this.arquivoSelecionado = null;
    this.tituloUpload = '';
    this.descricaoUpload = '';
    this.erroUpload = '';
    this.modo = 'upload';
  }

  onArquivoSelecionado(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.arquivoSelecionado = input.files && input.files.length > 0 ? input.files[0] : null;
  }

  enviarUpload(): void {
    if (!this.arquivoSelecionado) {
      this.erroUpload = 'Selecione um arquivo PDF.';
      return;
    }
    if (!this.tituloUpload.trim()) {
      this.erroUpload = 'Título é obrigatório.';
      return;
    }

    this.enviando = true;
    this.erroUpload = '';

    this.arquivoService
      .uploadArquivo({
        arquivo: this.arquivoSelecionado,
        titulo: this.tituloUpload.trim(),
        descricao: this.descricaoUpload.trim() || undefined,
      })
      .subscribe((envelope) => {
        this.enviando = false;

        if (envelope.success) {
          this.modo = 'lista';
          this.carregarArquivos();
        } else {
          this.erroUpload = envelope.error?.message ?? 'Não foi possível enviar o arquivo.';
        }
      });
  }

  abrirEdicao(arquivo: ArquivoResponse): void {
    this.arquivoEmEdicao = arquivo;
    this.formulario = {
      titulo: arquivo.titulo,
      descricao: arquivo.descricao ?? '',
    };
    this.erroFormulario = '';
    this.modo = 'formulario';
  }

  cancelar(): void {
    this.modo = 'lista';
    this.arquivoEmEdicao = null;
    this.erroFormulario = '';
  }

  salvar(): void {
    if (!this.arquivoEmEdicao) return;

    if (!this.formulario.titulo.trim()) {
      this.erroFormulario = 'Título é obrigatório.';
      return;
    }

    this.salvando = true;
    this.erroFormulario = '';

    this.arquivoService
      .editarArquivo(this.arquivoEmEdicao.id, {
        titulo: this.formulario.titulo.trim(),
        descricao: this.formulario.descricao.trim() || undefined,
      })
      .subscribe((envelope) => {
        this.salvando = false;

        if (envelope.success) {
          this.modo = 'lista';
          this.arquivoEmEdicao = null;
          this.carregarArquivos();
        } else {
          this.erroFormulario = envelope.error?.message ?? 'Não foi possível salvar as alterações.';
        }
      });
  }

  pedirConfirmacaoExclusao(arquivo: ArquivoResponse): void {
    this.confirmandoExclusao = arquivo.id;
    this.erroExclusao = '';
  }

  cancelarExclusao(): void {
    this.confirmandoExclusao = null;
    this.erroExclusao = '';
  }

  confirmarExclusao(arquivo: ArquivoResponse): void {
    this.excluindo = true;
    this.erroExclusao = '';

    this.arquivoService.deletarArquivo(arquivo.id).subscribe((envelope) => {
      this.excluindo = false;
      this.confirmandoExclusao = null;

      if (envelope.success) {
        this.arquivos = this.arquivos.filter((a) => a.id !== arquivo.id);
      } else {
        this.erroExclusao = envelope.error?.message ?? 'Não foi possível excluir o arquivo.';
      }
    });
  }

  formatarTamanho(bytes: number): string {
    if (bytes < 1024) return `${bytes} B`;
    const kb = bytes / 1024;
    if (kb < 1024) return `${kb.toFixed(0)} KB`;
    return `${(kb / 1024).toFixed(1)} MB`;
  }
}
