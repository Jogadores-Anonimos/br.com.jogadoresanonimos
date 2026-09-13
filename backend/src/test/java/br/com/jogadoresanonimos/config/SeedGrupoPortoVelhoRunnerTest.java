package br.com.jogadoresanonimos.config;

import br.com.jogadoresanonimos.escritorio.dto.CriarEnderecoGrupoRequest;
import br.com.jogadoresanonimos.escritorio.entity.EstadoIntergrupo;
import br.com.jogadoresanonimos.escritorio.entity.RegiaoCodigo;
import br.com.jogadoresanonimos.escritorio.entity.RegiaoIntergrupo;
import br.com.jogadoresanonimos.escritorio.entity.UnidadeFederativa;
import br.com.jogadoresanonimos.escritorio.repository.EstadoIntergrupoRepository;
import br.com.jogadoresanonimos.escritorio.repository.RegiaoIntergrupoRepository;
import br.com.jogadoresanonimos.escritorio.service.EscritorioRegionalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeedGrupoPortoVelhoRunnerTest {

    @Mock
    EstadoIntergrupoRepository estadoRepository;

    @Mock
    RegiaoIntergrupoRepository regiaoRepository;

    @Mock
    EscritorioRegionalService escritorioService;

    @Test
    void desabilitado_naoChamaNadaDeNinguem() throws Exception {
        SeedGrupoPortoVelhoRunner runner = new SeedGrupoPortoVelhoRunner(estadoRepository, regiaoRepository,
                escritorioService, false);

        runner.run(null);

        verifyNoInteractions(estadoRepository, regiaoRepository, escritorioService);
    }

    @Test
    void estadoRoJaExiste_naoCriaGrupoDeNovo() throws Exception {
        when(estadoRepository.findByUf(UnidadeFederativa.RO))
                .thenReturn(Optional.of(mock(EstadoIntergrupo.class)));
        SeedGrupoPortoVelhoRunner runner = new SeedGrupoPortoVelhoRunner(estadoRepository, regiaoRepository,
                escritorioService, true);

        runner.run(null);

        verifyNoInteractions(regiaoRepository, escritorioService);
    }

    @Test
    void habilitadoSemEstadoRo_criaGrupoNaRegiaoNorte() throws Exception {
        UUID regiaoId = UUID.randomUUID();
        RegiaoIntergrupo regiaoNorte = new RegiaoIntergrupo("Norte", RegiaoCodigo.NORTE, 0);
        setId(regiaoNorte, regiaoId);
        when(estadoRepository.findByUf(UnidadeFederativa.RO)).thenReturn(Optional.empty());
        when(regiaoRepository.findByCodigo(RegiaoCodigo.NORTE)).thenReturn(Optional.of(regiaoNorte));
        SeedGrupoPortoVelhoRunner runner = new SeedGrupoPortoVelhoRunner(estadoRepository, regiaoRepository,
                escritorioService, true);

        runner.run(null);

        verify(escritorioService).criarEnderecoGrupo(eq(regiaoId), any(CriarEnderecoGrupoRequest.class));
    }

    @Test
    void regiaoNorteNaoEncontrada_naoDerrubaOBoot() throws Exception {
        when(estadoRepository.findByUf(UnidadeFederativa.RO)).thenReturn(Optional.empty());
        when(regiaoRepository.findByCodigo(RegiaoCodigo.NORTE)).thenReturn(Optional.empty());
        SeedGrupoPortoVelhoRunner runner = new SeedGrupoPortoVelhoRunner(estadoRepository, regiaoRepository,
                escritorioService, true);

        assertThatCode(() -> runner.run(null)).doesNotThrowAnyException();

        verify(escritorioService, never()).criarEnderecoGrupo(any(), any());
    }

    private static void setId(RegiaoIntergrupo regiao, UUID id) throws Exception {
        var campo = RegiaoIntergrupo.class.getDeclaredField("id");
        campo.setAccessible(true);
        campo.set(regiao, id);
    }
}
