package br.com.jogadoresanonimos.reuniao.service;

import br.com.jogadoresanonimos.reuniao.client.NominatimEnderecoResponse;
import br.com.jogadoresanonimos.reuniao.client.NominatimReverseGeocodingClient;
import br.com.jogadoresanonimos.reuniao.entity.CategoriaReuniao;
import br.com.jogadoresanonimos.reuniao.entity.Reuniao;
import br.com.jogadoresanonimos.reuniao.repository.ReuniaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReuniaoBackfillGeolocalizacaoItemServiceTest {

    @Mock
    ReuniaoRepository reuniaoRepository;

    @Mock
    NominatimReverseGeocodingClient nominatimClient;

    ReuniaoBackfillGeolocalizacaoItemService itemService;

    @BeforeEach
    void setUp() {
        itemService = new ReuniaoBackfillGeolocalizacaoItemService(reuniaoRepository, nominatimClient);
    }

    private Reuniao reuniaoPresencialSemEndereco() {
        return new Reuniao("Grupo Centro", CategoriaReuniao.presencial, "descricao", "Segunda-feira",
                "19h30", "Rua das Flores, 123", null, null, null,
                -23.5505, -46.6333, null, null, null, null, null, null, null);
    }

    private NominatimEnderecoResponse respostaValida(String estado, String cidade, String bairro) {
        return new NominatimEnderecoResponse(
                new NominatimEnderecoResponse.NominatimAddress(estado, cidade, null, null, null, bairro, null, null));
    }

    @Test
    void geocodificaEPreencheEstadoCidadeBairroSemAlterarLocalOuCoordenadas() {
        Reuniao reuniao = reuniaoPresencialSemEndereco();
        when(nominatimClient.reverseGeocode(-23.5505, -46.6333))
                .thenReturn(Optional.of(respostaValida("São Paulo", "São Paulo", "Centro")));
        when(reuniaoRepository.findById(reuniao.getId())).thenReturn(Optional.of(reuniao));

        Optional<String> falha = itemService.processar(reuniao.getId(), -23.5505, -46.6333);

        assertThat(falha).isEmpty();
        assertThat(reuniao.getEstado()).isEqualTo("SP");
        assertThat(reuniao.getCidade()).isEqualTo("São Paulo");
        assertThat(reuniao.getBairro()).isEqualTo("Centro");
        assertThat(reuniao.getLocal()).isEqualTo("Rua das Flores, 123");
        assertThat(reuniao.getLatitude()).isEqualTo(-23.5505);
        assertThat(reuniao.getLongitude()).isEqualTo(-46.6333);
        verify(reuniaoRepository).save(reuniao);
    }

    @Test
    void semRespostaDoNominatimRetornaFalhaSemAlterarReuniao() {
        when(nominatimClient.reverseGeocode(-23.5505, -46.6333)).thenReturn(Optional.empty());

        Optional<String> falha = itemService.processar(java.util.UUID.randomUUID(), -23.5505, -46.6333);

        assertThat(falha).contains("NOMINATIM_SEM_RESPOSTA");
        verifyNoInteractions(reuniaoRepository);
    }

    @Test
    void respostaSemAddressRetornaFalhaEnderecoIncompleto() {
        when(nominatimClient.reverseGeocode(-23.5505, -46.6333))
                .thenReturn(Optional.of(new NominatimEnderecoResponse(null)));

        Optional<String> falha = itemService.processar(java.util.UUID.randomUUID(), -23.5505, -46.6333);

        assertThat(falha).contains("NOMINATIM_ENDERECO_INCOMPLETO");
        verifyNoInteractions(reuniaoRepository);
    }

    @Test
    void estadoSemMapeamentoDeUfRetornaFalhaSemAlterarReuniao() {
        when(nominatimClient.reverseGeocode(-23.5505, -46.6333))
                .thenReturn(Optional.of(respostaValida("Estado Inexistente", "Cidade X", "Bairro Y")));

        Optional<String> falha = itemService.processar(java.util.UUID.randomUUID(), -23.5505, -46.6333);

        assertThat(falha).contains("NOMINATIM_ESTADO_NAO_MAPEADO");
        verifyNoInteractions(reuniaoRepository);
    }

    @Test
    void reuniaoJaPreenchidaConcorrentementeNaoEhSobrescrita() {
        Reuniao reuniao = reuniaoPresencialSemEndereco();
        reuniao.preencherEnderecoGeocodificado("RJ", "Rio de Janeiro", "Centro");
        when(nominatimClient.reverseGeocode(-23.5505, -46.6333))
                .thenReturn(Optional.of(respostaValida("São Paulo", "São Paulo", "Centro")));
        when(reuniaoRepository.findById(reuniao.getId())).thenReturn(Optional.of(reuniao));

        Optional<String> falha = itemService.processar(reuniao.getId(), -23.5505, -46.6333);

        assertThat(falha).contains("REUNIAO_JA_PROCESSADA_OU_REMOVIDA");
        assertThat(reuniao.getEstado()).isEqualTo("RJ");
        verify(reuniaoRepository, never()).save(any());
    }
}
