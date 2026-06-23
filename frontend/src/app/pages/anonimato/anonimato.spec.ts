import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Anonimato } from './anonimato';

describe('Anonimato', () => {
  let component: Anonimato;
  let fixture: ComponentFixture<Anonimato>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Anonimato],
    }).compileComponents();

    fixture = TestBed.createComponent(Anonimato);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
