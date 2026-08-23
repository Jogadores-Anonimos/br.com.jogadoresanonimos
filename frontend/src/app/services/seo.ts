import { Injectable, inject } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';
import { ActivatedRouteSnapshot, NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs';

export interface SeoData {
  title: string;
  description: string;
  ogTitle?: string;
  ogDescription?: string;
  robots?: string;
}

const DOMINIO_PRODUCAO = 'https://jogadoresanonimos.com.br';
const ROBOTS_PADRAO = 'index, follow';
const SEO_FALLBACK: SeoData = {
  title: 'Jogadores Anônimos do Brasil',
  description:
    'Jogadores Anônimos do Brasil: informação e apoio para pessoas afetadas pelo jogo compulsivo.',
  robots: ROBOTS_PADRAO,
};

@Injectable({ providedIn: 'root' })
export class Seo {
  private readonly router = inject(Router);
  private readonly title = inject(Title);
  private readonly meta = inject(Meta);

  constructor() {
    this.router.events
      .pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
      .subscribe((event) => {
        const seoData = this.obterSeoDataMaisProfundo(this.router.routerState.root.snapshot);
        this.aplicar(seoData ?? SEO_FALLBACK, event.urlAfterRedirects);
      });
  }

  private obterSeoDataMaisProfundo(snapshot: ActivatedRouteSnapshot): SeoData | undefined {
    let atual: ActivatedRouteSnapshot | null = snapshot;
    let seoData: SeoData | undefined;
    while (atual) {
      const dataDaRota = atual.data['seo'] as SeoData | undefined;
      if (dataDaRota) {
        seoData = dataDaRota;
      }
      atual = atual.firstChild;
    }
    return seoData;
  }

  private aplicar(seoData: SeoData, urlAtual: string): void {
    this.title.setTitle(seoData.title);
    this.meta.updateTag({ name: 'description', content: seoData.description });
    this.meta.updateTag({ property: 'og:title', content: seoData.ogTitle ?? seoData.title });
    this.meta.updateTag({
      property: 'og:description',
      content: seoData.ogDescription ?? seoData.description,
    });
    this.meta.updateTag({ property: 'og:type', content: 'website' });
    this.meta.updateTag({ property: 'og:url', content: `${DOMINIO_PRODUCAO}${urlAtual}` });
    this.meta.updateTag({ name: 'robots', content: seoData.robots ?? ROBOTS_PADRAO });
  }
}
