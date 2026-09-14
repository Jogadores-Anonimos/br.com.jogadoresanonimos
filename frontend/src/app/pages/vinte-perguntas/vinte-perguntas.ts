import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { VintePerguntasService } from '../../services/vinte-perguntas/vinte-perguntas';
import { extrairYoutubeVideoId } from '../../services/mural-de-apoio/youtube.util';

const VIDEO_URL_VINTE_PERGUNTAS = 'https://www.youtube.com/watch?v=OPkn4eTc2-8';

type Resposta = 'sim' | 'nao';

interface ResultadoQuiz {
  totalSim: number;
  totalPerguntas: number;
}

@Component({
  selector: 'app-vinte-perguntas',
  standalone: true,
  imports: [RouterLink, ResourceLayout],
  templateUrl: './vinte-perguntas.html',
  styleUrl: './vinte-perguntas.css',
})
export class VintePerguntas implements OnInit {
  private readonly vintePerguntasService = inject(VintePerguntasService);
  private readonly sanitizer = inject(DomSanitizer);

  readonly embedUrlVideo: SafeResourceUrl | null = this.criarEmbedUrlVideo();

  perguntas: string[] = [];

  carregando = true;
  erro: string | null = null;

  readonly limiarSinais = 7;

  quizIniciado = false;
  perguntaAtual = 0;
  respostas: Resposta[] = [];
  resultado: ResultadoQuiz | null = null;

  ngOnInit(): void {
    this.carregarPerguntas();
  }

  carregarPerguntas(): void {
    this.carregando = true;
    this.erro = null;

    this.vintePerguntasService.listar().subscribe((resposta) => {
      this.carregando = false;

      if (resposta.success && resposta.data && resposta.data.length > 0) {
        this.perguntas = [...resposta.data]
          .sort((a, b) => a.numero - b.numero)
          .map((questao) => questao.texto);
        return;
      }

      this.perguntas = [];
      this.erro = resposta.success
        ? 'Ainda não há perguntas cadastradas para este questionário.'
        : (resposta.error?.message ?? 'Não foi possível carregar as perguntas agora. Tente novamente mais tarde.');
    });
  }

  get progresso(): number {
    return Math.round((this.perguntaAtual / this.perguntas.length) * 100);
  }

  get sinaisIdentificados(): boolean {
    return (this.resultado?.totalSim ?? 0) >= this.limiarSinais;
  }

  iniciarQuiz(): void {
    this.quizIniciado = true;
    this.perguntaAtual = 0;
    this.respostas = [];
    this.resultado = null;
  }

  responder(resposta: Resposta): void {
    this.respostas.push(resposta);
    this.perguntaAtual++;

    if (this.perguntaAtual >= this.perguntas.length) {
      this.resultado = {
        totalSim: this.respostas.filter((r) => r === 'sim').length,
        totalPerguntas: this.perguntas.length,
      };
    }
  }

  refazer(): void {
    this.quizIniciado = false;
    this.perguntaAtual = 0;
    this.respostas = [];
    this.resultado = null;
  }

  private criarEmbedUrlVideo(): SafeResourceUrl | null {
    const videoId = extrairYoutubeVideoId(VIDEO_URL_VINTE_PERGUNTAS);
    return videoId
      ? this.sanitizer.bypassSecurityTrustResourceUrl(`https://www.youtube-nocookie.com/embed/${videoId}`)
      : null;
  }
}
