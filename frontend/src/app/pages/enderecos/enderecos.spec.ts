import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';

import { Enderecos } from './enderecos';
import { EscritorioRegionalService } from '../../services/escritorio-regional/escritorio-regional';
import {
  EscritoriosRegionais,
  EstadoIntergrupo,
  GrupoIntergrupo,
  RegiaoIntergrupo,
  UnidadeFederativaComRegiao,
} from '../../services/escritorio-regional/escritorio-regional.model';
import { ReuniaoOnlineService } from '../../services/reuniao-online/reuniao-online';
import { ReuniaoOnline } from '../../services/reuniao-online/reuniao-online.model';
import { apiError, apiSuccess } from '../../services/api-envelope';
import { WHATSAPP_PHONE_NUMBER } from '../../config/whatsapp.config';

function grupoFake(overrides: Partial<GrupoIntergrupo> = {}): GrupoIntergrupo {
  return {
    id: 'grupo-1',
    estadoId: 'estado-1',
    nome: 'Grupo Esperança',
    endereco: 'Rua Fake, 100',
    bairro: null,
    cidade: 'Cidade Fake',
    telefones: [],
    agenda: [{ diaSemana: 'SEGUNDA', horario: '19:00' }],
    ordem: 0,
    criadoEm: '2026-01-01T00:00:00.000Z',
    atualizadoEm: '2026-01-01T00:00:00.000Z',
    ...overrides,
  };
}

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

function reuniaoOnlineFake(overrides: Partial<ReuniaoOnline> = {}): ReuniaoOnline {
  return {
    id: 'reuniao-online-1',
    nome: 'Grupo Esperança Online',
    telefone: null,
    descricao: null,
    videoUrl: null,
    panfletoImagemId: null,
    panfletoImagemUrl: null,
    agenda: [{ diaSemana: 'SEGUNDA', horario: '19:00' }],
    atualizadoEm: '2026-01-01T00:00:00.000Z',
    ...overrides,
  };
}

