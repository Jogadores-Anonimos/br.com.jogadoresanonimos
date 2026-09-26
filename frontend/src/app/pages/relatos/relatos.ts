import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { Auth } from '../../services/auth/auth';
import { RelatoPublicoResumo, RelatosService } from '../../services/relatos/relatos';

const TEXTO_MIN_LENGTH = 50;
const TEXTO_MAX_LENGTH = 10000;
const NOME_EXIBICAO_MAX_LENGTH = 30;
const TAMANHO_PAGINA = 6;

@Component({
  selector: 'app-relatos',
  standalone: true,
  imports: [FormsModule, RouterLink, ResourceLayout],
  templateUrl: './relatos.html',
  styleUrl: './relatos.css',
})
export class Relatos implements OnInit {
  private readonly relatosService = inject(RelatosService);
  readonly auth = inject(Auth);

  readonly bannerSrc = '/images/home/reuniao-discussao.webp';
  readonly textoMinLength = TEXTO_MIN_LENGTH;
  readonly textoMaxLength = TEXTO_MAX_LENGTH;
  readonly nomeExibicaoMaxLength = NOME_EXIBICAO_MAX_LENGTH;

  carregando = true;
  erroCarregamento = '';
  relatos: RelatoPublicoResumo[] = [];

  pagina = 0;
  totalPaginas = 0;
  readonly tamanhoPagina = TAMANHO_PAGINA;

  private readonly textosCompletos = new Map<string, string>();
  private readonly carregandoTextoCompleto = new Set<string>();
  private readonly erroTextoCompleto = new Set<string>();

  novoTexto = '';
  novoNomeExibicao = '';
  enviando = false;
  erroEnvio = '';
  enviado = false;

  ngOnInit(): void {
    this.carregarRelatos();
  }

  irParaPagina(pagina: number): void {
    if (pagina < 0 || pagina >= this.totalPaginas || pagina === this.pagina) {
      return;
    }
    this.pagina = pagina;
    this.carregarRelatos();
  }

  private carregarRelatos(): void {
    this.carregando = true;
    this.erroCarregamento = '';

    this.relatosService.listarAprovados(this.pagina, this.tamanhoPagina).subscribe((envelope) => {
      this.carregando = false;

      if (envelope.success && envelope.data) {
        this.relatos = envelope.data.conteudo;
        this.totalPaginas = envelope.data.totalPaginas;
        this.textosCompletos.clear();
        this.carregandoTextoCompleto.clear();
        this.erroTextoCompleto.clear();
      } else {
        this.erroCarregamento =
          envelope.error?.message ?? 'Não foi possível carregar os relatos no momento.';
      }
    });
  }

  nomeParaExibir(relato: RelatoPublicoResumo): string {
    const nome = relato.nomeExibicao?.trim();
    return nome ? nome : 'Anônimo';
  }

  textoExibido(relato: RelatoPublicoResumo): string {
    return this.textosCompletos.get(relato.id) ?? relato.textoResumo;
  }

  mostrarBotaoLerMais(relato: RelatoPublicoResumo): boolean {
    return !this.textosCompletos.has(relato.id);
  }

  estaCarregandoTextoCompleto(relato: RelatoPublicoResumo): boolean {
    return this.carregandoTextoCompleto.has(relato.id);
  }

  teveErroTextoCompleto(relato: RelatoPublicoResumo): boolean {
    return this.erroTextoCompleto.has(relato.id);
  }

  lerCompleto(relato: RelatoPublicoResumo): void {
    if (this.carregandoTextoCompleto.has(relato.id)) {
      return;
    }

    this.erroTextoCompleto.delete(relato.id);
    this.carregandoTextoCompleto.add(relato.id);

    this.relatosService.buscarPorId(relato.id).subscribe((envelope) => {
      this.carregandoTextoCompleto.delete(relato.id);

      if (envelope.success && envelope.data) {
        this.textosCompletos.set(relato.id, envelope.data.texto);
      } else {
        this.erroTextoCompleto.add(relato.id);
      }
    });
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
