import { Component, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';

interface Reflexao {
  dia: number;
  mes: number;
  titulo: string;
  texto: string;
  pergunta: string;
  oracao: string;
  lembrete: string;
}

@Component({
  selector: 'app-reflexoes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './reflexoes.html',
  styleUrl: './reflexoes.css',
  encapsulation: ViewEncapsulation.None
})
export class Reflexoes {
  meses = ['Janeiro','Fevereiro','Março','Abril','Maio','Junho','Julho','Agosto','Setembro','Outubro','Novembro','Dezembro'];

  mesSelecionado = new Date().getMonth();
  diaSelecionado: number | null = new Date().getDate();
  reflexaoAtual: Reflexao | null = null;

  reflexoes: Reflexao[] = [
    { dia: 1, mes: 0, titulo: 'O Primeiro Passo', texto: 'Admitir nossa impotência perante o jogo foi o começo de tudo. Sem essa honestidade, nenhuma recuperação seria possível. Hoje escolho a verdade acima do orgulho.', pergunta: 'Estou sendo honesto comigo mesmo sobre minha relação com o jogo?', oracao: 'Deus, dai-me coragem para admitir minhas fraquezas e força para seguir em frente com humildade.', lembrete: 'A honestidade é o alicerce da recuperação.' },
    { dia: 2, mes: 0, titulo: 'Um Dia de Cada Vez', texto: 'Não preciso me preocupar com o amanhã. Tudo que preciso fazer é não jogar hoje. Um dia de cada vez é suficiente para construir uma vida nova.', pergunta: 'Estou vivendo o presente ou me perdendo em preocupações com o futuro?', oracao: 'Senhor, ajuda-me a focar no hoje e a encontrar paz no momento presente.', lembrete: 'Só por hoje.' },
    { dia: 3, mes: 0, titulo: 'O Poder das Reuniões', texto: 'Nas reuniões encontrei pessoas que entendem minha dor sem julgamento. A partilha de experiências nos fortalece mutuamente e nos lembra que não estamos sozinhos nessa jornada.', pergunta: 'Estou aproveitando ao máximo as reuniões e a comunidade de JA?', oracao: 'Que eu encontre nas reuniões a força que não consigo encontrar sozinho.', lembrete: 'As reuniões produzem recuperação.' },
    { dia: 4, mes: 0, titulo: 'Reparando os Danos', texto: 'O jogo compulsivo deixou feridas em pessoas que amamos. A recuperação nos convida a olhar para esses danos com coragem e a fazer o que for possível para repará-los.', pergunta: 'Há alguém a quem eu devo uma reparação ou um pedido de desculpas?', oracao: 'Deus, dai-me humildade para reconhecer os danos que causei e disposição para repará-los.', lembrete: 'A reparação é um ato de amor.' },
    { dia: 5, mes: 0, titulo: 'A Gratidão Como Remédio', texto: 'Quando a vontade de jogar aparece, a gratidão pode ser nosso remédio. Listar as coisas pelas quais somos gratos nos afasta do pensamento compulsivo e nos reconecta com o que realmente importa.', pergunta: 'O que tenho a agradecer hoje?', oracao: 'Obrigado, Senhor, por mais um dia longe do jogo e perto das pessoas que amo.', lembrete: 'A gratidão transforma a perspectiva.' },
    { dia: 1, mes: 1, titulo: 'Recomeços', texto: 'Fevereiro nos lembra que é sempre possível recomeçar. Cada amanhecer é uma nova oportunidade de escolher a recuperação, independente do que aconteceu ontem.', pergunta: 'Estou me permitindo recomeçar sem me prender à culpa do passado?', oracao: 'Que cada dia seja um novo começo cheio de esperança e determinação.', lembrete: 'Nunca é tarde para recomeçar.' },
    { dia: 2, mes: 1, titulo: 'O Anonimato', texto: 'O anonimato nos protege e cria um espaço seguro para a recuperação. Ele nos lembra que o que importa são os princípios, não as personalidades.', pergunta: 'Estou preservando o anonimato dos meus companheiros de JA?', oracao: 'Que eu seja guardião da confiança depositada em mim pelos meus irmãos de recuperação.', lembrete: 'O anonimato é a base espiritual do programa.' },
    { dia: 3, mes: 1, titulo: 'Servir ao Próximo', texto: 'Uma das maiores ferramentas da recuperação é ajudar outro jogador compulsivo. Ao estendermos a mão para quem ainda sofre, fortalecemos nossa própria sobriedade.', pergunta: 'Estou disponível para ajudar quem está no início da jornada?', oracao: 'Que eu possa ser instrumento de esperança para quem ainda sofre.', lembrete: 'Servir é parte da cura.' },
    { dia: 1, mes: 2, titulo: 'A Chegada da Primavera', texto: 'Assim como a natureza se renova, nossa vida em recuperação também se renova a cada dia. O que parecia impossível se torna real quando nos mantemos no caminho da sobriedade.', pergunta: 'Que renovações estou percebendo na minha vida desde que parei de jogar?', oracao: 'Senhor, que eu possa florescer assim como a natureza ao meu redor.', lembrete: 'A vida se renova a cada dia sóbrio.' },
    { dia: 2, mes: 2, titulo: 'Paciência', texto: 'A recuperação não acontece da noite para o dia. Precisamos ter paciência conosco e com o processo. Cada passo pequeno é uma vitória que merece ser celebrada.', pergunta: 'Estou sendo paciente comigo mesmo no processo de recuperação?', oracao: 'Que eu aprenda a confiar no tempo de Deus e na minha própria jornada.', lembrete: 'A paciência é uma virtude da recuperação.' },
    { dia: 1, mes: 3, titulo: 'Responsabilidade', texto: 'Assumir responsabilidade por nossas ações é um passo fundamental na recuperação. Não podemos mudar o passado, mas podemos escolher como agir daqui para frente.', pergunta: 'Estou assumindo responsabilidade pelas minhas escolhas sem me autopunir?', oracao: 'Que eu tenha coragem de ser responsável e sabedoria para não me destruir com a culpa.', lembrete: 'Responsabilidade é liberdade.' },
    { dia: 2, mes: 3, titulo: 'Limites Saudáveis', texto: 'A recuperação nos ensina a estabelecer limites saudáveis com pessoas, lugares e situações que ameaçam nossa sobriedade. Dizer não é um ato de autocuidado.', pergunta: 'Que limites preciso estabelecer para proteger minha recuperação?', oracao: 'Que eu tenha clareza para identificar o que me faz bem e coragem para me proteger.', lembrete: 'Limites são um ato de amor próprio.' },
    { dia: 1, mes: 4, titulo: 'O Poder da Esperança', texto: 'Quando chegamos a JA, muitos de nós havíamos perdido a esperança. A recuperação nos devolve a crença de que uma vida melhor é possível e que merecemos vivê-la.', pergunta: 'Em que aspectos da minha vida a esperança está voltando a florescer?', oracao: 'Que a esperança seja minha companheira em todos os momentos difíceis.', lembrete: 'A esperança é o combustível da recuperação.' },
    { dia: 2, mes: 4, titulo: 'Cuidando do Corpo', texto: 'A compulsão pelo jogo muitas vezes nos fez negligenciar nossa saúde física. A recuperação também envolve cuidar do corpo — dormir bem, alimentar-se e movimentar-se.', pergunta: 'Estou cuidando do meu corpo como parte da minha recuperação?', oracao: 'Que eu respeite e cuide do corpo que Deus me deu.', lembrete: 'Mente sã em corpo são.' },
    { dia: 1, mes: 5, titulo: 'Aceitação', texto: 'A aceitação é a resposta para muitos dos nossos problemas. Aceitar o que não podemos mudar nos libera para focar no que está ao nosso alcance transformar.', pergunta: 'Há algo que preciso aceitar para encontrar paz hoje?', oracao: 'Que eu aceite o que não posso mudar com serenidade e mude o que está ao meu alcance com coragem.', lembrete: 'A aceitação é a chave da serenidade.' },
    { dia: 2, mes: 5, titulo: 'Conexões Verdadeiras', texto: 'O jogo nos isolou. A recuperação nos reconecta com as pessoas. Construir relacionamentos honestos e amorosos é parte essencial da nossa cura.', pergunta: 'Estou investindo em relacionamentos saudáveis e genuínos?', oracao: 'Que eu possa dar e receber amor de forma honesta e saudável.', lembrete: 'A conexão é o antídoto para o vício.' },
    { dia: 3, mes: 5, titulo: 'Fé', texto: 'Não precisamos entender tudo para ter fé. A fé é confiar que existe um Poder maior que pode nos guiar, mesmo quando não enxergamos o caminho.', pergunta: 'Estou confiando num Poder maior ou tentando controlar tudo sozinho?', oracao: 'Que eu entregue minha vontade e minha vida aos cuidados de Deus como o concebo.', lembrete: 'A fé remove montanhas.' },
    { dia: 4, mes: 5, titulo: 'A Aceitação é a Resposta', texto: 'A aceitação é a resposta. Com demasiada frequência fui incapaz de aceitar quem eu era e, dessa forma, era impossível aceitar os outros. Antes de vir para Jogadores Anônimos, escondia-me da verdade por temê-la. Agora, com a ajuda de meus irmãos de JA posso superar isso.', pergunta: 'Aceito quem eu era, com todos os meus defeitos, e quem estou tentando me tornar?', oracao: 'Deus, concedei-me serenidade para aceitar as coisas que não posso modificar e coragem para mudar as que posso.', lembrete: 'A aceitação é a chave da recuperação.' },
    { dia: 1, mes: 6, titulo: 'Meio do Ano', texto: 'Chegamos ao meio do ano. Se você está em recuperação, cada dia foi uma vitória. Olhe para trás e veja o quanto cresceu. Olhe para frente com esperança.', pergunta: 'Que conquistas da recuperação posso celebrar neste meio de ano?', oracao: 'Obrigado por cada dia sóbrio. Que o segundo semestre seja de crescimento e paz.', lembrete: 'Cada dia sóbrio é uma conquista.' },
    { dia: 2, mes: 6, titulo: 'Humildade', texto: 'A humildade não é se diminuir — é ser honesto sobre quem somos, com nossos pontos fortes e fraquezas. A humildade abre espaço para o crescimento.', pergunta: 'Estou praticando a humildade ou ainda tentando aparentar ter tudo sob controle?', oracao: 'Que eu possa me ver com olhos honestos e caminhar com humildade.', lembrete: 'A humildade é o caminho para a sabedoria.' },
    { dia: 1, mes: 7, titulo: 'Persistência', texto: 'A recuperação não é uma linha reta. Haverá dias difíceis, mas a persistência nos mantém no caminho. Cada vez que escolhemos não jogar, estamos fortalecendo nossa recuperação.', pergunta: 'Estou sendo persistente mesmo nos dias mais difíceis?', oracao: 'Que eu não desista de mim mesmo, especialmente quando a caminhada é mais dura.', lembrete: 'Persistência vence a resistência.' },
    { dia: 1, mes: 8, titulo: 'Inventário Pessoal', texto: 'O inventário pessoal nos ajuda a monitorar nosso crescimento e identificar áreas que precisam de atenção. É uma ferramenta poderosa para quem busca o autoconhecimento.', pergunta: 'Estou fazendo meu inventário pessoal com regularidade e honestidade?', oracao: 'Que eu tenha coragem de me olhar no espelho com honestidade e compaixão.', lembrete: 'O autoconhecimento é poder.' },
    { dia: 1, mes: 9, titulo: 'Outonos da Vida', texto: 'Assim como o outono transforma as folhas, a recuperação transforma nossa vida. O que parecia perda pode se tornar libertação. O que parecia fim pode ser um novo começo.', pergunta: 'Que transformações positivas a recuperação trouxe para minha vida?', oracao: 'Que eu encontre beleza nas transformações que a recuperação traz.', lembrete: 'Toda transformação é uma oportunidade.' },
    { dia: 1, mes: 10, titulo: 'Gratidão Profunda', texto: 'Com o tempo na recuperação, nossa gratidão se aprofunda. Passamos a valorizar coisas simples que antes ignorávamos — uma refeição tranquila, uma noite de sono, a confiança de um filho.', pergunta: 'Quais pequenas coisas da vida sóbria estou aprendendo a valorizar?', oracao: 'Que meu coração esteja sempre aberto para reconhecer as bênçãos do dia a dia.', lembrete: 'A gratidão profunda nasce da experiência.' },
    { dia: 1, mes: 11, titulo: 'Fim e Recomeço', texto: 'Dezembro nos convida a refletir sobre o ano que passou. Cada dia sóbrio foi uma conquista. Cada dificuldade superada nos tornou mais fortes. O próximo ano começa com a sabedoria que adquirimos.', pergunta: 'O que aprendi sobre mim mesmo neste ano de recuperação?', oracao: 'Obrigado por este ano. Que o próximo seja ainda mais cheio de crescimento e paz.', lembrete: 'Cada ano sóbrio é um presente.' },
    { dia: 2, mes: 11, titulo: 'O Dom do Presente', texto: 'O presente é um presente. A recuperação nos ensina a viver o agora, sem nos perder nas culpas do passado ou nas ansiedades do futuro.', pergunta: 'Estou realmente presente na minha própria vida?', oracao: 'Que eu possa estar presente em cada momento, grato por estar vivo e sóbrio.', lembrete: 'O presente é tudo que temos.' },
    { dia: 3, mes: 11, titulo: 'Amor Próprio', texto: 'A compulsão muitas vezes vem de uma falta de amor próprio. A recuperação nos ensina a nos cuidar, nos respeitar e nos valorizar — não por egoísmo, mas por necessidade.', pergunta: 'Estou me tratando com o mesmo amor e cuidado que ofereço aos outros?', oracao: 'Que eu aprenda a me amar da forma como Deus me ama.', lembrete: 'Amor próprio não é egoísmo.' },
    { dia: 4, mes: 11, titulo: 'Conexão com o Programa', texto: 'Manter-se conectado ao programa de JA é essencial para a recuperação. As reuniões, o padrinho, a leitura — cada elemento nos fortalece e nos mantém no caminho.', pergunta: 'Estou mantendo minha conexão com o programa e com minha irmandade?', oracao: 'Que eu nunca me afaste dos recursos que me mantêm sóbrio.', lembrete: 'O programa funciona se você trabalhar nele.' },
    { dia: 5, mes: 11, titulo: 'Paz Interior', texto: 'A paz que a recuperação traz não é ausência de problemas — é a capacidade de enfrentá-los sem recorrer ao jogo. Essa paz é conquistada dia a dia.', pergunta: 'Estou encontrando paz interior mesmo nos momentos de dificuldade?', oracao: 'Que a paz que ultrapassa todo entendimento habite em meu coração.', lembrete: 'A paz interior é a maior riqueza.' },
    { dia: 6, mes: 11, titulo: 'Natal Sóbrio', texto: 'As festas de fim de ano podem ser desafiadoras. Mas um natal sóbrio, presente e consciente vale mais do que qualquer celebração perdida na compulsão. Hoje escolho estar presente.', pergunta: 'Como posso vivenciar as festas de forma saudável e conectada com quem amo?', oracao: 'Que eu esteja presente e grato neste natal, celebrando a vida e a recuperação.', lembrete: 'O melhor presente é sua presença.' },
  ];

  get diasDoMes(): number[] {
    const total = new Date(2025, this.mesSelecionado + 1, 0).getDate();
    return Array.from({ length: total }, (_, i) => i + 1);
  }

  get reflexoesDoPeriodo(): Reflexao[] {
    return this.reflexoes.filter(r => r.mes === this.mesSelecionado);
  }

  selecionarMes(index: number) {
    this.mesSelecionado = index;
    this.diaSelecionado = null;
    this.reflexaoAtual = null;
  }

  selecionarDia(dia: number) {
    this.diaSelecionado = dia;
    this.reflexaoAtual = this.reflexoes.find(r => r.mes === this.mesSelecionado && r.dia === dia) || null;
  }

  voltar() {
    this.reflexaoAtual = null;
    this.diaSelecionado = null;
  }

  temReflexao(dia: number): boolean {
    return this.reflexoes.some(r => r.mes === this.mesSelecionado && r.dia === dia);
  }
}
