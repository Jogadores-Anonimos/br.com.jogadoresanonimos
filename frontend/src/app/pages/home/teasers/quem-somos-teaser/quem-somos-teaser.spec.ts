import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuemSomosTeaser } from './quem-somos-teaser';

describe('QuemSomosTeaser', () => {
  let component: QuemSomosTeaser;
  let fixture: ComponentFixture<QuemSomosTeaser>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [QuemSomosTeaser],
    }).compileComponents();

    fixture = TestBed.createComponent(QuemSomosTeaser);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
