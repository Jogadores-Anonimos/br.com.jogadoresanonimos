package br.com.jogadoresanonimos.reuniao.service;

import br.com.jogadoresanonimos.common.exception.ErrorCode;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.escritorio.entity.EstadoIntergrupo;
import br.com.jogadoresanonimos.escritorio.entity.GrupoIntergrupo;
import br.com.jogadoresanonimos.escritorio.entity.UnidadeFederativa;
import br.com.jogadoresanonimos.escritorio.repository.EstadoIntergrupoRepository;
import br.com.jogadoresanonimos.escritorio.repository.GrupoIntergrupoRepository;
import br.com.jogadoresanonimos.reuniao.entity.Reuniao;
import br.com.jogadoresanonimos.reuniao.repository.ReuniaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class ReuniaoGrupoBackfillParItemService {

    private final ReuniaoRepository reuniaoRepository;
    private final GrupoIntergrupoRepository grupoRepository;
    private final EstadoIntergrupoRepository estadoRepository;
    private final ReuniaoGrupoSincronizacaoService sincronizacaoService;

    public ReuniaoGrupoBackfillParItemService(ReuniaoRepository reuniaoRepository,
                                               GrupoIntergrupoRepository grupoRepository,
                                               EstadoIntergrupoRepository estadoRepository,
                                               ReuniaoGrupoSincronizacaoService sincronizacaoService) {
        this.reuniaoRepository = reuniaoRepository;
        this.grupoRepository = grupoRepository;
        this.estadoRepository = estadoRepository;
        this.sincronizacaoService = sincronizacaoService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<UUID> criarGrupoParaReuniaoOrfaSeAindaOrfa(UUID reuniaoId) {
        Reuniao reuniao = reuniaoRepository.buscarPorIdComLock(reuniaoId).orElse(null);
        if (reuniao == null || reuniao.getGrupoIntergrupo() != null) {
            return Optional.empty();
        }
        UnidadeFederativa uf = UnidadeFederativa.valueOf(reuniao.getEstado());
        EstadoIntergrupo estado = estadoRepository.findByUf(uf)
                .orElseThrow(() -> new RecursoNaoEncontradoException(ErrorCode.ESCRITORIO_ESTADO_NAO_ENCONTRADO,
                        "Estado do grupo intergrupo não encontrado"));
        int ordem = grupoRepository.buscarMaiorOrdemPorEstadoId(estado.getId()) + 1;
        GrupoIntergrupo grupo = sincronizacaoService.criarGrupoParaReuniaoOrfa(reuniao, estado, ordem);
        return Optional.of(grupo.getId());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<UUID> criarReuniaoParaGrupoOrfaoSeAindaOrfao(UUID grupoId) {
        GrupoIntergrupo grupo = grupoRepository.buscarPorIdComLock(grupoId).orElse(null);
        if (grupo == null || reuniaoRepository.findByGrupoIntergrupoId(grupo.getId()).isPresent()) {
            return Optional.empty();
        }
        DadosEnderecoReuniao dadosEndereco = new DadosEnderecoReuniao(null, null, null, grupo.getBairro(),
                grupo.getCidade(), grupo.getEstado().getUf().name(), null, null, false);
        Reuniao reuniao = sincronizacaoService.criarReuniaoCanonica(grupo, dadosEndereco);
        return Optional.of(reuniao.getId());
    }
}
