import { PLATFORM_ID } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { Reunioes } from './reunioes';
import { ReuniaoService } from '../../services/reuniao/reuniao';
import { Reuniao } from '../../services/reuniao/reuniao.model';
import { apiSuccess } from '../../services/api-envelope';

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
const boundsExtendidos: unknown[][] = [];

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
  mapa.addSource = vi.fn((id: string) => {
    mapa.fontes![id] = criarFonteFake();
  });
  mapa.addLayer = vi.fn();
  mapa.getSource = vi.fn((id: string) => mapa.fontes![id]);
  return mapa as MapaFake;
}

class LngLatBoundsFake {
  pontos: unknown[] = [];
  extend(ponto: unknown): void {
    this.pontos.push(ponto);
    boundsExtendidos.push(this.pontos);
  }
}

class ResizeObserverFake {
  observe(): void {}
  unobserve(): void {}
  disconnect(): void {}
}

vi.mock('maplibre-gl', () => {
  return {
    Map: vi.fn(() => {
      const mapa = criarMapaFake();
      mapasCriados.push(mapa);
      return mapa;
    }),
    LngLatBounds: LngLatBoundsFake,
    NavigationControl: vi.fn(),
    Popup: vi.fn(() => ({
      setLngLat: vi.fn().mockReturnThis(),
      setHTML: vi.fn().mockReturnThis(),
      addTo: vi.fn().mockReturnThis(),
    })),
    setWorkerUrl: vi.fn(),
  };
});

function reuniaoFake(overrides: Partial<Reuniao> = {}): Reuniao {
  return {
    id: 'reuniao-1',
    nome: 'Grupo Esperança',
    categoria: 'presencial',
    descricao: 'Reunião de apoio mútuo.',
    agenda: [{ diaSemana: 'SEGUNDA', horario: '19:00' }],
    dia: 'Segunda',
    horario: '19:00',
    local: 'Rua Fictícia, 100, Bairro Teste, Cidade Teste, TS',
    whatsapp: null,
    diaSemana: 'Segunda',
    periodo: 'Noite',
    latitude: -23.55,
    longitude: -46.62,
    cep: null,
    numero: null,
    logradouro: null,
    bairro: 'Bairro Teste',
    cidade: 'Cidade Teste',
    estado: 'TS',
    linkReuniao: null,
    avisos: [],
    ...overrides,
  };
}

function dispararLoadDoMapa(mapa: MapaFake): void {
  mapa.handlers['load']?.();
}

