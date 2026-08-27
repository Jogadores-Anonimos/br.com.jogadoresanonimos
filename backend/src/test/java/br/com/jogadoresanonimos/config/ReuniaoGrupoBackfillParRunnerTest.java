package br.com.jogadoresanonimos.config;

import br.com.jogadoresanonimos.reuniao.service.ReuniaoGrupoBackfillParService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ReuniaoGrupoBackfillParRunnerTest {

    @Mock
    ReuniaoGrupoBackfillParService backfillService;

    @Test
    void modoDryRun_chamaServicoComDryRunVerdadeiro() throws Exception {
        ReuniaoGrupoBackfillParRunner runner = new ReuniaoGrupoBackfillParRunner(backfillService, "dry-run");

        runner.run(null);

        verify(backfillService).executar(true);
    }

    @Test
    void modoApply_chamaServicoComDryRunFalso() throws Exception {
        ReuniaoGrupoBackfillParRunner runner = new ReuniaoGrupoBackfillParRunner(backfillService, "apply");

        runner.run(null);

        verify(backfillService).executar(false);
    }

    @Test
    void modoVazio_naoChamaServico() throws Exception {
        ReuniaoGrupoBackfillParRunner runner = new ReuniaoGrupoBackfillParRunner(backfillService, "");

        runner.run(null);

        verifyNoInteractions(backfillService);
    }

    @Test
    void modoInvalido_lancaExcecaoDeBoot() {
        ReuniaoGrupoBackfillParRunner runner = new ReuniaoGrupoBackfillParRunner(backfillService, "invalido");

        assertThatThrownBy(() -> runner.run(null)).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(backfillService);
    }

    @Test
    void falhaNaExecucaoDoBackfill_naoDerrubaOBoot() throws Exception {
        ReuniaoGrupoBackfillParRunner runner = new ReuniaoGrupoBackfillParRunner(backfillService, "apply");
        doThrow(new RuntimeException("falha simulada")).when(backfillService).executar(false);

        assertThatCode(() -> runner.run(null)).doesNotThrowAnyException();
        verify(backfillService).executar(false);
    }
}
