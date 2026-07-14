import { Component, DestroyRef, HostListener, OnInit, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { Cms } from '../../../services/cms';
import { CmsBloco, CmsBlocoImagem, CmsBlocoTexto } from '../../../services/cms.model';
import { Imagem } from '../../../services/imagem';
import { VintePerguntasService } from '../../../services/vinte-perguntas';
import { VinteQuestaoResponse } from '../../../services/vinte-perguntas.model';

const SLUG_VINTE_PERGUNTAS = 'vinte-perguntas';

@Component({
  selector: 'app-pagina-editor',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './pagina-editor.html',
  styleUrl: './pagina-editor.css',
})
export class PaginaEditor implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly cms = inject(Cms);
  private readonly imagemService = inject(Imagem);
  private readonly vintePerguntas = inject(VintePerguntasService);
  private readonly destroyRef = inject(DestroyRef);

  // `paginas/:slug` é uma rota parametrizada — navegar entre páginas
  // irmãs (ex: sobre → historia) reaproveita a mesma instância do
  // componente em vez de recriá-la, então ler o slug só uma vez via
  // `route.snapshot` (como era antes) travava a página no primeiro slug
  // carregado. Reagir a `paramMap` resolve isso.
  slug = this.route.snapshot.paramMap.get('slug') ?? '';
  ehVintePerguntas = this.slug === SLUG_VINTE_PERGUNTAS;

  readonly carregando = signal(true);
  readonly salvando = signal(false);
  readonly erro = signal<string | null>(null);
  readonly mensagemSucesso = signal<string | null>(null);

  readonly titulo = signal('');
  readonly blocos = signal<CmsBloco[]>([]);
  readonly temAlteracoesPendentes = signal(false);

  readonly enviandoImagemIndice = signal<number | null>(null);
  readonly erroUploadImagem = signal<string | null>(null);

  // Vinte Perguntas — seção fixa, edição de texto via service dedicado.
  readonly perguntas = signal<VinteQuestaoResponse[]>([]);
  readonly perguntasCarregando = signal(false);
  readonly perguntasIndisponivel = signal(false);
  readonly perguntasSalvandoId = signal<string | null>(null);
  readonly perguntasErro = signal<string | null>(null);
  readonly perguntasAdicionando = signal(false);
  readonly perguntasRemovendoId = signal<string | null>(null);

  readonly totalBlocos = computed(() => this.blocos().length);

  ngOnInit(): void {
    this.route.paramMap.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((params) => {
      this.slug = params.get('slug') ?? '';
      this.ehVintePerguntas = this.slug === SLUG_VINTE_PERGUNTAS;
      this.temAlteracoesPendentes.set(false);
      this.erro.set(null);
      this.mensagemSucesso.set(null);
      if (this.ehVintePerguntas) {
        this.carregando.set(false);
        this.carregarPerguntas();
      } else {
        this.carregarPagina();
      }
    });
  }

  @HostListener('window:beforeunload', ['$event'])
  avisarSaidaComAlteracoes(evento: BeforeUnloadEvent): void {
    if (this.temAlteracoesPendentes()) {
      evento.preventDefault();
      evento.returnValue = '';
    }
  }

  private carregarPagina(): void {
    this.carregando.set(true);
    this.erro.set(null);
    this.cms.getPaginaAdmin(this.slug).subscribe((envelope) => {
      this.carregando.set(false);
      if (envelope.success && envelope.data) {
        this.titulo.set(envelope.data.titulo);
        this.blocos.set(envelope.data.blocos.map((b) => ({ ...b })));
        this.temAlteracoesPendentes.set(false);
      } else {
        this.erro.set(envelope.error?.message ?? 'Não foi possível carregar esta página.');
      }
    });
  }

  private carregarPerguntas(): void {
    this.perguntasCarregando.set(true);
    this.perguntasIndisponivel.set(false);
    this.vintePerguntas.listarAdmin().subscribe((envelope) => {
      this.perguntasCarregando.set(false);
      if (envelope.success && envelope.data) {
        this.perguntas.set([...envelope.data].sort((a, b) => a.ordem - b.ordem));
      } else {
        this.perguntasIndisponivel.set(true);
      }
    });
  }

  salvarPergunta(id: string, texto: string): void {
    this.perguntasSalvandoId.set(id);
    this.perguntasErro.set(null);

    this.vintePerguntas.atualizarPergunta(id, { texto }).subscribe((envelope) => {
      this.perguntasSalvandoId.set(null);

      if (envelope.success && envelope.data) {
        const atualizada = envelope.data;
        this.perguntas.set(this.perguntas().map((p) => (p.id === id ? atualizada : p)));
      } else {
        this.perguntasErro.set(envelope.error?.message ?? 'Não foi possível salvar a pergunta.');
      }
    });
  }

  adicionarPergunta(): void {
    if (this.perguntasAdicionando()) return;
    this.perguntasAdicionando.set(true);
    this.perguntasErro.set(null);

    this.vintePerguntas.criarPergunta('Nova pergunta — edite o texto abaixo').subscribe((envelope) => {
      this.perguntasAdicionando.set(false);

      if (envelope.success && envelope.data) {
        this.perguntas.set([...this.perguntas(), envelope.data]);
      } else {
        this.perguntasErro.set(envelope.error?.message ?? 'Não foi possível adicionar a pergunta.');
      }
    });
  }

  removerPergunta(id: string): void {
    if (this.perguntas().length <= 1) {
      this.perguntasErro.set('É preciso manter ao menos uma pergunta.');
      return;
    }
    if (this.perguntasRemovendoId() !== null) return;

    if (!window.confirm('Remover esta pergunta? Essa ação não pode ser desfeita.')) {
      return;
    }

    this.perguntasRemovendoId.set(id);
    this.perguntasErro.set(null);

    this.vintePerguntas.removerPergunta(id).subscribe((envelope) => {
      this.perguntasRemovendoId.set(null);

      if (envelope.success) {
        this.perguntas.set(this.perguntas().filter((p) => p.id !== id));
      } else {
        this.perguntasErro.set(envelope.error?.message ?? 'Não foi possível remover a pergunta.');
      }
    });
  }

  marcarAlteracao(): void {
    this.temAlteracoesPendentes.set(true);
    this.mensagemSucesso.set(null);
  }

  atualizarTexto(indice: number, valor: string): void {
    const atual = this.blocos();
    const bloco = atual[indice] as CmsBlocoTexto;
    if (bloco.tipo !== 'texto') return;
    const novo = [...atual];
    novo[indice] = { ...bloco, conteudo: valor };
    this.blocos.set(novo);
    this.marcarAlteracao();
  }

  atualizarAltImagem(indice: number, valor: string): void {
    const atual = this.blocos();
    const bloco = atual[indice] as CmsBlocoImagem;
    if (bloco.tipo !== 'imagem') return;
    const novo = [...atual];
    novo[indice] = { ...bloco, alt: valor };
    this.blocos.set(novo);
    this.marcarAlteracao();
  }

  onArquivoBlocoSelecionado(indice: number, event: Event): void {
    const input = event.target as HTMLInputElement;
    const arquivo = input.files && input.files.length > 0 ? input.files[0] : null;
    if (!arquivo) return;

    const atual = this.blocos();
    const bloco = atual[indice] as CmsBlocoImagem;
    if (bloco.tipo !== 'imagem') return;

    this.enviandoImagemIndice.set(indice);
    this.erroUploadImagem.set(null);

    this.imagemService.uploadImagem({ arquivo, alt: bloco.alt || arquivo.name }).subscribe((envelope) => {
      this.enviandoImagemIndice.set(null);

      if (envelope.success && envelope.data) {
        const imagem = envelope.data;
        const novo = [...this.blocos()];
        novo[indice] = { ...(novo[indice] as CmsBlocoImagem), imagemId: imagem.id, url: imagem.url };
        this.blocos.set(novo);
        this.marcarAlteracao();
      } else {
        this.erroUploadImagem.set(envelope.error?.message ?? 'Não foi possível enviar a imagem.');
      }

      input.value = '';
    });
  }

  salvar(): void {
    if (this.salvando()) return;
    this.salvando.set(true);
    this.erro.set(null);
    this.cms.atualizarPagina(this.slug, { titulo: this.titulo(), blocos: this.blocos() }).subscribe((envelope) => {
      this.salvando.set(false);
      if (envelope.success && envelope.data) {
        this.blocos.set(envelope.data.blocos.map((b) => ({ ...b })));
        this.temAlteracoesPendentes.set(false);
        this.mensagemSucesso.set('Alterações salvas com sucesso.');
      } else {
        this.erro.set(envelope.error?.message ?? 'Não foi possível salvar as alterações.');
      }
    });
  }

  sair(): void {
    if (this.temAlteracoesPendentes()) {
      if (!window.confirm('Você tem alterações não salvas. Deseja sair mesmo assim?')) {
        return;
      }
    }
    this.router.navigate(['/admin/paginas']);
  }
}
