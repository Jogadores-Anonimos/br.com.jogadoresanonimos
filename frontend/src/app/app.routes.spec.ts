import { TestBed } from '@angular/core/testing';
import { Meta, Title } from '@angular/platform-browser';
import { Router, provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { Seo } from './services/seo';

describe('rota de página não encontrada', () => {
  it('mantém o wildcard como última rota e carrega o componente sob demanda', async () => {
    const wildcard = routes.at(-1);

    expect(wildcard?.path).toBe('**');
    expect(wildcard?.loadComponent).toBeTypeOf('function');

    const componente = await wildcard?.loadComponent?.();
    expect(componente).toBeTruthy();
  });

  it('aplica título e descrição próprios ao navegar para URL inexistente', async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter(routes)],
    }).compileComponents();

    const router = TestBed.inject(Router);
    const title = TestBed.inject(Title);
    const meta = TestBed.inject(Meta);
    TestBed.inject(Seo);

    await router.navigateByUrl('/pagina-que-nao-existe');

    expect(title.getTitle()).toBe('Página não encontrada | Jogadores Anônimos do Brasil');
    expect(meta.getTag('name="description"')?.content).toContain('não foi encontrada');
    expect(meta.getTag('name="robots"')?.content).toBe('noindex, follow');
    expect(router.routerState.snapshot.root.firstChild?.routeConfig?.path).toBe('**');

    await router.navigateByUrl('/');

    expect(meta.getTag('name="robots"')?.content).toBe('index, follow');
  });
});
