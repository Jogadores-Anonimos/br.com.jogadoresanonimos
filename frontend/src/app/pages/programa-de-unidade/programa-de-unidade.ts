import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { Cms } from '../../services/cms/cms';
import { CmsBlocoTexto } from '../../services/cms/cms.model';

@Component({
  selector: 'app-programa-de-unidade',
  standalone: true,
  imports: [RouterLink, ResourceLayout],
  templateUrl: './programa-de-unidade.html',
  styleUrl: './programa-de-unidade.css',
})
export class ProgramaDeUnidade implements OnInit {
  private readonly cms = inject(Cms);

  readonly blocosTexto = signal<CmsBlocoTexto[]>([]);

  ngOnInit(): void {
    this.cms.getPaginaPublica('programa-de-unidade').subscribe((resposta) => {
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
