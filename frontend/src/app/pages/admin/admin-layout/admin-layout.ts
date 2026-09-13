import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

interface AdminMenuItem {
  label: string;
  rota: string;
}

interface AdminMenuGrupo {
  titulo: string;
  itens: AdminMenuItem[];
}

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './admin-layout.html',
  styleUrl: './admin-layout.css',
})
export class AdminLayout {
  menuAberto = false;
  fechandoPorNavegacao = false;

  toggleMenu(): void {
    this.fechandoPorNavegacao = false;
    this.menuAberto = !this.menuAberto;
  }

  fecharMenu(): void {
    this.fechandoPorNavegacao = true;
    this.menuAberto = false;
  }

  readonly grupos: AdminMenuGrupo[] = [
    {
      titulo: 'Conteúdo do dia a dia',
      itens: [
        { label: 'Avisos', rota: 'avisos' },
        { label: 'Reflexão do Dia', rota: 'reflexao-do-dia' },
        { label: 'Mural de Apoio', rota: 'mural-de-apoio' },
        { label: 'Relatos', rota: 'relatos' },
      ],
    },
    {
      titulo: 'Estrutura de apoio',
      itens: [
        { label: 'Endereços', rota: 'enderecos' },
        { label: 'Escritórios', rota: 'escritorios' },
        { label: 'Linhas de Ajuda', rota: 'hotlines' },
        { label: 'Perguntas Frequentes', rota: 'perguntas-e-respostas' },
        { label: '20 Perguntas', rota: 'vinte-perguntas' },
      ],
    },
    {
      titulo: 'Conta e biblioteca',
      itens: [
        { label: 'Literatura', rota: 'literatura' },
        { label: 'Usuários', rota: 'usuarios' },
        { label: 'Segurança', rota: 'seguranca' },
        { label: 'Auditoria', rota: 'auditoria' },
      ],
    },
  ];
}
