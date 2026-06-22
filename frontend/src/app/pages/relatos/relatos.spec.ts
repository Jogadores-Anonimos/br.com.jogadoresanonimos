import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Relatos } from './relatos';

describe('Relatos', () => {
  let component: Relatos;
  let fixture: ComponentFixture<Relatos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Relatos],
    }).compileComponents();

    fixture = TestBed.createComponent(Relatos);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
