package br.com.jogadoresanonimos.imagem.controller;

import br.com.jogadoresanonimos.common.exception.ErrorCode;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.imagem.service.StorageClient;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
public class ImagemPublicController {

    private final StorageClient storageClient;

    public ImagemPublicController(StorageClient storageClient) {
        this.storageClient = storageClient;
    }

    @GetMapping("/stub-storage/{chave}")
    public ResponseEntity<byte[]> servir(@PathVariable String chave) {
        if (chave.contains("/") || chave.contains("..")) {
            throw new RecursoNaoEncontradoException(ErrorCode.ARQUIVO_NAO_ENCONTRADO, "Arquivo não encontrado");
        }
        byte[] conteudo = storageClient.download(chave);
        return ResponseEntity.ok()
                .contentType(inferirContentType(chave))
                .header(HttpHeaders.CACHE_CONTROL, CacheControl.maxAge(Duration.ofDays(1)).getHeaderValue())
                .header("X-Content-Type-Options", "nosniff")
                .body(conteudo);
    }

    private MediaType inferirContentType(String chave) {
        if (chave.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        }
        return MediaType.IMAGE_JPEG;
    }
}
