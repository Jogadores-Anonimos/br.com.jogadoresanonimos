import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-perguntas-frequentes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './perguntas-frequentes.html',
  styleUrl: './perguntas-frequentes.css',
})
export class PerguntasFrequentes {
  faqAberta: number | null = 0;

  toggleFaq(index: number) {
    this.faqAberta = this.faqAberta === index ? null : index;
  }

  faqs = [
    {
      pergunta: 'Qual é a primeira coisa que um jogador compulsivo deve fazer para parar de jogar?',
      resposta: 'Aceitar que está sob o domínio da doença e desejar sinceramente parar.'
    },
    {
      pergunta: 'É importante saber por que jogávamos?',
      resposta: 'Pode ajudar, mas muitos membros conseguiram parar sem entender completamente as causas.'
    },
    {
      pergunta: 'Qual é o maior sonho do jogador compulsivo?',
      resposta: 'Acreditar que uma grande vitória resolverá todos os problemas.'
    },
    {
      pergunta: 'O jogo compulsivo é apenas um problema financeiro?',
      resposta: 'Não. É um problema emocional que afeta diversas áreas da vida.'
    },
    {
      pergunta: 'A força de vontade é suficiente?',
      resposta: 'Nossa experiência mostra que a recuperação exige mais do que força de vontade.'
    },
    {
      pergunta: 'O que é o Programa de Recuperação?',
      resposta: 'Um programa de 12 Passos utilizado para crescimento pessoal e recuperação.'
    },
    {
      pergunta: 'Jogadores Anônimos é uma religião?',
      resposta: 'Não. Pessoas de diferentes crenças participam da Irmandade.'
    },
    {
      pergunta: 'JA quer acabar com os jogos?',
      resposta: 'Não. JA não se posiciona sobre questões externas.'
    }
  ];
}
