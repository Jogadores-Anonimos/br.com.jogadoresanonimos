package br.com.jogadoresanonimos.imagem.service;

import br.com.jogadoresanonimos.imagem.entity.Imagem;
import br.com.jogadoresanonimos.imagem.repository.ImagemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImagemOrfaGcJobTest {

    @Mock
    ImagemRepository imagemRepository;
    @Mock
    StorageClient storageClient;

    ImagemOrfaGcJob imagemOrfaGcJob;

    @BeforeEach
    void setUp() {
        imagemOrfaGcJob = new ImagemOrfaGcJob(imagemRepository, storageClient);
    }

    private Imagem imagemComId(UUID id) {
        Imagem imagem = new Imagem("original.jpg", "chave-" + UUID.randomUUID(), 1024, "alt", UUID.randomUUID());
        setField(imagem, "id", id);
        setField(imagem, "criadoEm", Instant.now());
        return imagem;
    }

    private void setField(Object alvo, String nomeCampo, Object valor) {
        try {
            Field campo = Imagem.class.getDeclaredField(nomeCampo);
            campo.setAccessible(true);
            campo.set(alvo, valor);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void executar_deveRemoverImagensOrfasRetornadasPelaQuery() {
        UUID id = UUID.randomUUID();
        Imagem imagemOrfa = imagemComId(id);
        when(imagemRepository.buscarIdsOrfaasAnterioresA(any())).thenReturn(List.of(id));
        when(imagemRepository.findAllById(List.of(id))).thenReturn(List.of(imagemOrfa));
        imagemOrfaGcJob.executar();
        verify(storageClient).delete(imagemOrfa.getChaveArquivo());
        verify(imagemRepository).delete(imagemOrfa);
    }

    @Test
    void executar_naoDeveFalharQuandoNaoHaImagensCandidatas() {
        when(imagemRepository.buscarIdsOrfaasAnterioresA(any())).thenReturn(List.of());
        when(imagemRepository.findAllById(List.of())).thenReturn(List.of());
        imagemOrfaGcJob.executar();
        verify(storageClient, never()).delete(any());
        verify(imagemRepository, never()).delete(any());
    }

    @Test
    void executar_deveRemoverTodasAsImagensRetornadasPelaQuery() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        Imagem imagem1 = imagemComId(id1);
        Imagem imagem2 = imagemComId(id2);
        when(imagemRepository.buscarIdsOrfaasAnterioresA(any())).thenReturn(List.of(id1, id2));
        when(imagemRepository.findAllById(List.of(id1, id2))).thenReturn(List.of(imagem1, imagem2));
        imagemOrfaGcJob.executar();
        verify(storageClient, times(1)).delete(imagem1.getChaveArquivo());
        verify(imagemRepository, times(1)).delete(imagem1);
        verify(storageClient, times(1)).delete(imagem2.getChaveArquivo());
        verify(imagemRepository, times(1)).delete(imagem2);
    }
}