describe('Enderecos', () => {
  let component: Enderecos;
  let fixture: ComponentFixture<Enderecos>;
  let escritorioServiceSpy: Record<string, ReturnType<typeof vi.fn>>;
  let reuniaoOnlineServiceSpy: Record<string, ReturnType<typeof vi.fn>>;

  const regiao1 = regiaoFake({ id: 'regiao-1', nome: 'Região Um', estados: [estadoFake()] });
  const regiao2 = regiaoFake({ id: 'regiao-2', nome: 'Região Dois', codigo: 'NORDESTE', estados: [] });

  function estruturaCompleta(): EscritoriosRegionais {
    return {
      escritorios: [],
      regioes: [regiao1, regiao2],
    };
  }

  function unidadesFederativasFake(): UnidadeFederativaComRegiao[] {
    return [
      { uf: 'SP', nome: 'São Paulo', codigoRegiao: 'SUDESTE' },
      { uf: 'BA', nome: 'Bahia', codigoRegiao: 'NORDESTE' },
    ];
  }

  async function criarComponente(reunioesOnline: ReuniaoOnline[] = []): Promise<void> {
    reuniaoOnlineServiceSpy = {
      listarReunioesOnline: vi.fn().mockReturnValue(of(apiSuccess(reunioesOnline))),
    };

    TestBed.resetTestingModule();

    await TestBed.configureTestingModule({
      imports: [Enderecos],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: EscritorioRegionalService, useValue: escritorioServiceSpy },
        { provide: ReuniaoOnlineService, useValue: reuniaoOnlineServiceSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Enderecos);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();
  }

  describe('carregamento com sucesso', () => {
    beforeEach(async () => {
      escritorioServiceSpy = {
        listarEstrutura: vi.fn().mockReturnValue(of(apiSuccess(estruturaCompleta()))),
        listarUnidadesFederativas: vi.fn().mockReturnValue(of(apiSuccess(unidadesFederativasFake()))),
      };
      await criarComponente();
    });

    it('deve criar', () => {
      expect(component).toBeTruthy();
    });

    it('carrega regiões e reuniões online em paralelo, selecionando a primeira região automaticamente', () => {
      expect(component.carregando()).toBe(false);
      expect(component.erro()).toBe(false);
      expect(component.regioes()).toHaveLength(2);
      expect(component.regiaoSelecionada()).toBe('regiao-1');
      expect(component.regiaoAtual).toBe(regiao1);
    });

    it('troca a região selecionada ao chamar selecionarRegiao', () => {
      component.selecionarRegiao(regiao2);

      expect(component.regiaoSelecionada()).toBe('regiao-2');
      expect(component.regiaoAtual).toBe(regiao2);
    });

    it('formata a agenda com abreviação do dia e horário', () => {
      const texto = component.formatarAgenda([
        { diaSemana: 'SEGUNDA', horario: '19:00' },
        { diaSemana: 'QUARTA', horario: '20:30' },
      ]);

      expect(texto).toBe('Seg 19:00 · Qua 20:30');
    });

    it('link de WhatsApp para grupo inexistente usa WHATSAPP_PHONE_NUMBER', () => {
      expect(component.linkWhatsappGrupoInexistente()).toContain(WHATSAPP_PHONE_NUMBER);
    });

    it('link de WhatsApp para pedir reunião online usa WHATSAPP_PHONE_NUMBER', () => {
      expect(component.linkWhatsappPedirLinkReuniaoOnline()).toContain(WHATSAPP_PHONE_NUMBER);
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
      escritorioServiceSpy = {
        listarEstrutura: vi.fn().mockReturnValue(of(apiSuccess({ escritorios: [], regioes: [] }))),
        listarUnidadesFederativas: vi.fn().mockReturnValue(of(apiSuccess(unidadesFederativasFake()))),
      };
      await criarComponente();
    });

    it('não quebra quando não há regiões, mantendo regiaoSelecionada nula', () => {
      expect(component.carregando()).toBe(false);
      expect(component.erro()).toBe(false);
      expect(component.regiaoSelecionada()).toBeNull();
      expect(component.regiaoAtual).toBeUndefined();
    });
  });

  describe('erro de negócio (envelope success=false)', () => {
    beforeEach(async () => {
      escritorioServiceSpy = {
        listarEstrutura: vi.fn().mockReturnValue(of(apiError('UPSTREAM_ERROR', 'Não foi possível carregar os endereços.'))),
        listarUnidadesFederativas: vi.fn().mockReturnValue(of(apiSuccess(unidadesFederativasFake()))),
      };
      await criarComponente();
    });

    it('marca erro e encerra carregamento', () => {
      expect(component.carregando()).toBe(false);
      expect(component.erro()).toBe(true);
      expect(component.regioes()).toHaveLength(0);
    });
  });

  describe('falha de rede', () => {
    beforeEach(async () => {
      escritorioServiceSpy = {
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

  describe('painel Online', () => {
    beforeEach(() => {
      escritorioServiceSpy = {
        listarEstrutura: vi.fn().mockReturnValue(of(apiSuccess(estruturaCompleta()))),
        listarUnidadesFederativas: vi.fn().mockReturnValue(of(apiSuccess(unidadesFederativasFake()))),
      };
    });

    it('agrupa horários de reuniões online por dia da semana', async () => {
      const reuniaoA = reuniaoOnlineFake({
        id: 'reuniao-a',
        nome: 'Grupo A',
        agenda: [{ diaSemana: 'SEGUNDA', horario: '20:00' }],
      });
      const reuniaoB = reuniaoOnlineFake({
        id: 'reuniao-b',
        nome: 'Grupo B',
        agenda: [{ diaSemana: 'SEGUNDA', horario: '19:00' }],
      });
      await criarComponente([reuniaoA, reuniaoB]);

      const grade = component.gradeOnlinePorDia();
      const segunda = grade.find((dia) => dia.diaSemana === 'SEGUNDA');

      expect(segunda?.itens).toEqual([
        { horario: '19:00', nomeGrupo: 'Grupo B' },
        { horario: '20:00', nomeGrupo: 'Grupo A' },
      ]);
    });

    it('identifica o Grupo Salvador como grupo próprio online', async () => {
      const grupoSalvador = reuniaoOnlineFake({ id: 'grupo-salvador', nome: 'Grupo Salvador' });
      await criarComponente([grupoSalvador]);

      expect(component.grupoProprioOnline()).toEqual(grupoSalvador);
    });

    it('retorna null como grupo próprio quando não há Grupo Salvador cadastrado', async () => {
      const outroGrupo = reuniaoOnlineFake({ id: 'outro', nome: 'Grupo Outro' });
      await criarComponente([outroGrupo]);

      expect(component.grupoProprioOnline()).toBeNull();
    });

    it('lista nomes únicos de grupos online ordenados alfabeticamente', async () => {
      const reuniaoA = reuniaoOnlineFake({ id: 'a', nome: 'Grupo Zulu', agenda: [{ diaSemana: 'SEGUNDA', horario: '19:00' }] });
      const reuniaoB = reuniaoOnlineFake({ id: 'b', nome: 'Grupo Alfa', agenda: [{ diaSemana: 'TERCA', horario: '19:00' }] });
      await criarComponente([reuniaoA, reuniaoB]);

      expect(component.nomesGruposOnline()).toEqual(['Grupo Alfa', 'Grupo Zulu']);
    });

    it('grade fica vazia quando não há reuniões online cadastradas', async () => {
      await criarComponente([]);

      expect(component.gradeOnlinePorDia()).toHaveLength(0);
      expect(component.nomesGruposOnline()).toHaveLength(0);
    });
  });

  describe('modal de detalhes de grupo presencial', () => {
    beforeEach(async () => {
      escritorioServiceSpy = {
        listarEstrutura: vi.fn().mockReturnValue(of(apiSuccess(estruturaCompleta()))),
        listarUnidadesFederativas: vi.fn().mockReturnValue(of(apiSuccess(unidadesFederativasFake()))),
      };
      await criarComponente();
    });

    it('abre e fecha os detalhes de um grupo', () => {
      const grupo = grupoFake();
      component.abrirDetalhesGrupo(grupo);
      expect(component.grupoDetalhe).toBe(grupo);

      component.fecharDetalhes();
      expect(component.grupoDetalhe).toBeNull();
    });

    it('fecha os detalhes ao pressionar Escape', () => {
      component.abrirDetalhesGrupo(grupoFake());
      component.onEscape();
      expect(component.grupoDetalhe).toBeNull();
    });

    it('não quebra ao pressionar Escape sem detalhes abertos', () => {
      expect(() => component.onEscape()).not.toThrow();
      expect(component.grupoDetalhe).toBeNull();
    });

    it('fecha os detalhes ao clicar fora do painel (evento no overlay)', () => {
      component.abrirDetalhesGrupo(grupoFake());
      const overlay = document.createElement('div');
      const evento = { target: overlay, currentTarget: overlay } as unknown as MouseEvent;

      component.fecharDetalhesSeCliqueForaDoPainel(evento);

      expect(component.grupoDetalhe).toBeNull();
    });

    it('mantém os detalhes abertos ao clicar dentro do painel', () => {
      const grupo = grupoFake();
      component.abrirDetalhesGrupo(grupo);
      const overlay = document.createElement('div');
      const painel = document.createElement('div');
      const evento = { target: painel, currentTarget: overlay } as unknown as MouseEvent;

      component.fecharDetalhesSeCliqueForaDoPainel(evento);

      expect(component.grupoDetalhe).toBe(grupo);
    });

    it('usa o telefone do grupo no link do WhatsApp quando informado', () => {
      const grupo = grupoFake({ telefones: [{ id: 'tel-1', label: null, telefone: '5511988887777' }] });

      expect(component.whatsappLinkGrupo(grupo)).toContain('5511988887777');
    });

    it('cai para WHATSAPP_PHONE_NUMBER no link do WhatsApp quando o grupo não tem telefone', () => {
      const grupo = grupoFake({ telefones: [] });

      expect(component.whatsappLinkGrupo(grupo)).toContain(WHATSAPP_PHONE_NUMBER);
    });
  });
});
