import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { PaginaNaoEncontrada } from './pagina-nao-encontrada';

describe('PaginaNaoEncontrada', () => {
  let fixture: ComponentFixture<PaginaNaoEncontrada>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PaginaNaoEncontrada],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(PaginaNaoEncontrada);
    fixture.detectChanges();
  });

  it('mostra uma mensagem clara de página não encontrada', () => {
    const elemento = fixture.nativeElement as HTMLElement;

    expect(elemento.querySelector('.pagina-404__codigo')?.textContent?.trim()).toBe('404');
    expect(elemento.querySelector('h1')?.textContent?.trim()).toBe('Página não encontrada');
  });

  it('move o foco para o título ao entrar', () => {
    const titulo = fixture.nativeElement.querySelector('h1') as HTMLHeadingElement;

    expect(document.activeElement).toBe(titulo);
    expect(titulo.tabIndex).toBe(-1);
  });

  it('oferece links reais para o início e para reuniões', () => {
    const links = Array.from(fixture.nativeElement.querySelectorAll('a')) as HTMLAnchorElement[];

    expect(links).toHaveLength(2);
    expect(links[0].getAttribute('href')).toBe('/');
    expect(links[1].getAttribute('href')).toBe('/reunioes');
  });
});
