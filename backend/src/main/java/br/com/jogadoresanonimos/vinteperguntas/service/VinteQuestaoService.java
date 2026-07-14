package br.com.jogadoresanonimos.vinteperguntas.service;

import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.exception.ValidationException;
import br.com.jogadoresanonimos.common.util.ReordenacaoUtil;
import br.com.jogadoresanonimos.vinteperguntas.dto.VinteQuestaoCriarRequest;
import br.com.jogadoresanonimos.vinteperguntas.dto.VinteQuestaoResponse;
import br.com.jogadoresanonimos.vinteperguntas.dto.VinteQuestaoUpdateRequest;
import br.com.jogadoresanonimos.vinteperguntas.entity.VinteQuestao;
import br.com.jogadoresanonimos.vinteperguntas.repository.VinteQuestaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class VinteQuestaoService {

    private final VinteQuestaoRepository vinteQuestaoRepository;

    public VinteQuestaoService(VinteQuestaoRepository vinteQuestaoRepository) {
        this.vinteQuestaoRepository = vinteQuestaoRepository;
    }

    @Transactional(readOnly = true)
    public List<VinteQuestaoResponse> listar() {
        return paraResponse(vinteQuestaoRepository.findAllByOrderByOrdemAsc());
    }

    @Transactional
    public VinteQuestaoResponse criar(VinteQuestaoCriarRequest request, UUID adminId) {
        int proximaOrdem = vinteQuestaoRepository.findAllByOrderByOrdemAsc().stream()
                .mapToInt(VinteQuestao::getOrdem).max().orElse(-1) + 1;
        VinteQuestao questao = new VinteQuestao(proximaOrdem, request.texto().trim(), adminId);
        questao = vinteQuestaoRepository.save(questao);
        int numero = vinteQuestaoRepository.findAllByOrderByOrdemAsc().indexOf(questao) + 1;
        return VinteQuestaoResponse.de(questao, numero);
    }

    @Transactional
    public VinteQuestaoResponse atualizar(UUID id, VinteQuestaoUpdateRequest request, UUID adminId) {
        VinteQuestao questao = buscarOuFalhar(id);
        questao.atualizarTexto(request.texto().trim(), adminId);
        int numero = vinteQuestaoRepository.findAllByOrderByOrdemAsc().indexOf(questao) + 1;
        return VinteQuestaoResponse.de(questao, numero);
    }

    @Transactional
    public void remover(UUID id) {
        VinteQuestao questao = buscarOuFalhar(id);
        if (vinteQuestaoRepository.count() <= 1) {
            throw new ValidationException("Não é possível remover a última pergunta.");
        }
        vinteQuestaoRepository.delete(questao);
    }

    @Transactional
    public VinteQuestaoResponse moverParaCima(UUID id) {
        return reordenar(id, true);
    }

    @Transactional
    public VinteQuestaoResponse moverParaBaixo(UUID id) {
        return reordenar(id, false);
    }

    private VinteQuestaoResponse reordenar(UUID id, boolean paraCima) {
        VinteQuestao questao = buscarOuFalhar(id);
        List<VinteQuestao> ordenadas = vinteQuestaoRepository.findAllByOrderByOrdemAsc();
        if (paraCima) {
            ReordenacaoUtil.moverParaCima(ordenadas, questao, VinteQuestao::getOrdem, VinteQuestao::reordenar);
        } else {
            ReordenacaoUtil.moverParaBaixo(ordenadas, questao, VinteQuestao::getOrdem, VinteQuestao::reordenar);
        }
        int numero = vinteQuestaoRepository.findAllByOrderByOrdemAsc().indexOf(questao) + 1;
        return VinteQuestaoResponse.de(questao, numero);
    }

    private VinteQuestao buscarOuFalhar(UUID id) {
        return vinteQuestaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pergunta não encontrada"));
    }

    private List<VinteQuestaoResponse> paraResponse(List<VinteQuestao> questoes) {
        return java.util.stream.IntStream.range(0, questoes.size())
                .mapToObj(i -> VinteQuestaoResponse.de(questoes.get(i), i + 1))
                .toList();
    }
}
