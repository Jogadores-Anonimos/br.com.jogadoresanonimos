import { Component, ElementRef, HostBinding, HostListener, OnDestroy } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';

type DropdownKey = 'sobre' | 'recursos';

/** Scroll mínimo (em px) para considerar a página "rolada". Evita que a
 * utility-bar pisque ao primeiro pixel de scroll (ex: bounce do iOS). */
const SCROLL_THRESHOLD_PX = 10;

/** Mesmo breakpoint do `@media (max-width: 900px)` em `navbar.css` — é só
 * abaixo dele que a .utility-bar realmente encolhe/some visualmente ao
 * rolar. Precisa ficar em sincronia com o CSS: se um mudar, o outro
 * também precisa mudar. */
const MOBILE_LAYOUT_BREAKPOINT_PX = 900;

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class Navbar implements OnDestroy {
  private readonly mobileLayoutMql = window.matchMedia(
    `(max-width: ${MOBILE_LAYOUT_BREAKPOINT_PX}px)`
  );

  /** Fecha a ligação entre `addEventListener` e a arrow function pra poder
   * remover o listener em `ngOnDestroy` sem depender de `.bind(this)`. */
  private readonly aoMudarLayout = (event: MediaQueryListEvent): void => {
    this.isMobileLayout = event.matches;
  };

  /** Fecha o menu mobile a cada navegação concluída (ex: usuário clica num
   * link do menu aberto). Sem isso, o menu continuava aberto por cima da
   * página seguinte até o usuário clicar de novo no hambúrguer. */
  private readonly routerEventsSub: Subscription;

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
  }

  ngOnDestroy(): void {
    this.mobileLayoutMql.removeEventListener('change', this.aoMudarLayout);
    this.routerEventsSub.unsubscribe();
  }

  menuAberto = false;

  /** Só no layout mobile/tablet (<= 900px) a .utility-bar realmente
   * encolhe/some visualmente ao rolar (ver @media max-width: 900px em
   * navbar.css). É essa flag — e não `scrolled` sozinho — que decide se a
   * barra deve virar `inert`: em desktop a barra nunca some da tela, então
   * nunca deve ficar inacessível a cliques/teclado, mesmo depois de rolar. */
  isMobileLayout = this.mobileLayoutMql.matches;

  /** Controla a exibição do painel de busca que desliza sobre o `#navbar`.
   * Ainda não existe busca real implementada no backend — este estado é
   * puramente visual (abre/fecha o painel e permite digitar no campo, sem
   * disparar nenhuma pesquisa). Ver `search-panel` no template. */
  buscaAberta = false;

  /** Reflete a classe `.scrolled` no host (`<app-navbar>`), permitindo que o
   * CSS controle tanto a utility-bar quanto o logo a partir de um único
   * estado, sem precisar duplicar a lógica em vários elementos. */
  @HostBinding('class.scrolled')
  scrolled = false;

  dropdownsAbertos: Record<DropdownKey, boolean> = {
    sobre: false,
    recursos: false
  };

  toggleMenu(): void {
    this.menuAberto = !this.menuAberto;
    if (this.menuAberto) {
      this.buscaAberta = false;
      this.fecharDropdowns();
    }
  }

  toggleDropdown(chave: DropdownKey, event: Event): void {
    event.stopPropagation();
    const estavaAberto = this.dropdownsAbertos[chave];
    this.fecharDropdowns();
    this.dropdownsAbertos[chave] = !estavaAberto;
  }

  /** Chamado pelos dois botões de lupa (desktop e mobile) — ambos controlam
   * o mesmo estado `buscaAberta`. Fecha os dropdowns ao abrir a busca para
   * não sobrepor os dois painéis ao mesmo tempo. */
  toggleBusca(): void {
    this.buscaAberta = !this.buscaAberta;
    if (this.buscaAberta) {
      this.fecharDropdowns();
      this.menuAberto = false;
    }
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
    }
  }

  @HostListener('window:scroll')
  aoRolarPagina(): void {
    this.scrolled = window.scrollY > SCROLL_THRESHOLD_PX;
  }
}
