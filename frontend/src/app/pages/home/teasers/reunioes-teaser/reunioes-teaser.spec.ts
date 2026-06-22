import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReunioesTeaser } from './reunioes-teaser';

describe('ReunioesTeaser', () => {
  let component: ReunioesTeaser;
  let fixture: ComponentFixture<ReunioesTeaser>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReunioesTeaser],
    }).compileComponents();

    fixture = TestBed.createComponent(ReunioesTeaser);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
