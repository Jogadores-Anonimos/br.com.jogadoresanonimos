import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JogoCompulsivo } from './jogo-compulsivo';

describe('JogoCompulsivo', () => {
  let component: JogoCompulsivo;
  let fixture: ComponentFixture<JogoCompulsivo>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JogoCompulsivo],
    }).compileComponents();

    fixture = TestBed.createComponent(JogoCompulsivo);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
