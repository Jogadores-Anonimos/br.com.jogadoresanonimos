import { Component, OnInit, inject } from '@angular/core';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { Cms } from '../../services/cms';
import { CmsBlocoTexto } from '../../services/cms.model';

@Component({
  selector: 'app-anonimato',
  standalone: true,
  imports: [ResourceLayout],
  templateUrl: './anonimato.html',
  styleUrl: './anonimato.css',
})
export class Anonimato implements OnInit {
  private readonly cms = inject(Cms);

  blocosTexto: CmsBlocoTexto[] = [];

  ngOnInit(): void {
    this.cms.getPaginaPublica('anonimato').subscribe((resposta) => {
      if (resposta.success && resposta.data) {
        this.blocosTexto = resposta.data.blocos.filter(
          (bloco): bloco is CmsBlocoTexto => bloco.tipo === 'texto',
        );
      }
    });
  }
}
