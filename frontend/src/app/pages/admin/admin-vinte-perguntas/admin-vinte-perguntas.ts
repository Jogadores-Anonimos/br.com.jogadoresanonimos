import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { mapearErrosCampo } from '../../../services/api-envelope';
import { VintePerguntasService } from '../../../services/vinte-perguntas/vinte-perguntas';
import { VinteQuestaoResponse } from '../../../services/vinte-perguntas/vinte-perguntas.model';

@Component({
  selector: 'app-admin-vinte-perguntas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-vinte-perguntas.html',
  styleUrl: './admin-vinte-perguntas.css',
})
export class AdminVintePerguntas implements OnInit {
  private readonly vintePerguntas = inject(VintePerguntasService);

  carregando = true;
  erroCarregamento = '';
  perguntas: VinteQuestaoResponse[] = [];

  modo: 'lista' | 'formulario' = 'lista';
  perguntaEmEdicao: VinteQuestaoResponse | null = null;
  textoFormulario = '';
  salvando = false;
  erroFormulario = '';
  errosCampo: Record<string, string> = {};

  confirmandoExclusao: string | null = null;
  excluindo = false;
  erroExclusao = '';

  movendoId: string | null = null;
  erroMover = '';

  ngOnInit(): void {
    this.carregarPerguntas();
  }

  private carregarPerguntas(): void {
    this.carregando = true;
    this.erroCarregamento = '';

    this.vintePerguntas.listarAdmin().subscribe((envelope) => {
      this.carregando = false;

      if (envelope.success && envelope.data) {
        this.perguntas = [...envelope.data].sort((a, b) => a.ordem - b.ordem);
      } else {
        this.perguntas = [];
        this.erroCarregamento = envelope.error?.message ?? 'Não foi possível carregar as perguntas.';
      }
    });
  }

  abrirNovo(): void {
    this.perguntaEmEdicao = null;
    this.textoFormulario = '';
    this.erroFormulario = '';
    this.errosCampo = {};
    this.modo = 'formulario';
  }

  abrirEdicao(pergunta: VinteQuestaoResponse): void {
    this.perguntaEmEdicao = pergunta;
    this.textoFormulario = pergunta.texto;
    this.erroFormulario = '';
    this.errosCampo = {};
    this.modo = 'formulario';
  }

  cancelar(): void {
    this.modo = 'lista';
    this.perguntaEmEdicao = null;
    this.erroFormulario = '';
    this.errosCampo = {};
  }

  salvar(): void {
    this.errosCampo = {};

    if (!this.textoFormulario.trim()) {
      this.errosCampo['texto'] = 'Texto é obrigatório.';
      this.erroFormulario = 'Texto é obrigatório.';
      return;
    }

    this.salvando = true;
    this.erroFormulario = '';

    const texto = this.textoFormulario.trim();
    const acao = this.perguntaEmEdicao
      ? this.vintePerguntas.atualizarPergunta(this.perguntaEmEdicao.id, { texto })
      : this.vintePerguntas.criarPergunta(texto);

    acao.subscribe((envelope) => {
      this.salvando = false;

      if (envelope.success && envelope.data) {
        const salva = envelope.data;
        this.perguntas = this.perguntaEmEdicao
          ? this.perguntas.map((p) => (p.id === salva.id ? salva : p))
          : [...this.perguntas, salva];
        this.modo = 'lista';
        this.perguntaEmEdicao = null;
      } else {
        this.erroFormulario = envelope.error?.message ?? 'Não foi possível salvar a pergunta.';
        this.errosCampo = mapearErrosCampo(envelope.error?.fields);
      }
    });
  }

  pedirConfirmacaoExclusao(pergunta: VinteQuestaoResponse): void {
    this.confirmandoExclusao = pergunta.id;
    this.erroExclusao = '';
  }

  cancelarExclusao(): void {
    this.confirmandoExclusao = null;
    this.erroExclusao = '';
  }

  confirmarExclusao(pergunta: VinteQuestaoResponse): void {
    if (this.perguntas.length <= 1) {
      this.erroExclusao = 'É preciso manter ao menos uma pergunta.';
      return;
    }

    this.excluindo = true;
    this.erroExclusao = '';

    this.vintePerguntas.removerPergunta(pergunta.id).subscribe((envelope) => {
      this.excluindo = false;
      this.confirmandoExclusao = null;

      if (envelope.success) {
        this.perguntas = this.perguntas.filter((p) => p.id !== pergunta.id);
      } else {
        this.erroExclusao = envelope.error?.message ?? 'Não foi possível excluir a pergunta.';
      }
    });
  }

  moverParaCima(pergunta: VinteQuestaoResponse): void {
    this.mover(pergunta, 'cima', () => this.vintePerguntas.moverPerguntaCima(pergunta.id));
  }

  moverParaBaixo(pergunta: VinteQuestaoResponse): void {
    this.mover(pergunta, 'baixo', () => this.vintePerguntas.moverPerguntaBaixo(pergunta.id));
  }

  private mover(
    pergunta: VinteQuestaoResponse,
    direcao: 'cima' | 'baixo',
    acao: () => ReturnType<VintePerguntasService['moverPerguntaCima']>,
  ): void {
    this.movendoId = pergunta.id;
    this.erroMover = '';

    acao().subscribe((envelope) => {
      this.movendoId = null;

      if (envelope.success) {
        const indiceAtual = this.perguntas.findIndex((p) => p.id === pergunta.id);
        const indiceVizinho = direcao === 'cima' ? indiceAtual - 1 : indiceAtual + 1;
        if (indiceAtual !== -1 && indiceVizinho >= 0 && indiceVizinho < this.perguntas.length) {
          const perguntas = [...this.perguntas];
          const ordemAtual = perguntas[indiceAtual].ordem;
          perguntas[indiceAtual] = { ...perguntas[indiceAtual], ordem: perguntas[indiceVizinho].ordem };
          perguntas[indiceVizinho] = { ...perguntas[indiceVizinho], ordem: ordemAtual };
          perguntas.sort((a, b) => a.ordem - b.ordem);
          this.perguntas = perguntas;
        }
      } else {
        this.erroMover = envelope.error?.message ?? 'Não foi possível reordenar a pergunta.';
      }
    });
  }
}
