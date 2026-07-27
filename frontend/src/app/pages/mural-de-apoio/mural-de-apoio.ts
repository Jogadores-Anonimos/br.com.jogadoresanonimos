import { Component, OnDestroy, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { BannerMuralApoio, MuralDeApoioService } from '../../services/mural-de-apoio/mural-de-apoio';
import { paraYoutubeEmbedUrl, paraYoutubeThumbnailUrl } from '../../services/mural-de-apoio/youtube.util';

const INTERVALO_AUTOPLAY_MS = 7000;

@Component({
  selector: 'app-mural-de-apoio',
  standalone: true,
  imports: [RouterLink, ResourceLayout],
  templateUrl: './mural-de-apoio.html',
  styleUrl: './mural-de-apoio.css',
})
export class MuralDeApoio implements OnInit, OnDestroy {
  banners: BannerMuralApoio[] = [];
  carregando = true;
  erro = false;
  slideAtual = 0;
  autoplayLigado = false;
  autoplayPausadoTemporariamente = false;
  prefereMovimentoReduzido = false;
  anuncioSlide = '';

  private readonly banneresTocando = new Set<string>();
  private timerAutoplay: ReturnType<typeof setInterval> | null = null;
  private readonly onVisibilityChange = (): void => {
    if (document.hidden) {
      this.pausarTemporariamente();
    } else {
      this.retomarTemporariamente();
    }
  };

  constructor(
    private readonly muralDeApoioService: MuralDeApoioService,
    private readonly sanitizer: DomSanitizer,
  ) {}

  embedUrlVideo(banner: BannerMuralApoio): SafeResourceUrl | null {
    const embedUrl = paraYoutubeEmbedUrl(banner.videoUrl);
    return embedUrl ? this.sanitizer.bypassSecurityTrustResourceUrl(embedUrl) : null;
  }

  thumbnailUrlVideo(banner: BannerMuralApoio): string | null {
    return paraYoutubeThumbnailUrl(banner.videoUrl);
  }

  videoEstaTocando(banner: BannerMuralApoio): boolean {
    return this.banneresTocando.has(banner.id);
  }

  tocarVideo(banner: BannerMuralApoio, evento: Event): void {
    evento.preventDefault();
    evento.stopPropagation();
    this.banneresTocando.add(banner.id);
  }

  ngOnInit(): void {
    this.prefereMovimentoReduzido = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    document.addEventListener('visibilitychange', this.onVisibilityChange);

    this.muralDeApoioService.getBannersCompleto().subscribe((resposta) => {
      this.carregando = false;

      if (!resposta.success || !resposta.data) {
        this.erro = true;
        return;
      }

      this.banners = resposta.data;
      this.atualizarAnuncio();
    });
  }

  ngOnDestroy(): void {
    document.removeEventListener('visibilitychange', this.onVisibilityChange);
    this.pararTimer();
  }

  irParaSlide(indice: number): void {
    this.slideAtual = indice;
    this.atualizarAnuncio();
  }

  slideAnterior(): void {
    this.slideAtual = (this.slideAtual - 1 + this.banners.length) % this.banners.length;
    this.atualizarAnuncio();
    this.pausarAutoplay();
  }

  proximoSlide(): void {
    this.slideAtual = (this.slideAtual + 1) % this.banners.length;
    this.atualizarAnuncio();
  }

  proximoSlideEUsuarioPausar(): void {
    this.proximoSlide();
    this.pausarAutoplay();
  }

  irParaSlideEUsuarioPausar(indice: number): void {
    this.irParaSlide(indice);
    this.pausarAutoplay();
  }

  alternarAutoplay(): void {
    if (this.autoplayLigado) {
      this.pausarAutoplay();
      return;
    }

    this.autoplayLigado = true;
    this.iniciarTimer();
  }

  pausarTemporariamente(): void {
    if (!this.autoplayLigado) {
      return;
    }

    this.autoplayPausadoTemporariamente = true;
    this.pararTimer();
  }

  retomarTemporariamente(): void {
    if (!this.autoplayLigado || !this.autoplayPausadoTemporariamente) {
      return;
    }

    this.autoplayPausadoTemporariamente = false;
    this.iniciarTimer();
  }

  private pausarAutoplay(): void {
    this.autoplayLigado = false;
    this.autoplayPausadoTemporariamente = false;
    this.pararTimer();
  }

  private iniciarTimer(): void {
    this.pararTimer();
    this.timerAutoplay = setInterval(() => {
      this.proximoSlide();
    }, INTERVALO_AUTOPLAY_MS);
  }

  private pararTimer(): void {
    if (this.timerAutoplay !== null) {
      clearInterval(this.timerAutoplay);
      this.timerAutoplay = null;
    }
  }

  private atualizarAnuncio(): void {
    const banner = this.banners[this.slideAtual];
    if (!banner) {
      return;
    }

    this.anuncioSlide = `Slide ${this.slideAtual + 1} de ${this.banners.length}: ${banner.altText}`;
  }
}
