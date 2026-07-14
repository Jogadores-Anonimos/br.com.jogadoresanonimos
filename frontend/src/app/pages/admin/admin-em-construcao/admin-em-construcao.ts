import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-admin-em-construcao',
  standalone: true,
  templateUrl: './admin-em-construcao.html',
  styleUrl: './admin-em-construcao.css',
})
export class AdminEmConstrucao {
  private readonly route = inject(ActivatedRoute);

  readonly titulo: string = this.route.snapshot.data['titulo'] ?? 'Área administrativa';
}
