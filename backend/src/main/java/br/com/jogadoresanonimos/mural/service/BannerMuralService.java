package br.com.jogadoresanonimos.mural.service;

import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import br.com.jogadoresanonimos.imagem.entity.Imagem;
import br.com.jogadoresanonimos.imagem.repository.ImagemRepository;
import br.com.jogadoresanonimos.imagem.service.StorageClient;
import br.com.jogadoresanonimos.mural.dto.BannerMuralAdminResponse;
import br.com.jogadoresanonimos.mural.dto.BannerMuralPublicoResponse;
import br.com.jogadoresanonimos.mural.dto.CriarBannerMuralRequest;
import br.com.jogadoresanonimos.mural.dto.EditarBannerMuralRequest;
import br.com.jogadoresanonimos.mural.entity.BannerMural;
import br.com.jogadoresanonimos.mural.repository.BannerMuralRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BannerMuralService {

    private final BannerMuralRepository bannerMuralRepository;
    private final ImagemRepository imagemRepository;
    private final StorageClient storageClient;

    public BannerMuralService(BannerMuralRepository bannerMuralRepository, ImagemRepository imagemRepository,
                               StorageClient storageClient) {
        this.bannerMuralRepository = bannerMuralRepository;
        this.imagemRepository = imagemRepository;
        this.storageClient = storageClient;
    }

    @Transactional(readOnly = true)
    public List<BannerMuralPublicoResponse> listarAtivos() {
        List<BannerMural> banners = bannerMuralRepository.findByAtivoTrue(Sort.by(Sort.Direction.DESC, "criadoEm"));
        return banners.stream().map(this::paraPublico).toList();
    }

    @Transactional(readOnly = true)
    public List<BannerMuralAdminResponse> listarTodos() {
        List<BannerMural> banners = bannerMuralRepository.findAll(Sort.by(Sort.Direction.DESC, "criadoEm"));
        return banners.stream().map(this::paraAdmin).toList();
    }

    @Transactional
    public BannerMuralAdminResponse criar(CriarBannerMuralRequest request) {
        validarImagem(request.imagemId());
        BannerMural banner = new BannerMural(request.imagemId(), request.altText(), request.legenda(), request.linkDestino());
        banner = bannerMuralRepository.save(banner);
        return paraAdmin(banner);
    }

    @Transactional
    public BannerMuralAdminResponse editar(UUID id, EditarBannerMuralRequest request) {
        BannerMural banner = buscarOuFalhar(id);
        validarImagem(request.imagemId());
        banner.editar(request.imagemId(), request.altText(), request.legenda(), request.linkDestino(), request.ativo());
        return paraAdmin(banner);
    }

    @Transactional
    public void remover(UUID id) {
        BannerMural banner = buscarOuFalhar(id);
        bannerMuralRepository.delete(banner);
    }

    private void validarImagem(UUID imagemId) {
        if (!imagemRepository.existsById(imagemId)) {
            throw new ValidationException("imagemId não corresponde a um upload existente");
        }
    }

    private BannerMural buscarOuFalhar(UUID id) {
        return bannerMuralRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Banner não encontrado"));
    }

    private String resolverUrl(UUID imagemId) {
        return imagemRepository.findById(imagemId)
                .map(Imagem::getChaveArquivo)
                .map(storageClient::urlPublicaPara)
                .orElse(null);
    }

    private BannerMuralPublicoResponse paraPublico(BannerMural banner) {
        return new BannerMuralPublicoResponse(banner.getId(), resolverUrl(banner.getImagemId()), banner.getAltText(),
                banner.getLegenda(), banner.getLinkDestino(), banner.getCriadoEm());
    }

    private BannerMuralAdminResponse paraAdmin(BannerMural banner) {
        return new BannerMuralAdminResponse(banner.getId(), banner.getImagemId(), resolverUrl(banner.getImagemId()),
                banner.getAltText(), banner.getLegenda(), banner.getLinkDestino(), banner.isAtivo(),
                banner.getCriadoEm(), banner.getAtualizadoEm());
    }
}
