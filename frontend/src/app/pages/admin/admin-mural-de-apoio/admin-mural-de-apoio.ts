import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { mapearErrosCampo } from '../../../services/api-envelope';
import { MuralDeApoioService } from '../../../services/mural-de-apoio/mural-de-apoio';
import { BannerMuralAdmin, CriarBannerMuralRequest } from '../../../services/mural-de-apoio/mural-de-apoio.model';
import { Imagem } from '../../../services/imagem/imagem';
import { ImagemResponse } from '../../../services/imagem/imagem.model';

interface FormularioBanner {
  imagemId: string;
  altText: string;
  legenda: string;
  linkDestino: string;
  ativo: boolean;
}

function formularioVazio(): FormularioBanner {
  return { imagemId: '', altText: '', legenda: '', linkDestino: '', ativo: true };
}

@Component({
  selector: 'app-admin-mural-de-apoio',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-mural-de-apoio.html',
  styleUrl: './admin-mural-de-apoio.css',
})
export class AdminMuralDeApoio implements OnInit {
  private readonly muralService = inject(MuralDeApoioService);
  private readonly imagemService = inject(Imagem);

  carregando = true;
  erroCarregamento = '';
  banners: BannerMuralAdmin[] = [];

  modo: 'lista' | 'formulario' = 'lista';
  bannerEmEdicao: BannerMuralAdmin | null = null;
  formulario: FormularioBanner = formularioVazio();
  imagemSelecionadaUrl: string | null = null;
  enviandoImagem = false;
  erroUploadImagem = '';
  salvando = false;
  erroFormulario = '';
  errosCampo: Record<string, string> = {};

  confirmandoExclusao: string | null = null;
  excluindo = false;
  erroExclusao = '';

  ngOnInit(): void {
    this.carregarBanners();
  }

  private carregarBanners(): void {
    this.carregando = true;
    this.erroCarregamento = '';

    this.muralService.listarTodosAdmin().subscribe((envelope) => {
      this.carregando = false;

      if (envelope.success && envelope.data) {
        this.banners = envelope.data;
      } else {
        this.banners = [];
        this.erroCarregamento = envelope.error?.message ?? 'Não foi possível carregar os banners.';
      }
    });
  }

  abrirNovo(): void {
    this.bannerEmEdicao = null;
    this.formulario = formularioVazio();
    this.imagemSelecionadaUrl = null;
    this.erroUploadImagem = '';
    this.erroFormulario = '';
    this.errosCampo = {};
    this.modo = 'formulario';
  }

  abrirEdicao(banner: BannerMuralAdmin): void {
    this.bannerEmEdicao = banner;
    this.formulario = {
      imagemId: banner.imagemId,
      altText: banner.altText,
      legenda: banner.legenda ?? '',
      linkDestino: banner.linkDestino ?? '',
      ativo: banner.ativo,
    };
    this.imagemSelecionadaUrl = banner.imagemUrl ?? null;
    this.erroUploadImagem = '';
    this.erroFormulario = '';
    this.errosCampo = {};
    this.modo = 'formulario';
  }

  cancelar(): void {
    this.modo = 'lista';
    this.bannerEmEdicao = null;
    this.erroFormulario = '';
    this.errosCampo = {};
  }

  onArquivoSelecionado(event: Event): void {
    const input = event.target as HTMLInputElement;
    const arquivo = input.files && input.files.length > 0 ? input.files[0] : null;
    if (!arquivo) return;

    this.enviandoImagem = true;
    this.erroUploadImagem = '';

    this.imagemService.uploadImagem({ arquivo, alt: this.formulario.altText.trim() || arquivo.name }).subscribe((envelope) => {
      this.enviandoImagem = false;

      if (envelope.success && envelope.data) {
        const imagem: ImagemResponse = envelope.data;
        this.formulario.imagemId = imagem.id;
        this.imagemSelecionadaUrl = imagem.url;
      } else {
        this.erroUploadImagem = envelope.error?.message ?? 'Não foi possível enviar a imagem.';
      }

      input.value = '';
    });
  }

  salvar(): void {
    this.errosCampo = {};

    if (!this.formulario.imagemId.trim()) {
      this.errosCampo['imagemId'] = 'Envie uma imagem antes de salvar.';
    }
    if (!this.formulario.altText.trim()) {
      this.errosCampo['altText'] = 'Texto alternativo é obrigatório.';
    }
    if (Object.keys(this.errosCampo).length > 0) {
      this.erroFormulario = this.errosCampo['imagemId'] ?? this.errosCampo['altText'];
      return;
    }

    this.salvando = true;
    this.erroFormulario = '';

    const requestBase: CriarBannerMuralRequest = {
      imagemId: this.formulario.imagemId.trim(),
      altText: this.formulario.altText.trim(),
      legenda: this.formulario.legenda.trim() || undefined,
      linkDestino: this.formulario.linkDestino.trim() || undefined,
    };

    const acao = this.bannerEmEdicao
      ? this.muralService.editarBannerAdmin(this.bannerEmEdicao.id, { ...requestBase, ativo: this.formulario.ativo })
      : this.muralService.criarBannerAdmin(requestBase);

    acao.subscribe((envelope) => {
      this.salvando = false;

      if (envelope.success) {
        this.modo = 'lista';
        this.bannerEmEdicao = null;
        this.carregarBanners();
      } else {
        this.erroFormulario = envelope.error?.message ?? 'Não foi possível salvar o banner.';
        this.errosCampo = mapearErrosCampo(envelope.error?.fields);
      }
    });
  }

  alternarAtivo(banner: BannerMuralAdmin): void {
    const request = {
      imagemId: banner.imagemId,
      altText: banner.altText,
      legenda: banner.legenda ?? undefined,
      linkDestino: banner.linkDestino ?? undefined,
      ativo: !banner.ativo,
    };

    this.muralService.editarBannerAdmin(banner.id, request).subscribe((envelope) => {
      if (envelope.success && envelope.data) {
        banner.ativo = envelope.data.ativo;
      } else {
        this.erroCarregamento = envelope.error?.message ?? 'Não foi possível atualizar o status do banner.';
      }
    });
  }

  pedirConfirmacaoExclusao(banner: BannerMuralAdmin): void {
    this.confirmandoExclusao = banner.id;
    this.erroExclusao = '';
  }

  cancelarExclusao(): void {
    this.confirmandoExclusao = null;
    this.erroExclusao = '';
  }

  confirmarExclusao(banner: BannerMuralAdmin): void {
    this.excluindo = true;
    this.erroExclusao = '';

    this.muralService.excluirBannerAdmin(banner.id).subscribe((envelope) => {
      this.excluindo = false;
      this.confirmandoExclusao = null;

      if (envelope.success) {
        this.banners = this.banners.filter((b) => b.id !== banner.id);
      } else {
        this.erroExclusao = envelope.error?.message ?? 'Não foi possível excluir o banner.';
      }
    });
  }
}
