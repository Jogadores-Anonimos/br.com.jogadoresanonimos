import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';

interface HotlineContato {
  label: string;
  telefone: string;
}

interface HotlineEstado {
  estado: string;
  linhas: HotlineContato[];
}

@Component({
  selector: 'app-hotlines',
  standalone: true,
  imports: [RouterLink, ResourceLayout],
  templateUrl: './hotlines.html',
  styleUrl: './hotlines.css',
})
export class Hotlines {
  readonly estados: HotlineEstado[] = [
    { estado: 'Bahia', linhas: [{ label: 'Linha de apoio', telefone: '(a definir)' }] },
    {
      estado: 'Distrito Federal',
      linhas: [{ label: 'Linha de apoio', telefone: '(a definir)' }],
    },
    { estado: 'Goiás', linhas: [{ label: 'Linha de apoio', telefone: '(a definir)' }] },
    {
      estado: 'Minas Gerais',
      linhas: [{ label: 'Linha de apoio', telefone: '(a definir)' }],
    },
    { estado: 'Paraná', linhas: [{ label: 'Linha de apoio', telefone: '(a definir)' }] },
    {
      estado: 'Pernambuco',
      linhas: [{ label: 'Linha de apoio', telefone: '(a definir)' }],
    },
    {
      estado: 'Rio de Janeiro',
      linhas: [{ label: 'Linha de apoio', telefone: '(a definir)' }],
    },
    {
      estado: 'Rio Grande do Sul',
      linhas: [{ label: 'Linha de apoio', telefone: '(a definir)' }],
    },
    {
      estado: 'Santa Catarina',
      linhas: [{ label: 'Linha de apoio', telefone: '(a definir)' }],
    },
    { estado: 'São Paulo', linhas: [{ label: 'Linha de apoio', telefone: '(a definir)' }] },
  ];

  estadoAberto: string | null = null;

  toggleEstado(estado: string): void {
    this.estadoAberto = this.estadoAberto === estado ? null : estado;
  }
}
