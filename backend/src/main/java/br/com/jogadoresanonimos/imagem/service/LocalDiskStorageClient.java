package br.com.jogadoresanonimos.imagem.service;

import br.com.jogadoresanonimos.common.exception.UpstreamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.EnumSet;
import java.util.Set;

@Component
public class LocalDiskStorageClient implements StorageClient {

    private static final Logger log = LoggerFactory.getLogger(LocalDiskStorageClient.class);

    private static final Set<PosixFilePermission> PERMISSOES_ARQUIVO =
            PosixFilePermissions.fromString("rw-r-----");
    private static final Set<PosixFilePermission> PERMISSOES_DIRETORIO =
            EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE,
                    PosixFilePermission.OWNER_EXECUTE);

    private final Path baseDir;
    private final String publicBaseUrl;

    public LocalDiskStorageClient(@Value("${app.storage.stub-dir:./data/imagens}") String baseDir,
                                   @Value("${app.storage.public-base-url:http://localhost:8080/stub-storage}") String publicBaseUrl) {
        this.baseDir = Paths.get(baseDir);
        this.publicBaseUrl = publicBaseUrl;
    }

    @Override
    public void upload(String chave, byte[] conteudo, String contentType) {
        try {
            criarDiretorioBaseSeNecessario();
            Path destino = baseDir.resolve(chave);
            Files.write(destino, conteudo);
            aplicarPermissoesRestritivas(destino);
        } catch (IOException e) {
            log.error("falha_upload_storage_local");
            throw new UpstreamException("Não foi possível concluir o upload no momento");
        }
    }

    @Override
    public void delete(String chave) {
        try {
            Files.deleteIfExists(baseDir.resolve(chave));
        } catch (IOException e) {
            log.error("falha_delete_storage_local");
            throw new UpstreamException("Não foi possível remover a imagem no momento");
        }
    }

    @Override
    public String urlPublicaPara(String chave) {
        return publicBaseUrl + "/" + chave;
    }

    @Override
    public byte[] download(String chave) {
        try {
            Path caminho = baseDir.resolve(chave).normalize();
            if (!caminho.startsWith(baseDir.normalize())) {
                throw new UpstreamException("Arquivo não encontrado");
            }
            return Files.readAllBytes(caminho);
        } catch (IOException e) {
            log.error("falha_download_storage_local");
            throw new UpstreamException("Não foi possível ler o arquivo no momento");
        }
    }

    private void criarDiretorioBaseSeNecessario() throws IOException {
        if (Files.exists(baseDir)) {
            return;
        }
        Files.createDirectories(baseDir);
        aplicarPermissoesRestritivasSePosix(baseDir, PERMISSOES_DIRETORIO);
    }

    private void aplicarPermissoesRestritivas(Path arquivo) throws IOException {
        aplicarPermissoesRestritivasSePosix(arquivo, PERMISSOES_ARQUIVO);
    }

    private void aplicarPermissoesRestritivasSePosix(Path path, Set<PosixFilePermission> permissoes) throws IOException {
        PosixFileAttributeView view = Files.getFileAttributeView(path, PosixFileAttributeView.class);
        if (view != null) {
            view.setPermissions(permissoes);
        }
    }
}
