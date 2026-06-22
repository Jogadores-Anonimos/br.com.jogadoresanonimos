package site.psi.ads3.service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import site.psi.ads3.dto.request.ReuniaoRequest;
import site.psi.ads3.dto.response.ReuniaoResponse;
import site.psi.ads3.entity.Cidade;
import site.psi.ads3.entity.Reuniao;
import site.psi.ads3.repository.CidadeRepository;
import site.psi.ads3.repository.ReuniaoRepository;

@AllArgsConstructor
@Service
public class ReuniaoService {

    private final ReuniaoRepository reuniaoRepository;
    private final CidadeRepository cidadeRepository;

    public ReuniaoResponse criarReuniao(ReuniaoRequest request) {
        Cidade cidade = cidadeRepository.findById(request.idCidade()).orElseThrow(() -> new IllegalArgumentException("Cidade não encontrada"));
        Reuniao reuniao = new Reuniao(request.titulo(), request.endereco(), cidade, request.dataHora());
        if(request.descricao() != null) reuniao.setDescricao(request.descricao());
        reuniaoRepository.save(reuniao);
        return ReuniaoResponse.fromEntity(reuniao);
    }

    public Page<ReuniaoResponse> getAllReunioes(Pageable pageable){
        return reuniaoRepository.findAll(pageable).map(ReuniaoResponse::fromEntity);
    }

    public ReuniaoResponse getReuniaoById(Long id) throws IllegalArgumentException {
        Reuniao reuniao = findReuniaoById(id);
        return ReuniaoResponse.fromEntity(reuniao);
    }

    public ReuniaoResponse updateReuniao(Long id, ReuniaoRequest request) throws IllegalArgumentException {
        Reuniao reuniao = findReuniaoById(id);
        request.updateReuniao(reuniao);
        reuniaoRepository.save(reuniao);
        return ReuniaoResponse.fromEntity(reuniao);
    }

    public void deleteReuniao(Long id) {
        reuniaoRepository.deleteById(id);
    }

    private Reuniao findReuniaoById(Long id) {
        return reuniaoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Reunião não encontrada"));
    }

     public List<Reuniao> listarHoje() {
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicio = hoje.atStartOfDay();
        LocalDateTime fim = hoje.atTime(23, 59, 59);
        return reuniaoRepository.findByDataHoraBetween(inicio, fim);
    }

    public List<Reuniao> listarSemana() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioSemana = hoje.with(java.time.DayOfWeek.MONDAY);
        LocalDate fimSemana = hoje.with(java.time.DayOfWeek.SUNDAY);
        return reuniaoRepository.findByDataHoraBetween(inicioSemana.atStartOfDay(), fimSemana.atTime(23, 59, 59));
    }
}