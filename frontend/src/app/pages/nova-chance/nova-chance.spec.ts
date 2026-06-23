import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NovaChance } from './nova-chance';

describe('NovaChance', () => {
  let component: NovaChance;
  let fixture: ComponentFixture<NovaChance>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NovaChance],
    }).compileComponents();

    fixture = TestBed.createComponent(NovaChance);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
