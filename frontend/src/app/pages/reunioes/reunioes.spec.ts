import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Reunioes } from './reunioes';

describe('Reunioes', () => {
  let component: Reunioes;
  let fixture: ComponentFixture<Reunioes>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Reunioes],
    }).compileComponents();

    fixture = TestBed.createComponent(Reunioes);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
