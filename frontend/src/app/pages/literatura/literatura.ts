import { Component, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';

interface Literatura {
  titulo: string;
  formato: string;
  descricao: string;
  imagem: string;
  tipo: 'folheto' | 'livro';
}

@Component({
  selector: 'app-literatura',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './literatura.html',
  styleUrl: './literatura.css',
  encapsulation: ViewEncapsulation.None
})
export class LiteraturaComponent {
  filtroAtivo: 'todos' | 'folheto' | 'livro' = 'todos';

  literaturas: Literatura[] = [
    {
      titulo: 'Sua Primeira Reunião',
      formato: 'Folheto 1 dobra · Formato 15x21 cm',
      descricao: 'Tem o objetivo de fornecer os primeiros esclarecimentos da irmandade de Jogadores Anônimos e desejar boas-vindas ao novo membro. Esse folheto é dado aos ingressantes em Jogadores Anônimos.',
      imagem: 'images/literatura/sua-primeira-reuniao.jpg',
      tipo: 'folheto'
    },
    {
      titulo: 'Um Novo Começo',
      formato: '104 páginas · Formato 15x21 cm',
      descricao: 'Apresenta a proposta de Jogadores Anônimos. Trata dos Passos de Recuperação e de Unidade, Apadrinhamento, Ferramentas de JA, Depoimentos de Recuperação, e outros temas necessários a todos os membros da Irmandade.',
      imagem: 'images/literatura/um-novo-comeco.jpg',
      tipo: 'livro'
    },
    {
      titulo: 'As 20 Perguntas',
      formato: 'Folheto · Formato 15x21 cm',
      descricao: 'Um questionário simples e direto para ajudar a identificar sinais de compulsão com jogos de azar. Ferramenta de autoavaliação amplamente utilizada nas reuniões de JA.',
      imagem: 'images/literatura/20-perguntas.jpg',
      tipo: 'folheto'
    },
    {
      titulo: 'Os 12 Passos de Jogadores Anônimos',
      formato: '48 páginas · Formato 15x21 cm',
      descricao: 'Apresenta e explica cada um dos Doze Passos do programa de recuperação de Jogadores Anônimos, com reflexões e orientações para quem está trilhando o caminho da sobriedade.',
      imagem: 'images/literatura/12-passos.jpg',
      tipo: 'livro'
    },
    {
      titulo: 'O Programa de Recuperação',
      formato: 'Folheto · Formato 15x21 cm',
      descricao: 'Explica de forma objetiva como funciona o programa de recuperação de JA, suas ferramentas e como o membro pode aproveitar ao máximo a irmandade.',
      imagem: 'images/literatura/programa-recuperacao.jpg',
      tipo: 'folheto'
    },
    {
      titulo: 'Depoimentos de Recuperação',
      formato: '80 páginas · Formato 15x21 cm',
      descricao: 'Relatos de membros que encontraram a recuperação através do programa de Jogadores Anônimos. Histórias reais de esperança, força e experiência que inspiram quem ainda sofre.',
      imagem: 'images/literatura/depoimentos.jpg',
      tipo: 'livro'
    },
    {
      titulo: 'O Apadrinhamento em JA',
      formato: 'Folheto · Formato 15x21 cm',
      descricao: 'Esclarece o papel do padrinho e do afilhado no programa de Jogadores Anônimos, explicando como essa relação fortalece a recuperação de ambos.',
      imagem: 'images/literatura/apadrinhamento.jpg',
      tipo: 'folheto'
    },
    {
      titulo: 'Reflexões Diárias',
      formato: '365 páginas · Formato 15x21 cm',
      descricao: 'Uma reflexão para cada dia do ano, com mensagens de esperança, fé e recuperação para fortalecer a jornada do membro de Jogadores Anônimos no dia a dia.',
      imagem: 'images/literatura/reflexoes.jpg',
      tipo: 'livro'
    },
  ];

  get literaturasFiltradas(): Literatura[] {
    if (this.filtroAtivo === 'todos') return this.literaturas;
    return this.literaturas.filter(l => l.tipo === this.filtroAtivo);
  }
}
