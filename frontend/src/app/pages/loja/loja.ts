import { Component } from '@angular/core';

interface ProdutoPreview {
  titulo: string;
  preco: string;
}

@Component({
  selector: 'app-loja',
  standalone: true,
  imports: [],
  templateUrl: './loja.html',
  styleUrl: './loja.css'
})
export class Loja {
  readonly produtos: ProdutoPreview[] = [
    { titulo: 'Produto em preparação', preco: '[a definir]' },
    { titulo: 'Produto em preparação', preco: '[a definir]' },
    { titulo: 'Produto em preparação', preco: '[a definir]' }
  ];
}
