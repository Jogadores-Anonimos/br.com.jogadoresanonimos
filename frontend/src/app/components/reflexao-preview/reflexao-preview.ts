import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import {
  ReflexaoDoDia,
  ReflexaoDoDiaService,
} from '../../services/reflexao-do-dia';

const TAMANHO_MAX_TRUNCADO = 220;

/**
 * Card de preview da "Reflexão do Dia" usado na Home. Mostra APENAS a
 * reflexão do dia atual (sem grade de navegação de mês/dia — isso só
 * existe na página completa /reflexao-do-dia), com texto truncado e um
 * CTA para a página completa.
 */
@Component({
  selector: 'app-reflexao-preview',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './reflexao-preview.html',
  styleUrl: './reflexao-preview.css',
})
export class ReflexaoPreview implements OnInit {
  reflexao: ReflexaoDoDia | null = null;
  carregando = true;
  erro: string | null = null;

  constructor(private readonly reflexaoService: ReflexaoDoDiaService) {}

  ngOnInit(): void {
    this.reflexaoService.obterReflexaoDeHoje().subscribe((resposta) => {
      this.carregando = false;

      if (!resposta.success || !resposta.data) {
        this.erro = resposta.error ?? 'Não foi possível carregar a reflexão de hoje.';
        return;
      }

      this.reflexao = resposta.data;
    });
  }

  get textoTruncado(): string {
    const texto = this.reflexao?.texto ?? '';
    if (texto.length <= TAMANHO_MAX_TRUNCADO) {
      return texto;
    }
    return `${texto.slice(0, TAMANHO_MAX_TRUNCADO).trimEnd()}...`;
  }
}
