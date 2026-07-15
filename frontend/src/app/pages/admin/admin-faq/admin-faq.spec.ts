import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { AdminFaq } from './admin-faq';
import { FaqService } from '../../../services/faq/faq';
import { Faq } from '../../../services/faq/faq.model';
import { apiError, apiSuccess } from '../../../services/api-envelope';

function faqFake(overrides: Partial<Faq> = {}): Faq {
  return { id: 'faq-1', pergunta: 'Pergunta fake?', resposta: 'Resposta fake.', ordem: 0, ...overrides };
}

describe('AdminFaq', () => {
  let component: AdminFaq;
  let fixture: ComponentFixture<AdminFaq>;
  let faqServiceSpy: {
    listarFaqAdmin: ReturnType<typeof vi.fn>;
    criarFaqAdmin: ReturnType<typeof vi.fn>;
    editarFaqAdmin: ReturnType<typeof vi.fn>;
    excluirFaqAdmin: ReturnType<typeof vi.fn>;
    moverFaqParaCima: ReturnType<typeof vi.fn>;
    moverFaqParaBaixo: ReturnType<typeof vi.fn>;
  };

  const perguntas: Faq[] = [
    faqFake({ id: 'faq-1', pergunta: 'Primeira?', ordem: 0 }),
    faqFake({ id: 'faq-2', pergunta: 'Segunda?', ordem: 1 }),
    faqFake({ id: 'faq-3', pergunta: 'Terceira?', ordem: 2 }),
  ];

  beforeEach(async () => {
    faqServiceSpy = {
      listarFaqAdmin: vi.fn().mockReturnValue(of(apiSuccess(perguntas))),
      criarFaqAdmin: vi.fn().mockReturnValue(of(apiSuccess(faqFake()))),
      editarFaqAdmin: vi.fn().mockReturnValue(of(apiSuccess(faqFake()))),
      excluirFaqAdmin: vi.fn().mockReturnValue(of(apiSuccess({ mensagem: 'ok' }))),
      moverFaqParaCima: vi.fn().mockReturnValue(of(apiSuccess(faqFake()))),
      moverFaqParaBaixo: vi.fn().mockReturnValue(of(apiSuccess(faqFake()))),
    };

    await TestBed.configureTestingModule({
      imports: [AdminFaq],
      providers: [{ provide: FaqService, useValue: faqServiceSpy }],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminFaq);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve criar', () => {
    expect(component).toBeTruthy();
  });

  it('renderiza a lista de perguntas carregadas do service', () => {
    const titulos = fixture.nativeElement.querySelectorAll('.admin-faq__card-titulo');
    expect(titulos.length).toBe(3);
    expect(titulos[0].textContent).toContain('Primeira?');
  });

  it('mostra mensagem de erro quando o carregamento falha', async () => {
    faqServiceSpy.listarFaqAdmin.mockReturnValue(of(apiError('UPSTREAM_ERROR', 'Não foi possível carregar as perguntas.')));
    fixture = TestBed.createComponent(AdminFaq);
    component = fixture.componentInstance;
    fixture.detectChanges();

    const erro = fixture.nativeElement.querySelector('.admin-faq__estado--erro');
    expect(erro?.textContent).toContain('Não foi possível carregar as perguntas.');
  });

  it('mostra estado vazio quando não há perguntas cadastradas', () => {
    faqServiceSpy.listarFaqAdmin.mockReturnValue(of(apiSuccess([])));
    fixture = TestBed.createComponent(AdminFaq);
    fixture.detectChanges();

    const estados = fixture.nativeElement.querySelectorAll('.admin-faq__estado');
    const textos = Array.from(estados as NodeListOf<HTMLElement>).map((e) => e.textContent);
    expect(textos.some((t) => t?.includes('Nenhuma pergunta cadastrada'))).toBe(true);
  });

  it('formulário de criação não envia campo ordem no payload', () => {
    component.abrirNovo();
    component.formulario.pergunta = 'Nova pergunta?';
    component.formulario.resposta = 'Nova resposta.';

    component.salvar();

    expect(faqServiceSpy.criarFaqAdmin).toHaveBeenCalledWith({ pergunta: 'Nova pergunta?', resposta: 'Nova resposta.' });
    const payloadEnviado = faqServiceSpy.criarFaqAdmin.mock.calls[0][0];
    expect(payloadEnviado.ordem).toBeUndefined();
  });

  it('formulário de edição não envia campo ordem no payload', () => {
    component.abrirEdicao(perguntas[1]);
    component.formulario.pergunta = 'Segunda editada?';

    component.salvar();

    expect(faqServiceSpy.editarFaqAdmin).toHaveBeenCalledWith('faq-2', {
      pergunta: 'Segunda editada?',
      resposta: perguntas[1].resposta,
    });
    const payloadEnviado = faqServiceSpy.editarFaqAdmin.mock.calls[0][1];
    expect(payloadEnviado.ordem).toBeUndefined();
  });

  it('não salva e mostra erro quando pergunta ou resposta está vazia', () => {
    component.abrirNovo();
    component.formulario.pergunta = '   ';
    component.formulario.resposta = 'Resposta válida.';

    component.salvar();

    expect(faqServiceSpy.criarFaqAdmin).not.toHaveBeenCalled();
    expect(component.erroFormulario).toContain('obrigatórias');
  });

  it('botão de mover para cima fica desabilitado no primeiro item da lista', () => {
    const botoesSubir = fixture.nativeElement.querySelectorAll('[aria-label*="para cima"]');
    expect(botoesSubir[0].disabled).toBe(true);
    expect(botoesSubir[1].disabled).toBe(false);
    expect(botoesSubir[2].disabled).toBe(false);
  });

  it('botão de mover para baixo fica desabilitado no último item da lista', () => {
    const botoesDescer = fixture.nativeElement.querySelectorAll('[aria-label*="para baixo"]');
    expect(botoesDescer[0].disabled).toBe(false);
    expect(botoesDescer[1].disabled).toBe(false);
    expect(botoesDescer[2].disabled).toBe(true);
  });

  it('clicar em mover para cima chama moverFaqParaCima com o id correto e recarrega a lista', () => {
    component.moverParaCima(perguntas[1]);

    expect(faqServiceSpy.moverFaqParaCima).toHaveBeenCalledWith('faq-2');
    expect(faqServiceSpy.listarFaqAdmin).toHaveBeenCalledTimes(2);
  });

  it('clicar em mover para baixo chama moverFaqParaBaixo com o id correto', () => {
    component.moverParaBaixo(perguntas[0]);

    expect(faqServiceSpy.moverFaqParaBaixo).toHaveBeenCalledWith('faq-1');
  });

  it('mostra erro quando reordenação falha, sem quebrar a lista atual', () => {
    faqServiceSpy.moverFaqParaCima.mockReturnValue(of(apiError('UPSTREAM_ERROR', 'Não foi possível reordenar a pergunta agora.')));

    component.moverParaCima(perguntas[1]);

    expect(component.erroMover).toBe('Não foi possível reordenar a pergunta agora.');
  });
});
