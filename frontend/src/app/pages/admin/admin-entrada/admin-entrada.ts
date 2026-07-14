import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';

import { RelatosService } from '../../../services/relatos';

// Tela de trânsito: não renderiza nada visível, só decide pra onde a
// entrada do admin deve ir. Se houver relato pendente, prioriza a fila
// de moderação (é a única ação com urgência real neste admin); senão,
// cai em Avisos.
@Component({
  selector: 'app-admin-entrada',
  standalone: true,
  template: '',
})
export class AdminEntrada implements OnInit {
  private readonly relatosService = inject(RelatosService);
  private readonly router = inject(Router);

  ngOnInit(): void {
    this.relatosService.listarPorStatusAdmin('pending', 0, 1).subscribe((envelope) => {
      const temPendente = envelope.success && (envelope.data?.totalElementos ?? 0) > 0;
      this.router.navigateByUrl(temPendente ? '/admin/relatos' : '/admin/avisos');
    });
  }
}
