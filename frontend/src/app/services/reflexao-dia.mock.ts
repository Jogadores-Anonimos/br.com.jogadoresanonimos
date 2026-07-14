// TODO: substituir por chamada real quando o endpoint estiver disponível
// Mocks centralizados do domínio Reflexão do Dia — ver contrato em
// backend/docs/api/contrato-reflexao-do-dia.md
import { ReflexaoDia } from './reflexao-dia.model';

// índice 0 = janeiro; fevereiro sempre 28 dias (sem considerar ano bissexto) já que a reflexão é só mês+dia
export const DIAS_POR_MES: number[] = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];

// conteúdo variado só para julho (mês em que este mock foi escrito); demais meses ficam sem reflexão cadastrada,
// simulando o comportamento real do backend (dia sem conteúdo = 404, não texto placeholder)
const TEXTOS_JULHO: string[] = [
  'Hoje escolho lembrar que a recuperação é construída um dia de cada vez, não em uma corrida contra o tempo.',
  'A honestidade comigo mesmo é o primeiro passo para qualquer mudança duradoura.',
  'Quando sinto vontade de jogar, posso parar e perguntar: do que eu realmente preciso agora?',
  'Pedir ajuda não é fraqueza — é a decisão mais forte que posso tomar hoje.',
  'Minha história não define meu futuro; minhas escolhas de hoje sim.',
  'Compartilhar minha experiência pode ser exatamente o que outra pessoa precisa ouvir.',
  'A recaída não apaga o progresso já feito — ela é informação, não sentença.',
  'Hoje pratico paciência comigo mesmo, sabendo que a recuperação não é linear.',
  'Cada reunião que participo é um tijolo a mais na construção da minha estabilidade.',
  'Posso substituir a urgência do jogo pela urgência de cuidar de mim.',
  'A gratidão pelo que já conquistei me dá forças para continuar.',
  'Não preciso enfrentar sozinho os pensamentos difíceis — o grupo existe para isso.',
  'Hoje escolho a verdade, mesmo quando ela é desconfortável.',
  'Meu valor não é medido por vitórias ou perdas no jogo, mas por quem eu sou.',
  'A recuperação também é feita de pequenos momentos de autocuidado.',
  'Posso perdoar meus erros passados sem esquecer as lições que eles trouxeram.',
  'Hoje escolho ocupar minha mente com algo que me fortalece, não que me consome.',
  'A confiança se reconstrói aos poucos, com ações consistentes ao longo do tempo.',
  'Não estou sozinho nessa caminhada — outros já passaram por onde estou passando.',
  'Hoje é um bom dia para reconhecer o quanto já caminhei, mesmo com tropeços.',
  'A vontade de jogar é temporária; a decisão de recomeçar pode ser permanente.',
  'Posso pedir apoio antes de a situação piorar — isso também é recuperação.',
  'Hoje escolho falar sobre o que sinto, em vez de guardar tudo para mim.',
  'Celebro cada dia de recuperação, por menor que ele pareça.',
  'A mudança começa com um pensamento diferente sobre mim mesmo.',
  'Hoje entendo que buscar ajuda profissional é parte do cuidado comigo.',
  'Meus dias difíceis também ensinam — não preciso ser perfeito, só constante.',
  'Reconheço meus gatilhos e escolho me afastar deles quando possível.',
  'A recuperação também se constrói em comunidade, não apenas sozinho.',
  'Hoje escolho confiar no processo, mesmo sem ver o resultado final ainda.',
  'Um dia de cada vez: essa é a medida certa para minha recuperação.',
];

// mapa mes -> dia -> texto; só julho tem conteúdo cadastrado neste mock
export const REFLEXOES_MOCK = new Map<number, Map<number, string>>([
  [7, new Map(TEXTOS_JULHO.map((texto, indice) => [indice + 1, texto]))],
]);

export function buscarReflexaoMock(mes: number, dia: number): ReflexaoDia | null {
  const texto = REFLEXOES_MOCK.get(mes)?.get(dia);
  return texto ? { mes, dia, texto } : null;
}
