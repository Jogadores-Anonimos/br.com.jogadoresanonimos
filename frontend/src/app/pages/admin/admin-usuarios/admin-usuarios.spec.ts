import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { AdminUsuarios } from './admin-usuarios';
import { UsuarioAdminService } from '../../../services/usuario-admin/usuario-admin';
import { UsuarioAdmin } from '../../../services/usuario-admin/usuario-admin.model';
import { apiSuccess } from '../../../services/api-envelope';

function usuarioFake(overrides: Partial<UsuarioAdmin> = {}): UsuarioAdmin {
  return {
    id: 'usuario-1',
    apelido: 'viajante-teste',
    email: 'viajante-teste@exemplo.com',
    role: 'PARTICIPANTE',
    criadoEm: '2026-01-01T00:00:00Z',
    ...overrides,
  };
}

describe('AdminUsuarios', () => {
  let component: AdminUsuarios;
  let fixture: ComponentFixture<AdminUsuarios>;
  let usuarioAdminServiceSpy: Record<string, ReturnType<typeof vi.fn>>;

  const usuario1 = usuarioFake({ id: 'usuario-1', apelido: 'viajante-teste', role: 'PARTICIPANTE' });
  const usuario2 = usuarioFake({ id: 'usuario-2', apelido: 'estrela-teste', role: 'ADMIN' });
  const usuario3 = usuarioFake({ id: 'usuario-3', apelido: 'farol-teste', role: 'PARTICIPANTE' });

  async function configurarTeste(usuariosIniciais: UsuarioAdmin[] = [usuario1, usuario2, usuario3]): Promise<void> {
    usuarioAdminServiceSpy = {
      listarUsuarios: vi.fn().mockReturnValue(of(apiSuccess(usuariosIniciais))),
      editarRole: vi.fn().mockReturnValue(of(apiSuccess(usuarioFake()))),
    };

    TestBed.resetTestingModule();

    await TestBed.configureTestingModule({
      imports: [AdminUsuarios],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: UsuarioAdminService, useValue: usuarioAdminServiceSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminUsuarios);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();
  }

  beforeEach(async () => {
    await configurarTeste();
  });

  it('deve criar', () => {
    expect(component).toBeTruthy();
  });

  describe('contador de total de usuários', () => {
    function textoTotal(): string {
      const elemento = fixture.nativeElement.querySelector('.admin-usuarios__total');
      return elemento?.textContent?.trim() ?? '';
    }

    async function digitarFiltroTexto(valor: string): Promise<void> {
      const input: HTMLInputElement = fixture.nativeElement.querySelector('#usuarios-filtro-texto');
      input.value = valor;
      input.dispatchEvent(new Event('input'));
      fixture.detectChanges();
      await fixture.whenStable();
      fixture.detectChanges();
    }

    async function selecionarFiltroRole(valor: string): Promise<void> {
      const select: HTMLSelectElement = fixture.nativeElement.querySelector('#usuarios-filtro-role');
      select.value = valor;
      select.dispatchEvent(new Event('change'));
      fixture.detectChanges();
      await fixture.whenStable();
      fixture.detectChanges();
    }

    it('exibe o total geral de usuários, não o total filtrado', () => {
      expect(textoTotal()).toBe('Total: 3 usuários');
    });

    it('não muda quando um filtro de texto reduz a lista exibida', async () => {
      await digitarFiltroTexto('estrela-teste');

      expect(component.usuariosFiltrados).toHaveLength(1);
      expect(component.usuarios).toHaveLength(3);
      expect(textoTotal()).toBe('Total: 3 usuários');
    });

    it('não muda quando um filtro de role reduz a lista exibida', async () => {
      await selecionarFiltroRole('ADMIN');

      expect(component.usuariosFiltrados).toHaveLength(1);
      expect(component.usuarios).toHaveLength(3);
      expect(textoTotal()).toBe('Total: 3 usuários');
    });

    it('não muda quando o filtro combinado não encontra nenhum usuário', async () => {
      await digitarFiltroTexto('nao-existe');
      await selecionarFiltroRole('ADMIN');

      expect(component.usuariosFiltrados).toHaveLength(0);
      expect(textoTotal()).toBe('Total: 3 usuários');
    });
  });
});
