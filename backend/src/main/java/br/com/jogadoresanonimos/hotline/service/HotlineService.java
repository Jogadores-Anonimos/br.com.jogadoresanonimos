package br.com.jogadoresanonimos.hotline.service;

import br.com.jogadoresanonimos.common.exception.ConflitoDeEstadoException;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.hotline.dto.*;
import br.com.jogadoresanonimos.hotline.entity.HotlineRegiao;
import br.com.jogadoresanonimos.hotline.entity.HotlineTelefone;
import br.com.jogadoresanonimos.hotline.repository.HotlineRegiaoRepository;
import br.com.jogadoresanonimos.hotline.repository.HotlineTelefoneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class HotlineService {

    private final HotlineRegiaoRepository hotlineRegiaoRepository;
    private final HotlineTelefoneRepository hotlineTelefoneRepository;

    public HotlineService(HotlineRegiaoRepository hotlineRegiaoRepository,
                           HotlineTelefoneRepository hotlineTelefoneRepository) {
        this.hotlineRegiaoRepository = hotlineRegiaoRepository;
        this.hotlineTelefoneRepository = hotlineTelefoneRepository;
    }

    @Transactional(readOnly = true)
    public List<HotlineRegiaoResponse> listarTodas() {
        return hotlineRegiaoRepository.findAllByOrderByNomeAsc().stream()
                .map(HotlineRegiaoResponse::from)
                .toList();
    }

    @Transactional
    public HotlineRegiaoResponse criarRegiao(CriarRegiaoRequest request) {
        String nome = request.nome().trim();
        if (hotlineRegiaoRepository.existsByNomeIgnoreCase(nome)) {
            throw new ConflitoDeEstadoException("Já existe uma região/estado com esse nome.");
        }
        HotlineRegiao regiao = hotlineRegiaoRepository.save(new HotlineRegiao(nome));
        return HotlineRegiaoResponse.from(regiao);
    }

    @Transactional
    public void removerRegiao(UUID id) {
        HotlineRegiao regiao = buscarRegiaoOuFalhar(id);
        hotlineRegiaoRepository.delete(regiao);
    }

    @Transactional
    public HotlineRegiaoResponse adicionarTelefone(UUID regiaoId, CriarTelefoneRequest request) {
        HotlineRegiao regiao = buscarRegiaoOuFalhar(regiaoId);
        HotlineTelefone telefone = new HotlineTelefone(regiao, request.label().trim(), request.telefone().trim());
        regiao.getTelefones().add(telefone);
        hotlineTelefoneRepository.save(telefone);
        return HotlineRegiaoResponse.from(regiao);
    }

    @Transactional
    public HotlineTelefoneResponse editarTelefone(UUID telefoneId, EditarTelefoneRequest request) {
        HotlineTelefone telefone = buscarTelefoneOuFalhar(telefoneId);
        telefone.editar(request.label().trim(), request.telefone().trim());
        return HotlineTelefoneResponse.from(telefone);
    }

    @Transactional
    public void removerTelefone(UUID telefoneId) {
        HotlineTelefone telefone = buscarTelefoneOuFalhar(telefoneId);
        hotlineTelefoneRepository.delete(telefone);
    }

    private HotlineRegiao buscarRegiaoOuFalhar(UUID id) {
        return hotlineRegiaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Região não encontrada"));
    }

    private HotlineTelefone buscarTelefoneOuFalhar(UUID id) {
        return hotlineTelefoneRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Telefone não encontrado"));
    }
}
