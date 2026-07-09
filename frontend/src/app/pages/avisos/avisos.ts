import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { Aviso, AvisosService } from '../../services/avisos';

@Component({
  selector: 'app-avisos',
  standalone: true,
  imports: [CommonModule, ResourceLayout],
  templateUrl: './avisos.html',
  styleUrl: './avisos.css',
})
export class Avisos implements OnInit {
  // já vem ordenada do service (fixado primeiro, depois data decrescente) — não reordenar aqui
  readonly avisos = signal<Aviso[]>([]);
  readonly carregando = signal(true);
  readonly erro = signal(false);

  constructor(private readonly avisosService: AvisosService) {}

  ngOnInit(): void {
    this.avisosService.listarAvisos().subscribe({
      next: (resposta) => {
        this.carregando.set(false);
        if (resposta.success && resposta.data) {
          this.avisos.set(resposta.data);
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
