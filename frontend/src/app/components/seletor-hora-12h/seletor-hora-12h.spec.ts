import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SeletorHora12h } from './seletor-hora-12h';

describe('SeletorHora12h', () => {
  let component: SeletorHora12h;
  let fixture: ComponentFixture<SeletorHora12h>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SeletorHora12h],
    }).compileComponents();

    fixture = TestBed.createComponent(SeletorHora12h);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve criar', () => {
    expect(component).toBeTruthy();
  });

  it('converte "HH:mm" 24h para hora 12h e período ao escrever o valor', () => {
    component.writeValue('19:30');

    expect(component.hora).toBe(7);
    expect(component.minuto).toBe(30);
    expect(component.periodo).toBe('PM');
  });

  it('converte meia-noite (00:00) para 12 AM', () => {
    component.writeValue('00:00');

    expect(component.hora).toBe(12);
    expect(component.periodo).toBe('AM');
  });

  it('converte meio-dia (12:00) para 12 PM', () => {
    component.writeValue('12:00');

    expect(component.hora).toBe(12);
    expect(component.periodo).toBe('PM');
  });

  it('ignora valor inválido e mantém estado atual', () => {
    component.writeValue('25:99');

    expect(component.hora).toBe(7);
    expect(component.periodo).toBe('PM');
  });

  it('emite "HH:mm" 24h ao atualizar com hora 12h e período PM', () => {
    let valorEmitido = '';
    component.registerOnChange((valor) => (valorEmitido = valor));
    component.hora = 8;
    component.minuto = 15;
    component.periodo = 'PM';

    component.atualizar();

    expect(valorEmitido).toBe('20:15');
  });

  it('emite "HH:mm" 24h ao atualizar com hora 12h e período AM', () => {
    let valorEmitido = '';
    component.registerOnChange((valor) => (valorEmitido = valor));
    component.hora = 8;
    component.minuto = 15;
    component.periodo = 'AM';

    component.atualizar();

    expect(valorEmitido).toBe('08:15');
  });

  it('converte 12 AM para "00:mm" e 12 PM para "12:mm"', () => {
    let valorEmitido = '';
    component.registerOnChange((valor) => (valorEmitido = valor));

    component.hora = 12;
    component.minuto = 0;
    component.periodo = 'AM';
    component.atualizar();
    expect(valorEmitido).toBe('00:00');

    component.hora = 12;
    component.minuto = 0;
    component.periodo = 'PM';
    component.atualizar();
    expect(valorEmitido).toBe('12:00');
  });

  it('selecionarPeriodo troca o período e reemite o valor', () => {
    let valorEmitido = '';
    component.registerOnChange((valor) => (valorEmitido = valor));
    component.hora = 7;
    component.minuto = 0;
    component.periodo = 'AM';

    component.selecionarPeriodo('PM');

    expect(component.periodo).toBe('PM');
    expect(valorEmitido).toBe('19:00');
  });

  it('selecionarPeriodo não emite quando desabilitado', () => {
    let chamado = false;
    component.registerOnChange(() => (chamado = true));
    component.setDisabledState(true);

    component.selecionarPeriodo('PM');

    expect(chamado).toBe(false);
  });
});
