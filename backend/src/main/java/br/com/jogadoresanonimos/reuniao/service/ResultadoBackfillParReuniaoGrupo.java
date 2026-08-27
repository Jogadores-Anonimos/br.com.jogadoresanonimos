package br.com.jogadoresanonimos.reuniao.service;

import java.util.List;
import java.util.UUID;

public record ResultadoBackfillParReuniaoGrupo(
        boolean dryRun,
        List<UUID> reuniaoIdsOrfasDetectadas,
        List<UUID> grupoIdsOrfaosDetectados,
        List<UUID> grupoIdsCriados,
        List<UUID> reuniaoIdsCriadas
) {
}
