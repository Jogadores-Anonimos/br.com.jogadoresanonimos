package br.com.jogadoresanonimos.imagem.service;

public interface StorageClient {

    void upload(String chave, byte[] conteudo, String contentType);

    void delete(String chave);

    String urlPublicaPara(String chave);

    byte[] download(String chave);
}
