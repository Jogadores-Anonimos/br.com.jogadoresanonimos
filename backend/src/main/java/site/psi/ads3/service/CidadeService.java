package site.psi.ads3.service;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import site.psi.ads3.dto.request.CidadeRequest;
import site.psi.ads3.dto.response.CidadeResponse;
import site.psi.ads3.entity.Cidade;
import site.psi.ads3.repository.CidadeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@Service
public class CidadeService {
    
    private final CidadeRepository cidadeRepository;

    public CidadeResponse criarCidade(CidadeRequest request) {
        Cidade cidade = new Cidade(request.nome(), request.estado());
        cidadeRepository.save(cidade);
        return CidadeResponse.fromEntity(cidade);
    }

    public Page<CidadeResponse> getAllCidades(Pageable pageable){
        return cidadeRepository.findAll(pageable).map(CidadeResponse::fromEntity);
    }

    public CidadeResponse getCidadeById(Long id) throws IllegalArgumentException {
        Cidade cidade = findCidadeById(id);
        return CidadeResponse.fromEntity(cidade);
    }

    public CidadeResponse updateCidade(Long id, CidadeRequest request) throws IllegalArgumentException {
        Cidade cidade = findCidadeById(id);
        request.updateCidade(cidade);
        cidadeRepository.save(cidade);
        return CidadeResponse.fromEntity(cidade);
    }

    public void deleteCidade(Long id) {
        cidadeRepository.deleteById(id);
    }

    private Cidade findCidadeById(Long id) {
        return cidadeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }
}