import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';

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
export class VintePerguntas {
  /**
   * As 20 Perguntas são material de programa público da irmandade
   * Jogadores Anônimos, usadas mundialmente para autoavaliação.
   * Nenhuma resposta é armazenada ou enviada — tudo acontece só
   * na memória do navegador durante a visita.
   */
  readonly perguntas: string[] = [
    'Você já perdeu tempo de trabalho ou de escola por causa do jogo?',
    'O jogo já tornou sua vida familiar infeliz?',
    'O jogo já afetou a sua reputação?',
    'Você já sentiu remorso depois de jogar?',
    'Você já jogou para conseguir dinheiro com o qual pagar dívidas ou resolver dificuldades financeiras?',
    'O jogo já causou uma diminuição da sua ambição ou eficiência?',
    'Depois de perder, você sentiu que precisava voltar o quanto antes para recuperar o prejuízo?',
    'Depois de ganhar, você teve um forte impulso de voltar a jogar para ganhar mais?',
    'Você costumava jogar até gastar o último centavo que tinha?',
    'Você já pegou dinheiro emprestado para financiar o jogo?',
    'Você já vendeu alguma coisa para financiar o jogo?',
    'Você relutava em usar o "dinheiro do jogo" para despesas normais?',
    'O jogo já fez você negligenciar o bem-estar seu ou da sua família?',
    'Você já jogou por mais tempo do que havia planejado?',
    'Você já jogou para escapar de preocupações ou problemas?',
    'Você já cometeu, ou considerou cometer, algum ato ilegal para financiar o jogo?',
    'O jogo já causou dificuldade para você dormir?',
    'Discussões, decepções ou frustrações despertam em você o impulso de jogar?',
    'Você já teve vontade de comemorar alguma boa notícia jogando por algumas horas?',
    'Você já considerou se autodestruir ou cometer suicídio como resultado do jogo?',
  ];

  /** Critério oficial: a maioria dos jogadores compulsivos responde SIM a pelo menos 7 destas perguntas. */
  readonly limiarSinais = 7;

  quizIniciado = false;
  perguntaAtual = 0;
  respostas: Resposta[] = [];
  resultado: ResultadoQuiz | null = null;

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
}
