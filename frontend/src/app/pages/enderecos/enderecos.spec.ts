import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';

import { Enderecos } from './enderecos';
import { EscritorioRegionalService } from '../../services/escritorio-regional/escritorio-regional';
import {
  EscritoriosRegionais,
  EscritorioServico,
  EstadoIntergrupo,
  RegiaoIntergrupo,
  UnidadeFederativaComRegiao,
} from '../../services/escritorio-regional/escritorio-regional.model';
import { apiError, apiSuccess } from '../../services/api-envelope';

function estadoFake(overrides: Partial<EstadoIntergrupo> = {}): EstadoIntergrupo {
  return {
    id: 'estado-1',
    regiaoId: 'regiao-1',
    uf: 'SP',
    nome: 'São Paulo',
    ordem: 0,
    grupos: [],
    criadoEm: '2026-01-01T00:00:00.000Z',
    atualizadoEm: '2026-01-01T00:00:00.000Z',
    ...overrides,
  };
}

function regiaoFake(overrides: Partial<RegiaoIntergrupo> = {}): RegiaoIntergrupo {
  return {
    id: 'regiao-1',
    codigo: 'SUDESTE',
    nome: 'Região Fake',
    ordem: 0,
    estados: [],
    criadoEm: '2026-01-01T00:00:00.000Z',
    atualizadoEm: '2026-01-01T00:00:00.000Z',
    ...overrides,
  };
}

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

describe('Enderecos', () => {
  let component: Enderecos;
  let fixture: ComponentFixture<Enderecos>;
  let serviceSpy: Record<string, ReturnType<typeof vi.fn>>;

  const regiao1 = regiaoFake({ id: 'regiao-1', nome: 'Região Um', estados: [estadoFake()] });
  const regiao2 = regiaoFake({ id: 'regiao-2', nome: 'Região Dois', estados: [] });

  function estruturaCompleta(): EscritoriosRegionais {
    return {
      escritorios: [escritorioFake()],
      regioes: [regiao1, regiao2],
    };
  }

  function unidadesFederativasFake(): UnidadeFederativaComRegiao[] {
    return [
      { uf: 'SP', nome: 'São Paulo', codigoRegiao: 'SUDESTE' },
      { uf: 'BA', nome: 'Bahia', codigoRegiao: 'NORDESTE' },
    ];
  }

  async function criarComponente(): Promise<void> {
    await TestBed.configureTestingModule({
      imports: [Enderecos],
      providers: [provideRouter([]), { provide: EscritorioRegionalService, useValue: serviceSpy }],
    }).compileComponents();

    fixture = TestBed.createComponent(Enderecos);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  describe('carregamento com sucesso', () => {
    beforeEach(async () => {
      serviceSpy = {
        listarEstrutura: vi.fn().mockReturnValue(of(apiSuccess(estruturaCompleta()))),
        listarUnidadesFederativas: vi.fn().mockReturnValue(of(apiSuccess(unidadesFederativasFake()))),
      };
      await criarComponente();
    });

    it('deve criar', () => {
      expect(component).toBeTruthy();
    });

    it('carrega escritórios e regiões, selecionando a primeira região automaticamente', () => {
      expect(component.carregando()).toBe(false);
      expect(component.erro()).toBe(false);
      expect(component.escritorios()).toHaveLength(1);
      expect(component.regioes()).toHaveLength(2);
      expect(component.regiaoSelecionada).toBe('regiao-1');
      expect(component.regiaoAtual).toBe(regiao1);
    });

    it('troca a região selecionada ao chamar selecionarRegiao', () => {
      component.selecionarRegiao(regiao2);

      expect(component.regiaoSelecionada).toBe('regiao-2');
      expect(component.regiaoAtual).toBe(regiao2);
    });

    it('formata a agenda com abreviação do dia e horário', () => {
      const texto = component.formatarAgenda([
        { diaSemana: 'SEGUNDA', horario: '19:00' },
        { diaSemana: 'QUARTA', horario: '20:30' },
      ]);

      expect(texto).toBe('Seg 19:00 · Qua 20:30');
    });

    it('formata telefones com label quando presente e sem label quando ausente', () => {
      const texto = component.formatarTelefones([
        { label: 'Central', telefone: '(11) 4000-0000' },
        { label: null, telefone: '(11) 90000-0000' },
      ]);

      expect(texto).toBe('Central: (11) 4000-0000 / (11) 90000-0000');
    });
  });

  describe('lista vazia', () => {
    beforeEach(async () => {
      serviceSpy = {
        listarEstrutura: vi.fn().mockReturnValue(of(apiSuccess({ escritorios: [], regioes: [] }))),
        listarUnidadesFederativas: vi.fn().mockReturnValue(of(apiSuccess(unidadesFederativasFake()))),
      };
      await criarComponente();
    });

    it('não quebra quando não há regiões, mantendo regiaoSelecionada nula', () => {
      expect(component.carregando()).toBe(false);
      expect(component.erro()).toBe(false);
      expect(component.regiaoSelecionada).toBeNull();
      expect(component.regiaoAtual).toBeUndefined();
    });
  });

  describe('erro de negócio (envelope success=false)', () => {
    beforeEach(async () => {
      serviceSpy = {
        listarEstrutura: vi.fn().mockReturnValue(of(apiError('UPSTREAM_ERROR', 'Não foi possível carregar os endereços.'))),
        listarUnidadesFederativas: vi.fn().mockReturnValue(of(apiSuccess(unidadesFederativasFake()))),
      };
      await criarComponente();
    });

    it('marca erro e encerra carregamento', () => {
      expect(component.carregando()).toBe(false);
      expect(component.erro()).toBe(true);
      expect(component.escritorios()).toHaveLength(0);
      expect(component.regioes()).toHaveLength(0);
    });
  });

  describe('falha de rede', () => {
    beforeEach(async () => {
      serviceSpy = {
        listarEstrutura: vi.fn().mockReturnValue(throwError(() => new Error('network error'))),
        listarUnidadesFederativas: vi.fn().mockReturnValue(of(apiSuccess(unidadesFederativasFake()))),
      };
      await criarComponente();
    });

    it('marca erro e encerra carregamento sem quebrar o componente', () => {
      expect(component.carregando()).toBe(false);
      expect(component.erro()).toBe(true);
    });
  });
});
