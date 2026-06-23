import { Component } from '@angular/core';

@Component({
  selector: 'app-doze-passos',
  standalone: true,
  imports: [],
  templateUrl: './doze-passos.html',
  styleUrl: './doze-passos.css',
})
export class DozePassos {
  abaAtiva: '12passos' | 'unidade' = '12passos';
}
