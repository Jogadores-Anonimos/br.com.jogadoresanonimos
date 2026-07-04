import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';

interface PublicacaoLiteratura {
  titulo: string;
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
    { titulo: 'Caderno de 20 Perguntas — Você é um Jogador Compulsivo?' },
    { titulo: 'Perguntas e Respostas Sobre o Jogo — Folheto' },
    { titulo: 'Sugestões da JA para Lidar com o Impulso de Jogar' },
    { titulo: 'Primeiro Passo — A Base da Recuperação do Jogo Compulsivo' },
    { titulo: 'Folheto Trabalhando os Passos' },
    { titulo: 'Caderno de Unidade dos 12 Passos' },
    { titulo: 'Livro de Estudo em Grupo sobre os Passos de Unidade' },
    { titulo: 'Livro Combinado (Combo Book)' },
    { titulo: 'Jovens Jogadores em Recuperação' },
    { titulo: 'Manual de Grupo da JA' },
    { titulo: 'Código de Orientação da JA' },
    { titulo: 'Pacote de Informações da JA' },
    { titulo: 'Sugestões da JA para Retenção de Novos Membros' },
  ];
}
