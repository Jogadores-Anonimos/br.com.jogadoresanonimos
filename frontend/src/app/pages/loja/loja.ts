import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AvisoEmailNaoConfirmado } from '../../components/aviso-email-nao-confirmado/aviso-email-nao-confirmado';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { Auth } from '../../services/auth/auth';

interface ProdutoPreview {
  titulo: string;
  preco: string;
}

@Component({
  selector: 'app-loja',
  standalone: true,
  imports: [RouterLink, ResourceLayout, AvisoEmailNaoConfirmado],
  templateUrl: './loja.html',
  styleUrl: './loja.css',
})
export class Loja {
  readonly bannerSrc = '/images/home/reuniao-discussao.jpg';

  readonly auth = inject(Auth);

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
