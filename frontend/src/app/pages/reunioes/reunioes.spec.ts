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

interface MarcadorFake {
  latLng: [number, number];
  opcoes: Record<string, unknown>;
  removido: boolean;
  handlers: Record<string, () => void>;
  on: ReturnType<typeof vi.fn>;
  addTo: ReturnType<typeof vi.fn>;
  remove: ReturnType<typeof vi.fn>;
  getLatLng: ReturnType<typeof vi.fn>;
  bindTooltip: ReturnType<typeof vi.fn>;
  openTooltip: ReturnType<typeof vi.fn>;
}

function criarMarcadorFake(latLng: [number, number], opcoes: Record<string, unknown>): MarcadorFake {
  const marcador: Partial<MarcadorFake> = {
    latLng,
    opcoes,
    removido: false,
    handlers: {},
  };
  marcador.on = vi.fn((evento: string, handler: () => void) => {
    marcador.handlers![evento] = handler;
    return marcador;
  });
  marcador.addTo = vi.fn(() => marcador);
  marcador.remove = vi.fn(() => {
    marcador.removido = true;
    return marcador;
  });
  marcador.getLatLng = vi.fn(() => latLng);
  marcador.bindTooltip = vi.fn(() => marcador);
  marcador.openTooltip = vi.fn(() => marcador);
  return marcador as MarcadorFake;
}

const marcadoresCriados: MarcadorFake[] = [];

class ResizeObserverFake {
  observe(): void {}
  unobserve(): void {}
  disconnect(): void {}
}

vi.mock('leaflet', () => {
  const mapaFake = {
    setView: vi.fn(),
    fitBounds: vi.fn(),
    remove: vi.fn(),
    invalidateSize: vi.fn(),
  };
  return {
    default: {
      map: vi.fn(() => mapaFake),
      tileLayer: vi.fn(() => ({ addTo: vi.fn() })),
      divIcon: vi.fn((opcoes: Record<string, unknown>) => opcoes),
      latLngBounds: vi.fn((pontos: unknown) => pontos),
      marker: vi.fn((latLng: [number, number], opcoes: Record<string, unknown>) => {
        const marcador = criarMarcadorFake(latLng, opcoes);
        marcadoresCriados.push(marcador);
        return marcador;
      }),
    },
  };
});

function reuniaoFake(overrides: Partial<Reuniao> = {}): Reuniao {
  return {
    id: 'reuniao-1',
    nome: 'Grupo Esperança',
    categoria: 'presencial',
    descricao: 'Reunião de apoio mútuo.',
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
    ...overrides,
  };
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
    marcadoresCriados.length = 0;
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
    it('renderiza sem erro e cria marcador para reunião presencial com latitude e longitude', async () => {
      const comCoordenadas = reuniaoFake({
        id: 'reuniao-com-coords',
        categoria: 'presencial',
        latitude: -23.55,
        longitude: -46.62,
      });

      await configurarTeste([comCoordenadas]);

      await expect(selecionarPresencial()).resolves.not.toThrow();

      expect(marcadoresCriados).toHaveLength(1);
      expect(marcadoresCriados[0].latLng).toEqual([-23.55, -46.62]);
    });

    it('não cria marcador para reunião presencial sem latitude e longitude', async () => {
      const semCoordenadas = reuniaoFake({
        id: 'reuniao-sem-coords',
        categoria: 'presencial',
        latitude: null,
        longitude: null,
      });

      await configurarTeste([semCoordenadas]);

      await selecionarPresencial();

      expect(marcadoresCriados).toHaveLength(0);
    });

    it('não cria marcador para reunião virtual mesmo com latitude e longitude preenchidas (dado legado)', async () => {
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

      expect(marcadoresCriados).toHaveLength(1);
      expect(marcadoresCriados[0].latLng).toEqual([-22.9, -43.2]);
      expect(component.reunioesFiltradas().every((r) => r.categoria === 'presencial')).toBe(true);
    });

    it('remove o marcador do mapa quando a reunião é excluída da lista', async () => {
      const reuniaoRemovivel = reuniaoFake({
        id: 'reuniao-removivel',
        categoria: 'presencial',
        latitude: -23.55,
        longitude: -46.62,
      });

      await configurarTeste([reuniaoRemovivel]);

      await selecionarPresencial();
      expect(marcadoresCriados).toHaveLength(1);
      const marcadorOriginal = marcadoresCriados[0];

      component.reunioes.set([]);
      component.atualizarMarcadores();
      await fixture.whenStable();

      expect(marcadorOriginal.remove).toHaveBeenCalled();
      expect(marcadoresCriados).toHaveLength(1);
    });
  });
});
