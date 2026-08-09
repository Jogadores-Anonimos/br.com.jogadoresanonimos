import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, delay, tap } from 'rxjs/operators';

import { environment } from '../../../environments/environment';
import { ApiEnvelope, ApiFieldError, MFA_CODIGO_INVALIDO_MENSAGEM, apiError, apiErrorDeHttpError, apiSuccess } from '../api-envelope';
import {
  AlterarSenhaRequest,
  AtualizarContaRequest,
  ConfirmarMfaRequest,
  DesativarMfaRequest,
  EsqueciSenhaRequest,
  LoginMfaRequest,
  LoginRequest,
  LoginResponse,
  MensagemResponse,
  MfaEnrollResponse,
  RedefinirSenhaRequest,
  ReenviarConfirmacaoEmailRequest,
  RegistroRequest,
  UsuarioMe,
  UsuarioResponse,
  UsuarioResumo,
} from './auth.model';
import {
  CODIGO_TOTP_MOCK,
  OTPAUTH_URI_MOCK,
  TOKEN_CONFIRMACAO_EMAIL_EXPIRADO_MOCK,
  TOKEN_CONFIRMACAO_EMAIL_JA_CONFIRMADO_MOCK,
  TOKEN_CONFIRMACAO_EMAIL_VALIDO_MOCK,
  USUARIOS_MOCK,
  encontrarUsuarioMockPorIdentificador,
  encontrarUsuarioMockPorToken,
  gerarTokenMock,
  paraResumo,
} from './auth.mock';

const APELIDO_MIN = 3;
const APELIDO_MAX = 30;
const SENHA_MIN = 10;

const SESSAO_STORAGE_KEY = 'ja_pref_sync_v1';

interface SessaoPersistida {
  token: string;
  expiraEm: string;
}

