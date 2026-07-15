import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { HotlineService } from '../../services/hotline/hotline';
import { HotlineRegiao } from '../../services/hotline/hotline.model';

@Component({
  selector: 'app-hotlines',
  standalone: true,
  imports: [CommonModule, RouterLink, ResourceLayout],
  templateUrl: './hotlines.html',
  styleUrl: './hotlines.css',
})
export class Hotlines implements OnInit {
  private readonly hotlineService = inject(HotlineService);

  readonly regioes = signal<HotlineRegiao[]>([]);
  readonly carregando = signal(true);
  readonly erro = signal(false);

  regiaoAbertaId: string | null = null;

  ngOnInit(): void {
    this.hotlineService.listarHotlines().subscribe({
      next: (resposta) => {
        this.carregando.set(false);
        if (resposta.success && resposta.data) {
          this.regioes.set(resposta.data);
        } else {
          this.erro.set(true);
        }
      },
      error: () => {
        this.carregando.set(false);
        this.erro.set(true);
      },
    });
  }

  toggleRegiao(regiaoId: string): void {
    this.regiaoAbertaId = this.regiaoAbertaId === regiaoId ? null : regiaoId;
  }
}
