import { Component, ElementRef, HostBinding, HostListener, OnDestroy, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { Subscription, fromEvent } from 'rxjs';
import { auditTime, filter } from 'rxjs/operators';
import { NAVBAR_SEARCH_INDEX, SearchIndexItem } from './navbar-search-index';
import { Auth } from '../../services/auth/auth';

type DropdownKey = 'sobre' | 'recursos';

const SCROLL_THRESHOLD_PX = 10;

const MOBILE_LAYOUT_BREAKPOINT_PX = 1100;

const MAX_RESULTADOS_BUSCA = 8;

const SCROLL_AUDIT_MS = 100;

function criarMobileLayoutMql(): MediaQueryList {
  if (typeof window.matchMedia === 'function') {
    return window.matchMedia(`(max-width: ${MOBILE_LAYOUT_BREAKPOINT_PX}px)`);
  }
  return {
    matches: false,
    media: '',
    onchange: null,
    addEventListener: () => {},
    removeEventListener: () => {},
    addListener: () => {},
    removeListener: () => {},
    dispatchEvent: () => false
  } as unknown as MediaQueryList;
}

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class Navbar implements OnDestroy {
  readonly auth = inject(Auth);

  private readonly mobileLayoutMql = criarMobileLayoutMql();

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

  showCart = false;

  isMobileLayout = this.mobileLayoutMql.matches;

  buscaAberta = false;

  termoBusca = '';

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
