package br.com.jogadoresanonimos.config;

import br.com.jogadoresanonimos.escritorio.dto.AgendaItemRequest;
import br.com.jogadoresanonimos.escritorio.dto.CriarEnderecoGrupoRequest;
import br.com.jogadoresanonimos.escritorio.entity.UnidadeFederativa;
import br.com.jogadoresanonimos.escritorio.repository.EstadoIntergrupoRepository;
import br.com.jogadoresanonimos.escritorio.repository.RegiaoIntergrupoRepository;
import br.com.jogadoresanonimos.escritorio.service.EscritorioRegionalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeedGrupoPortoVelhoRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SeedGrupoPortoVelhoRunner.class);

    private final EstadoIntergrupoRepository estadoRepository;
    private final RegiaoIntergrupoRepository regiaoRepository;
    private final EscritorioRegionalService escritorioService;
    private final boolean habilitado;

    public SeedGrupoPortoVelhoRunner(EstadoIntergrupoRepository estadoRepository,
                                      RegiaoIntergrupoRepository regiaoRepository,
                                      EscritorioRegionalService escritorioService,
                                      @Value("${app.seed.grupo-porto-velho:false}") boolean habilitado) {
        this.estadoRepository = estadoRepository;
        this.regiaoRepository = regiaoRepository;
        this.escritorioService = escritorioService;
        this.habilitado = habilitado;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!habilitado) {
            return;
        }
        if (estadoRepository.findByUf(UnidadeFederativa.RO).isPresent()) {
            log.info("seed_grupo_porto_velho_ignorado motivo=estado_ro_ja_existe");
            return;
        }
        try {
            var regiaoNorte = regiaoRepository.findByCodigo(UnidadeFederativa.RO.getCodigoRegiao())
                    .orElseThrow(() -> new IllegalStateException(
                            "Região Norte não encontrada; seed do Grupo Porto Velho abortado"));
            CriarEnderecoGrupoRequest request = new CriarEnderecoGrupoRequest("RO",
                    "Grupo Porto Velho (reunião aberta de apresentação - endereço regular em breve)",
                    "Rua José Bonifácio, 351 - Auditório D. João Batista Costa - Catedral, Caiari, Porto Velho - RO",
                    "Caiari", "Porto Velho",
                    List.of(new AgendaItemRequest("SEGUNDA", "19:30h às 21:30h")),
                    List.of());
            escritorioService.criarEnderecoGrupo(regiaoNorte.getId(), request);
            log.info("seed_grupo_porto_velho_concluido");
        } catch (RuntimeException e) {
            log.error("seed_grupo_porto_velho_falhou tipoErro={}", e.getClass().getSimpleName());
        }
    }
}
