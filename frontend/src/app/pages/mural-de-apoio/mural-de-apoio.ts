import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { BannerMuralApoio, MuralDeApoioService } from '../../services/mural-de-apoio/mural-de-apoio';
import { paraYoutubeEmbedUrl, paraYoutubeThumbnailUrl } from '../../services/mural-de-apoio/youtube.util';

@Component({
  selector: 'app-mural-de-apoio',
  standalone: true,
  imports: [RouterLink, ResourceLayout],
  templateUrl: './mural-de-apoio.html',
  styleUrl: './mural-de-apoio.css',
})
export class MuralDeApoio implements OnInit {
  banners: BannerMuralApoio[] = [];
  carregando = true;
  erro = false;

  private readonly banneresTocando = new Set<string>();

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
    this.muralDeApoioService.getBannersCompleto().subscribe((resposta) => {
      this.carregando = false;

      if (!resposta.success || !resposta.data) {
        this.erro = true;
        return;
      }

      this.banners = resposta.data;
    });
  }
}
