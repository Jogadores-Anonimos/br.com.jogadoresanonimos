package br.com.jogadoresanonimos.reuniao.service;

import br.com.jogadoresanonimos.common.exception.RateLimitedException;
import br.com.jogadoresanonimos.common.ratelimit.RateLimiterService;
import br.com.jogadoresanonimos.reuniao.dto.BackfillGeolocalizacaoResponse;
import br.com.jogadoresanonimos.reuniao.entity.CategoriaReuniao;
import br.com.jogadoresanonimos.reuniao.entity.Reuniao;
import br.com.jogadoresanonimos.reuniao.repository.ReuniaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReuniaoBackfillGeolocalizacaoServiceTest {

    @Mock
    ReuniaoRepository reuniaoRepository;

    @Mock
    ReuniaoBackfillGeolocalizacaoItemService itemService;

    RateLimiterService rateLimiterService;

    ReuniaoBackfillGeolocalizacaoService service;

    @BeforeEach
    void setUp() {
        rateLimiterService = new RateLimiterService();
        service = new ReuniaoBackfillGeolocalizacaoService(reuniaoRepository, itemService, rateLimiterService);
    }

    private Reuniao reuniaoPresencialSemEndereco() {
        return new Reuniao("Grupo Centro", CategoriaReuniao.presencial, "descricao", "Segunda-feira",
                "19h30", "Rua das Flores, 123", null, null, null,
                -23.5505, -46.6333, null, null, null, null, null, null, null);
    }

    @Test
    void reuniaoComEstadoNuloEhAtualizadaComEnderecoGeocodificado() {
        Reuniao reuniao = reuniaoPresencialSemEndereco();
        when(reuniaoRepository.findAllByCategoriaAndEstadoIsNullAndLatitudeIsNotNullAndLongitudeIsNotNull(CategoriaReuniao.presencial))
                .thenReturn(List.of(reuniao));
        when(itemService.processar(reuniao.getId(), -23.5505, -46.6333)).thenReturn(Optional.empty());

        BackfillGeolocalizacaoResponse response = service.executar();

        assertThat(response.totalElegiveis()).isEqualTo(1);
        assertThat(response.atualizadas()).isEqualTo(1);
        assertThat(response.falhas()).isEqualTo(0);
        assertThat(response.detalhesFalha()).isEmpty();
    }

    @Test
    void reuniaoJaComEstadoPreenchidoNaoEhReprocessada() {
        when(reuniaoRepository.findAllByCategoriaAndEstadoIsNullAndLatitudeIsNotNullAndLongitudeIsNotNull(CategoriaReuniao.presencial))
                .thenReturn(List.of());

        BackfillGeolocalizacaoResponse response = service.executar();

        assertThat(response.totalElegiveis()).isEqualTo(0);
        assertThat(response.atualizadas()).isEqualTo(0);
        assertThat(response.falhas()).isEqualTo(0);
        verifyNoInteractions(itemService);
    }

    @Test
    void falhaDeGeocodeDeUmItemNaoAbortaOLote() {
        Reuniao reuniaoComFalha = reuniaoPresencialSemEndereco();
        Reuniao reuniaoComSucesso = reuniaoPresencialSemEndereco();
        ReflectionTestUtils.setField(reuniaoComFalha, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(reuniaoComSucesso, "id", UUID.randomUUID());
        when(reuniaoRepository.findAllByCategoriaAndEstadoIsNullAndLatitudeIsNotNullAndLongitudeIsNotNull(CategoriaReuniao.presencial))
                .thenReturn(List.of(reuniaoComFalha, reuniaoComSucesso));
        when(itemService.processar(reuniaoComFalha.getId(), -23.5505, -46.6333))
                .thenReturn(Optional.of("NOMINATIM_SEM_RESPOSTA"));
        when(itemService.processar(reuniaoComSucesso.getId(), -23.5505, -46.6333))
                .thenReturn(Optional.empty());

        BackfillGeolocalizacaoResponse response = service.executar();

        assertThat(response.totalElegiveis()).isEqualTo(2);
        assertThat(response.atualizadas()).isEqualTo(1);
        assertThat(response.falhas()).isEqualTo(1);
        assertThat(response.detalhesFalha()).hasSize(1);
        assertThat(response.detalhesFalha().get(0).reuniaoId()).isEqualTo(reuniaoComFalha.getId());
        assertThat(response.detalhesFalha().get(0).motivo()).isEqualTo("NOMINATIM_SEM_RESPOSTA");
        verify(itemService).processar(reuniaoComFalha.getId(), -23.5505, -46.6333);
        verify(itemService).processar(reuniaoComSucesso.getId(), -23.5505, -46.6333);
    }

    @Test
    void segundaChamadaDentroDaJanelaDeCincoMinutosEhBloqueadaPorRateLimit() {
        when(reuniaoRepository.findAllByCategoriaAndEstadoIsNullAndLatitudeIsNotNullAndLongitudeIsNotNull(CategoriaReuniao.presencial))
                .thenReturn(List.of());

        service.executar();

        assertThatThrownBy(() -> service.executar())
                .isInstanceOf(RateLimitedException.class)
                .hasFieldOrPropertyWithValue("code", "REUNIAO_BACKFILL_RATE_LIMITED");
    }
}
