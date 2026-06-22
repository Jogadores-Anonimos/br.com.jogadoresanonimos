package site.psi.ads3.controller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import site.psi.ads3.dto.request.CidadeRequest;
import site.psi.ads3.dto.response.CidadeResponse;
import site.psi.ads3.service.CidadeService;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/cidades")
public class CidadeController {
    
    private final CidadeService cidadeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CidadeResponse criarCidade(@RequestBody CidadeRequest cidadeRequest) {
        return cidadeService.criarCidade(cidadeRequest);
    }

    @GetMapping
    public Page<CidadeResponse> getAllCidades(Pageable pageable) {
        return cidadeService.getAllCidades(pageable);
    }

    @GetMapping("/{id}")
    public CidadeResponse getCidadeById(@PathVariable Long id) {
        return cidadeService.getCidadeById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CidadeResponse updateCidade(@PathVariable Long id, @RequestBody CidadeRequest request) {
        return cidadeService.updateCidade(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCidade(@PathVariable Long id) {
        cidadeService.deleteCidade(id);
    }
}