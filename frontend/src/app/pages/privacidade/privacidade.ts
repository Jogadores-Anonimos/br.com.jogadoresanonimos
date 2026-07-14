import { Component, OnInit, inject } from '@angular/core';
import { Cms } from '../../services/cms';
import { CmsBlocoTexto } from '../../services/cms.model';

@Component({
  selector: 'app-privacidade',
  standalone: true,
  imports: [],
  templateUrl: './privacidade.html',
  styleUrl: './privacidade.css',
})
export class Privacidade implements OnInit {
  private readonly cms = inject(Cms);

  blocosTexto: CmsBlocoTexto[] = [];

  ngOnInit(): void {
    this.cms.getPaginaPublica('privacidade').subscribe((resposta) => {
      if (resposta.success && resposta.data) {
        this.blocosTexto = resposta.data.blocos.filter(
          (bloco): bloco is CmsBlocoTexto => bloco.tipo === 'texto',
        );
      }
    });
  }
}