@Injectable({ providedIn: 'root' })
export class Auth {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/auth`;

  private readonly usuarioAtualSignal = signal<UsuarioResumo | null>(null);
  readonly usuarioAtual = this.usuarioAtualSignal.asReadonly();

  private readonly usuarioAtualDetalhadoSignal = signal<UsuarioMe | null>(null);
  readonly usuarioAtualDetalhado = this.usuarioAtualDetalhadoSignal.asReadonly();

  private readonly rehidratacaoConcluidaSignal = signal(false);
  readonly rehidratacaoConcluida = this.rehidratacaoConcluidaSignal.asReadonly();

  private tokenAtual: string | null = null;

  constructor() {
    this.rehidratarSessao();
  }

  obterToken(): string | null {
    return this.tokenAtual;
  }

  private rehidratarSessao(): void {
    const sessao = this.lerSessaoPersistida();
    if (!sessao) {
      this.rehidratacaoConcluidaSignal.set(true);
      return;
    }
    if (new Date(sessao.expiraEm).getTime() <= Date.now()) {
      this.limparSessaoPersistida();
      this.rehidratacaoConcluidaSignal.set(true);
      return;
    }

    this.tokenAtual = sessao.token;
    this.salvarSessaoPersistida(sessao, this.lerSessaoDeStorage(localStorage) !== null);
    this.buscarUsuarioLogado().subscribe((resposta) => {
      if (resposta.success && resposta.data) {
        this.definirUsuarioAtualDetalhado(resposta.data);
      } else {
        this.logout();
      }
      this.rehidratacaoConcluidaSignal.set(true);
    });
  }

  private lerSessaoPersistida(): SessaoPersistida | null {
    return this.lerSessaoDeStorage(sessionStorage) ?? this.lerSessaoDeStorage(localStorage);
  }

  private lerSessaoDeStorage(storage: Storage): SessaoPersistida | null {
    try {
      const bruto = storage.getItem(SESSAO_STORAGE_KEY);
      if (!bruto) {
        return null;
      }
      const sessao = JSON.parse(bruto) as SessaoPersistida;
      if (!sessao.token || !sessao.expiraEm) {
        return null;
      }
      return sessao;
    } catch {
      return null;
    }
  }

  private salvarSessaoPersistida(sessao: SessaoPersistida, lembrarDeMim: boolean): void {
    try {
      sessionStorage.setItem(SESSAO_STORAGE_KEY, JSON.stringify(sessao));
    } catch {
      return;
    }
    if (lembrarDeMim) {
      try {
        localStorage.setItem(SESSAO_STORAGE_KEY, JSON.stringify(sessao));
      } catch {
        return;
      }
    }
  }

  private limparSessaoPersistida(): void {
    try {
      sessionStorage.removeItem(SESSAO_STORAGE_KEY);
    } catch {
      return;
    }
    try {
      localStorage.removeItem(SESSAO_STORAGE_KEY);
    } catch {
      return;
    }
  }

  registrar(req: RegistroRequest): Observable<ApiEnvelope<UsuarioResponse>> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<UsuarioResponse>>(`${this.baseUrl}/registro`, req).pipe(
        catchError((erro) =>
          of(apiErrorDeHttpError<UsuarioResponse>(erro, 'INTERNAL_ERROR', 'Não foi possível concluir o cadastro. Tente novamente.')),
        ),
      );
    }

    const validacao = this.validarRegistro(req);
    if (validacao) {
      return of(validacao).pipe(delay(400));
    }

    const apelidoEmUso = encontrarUsuarioMockPorIdentificador(req.apelido);
    if (apelidoEmUso) {
      return of(
        apiError<UsuarioResponse>('AUTH_APELIDO_EM_USO', 'Esse apelido já está em uso, escolha outro', [
          { campo: 'apelido', mensagem: 'Esse apelido já está em uso, escolha outro' },
        ]),
      ).pipe(delay(400));
    }

    const emailEmUso = encontrarUsuarioMockPorIdentificador(req.email);
    if (emailEmUso) {
      return of(
        apiError<UsuarioResponse>('AUTH_EMAIL_EM_USO', 'Esse e-mail já está cadastrado', [
          { campo: 'email', mensagem: 'Esse e-mail já está cadastrado' },
        ]),
      ).pipe(delay(400));
    }

    const resposta: UsuarioResponse = {
      id: `mock-${crypto.randomUUID()}`,
      apelido: req.apelido,
      role: 'PARTICIPANTE',
      deveTrocarSenha: false,
      criadoEm: new Date().toISOString(),
    };
    return of(apiSuccess(resposta)).pipe(delay(500));
  }

  login(req: LoginRequest): Observable<ApiEnvelope<LoginResponse>> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<LoginResponse>>(`${this.baseUrl}/login`, req).pipe(
        tap((resposta) => this.aplicarLoginBemSucedido(resposta, req.lembrarDeMim ?? false)),
        catchError((erro) =>
          of(apiErrorDeHttpError<LoginResponse>(erro, 'INTERNAL_ERROR', 'Não foi possível entrar. Tente novamente.')),
        ),
      );
    }

    if (!req.identificador?.trim() || !req.senha) {
      return of(apiError<LoginResponse>('VALIDATION_ERROR', 'Preencha apelido/e-mail e senha.')).pipe(delay(300));
    }

    const usuario = encontrarUsuarioMockPorIdentificador(req.identificador);
    if (!usuario || usuario.senha !== req.senha) {
      return of(apiError<LoginResponse>('AUTH_INVALID_CREDENTIALS', 'Apelido/e-mail ou senha inválidos.')).pipe(delay(400));
    }

    if (usuario.mfaAtivo) {
      const respostaMfa: LoginResponse = { mfaRequerido: true, token: null, expiraEm: null, usuario: null };
      return of(apiSuccess(respostaMfa)).pipe(delay(500));
    }

    const resposta: LoginResponse = {
      mfaRequerido: false,
      token: gerarTokenMock(usuario.id),
      expiraEm: new Date(Date.now() + 60 * 60 * 1000).toISOString(),
      usuario: paraResumo(usuario),
    };
    return of(apiSuccess(resposta)).pipe(
      delay(500),
      tap((envelope) => this.aplicarLoginBemSucedido(envelope, req.lembrarDeMim ?? false)),
    );
  }

  loginComMfa(req: LoginMfaRequest, lembrarDeMim = false): Observable<ApiEnvelope<LoginResponse>> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<LoginResponse>>(`${this.baseUrl}/login/mfa`, req).pipe(
        tap((resposta) => this.aplicarLoginBemSucedido(resposta, lembrarDeMim)),
        catchError((erro) =>
          of(apiErrorDeHttpError<LoginResponse>(erro, 'INTERNAL_ERROR', 'Não foi possível entrar. Tente novamente.')),
        ),
      );
    }

    const usuario = encontrarUsuarioMockPorIdentificador(req.identificador);
    if (!usuario || usuario.senha !== req.senha) {
      return of(apiError<LoginResponse>('AUTH_INVALID_CREDENTIALS', 'Apelido/e-mail ou senha inválidos.')).pipe(delay(400));
    }
    if (req.codigoTotp !== CODIGO_TOTP_MOCK) {
      return of(apiError<LoginResponse>('MFA_CODIGO_INVALIDO', MFA_CODIGO_INVALIDO_MENSAGEM)).pipe(delay(400));
    }

    const resposta: LoginResponse = {
      mfaRequerido: false,
      token: gerarTokenMock(usuario.id),
      expiraEm: new Date(Date.now() + 60 * 60 * 1000).toISOString(),
      usuario: paraResumo(usuario),
    };
    return of(apiSuccess(resposta)).pipe(
      delay(500),
      tap((envelope) => this.aplicarLoginBemSucedido(envelope, lembrarDeMim)),
    );
  }

  enrollMfa(): Observable<ApiEnvelope<MfaEnrollResponse>> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<MfaEnrollResponse>>(`${environment.apiBaseUrl}/admin/mfa/enroll`, {}).pipe(
        catchError((erro) =>
          of(apiErrorDeHttpError<MfaEnrollResponse>(erro, 'INTERNAL_ERROR', 'Não foi possível iniciar a configuração do MFA. Tente novamente.')),
        ),
      );
    }

    const atual = this.usuarioAtualSignal();
    if (!atual) {
      return of(apiError<MfaEnrollResponse>('AUTH_TOKEN_AUSENTE_OU_INVALIDO', 'Sessão expirada. Faça login novamente.')).pipe(delay(300));
    }
    return of(apiSuccess<MfaEnrollResponse>({ otpauthUri: OTPAUTH_URI_MOCK })).pipe(delay(500));
  }

  confirmarMfa(req: ConfirmarMfaRequest): Observable<ApiEnvelope<MensagemResponse>> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<MensagemResponse>>(`${environment.apiBaseUrl}/admin/mfa/confirmar`, req).pipe(
        catchError((erro) =>
          of(apiErrorDeHttpError<MensagemResponse>(erro, 'INTERNAL_ERROR', 'Não foi possível confirmar o MFA. Tente novamente.')),
        ),
      );
    }

    const atual = this.usuarioAtualSignal();
    const usuarioMock = atual ? USUARIOS_MOCK.find((u) => u.id === atual.id) : undefined;
    if (!atual || !usuarioMock) {
      return of(apiError<MensagemResponse>('AUTH_TOKEN_AUSENTE_OU_INVALIDO', 'Sessão expirada. Faça login novamente.')).pipe(delay(300));
    }
    if (req.codigoTotp !== CODIGO_TOTP_MOCK) {
      return of(apiError<MensagemResponse>('MFA_CODIGO_INVALIDO', MFA_CODIGO_INVALIDO_MENSAGEM)).pipe(delay(400));
    }

    usuarioMock.mfaAtivo = true;
    return of(apiSuccess<MensagemResponse>({ mensagem: 'MFA confirmado com sucesso.' })).pipe(delay(500));
  }

  desativarMfa(req: DesativarMfaRequest): Observable<ApiEnvelope<MensagemResponse>> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<MensagemResponse>>(`${environment.apiBaseUrl}/admin/mfa/desativar`, req).pipe(
        catchError((erro) =>
          of(apiErrorDeHttpError<MensagemResponse>(erro, 'INTERNAL_ERROR', 'Não foi possível desativar o MFA. Tente novamente.')),
        ),
      );
    }

    const atual = this.usuarioAtualSignal();
    const usuarioMock = atual ? USUARIOS_MOCK.find((u) => u.id === atual.id) : undefined;
    if (!atual || !usuarioMock || usuarioMock.senha !== req.senha) {
      return of(apiError<MensagemResponse>('MFA_SENHA_INCORRETA', 'Senha atual incorreta.')).pipe(delay(400));
    }
    if (req.codigoTotp !== CODIGO_TOTP_MOCK) {
      return of(apiError<MensagemResponse>('MFA_CODIGO_INVALIDO', MFA_CODIGO_INVALIDO_MENSAGEM)).pipe(delay(400));
    }

    usuarioMock.mfaAtivo = false;
    return of(apiSuccess<MensagemResponse>({ mensagem: 'MFA desativado com sucesso.' })).pipe(delay(500));
  }

  esqueciSenha(req: EsqueciSenhaRequest): Observable<ApiEnvelope<MensagemResponse>> {
    const mensagemGenerica: MensagemResponse = {
      mensagem: 'Se o apelido ou e-mail existir em nossa base, enviaremos um link de redefinição.',
    };

    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<MensagemResponse>>(`${this.baseUrl}/esqueci-senha`, req).pipe(
        catchError((erro) =>
          of(apiErrorDeHttpError<MensagemResponse>(erro, 'INTERNAL_ERROR', 'Não foi possível processar seu pedido. Tente novamente.')),
        ),
      );
    }

    if (!req.identificador?.trim()) {
      return of(apiError<MensagemResponse>('VALIDATION_ERROR', 'Informe seu apelido ou e-mail.')).pipe(delay(300));
    }
    return of(apiSuccess(mensagemGenerica)).pipe(delay(500));
  }

  redefinirSenha(req: RedefinirSenhaRequest): Observable<ApiEnvelope<MensagemResponse>> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<MensagemResponse>>(`${this.baseUrl}/redefinir-senha`, req).pipe(
        catchError((erro) =>
          of(apiErrorDeHttpError<MensagemResponse>(erro, 'INTERNAL_ERROR', 'Não foi possível redefinir sua senha. Tente novamente.')),
        ),
      );
    }

    if (req.novaSenha.length < SENHA_MIN) {
      return of(apiError<MensagemResponse>('VALIDATION_ERROR', `A nova senha deve ter no mínimo ${SENHA_MIN} caracteres.`)).pipe(delay(300));
    }
    if (req.novaSenha !== req.confirmarNovaSenha) {
      return of(apiError<MensagemResponse>('AUTH_SENHA_CONFIRMACAO_DIVERGENTE', 'As senhas informadas não coincidem.')).pipe(delay(300));
    }
    if (!req.token) {
      return of(apiError<MensagemResponse>('AUTH_RESET_TOKEN_INVALIDO', 'Link inválido ou expirado.')).pipe(delay(300));
    }

    return of(apiSuccess<MensagemResponse>({ mensagem: 'Senha redefinida com sucesso.' })).pipe(delay(500));
  }

  confirmarEmail(token: string): Observable<ApiEnvelope<MensagemResponse>> {
    if (!environment.useMocks) {
      return this.http.get<ApiEnvelope<MensagemResponse>>(`${this.baseUrl}/confirmar-email`, { params: { token } }).pipe(
        catchError((erro) =>
          of(
            apiErrorDeHttpError<MensagemResponse>(
              erro,
              'AUTH_EMAIL_CONFIRMACAO_TOKEN_INVALIDO',
              'Não foi possível confirmar seu e-mail. Tente novamente.',
            ),
          ),
        ),
      );
    }

    if (!token?.trim()) {
      return of(apiError<MensagemResponse>('VALIDATION_ERROR', 'Link de confirmação inválido.')).pipe(delay(300));
    }
    if (token === TOKEN_CONFIRMACAO_EMAIL_EXPIRADO_MOCK) {
      return of(
        apiError<MensagemResponse>('AUTH_EMAIL_CONFIRMACAO_TOKEN_INVALIDO', 'Link de confirmação inválido ou expirado.'),
      ).pipe(delay(400));
    }
    if (token === TOKEN_CONFIRMACAO_EMAIL_VALIDO_MOCK || token === TOKEN_CONFIRMACAO_EMAIL_JA_CONFIRMADO_MOCK) {
      return of(apiSuccess<MensagemResponse>({ mensagem: 'E-mail confirmado com sucesso.' })).pipe(delay(500));
    }
    return of(
      apiError<MensagemResponse>('AUTH_EMAIL_CONFIRMACAO_TOKEN_INVALIDO', 'Link de confirmação inválido ou expirado.'),
    ).pipe(delay(400));
  }

  reenviarConfirmacaoEmail(req: ReenviarConfirmacaoEmailRequest): Observable<ApiEnvelope<MensagemResponse>> {
    const mensagemGenerica: MensagemResponse = {
      mensagem: 'Se o apelido ou e-mail existir em nossa base e o e-mail ainda não estiver confirmado, enviaremos um novo link de confirmação.',
    };

    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<MensagemResponse>>(`${this.baseUrl}/reenviar-confirmacao-email`, req).pipe(
        catchError((erro) =>
          of(apiErrorDeHttpError<MensagemResponse>(erro, 'INTERNAL_ERROR', 'Não foi possível processar seu pedido. Tente novamente.')),
        ),
      );
    }

    if (!req.identificador?.trim()) {
      return of(apiError<MensagemResponse>('VALIDATION_ERROR', 'Informe seu apelido ou e-mail.')).pipe(delay(300));
    }
    return of(apiSuccess(mensagemGenerica)).pipe(delay(500));
  }

  buscarUsuarioLogado(): Observable<ApiEnvelope<UsuarioMe>> {
    if (!environment.useMocks) {
      return this.http.get<ApiEnvelope<UsuarioMe>>(`${environment.apiBaseUrl}/usuarios/me`).pipe(
        catchError((erro) =>
          of(apiErrorDeHttpError<UsuarioMe>(erro, 'AUTH_TOKEN_AUSENTE_OU_INVALIDO', 'Sessão expirada. Faça login novamente.')),
        ),
      );
    }

    const atual = this.usuarioAtualSignal();
    const usuario = atual
      ? encontrarUsuarioMockPorIdentificador(atual.apelido)
      : this.tokenAtual
        ? encontrarUsuarioMockPorToken(this.tokenAtual)
        : undefined;
    if (!usuario) {
      return of(apiError<UsuarioMe>('AUTH_TOKEN_AUSENTE_OU_INVALIDO', 'Sessão expirada. Faça login novamente.')).pipe(delay(300));
    }

    const { senha: _senha, ...usuarioMe } = usuario;
    return of(apiSuccess(usuarioMe)).pipe(delay(400));
  }

  atualizarConta(req: AtualizarContaRequest): Observable<ApiEnvelope<UsuarioMe>> {
    if (!environment.useMocks) {
      return this.http.put<ApiEnvelope<UsuarioMe>>(`${environment.apiBaseUrl}/usuarios/me`, req).pipe(
        tap((envelope) => this.aplicarAtualizacaoContaBemSucedida(envelope)),
        catchError((erro) =>
          of(apiErrorDeHttpError<UsuarioMe>(erro, 'INTERNAL_ERROR', 'Não foi possível salvar os dados informados. Tente novamente.')),
        ),
      );
    }

    const validacao = this.validarAtualizarConta(req);
    if (validacao) {
      return of(validacao).pipe(delay(300));
    }

    const atual = this.usuarioAtualSignal();
    if (!atual) {
      return of(apiError<UsuarioMe>('AUTH_TOKEN_AUSENTE_OU_INVALIDO', 'Sessão expirada. Faça login novamente.')).pipe(delay(300));
    }

    const conflito = encontrarUsuarioMockPorIdentificador(req.apelido) ?? encontrarUsuarioMockPorIdentificador(req.email);
    if (conflito && conflito.id !== atual.id) {
      return of(apiError<UsuarioMe>('USUARIO_DADOS_EM_USO', 'Não foi possível salvar os dados informados')).pipe(delay(400));
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
      deveTrocarSenha: atual.deveTrocarSenha,
      criadoEm: usuarioMock?.criadoEm ?? new Date().toISOString(),
      mfaAtivo: usuarioMock?.mfaAtivo ?? false,
      emailConfirmado: usuarioMock?.emailConfirmado ?? false,
    };
    return of(apiSuccess(resposta)).pipe(
      delay(400),
      tap((envelope) => this.aplicarAtualizacaoContaBemSucedida(envelope)),
    );
  }

  alterarSenha(req: AlterarSenhaRequest): Observable<ApiEnvelope<MensagemResponse>> {
    if (!environment.useMocks) {
      return this.http.put<ApiEnvelope<MensagemResponse>>(`${environment.apiBaseUrl}/usuarios/me/senha`, req).pipe(
        catchError((erro) =>
          of(apiErrorDeHttpError<MensagemResponse>(erro, 'INTERNAL_ERROR', 'Não foi possível alterar sua senha. Tente novamente.')),
        ),
      );
    }

    if (!req.senhaAtual) {
      return of(apiError<MensagemResponse>('VALIDATION_ERROR', 'Informe a senha atual.')).pipe(delay(300));
    }
    if (!req.novaSenha || req.novaSenha.length < SENHA_MIN) {
      return of(apiError<MensagemResponse>('VALIDATION_ERROR', `A nova senha deve ter no mínimo ${SENHA_MIN} caracteres.`)).pipe(delay(300));
    }
    if (req.novaSenha !== req.confirmarNovaSenha) {
      return of(apiError<MensagemResponse>('USUARIO_SENHA_CONFIRMACAO_DIVERGENTE', 'As senhas informadas não coincidem.')).pipe(delay(300));
    }

    const atual = this.usuarioAtualSignal();
    const usuarioMock = atual ? USUARIOS_MOCK.find((u) => u.id === atual.id) : undefined;
    if (!atual || !usuarioMock || usuarioMock.senha !== req.senhaAtual) {
      return of(apiError<MensagemResponse>('USUARIO_SENHA_ATUAL_INCORRETA', 'Senha atual incorreta.')).pipe(delay(400));
    }

    usuarioMock.senha = req.novaSenha;
    return of(apiSuccess<MensagemResponse>({ mensagem: 'Senha alterada com sucesso.' })).pipe(delay(500));
  }

  logout(): void {
    this.efetuarLogoutNoServidor().subscribe();
    this.limparSessaoLocal();
  }

  private efetuarLogoutNoServidor(): Observable<ApiEnvelope<MensagemResponse>> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<MensagemResponse>>(`${this.baseUrl}/logout`, {}).pipe(
        catchError(() => of(apiError<MensagemResponse>('INTERNAL_ERROR', 'Não foi possível encerrar a sessão no servidor.'))),
      );
    }

    return of(apiSuccess<MensagemResponse>({ mensagem: 'Sessão encerrada com sucesso.' })).pipe(delay(200));
  }

  private limparSessaoLocal(): void {
    this.usuarioAtualSignal.set(null);
    this.usuarioAtualDetalhadoSignal.set(null);
    this.tokenAtual = null;
    this.limparSessaoPersistida();
  }

  private definirUsuarioAtualDetalhado(usuarioMe: UsuarioMe): void {
    const { email: _email, criadoEm: _criadoEm, ...resumo } = usuarioMe;
    this.usuarioAtualSignal.set(resumo);
    this.usuarioAtualDetalhadoSignal.set(usuarioMe);
  }

  private aplicarLoginBemSucedido(envelope: ApiEnvelope<LoginResponse>, lembrarDeMim: boolean): void {
    const dados = envelope.data;
    if (envelope.success && dados && !dados.mfaRequerido && dados.token && dados.expiraEm && dados.usuario) {
      this.usuarioAtualSignal.set(dados.usuario);
      this.tokenAtual = dados.token;
      this.salvarSessaoPersistida({ token: dados.token, expiraEm: dados.expiraEm }, lembrarDeMim);
    }
  }

  private aplicarAtualizacaoContaBemSucedida(envelope: ApiEnvelope<UsuarioMe>): void {
    if (envelope.success && envelope.data) {
      this.definirUsuarioAtualDetalhado(envelope.data);
    }
  }

  private validarAtualizarConta(req: AtualizarContaRequest): ApiEnvelope<UsuarioMe> | null {
    const fields: ApiFieldError[] = [];

    if (!req.apelido || req.apelido.includes(' ') || req.apelido.length < APELIDO_MIN || req.apelido.length > APELIDO_MAX) {
      fields.push({ campo: 'apelido', mensagem: `Apelido deve ter entre ${APELIDO_MIN} e ${APELIDO_MAX} caracteres, sem espaços.` });
    }
    if (!req.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(req.email)) {
      fields.push({ campo: 'email', mensagem: 'E-mail inválido.' });
    }

    if (fields.length === 0) {
      return null;
    }
    return apiError<UsuarioMe>('VALIDATION_ERROR', 'Verifique os campos informados.', fields);
  }

  private validarRegistro(req: RegistroRequest): ApiEnvelope<UsuarioResponse> | null {
    const fields: ApiFieldError[] = [];

    if (!req.apelido || req.apelido.includes(' ') || req.apelido.length < APELIDO_MIN || req.apelido.length > APELIDO_MAX) {
      fields.push({ campo: 'apelido', mensagem: `Apelido deve ter entre ${APELIDO_MIN} e ${APELIDO_MAX} caracteres, sem espaços.` });
    }
    if (!req.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(req.email)) {
      fields.push({ campo: 'email', mensagem: 'E-mail inválido.' });
    }
    if (!req.senha || req.senha.length < SENHA_MIN) {
      fields.push({ campo: 'senha', mensagem: `Senha deve ter no mínimo ${SENHA_MIN} caracteres.` });
    }
    if (req.senha !== req.confirmarSenha) {
      fields.push({ campo: 'confirmarSenha', mensagem: 'As senhas informadas não coincidem.' });
    }

    if (fields.length === 0) {
      return null;
    }
    return apiError<UsuarioResponse>('VALIDATION_ERROR', 'Verifique os campos informados.', fields);
  }
}
