package br.com.jogadoresanonimos.config;

import br.com.jogadoresanonimos.reuniao.service.ReuniaoGrupoBackfillParService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ReuniaoGrupoBackfillParRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(ReuniaoGrupoBackfillParRunner.class);
    private static final String MODO_DRY_RUN = "dry-run";
    private static final String MODO_APPLY = "apply";

    private final ReuniaoGrupoBackfillParService backfillService;
    private final String modo;

    public ReuniaoGrupoBackfillParRunner(ReuniaoGrupoBackfillParService backfillService,
                                          @Value("${app.backfill.par-reuniao-grupo:}") String modo) {
        this.backfillService = backfillService;
        this.modo = modo;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (modo == null || modo.isBlank()) {
            return;
        }
        if (!MODO_DRY_RUN.equals(modo) && !MODO_APPLY.equals(modo)) {
            throw new IllegalStateException(
                    "BACKFILL_PAR inválido: valores aceitos são 'dry-run' ou 'apply'");
        }
        try {
            backfillService.executar(MODO_DRY_RUN.equals(modo));
        } catch (RuntimeException e) {
            log.error("backfill_par_reuniao_grupo_falhou modo={} tipoErro={}", modo, e.getClass().getSimpleName());
        }
    }
}
