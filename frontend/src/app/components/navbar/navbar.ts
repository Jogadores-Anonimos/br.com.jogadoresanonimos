import { Component, ElementRef, HostBinding, HostListener, OnDestroy } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';

type DropdownKey = 'sobre' | 'recursos';

const SCROLL_THRESHOLD_PX = 10;

const MOBILE_LAYOUT_BREAKPOINT_PX = 1100;

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

  private readonly aoMudarLayout = (event: MediaQueryListEvent): void => {
    this.isMobileLayout = event.matches;
  };

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

  isMobileLayout = this.mobileLayoutMql.matches;

  buscaAberta = false;

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
