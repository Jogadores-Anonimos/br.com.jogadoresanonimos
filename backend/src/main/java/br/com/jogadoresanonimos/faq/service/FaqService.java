package br.com.jogadoresanonimos.faq.service;

import br.com.jogadoresanonimos.common.exception.RecursoNaoEncontradoException;
import br.com.jogadoresanonimos.common.util.ReordenacaoUtil;
import br.com.jogadoresanonimos.faq.dto.CriarFaqRequest;
import br.com.jogadoresanonimos.faq.dto.EditarFaqRequest;
import br.com.jogadoresanonimos.faq.dto.FaqResponse;
import br.com.jogadoresanonimos.faq.entity.Faq;
import br.com.jogadoresanonimos.faq.repository.FaqRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class FaqService {

    private final FaqRepository faqRepository;

    public FaqService(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    @Transactional(readOnly = true)
    public List<FaqResponse> listarTodos() {
        return faqRepository.findAllByOrderByOrdemAsc().stream()
                .map(FaqResponse::from)
                .toList();
    }

    @Transactional
    public FaqResponse criar(CriarFaqRequest request) {
        int ordem = faqRepository.findAllByOrderByOrdemAsc().stream()
                .mapToInt(Faq::getOrdem).max().orElse(-1) + 1;
        Faq faq = new Faq(request.pergunta().trim(), request.resposta().trim(), ordem);
        faq = faqRepository.save(faq);
        return FaqResponse.from(faq);
    }

    @Transactional
    public FaqResponse editar(UUID id, EditarFaqRequest request) {
        Faq faq = buscarOuFalhar(id);
        faq.editar(request.pergunta().trim(), request.resposta().trim());
        return FaqResponse.from(faq);
    }

    @Transactional
    public void remover(UUID id) {
        Faq faq = buscarOuFalhar(id);
        faqRepository.delete(faq);
    }

    @Transactional
    public FaqResponse moverParaCima(UUID id) {
        return reordenar(id, true);
    }

    @Transactional
    public FaqResponse moverParaBaixo(UUID id) {
        return reordenar(id, false);
    }

    private FaqResponse reordenar(UUID id, boolean paraCima) {
        Faq faq = buscarOuFalhar(id);
        List<Faq> ordenadas = faqRepository.findAllByOrderByOrdemAsc();
        if (paraCima) {
            ReordenacaoUtil.moverParaCima(ordenadas, faq, Faq::getOrdem, Faq::reordenar);
        } else {
            ReordenacaoUtil.moverParaBaixo(ordenadas, faq, Faq::getOrdem, Faq::reordenar);
        }
        return FaqResponse.from(faq);
    }

    private Faq buscarOuFalhar(UUID id) {
        return faqRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pergunta não encontrada"));
    }
}
