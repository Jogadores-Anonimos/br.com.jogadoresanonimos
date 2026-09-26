import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { ResourceLayout } from '../../components/resource-layout/resource-layout';

interface ProdutoPreview {
  titulo: string;
  preco: string;
}

@Component({
  selector: 'app-loja',
  standalone: true,
  imports: [RouterLink, ResourceLayout],
  templateUrl: './loja.html',
  styleUrl: './loja.css',
})
export class Loja {
  readonly bannerSrc = '/images/home/reuniao-discussao.webp';

  readonly categorias: string[] = [
    'Todos os Produtos',
    'Livros',
    'Outras Publicações',
    'Broches, Chaveiros & Medalhas',
    'Folhetos',
    'Vídeos/Multimídia',
  ];

  readonly produtos: ProdutoPreview[] = [
    { titulo: 'Produto em preparação', preco: '[a definir]' },
    { titulo: 'Produto em preparação', preco: '[a definir]' },
    { titulo: 'Produto em preparação', preco: '[a definir]' },
  ];
}
