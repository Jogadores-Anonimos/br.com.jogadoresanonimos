import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';

import { RelatosService } from '../../../services/relatos/relatos';

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
