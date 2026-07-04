import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';

@Component({
  selector: 'app-programa-de-recuperacao',
  standalone: true,
  imports: [RouterLink, ResourceLayout],
  templateUrl: './programa-de-recuperacao.html',
  styleUrl: './programa-de-recuperacao.css',
})
export class ProgramaDeRecuperacao {
  readonly passos: string[] = [
    'Admitimos que éramos impotentes perante o jogo e que nossas vidas haviam se tornado ingovernáveis.',
    'Viemos a acreditar que um Poder superior a nós mesmos poderia devolver-nos à sanidade.',
    'Decidimos entregar nossa vontade e nossa vida aos cuidados de Deus, na forma em que O concebíamos.',
    'Fizemos minucioso e destemido inventário moral de nós mesmos.',
    'Admitimos perante Deus, perante nós mesmos e perante outro ser humano a natureza exata de nossas falhas.',
    'Prontificamo-nos inteiramente a deixar que Deus removesse todos esses defeitos de caráter.',
    'Humildemente pedimos a Ele que removesse nossos defeitos.',
    'Fizemos uma relação de todas as pessoas que tínhamos prejudicado e nos dispusemos a reparar os danos a elas causados.',
    'Fizemos reparações diretas dos danos causados a tais pessoas, sempre que possível, salvo quando fazê-lo significasse prejudicá-las ou a outrem.',
    'Continuamos fazendo o inventário pessoal e, quando estávamos errados, nós o admitíamos prontamente.',
    'Procuramos, através da prece e da meditação, melhorar nosso contato consciente com Deus.',
    'Tendo experimentado um despertar espiritual, procuramos transmitir esta mensagem a outros jogadores compulsivos.',
  ];
}
