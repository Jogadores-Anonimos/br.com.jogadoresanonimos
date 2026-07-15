package br.com.jogadoresanonimos.hotline.service;

import br.com.jogadoresanonimos.common.exception.ConflitoDeRegiaoHotlineException;
import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import br.com.jogadoresanonimos.escritorio.entity.UnidadeFederativa;
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
        return hotlineRegiaoRepository.findAllByOrderByUfAscCidadeAsc().stream()
                .map(HotlineRegiaoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<HotlineRegiaoResponse> listarPublicas() {
        return hotlineRegiaoRepository.findAllComPeloMenosUmTelefoneOrderByUfAscCidadeAsc().stream()
                .map(HotlineRegiaoResponse::from)
                .toList();
    }

    @Transactional
    public HotlineRegiaoResponse criarRegiao(CriarRegiaoRequest request) {
        UnidadeFederativa uf = resolverUf(request.uf());
        String cidade = request.cidade().trim();
        if (hotlineRegiaoRepository.existsByUfAndCidadeIgnoreCase(uf, cidade)) {
            throw new ConflitoDeRegiaoHotlineException(
                    "Já existe uma região com essa UF e cidade. Edite a região existente em vez de criar uma nova.");
        }
        HotlineRegiao regiao = hotlineRegiaoRepository.save(new HotlineRegiao(uf, cidade));
        return HotlineRegiaoResponse.from(regiao);
    }

    @Transactional
    public HotlineRegiaoResponse editarRegiao(UUID id, EditarRegiaoRequest request) {
        HotlineRegiao regiao = buscarRegiaoOuFalhar(id);
        UnidadeFederativa uf = resolverUf(request.uf());
        String cidade = request.cidade().trim();
        boolean mudouChave = uf != regiao.getUf() || !cidade.equalsIgnoreCase(regiao.getCidade());
        if (mudouChave && hotlineRegiaoRepository.existsByUfAndCidadeIgnoreCase(uf, cidade)) {
            throw new ConflitoDeRegiaoHotlineException(
                    "Já existe uma região com essa UF e cidade. Edite a região existente em vez de criar uma nova.");
        }
        regiao.editar(uf, cidade);
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
        HotlineTelefone telefone = new HotlineTelefone(regiao, request.telefone().trim());
        regiao.getTelefones().add(telefone);
        hotlineTelefoneRepository.save(telefone);
        return HotlineRegiaoResponse.from(regiao);
    }

    @Transactional
    public HotlineTelefoneResponse editarTelefone(UUID telefoneId, EditarTelefoneRequest request) {
        HotlineTelefone telefone = buscarTelefoneOuFalhar(telefoneId);
        telefone.editar(request.telefone().trim());
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

    private static UnidadeFederativa resolverUf(String uf) {
        return UnidadeFederativa.deSigla(uf)
                .orElseThrow(() -> new ValidationException("UF inválida: informe uma das 27 UFs do Brasil."));
    }
}
