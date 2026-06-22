import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReflexoesTeaser } from './reflexoes-teaser';

describe('ReflexoesTeaser', () => {
  let component: ReflexoesTeaser;
  let fixture: ComponentFixture<ReflexoesTeaser>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReflexoesTeaser],
    }).compileComponents();

    fixture = TestBed.createComponent(ReflexoesTeaser);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
