import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';

import { BannerMuralApoio, MuralDeApoioService } from '../../services/mural-de-apoio';
import { ReflexaoPreview } from '../../components/reflexao-preview/reflexao-preview';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, ReflexaoPreview],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home implements OnInit {
  bannersMural: BannerMuralApoio[] = [];
  carregandoMural = true;
  erroMural = false;

  constructor(private readonly muralDeApoioService: MuralDeApoioService) {}

  ngOnInit(): void {
    this.muralDeApoioService.getBannersDestaque().subscribe((resposta) => {
      this.carregandoMural = false;

      if (!resposta.success || !resposta.data) {
        this.erroMural = true;
        return;
      }

      this.bannersMural = resposta.data;
    });
  }
}
