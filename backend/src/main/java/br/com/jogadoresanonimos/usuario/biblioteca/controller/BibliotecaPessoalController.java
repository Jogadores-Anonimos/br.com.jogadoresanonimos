package br.com.jogadoresanonimos.usuario.biblioteca.controller;

import br.com.jogadoresanonimos.arquivo.dto.ArquivoResponse;
import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.security.AuthenticatedUser;
import br.com.jogadoresanonimos.usuario.biblioteca.service.BibliotecaPessoalService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/usuarios/me/biblioteca")
public class BibliotecaPessoalController {

    private final BibliotecaPessoalService bibliotecaPessoalService;

    public BibliotecaPessoalController(BibliotecaPessoalService bibliotecaPessoalService) {
        this.bibliotecaPessoalService = bibliotecaPessoalService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ArquivoResponse>>> listar(@AuthenticationPrincipal AuthenticatedUser principal) {
        return ResponseEntity.ok(ApiResponse.ok(bibliotecaPessoalService.listar(principal.id())));
    }

    @PostMapping("/{arquivoId}")
    public ResponseEntity<ApiResponse<MensagemResponse>> adicionar(@PathVariable UUID arquivoId,
                                                                     @AuthenticationPrincipal AuthenticatedUser principal) {
        bibliotecaPessoalService.adicionar(principal.id(), arquivoId);
        return ResponseEntity.ok(ApiResponse.ok(new MensagemResponse("Arquivo adicionado à biblioteca.")));
    }

    @DeleteMapping("/{arquivoId}")
    public ResponseEntity<ApiResponse<MensagemResponse>> remover(@PathVariable UUID arquivoId,
                                                                   @AuthenticationPrincipal AuthenticatedUser principal) {
        bibliotecaPessoalService.remover(principal.id(), arquivoId);
        return ResponseEntity.ok(ApiResponse.ok(new MensagemResponse("Arquivo removido da biblioteca.")));
    }
}
