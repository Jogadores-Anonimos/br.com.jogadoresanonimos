package site.psi.ads3.controller;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import site.psi.ads3.dto.request.ReuniaoRequest;
import site.psi.ads3.dto.response.ReuniaoResponse;
import site.psi.ads3.entity.Reuniao;
import site.psi.ads3.service.ReuniaoService;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/reunioes")
public class ReuniaoController {

    private final ReuniaoService reuniaoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReuniaoResponse criarReuniao(@RequestBody ReuniaoRequest request) {
        return reuniaoService.criarReuniao(request);
    }

    @GetMapping
    public Page<ReuniaoResponse> getAllReunioes(Pageable pageable) {
        return reuniaoService.getAllReunioes(pageable);
    }

    @GetMapping("/{id}")
    public ReuniaoResponse getReuniaoById(@PathVariable Long id) {
        return reuniaoService.getReuniaoById(id);
    }

    @PutMapping("/{id}")
    public ReuniaoResponse updateReuniao(@PathVariable Long id, @RequestBody ReuniaoRequest request) {
        return reuniaoService.updateReuniao(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReuniao(@PathVariable Long id) {
        reuniaoService.deleteReuniao(id);
    }

     @GetMapping("/hoje")
    public List<Reuniao> listarHoje() {
        return reuniaoService.listarHoje();
    }

    @GetMapping("/semana")
    public List<Reuniao> listarSemana() {
        return reuniaoService.listarSemana();
    }
}