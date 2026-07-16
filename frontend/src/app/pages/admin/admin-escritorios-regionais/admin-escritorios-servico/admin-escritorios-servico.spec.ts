import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { AdminEscritoriosServico } from './admin-escritorios-servico';
import { EscritorioRegionalService } from '../../../../services/escritorio-regional/escritorio-regional';
import { EscritorioServico } from '../../../../services/escritorio-regional/escritorio-regional.model';
import { apiSuccess } from '../../../../services/api-envelope';

function escritorioFake(overrides: Partial<EscritorioServico> = {}): EscritorioServico {
  return {
    id: 'escritorio-1',
    nome: 'Escritório Fake',
    endereco: 'Rua Fake, 200',
    complemento: null,
    bairro: null,
    cidade: 'Cidade Fake - UF',
    emails: [],
    telefones: [],
    ordem: 0,
    criadoEm: '2026-01-01T00:00:00.000Z',
    atualizadoEm: '2026-01-01T00:00:00.000Z',
    ...overrides,
  };
}

describe('AdminEscritoriosServico', () => {
  let component: AdminEscritoriosServico;
  let fixture: ComponentFixture<AdminEscritoriosServico>;
  let httpMock: HttpTestingController;
  let serviceSpy: Record<string, ReturnType<typeof vi.fn>>;

  beforeEach(async () => {
    serviceSpy = {
      editarEscritorioServicoAdmin: vi.fn().mockReturnValue(of(apiSuccess(escritorioFake()))),
    };

    await TestBed.configureTestingModule({
      imports: [AdminEscritoriosServico],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: EscritorioRegionalService, useValue: serviceSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminEscritoriosServico);
    component = fixture.componentInstance;
    component.escritorios = [escritorioFake()];
    fixture.detectChanges();
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('deve criar', () => {
    expect(component).toBeTruthy();
  });

  describe('edição', () => {
    it('edita escritório existente preenchendo o formulário com os dados atuais, incluindo bairro e telefones', () => {
      const escritorioComDados = escritorioFake({
        id: 'escritorio-9',
        nome: 'Escritório Existente',
        bairro: 'Bela Vista',
        emails: ['contato@example.org'],
        telefones: [{ id: 'tel-1', label: 'Central', telefone: '11-90000-0000' }],
      });

      component.abrirEdicaoEscritorio(escritorioComDados);

      expect(component.formularioEscritorio.bairro).toBe('Bela Vista');
      expect(component.formularioEscritorio.emailsTexto).toBe('contato@example.org');
      expect(component.formularioEscritorio.telefones).toHaveLength(1);
      expect(component.formularioEscritorio.telefones[0].telefone).toBe('11-90000-0000');
      expect(component.formularioEscritorio.telefones[0].label).toBe('Central');

      component.formularioEscritorio.bairro = 'Bairro Editado';
      component.salvarEscritorio();

      expect(serviceSpy['editarEscritorioServicoAdmin']).toHaveBeenCalledWith(
        'escritorio-9',
        expect.objectContaining({ bairro: 'Bairro Editado' }),
      );
    });

    it('preenche bairro como campo vazio (nunca undefined) quando o escritório existente não tem bairro', () => {
      const escritorioSemBairro = escritorioFake({ id: 'escritorio-10', bairro: null });

      component.abrirEdicaoEscritorio(escritorioSemBairro);

      expect(component.formularioEscritorio.bairro).toBe('');
    });

    it('emite escritorioSalvo com o escritório retornado pelo service ao salvar com sucesso', () => {
      const escritorioSalvo = escritorioFake({ id: 'escritorio-1', nome: 'Escritório Salvo' });
      serviceSpy['editarEscritorioServicoAdmin'].mockReturnValue(of(apiSuccess(escritorioSalvo)));
      const emitidos: EscritorioServico[] = [];
      component.escritorioSalvo.subscribe((e) => emitidos.push(e));

      component.abrirEdicaoEscritorio(escritorioFake());
      component.formularioEscritorio.nome = 'Escritório Salvo';
      component.salvarEscritorio();

      expect(emitidos).toEqual([escritorioSalvo]);
    });
  });

  describe('busca de CEP', () => {
    beforeEach(() => {
      component.abrirEdicaoEscritorio(escritorioFake());
    });

    it('preenche endereço, bairro e cidade automaticamente ao buscar um CEP válido', () => {
      component.cepEscritorio = '01310-100';
      component.numeroEscritorio = '93';

      component.buscarCepEscritorio();

      const chamada = httpMock.expectOne('https://viacep.com.br/ws/01310100/json/');
      chamada.flush({
        logradouro: 'Rua Paulino Guimarães',
        bairro: 'Luz',
        localidade: 'São Paulo',
        uf: 'SP',
      });

      expect(component.formularioEscritorio.endereco).toBe('Rua Paulino Guimarães, 93');
      expect(component.formularioEscritorio.bairro).toBe('Luz');
      expect(component.formularioEscritorio.cidade).toBe('São Paulo - SP');
      expect(component.erroCepEscritorio).toBe('');
    });

    it('mostra erro e não altera o formulário quando o CEP não é encontrado', () => {
      component.cepEscritorio = '00000-000';

      component.buscarCepEscritorio();

      const chamada = httpMock.expectOne('https://viacep.com.br/ws/00000000/json/');
      chamada.flush({ erro: true });

      expect(component.erroCepEscritorio).toBe('CEP não encontrado.');
      expect(component.formularioEscritorio.endereco).toBe('Rua Fake, 200');
    });

    it('mostra erro de CEP inválido sem chamar a API quando o CEP tem menos de 8 dígitos', () => {
      component.cepEscritorio = 'abc';

      component.buscarCepEscritorio();

      httpMock.expectNone('https://viacep.com.br/ws/abc/json/');
      expect(component.erroCepEscritorio).toContain('inválido');
    });

    it('trata falha de rede na busca de CEP sem quebrar o formulário', () => {
      component.cepEscritorio = '01310-100';

      component.buscarCepEscritorio();

      const chamada = httpMock.expectOne('https://viacep.com.br/ws/01310100/json/');
      chamada.error(new ProgressEvent('network error'));

      expect(component.erroCepEscritorio).toBe('CEP não encontrado.');
    });
  });

  describe('múltiplos telefones', () => {
    beforeEach(() => {
      component.abrirEdicaoEscritorio(escritorioFake());
    });

    it('adiciona telefone à lista do formulário', () => {
      expect(component.formularioEscritorio.telefones).toHaveLength(0);

      component.adicionarTelefoneEscritorio();

      expect(component.formularioEscritorio.telefones).toHaveLength(1);
    });

    it('permite adicionar múltiplos telefones e remover um deles individualmente', () => {
      component.adicionarTelefoneEscritorio();
      component.adicionarTelefoneEscritorio();
      component.formularioEscritorio.telefones[0].label = 'Central';
      component.formularioEscritorio.telefones[0].telefone = '11-90000-0000';
      component.formularioEscritorio.telefones[1].label = 'Plantão';
      component.formularioEscritorio.telefones[1].telefone = '11-91111-1111';
      const idParaRemover = component.formularioEscritorio.telefones[0].id;

      component.removerTelefoneEscritorio(idParaRemover);

      expect(component.formularioEscritorio.telefones).toHaveLength(1);
      expect(component.formularioEscritorio.telefones[0].label).toBe('Plantão');
    });

    it('envia a lista de telefones editada no payload ao salvar o escritório', () => {
      component.formularioEscritorio.nome = 'Escritório Telefones';
      component.formularioEscritorio.endereco = 'Rua X, 1';
      component.formularioEscritorio.cidade = 'Cidade X - UF';
      component.adicionarTelefoneEscritorio();
      component.formularioEscritorio.telefones[0].label = 'Central';
      component.formularioEscritorio.telefones[0].telefone = '11-98888-7777';

      component.salvarEscritorio();

      expect(serviceSpy['editarEscritorioServicoAdmin']).toHaveBeenCalledWith(
        'escritorio-1',
        expect.objectContaining({
          telefones: [{ label: 'Central', telefone: '11-98888-7777' }],
        }),
      );
    });

    it('não salva quando há telefone incompleto (sem número) na lista', () => {
      component.formularioEscritorio.nome = 'Escritório Telefone Incompleto';
      component.formularioEscritorio.endereco = 'Rua X, 1';
      component.formularioEscritorio.cidade = 'Cidade X - UF';
      component.adicionarTelefoneEscritorio();

      component.salvarEscritorio();

      expect(serviceSpy['editarEscritorioServicoAdmin']).not.toHaveBeenCalled();
      expect(component.erroFormularioEscritorio).toContain('telefones incompletos');
    });
  });

  describe('e-mails manuais (emailsTexto)', () => {
    beforeEach(() => {
      component.abrirEdicaoEscritorio(escritorioFake());
      component.formularioEscritorio.nome = 'Escritório E-mails';
      component.formularioEscritorio.endereco = 'Rua X, 1';
      component.formularioEscritorio.cidade = 'Cidade X - UF';
    });

    it('converte texto separado por vírgula em lista de e-mails, removendo espaços', () => {
      component.formularioEscritorio.emailsTexto = 'contato@example.org,  suporte@example.org ,outro@example.org';

      component.salvarEscritorio();

      expect(serviceSpy['editarEscritorioServicoAdmin']).toHaveBeenCalledWith(
        'escritorio-1',
        expect.objectContaining({
          emails: ['contato@example.org', 'suporte@example.org', 'outro@example.org'],
        }),
      );
    });

    it('envia lista de e-mails vazia quando emailsTexto está vazio, sem quebrar o formulário', () => {
      component.formularioEscritorio.emailsTexto = '';

      component.salvarEscritorio();

      expect(serviceSpy['editarEscritorioServicoAdmin']).toHaveBeenCalledWith(
        'escritorio-1',
        expect.objectContaining({ emails: [] }),
      );
    });

    it('não altera emailsTexto ao mexer em outros campos do formulário (bairro, telefones)', () => {
      component.formularioEscritorio.emailsTexto = 'contato@example.org';
      component.formularioEscritorio.bairro = 'Centro';
      component.adicionarTelefoneEscritorio();

      expect(component.formularioEscritorio.emailsTexto).toBe('contato@example.org');
    });
  });

  describe('lista ordenada', () => {
    it('ordena escritórios pelo campo ordem', () => {
      component.escritorios = [
        escritorioFake({ id: 'escritorio-b', ordem: 1 }),
        escritorioFake({ id: 'escritorio-a', ordem: 0 }),
      ];

      expect(component.escritoriosOrdenados.map((e) => e.id)).toEqual(['escritorio-a', 'escritorio-b']);
    });
  });
});
