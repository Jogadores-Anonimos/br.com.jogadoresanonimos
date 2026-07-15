import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { mapearErrosCampo } from '../../../services/api-envelope';
import { FaqService } from '../../../services/faq/faq';
import { CriarFaqRequest, Faq } from '../../../services/faq/faq.model';

interface FormularioFaq {
  pergunta: string;
  resposta: string;
}

function formularioVazio(): FormularioFaq {
  return { pergunta: '', resposta: '' };
}

@Component({
  selector: 'app-admin-faq',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-faq.html',
  styleUrl: './admin-faq.css',
})
export class AdminFaq implements OnInit {
  private readonly faqService = inject(FaqService);

  carregando = true;
  erroCarregamento = '';
  perguntas: Faq[] = [];

  modo: 'lista' | 'formulario' = 'lista';
  faqEmEdicao: Faq | null = null;
  formulario: FormularioFaq = formularioVazio();
  salvando = false;
  erroFormulario = '';
  errosCampo: Record<string, string> = {};

  confirmandoExclusao: string | null = null;
  excluindo = false;
  erroExclusao = '';

  movendoId: string | null = null;
  erroMover = '';

  ngOnInit(): void {
    this.carregarFaq();
  }

  private carregarFaq(): void {
    this.carregando = true;
    this.erroCarregamento = '';

    this.faqService.listarFaqAdmin().subscribe((envelope) => {
      this.carregando = false;

      if (envelope.success && envelope.data) {
        this.perguntas = envelope.data;
      } else {
        this.perguntas = [];
        this.erroCarregamento = envelope.error?.message ?? 'Não foi possível carregar as perguntas.';
      }
    });
  }

  abrirNovo(): void {
    this.faqEmEdicao = null;
    this.formulario = formularioVazio();
    this.erroFormulario = '';
    this.errosCampo = {};
    this.modo = 'formulario';
  }

  abrirEdicao(faq: Faq): void {
    this.faqEmEdicao = faq;
    this.formulario = {
      pergunta: faq.pergunta,
      resposta: faq.resposta,
    };
    this.erroFormulario = '';
    this.errosCampo = {};
    this.modo = 'formulario';
  }

  cancelar(): void {
    this.modo = 'lista';
    this.faqEmEdicao = null;
    this.erroFormulario = '';
    this.errosCampo = {};
  }

  salvar(): void {
    this.errosCampo = {};

    if (!this.formulario.pergunta.trim()) {
      this.errosCampo['pergunta'] = 'Pergunta é obrigatória.';
    }
    if (!this.formulario.resposta.trim()) {
      this.errosCampo['resposta'] = 'Resposta é obrigatória.';
    }
    if (Object.keys(this.errosCampo).length > 0) {
      this.erroFormulario = 'Pergunta e resposta são obrigatórias.';
      return;
    }

    this.salvando = true;
    this.erroFormulario = '';

    const request: CriarFaqRequest = {
      pergunta: this.formulario.pergunta.trim(),
      resposta: this.formulario.resposta.trim(),
    };

    const acao = this.faqEmEdicao
      ? this.faqService.editarFaqAdmin(this.faqEmEdicao.id, request)
      : this.faqService.criarFaqAdmin(request);

    acao.subscribe((envelope) => {
      this.salvando = false;

      if (envelope.success && envelope.data) {
        const salvo = envelope.data;
        this.perguntas = this.faqEmEdicao
          ? this.perguntas.map((f) => (f.id === salvo.id ? salvo : f))
          : [...this.perguntas, salvo];
        this.modo = 'lista';
        this.faqEmEdicao = null;
      } else {
        this.erroFormulario = envelope.error?.message ?? 'Não foi possível salvar a pergunta.';
        this.errosCampo = mapearErrosCampo(envelope.error?.fields);
      }
    });
  }

  pedirConfirmacaoExclusao(faq: Faq): void {
    this.confirmandoExclusao = faq.id;
    this.erroExclusao = '';
  }

  cancelarExclusao(): void {
    this.confirmandoExclusao = null;
    this.erroExclusao = '';
  }

  confirmarExclusao(faq: Faq): void {
    this.excluindo = true;
    this.erroExclusao = '';

    this.faqService.excluirFaqAdmin(faq.id).subscribe((envelope) => {
      this.excluindo = false;
      this.confirmandoExclusao = null;

      if (envelope.success) {
        this.perguntas = this.perguntas.filter((f) => f.id !== faq.id);
      } else {
        this.erroExclusao = envelope.error?.message ?? 'Não foi possível excluir a pergunta.';
      }
    });
  }

  moverParaCima(faq: Faq): void {
    this.mover(faq, 'cima', () => this.faqService.moverFaqParaCima(faq.id));
  }

  moverParaBaixo(faq: Faq): void {
    this.mover(faq, 'baixo', () => this.faqService.moverFaqParaBaixo(faq.id));
  }

  private mover(faq: Faq, direcao: 'cima' | 'baixo', acao: () => ReturnType<FaqService['moverFaqParaCima']>): void {
    this.movendoId = faq.id;
    this.erroMover = '';

    acao().subscribe((envelope) => {
      this.movendoId = null;

      if (envelope.success) {
        const indiceAtual = this.perguntas.findIndex((f) => f.id === faq.id);
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
