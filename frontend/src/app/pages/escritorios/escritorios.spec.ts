import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';

import { Escritorios } from './escritorios';
import { EscritorioRegionalService } from '../../services/escritorio-regional/escritorio-regional';
import { EscritorioServico } from '../../services/escritorio-regional/escritorio-regional.model';
import { apiError, apiSuccess } from '../../services/api-envelope';

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

describe('Escritorios', () => {
  let component: Escritorios;
  let fixture: ComponentFixture<Escritorios>;
  let serviceSpy: Record<string, ReturnType<typeof vi.fn>>;

  async function criarComponente(): Promise<void> {
    await TestBed.configureTestingModule({
      imports: [Escritorios],
      providers: [provideRouter([]), { provide: EscritorioRegionalService, useValue: serviceSpy }],
    }).compileComponents();

    fixture = TestBed.createComponent(Escritorios);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  describe('carregamento com sucesso', () => {
    beforeEach(async () => {
      serviceSpy = {
        listarEstrutura: vi.fn().mockReturnValue(
          of(apiSuccess({ escritorios: [escritorioFake(), escritorioFake({ id: 'escritorio-2' })], regioes: [] })),
        ),
      };
      await criarComponente();
    });

    it('deve criar', () => {
      expect(component).toBeTruthy();
    });

    it('carrega e exibe a lista de escritórios', () => {
      expect(component.carregando()).toBe(false);
      expect(component.erro()).toBe(false);
      expect(component.escritorios()).toHaveLength(2);
    });

    it('renderiza um card por escritório no template', () => {
      const cards = fixture.nativeElement.querySelectorAll('.escritorio-card');
      expect(cards.length).toBe(2);
    });

  });

  describe('escritório com telefone e email preenchidos', () => {
    beforeEach(async () => {
      serviceSpy = {
        listarEstrutura: vi.fn().mockReturnValue(
          of(
            apiSuccess({
              escritorios: [
                escritorioFake({
                  telefones: [{ id: 'tel-1', label: 'Central', telefone: '(11) 4000-0000' }],
                  emails: ['contato@fake.org'],
                }),
              ],
              regioes: [],
            }),
          ),
        ),
      };
      await criarComponente();
    });

    it('renderiza telefone com label e email do escritório', () => {
      const texto = fixture.nativeElement.textContent as string;
      expect(texto).toContain('Central: (11) 4000-0000');
      expect(texto).toContain('contato@fake.org');
    });
  });

  describe('lista vazia', () => {
    beforeEach(async () => {
      serviceSpy = {
        listarEstrutura: vi.fn().mockReturnValue(of(apiSuccess({ escritorios: [], regioes: [] }))),
      };
      await criarComponente();
    });

    it('não quebra e mostra estado de lista vazia', () => {
      expect(component.carregando()).toBe(false);
      expect(component.erro()).toBe(false);
      expect(component.escritorios()).toHaveLength(0);
      expect(fixture.nativeElement.textContent).toContain('Nenhum escritório regional cadastrado no momento.');
    });
  });

  describe('erro de negócio (envelope success=false)', () => {
    beforeEach(async () => {
      serviceSpy = {
        listarEstrutura: vi.fn().mockReturnValue(
          of(apiError('UPSTREAM_ERROR', 'Não foi possível carregar os escritórios.')),
        ),
      };
      await criarComponente();
    });

    it('marca erro e encerra carregamento sem exibir a lista', () => {
      expect(component.carregando()).toBe(false);
      expect(component.erro()).toBe(true);
      expect(component.escritorios()).toHaveLength(0);
      expect(fixture.nativeElement.querySelectorAll('.escritorio-card').length).toBe(0);
    });
  });

  describe('falha de rede', () => {
    beforeEach(async () => {
      serviceSpy = {
        listarEstrutura: vi.fn().mockReturnValue(throwError(() => new Error('network error'))),
      };
      await criarComponente();
    });

    it('marca erro e encerra carregamento sem quebrar o componente', () => {
      expect(component.carregando()).toBe(false);
      expect(component.erro()).toBe(true);
    });
  });
});
