import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { AdminVintePerguntas } from './admin-vinte-perguntas';
import { VintePerguntasService } from '../../../services/vinte-perguntas/vinte-perguntas';
import { VinteQuestaoResponse } from '../../../services/vinte-perguntas/vinte-perguntas.model';
import { apiError, apiSuccess } from '../../../services/api-envelope';

function perguntaFake(overrides: Partial<VinteQuestaoResponse> = {}): VinteQuestaoResponse {
  return { id: 'pergunta-1', numero: 1, ordem: 0, texto: 'Pergunta fake?', ...overrides };
}

describe('AdminVintePerguntas', () => {
  let component: AdminVintePerguntas;
  let fixture: ComponentFixture<AdminVintePerguntas>;
  let servicoSpy: {
    listarAdmin: ReturnType<typeof vi.fn>;
    criarPergunta: ReturnType<typeof vi.fn>;
    atualizarPergunta: ReturnType<typeof vi.fn>;
    removerPergunta: ReturnType<typeof vi.fn>;
    moverPerguntaCima: ReturnType<typeof vi.fn>;
    moverPerguntaBaixo: ReturnType<typeof vi.fn>;
  };

  const perguntas: VinteQuestaoResponse[] = [
    perguntaFake({ id: 'pergunta-1', numero: 1, ordem: 0, texto: 'Primeira?' }),
    perguntaFake({ id: 'pergunta-2', numero: 2, ordem: 1, texto: 'Segunda?' }),
    perguntaFake({ id: 'pergunta-3', numero: 3, ordem: 2, texto: 'Terceira?' }),
  ];

  beforeEach(async () => {
    servicoSpy = {
      listarAdmin: vi.fn().mockReturnValue(of(apiSuccess(perguntas))),
      criarPergunta: vi.fn().mockReturnValue(of(apiSuccess(perguntaFake()))),
      atualizarPergunta: vi.fn().mockReturnValue(of(apiSuccess(perguntaFake()))),
      removerPergunta: vi.fn().mockReturnValue(of(apiSuccess({ mensagem: 'ok' }))),
      moverPerguntaCima: vi.fn().mockReturnValue(of(apiSuccess(perguntaFake()))),
      moverPerguntaBaixo: vi.fn().mockReturnValue(of(apiSuccess(perguntaFake()))),
    };

    await TestBed.configureTestingModule({
      imports: [AdminVintePerguntas],
      providers: [{ provide: VintePerguntasService, useValue: servicoSpy }],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminVintePerguntas);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve criar', () => {
    expect(component).toBeTruthy();
  });

  it('renderiza a lista de perguntas carregadas do service', () => {
    const corpos = fixture.nativeElement.querySelectorAll('.admin-vinte-perguntas__corpo');
    expect(corpos.length).toBe(3);
    expect(corpos[0].textContent).toContain('Primeira?');
  });

  it('mostra mensagem de erro quando o carregamento falha', () => {
    servicoSpy.listarAdmin.mockReturnValue(of(apiError('UPSTREAM_ERROR', 'Não foi possível carregar as perguntas.')));
    fixture = TestBed.createComponent(AdminVintePerguntas);
    component = fixture.componentInstance;
    fixture.detectChanges();

    const erro = fixture.nativeElement.querySelector('.admin-vinte-perguntas__estado--erro');
    expect(erro?.textContent).toContain('Não foi possível carregar as perguntas.');
  });

  it('mostra estado vazio quando não há perguntas cadastradas', () => {
    servicoSpy.listarAdmin.mockReturnValue(of(apiSuccess([])));
    fixture = TestBed.createComponent(AdminVintePerguntas);
    fixture.detectChanges();

    const estados = fixture.nativeElement.querySelectorAll('.admin-vinte-perguntas__estado');
    const textos = Array.from(estados as NodeListOf<HTMLElement>).map((e) => e.textContent);
    expect(textos.some((t) => t?.includes('Nenhuma pergunta cadastrada'))).toBe(true);
  });

  it('não salva e mostra erro quando texto está vazio', () => {
    component.abrirNovo();
    component.textoFormulario = '   ';

    component.salvar();

    expect(servicoSpy.criarPergunta).not.toHaveBeenCalled();
    expect(component.erroFormulario).toContain('obrigatório');
  });

  it('cria pergunta com texto tratado (trim)', () => {
    component.abrirNovo();
    component.textoFormulario = '  Nova pergunta?  ';

    component.salvar();

    expect(servicoSpy.criarPergunta).toHaveBeenCalledWith('Nova pergunta?');
  });

  it('edita pergunta existente', () => {
    component.abrirEdicao(perguntas[1]);
    component.textoFormulario = 'Segunda editada?';

    component.salvar();

    expect(servicoSpy.atualizarPergunta).toHaveBeenCalledWith('pergunta-2', { texto: 'Segunda editada?' });
  });

  it('impede exclusão quando só resta uma pergunta', () => {
    servicoSpy.listarAdmin.mockReturnValue(of(apiSuccess([perguntas[0]])));
    fixture = TestBed.createComponent(AdminVintePerguntas);
    component = fixture.componentInstance;
    fixture.detectChanges();

    component.confirmarExclusao(perguntas[0]);

    expect(servicoSpy.removerPergunta).not.toHaveBeenCalled();
    expect(component.erroExclusao).toContain('ao menos uma pergunta');
  });

  it('botão de mover para cima fica desabilitado no primeiro item da lista', () => {
    const botoesSubir = fixture.nativeElement.querySelectorAll('[aria-label*="para cima"]');
    expect(botoesSubir[0].disabled).toBe(true);
    expect(botoesSubir[1].disabled).toBe(false);
  });

  it('botão de mover para baixo fica desabilitado no último item da lista', () => {
    const botoesDescer = fixture.nativeElement.querySelectorAll('[aria-label*="para baixo"]');
    expect(botoesDescer[2].disabled).toBe(true);
  });

  it('clicar em mover para cima chama moverPerguntaCima com o id correto', () => {
    component.moverParaCima(perguntas[1]);

    expect(servicoSpy.moverPerguntaCima).toHaveBeenCalledWith('pergunta-2');
  });

  it('clicar em mover para baixo chama moverPerguntaBaixo com o id correto', () => {
    component.moverParaBaixo(perguntas[0]);

    expect(servicoSpy.moverPerguntaBaixo).toHaveBeenCalledWith('pergunta-1');
  });

  it('mostra erro quando reordenação falha, sem quebrar a lista atual', () => {
    servicoSpy.moverPerguntaCima.mockReturnValue(of(apiError('UPSTREAM_ERROR', 'Não foi possível reordenar a pergunta agora.')));

    component.moverParaCima(perguntas[1]);

    expect(component.erroMover).toBe('Não foi possível reordenar a pergunta agora.');
  });
});
