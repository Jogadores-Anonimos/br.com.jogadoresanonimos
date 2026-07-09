import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { Depoimento, DepoimentosService } from '../../services/depoimentos';

const TEXTO_TRUNCADO_LIMITE = 320;

// Espelha os limites validados no DepoimentosService (mock hoje, backend
// real depois) apenas para dar feedback imediato no contador de
// caracteres da UI — a validação de fato acontece no service/servidor.
const TEXTO_MIN_LENGTH = 50;
const TEXTO_MAX_LENGTH = 4000;
const NOME_EXIBICAO_MAX_LENGTH = 30;

@Component({
  selector: 'app-depoimentos',
  standalone: true,
  imports: [FormsModule, ResourceLayout],
  templateUrl: './depoimentos.html',
  styleUrl: './depoimentos.css',
})
export class Depoimentos implements OnInit {
  private readonly depoimentosService = inject(DepoimentosService);

  readonly bannerSrc = '/images/home/reuniao-discussao.jpg';
  readonly textoTruncadoLimite = TEXTO_TRUNCADO_LIMITE;
  readonly textoMinLength = TEXTO_MIN_LENGTH;
  readonly textoMaxLength = TEXTO_MAX_LENGTH;
  readonly nomeExibicaoMaxLength = NOME_EXIBICAO_MAX_LENGTH;

  carregando = true;
  erroCarregamento = '';
  depoimentos: Depoimento[] = [];

  // Controla, por id de depoimento, se o card está expandido ("ler mais").
  private readonly expandidos = new Set<string>();

  // Formulário de envio
  novoTexto = '';
  novoNomeExibicao = '';
  enviando = false;
  erroEnvio = '';
  enviado = false;

  ngOnInit(): void {
    this.carregarDepoimentos();
  }

  private carregarDepoimentos(): void {
    this.carregando = true;
    this.erroCarregamento = '';

    this.depoimentosService.listarDepoimentosAprovados().subscribe((resposta) => {
      this.carregando = false;

      if (resposta.success) {
        this.depoimentos = resposta.data ?? [];
      } else {
        this.erroCarregamento =
          resposta.error ?? 'Não foi possível carregar os depoimentos no momento.';
      }
    });
  }

  nomeParaExibir(depoimento: Depoimento): string {
    const nome = depoimento.nomeExibicao?.trim();
    return nome ? nome : 'Anônimo';
  }

  precisaTruncar(depoimento: Depoimento): boolean {
    return depoimento.texto.length > this.textoTruncadoLimite;
  }

  estaExpandido(depoimento: Depoimento): boolean {
    return this.expandidos.has(depoimento.id);
  }

  textoExibido(depoimento: Depoimento): string {
    if (!this.precisaTruncar(depoimento) || this.estaExpandido(depoimento)) {
      return depoimento.texto;
    }
    return depoimento.texto.slice(0, this.textoTruncadoLimite).trimEnd() + '…';
  }

  alternarExpandido(depoimento: Depoimento): void {
    if (this.expandidos.has(depoimento.id)) {
      this.expandidos.delete(depoimento.id);
    } else {
      this.expandidos.add(depoimento.id);
    }
  }

  onEnviarDepoimento(): void {
    this.erroEnvio = '';
    this.enviando = true;

    this.depoimentosService
      .enviarDepoimento({
        texto: this.novoTexto,
        nomeExibicao: this.novoNomeExibicao || undefined,
      })
      .subscribe((resposta) => {
        this.enviando = false;

        if (resposta.success) {
          this.enviado = true;
          this.novoTexto = '';
          this.novoNomeExibicao = '';
        } else {
          this.erroEnvio =
            resposta.error ?? 'Não foi possível enviar seu depoimento agora. Tente novamente.';
        }
      });
  }

  compartilharNovamente(): void {
    this.enviado = false;
  }
}
