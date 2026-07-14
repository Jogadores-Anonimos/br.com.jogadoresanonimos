import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';

interface Passo {
  numero: number;
  texto: string;
}

@Component({
  selector: 'app-programa-de-recuperacao',
  standalone: true,
  imports: [RouterLink, ResourceLayout],
  templateUrl: './programa-de-recuperacao.html',
  styleUrl: './programa-de-recuperacao.css',
})
export class ProgramaDeRecuperacao {
  readonly passos: Passo[] = [
    { numero: 1, texto: 'Admitimos que éramos impotentes perante o jogo, que nossas vidas se tornaram incontroláveis.' },
    { numero: 2, texto: 'Viemos a acreditar que um Poder Superior a nós mesmos poderia devolver-nos à sanidade.' },
    { numero: 3, texto: 'Decidimos entregar nossa vontade e nossa vida aos cuidados de Deus, na forma em que O concebêssemos.' },
    { numero: 4, texto: 'Fizemos minucioso e destemido inventário moral de nós mesmos.' },
    { numero: 5, texto: 'Admitimos a Deus, a nós mesmos e a outro ser humano a natureza exata de nossas falhas.' },
    { numero: 6, texto: 'Estávamos inteiramente dispostos a permitir que Deus removesse todos esses defeitos de caráter.' },
    { numero: 7, texto: 'Humildemente rogamos a Ele que removesse nossas imperfeições.' },
    { numero: 8, texto: 'Fizemos uma lista de todas as pessoas que havíamos prejudicado e nos dispusemos a reparar os danos a todas elas.' },
    { numero: 9, texto: 'Fizemos reparações diretas a estas pessoas sempre que possível, exceto quando fazê-lo significasse prejudicá-las ou a outrem.' },
    { numero: 10, texto: 'Continuamos fazendo o inventário pessoal e, quando estávamos errados, nós o admitíamos prontamente.' },
    { numero: 11, texto: 'Procuramos, através da prece e da meditação, melhorar nosso contato consciente com Deus, na forma em que O concebêssemos, rogando apenas o conhecimento de Sua vontade em relação a nós e o poder de realizar essa vontade.' },
    { numero: 12, texto: 'Tendo experimentado um despertar espiritual como resultado destes passos, procuramos levar esta mensagem a outros jogadores compulsivos e praticar estes princípios em todas as nossas atividades.' },
  ];
}
