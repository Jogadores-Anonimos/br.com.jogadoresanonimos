import { Component, ElementRef, HostListener } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

type DropdownKey = 'sobre' | 'recursos';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class Navbar {
  constructor(private readonly el: ElementRef<HTMLElement>) {}

  menuAberto = false;

  dropdownsAbertos: Record<DropdownKey, boolean> = {
    sobre: false,
    recursos: false
  };

  toggleMenu(): void {
    this.menuAberto = !this.menuAberto;
  }

  toggleDropdown(chave: DropdownKey, event: Event): void {
    event.stopPropagation();
    const estavaAberto = this.dropdownsAbertos[chave];
    this.fecharDropdowns();
    this.dropdownsAbertos[chave] = !estavaAberto;
  }

  private fecharDropdowns(): void {
    this.dropdownsAbertos = { sobre: false, recursos: false };
  }

  @HostListener('document:click', ['$event'])
  aoClicarFora(event: Event): void {
    if (!this.el.nativeElement.contains(event.target as Node)) {
      this.fecharDropdowns();
    }
  }
}
