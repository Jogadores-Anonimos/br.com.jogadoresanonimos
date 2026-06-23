import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-autoaval',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './autoaval.html',
  styleUrl: './autoaval.css'
})
export class Autoaval {
  constructor(private cdr: ChangeDetectorRef) {}
  quizIniciado = false;
  perguntaAtual = 0;
  respostas: string[] = [];
  botoesAtivos = false;
  resultado: any = null;

  perguntas = [
    "Você já perdeu horas de trabalho ou da escola devido ao jogo?",
    "Alguma vez o jogo já causou infelicidade na sua vida familiar?",
    "O jogo afetou a sua reputação?",
    "Você já mentiu sobre o quanto joga?",
    "O jogo já prejudicou sua vida financeira?",
    "Você já sentiu ansiedade por não jogar?",
    "Você já pediu dinheiro emprestado para jogar?",
    "Você já tentou recuperar perdas?",
    "Você já sentiu culpa após jogar?",
    "Você acha difícil ficar sem jogar?",
    "Você já deixou de cumprir compromissos por causa do jogo?",
    "Você já escondeu o quanto joga de outras pessoas?",
    "O jogo já causou conflitos com família ou amigos?",
    "Você já perdeu noção do tempo enquanto jogava?",
    "Você já jogou para aliviar estresse ou problemas pessoais?"
  ];

  get progresso() {
    return Math.round((this.perguntaAtual / this.perguntas.length) * 100);
  }

  get sim() {
    return this.respostas.filter(r => r === 'sim').length;
  }

  get resultadoTexto() {
    if (this.sim >= 9) return `Você respondeu SIM a ${this.sim} perguntas. Vários sinais importantes estão presentes. Buscar apoio de um profissional de saúde é um passo de coragem e cuidado com você mesmo.`;
    if (this.sim >= 4) return `Você respondeu SIM a ${this.sim} perguntas. Alguns sinais merecem atenção. Conversar com alguém de confiança ou um profissional pode ajudar muito.`;
    return `Você respondeu SIM a apenas ${this.sim} pergunta(s). Nenhum sinal preocupante no momento. Continue atento ao seu bem-estar.`;
  }

  get resultadoIconCor() {
    if (this.sim >= 9) return '#EF4444';
    if (this.sim >= 4) return '#F59E0B';
    return '#10B981';
  }

  iniciarQuiz() {
    this.quizIniciado = true;
    this.perguntaAtual = 0;
    this.respostas = [];
    this.resultado = null;
    this.botoesAtivos = true;
  }

  responder(resposta: string) {
    if (!this.botoesAtivos) return;
    this.botoesAtivos = false;
    this.respostas.push(resposta);
    this.perguntaAtual++;

    if (this.perguntaAtual >= this.perguntas.length) {
      this.resultado = {
        sim: this.sim,
        total: this.respostas.length,
        data: new Date().toISOString()
      };
      localStorage.setItem('autoaval_resultado', JSON.stringify({
        respostas: this.respostas,
        ...this.resultado
      }));
    } else {
      setTimeout(() => {
        this.botoesAtivos = true;
        this.cdr.detectChanges();
      }, 10);
    }
  }
}
