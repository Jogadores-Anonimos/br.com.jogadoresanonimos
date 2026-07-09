import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { Relato, RelatosService } from '../../services/relatos';

const TEXTO_TRUNCADO_LIMITE = 320;

// espelha os limites do RelatosService só para feedback imediato no contador; validação real é no service/servidor
const TEXTO_MIN_LENGTH = 50;
const TEXTO_MAX_LENGTH = 4000;
const NOME_EXIBICAO_MAX_LENGTH = 30;

@Component({
  selector: 'app-relatos',
  standalone: true,
  imports: [FormsModule, ResourceLayout],
  templateUrl: './relatos.html',
  styleUrl: './relatos.css',
})
export class Relatos implements OnInit {
  private readonly relatosService = inject(RelatosService);

  readonly bannerSrc = '/images/home/reuniao-discussao.jpg';
  readonly textoTruncadoLimite = TEXTO_TRUNCADO_LIMITE;
  readonly textoMinLength = TEXTO_MIN_LENGTH;
  readonly textoMaxLength = TEXTO_MAX_LENGTH;
  readonly nomeExibicaoMaxLength = NOME_EXIBICAO_MAX_LENGTH;

  carregando = true;
  erroCarregamento = '';
  relatos: Relato[] = [];

  private readonly expandidos = new Set<string>();

  novoTexto = '';
  novoNomeExibicao = '';
  enviando = false;
  erroEnvio = '';
  enviado = false;

  ngOnInit(): void {
    this.carregarRelatos();
  }

  private carregarRelatos(): void {
    this.carregando = true;
    this.erroCarregamento = '';

    this.relatosService.listarRelatosAprovados().subscribe((resposta) => {
      this.carregando = false;

      if (resposta.success) {
        this.relatos = resposta.data ?? [];
      } else {
        this.erroCarregamento =
          resposta.error ?? 'Não foi possível carregar os relatos no momento.';
      }
    });
  }

  nomeParaExibir(relato: Relato): string {
    const nome = relato.nomeExibicao?.trim();
    return nome ? nome : 'Anônimo';
  }

  precisaTruncar(relato: Relato): boolean {
    return relato.texto.length > this.textoTruncadoLimite;
  }

  estaExpandido(relato: Relato): boolean {
    return this.expandidos.has(relato.id);
  }

  textoExibido(relato: Relato): string {
    if (!this.precisaTruncar(relato) || this.estaExpandido(relato)) {
      return relato.texto;
    }
    return relato.texto.slice(0, this.textoTruncadoLimite).trimEnd() + '…';
  }

  alternarExpandido(relato: Relato): void {
    if (this.expandidos.has(relato.id)) {
      this.expandidos.delete(relato.id);
    } else {
      this.expandidos.add(relato.id);
    }
  }

  onEnviarRelato(): void {
    this.erroEnvio = '';
    this.enviando = true;

    this.relatosService
      .enviarRelato({
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
            resposta.error ?? 'Não foi possível enviar seu relato agora. Tente novamente.';
        }
      });
  }

  compartilharNovamente(): void {
    this.enviado = false;
  }
}
