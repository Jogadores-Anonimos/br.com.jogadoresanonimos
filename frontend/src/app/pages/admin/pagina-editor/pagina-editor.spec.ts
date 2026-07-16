import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { of, throwError } from 'rxjs';

import { PaginaEditor } from './pagina-editor';
import { Cms } from '../../../services/cms/cms';
import { CmsPaginaAdminDetalhe } from '../../../services/cms/cms.model';
import { Imagem } from '../../../services/imagem/imagem';
import { VintePerguntasService } from '../../../services/vinte-perguntas/vinte-perguntas';
import { VinteQuestaoResponse } from '../../../services/vinte-perguntas/vinte-perguntas.model';
import { apiError, apiSuccess } from '../../../services/api-envelope';

function paginaFake(overrides: Partial<CmsPaginaAdminDetalhe> = {}): CmsPaginaAdminDetalhe {
  return {
    slug: 'pagina-fake',
    titulo: 'Página Fake',
    blocos: [{ ordem: 0, tipo: 'texto', conteudo: 'Conteúdo fake' }],
    atualizadoEm: '2026-01-01T00:00:00.000Z',
    atualizadoPor: 'admin-fake',
    ...overrides,
  };
}

function questaoFake(overrides: Partial<VinteQuestaoResponse> = {}): VinteQuestaoResponse {
  return {
    id: 'questao-1',
    numero: 1,
    ordem: 0,
    texto: 'Pergunta fake?',
    ...overrides,
  };
}

