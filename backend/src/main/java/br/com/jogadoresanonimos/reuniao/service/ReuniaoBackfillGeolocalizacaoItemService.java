package br.com.jogadoresanonimos.reuniao.service;

import br.com.jogadoresanonimos.escritorio.entity.UnidadeFederativa;
import br.com.jogadoresanonimos.reuniao.client.NominatimEnderecoResponse;
import br.com.jogadoresanonimos.reuniao.client.NominatimReverseGeocodingClient;
import br.com.jogadoresanonimos.reuniao.entity.Reuniao;
import br.com.jogadoresanonimos.reuniao.repository.ReuniaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class ReuniaoBackfillGeolocalizacaoItemService {

    private final ReuniaoRepository reuniaoRepository;
    private final NominatimReverseGeocodingClient nominatimClient;

    public ReuniaoBackfillGeolocalizacaoItemService(ReuniaoRepository reuniaoRepository,
                                                      NominatimReverseGeocodingClient nominatimClient) {
        this.reuniaoRepository = reuniaoRepository;
        this.nominatimClient = nominatimClient;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<String> processar(UUID reuniaoId, double latitude, double longitude) {
        Optional<NominatimEnderecoResponse> resposta = nominatimClient.reverseGeocode(latitude, longitude);
        if (resposta.isEmpty()) {
            return Optional.of("NOMINATIM_SEM_RESPOSTA");
        }
        if (resposta.get().address() == null) {
            return Optional.of("NOMINATIM_ENDERECO_INCOMPLETO");
        }
        NominatimEnderecoResponse.NominatimAddress address = resposta.get().address();
        Optional<UnidadeFederativa> uf = UnidadeFederativa.deNomeCompleto(address.state());
        if (uf.isEmpty()) {
            return Optional.of("NOMINATIM_ESTADO_NAO_MAPEADO");
        }
        Reuniao reuniao = reuniaoRepository.findById(reuniaoId).orElse(null);
        if (reuniao == null || reuniao.getEstado() != null) {
            return Optional.of("REUNIAO_JA_PROCESSADA_OU_REMOVIDA");
        }
        reuniao.preencherEnderecoGeocodificado(uf.get().name(), address.cidadeResolvida(), address.bairroResolvido());
        reuniaoRepository.save(reuniao);
        return Optional.empty();
    }
}
