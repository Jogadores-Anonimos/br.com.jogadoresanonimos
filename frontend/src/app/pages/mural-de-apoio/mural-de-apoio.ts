import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';

import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { BannerMuralApoio, MuralDeApoioService } from '../../services/mural-de-apoio/mural-de-apoio';

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

  constructor(private readonly muralDeApoioService: MuralDeApoioService) {}

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
