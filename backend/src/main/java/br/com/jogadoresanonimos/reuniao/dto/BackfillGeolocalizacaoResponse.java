package br.com.jogadoresanonimos.reuniao.dto;

import java.util.List;
import java.util.UUID;

public record BackfillGeolocalizacaoResponse(int totalElegiveis, int atualizadas, int falhas,
                                               List<FalhaItem> detalhesFalha) {

    public record FalhaItem(UUID reuniaoId, String motivo) {
    }
}
