package br.com.jogadoresanonimos.imagem.controller;

import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.imagem.dto.ImagemResponse;
import br.com.jogadoresanonimos.imagem.service.ImagemService;
import br.com.jogadoresanonimos.security.AuthenticatedUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/imagens")
@PreAuthorize("hasRole('ADMIN')")
public class ImagemController {

    private final ImagemService imagemService;

    public ImagemController(ImagemService imagemService) {
        this.imagemService = imagemService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<ImagemResponse>> upload(@RequestParam("arquivo") MultipartFile arquivo,
                                                               @RequestParam("alt") String alt,
                                                               @AuthenticationPrincipal AuthenticatedUser principal) {
        ImagemResponse response = imagemService.upload(arquivo, alt, principal.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<MensagemResponse>> remover(@PathVariable UUID id) {
        imagemService.remover(id);
        return ResponseEntity.ok(ApiResponse.ok(new MensagemResponse("Imagem removida.")));
    }
}
