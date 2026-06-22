import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Autoaval } from './autoaval';

describe('Autoaval', () => {
  let component: Autoaval;
  let fixture: ComponentFixture<Autoaval>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Autoaval],
    }).compileComponents();

    fixture = TestBed.createComponent(Autoaval);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
