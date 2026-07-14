import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, delay, tap } from 'rxjs/operators';

import { environment } from '../../environments/environment';
import { ApiEnvelope, apiError, apiSuccess } from './api-envelope';
import {
  AlterarSenhaRequest,
  AtualizarContaRequest,
  EsqueciSenhaRequest,
  LoginRequest,
  LoginResponse,
  MensagemResponse,
  RedefinirSenhaRequest,
  RegistroRequest,
  UsuarioMe,
  UsuarioResponse,
  UsuarioResumo,
} from './auth.model';
import { USUARIOS_MOCK, encontrarUsuarioMockPorIdentificador, gerarTokenMock, paraResumo } from './auth.mock';

const APELIDO_MIN = 3;
const APELIDO_MAX = 30;
const SENHA_MIN = 10;

@Injectable({ providedIn: 'root' })
export class Auth {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/auth`;

  private readonly usuarioAtualSignal = signal<UsuarioResumo | null>(null);
  readonly usuarioAtual = this.usuarioAtualSignal.asReadonly();

  // Token só em memória (sem localStorage/sessionStorage) — mesma decisão
  // de não persistir sessão já assumida pelo `adminGuard`. Reduz superfície
  // de XSS (nada de sensível fica acessível via storage do browser) às
  // custas de perder sessão no F5; trade-off aceito dado o requisito de
  // anonimato do projeto. Ver `.claude/knowledge/security.md`.
  private tokenAtual: string | null = null;

  obterToken(): string | null {
    return this.tokenAtual;
  }

  registrar(req: RegistroRequest): Observable<ApiEnvelope<UsuarioResponse>> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<UsuarioResponse>>(`${this.baseUrl}/registro`, req).pipe(
        catchError(() => of(apiError<UsuarioResponse>('UPSTREAM_ERROR', 'Não foi possível concluir o cadastro. Tente novamente.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    const validacao = this.validarRegistro(req);
    if (validacao) {
      return of(validacao).pipe(delay(400));
    }

    const jaExiste = encontrarUsuarioMockPorIdentificador(req.apelido) || encontrarUsuarioMockPorIdentificador(req.email);
    if (jaExiste) {
      return of(apiError<UsuarioResponse>('CONFLICT', 'Não foi possível concluir o cadastro com os dados informados')).pipe(delay(400));
    }

    const resposta: UsuarioResponse = {
      id: `mock-${crypto.randomUUID()}`,
      apelido: req.apelido,
      role: 'PARTICIPANTE',
      criadoEm: new Date().toISOString(),
    };
    return of(apiSuccess(resposta)).pipe(delay(500));
  }

  login(req: LoginRequest): Observable<ApiEnvelope<LoginResponse>> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<LoginResponse>>(`${this.baseUrl}/login`, req).pipe(
        tap((resposta) => this.aplicarLoginBemSucedido(resposta)),
        catchError(() => of(apiError<LoginResponse>('UPSTREAM_ERROR', 'Não foi possível entrar. Tente novamente.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    if (!req.identificador?.trim() || !req.senha) {
      return of(apiError<LoginResponse>('VALIDATION_ERROR', 'Preencha apelido/e-mail e senha.')).pipe(delay(300));
    }

    const usuario = encontrarUsuarioMockPorIdentificador(req.identificador);
    if (!usuario || usuario.senha !== req.senha) {
      return of(apiError<LoginResponse>('UNAUTHENTICATED', 'Apelido/e-mail ou senha inválidos.')).pipe(delay(400));
    }

    const resposta: LoginResponse = {
      token: gerarTokenMock(usuario.id),
      expiraEm: new Date(Date.now() + 60 * 60 * 1000).toISOString(),
      usuario: paraResumo(usuario),
    };
    return of(apiSuccess(resposta)).pipe(
      delay(500),
      tap((envelope) => this.aplicarLoginBemSucedido(envelope)),
    );
  }

  esqueciSenha(req: EsqueciSenhaRequest): Observable<ApiEnvelope<MensagemResponse>> {
    const mensagemGenerica: MensagemResponse = {
      mensagem: 'Se o apelido ou e-mail existir em nossa base, enviaremos um link de redefinição.',
    };

    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<MensagemResponse>>(`${this.baseUrl}/esqueci-senha`, req).pipe(
        catchError(() => of(apiSuccess(mensagemGenerica))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    // resposta é sempre genérica, exista ou não a conta — anti-enumeration (ver contrato 1.3)
    if (!req.identificador?.trim()) {
      return of(apiError<MensagemResponse>('VALIDATION_ERROR', 'Informe seu apelido ou e-mail.')).pipe(delay(300));
    }
    return of(apiSuccess(mensagemGenerica)).pipe(delay(500));
  }

  redefinirSenha(req: RedefinirSenhaRequest): Observable<ApiEnvelope<MensagemResponse>> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<MensagemResponse>>(`${this.baseUrl}/redefinir-senha`, req).pipe(
        catchError(() => of(apiError<MensagemResponse>('UPSTREAM_ERROR', 'Não foi possível redefinir sua senha. Tente novamente.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    if (req.novaSenha.length < SENHA_MIN) {
      return of(apiError<MensagemResponse>('VALIDATION_ERROR', `A nova senha deve ter no mínimo ${SENHA_MIN} caracteres.`)).pipe(delay(300));
    }
    if (req.novaSenha !== req.confirmarNovaSenha) {
      return of(apiError<MensagemResponse>('VALIDATION_ERROR', 'As senhas informadas não coincidem.')).pipe(delay(300));
    }
    if (!req.token) {
      return of(apiError<MensagemResponse>('UNAUTHENTICATED', 'Link inválido ou expirado.')).pipe(delay(300));
    }

    return of(apiSuccess<MensagemResponse>({ mensagem: 'Senha redefinida com sucesso.' })).pipe(delay(500));
  }

  buscarUsuarioLogado(): Observable<ApiEnvelope<UsuarioMe>> {
    if (!environment.useMocks) {
      return this.http.get<ApiEnvelope<UsuarioMe>>(`${environment.apiBaseUrl}/usuarios/me`).pipe(
        catchError(() => of(apiError<UsuarioMe>('UNAUTHENTICATED', 'Sessão expirada. Faça login novamente.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    const atual = this.usuarioAtualSignal();
    if (!atual) {
      return of(apiError<UsuarioMe>('UNAUTHENTICATED', 'Sessão expirada. Faça login novamente.')).pipe(delay(300));
    }

    const usuario = encontrarUsuarioMockPorIdentificador(atual.apelido);
    if (!usuario) {
      return of(apiError<UsuarioMe>('UNAUTHENTICATED', 'Sessão expirada. Faça login novamente.')).pipe(delay(300));
    }

    const { senha: _senha, ...usuarioMe } = usuario;
    return of(apiSuccess(usuarioMe)).pipe(delay(400));
  }

  atualizarConta(req: AtualizarContaRequest): Observable<ApiEnvelope<UsuarioMe>> {
    if (!environment.useMocks) {
      return this.http.put<ApiEnvelope<UsuarioMe>>(`${environment.apiBaseUrl}/usuarios/me`, req).pipe(
        tap((envelope) => this.aplicarAtualizacaoContaBemSucedida(envelope)),
        catchError(() => of(apiError<UsuarioMe>('UPSTREAM_ERROR', 'Não foi possível salvar os dados informados. Tente novamente.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    const validacao = this.validarAtualizarConta(req);
    if (validacao) {
      return of(validacao).pipe(delay(300));
    }

    const atual = this.usuarioAtualSignal();
    if (!atual) {
      return of(apiError<UsuarioMe>('UNAUTHENTICATED', 'Sessão expirada. Faça login novamente.')).pipe(delay(300));
    }

    const conflito = encontrarUsuarioMockPorIdentificador(req.apelido) ?? encontrarUsuarioMockPorIdentificador(req.email);
    if (conflito && conflito.id !== atual.id) {
      return of(apiError<UsuarioMe>('CONFLICT', 'Não foi possível salvar os dados informados')).pipe(delay(400));
    }

    const usuarioMock = USUARIOS_MOCK.find((u) => u.id === atual.id);
    if (usuarioMock) {
      usuarioMock.apelido = req.apelido;
      usuarioMock.email = req.email;
    }

    const resposta: UsuarioMe = {
      id: atual.id,
      apelido: req.apelido,
      email: req.email,
      role: atual.role,
      criadoEm: usuarioMock?.criadoEm ?? new Date().toISOString(),
    };
    return of(apiSuccess(resposta)).pipe(
      delay(400),
      tap((envelope) => this.aplicarAtualizacaoContaBemSucedida(envelope)),
    );
  }

  alterarSenha(req: AlterarSenhaRequest): Observable<ApiEnvelope<MensagemResponse>> {
    if (!environment.useMocks) {
      return this.http.put<ApiEnvelope<MensagemResponse>>(`${environment.apiBaseUrl}/usuarios/me/senha`, req).pipe(
        catchError(() => of(apiError<MensagemResponse>('UPSTREAM_ERROR', 'Não foi possível alterar sua senha. Tente novamente.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    if (!req.senhaAtual) {
      return of(apiError<MensagemResponse>('VALIDATION_ERROR', 'Informe a senha atual.')).pipe(delay(300));
    }
    if (!req.novaSenha || req.novaSenha.length < SENHA_MIN) {
      return of(apiError<MensagemResponse>('VALIDATION_ERROR', `A nova senha deve ter no mínimo ${SENHA_MIN} caracteres.`)).pipe(delay(300));
    }
    if (req.novaSenha !== req.confirmarNovaSenha) {
      return of(apiError<MensagemResponse>('VALIDATION_ERROR', 'As senhas informadas não coincidem.')).pipe(delay(300));
    }

    const atual = this.usuarioAtualSignal();
    const usuarioMock = atual ? USUARIOS_MOCK.find((u) => u.id === atual.id) : undefined;
    if (!atual || !usuarioMock || usuarioMock.senha !== req.senhaAtual) {
      return of(apiError<MensagemResponse>('UNAUTHENTICATED', 'Senha atual incorreta.')).pipe(delay(400));
    }

    usuarioMock.senha = req.novaSenha;
    return of(apiSuccess<MensagemResponse>({ mensagem: 'Senha alterada com sucesso.' })).pipe(delay(500));
  }

  logout(): void {
    this.usuarioAtualSignal.set(null);
    this.tokenAtual = null;
  }

  private aplicarLoginBemSucedido(envelope: ApiEnvelope<LoginResponse>): void {
    if (envelope.success && envelope.data) {
      this.usuarioAtualSignal.set(envelope.data.usuario);
      this.tokenAtual = envelope.data.token;
    }
  }

  private aplicarAtualizacaoContaBemSucedida(envelope: ApiEnvelope<UsuarioMe>): void {
    if (envelope.success && envelope.data) {
      const { email: _email, criadoEm: _criadoEm, ...resumo } = envelope.data;
      this.usuarioAtualSignal.set(resumo);
    }
  }

  private validarAtualizarConta(req: AtualizarContaRequest): ApiEnvelope<UsuarioMe> | null {
    const fields: Record<string, string> = {};

    if (!req.apelido || req.apelido.includes(' ') || req.apelido.length < APELIDO_MIN || req.apelido.length > APELIDO_MAX) {
      fields['apelido'] = `Apelido deve ter entre ${APELIDO_MIN} e ${APELIDO_MAX} caracteres, sem espaços.`;
    }
    if (!req.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(req.email)) {
      fields['email'] = 'E-mail inválido.';
    }

    if (Object.keys(fields).length === 0) {
      return null;
    }
    return apiError<UsuarioMe>('VALIDATION_ERROR', 'Verifique os campos informados.', fields);
  }

  private validarRegistro(req: RegistroRequest): ApiEnvelope<UsuarioResponse> | null {
    const fields: Record<string, string> = {};

    if (!req.apelido || req.apelido.includes(' ') || req.apelido.length < APELIDO_MIN || req.apelido.length > APELIDO_MAX) {
      fields['apelido'] = `Apelido deve ter entre ${APELIDO_MIN} e ${APELIDO_MAX} caracteres, sem espaços.`;
    }
    if (!req.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(req.email)) {
      fields['email'] = 'E-mail inválido.';
    }
    if (!req.senha || req.senha.length < SENHA_MIN) {
      fields['senha'] = `Senha deve ter no mínimo ${SENHA_MIN} caracteres.`;
    }
    if (req.senha !== req.confirmarSenha) {
      fields['confirmarSenha'] = 'As senhas informadas não coincidem.';
    }

    if (Object.keys(fields).length === 0) {
      return null;
    }
    return apiError<UsuarioResponse>('VALIDATION_ERROR', 'Verifique os campos informados.', fields);
  }
}
