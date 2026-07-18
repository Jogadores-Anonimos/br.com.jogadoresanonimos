package br.com.jogadoresanonimos.imagem.service;

import br.com.jogadoresanonimos.imagem.repository.ImagemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Test
    void executar_deveRemoverImagensOrfasRetornadasPelaQuery() {
        UUID id = UUID.randomUUID();
        String chaveArquivo = "chave-" + id;
        when(imagemRepository.buscarIdsOrfaasAnterioresA(any())).thenReturn(List.of(id));
        when(imagemRepository.findIdEChavePorIdIn(List.of(id)))
                .thenReturn(List.<Object[]>of(new Object[]{id, chaveArquivo}));
        imagemOrfaGcJob.executar();
        verify(storageClient).delete(chaveArquivo);
        verify(imagemRepository).deleteAllByIdInBatch(List.of(id));
    }

    @Test
    void executar_naoDeveFalharQuandoNaoHaImagensCandidatas() {
        when(imagemRepository.buscarIdsOrfaasAnterioresA(any())).thenReturn(List.of());
        when(imagemRepository.findIdEChavePorIdIn(List.of())).thenReturn(List.of());
        imagemOrfaGcJob.executar();
        verify(storageClient, never()).delete(any());
        verify(imagemRepository, never()).deleteAllByIdInBatch(any());
    }

    @Test
    void executar_deveRemoverTodasAsImagensRetornadasPelaQuery() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        String chave1 = "chave-" + id1;
        String chave2 = "chave-" + id2;
        when(imagemRepository.buscarIdsOrfaasAnterioresA(any())).thenReturn(List.of(id1, id2));
        when(imagemRepository.findIdEChavePorIdIn(List.of(id1, id2)))
                .thenReturn(List.<Object[]>of(new Object[]{id1, chave1}, new Object[]{id2, chave2}));
        imagemOrfaGcJob.executar();
        verify(storageClient, times(1)).delete(chave1);
        verify(storageClient, times(1)).delete(chave2);
        verify(imagemRepository, times(1)).deleteAllByIdInBatch(List.of(id1, id2));
    }
}
