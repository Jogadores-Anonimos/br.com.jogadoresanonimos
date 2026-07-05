import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';

interface PublicacaoLiteratura {
  titulo: string;
  imagem: string;
  formato: string;
  descricao: string;
}

@Component({
  selector: 'app-literatura',
  standalone: true,
  imports: [RouterLink, ResourceLayout],
  templateUrl: './literatura.html',
  styleUrl: './literatura.css',
})
export class Literatura {
  readonly publicacoes: PublicacaoLiteratura[] = [
    {
      titulo: 'Sua Primeira Reunião',
      imagem: '/images/literatura/sua-primeira-reuniao.jpg',
      formato: 'Folheto de uma dobra, 15×21 cm',
      descricao:
        'Traz esclarecimentos iniciais sobre os Jogadores Anônimos e acolhe novos membros.',
    },
    {
      titulo: 'Um Novo Começo',
      imagem: '/images/literatura/um-novo-comeco.jpg',
      formato: 'Manual, 104 páginas, 15×21 cm',
      descricao:
        'Aborda os passos de recuperação, apadrinhamento, ferramentas e depoimentos de recuperação.',
    },
    {
      titulo: 'Guia para o Inventário do Quarto Passo',
      imagem: '/images/literatura/guia-inventario-quarto-passo.jpg',
      formato: 'Caderno de exercícios, 18 páginas, 15×21 cm',
      descricao:
        "Auxilia na reflexão do 'inventário moral' para quem está no programa.",
    },
    {
      titulo: 'Combo',
      imagem: '/images/literatura/combo.jpg',
      formato: 'Guia de bolso, 18 páginas, 9×14 cm',
      descricao:
        'Resume o conteúdo do programa; lido durante as reuniões de terapia.',
    },
    {
      titulo: 'Em Direção aos Noventa Dias',
      imagem: '/images/literatura/em-direcao-noventa-dias.jpg',
      formato: 'Guia de bolso, 20 páginas, 9×14 cm',
      descricao:
        'Traz ferramentas de abstinência para o período inicial de 90 dias de recuperação.',
    },
    {
      titulo: 'Além dos Noventa Dias',
      imagem: '/images/literatura/alem-noventa-dias.jpg',
      formato: 'Guia de bolso, 26 páginas, 9×14 cm',
      descricao:
        'Recursos para superar obstáculos de recuperação para membros já estabelecidos.',
    },
  ];
}
