import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

interface HotlineEstado {
  estado: string;
  telefone: string;
}

@Component({
  selector: 'app-hotlines',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './hotlines.html',
  styleUrl: './hotlines.css'
})
export class Hotlines {
  readonly estados: HotlineEstado[] = [
    { estado: 'Bahia', telefone: '(a definir)' },
    { estado: 'Distrito Federal', telefone: '(a definir)' },
    { estado: 'Goiás', telefone: '(a definir)' },
    { estado: 'Minas Gerais', telefone: '(a definir)' },
    { estado: 'Paraná', telefone: '(a definir)' },
    { estado: 'Pernambuco', telefone: '(a definir)' },
    { estado: 'Rio de Janeiro', telefone: '(a definir)' },
    { estado: 'Rio Grande do Sul', telefone: '(a definir)' },
    { estado: 'Santa Catarina', telefone: '(a definir)' },
    { estado: 'São Paulo', telefone: '(a definir)' }
  ];
}
