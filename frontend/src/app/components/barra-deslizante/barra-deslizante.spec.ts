import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BarraDeslizante } from './barra-deslizante';

describe('BarraDeslizante', () => {
  let component: BarraDeslizante;
  let fixture: ComponentFixture<BarraDeslizante>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BarraDeslizante],
    }).compileComponents();

    fixture = TestBed.createComponent(BarraDeslizante);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
