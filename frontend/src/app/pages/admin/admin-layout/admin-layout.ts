import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

interface AdminMenuItem {
  label: string;
  rota: string;
}

interface AdminMenuGrupo {
  titulo: string;
  itens: AdminMenuItem[];
  expansivel?: boolean;
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
      titulo: 'Páginas do site',
      expansivel: true,
      itens: [
        { label: 'Sobre', rota: 'paginas/sobre' },
        { label: 'História', rota: 'paginas/historia' },
        { label: 'Anonimato', rota: 'paginas/anonimato' },
        { label: 'Privacidade', rota: 'paginas/privacidade' },
        { label: 'Termos', rota: 'paginas/termos' },
        { label: 'Jogo Compulsivo', rota: 'paginas/jogo-compulsivo' },
        { label: 'Uma Nova Chance', rota: 'paginas/uma-nova-chance' },
        { label: '20 Perguntas', rota: 'paginas/vinte-perguntas' },
        { label: 'Contribuir', rota: 'paginas/contribuir' },
      ],
    },
    {
      titulo: 'Estrutura de apoio',
      itens: [
        { label: 'Reuniões', rota: 'reunioes' },
        { label: 'Endereços', rota: 'escritorios-regionais' },
        { label: 'Linhas de Ajuda', rota: 'hotlines' },
        { label: 'Perguntas e Respostas', rota: 'perguntas-e-respostas' },
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