describe('Reunioes', () => {
  let component: Reunioes;
  let fixture: ComponentFixture<Reunioes>;
  let reuniaoServiceSpy: Record<string, ReturnType<typeof vi.fn>>;

  beforeAll(() => {
    if (typeof globalThis.ResizeObserver === 'undefined') {
      globalThis.ResizeObserver = ResizeObserverFake as unknown as typeof ResizeObserver;
    }
  });

  async function configurarTeste(reunioesIniciais: Reuniao[]): Promise<void> {
    mapasCriados.length = 0;
    boundsExtendidos.length = 0;
    reuniaoServiceSpy = {
      listarReunioes: vi.fn().mockReturnValue(of(apiSuccess(reunioesIniciais))),
    };

    TestBed.resetTestingModule();

    await TestBed.configureTestingModule({
      imports: [Reunioes],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: PLATFORM_ID, useValue: 'browser' },
        { provide: ReuniaoService, useValue: reuniaoServiceSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Reunioes);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();
  }

  async function selecionarPresencial(): Promise<void> {
    component.selecionarCategoria('presencial');
    fixture.detectChanges();
    await fixture.whenStable();
    await new Promise((resolve) => setTimeout(resolve, 0));
    fixture.detectChanges();
    await fixture.whenStable();
  }

  describe('mapa de reuniões presenciais', () => {
    it('renderiza sem erro e cria a fonte GeoJSON com feature para reunião presencial com latitude e longitude', async () => {
      const comCoordenadas = reuniaoFake({
        id: 'reuniao-com-coords',
        categoria: 'presencial',
        latitude: -23.55,
        longitude: -46.62,
      });

      await configurarTeste([comCoordenadas]);

      await expect(selecionarPresencial()).resolves.not.toThrow();

      expect(mapasCriados).toHaveLength(1);
      const mapa = mapasCriados[0];
      dispararLoadDoMapa(mapa);

      const fonte = mapa.getSource('reunioes') as FonteFake;
      const dados = fonte.dadosAtuais as { features: { geometry: { coordinates: number[] } }[] };
      expect(dados.features).toHaveLength(1);
      expect(dados.features[0].geometry.coordinates).toEqual([-46.62, -23.55]);
    });

    it('não inclui reunião presencial sem latitude e longitude na coleção do mapa', async () => {
      const semCoordenadas = reuniaoFake({
        id: 'reuniao-sem-coords',
        categoria: 'presencial',
        latitude: null,
        longitude: null,
      });

      await configurarTeste([semCoordenadas]);
      await selecionarPresencial();

      const mapa = mapasCriados[0];
      dispararLoadDoMapa(mapa);

      const fonte = mapa.getSource('reunioes') as FonteFake;
      const dados = fonte.dadosAtuais as { features: unknown[] };
      expect(dados.features).toHaveLength(0);
    });

    it('não inclui reunião virtual mesmo com latitude e longitude preenchidas (dado legado)', async () => {
      const virtualComCoordsLegado = reuniaoFake({
        id: 'reuniao-virtual-legado',
        categoria: 'virtual',
        diaSemana: 'Quarta',
        local: 'Link enviado por e-mail',
        latitude: -23.55,
        longitude: -46.62,
      });
      const presencialComCoords = reuniaoFake({
        id: 'reuniao-presencial-com-coords',
        categoria: 'presencial',
        latitude: -22.9,
        longitude: -43.2,
      });

      await configurarTeste([virtualComCoordsLegado, presencialComCoords]);
      await selecionarPresencial();

      const mapa = mapasCriados[0];
      dispararLoadDoMapa(mapa);

      const fonte = mapa.getSource('reunioes') as FonteFake;
      const dados = fonte.dadosAtuais as { features: { properties: { id: string } }[] };
      expect(dados.features).toHaveLength(1);
      expect(dados.features[0].properties.id).toBe('reuniao-presencial-com-coords');
      expect(component.reunioesFiltradas().every((r) => r.categoria === 'presencial')).toBe(true);
    });

    it('atualiza a fonte GeoJSON quando a lista de reuniões muda', async () => {
      const reuniaoRemovivel = reuniaoFake({
        id: 'reuniao-removivel',
        categoria: 'presencial',
        latitude: -23.55,
        longitude: -46.62,
      });

      await configurarTeste([reuniaoRemovivel]);
      await selecionarPresencial();

      const mapa = mapasCriados[0];
      dispararLoadDoMapa(mapa);
      const fonte = mapa.getSource('reunioes') as FonteFake;
      expect((fonte.dadosAtuais as { features: unknown[] }).features).toHaveLength(1);

      component.reunioes.set([]);
      fixture.detectChanges();
      await fixture.whenStable();

      expect((fonte.dadosAtuais as { features: unknown[] }).features).toHaveLength(0);
    });
  });

  describe('agenda múltipla', () => {
    it('agrupa uma reunião virtual em cada dia da sua agenda', async () => {
      const virtual = reuniaoFake({
        categoria: 'virtual',
        agenda: [
          { diaSemana: 'SEGUNDA', horario: '19:00' },
          { diaSemana: 'QUINTA', horario: '20:30' },
        ],
        latitude: null,
        longitude: null,
      });
      await configurarTeste([virtual]);

      component.selecionarCategoria('virtual');

      expect(component.reunioesVirtuaisAgrupadas()).toEqual([
        { dia: 'Segunda', itens: [{ reuniao: virtual, horarios: ['19:00'] }] },
        { dia: 'Quinta', itens: [{ reuniao: virtual, horarios: ['20:30'] }] },
      ]);
    });

    it('mostra uma única reunião por dia com formatos mistos em ordem cronológica', async () => {
      const virtual = reuniaoFake({
        categoria: 'virtual',
        agenda: [
          { diaSemana: 'SEGUNDA', horario: '19:30h' },
          { diaSemana: 'SEGUNDA', horario: '9h' },
          { diaSemana: 'SEGUNDA', horario: '14h às 16h' },
          { diaSemana: 'SEGUNDA', horario: 'a confirmar' },
        ],
        latitude: null,
        longitude: null,
      });
      await configurarTeste([virtual]);

      component.selecionarCategoria('virtual');

      expect(component.reunioesVirtuaisAgrupadas()).toEqual([
        {
          dia: 'Segunda',
          itens: [{ reuniao: virtual, horarios: ['9h', '14h às 16h', '19:30h', 'a confirmar'] }],
        },
      ]);
      expect(component.resumoAgenda(virtual)).toBe('Segunda 9h, 14h às 16h, 19:30h, a confirmar');
      expect(component.agendaAgrupadaDaReuniao(virtual)).toEqual([
        { diaSemana: 'SEGUNDA', horarios: ['9h', '14h às 16h', '19:30h', 'a confirmar'] },
      ]);
    });

    it('considera qualquer dia ou horário da agenda na busca', async () => {
      await configurarTeste([
        reuniaoFake({
          categoria: 'virtual',
          agenda: [
            { diaSemana: 'SEGUNDA', horario: '19:00' },
            { diaSemana: 'SABADO', horario: '08:30' },
          ],
        }),
      ]);
      component.selecionarCategoria('virtual');
      (component as unknown as { filtroTextoDebounced: { set: (valor: string) => void } }).filtroTextoDebounced.set('sábado');

      expect(component.reunioesFiltradas()).toHaveLength(1);
    });

    it('gera resumo e detalhes com a agenda completa em ordem', async () => {
      const reuniao = reuniaoFake({
        agenda: [
          { diaSemana: 'SEGUNDA', horario: '19:00' },
          { diaSemana: 'QUINTA', horario: '20:30' },
        ],
      });
      await configurarTeste([reuniao]);

      expect(component.resumoAgenda(reuniao)).toBe('Segunda 19:00 · Quinta 20:30');
      expect(component.agendaDaReuniao(reuniao)).toHaveLength(2);
    });

    it('agrupa dado legado sem agenda usando dia e horário antigos', async () => {
      const virtualLegada = reuniaoFake({
        categoria: 'virtual',
        agenda: [],
        dia: 'Terça-feira',
        diaSemana: null,
        horario: '18:30',
        latitude: null,
        longitude: null,
      });
      await configurarTeste([virtualLegada]);

      component.selecionarCategoria('virtual');

      expect(component.reunioesVirtuaisAgrupadas()).toEqual([
        { dia: 'Terça', itens: [{ reuniao: virtualLegada, horarios: ['18:30'] }] },
      ]);
    });
  });

  describe('centralizarMapa', () => {
    it('com localização do usuário e reuniões filtradas não vazias, usa fitBounds só com as coordenadas das reuniões', async () => {
      const reuniao1 = reuniaoFake({
        id: 'reuniao-1',
        categoria: 'presencial',
        latitude: -23.55,
        longitude: -46.62,
      });
      const reuniao2 = reuniaoFake({
        id: 'reuniao-2',
        categoria: 'presencial',
        latitude: -22.9,
        longitude: -43.2,
      });

      await configurarTeste([reuniao1, reuniao2]);
      await selecionarPresencial();

      const mapa = mapasCriados[0];
      dispararLoadDoMapa(mapa);
      mapa.fitBounds.mockClear();
      mapa.setCenter.mockClear();
      mapa.setZoom.mockClear();
      boundsExtendidos.length = 0;

      (component as unknown as { userCoords: { set: (v: unknown) => void } }).userCoords.set({
        latitude: -10,
        longitude: -50,
      });
      (component as unknown as { centralizarMapa: () => void }).centralizarMapa();

      expect(mapa.fitBounds).toHaveBeenCalledTimes(1);
      expect(mapa.setCenter).not.toHaveBeenCalled();
      expect(mapa.setZoom).not.toHaveBeenCalled();

      const pontosUsados = boundsExtendidos[boundsExtendidos.length - 1] as number[][];
      expect(pontosUsados).toEqual([
        [-46.62, -23.55],
        [-43.2, -22.9],
      ]);
      expect(pontosUsados).not.toContainEqual([-50, -10]);
    });

    it('sem reuniões filtradas e com localização do usuário disponível, centraliza em userCoords', async () => {
      await configurarTeste([]);
      await selecionarPresencial();

      const mapa = mapasCriados[0];
      dispararLoadDoMapa(mapa);
      mapa.fitBounds.mockClear();
      mapa.setCenter.mockClear();
      mapa.setZoom.mockClear();

      (component as unknown as { userCoords: { set: (v: unknown) => void } }).userCoords.set({
        latitude: -10,
        longitude: -50,
      });
      (component as unknown as { centralizarMapa: () => void }).centralizarMapa();

      expect(mapa.setCenter).toHaveBeenCalledWith([-50, -10]);
      expect(mapa.setZoom).toHaveBeenCalled();
      expect(mapa.fitBounds).not.toHaveBeenCalled();
    });

    it('sem reuniões filtradas e sem localização do usuário, centraliza no CENTRO_PADRAO', async () => {
      await configurarTeste([]);
      await selecionarPresencial();

      const mapa = mapasCriados[0];
      dispararLoadDoMapa(mapa);
      mapa.fitBounds.mockClear();
      mapa.setCenter.mockClear();
      mapa.setZoom.mockClear();

      (component as unknown as { centralizarMapa: () => void }).centralizarMapa();

      expect(mapa.setCenter).toHaveBeenCalledWith([-46.62, -23.55]);
      expect(mapa.setZoom).toHaveBeenCalled();
      expect(mapa.fitBounds).not.toHaveBeenCalled();
    });
  });
});
