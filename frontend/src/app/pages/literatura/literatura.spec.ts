import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Literatura } from './literatura';

describe('Literatura', () => {
  let component: Literatura;
  let fixture: ComponentFixture<Literatura>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Literatura],
    }).compileComponents();

    fixture = TestBed.createComponent(Literatura);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
