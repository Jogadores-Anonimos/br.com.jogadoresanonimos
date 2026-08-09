import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { Role } from '../../../services/auth/auth.model';
import { mensagemDeErro } from '../../../services/api-envelope';
import { UsuarioAdminService } from '../../../services/usuario-admin/usuario-admin';
import { UsuarioAdmin } from '../../../services/usuario-admin/usuario-admin.model';

@Component({
  selector: 'app-admin-usuarios',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-usuarios.html',
  styleUrl: './admin-usuarios.css',
})
export class AdminUsuarios implements OnInit {
  private readonly usuarioAdminService = inject(UsuarioAdminService);

  carregando = true;
  erroCarregamento = '';
  usuarios: UsuarioAdmin[] = [];

  filtroTexto = '';
  filtroRole: Role | '' = '';

  roleSelecionada: Record<string, Role> = {};
  confirmandoPromocao: string | null = null;
  codigoTotpPromocao = '';
  salvandoRole: string | null = null;
  erroPorUsuario: Record<string, string> = {};

  ngOnInit(): void {
    this.carregarUsuarios();
  }

  get usuariosFiltrados(): UsuarioAdmin[] {
    const texto = this.filtroTexto.trim().toLowerCase();
    return this.usuarios.filter((usuario) => {
      const bateTexto =
        !texto ||
        usuario.apelido.toLowerCase().includes(texto) ||
        usuario.email.toLowerCase().includes(texto);
      const bateRole = !this.filtroRole || usuario.role === this.filtroRole;
      return bateTexto && bateRole;
    });
  }

  private carregarUsuarios(): void {
    this.carregando = true;
    this.erroCarregamento = '';

    this.usuarioAdminService.listarUsuarios().subscribe((envelope) => {
      this.carregando = false;

      if (envelope.success && envelope.data) {
        this.usuarios = envelope.data;
        this.roleSelecionada = {};
        for (const usuario of this.usuarios) {
          this.roleSelecionada[usuario.id] = usuario.role;
        }
      } else {
        this.usuarios = [];
        this.erroCarregamento = envelope.error?.message ?? 'Não foi possível carregar os usuários.';
      }
    });
  }

  onRoleSelecionada(usuario: UsuarioAdmin, novaRole: Role): void {
    this.erroPorUsuario[usuario.id] = '';

    if (novaRole === usuario.role) {
      return;
    }

    if (novaRole === 'ADMIN') {
      this.confirmandoPromocao = usuario.id;
      return;
    }

    this.aplicarRole(usuario, novaRole);
  }

  confirmarPromocao(usuario: UsuarioAdmin): void {
    this.aplicarRole(usuario, 'ADMIN', this.codigoTotpPromocao.trim());
  }

  cancelarPromocao(usuario: UsuarioAdmin): void {
    this.confirmandoPromocao = null;
    this.codigoTotpPromocao = '';
    this.erroPorUsuario[usuario.id] = '';
    this.roleSelecionada[usuario.id] = usuario.role;
  }

  private aplicarRole(usuario: UsuarioAdmin, novaRole: Role, codigoTotp?: string): void {
    this.salvandoRole = usuario.id;
    this.erroPorUsuario[usuario.id] = '';

    this.usuarioAdminService.editarRole(usuario.id, { role: novaRole, ...(codigoTotp ? { codigoTotp } : {}) }).subscribe((envelope) => {
      this.salvandoRole = null;

      if (envelope.success && envelope.data) {
        const atualizado = envelope.data;
        this.usuarios = this.usuarios.map((u) => (u.id === atualizado.id ? atualizado : u));
        this.roleSelecionada[atualizado.id] = atualizado.role;
        this.confirmandoPromocao = null;
        this.codigoTotpPromocao = '';
      } else if (this.confirmandoPromocao === usuario.id) {
        this.erroPorUsuario[usuario.id] = mensagemDeErro(envelope.error, 'Não foi possível alterar a role agora.');
        this.codigoTotpPromocao = '';
      } else {
        this.erroPorUsuario[usuario.id] = mensagemDeErro(envelope.error, 'Não foi possível alterar a role agora.');
        this.roleSelecionada[usuario.id] = usuario.role;
      }
    });
  }
}
