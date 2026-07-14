package br.com.jogadoresanonimos.usuario.biblioteca.service;

import br.com.jogadoresanonimos.arquivo.dto.ArquivoResponse;
import br.com.jogadoresanonimos.arquivo.entity.Arquivo;
import br.com.jogadoresanonimos.arquivo.repository.ArquivoRepository;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.imagem.service.StorageClient;
import br.com.jogadoresanonimos.usuario.biblioteca.entity.UsuarioArquivo;
import br.com.jogadoresanonimos.usuario.biblioteca.repository.UsuarioArquivoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BibliotecaPessoalService {

    private final UsuarioArquivoRepository usuarioArquivoRepository;
    private final ArquivoRepository arquivoRepository;
    private final StorageClient storageClient;

    public BibliotecaPessoalService(UsuarioArquivoRepository usuarioArquivoRepository,
                                     ArquivoRepository arquivoRepository, StorageClient storageClient) {
        this.usuarioArquivoRepository = usuarioArquivoRepository;
        this.arquivoRepository = arquivoRepository;
        this.storageClient = storageClient;
    }

    @Transactional(readOnly = true)
    public List<ArquivoResponse> listar(UUID usuarioId) {
        return usuarioArquivoRepository.listarArquivosDoUsuario(usuarioId).stream()
                .map(this::paraResponse)
                .toList();
    }

    private ArquivoResponse paraResponse(Arquivo arquivo) {
        String thumbnailUrl = arquivo.getThumbnailChave() != null
                ? storageClient.urlPublicaPara(arquivo.getThumbnailChave())
                : null;
        return ArquivoResponse.from(arquivo, thumbnailUrl);
    }

    @Transactional
    public void adicionar(UUID usuarioId, UUID arquivoId) {
        if (usuarioArquivoRepository.existsById_UsuarioIdAndId_ArquivoId(usuarioId, arquivoId)) {
            return;
        }
        if (!arquivoRepository.existsById(arquivoId)) {
            throw new RecursoNaoEncontradoException("Arquivo não encontrado");
        }
        usuarioArquivoRepository.save(new UsuarioArquivo(usuarioId, arquivoId));
    }

    @Transactional
    public void remover(UUID usuarioId, UUID arquivoId) {
        usuarioArquivoRepository.deleteById_UsuarioIdAndId_ArquivoId(usuarioId, arquivoId);
    }
}
