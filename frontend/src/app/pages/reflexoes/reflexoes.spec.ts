import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Reflexoes } from './reflexoes';

describe('Reflexoes', () => {
  let component: Reflexoes;
  let fixture: ComponentFixture<Reflexoes>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Reflexoes],
    }).compileComponents();

    fixture = TestBed.createComponent(Reflexoes);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
