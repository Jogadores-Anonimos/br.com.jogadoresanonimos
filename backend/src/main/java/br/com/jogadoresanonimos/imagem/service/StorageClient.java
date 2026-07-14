package br.com.jogadoresanonimos.imagem.service;

/**
 * Abstração de storage de arquivo/imagem. Implementação de produção é
 * disco local persistente (decisão 2026-07-12, ver LocalDiskStorageClient
 * e contrato-auth-cms-upload.md) — plano anterior de bucket S3-compatible
 * externo (R2/B2) foi descartado.
 */
public interface StorageClient {

    void upload(String chave, byte[] conteudo, String contentType);

    void delete(String chave);

    String urlPublicaPara(String chave);

    byte[] download(String chave);
}
