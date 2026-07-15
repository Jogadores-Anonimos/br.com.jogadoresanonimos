package br.com.jogadoresanonimos.escritorio.controller;

import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.escritorio.dto.*;
import br.com.jogadoresanonimos.escritorio.service.EscritorioRegionalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EscritorioRegionalAdminControllerTest {

    @Mock
    EscritorioRegionalService escritorioRegionalService;

    EscritorioRegionalAdminController controller;

    @BeforeEach
    void setUp() {
        controller = new EscritorioRegionalAdminController(escritorioRegionalService);
    }

    @Test
    void listar_deveRetornarOkComEstruturaDoService() {
        EscritoriosRegionaisResponse esperado = new EscritoriosRegionaisResponse(List.of(), List.of());
        when(escritorioRegionalService.listarEstruturaCompleta()).thenReturn(esperado);
        ResponseEntity<ApiResponse<EscritoriosRegionaisResponse>> resposta = controller.listar();
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
    }

    @Test
    void editarRegiao_deveRetornarOkSemAceitarOrdem() {
        UUID id = UUID.randomUUID();
        EditarRegiaoRequest request = new EditarRegiaoRequest("Região Sul");
        RegiaoIntergrupoResponse esperado = new RegiaoIntergrupoResponse(id, "Região Sul", 1, List.of(),
                Instant.now(), Instant.now());
        when(escritorioRegionalService.editarRegiao(id, request)).thenReturn(esperado);
        ResponseEntity<ApiResponse<RegiaoIntergrupoResponse>> resposta = controller.editarRegiao(id, request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
    }

    @Test
    void moverRegiaoParaCima_deveRetornarOkComRespostaDoService() {
        UUID id = UUID.randomUUID();
        RegiaoIntergrupoResponse esperado = new RegiaoIntergrupoResponse(id, "Região Sul", 0, List.of(),
                Instant.now(), Instant.now());
        when(escritorioRegionalService.moverRegiaoParaCima(id)).thenReturn(esperado);
        ResponseEntity<ApiResponse<RegiaoIntergrupoResponse>> resposta = controller.moverRegiaoParaCima(id);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
    }

    @Test
    void moverRegiaoParaBaixo_deveRetornarOkComRespostaDoService() {
        UUID id = UUID.randomUUID();
        RegiaoIntergrupoResponse esperado = new RegiaoIntergrupoResponse(id, "Região Sul", 1, List.of(),
                Instant.now(), Instant.now());
        when(escritorioRegionalService.moverRegiaoParaBaixo(id)).thenReturn(esperado);
        ResponseEntity<ApiResponse<RegiaoIntergrupoResponse>> resposta = controller.moverRegiaoParaBaixo(id);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
    }

    @Test
    void criarEstado_deveRetornarCreatedComUfENomePorExtenso() {
        UUID regiaoId = UUID.randomUUID();
        CriarEstadoRequest request = new CriarEstadoRequest("SP");
        EstadoIntergrupoResponse esperado = new EstadoIntergrupoResponse(UUID.randomUUID(), regiaoId, "SP",
                "São Paulo", 0, List.of(), Instant.now(), Instant.now());
        when(escritorioRegionalService.criarEstado(regiaoId, request)).thenReturn(esperado);
        ResponseEntity<ApiResponse<EstadoIntergrupoResponse>> resposta = controller.criarEstado(regiaoId, request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
    }

    @Test
    void removerEstado_deveRetornarOkEChamarService() {
        UUID regiaoId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        ResponseEntity<ApiResponse<MensagemResponse>> resposta = controller.removerEstado(regiaoId, id);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(escritorioRegionalService).removerEstado(regiaoId, id);
    }

    @Test
    void moverEstadoParaCima_deveRetornarOkComRespostaDoService() {
        UUID regiaoId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        EstadoIntergrupoResponse esperado = new EstadoIntergrupoResponse(id, regiaoId, "SP", "São Paulo", 0,
                List.of(), Instant.now(), Instant.now());
        when(escritorioRegionalService.moverEstadoParaCima(regiaoId, id)).thenReturn(esperado);
        ResponseEntity<ApiResponse<EstadoIntergrupoResponse>> resposta =
                controller.moverEstadoParaCima(regiaoId, id);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
    }

    @Test
    void moverEstadoParaBaixo_deveRetornarOkComRespostaDoService() {
        UUID regiaoId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        EstadoIntergrupoResponse esperado = new EstadoIntergrupoResponse(id, regiaoId, "SP", "São Paulo", 1,
                List.of(), Instant.now(), Instant.now());
        when(escritorioRegionalService.moverEstadoParaBaixo(regiaoId, id)).thenReturn(esperado);
        ResponseEntity<ApiResponse<EstadoIntergrupoResponse>> resposta =
                controller.moverEstadoParaBaixo(regiaoId, id);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
    }

    @Test
    void criarGrupo_deveRetornarCreatedComDiasSemanaEHorarioETelefones() {
        UUID estadoId = UUID.randomUUID();
        CriarGrupoRequest request = new CriarGrupoRequest("Grupo Pioneiro", "Rua X", "Bairro", "Cidade - SP",
                List.of(new AgendaItemRequest("SEGUNDA", "18h"), new AgendaItemRequest("QUINTA", "18h")),
                List.of(new GrupoTelefoneRequest("Central", "11 90000-0000")));
        GrupoIntergrupoResponse esperado = new GrupoIntergrupoResponse(UUID.randomUUID(), estadoId,
                "Grupo Pioneiro", "Rua X", "Bairro", "Cidade - SP",
                List.of(new AgendaItemResponse("SEGUNDA", "18h"), new AgendaItemResponse("QUINTA", "18h")),
                List.of(new GrupoTelefoneResponse(UUID.randomUUID(), "Central", "11 90000-0000")), 0,
                Instant.now(), Instant.now());
        when(escritorioRegionalService.criarGrupo(estadoId, request)).thenReturn(esperado);
        ResponseEntity<ApiResponse<GrupoIntergrupoResponse>> resposta = controller.criarGrupo(estadoId, request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
    }

    @Test
    void removerGrupo_deveRetornarOkEChamarService() {
        UUID estadoId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        ResponseEntity<ApiResponse<MensagemResponse>> resposta = controller.removerGrupo(estadoId, id);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(escritorioRegionalService).removerGrupo(estadoId, id);
    }

    @Test
    void moverGrupoParaCima_deveRetornarOkComRespostaDoService() {
        UUID estadoId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        GrupoIntergrupoResponse esperado = new GrupoIntergrupoResponse(id, estadoId, "Grupo Pioneiro", "Rua X",
                null, "Cidade", List.of(new AgendaItemResponse("SEGUNDA", "18h")), List.of(), 0,
                Instant.now(), Instant.now());
        when(escritorioRegionalService.moverGrupoParaCima(estadoId, id)).thenReturn(esperado);
        ResponseEntity<ApiResponse<GrupoIntergrupoResponse>> resposta =
                controller.moverGrupoParaCima(estadoId, id);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
    }

    @Test
    void moverGrupoParaBaixo_deveRetornarOkComRespostaDoService() {
        UUID estadoId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        GrupoIntergrupoResponse esperado = new GrupoIntergrupoResponse(id, estadoId, "Grupo Pioneiro", "Rua X",
                null, "Cidade", List.of(new AgendaItemResponse("SEGUNDA", "18h")), List.of(), 1,
                Instant.now(), Instant.now());
        when(escritorioRegionalService.moverGrupoParaBaixo(estadoId, id)).thenReturn(esperado);
        ResponseEntity<ApiResponse<GrupoIntergrupoResponse>> resposta =
                controller.moverGrupoParaBaixo(estadoId, id);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
    }

    @Test
    void criarEscritorioServico_deveRetornarCreatedSemAceitarOrdem() {
        CriarEscritorioServicoRequest request = new CriarEscritorioServicoRequest("ERSJAB", "Rua Y", "Sala 12", null,
                "São Paulo - SP", List.of("a@a.com"), List.of());
        EscritorioServicoResponse esperado = new EscritorioServicoResponse(UUID.randomUUID(), "ERSJAB", "Rua Y",
                "Sala 12", null, "São Paulo - SP", List.of("a@a.com"), List.of(), 0, Instant.now(), Instant.now());
        when(escritorioRegionalService.criarEscritorioServico(request)).thenReturn(esperado);
        ResponseEntity<ApiResponse<EscritorioServicoResponse>> resposta =
                controller.criarEscritorioServico(request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
    }

    @Test
    void criarEscritorioServico_deveRetornarCreatedComBairroETelefones() {
        CriarEscritorioServicoRequest request = new CriarEscritorioServicoRequest("ERSJAB", "Rua Y", "Sala 12",
                "Centro", "São Paulo - SP", List.of("a@a.com"),
                List.of(new EscritorioTelefoneRequest("Central", "11 90000-0000")));
        EscritorioServicoResponse esperado = new EscritorioServicoResponse(UUID.randomUUID(), "ERSJAB", "Rua Y",
                "Sala 12", "Centro", "São Paulo - SP", List.of("a@a.com"),
                List.of(new EscritorioTelefoneResponse(UUID.randomUUID(), "Central", "11 90000-0000")), 0,
                Instant.now(), Instant.now());
        when(escritorioRegionalService.criarEscritorioServico(request)).thenReturn(esperado);
        ResponseEntity<ApiResponse<EscritorioServicoResponse>> resposta =
                controller.criarEscritorioServico(request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
        assertThat(resposta.getBody().data().bairro()).isEqualTo("Centro");
        assertThat(resposta.getBody().data().telefones()).hasSize(1);
        assertThat(resposta.getBody().data().telefones().get(0).telefone()).isEqualTo("11 90000-0000");
    }

    @Test
    void removerEscritorioServico_deveRetornarOkEChamarService() {
        UUID id = UUID.randomUUID();
        ResponseEntity<ApiResponse<MensagemResponse>> resposta = controller.removerEscritorioServico(id);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(escritorioRegionalService).removerEscritorioServico(id);
    }

    @Test
    void moverEscritorioServicoParaCima_deveRetornarOkComRespostaDoService() {
        UUID id = UUID.randomUUID();
        EscritorioServicoResponse esperado = new EscritorioServicoResponse(id, "ERSJAB", "Rua Y", "Sala 12", null,
                "São Paulo - SP", List.of("a@a.com"), List.of(), 0, Instant.now(), Instant.now());
        when(escritorioRegionalService.moverEscritorioServicoParaCima(id)).thenReturn(esperado);
        ResponseEntity<ApiResponse<EscritorioServicoResponse>> resposta =
                controller.moverEscritorioServicoParaCima(id);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
    }

    @Test
    void moverEscritorioServicoParaBaixo_deveRetornarOkComRespostaDoService() {
        UUID id = UUID.randomUUID();
        EscritorioServicoResponse esperado = new EscritorioServicoResponse(id, "ERSJAB", "Rua Y", "Sala 12", null,
                "São Paulo - SP", List.of("a@a.com"), List.of(), 1, Instant.now(), Instant.now());
        when(escritorioRegionalService.moverEscritorioServicoParaBaixo(id)).thenReturn(esperado);
        ResponseEntity<ApiResponse<EscritorioServicoResponse>> resposta =
                controller.moverEscritorioServicoParaBaixo(id);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().data()).isEqualTo(esperado);
    }

    private static void setId(Object entity, UUID id) {
        try {
            var field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
