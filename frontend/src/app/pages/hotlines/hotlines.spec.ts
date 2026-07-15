import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Subject, of, throwError } from 'rxjs';

import { Hotlines } from './hotlines';
import { HotlineService } from '../../services/hotline/hotline';
import { HotlineRegiao } from '../../services/hotline/hotline.model';
import { apiError, apiSuccess } from '../../services/api-envelope';

function regiaoFake(overrides: Partial<HotlineRegiao> = {}): HotlineRegiao {
  return {
    id: 'regiao-1',
    uf: 'SP',
    cidade: 'São Paulo',
    telefones: [{ id: 'telefone-1', label: 'Central', telefone: '(11) 4321-0000' }],
    ...overrides,
  };
}

describe('Hotlines', () => {
  let component: Hotlines;
  let fixture: ComponentFixture<Hotlines>;
  let serviceSpy: Record<string, ReturnType<typeof vi.fn>>;

  async function montar(): Promise<void> {
    await TestBed.configureTestingModule({
      imports: [Hotlines],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: HotlineService, useValue: serviceSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Hotlines);
    component = fixture.componentInstance;
  }

  describe('estado de carregamento', () => {
    it('mostra a mensagem de carregando antes do service responder', async () => {
      serviceSpy = { listarHotlines: vi.fn().mockReturnValue(new Subject()) };
      await montar();

      fixture.detectChanges();

      expect(component.carregando()).toBe(true);
      const texto = (fixture.nativeElement as HTMLElement).textContent ?? '';
      expect(texto).toContain('Carregando linhas de ajuda');
    });
  });

  describe('estado de erro', () => {
    it('mostra mensagem de erro quando o envelope retorna success: false', async () => {
      serviceSpy = {
        listarHotlines: vi.fn().mockReturnValue(of(apiError('UPSTREAM_ERROR', 'Não foi possível carregar as linhas de ajuda.'))),
      };
      await montar();

      fixture.detectChanges();
      await fixture.whenStable();
      fixture.detectChanges();

      expect(component.erro()).toBe(true);
      expect(component.carregando()).toBe(false);
      const texto = (fixture.nativeElement as HTMLElement).textContent ?? '';
      expect(texto).toContain('Não foi possível carregar as linhas de ajuda agora');
    });

    it('mostra mensagem de erro quando o Observable falha (erro de rede)', async () => {
      serviceSpy = { listarHotlines: vi.fn().mockReturnValue(throwError(() => new Error('falha de rede'))) };
      await montar();

      fixture.detectChanges();
      await fixture.whenStable();
      fixture.detectChanges();

      expect(component.erro()).toBe(true);
      expect(component.carregando()).toBe(false);
    });
  });

  describe('estado vazio', () => {
    it('mostra mensagem de lista vazia quando o service retorna sucesso sem regiões', async () => {
      serviceSpy = { listarHotlines: vi.fn().mockReturnValue(of(apiSuccess([]))) };
      await montar();

      fixture.detectChanges();
      await fixture.whenStable();
      fixture.detectChanges();

      expect(component.regioes()).toHaveLength(0);
      const texto = (fixture.nativeElement as HTMLElement).textContent ?? '';
      expect(texto).toContain('Nenhuma linha de ajuda cadastrada no momento.');
    });
  });

  describe('estado de sucesso', () => {
    const regiaoSp = regiaoFake({ id: 'regiao-sp', uf: 'SP', cidade: 'São Paulo', telefones: [{ id: 't1', label: 'Central', telefone: '(11) 4321-0000' }] });
    const regiaoRj = regiaoFake({ id: 'regiao-rj', uf: 'RJ', cidade: 'Rio de Janeiro', telefones: [] });

    beforeEach(async () => {
      serviceSpy = { listarHotlines: vi.fn().mockReturnValue(of(apiSuccess([regiaoSp, regiaoRj]))) };
      await montar();
      fixture.detectChanges();
      await fixture.whenStable();
      fixture.detectChanges();
    });

    it('exibe as regiões retornadas no acordeão', () => {
      const texto = (fixture.nativeElement as HTMLElement).textContent ?? '';
      expect(texto).toContain('São Paulo - SP');
      expect(texto).toContain('Rio de Janeiro - RJ');
    });

    it('mantém o painel fechado por padrão', () => {
      const painel = (fixture.nativeElement as HTMLElement).querySelector('#hotline-painel-0') as HTMLElement;
      expect(painel.hidden).toBe(true);
    });

    it('abre o painel da região e mostra os telefones ao clicar no cabeçalho', () => {
      const botao = (fixture.nativeElement as HTMLElement).querySelector('#hotline-cabecalho-0') as HTMLButtonElement;
      botao.click();
      fixture.detectChanges();

      const painel = (fixture.nativeElement as HTMLElement).querySelector('#hotline-painel-0') as HTMLElement;
      expect(painel.hidden).toBe(false);
      expect(painel.textContent).toContain('Central: (11) 4321-0000');
    });

    it('fecha o painel ao clicar novamente no mesmo cabeçalho', () => {
      const botao = (fixture.nativeElement as HTMLElement).querySelector('#hotline-cabecalho-0') as HTMLButtonElement;
      botao.click();
      fixture.detectChanges();
      botao.click();
      fixture.detectChanges();

      const painel = (fixture.nativeElement as HTMLElement).querySelector('#hotline-painel-0') as HTMLElement;
      expect(painel.hidden).toBe(true);
    });

    it('não exibe telefones para uma região sem nenhum cadastrado', () => {
      const botao = (fixture.nativeElement as HTMLElement).querySelector('#hotline-cabecalho-1') as HTMLButtonElement;
      botao.click();
      fixture.detectChanges();

      const painel = (fixture.nativeElement as HTMLElement).querySelector('#hotline-painel-1') as HTMLElement;
      expect(painel.querySelectorAll('li')).toHaveLength(0);
    });
  });
});
