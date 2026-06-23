import { Component } from '@angular/core';
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-reflexoes-teaser',
    imports: [
        RouterLink
    ],
  templateUrl: './reflexoes-teaser.html',
  styleUrl: './reflexoes-teaser.css',
})
export class ReflexoesTeaser {
  diaAtual = new Date().getDate();
  mesAtual = ['Janeiro','Fevereiro','Março','Abril','Maio','Junho','Julho','Agosto','Setembro','Outubro','Novembro','Dezembro'][new Date().getMonth()];
}
