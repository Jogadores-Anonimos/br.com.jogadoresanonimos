import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DozePassos } from './doze-passos';

describe('DozePassos', () => {
  let component: DozePassos;
  let fixture: ComponentFixture<DozePassos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DozePassos],
    }).compileComponents();

    fixture = TestBed.createComponent(DozePassos);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
