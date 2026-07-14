import { Component, ElementRef, HostBinding, HostListener, OnDestroy, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NavigationEnd, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { Subscription, fromEvent } from 'rxjs';
import { auditTime, filter } from 'rxjs/operators';
import { NAVBAR_SEARCH_INDEX, SearchIndexItem } from './navbar-search-index';
import { Auth } from '../../services/auth';

type DropdownKey = 'sobre' | 'recursos';

const SCROLL_THRESHOLD_PX = 10;

const MOBILE_LAYOUT_BREAKPOINT_PX = 1100;

const MAX_RESULTADOS_BUSCA = 8;

// auditTime evita recalcular a cada pixel rolado (menos trabalho na main thread)
const SCROLL_AUDIT_MS = 100;

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, FormsModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class Navbar implements OnDestroy {
  readonly auth = inject(Auth);

  private readonly mobileLayoutMql = window.matchMedia(
    `(max-width: ${MOBILE_LAYOUT_BREAKPOINT_PX}px)`
  );

  private readonly aoMudarLayout = (event: MediaQueryListEvent): void => {
    this.isMobileLayout = event.matches;
  };

  private readonly routerEventsSub: Subscription;
  private readonly scrollSub: Subscription;

  constructor(
    private readonly el: ElementRef<HTMLElement>,
    private readonly router: Router
  ) {
    this.mobileLayoutMql.addEventListener('change', this.aoMudarLayout);
    this.routerEventsSub = this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe(() => {
        this.menuAberto = false;
      });
    this.scrollSub = fromEvent(window, 'scroll', { passive: true })
      .pipe(auditTime(SCROLL_AUDIT_MS))
      .subscribe(() => {
        this.scrolledSignal.set(window.scrollY > SCROLL_THRESHOLD_PX);
      });
  }

  ngOnDestroy(): void {
    this.mobileLayoutMql.removeEventListener('change', this.aoMudarLayout);
    this.routerEventsSub.unsubscribe();
    this.scrollSub.unsubscribe();
  }

  menuAberto = false;

  isMobileLayout = this.mobileLayoutMql.matches;

  buscaAberta = false;

  /**
   * Termo de busca: vive só em memória do componente. Nunca é logado,
   * persistido (localStorage/sessionStorage/cookie) ou colocado na URL,
   * por requisito de anonimato do projeto. É sempre limpo quando o
   * painel de busca fecha.
   */
  termoBusca = '';

  // Sinal em vez de campo comum: escrito fora do Angular (callback de
  // scroll do RxJS) — em app zoneless, escrita em campo simples não
  // agenda um novo ciclo de detecção de forma confiável e pode ficar
  // dessincronizada de outros bindings no mesmo tick, disparando
  // NG0100 (ExpressionChangedAfterItHasBeenCheckedError) que aborta a
  // atualização do DOM daquele ciclo inteiro — inclusive de componentes
  // não relacionados. Sinal resolve isso.
  private readonly scrolledSignal = signal(false);

  @HostBinding('class.scrolled')
  get scrolled(): boolean {
    return this.scrolledSignal();
  }

  dropdownsAbertos: Record<DropdownKey, boolean> = {
    sobre: false,
    recursos: false
  };

  toggleMenu(): void {
    this.menuAberto = !this.menuAberto;
    if (this.menuAberto) {
      this.buscaAberta = false;
      this.limparBusca();
      this.fecharDropdowns();
    }
  }

  toggleDropdown(chave: DropdownKey, event: Event): void {
    event.stopPropagation();
    if (this.dropdownsAbertos[chave]) {
      this.fecharDropdown(chave);
    } else {
      this.abrirDropdown(chave);
    }
  }

  abrirDropdown(chave: DropdownKey): void {
    this.dropdownsAbertos = {
      sobre: chave === 'sobre',
      recursos: chave === 'recursos'
    };
  }

  fecharDropdown(chave: DropdownKey): void {
    this.dropdownsAbertos[chave] = false;
  }

  toggleBusca(): void {
    this.buscaAberta = !this.buscaAberta;
    if (this.buscaAberta) {
      this.fecharDropdowns();
      this.menuAberto = false;
    } else {
      this.limparBusca();
    }
  }

  /** Resultados filtrados em tempo real a partir do termo atual. */
  get resultadosBusca(): SearchIndexItem[] {
    const termo = this.normalizarTermo(this.termoBusca);
    if (!termo) {
      return [];
    }
    return NAVBAR_SEARCH_INDEX.filter((item) =>
      [item.titulo, ...item.keywords].some((texto) =>
        this.normalizarTermo(texto).includes(termo)
      )
    ).slice(0, MAX_RESULTADOS_BUSCA);
  }

  private normalizarTermo(valor: string): string {
    return valor.trim().toLowerCase();
  }

  selecionarResultado(item: SearchIndexItem): void {
    this.buscaAberta = false;
    this.limparBusca();
    this.router.navigateByUrl(item.rota);
  }

  aoSubmeterBusca(event: Event): void {
    event.preventDefault();
    const primeiroResultado = this.resultadosBusca[0];
    if (primeiroResultado) {
      this.selecionarResultado(primeiroResultado);
    }
  }

  private limparBusca(): void {
    this.termoBusca = '';
  }

  private fecharDropdowns(): void {
    this.dropdownsAbertos = { sobre: false, recursos: false };
  }

  @HostListener('document:click', ['$event'])
  aoClicarFora(event: Event): void {
    if (!this.el.nativeElement.contains(event.target as Node)) {
      this.fecharDropdowns();
      this.buscaAberta = false;
      this.menuAberto = false;
      this.limparBusca();
    }
  }

  @HostListener('document:keydown.escape')
  aoPressionarEsc(): void {
    if (this.buscaAberta) {
      this.buscaAberta = false;
      this.limparBusca();
    }
  }

}
