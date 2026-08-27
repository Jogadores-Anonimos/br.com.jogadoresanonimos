package br.com.jogadoresanonimos.reuniao.service;

import br.com.jogadoresanonimos.escritorio.entity.GrupoIntergrupo;
import br.com.jogadoresanonimos.escritorio.repository.GrupoIntergrupoRepository;
import br.com.jogadoresanonimos.reuniao.entity.CategoriaReuniao;
import br.com.jogadoresanonimos.reuniao.entity.Reuniao;
import br.com.jogadoresanonimos.reuniao.repository.ReuniaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReuniaoGrupoBackfillParService {

    private static final Logger log = LoggerFactory.getLogger(ReuniaoGrupoBackfillParService.class);

    private final ReuniaoRepository reuniaoRepository;
    private final GrupoIntergrupoRepository grupoRepository;
    private final ReuniaoGrupoBackfillParItemService itemService;

    public ReuniaoGrupoBackfillParService(ReuniaoRepository reuniaoRepository,
                                           GrupoIntergrupoRepository grupoRepository,
                                           ReuniaoGrupoBackfillParItemService itemService) {
        this.reuniaoRepository = reuniaoRepository;
        this.grupoRepository = grupoRepository;
        this.itemService = itemService;
    }

    public ResultadoBackfillParReuniaoGrupo executar(boolean dryRun) {
        List<Reuniao> reuniaoOrfas = reuniaoRepository
                .findAllByCategoriaAndGrupoIntergrupoIsNull(CategoriaReuniao.presencial);
        List<GrupoIntergrupo> grupoOrfaos = grupoRepository.buscarOrfaosSemReuniao();
        List<UUID> reuniaoIdsOrfasDetectadas = reuniaoOrfas.stream().map(Reuniao::getId).toList();
        List<UUID> grupoIdsOrfaosDetectados = grupoOrfaos.stream().map(GrupoIntergrupo::getId).toList();
        List<UUID> grupoIdsCriados = dryRun ? List.of() : criarGrupos(reuniaoIdsOrfasDetectadas);
        List<UUID> reuniaoIdsCriadas = dryRun ? List.of() : criarReunioes(grupoIdsOrfaosDetectados);
        ResultadoBackfillParReuniaoGrupo resultado = new ResultadoBackfillParReuniaoGrupo(dryRun,
                reuniaoIdsOrfasDetectadas, grupoIdsOrfaosDetectados, grupoIdsCriados, reuniaoIdsCriadas);
        log.info("backfill_par_reuniao_grupo_concluido modo={} reuniaoOrfasDetectadas={} grupoOrfaosDetectados={} "
                        + "grupoIdsCriados={} reuniaoIdsCriadas={}",
                dryRun ? "dry-run" : "apply", reuniaoIdsOrfasDetectadas.size(), grupoIdsOrfaosDetectados.size(),
                grupoIdsCriados, reuniaoIdsCriadas);
        return resultado;
    }

    private List<UUID> criarGrupos(List<UUID> reuniaoIds) {
        return reuniaoIds.stream()
                .map(itemService::criarGrupoParaReuniaoOrfaSeAindaOrfa)
                .flatMap(Optional::stream)
                .toList();
    }

    private List<UUID> criarReunioes(List<UUID> grupoIds) {
        return grupoIds.stream()
                .map(itemService::criarReuniaoParaGrupoOrfaoSeAindaOrfao)
                .flatMap(Optional::stream)
                .toList();
    }
}
