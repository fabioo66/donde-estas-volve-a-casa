import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MascotaForm } from './mascota-form';

describe('MascotaForm', () => {
  let component: MascotaForm;
  let fixture: ComponentFixture<MascotaForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MascotaForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MascotaForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
