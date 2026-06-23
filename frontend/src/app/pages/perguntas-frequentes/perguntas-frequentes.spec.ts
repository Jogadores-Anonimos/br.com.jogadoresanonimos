import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PerguntasFrequentes } from './perguntas-frequentes';

describe('PerguntasFrequentes', () => {
  let component: PerguntasFrequentes;
  let fixture: ComponentFixture<PerguntasFrequentes>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PerguntasFrequentes],
    }).compileComponents();

    fixture = TestBed.createComponent(PerguntasFrequentes);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
