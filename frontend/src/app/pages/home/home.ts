import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HeroSlider } from './components/hero-slider/hero-slider';
import { AvisosTeaser } from './teasers/avisos-teaser/avisos-teaser';
import { QuemSomosTeaser } from './teasers/quem-somos-teaser/quem-somos-teaser';
import { ContatoTeaser } from './teasers/contato-teaser/contato-teaser';
import { ReunioesTeaser } from './teasers/reunioes-teaser/reunioes-teaser';
import { ReflexoesTeaser } from './teasers/reflexoes-teaser/reflexoes-teaser';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, HeroSlider, AvisosTeaser, QuemSomosTeaser, ContatoTeaser, ReunioesTeaser, ReflexoesTeaser],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home {}
