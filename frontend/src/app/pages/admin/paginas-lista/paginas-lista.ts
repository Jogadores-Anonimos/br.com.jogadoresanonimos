import { DatePipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Cms } from '../../../services/cms/cms';
import { CmsPaginaAdminResumo } from '../../../services/cms/cms.model';

@Component({
  selector: 'app-paginas-lista',
  standalone: true,
  imports: [RouterLink, DatePipe],
  templateUrl: './paginas-lista.html',
  styleUrl: './paginas-lista.css',
})
export class PaginasLista implements OnInit {
  private readonly cms = inject(Cms);

  readonly paginas = signal<CmsPaginaAdminResumo[]>([]);
  readonly carregando = signal(true);
  readonly erro = signal<string | null>(null);

  ngOnInit(): void {
    this.cms.listarPaginasAdmin().subscribe((envelope) => {
      this.carregando.set(false);
      if (envelope.success && envelope.data) {
        this.paginas.set(envelope.data);
      } else {
        this.erro.set(envelope.error?.message ?? 'Não foi possível carregar as páginas.');
      }
    });
  }
}
