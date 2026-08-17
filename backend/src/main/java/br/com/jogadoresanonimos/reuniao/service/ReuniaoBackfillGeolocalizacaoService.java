package br.com.jogadoresanonimos.reuniao.service;

import br.com.jogadoresanonimos.reuniao.dto.BackfillGeolocalizacaoResponse;
import br.com.jogadoresanonimos.reuniao.entity.CategoriaReuniao;
import br.com.jogadoresanonimos.reuniao.entity.Reuniao;
import br.com.jogadoresanonimos.reuniao.repository.ReuniaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReuniaoBackfillGeolocalizacaoService {

    private static final Logger log = LoggerFactory.getLogger(ReuniaoBackfillGeolocalizacaoService.class);
    private static final long INTERVALO_ENTRE_CHAMADAS_MS = 1000;

    private final ReuniaoRepository reuniaoRepository;
    private final ReuniaoBackfillGeolocalizacaoItemService itemService;

    public ReuniaoBackfillGeolocalizacaoService(ReuniaoRepository reuniaoRepository,
                                                 ReuniaoBackfillGeolocalizacaoItemService itemService) {
        this.reuniaoRepository = reuniaoRepository;
        this.itemService = itemService;
    }

    public BackfillGeolocalizacaoResponse executar() {
        List<Reuniao> elegiveis = reuniaoRepository
                .findAllByCategoriaAndEstadoIsNullAndLatitudeIsNotNullAndLongitudeIsNotNull(CategoriaReuniao.presencial);
        int atualizadas = 0;
        List<BackfillGeolocalizacaoResponse.FalhaItem> falhas = new ArrayList<>();
        for (int i = 0; i < elegiveis.size(); i++) {
            Reuniao reuniao = elegiveis.get(i);
            Optional<String> motivoFalha = itemService.processar(reuniao.getId(), reuniao.getLatitude(), reuniao.getLongitude());
            if (motivoFalha.isPresent()) {
                falhas.add(new BackfillGeolocalizacaoResponse.FalhaItem(reuniao.getId(), motivoFalha.get()));
            } else {
                atualizadas++;
            }
            if (i < elegiveis.size() - 1) {
                aguardarRateLimit();
            }
        }
        log.info("reuniao_backfill_geolocalizacao_concluido totalElegiveis={} atualizadas={} falhas={}",
                elegiveis.size(), atualizadas, falhas.size());
        return new BackfillGeolocalizacaoResponse(elegiveis.size(), atualizadas, falhas.size(), falhas);
    }

    private void aguardarRateLimit() {
        try {
            Thread.sleep(INTERVALO_ENTRE_CHAMADAS_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