describe('PaginaEditor', () => {
  let component: PaginaEditor;
  let fixture: ComponentFixture<PaginaEditor>;
  let cmsSpy: Record<string, ReturnType<typeof vi.fn>>;
  let imagemSpy: Record<string, ReturnType<typeof vi.fn>>;
  let vintePerguntasSpy: Record<string, ReturnType<typeof vi.fn>>;
  let routerSpy: Record<string, ReturnType<typeof vi.fn>>;

  function criarActivatedRoute(slug: string) {
    const paramMap = convertToParamMap({ slug });
    return {
      snapshot: { paramMap },
      paramMap: of(paramMap),
    };
  }

  async function criarComponente(
    slug: string,
    overrides: { getPaginaAdmin?: ReturnType<typeof vi.fn>; listarAdmin?: ReturnType<typeof vi.fn> } = {},
  ): Promise<void> {
    cmsSpy = {
      getPaginaAdmin: overrides.getPaginaAdmin ?? vi.fn().mockReturnValue(of(apiSuccess(paginaFake()))),
      atualizarPagina: vi.fn().mockReturnValue(of(apiSuccess(paginaFake()))),
    };
    imagemSpy = {
      uploadImagem: vi.fn().mockReturnValue(
        of(apiSuccess({ id: 'imagem-1', nomeOriginal: 'foto.png', tamanhoBytes: 100, alt: 'Foto', url: '/img/foto.png', criadoEm: '2026-01-01T00:00:00.000Z' })),
      ),
    };
    vintePerguntasSpy = {
      listarAdmin: overrides.listarAdmin ?? vi.fn().mockReturnValue(of(apiSuccess([questaoFake()]))),
      criarPergunta: vi.fn().mockReturnValue(of(apiSuccess(questaoFake({ id: 'questao-2', numero: 2, ordem: 1 })))),
      atualizarPergunta: vi.fn().mockReturnValue(of(apiSuccess(questaoFake({ texto: 'Texto editado' })))),
      removerPergunta: vi.fn().mockReturnValue(of(apiSuccess({ mensagem: 'ok' }))),
    };
    routerSpy = { navigate: vi.fn() };
    TestBed.resetTestingModule();

    await TestBed.configureTestingModule({
      imports: [PaginaEditor],
      providers: [
        { provide: Cms, useValue: cmsSpy },
        { provide: Imagem, useValue: imagemSpy },
        { provide: VintePerguntasService, useValue: vintePerguntasSpy },
        { provide: Router, useValue: routerSpy },
        { provide: ActivatedRoute, useValue: criarActivatedRoute(slug) },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(PaginaEditor);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  describe('carregamento de página comum', () => {
    beforeEach(async () => {
      await criarComponente('sobre-nos');
    });

    it('deve criar', () => {
      expect(component).toBeTruthy();
    });

    it('carrega título e blocos da página a partir do service', () => {
      expect(cmsSpy['getPaginaAdmin']).toHaveBeenCalledWith('sobre-nos');
      expect(component.titulo()).toBe('Página Fake');
      expect(component.blocos()).toHaveLength(1);
      expect(component.carregando()).toBe(false);
      expect(component.temAlteracoesPendentes()).toBe(false);
    });

    it('mostra erro quando a página não pode ser carregada', async () => {
      await criarComponente('inexistente', {
        getPaginaAdmin: vi.fn().mockReturnValue(of(apiError('NOT_FOUND', 'Página não encontrada.'))),
      });

      expect(component.erro()).toBe('Página não encontrada.');
      expect(component.carregando()).toBe(false);
    });
  });

  describe('edição de blocos', () => {
    beforeEach(async () => {
      await criarComponente('sobre-nos');
    });

    it('atualiza o conteúdo de um bloco de texto e marca alteração pendente', () => {
      component.atualizarTexto(0, 'Novo conteúdo');

      expect(component.blocos()[0]).toEqual({ ordem: 0, tipo: 'texto', conteudo: 'Novo conteúdo' });
      expect(component.temAlteracoesPendentes()).toBe(true);
    });

    it('não altera bloco de texto quando o índice aponta para um bloco de tipo diferente', () => {
      component.blocos.set([{ ordem: 0, tipo: 'imagem', imagemId: 'img-1', alt: 'Foto' }]);

      component.atualizarTexto(0, 'Não deveria entrar aqui');

      expect(component.blocos()[0]).toEqual({ ordem: 0, tipo: 'imagem', imagemId: 'img-1', alt: 'Foto' });
    });

    it('atualiza o alt de um bloco de imagem', () => {
      component.blocos.set([{ ordem: 0, tipo: 'imagem', imagemId: 'img-1', alt: 'Alt antigo' }]);

      component.atualizarAltImagem(0, 'Alt novo');

      expect((component.blocos()[0] as { alt: string }).alt).toBe('Alt novo');
      expect(component.temAlteracoesPendentes()).toBe(true);
    });
  });

  describe('upload de imagem em bloco', () => {
    beforeEach(async () => {
      await criarComponente('sobre-nos');
      component.blocos.set([{ ordem: 0, tipo: 'imagem', imagemId: '', alt: 'Foto do bloco' }]);
    });

    function eventoComArquivo(arquivo: File | null): Event {
      const input = document.createElement('input');
      input.type = 'file';
      if (arquivo) {
        const arquivos = [arquivo] as unknown as FileList;
        Object.defineProperty(arquivos, 'item', { value: (i: number) => [arquivo][i] });
        Object.defineProperty(input, 'files', { value: arquivos, configurable: true });
      }
      return { target: input } as unknown as Event;
    }

    it('envia a imagem selecionada e atualiza o bloco com o id e url retornados', () => {
      const arquivo = new File(['conteudo'], 'foto.png', { type: 'image/png' });

      component.onArquivoBlocoSelecionado(0, eventoComArquivo(arquivo));

      expect(imagemSpy['uploadImagem']).toHaveBeenCalledWith({ arquivo, alt: 'Foto do bloco' });
      const bloco = component.blocos()[0] as { imagemId: string; url?: string };
      expect(bloco.imagemId).toBe('imagem-1');
      expect(bloco.url).toBe('/img/foto.png');
      expect(component.temAlteracoesPendentes()).toBe(true);
    });

    it('não faz nada quando nenhum arquivo é selecionado', () => {
      component.onArquivoBlocoSelecionado(0, eventoComArquivo(null));

      expect(imagemSpy['uploadImagem']).not.toHaveBeenCalled();
    });

    it('mostra erro quando o upload falha', () => {
      imagemSpy['uploadImagem'].mockReturnValue(of(apiError('VALIDATION_ERROR', 'Arquivo inválido.')));
      const arquivo = new File(['conteudo'], 'foto.png', { type: 'image/png' });

      component.onArquivoBlocoSelecionado(0, eventoComArquivo(arquivo));

      expect(component.erroUploadImagem()).toBe('Arquivo inválido.');
    });
  });

  describe('salvar página', () => {
    beforeEach(async () => {
      await criarComponente('sobre-nos');
    });

    it('salva com sucesso, atualiza blocos e mostra mensagem de sucesso', () => {
      component.atualizarTexto(0, 'Alterado');
      const blocosEnviados = component.blocos();

      component.salvar();

      expect(cmsSpy['atualizarPagina']).toHaveBeenCalledWith('sobre-nos', { blocos: blocosEnviados });
      expect(component.temAlteracoesPendentes()).toBe(false);
      expect(component.mensagemSucesso()).toBe('Alterações salvas com sucesso.');
    });

    it('não dispara uma segunda chamada quando já está salvando', () => {
      component.salvando.set(true);

      component.salvar();

      expect(cmsSpy['atualizarPagina']).not.toHaveBeenCalled();
    });

    it('mostra erro quando o salvamento falha', () => {
      cmsSpy['atualizarPagina'].mockReturnValue(of(apiError('UPSTREAM_ERROR', 'Não foi possível salvar as alterações.')));

      component.salvar();

      expect(component.erro()).toBe('Não foi possível salvar as alterações.');
    });
  });

  describe('sair do editor', () => {
    beforeEach(async () => {
      await criarComponente('sobre-nos');
    });

    it('navega direto quando não há alterações pendentes', () => {
      component.sair();

      expect(routerSpy['navigate']).toHaveBeenCalledWith(['/admin/paginas']);
    });

    it('pede confirmação e navega quando confirmado, havendo alterações pendentes', () => {
      component.atualizarTexto(0, 'Alterado');
      vi.spyOn(window, 'confirm').mockReturnValue(true);

      component.sair();

      expect(routerSpy['navigate']).toHaveBeenCalledWith(['/admin/paginas']);
    });

    it('não navega quando o usuário cancela a confirmação', () => {
      component.atualizarTexto(0, 'Alterado');
      vi.spyOn(window, 'confirm').mockReturnValue(false);

      component.sair();

      expect(routerSpy['navigate']).not.toHaveBeenCalled();
    });
  });

  describe('página de vinte perguntas', () => {
    beforeEach(async () => {
      await criarComponente('vinte-perguntas');
    });

    it('detecta o slug especial e carrega perguntas em vez de blocos de CMS', () => {
      expect(component.ehVintePerguntas).toBe(true);
      expect(cmsSpy['getPaginaAdmin']).not.toHaveBeenCalled();
      expect(vintePerguntasSpy['listarAdmin']).toHaveBeenCalled();
      expect(component.perguntas()).toHaveLength(1);
      expect(component.carregando()).toBe(false);
    });

    it('marca perguntasIndisponivel quando o service falha ao listar', async () => {
      await criarComponente('vinte-perguntas', {
        listarAdmin: vi.fn().mockReturnValue(of(apiError('UPSTREAM_ERROR', 'Falha ao listar'))),
      });

      expect(component.perguntasIndisponivel()).toBe(true);
    });

    it('salva o texto de uma pergunta editada', () => {
      component.salvarPergunta('questao-1', 'Texto editado');

      expect(vintePerguntasSpy['atualizarPergunta']).toHaveBeenCalledWith('questao-1', { texto: 'Texto editado' });
      expect(component.perguntas()[0].texto).toBe('Texto editado');
    });

    it('mostra erro quando falha ao salvar pergunta', () => {
      vintePerguntasSpy['atualizarPergunta'].mockReturnValue(of(apiError('VALIDATION_ERROR', 'Texto obrigatório.')));

      component.salvarPergunta('questao-1', '');

      expect(component.perguntasErro()).toBe('Texto obrigatório.');
    });

    it('adiciona uma nova pergunta com sucesso', () => {
      component.adicionarPergunta();

      expect(vintePerguntasSpy['criarPergunta']).toHaveBeenCalled();
      expect(component.perguntas()).toHaveLength(2);
    });

    it('não permite adicionar pergunta enquanto outra adição está em andamento', () => {
      component.perguntasAdicionando.set(true);

      component.adicionarPergunta();

      expect(vintePerguntasSpy['criarPergunta']).not.toHaveBeenCalled();
    });

    it('não remove a última pergunta restante, mostrando erro', () => {
      component.removerPergunta('questao-1');

      expect(vintePerguntasSpy['removerPergunta']).not.toHaveBeenCalled();
      expect(component.perguntasErro()).toBe('É preciso manter ao menos uma pergunta.');
    });

    it('remove uma pergunta quando há mais de uma e o usuário confirma', () => {
      component.perguntas.set([questaoFake({ id: 'questao-1' }), questaoFake({ id: 'questao-2', numero: 2, ordem: 1 })]);
      vi.spyOn(window, 'confirm').mockReturnValue(true);

      component.removerPergunta('questao-1');

      expect(vintePerguntasSpy['removerPergunta']).toHaveBeenCalledWith('questao-1');
      expect(component.perguntas().find((p) => p.id === 'questao-1')).toBeUndefined();
    });

    it('não remove quando o usuário cancela a confirmação', () => {
      component.perguntas.set([questaoFake({ id: 'questao-1' }), questaoFake({ id: 'questao-2', numero: 2, ordem: 1 })]);
      vi.spyOn(window, 'confirm').mockReturnValue(false);

      component.removerPergunta('questao-1');

      expect(vintePerguntasSpy['removerPergunta']).not.toHaveBeenCalled();
    });
  });

  describe('aviso de saída com alterações não salvas', () => {
    beforeEach(async () => {
      await criarComponente('sobre-nos');
    });

    it('previne o fechamento da aba quando há alterações pendentes', () => {
      component.atualizarTexto(0, 'Alterado');
      const evento = { preventDefault: vi.fn(), returnValue: '' } as unknown as BeforeUnloadEvent;

      component.avisarSaidaComAlteracoes(evento);

      expect(evento.preventDefault).toHaveBeenCalled();
    });

    it('não interfere no fechamento quando não há alterações pendentes', () => {
      const evento = { preventDefault: vi.fn(), returnValue: '' } as unknown as BeforeUnloadEvent;

      component.avisarSaidaComAlteracoes(evento);

      expect(evento.preventDefault).not.toHaveBeenCalled();
    });
  });
});
