import { Component } from '@angular/core';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';

interface PerguntaResposta {
  pergunta: string;
  resposta: string;
}

@Component({
  selector: 'app-perguntas-e-respostas',
  standalone: true,
  imports: [ResourceLayout],
  templateUrl: './perguntas-e-respostas.html',
  styleUrl: './perguntas-e-respostas.css',
})
export class PerguntasERespostas {
  readonly faqs: PerguntaResposta[] = [
    {
      pergunta: 'O que é o jogo compulsivo?',
      resposta:
        'É um transtorno progressivo caracterizado pela perda de controle sobre o impulso de jogar, mesmo diante de consequências financeiras, familiares e emocionais graves. Não é uma questão de força de vontade ou moral: é reconhecido como uma condição de saúde que pode ser tratada.',
    },
    {
      pergunta: 'Como sei se tenho um problema com o jogo?',
      resposta:
        'Alguns sinais comuns incluem jogar mais dinheiro ou tempo do que se pretendia, tentar recuperar perdas jogando ainda mais, mentir sobre hábitos de jogo, pedir dinheiro emprestado para jogar e sentir ansiedade ou irritação ao tentar parar. Se você se identifica com isso, vale a pena buscar apoio.',
    },
    {
      pergunta: 'Quais são os primeiros passos para buscar recuperação?',
      resposta:
        'O primeiro passo costuma ser admitir, para si mesmo, que o jogo se tornou um problema. A partir daí, procurar uma reunião de Jogadores Anônimos, conversar com alguém que já passou por isso e conhecer o programa dos Doze Passos são caminhos concretos para começar a recuperação.',
    },
    {
      pergunta: 'Como posso ajudar alguém que pode ter esse problema?',
      resposta:
        'Aborde a pessoa com empatia e sem julgamento, evitando cobranças ou ultimatos. Compartilhe informações sobre a Jogadores Anônimos e, se possível, ofereça-se para acompanhá-la a uma reunião. Lembre-se de que a decisão de buscar ajuda precisa partir da própria pessoa.',
    },
    {
      pergunta: 'Existe algum custo para participar das reuniões?',
      resposta:
        'Não. As reuniões são gratuitas e abertas a qualquer pessoa que tenha o desejo de parar de jogar. A irmandade se mantém apenas com contribuições voluntárias dos próprios membros, sem cobrar taxas ou mensalidades.',
    },
    {
      pergunta: 'É preciso ter alguma crença religiosa para participar?',
      resposta:
        'Não. Jogadores Anônimos não é afiliada a nenhuma religião, seita, partido político ou instituição. O programa fala em um "Poder Superior" como cada pessoa o compreende, e essa interpretação é livre e pessoal.',
    },
    {
      pergunta: 'O anonimato é garantido?',
      resposta:
        'Sim. O anonimato é um princípio fundamental da irmandade. O que é dito nas reuniões permanece nas reuniões, e ninguém é obrigado a se identificar além do primeiro nome ou apelido.',
    },
    {
      pergunta: 'Preciso me inscrever ou avisar antes de ir a uma reunião?',
      resposta:
        'Não é necessário. Você pode simplesmente comparecer a uma reunião aberta quando se sentir pronto. Não há inscrição, cadastro ou aprovação prévia para participar.',
    },
    {
      pergunta: 'O que acontece em uma reunião de Jogadores Anônimos?',
      resposta:
        'Nas reuniões, os membros compartilham suas experiências, dificuldades e conquistas relacionadas à compulsão pelo jogo, seguindo o formato dos Doze Passos e do Programa de Unidade. O ambiente é acolhedor, respeitoso e livre de julgamentos.',
    },
    {
      pergunta: 'A recuperação tem um tempo definido para acontecer?',
      resposta:
        'Não existe um prazo fixo. A recuperação é um processo contínuo e individual, vivido um dia de cada vez, com o apoio da irmandade e a prática constante dos princípios do programa.',
    },
  ];
}
