import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContatoTeaser } from './contato-teaser';

describe('ContatoTeaser', () => {
  let component: ContatoTeaser;
  let fixture: ComponentFixture<ContatoTeaser>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContatoTeaser],
    }).compileComponents();

    fixture = TestBed.createComponent(ContatoTeaser);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
