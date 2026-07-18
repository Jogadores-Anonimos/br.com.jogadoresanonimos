import { Component, OnInit, inject, signal } from '@angular/core';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { Cms } from '../../services/cms/cms';
import { CmsBlocoTexto } from '../../services/cms/cms.model';

@Component({
  selector: 'app-uma-nova-chance',
  standalone: true,
  imports: [ResourceLayout],
  templateUrl: './uma-nova-chance.html',
  styleUrl: './uma-nova-chance.css',
})
export class UmaNovaChance implements OnInit {
  private readonly cms = inject(Cms);

  readonly blocosTexto = signal<CmsBlocoTexto[]>([]);

  ngOnInit(): void {
    this.cms.getPaginaPublica('uma-nova-chance').subscribe((resposta) => {
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
