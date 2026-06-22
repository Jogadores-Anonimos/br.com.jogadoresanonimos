import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AvisosTeaser } from './avisos-teaser';

describe('AvisosTeaser', () => {
  let component: AvisosTeaser;
  let fixture: ComponentFixture<AvisosTeaser>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AvisosTeaser],
    }).compileComponents();

    fixture = TestBed.createComponent(AvisosTeaser);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
