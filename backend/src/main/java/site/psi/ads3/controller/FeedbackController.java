package site.psi.ads3.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import site.psi.ads3.dto.request.FeedbackRequest;
import site.psi.ads3.dto.response.FeedbackResponse;
import site.psi.ads3.service.FeedbackService;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/feedbacks")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FeedbackResponse criarFeedback(@RequestBody @Valid FeedbackRequest request) {
        return feedbackService.criarFeedback(request);
    }

    @GetMapping
    public Page<FeedbackResponse> getAllFeedbacks(Pageable pageable) {
        return feedbackService.getAllFeedbacks(pageable);
    }

    @GetMapping("/{id}")
    public FeedbackResponse getFeedbackById(@PathVariable Long id) {
        return feedbackService.getFeedbackById(id);
    }

    @PutMapping("/{id}")
    public FeedbackResponse updateFeedback(@PathVariable Long id, @RequestBody FeedbackRequest request) {
        return feedbackService.updateFeedback(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
    }

    @GetMapping("/public")
    public Page<FeedbackResponse> getFeedbacksPublicos(Pageable pageable) {
        return feedbackService.getAprovados(pageable);
    }

    @PatchMapping("/{id}/approve")
    public void approve(@PathVariable Long id) {
        feedbackService.approve(id);
    }

    @PatchMapping("/{id}/reject")
    public void reject(@PathVariable Long id) {
        feedbackService.reject(id);
    }
}