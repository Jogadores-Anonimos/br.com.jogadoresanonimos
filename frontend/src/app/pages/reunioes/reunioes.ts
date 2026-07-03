import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

type CategoriaReuniao = 'presencial' | 'nacional' | 'virtual' | 'telefone';

interface FiltroCategoria {
  valor: CategoriaReuniao;
  titulo: string;
  descricao: string;
}

interface Reuniao {
  nome: string;
  categoria: CategoriaReuniao;
  dia: string;
  horario: string;
  local: string;
}

@Component({
  selector: 'app-reunioes',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './reunioes.html',
  styleUrl: './reunioes.css',
})
export class Reunioes {
  readonly categorias: FiltroCategoria[] = [
    {
      valor: 'presencial',
      titulo: 'Presencial',
      descricao: 'Reuniões em grupos físicos perto de você.',
    },
    {
      valor: 'nacional',
      titulo: 'Nacional',
      descricao: 'Grupos em diferentes estados do Brasil.',
    },
    {
      valor: 'virtual',
      titulo: 'Virtual',
      descricao: 'Reuniões por videochamada, de onde você estiver.',
    },
    {
      valor: 'telefone',
      titulo: 'Telefone',
      descricao: 'Reuniões por chamada de voz, sem precisar de vídeo.',
    },
  ];

  /**
   * Dados de exemplo — nenhuma reunião, endereço ou horário aqui é real.
   * Tudo marcado como "[a definir]" até que a JA Brasil confirme os
   * grupos ativos.
   */
  readonly reunioes: Reuniao[] = [
    {
      nome: 'Grupo [a definir]',
      categoria: 'presencial',
      dia: '[a definir]',
      horario: '[a definir]',
      local: '[endereço a definir], [cidade a definir]',
    },
    {
      nome: 'Grupo [a definir]',
      categoria: 'presencial',
      dia: '[a definir]',
      horario: '[a definir]',
      local: '[endereço a definir], [cidade a definir]',
    },
    {
      nome: 'Grupo [a definir]',
      categoria: 'nacional',
      dia: '[a definir]',
      horario: '[a definir]',
      local: '[cidade/estado a definir]',
    },
    {
      nome: 'Grupo [a definir]',
      categoria: 'virtual',
      dia: '[a definir]',
      horario: '[a definir]',
      local: 'Link de acesso [a definir]',
    },
    {
      nome: 'Grupo [a definir]',
      categoria: 'telefone',
      dia: '[a definir]',
      horario: '[a definir]',
      local: 'Número de acesso [a definir]',
    },
  ];

  categoriaSelecionada: CategoriaReuniao = 'presencial';

  selecionarCategoria(categoria: CategoriaReuniao): void {
    this.categoriaSelecionada = categoria;
  }

  get reunioesFiltradas(): Reuniao[] {
    return this.reunioes.filter((r) => r.categoria === this.categoriaSelecionada);
  }
}
