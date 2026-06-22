import {Component, ElementRef, HostListener, ViewEncapsulation } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { NgOptimizedImage } from '@angular/common';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, NgOptimizedImage],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
  encapsulation: ViewEncapsulation.None
})
export class NavbarComponent {
  constructor(private el: ElementRef) {}

  menuAberto = false;

  toggleMenu() {
    this.menuAberto = !this.menuAberto;
  }

  dropdownAberto = false;

  toggleDropdown() {
    this.dropdownAberto = !this.dropdownAberto;
  }

  @HostListener('document:click', ['$event'])
  fecharDropdown(event: Event) {
    if (!this.el.nativeElement.contains(event.target)) {
      this.dropdownAberto = false;
    }
  }
}
