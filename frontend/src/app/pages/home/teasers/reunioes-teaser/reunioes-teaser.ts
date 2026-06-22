import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-reunioes-teaser',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './reunioes-teaser.html',
  styleUrl: './reunioes-teaser.css'
})
export class ReunioesTeaser {

  reunioes = [
    {
      dia: '23',
      mes: 'MAR',
      semana: 'SEG',
      nome: 'Grupo Esperança',
      horario: '19:30 às 21:00',
      cidade: 'Campinas/SP'
    },
    {
      dia: '24',
      mes: 'MAR',
      semana: 'TER',
      nome: 'Grupo Renovação',
      horario: '20:00 às 21:30',
      cidade: 'Campinas/SP'
    },
    {
      dia: '25',
      mes: 'MAR',
      semana: 'QUA',
      nome: 'Grupo Novo Caminho',
      horario: '19:00 às 20:30',
      cidade: 'Hortolândia/SP'
    }
  ];

}
