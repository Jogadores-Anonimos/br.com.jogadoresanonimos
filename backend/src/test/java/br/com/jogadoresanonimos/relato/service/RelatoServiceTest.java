package br.com.jogadoresanonimos.relato.service;

import br.com.jogadoresanonimos.auditoria.entity.AcaoAuditoria;
import br.com.jogadoresanonimos.auditoria.service.AdminAuditoriaService;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.ratelimit.RateLimiterService;
import br.com.jogadoresanonimos.relato.dto.EditarRelatoRequest;
import br.com.jogadoresanonimos.relato.dto.RelatoAdminResponse;
import br.com.jogadoresanonimos.relato.entity.Relato;
import br.com.jogadoresanonimos.relato.entity.StatusRelato;
import br.com.jogadoresanonimos.relato.repository.RelatoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RelatoServiceTest {

    @Mock
    RelatoRepository relatoRepository;
    @Mock
    AdminAuditoriaService adminAuditoriaService;

    RateLimiterService rateLimiterService;
    RelatoService relatoService;

    @BeforeEach
    void setUp() {
        rateLimiterService = new RateLimiterService();
        relatoService = new RelatoService(relatoRepository, rateLimiterService, adminAuditoriaService);
    }

    private Relato relatoComId(UUID id, StatusRelato status) {
        Relato relato = new Relato("texto".repeat(20), "Anônimo", UUID.randomUUID());
        setId(relato, id);
        setStatus(relato, status);
        return relato;
    }

    private void setId(Relato relato, UUID id) {
        try {
            var field = Relato.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(relato, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private void setStatus(Relato relato, StatusRelato status) {
        try {
            var field = Relato.class.getDeclaredField("status");
            field.setAccessible(true);
            field.set(relato, status);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void editar_deveFalharQuandoRelatoNaoEncontrado() {
        UUID id = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();
        when(relatoRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> relatoService.editar(id, new EditarRelatoRequest("texto editado".repeat(5)), adminId))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void editar_deveAtualizarTextoERegistrarAuditoria() {
        UUID id = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();
        Relato relato = relatoComId(id, StatusRelato.pending);
        when(relatoRepository.findById(id)).thenReturn(Optional.of(relato));
        EditarRelatoRequest request = new EditarRelatoRequest("texto editado".repeat(5));
        RelatoAdminResponse resposta = relatoService.editar(id, request, adminId);
        assertThat(resposta.texto()).isEqualTo(request.texto().trim());
        verify(adminAuditoriaService).registrar(adminId, "relato", id, AcaoAuditoria.EDITAR, "pending", "pending");
    }

    @Test
    void aprovar_deveAlterarStatusERegistrarAuditoria() {
        UUID id = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();
        Relato relato = relatoComId(id, StatusRelato.pending);
        when(relatoRepository.findById(id)).thenReturn(Optional.of(relato));
        RelatoAdminResponse resposta = relatoService.aprovar(id, adminId);
        assertThat(resposta.status()).isEqualTo("approved");
        verify(adminAuditoriaService).registrar(adminId, "relato", id, AcaoAuditoria.APROVAR, "pending", "approved");
    }

    @Test
    void aprovar_deveFalharQuandoRelatoNaoEncontrado() {
        UUID id = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();
        when(relatoRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> relatoService.aprovar(id, adminId))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void rejeitar_deveAlterarStatusERegistrarAuditoria() {
        UUID id = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();
        Relato relato = relatoComId(id, StatusRelato.pending);
        when(relatoRepository.findById(id)).thenReturn(Optional.of(relato));
        RelatoAdminResponse resposta = relatoService.rejeitar(id, adminId);
        assertThat(resposta.status()).isEqualTo("rejected");
        verify(adminAuditoriaService).registrar(adminId, "relato", id, AcaoAuditoria.REJEITAR, "pending", "rejected");
    }

    @Test
    void reverter_deveAlterarStatusERegistrarAuditoria() {
        UUID id = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();
        Relato relato = relatoComId(id, StatusRelato.approved);
        when(relatoRepository.findById(id)).thenReturn(Optional.of(relato));
        RelatoAdminResponse resposta = relatoService.reverter(id, adminId);
        assertThat(resposta.status()).isEqualTo("pending");
        verify(adminAuditoriaService).registrar(adminId, "relato", id, AcaoAuditoria.REVERTER, "approved", "pending");
    }

    @Test
    void remover_deveExcluirERegistrarAuditoria() {
        UUID id = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();
        Relato relato = relatoComId(id, StatusRelato.approved);
        when(relatoRepository.findById(id)).thenReturn(Optional.of(relato));
        relatoService.remover(id, adminId);
        verify(relatoRepository).delete(relato);
        verify(adminAuditoriaService).registrar(adminId, "relato", id, AcaoAuditoria.REMOVER, "approved", "approved");
    }

    @Test
    void remover_deveFalharQuandoRelatoNaoEncontrado() {
        UUID id = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();
        when(relatoRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> relatoService.remover(id, adminId))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }
}
