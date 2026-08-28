import { PLATFORM_ID } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
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
import { ReuniaoService } from '../../services/reuniao/reuniao';
import { Reuniao } from '../../services/reuniao/reuniao.model';
import { apiError, apiSuccess } from '../../services/api-envelope';
import { WHATSAPP_PHONE_NUMBER } from '../../config/whatsapp.config';

interface FonteFake {
  setData: ReturnType<typeof vi.fn>;
  dadosAtuais: unknown;
}

interface MapaFake {
  addControl: ReturnType<typeof vi.fn>;
  on: ReturnType<typeof vi.fn>;
  resize: ReturnType<typeof vi.fn>;
  remove: ReturnType<typeof vi.fn>;
  addSource: ReturnType<typeof vi.fn>;
  addLayer: ReturnType<typeof vi.fn>;
  getSource: (id: string) => FonteFake;
  getCanvas: ReturnType<typeof vi.fn>;
  setCenter: ReturnType<typeof vi.fn>;
  setZoom: ReturnType<typeof vi.fn>;
  fitBounds: ReturnType<typeof vi.fn>;
  handlers: Record<string, (...args: unknown[]) => void>;
  fontes: Record<string, FonteFake>;
}

const mapasCriados: MapaFake[] = [];

function criarFonteFake(): FonteFake {
  const fonte: Partial<FonteFake> = { dadosAtuais: undefined };
  fonte.setData = vi.fn((dados: unknown) => {
    fonte.dadosAtuais = dados;
  });
  return fonte as FonteFake;
}

function criarMapaFake(): MapaFake {
  const mapa: Partial<MapaFake> = {
    handlers: {},
    fontes: {},
  };
  mapa.addControl = vi.fn();
  mapa.resize = vi.fn();
  mapa.remove = vi.fn();
  mapa.getCanvas = vi.fn(() => ({ style: { cursor: '' } }));
  mapa.setCenter = vi.fn();
  mapa.setZoom = vi.fn();
  mapa.fitBounds = vi.fn();
  mapa.on = vi.fn((evento: string, layerOuHandler: unknown, talvezHandler?: unknown) => {
    const chave = typeof talvezHandler === 'function' ? `${evento}:${layerOuHandler}` : evento;
    const handler = typeof talvezHandler === 'function' ? talvezHandler : layerOuHandler;
    mapa.handlers![chave] = handler as (...args: unknown[]) => void;
    return mapa;
  });
  mapa.addSource = vi.fn((id: string, config: { data?: unknown }) => {
    const fonte = criarFonteFake();
    fonte.dadosAtuais = config?.data;
    mapa.fontes![id] = fonte;
  });
  mapa.addLayer = vi.fn();
  mapa.getSource = vi.fn((id: string) => mapa.fontes![id]);
  return mapa as MapaFake;
}

class LngLatBoundsFake {
  pontos: unknown[] = [];
  extend(ponto: unknown): void {
    this.pontos.push(ponto);
  }
}

class ResizeObserverFake {
  observe(): void {}
  unobserve(): void {}
  disconnect(): void {}
}

vi.mock('maplibre-gl', () => {
  const modulo = {
    Map: vi.fn(function MapFake() {
      const mapa = criarMapaFake();
      mapasCriados.push(mapa);
      return mapa;
    }),
    LngLatBounds: LngLatBoundsFake,
    NavigationControl: vi.fn(),
    Popup: vi.fn(function PopupFake() {
      return {
        setLngLat: vi.fn().mockReturnThis(),
        setText: vi.fn().mockReturnThis(),
        addTo: vi.fn().mockReturnThis(),
      };
    }),
    setWorkerUrl: vi.fn(),
  };
  return { ...modulo, default: modulo };
});

function dispararLoadDoMapa(mapa: MapaFake): void {
  mapa.handlers['load']?.();
}

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

function reuniaoFake(overrides: Partial<Reuniao> = {}): Reuniao {
  return {
    id: 'reuniao-1',
    nome: 'Grupo Esperança Online',
    categoria: 'virtual',
    descricao: 'Reunião de apoio mútuo por videochamada.',
    agenda: [{ diaSemana: 'SEGUNDA', horario: '19:00' }],
    dia: 'Segunda',
    horario: '19:00',
    local: 'Link de acesso enviado por e-mail',
    whatsapp: null,
    diaSemana: null,
    periodo: null,
    latitude: null,
    longitude: null,
    cep: null,
    numero: null,
    logradouro: null,
    bairro: null,
    cidade: null,
    estado: 'SP',
    linkReuniao: null,
    avisos: [],
    ...overrides,
  };
}

