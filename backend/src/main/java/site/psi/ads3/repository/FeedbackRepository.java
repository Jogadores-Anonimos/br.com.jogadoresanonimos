package site.psi.ads3.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.psi.ads3.entity.Feedback;
import site.psi.ads3.entity.FeedbackStatus;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Page<Feedback> findByStatus(FeedbackStatus status, Pageable pageable);
}