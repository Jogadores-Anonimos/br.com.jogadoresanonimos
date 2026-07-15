import { VinteQuestaoResponse } from './vinte-perguntas.model';

const TEXTOS_MOCK = [
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

export const VINTE_PERGUNTAS_MOCK: VinteQuestaoResponse[] = TEXTOS_MOCK.map((texto, indice) => ({
  id: `mock-${indice + 1}`,
  numero: indice + 1,
  ordem: indice,
  texto,
}));
