import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Contribuir } from './contribuir';

describe('Contribuir', () => {
  let component: Contribuir;
  let fixture: ComponentFixture<Contribuir>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Contribuir],
    }).compileComponents();

    fixture = TestBed.createComponent(Contribuir);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
