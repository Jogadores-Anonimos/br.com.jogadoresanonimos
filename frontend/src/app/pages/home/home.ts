import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';

import { BannerMuralApoio, MuralDeApoioService } from '../../services/mural-de-apoio';
import { Aviso, AvisosService } from '../../services/avisos';
import { Relato, RelatosService } from '../../services/relatos';
import { ReflexaoPreview } from '../../components/reflexao-preview/reflexao-preview';
import { WHATSAPP_CHAT_URL } from '../../config/whatsapp.config';

const QUANTIDADE_AVISOS_TEASER = 3;
const QUANTIDADE_RELATOS_TEASER = 3;
const TRECHO_RELATO_LIMITE = 70;

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, ReflexaoPreview],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home implements OnInit {
  readonly whatsappChatUrl = WHATSAPP_CHAT_URL;

  bannersMural: BannerMuralApoio[] = [];
  carregandoMural = true;
  erroMural = false;

  avisos: Aviso[] = [];
  carregandoAvisos = true;
  erroAvisos = false;

  relatosPreview: Relato[] = [];
  carregandoRelatos = true;
  erroRelatos = false;

  constructor(
    private readonly muralDeApoioService: MuralDeApoioService,
    private readonly avisosService: AvisosService,
    private readonly relatosService: RelatosService,
  ) {}

  ngOnInit(): void {
    this.muralDeApoioService.getBannersDestaque().subscribe((resposta) => {
      this.carregandoMural = false;

      if (!resposta.success || !resposta.data) {
        this.erroMural = true;
        return;
      }

      this.bannersMural = resposta.data;
    });

    this.avisosService.listarAvisos().subscribe((resposta) => {
      this.carregandoAvisos = false;

      if (!resposta.success || !resposta.data) {
        this.erroAvisos = true;
        return;
      }

      this.avisos = resposta.data.slice(0, QUANTIDADE_AVISOS_TEASER);
    });

    this.relatosService.listarRelatosAprovados().subscribe((resposta) => {
      this.carregandoRelatos = false;

      if (!resposta.success || !resposta.data) {
        this.erroRelatos = true;
        return;
      }

      this.relatosPreview = resposta.data.slice(0, QUANTIDADE_RELATOS_TEASER);
    });
  }

  nomeParaExibir(relato: Relato): string {
    const nome = relato.nomeExibicao?.trim();
    return nome ? nome : 'Anônimo';
  }

  trechoRelato(relato: Relato): string {
    const texto = relato.texto.trim();
    if (texto.length <= TRECHO_RELATO_LIMITE) {
      return texto;
    }
    return texto.slice(0, TRECHO_RELATO_LIMITE).trimEnd() + '...';
  }
}