describe('Enderecos', () => {
  let component: Enderecos;
  let fixture: ComponentFixture<Enderecos>;
  let escritorioServiceSpy: Record<string, ReturnType<typeof vi.fn>>;
  let reuniaoServiceSpy: Record<string, ReturnType<typeof vi.fn>>;

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

  beforeAll(() => {
    if (typeof globalThis.ResizeObserver === 'undefined') {
      globalThis.ResizeObserver = ResizeObserverFake as unknown as typeof ResizeObserver;
    }
  });

  async function criarComponente(reunioes: Reuniao[] = [], plataforma: 'browser' | 'server' = 'browser'): Promise<void> {
    mapasCriados.length = 0;
    reuniaoServiceSpy = {
      listarReunioes: vi.fn().mockReturnValue(of(apiSuccess(reunioes))),
    };

    TestBed.resetTestingModule();

    await TestBed.configureTestingModule({
      imports: [Enderecos],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: PLATFORM_ID, useValue: plataforma },
        { provide: EscritorioRegionalService, useValue: escritorioServiceSpy },
        { provide: ReuniaoService, useValue: reuniaoServiceSpy },
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

    it('carrega regiões e reuniões em paralelo, selecionando a primeira região automaticamente', () => {
      expect(component.carregando()).toBe(false);
      expect(component.erro()).toBe(false);
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

    it('link de WhatsApp para grupo inexistente usa WHATSAPP_PHONE_NUMBER', () => {
      expect(component.linkWhatsappGrupoInexistente()).toContain(WHATSAPP_PHONE_NUMBER);
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
      expect(component.regiaoSelecionada).toBeNull();
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

  describe('reconciliação por UF', () => {
    beforeEach(() => {
      escritorioServiceSpy = {
        listarEstrutura: vi.fn().mockReturnValue(of(apiSuccess(estruturaCompleta()))),
        listarUnidadesFederativas: vi.fn().mockReturnValue(of(apiSuccess(unidadesFederativasFake()))),
      };
    });

    it('lista reuniões online cuja categoria é virtual e o estado bate com a UF', async () => {
      const virtualSp = reuniaoFake({ id: 'virtual-sp', estado: 'SP' });
      const virtualBa = reuniaoFake({ id: 'virtual-ba', estado: 'BA' });
      await criarComponente([virtualSp, virtualBa]);

      const reunioesOnlineSp = component.reunioesOnlineDaUf('SP');

      expect(reunioesOnlineSp).toEqual([virtualSp]);
    });

    it('não considera reunião presencial como online mesmo com estado preenchido', async () => {
      const presencialSp = reuniaoFake({ id: 'presencial-sp', categoria: 'presencial', estado: 'SP' });
      await criarComponente([presencialSp]);

      expect(component.reunioesOnlineDaUf('SP')).toEqual([]);
    });

    it('omite sub-seção Online quando não há reunião virtual para a UF', async () => {
      await criarComponente([]);

      expect(component.reunioesOnlineDaUf('SP')).toHaveLength(0);
    });

    it('sub-seção Presencial usa os grupos de EstadoIntergrupo.grupos', async () => {
      const grupo = grupoFake({ id: 'grupo-sp', nome: 'Grupo SP Centro' });
      const regiaoComGrupo = regiaoFake({
        id: 'regiao-1',
        estados: [estadoFake({ uf: 'SP', grupos: [grupo] })],
      });
      escritorioServiceSpy['listarEstrutura'] = vi.fn().mockReturnValue(
        of(apiSuccess({ escritorios: [], regioes: [regiaoComGrupo] })),
      );
      await criarComponente([]);

      const item = component.estadosRegiaoAtual().find((i) => i.uf === 'SP');
      expect(item?.tipo).toBe('com-grupo');
      if (item?.tipo === 'com-grupo') {
        expect(item.estado.grupos).toEqual([grupo]);
      }
    });
  });

  describe('modal de detalhes', () => {
    beforeEach(async () => {
      escritorioServiceSpy = {
        listarEstrutura: vi.fn().mockReturnValue(of(apiSuccess(estruturaCompleta()))),
        listarUnidadesFederativas: vi.fn().mockReturnValue(of(apiSuccess(unidadesFederativasFake()))),
      };
      await criarComponente([reuniaoFake()]);
    });

    it('abre e fecha os detalhes de uma reunião', () => {
      const reuniao = reuniaoFake();
      component.abrirDetalhes(reuniao);
      expect(component.reuniaoDetalhe).toBe(reuniao);

      component.fecharDetalhes();
      expect(component.reuniaoDetalhe).toBeNull();
    });

    it('fecha os detalhes ao pressionar Escape', () => {
      component.abrirDetalhes(reuniaoFake());
      component.onEscape();
      expect(component.reuniaoDetalhe).toBeNull();
    });

    it('não quebra ao pressionar Escape sem detalhes abertos', () => {
      expect(() => component.onEscape()).not.toThrow();
      expect(component.reuniaoDetalhe).toBeNull();
    });

    it('fecha os detalhes ao clicar fora do painel (evento no overlay)', () => {
      component.abrirDetalhes(reuniaoFake());
      const overlay = document.createElement('div');
      const evento = { target: overlay, currentTarget: overlay } as unknown as MouseEvent;

      component.fecharDetalhesSeCliqueForaDoPainel(evento);

      expect(component.reuniaoDetalhe).toBeNull();
    });

    it('mantém os detalhes abertos ao clicar dentro do painel', () => {
      const reuniao = reuniaoFake();
      component.abrirDetalhes(reuniao);
      const overlay = document.createElement('div');
      const painel = document.createElement('div');
      const evento = { target: painel, currentTarget: overlay } as unknown as MouseEvent;

      component.fecharDetalhesSeCliqueForaDoPainel(evento);

      expect(component.reuniaoDetalhe).toBe(reuniao);
    });

    it('usa o telefone próprio da reunião no link do WhatsApp quando informado', () => {
      const reuniao = reuniaoFake({ whatsapp: '5511988887777' });

      expect(component.whatsappLink(reuniao)).toContain('5511988887777');
    });

    it('cai para WHATSAPP_PHONE_NUMBER no link do WhatsApp quando a reunião não tem número próprio', () => {
      const reuniao = reuniaoFake({ whatsapp: null });

      expect(component.whatsappLink(reuniao)).toContain(WHATSAPP_PHONE_NUMBER);
    });

    it('agrupa a agenda por dia da semana em ordem cronológica', () => {
      const reuniao = reuniaoFake({
        agenda: [
          { diaSemana: 'SEGUNDA', horario: '20:00' },
          { diaSemana: 'SEGUNDA', horario: '19:00' },
          { diaSemana: 'QUINTA', horario: '18:00' },
        ],
      });

      expect(component.agendaAgrupadaDaReuniao(reuniao)).toEqual([
        { diaSemana: 'SEGUNDA', horarios: ['19:00', '20:00'] },
        { diaSemana: 'QUINTA', horarios: ['18:00'] },
      ]);
    });
  });

  describe('toggle de mapa', () => {
    beforeEach(() => {
      escritorioServiceSpy = {
        listarEstrutura: vi.fn().mockReturnValue(of(apiSuccess(estruturaCompleta()))),
        listarUnidadesFederativas: vi.fn().mockReturnValue(of(apiSuccess(unidadesFederativasFake()))),
      };
    });

    it('não cria o mapa antes do primeiro clique em "Ver no mapa"', async () => {
      await criarComponente([]);
      expect(component.mapaAberto()).toBe(false);
      expect(mapasCriados).toHaveLength(0);
    });

    it('carrega maplibre-gl e cria o mapa de forma lazy no primeiro toggle', async () => {
      const reuniaoComCoords = reuniaoFake({
        id: 'presencial-coords',
        categoria: 'presencial',
        estado: null,
        latitude: -23.55,
        longitude: -46.62,
        grupoIntergrupoId: 'grupo-1',
      });
      await criarComponente([reuniaoComCoords]);

      component.toggleMapa();
      fixture.detectChanges();
      await fixture.whenStable();
      await new Promise((resolve) => setTimeout(resolve, 0));
      fixture.detectChanges();

      expect(component.mapaAberto()).toBe(true);
      expect(mapasCriados).toHaveLength(1);

      const mapa = mapasCriados[0];
      dispararLoadDoMapa(mapa);

      const fonte = mapa.getSource('reunioes') as FonteFake;
      const dados = fonte.dadosAtuais as { features: { properties: { id: string } }[] };
      expect(dados.features).toHaveLength(1);
      expect(dados.features[0].properties.id).toBe('presencial-coords');
    });

    it('recolhe o mapa ao clicar novamente, sem recriar o mapa', async () => {
      await criarComponente([]);

      component.toggleMapa();
      fixture.detectChanges();
      await fixture.whenStable();
      await new Promise((resolve) => setTimeout(resolve, 0));
      fixture.detectChanges();

      expect(mapasCriados).toHaveLength(1);

      component.toggleMapa();
      fixture.detectChanges();
      await fixture.whenStable();

      expect(component.mapaAberto()).toBe(false);
      expect(mapasCriados).toHaveLength(1);
    });
  });
});
