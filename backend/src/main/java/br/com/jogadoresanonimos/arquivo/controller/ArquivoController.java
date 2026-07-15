package br.com.jogadoresanonimos.arquivo.controller;

import br.com.jogadoresanonimos.arquivo.dto.ArquivoResponse;
import br.com.jogadoresanonimos.arquivo.dto.EditarArquivoRequest;
import br.com.jogadoresanonimos.arquivo.entity.Arquivo;
import br.com.jogadoresanonimos.arquivo.service.ArquivoService;
import br.com.jogadoresanonimos.auth.dto.MensagemResponse;
import br.com.jogadoresanonimos.common.dto.ApiResponse;
import br.com.jogadoresanonimos.imagem.service.StorageClient;
import br.com.jogadoresanonimos.security.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
public class ArquivoController {

    private final ArquivoService arquivoService;
    private final StorageClient storageClient;

    public ArquivoController(ArquivoService arquivoService, StorageClient storageClient) {
        this.arquivoService = arquivoService;
        this.storageClient = storageClient;
    }

    @GetMapping("/api/v1/arquivos")
    public ResponseEntity<ApiResponse<List<ArquivoResponse>>> listarPublico() {
        return ResponseEntity.ok(ApiResponse.ok(arquivoService.listarTodos()));
    }

    @GetMapping("/api/v1/admin/arquivos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ArquivoResponse>>> listarTodos() {
        return ResponseEntity.ok(ApiResponse.ok(arquivoService.listarTodos()));
    }

    @PostMapping(value = "/api/v1/admin/arquivos", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ArquivoResponse>> upload(@RequestParam("arquivo") MultipartFile arquivo,
                                                                @RequestParam("titulo") String titulo,
                                                                @RequestParam(value = "descricao", required = false) String descricao,
                                                                @AuthenticationPrincipal AuthenticatedUser principal) {
        ArquivoResponse response = arquivoService.upload(arquivo, titulo, descricao, principal.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PutMapping("/api/v1/admin/arquivos/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ArquivoResponse>> editar(@PathVariable UUID id,
                                                                 @Valid @RequestBody EditarArquivoRequest request) {
        ArquivoResponse response = arquivoService.editar(id, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/api/v1/admin/arquivos/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MensagemResponse>> remover(@PathVariable UUID id) {
        arquivoService.remover(id);
        return ResponseEntity.ok(ApiResponse.ok(new MensagemResponse("Arquivo removido.")));
    }

    @GetMapping("/api/v1/arquivos/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable UUID id) {
        Arquivo arquivo = arquivoService.buscarParaDownload(id);
        byte[] conteudo = storageClient.download(arquivo.getChaveArquivo());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomeSeguro(arquivo.getNomeOriginal()) + "\"")
                .header("X-Content-Type-Options", "nosniff")
                .body(conteudo);
    }

    private String nomeSeguro(String nomeOriginal) {
        if (nomeOriginal == null) {
            return "arquivo.pdf";
        }
        return nomeOriginal.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
