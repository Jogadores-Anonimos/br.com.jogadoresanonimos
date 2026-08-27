import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { EscritorioRegionalService } from '../../services/escritorio-regional/escritorio-regional';
import { EscritorioServico } from '../../services/escritorio-regional/escritorio-regional.model';

@Component({
  selector: 'app-escritorios',
  standalone: true,
  imports: [CommonModule, ResourceLayout],
  templateUrl: './escritorios.html',
  styleUrl: './escritorios.css',
})
export class Escritorios implements OnInit {
  private readonly escritorioRegionalService = inject(EscritorioRegionalService);

  readonly escritorios = signal<EscritorioServico[]>([]);
  readonly carregando = signal(true);
  readonly erro = signal(false);

  ngOnInit(): void {
    this.escritorioRegionalService.listarEstrutura().subscribe({
      next: (resposta) => {
        this.carregando.set(false);
        if (resposta.success && resposta.data) {
          this.escritorios.set(resposta.data.escritorios);
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
}
