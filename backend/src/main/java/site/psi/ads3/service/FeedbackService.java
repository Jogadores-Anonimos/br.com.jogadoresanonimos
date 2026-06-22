package site.psi.ads3.service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import site.psi.ads3.dto.request.FeedbackRequest;
import site.psi.ads3.dto.response.FeedbackResponse;
import site.psi.ads3.entity.Feedback;
import site.psi.ads3.entity.FeedbackStatus;
import site.psi.ads3.repository.FeedbackRepository;

@RequiredArgsConstructor
@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public FeedbackResponse criarFeedback(FeedbackRequest request) {
        Feedback feedback = new Feedback(request.nome(), request.idade(), request.descricao());
        feedbackRepository.save(feedback);
        return FeedbackResponse.fromEntity(feedback);
    }

    public Page<FeedbackResponse> getAllFeedbacks(Pageable pageable){
        return feedbackRepository.findAll(pageable).map(FeedbackResponse::fromEntity);
    }

    public FeedbackResponse getFeedbackById(Long id) throws IllegalArgumentException {
        Feedback feedback = findFeedbackById(id);
        return FeedbackResponse.fromEntity(feedback);
    }

    public FeedbackResponse updateFeedback(Long id, FeedbackRequest request) throws IllegalArgumentException {
        Feedback feedback = findFeedbackById(id);
        request.updateFeedback(feedback);
        feedbackRepository.save(feedback);
        return FeedbackResponse.fromEntity(feedback);
    }

    public void deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
    }

    private Feedback findFeedbackById(Long id) {
        return feedbackRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Feedback não encontrado"));
    }

    public Page<FeedbackResponse> getAprovados(Pageable pageable) {
        return feedbackRepository
                .findByStatus(FeedbackStatus.APROVADO, pageable)
                .map(FeedbackResponse::fromEntity);
    }

    public void approve(Long id) {
        Feedback fb = feedbackRepository.findById(id).orElseThrow();
        fb.setStatus(FeedbackStatus.APROVADO);
        feedbackRepository.save(fb);
    }

    public void reject(Long id) {
        Feedback fb = feedbackRepository.findById(id).orElseThrow();
        fb.setStatus(FeedbackStatus.REJEITADO);
        feedbackRepository.save(fb);
    }
}