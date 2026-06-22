package site.psi.ads3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.psi.ads3.entity.NewsArticle;

import java.util.List;
import java.util.Optional;

public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {

    Optional<NewsArticle> findByUrl(String url);

    List<NewsArticle> findAllByUrlIn(List<String> urls);

    List<NewsArticle> findAllByOrderByPublishedAtDesc();

    List<NewsArticle> findAllByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByPublishedAtDesc(
            String title, String description);

    List<NewsArticle> findTop200ByOrderByPublishedAtAsc();

}
