import { Component, OnInit, inject, signal } from '@angular/core';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { Cms } from '../../services/cms/cms';
import { CmsBlocoTexto } from '../../services/cms/cms.model';

@Component({
  selector: 'app-sobre',
  standalone: true,
  imports: [ResourceLayout],
  templateUrl: './sobre.html',
  styleUrl: './sobre.css',
})
export class Sobre implements OnInit {
  private readonly cms = inject(Cms);

  readonly blocosTexto = signal<CmsBlocoTexto[]>([]);

  ngOnInit(): void {
    this.cms.getPaginaPublica('sobre').subscribe((resposta) => {
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
