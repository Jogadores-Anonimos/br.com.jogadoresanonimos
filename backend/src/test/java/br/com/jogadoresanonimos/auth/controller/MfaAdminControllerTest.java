package br.com.jogadoresanonimos.auth.controller;

import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.auth.dto.MfaConfirmarRequest;
import br.com.jogadoresanonimos.auth.dto.MfaDesativarRequest;
import br.com.jogadoresanonimos.auth.dto.MfaEnrollResponse;
import br.com.jogadoresanonimos.auth.service.MfaService;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.security.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MfaAdminControllerTest {

    @Mock
    MfaService mfaService;

    MfaAdminController mfaAdminController;

    @BeforeEach
    void setUp() {
        mfaAdminController = new MfaAdminController(mfaService);
    }

    private AuthenticatedUser principalAdmin(UUID id) {
        return new AuthenticatedUser(id, "admin", "ADMIN");
    }

    @Test
    void enroll_deveRetornarOkComRespostaDoService() {
        UUID adminId = UUID.randomUUID();
        MfaEnrollResponse esperado = new MfaEnrollResponse("otpauth://totp/uri");
        when(mfaService.enroll(adminId)).thenReturn(esperado);
        ResponseEntity<ApiResponse<MfaEnrollResponse>> resposta = mfaAdminController.enroll(principalAdmin(adminId));
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
        verify(mfaService).enroll(adminId);
    }

    @Test
    void confirmar_deveRetornarOkComRespostaDoService() {
        UUID adminId = UUID.randomUUID();
        MfaConfirmarRequest request = new MfaConfirmarRequest("123456");
        MensagemResponse esperado = new MensagemResponse("MFA ativado com sucesso.");
        when(mfaService.confirmar(adminId, request)).thenReturn(esperado);
        ResponseEntity<ApiResponse<MensagemResponse>> resposta =
                mfaAdminController.confirmar(principalAdmin(adminId), request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
        verify(mfaService).confirmar(adminId, request);
    }

    @Test
    void desativar_deveRetornarOkComRespostaDoService() {
        UUID adminId = UUID.randomUUID();
        MfaDesativarRequest request = new MfaDesativarRequest("senhaCorreta", "123456");
        MensagemResponse esperado = new MensagemResponse("MFA desativado.");
        when(mfaService.desativar(adminId, request)).thenReturn(esperado);
        ResponseEntity<ApiResponse<MensagemResponse>> resposta =
                mfaAdminController.desativar(principalAdmin(adminId), request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
        verify(mfaService).desativar(adminId, request);
    }
}
