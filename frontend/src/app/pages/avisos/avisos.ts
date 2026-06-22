import { Component } from '@angular/core';

@Component({
  selector: 'app-avisos',
  imports: [],
  templateUrl: './avisos.html',
  styleUrl: './avisos.css',
})
export class Avisos {
  showModal = false;

  abrirModal() {
    this.showModal = true;
  }

  fecharModal() {
    this.showModal = false;
  }
}
