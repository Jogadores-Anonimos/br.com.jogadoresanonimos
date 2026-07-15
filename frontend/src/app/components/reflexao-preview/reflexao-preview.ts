import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { NOMES_MESES } from '../../config/meses.config';
import {
  ReflexaoDoDia,
  ReflexaoDoDiaService,
} from '../../services/reflexao-do-dia/reflexao-do-dia';

const TAMANHO_MAX_TRUNCADO = 220;

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

  get dataFormatada(): string {
    if (!this.reflexao) {
      return '';
    }
    const nomeMes = NOMES_MESES[this.reflexao.mes - 1] ?? '';
    return `${this.reflexao.dia} de ${nomeMes}`;
  }
}
