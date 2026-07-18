import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

import { BannerMuralApoio, MuralDeApoioService } from '../../services/mural-de-apoio/mural-de-apoio';
import { paraYoutubeEmbedUrl, paraYoutubeThumbnailUrl } from '../../services/mural-de-apoio/youtube.util';
import { Aviso, AvisosService } from '../../services/avisos/avisos';
import { RelatoPublicoResumo, RelatosService } from '../../services/relatos/relatos';
import { ReflexaoPreview } from '../../components/reflexao-preview/reflexao-preview';
import { WHATSAPP_CHAT_URL } from '../../config/whatsapp.config';

const QUANTIDADE_AVISOS_TEASER = 3;
const QUANTIDADE_RELATOS_TEASER = 3;
const TRECHO_RELATO_LIMITE = 70;

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, ReflexaoPreview],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home implements OnInit {
  readonly whatsappChatUrl = WHATSAPP_CHAT_URL;

  bannersMural: BannerMuralApoio[] = [];
  carregandoMural = true;
  erroMural = false;

  avisos: Aviso[] = [];
  carregandoAvisos = true;
  erroAvisos = false;

  relatosPreview: RelatoPublicoResumo[] = [];
  carregandoRelatos = true;
  erroRelatos = false;

  private readonly banneresTocando = new Set<string>();

  constructor(
    private readonly muralDeApoioService: MuralDeApoioService,
    private readonly avisosService: AvisosService,
    private readonly relatosService: RelatosService,
    private readonly sanitizer: DomSanitizer,
  ) {}

  ngOnInit(): void {
    this.muralDeApoioService.getBannersDestaque().subscribe({
      next: (resposta) => {
        this.carregandoMural = false;

        if (!resposta.success || !resposta.data) {
          this.erroMural = true;
          return;
        }

        this.bannersMural = resposta.data;
      },
      error: () => {
        this.carregandoMural = false;
        this.erroMural = true;
      },
    });

    this.avisosService.listarAvisos().subscribe({
      next: (resposta) => {
        this.carregandoAvisos = false;

        if (!resposta.success || !resposta.data) {
          this.erroAvisos = true;
          return;
        }

        this.avisos = resposta.data.slice(0, QUANTIDADE_AVISOS_TEASER);
      },
      error: () => {
        this.carregandoAvisos = false;
        this.erroAvisos = true;
      },
    });

    this.relatosService.listarRelatosAprovados().subscribe({
      next: (resposta) => {
        this.carregandoRelatos = false;

        if (!resposta.success || !resposta.data) {
          this.erroRelatos = true;
          return;
        }

        this.relatosPreview = resposta.data.slice(0, QUANTIDADE_RELATOS_TEASER);
      },
      error: () => {
        this.carregandoRelatos = false;
        this.erroRelatos = true;
      },
    });
  }

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

  nomeParaExibir(relato: RelatoPublicoResumo): string {
    const nome = relato.nomeExibicao?.trim();
    return nome ? nome : 'Anônimo';
  }

  trechoRelato(relato: RelatoPublicoResumo): string {
    const texto = relato.textoResumo.trim();
    if (texto.length <= TRECHO_RELATO_LIMITE) {
      return texto;
    }
    return texto.slice(0, TRECHO_RELATO_LIMITE).trimEnd() + '...';
  }
}
