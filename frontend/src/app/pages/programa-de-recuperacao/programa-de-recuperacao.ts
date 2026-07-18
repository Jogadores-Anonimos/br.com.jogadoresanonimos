import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { Cms } from '../../services/cms/cms';
import { CmsBlocoTexto } from '../../services/cms/cms.model';

@Component({
  selector: 'app-programa-de-recuperacao',
  standalone: true,
  imports: [RouterLink, ResourceLayout],
  templateUrl: './programa-de-recuperacao.html',
  styleUrl: './programa-de-recuperacao.css',
})
export class ProgramaDeRecuperacao implements OnInit {
  private readonly cms = inject(Cms);

  readonly blocosTexto = signal<CmsBlocoTexto[]>([]);

  ngOnInit(): void {
    this.cms.getPaginaPublica('programa-de-recuperacao').subscribe((resposta) => {
      if (resposta.success && resposta.data) {
        this.blocosTexto.set(
          resposta.data.blocos.filter(
            (bloco): bloco is CmsBlocoTexto => bloco.tipo === 'texto',
          ),
        );
      }
    });
  }
}
