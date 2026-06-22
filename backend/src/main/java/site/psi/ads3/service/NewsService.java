package site.psi.ads3.service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import site.psi.ads3.entity.NewsArticle;
import site.psi.ads3.repository.NewsArticleRepository;

@Service
public class NewsService {

    private static final Logger logger = LoggerFactory.getLogger(NewsService.class);

    private final RestTemplate restTemplate;
    private final NewsArticleRepository repo;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${newsapi.key}")
    private String newsApiKey;

    private static final int MAX_ARTICLES = 200;

    public NewsService(RestTemplate restTemplate, NewsArticleRepository repo) {
        this.restTemplate = restTemplate;
        this.repo = repo;
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.DAYS)
    public void scheduledFetch() {
        try {
            logger.info("Running scheduled news fetch...");
            fetchAndStore();
        } catch (Exception e) {
            logger.error("Error during scheduled fetch", e);
        }
    }

    public void fetchAndStore() {
        if (newsApiKey == null || newsApiKey.isBlank()) {
            logger.warn("newsapi.key not configured; skipping fetch");
            return;
        }

        var q = "vício em apostas OR vicio em bets";
        var url = String.format("https://newsapi.org/v2/everything?q=%s&language=pt&sortBy=publishedAt&pageSize=100&apiKey=%s", q, newsApiKey);

        var resp = restTemplate.getForEntity(url, String.class);
        if (!resp.getStatusCode().is2xxSuccessful()) {
            logger.warn("NewsAPI responded with status {}", resp.getStatusCode().value());
            return;
        }

        try {
            JsonNode root = mapper.readTree(resp.getBody());
            JsonNode articles = root.path("articles");
            if (!articles.isArray()) return;

            List<NewsArticle> toSave = new ArrayList<>();

            List<String> articleUrls = new ArrayList<>();
            for (JsonNode a : articles) {
                String articleUrl = a.path("url").asText(null);
                if (articleUrl == null || articleUrl.isBlank()) continue;
                articleUrls.add(articleUrl);
            }

            Set<String> existingUrls = new HashSet<>();
            if (!articleUrls.isEmpty()) {
                existingUrls.addAll(repo.findAllByUrlIn(articleUrls).stream()
                        .map(NewsArticle::getUrl)
                        .collect(Collectors.toSet()));
            }

            for (JsonNode a : articles) {
                String articleUrl = a.path("url").asText(null);
                if (articleUrl == null || articleUrl.isBlank()) continue;
                if (existingUrls.contains(articleUrl)) continue;

                NewsArticle na = new NewsArticle();
                na.setUrl(articleUrl);
                na.setTitle(a.path("title").asText(null));
                na.setDescription(a.path("description").asText(null));
                na.setImageUrl(a.path("urlToImage").asText(null));
                na.setSource(a.path("source").path("name").asText(null));

                String published = a.path("publishedAt").asText(null);
                if (published != null) {
                    try {
                        OffsetDateTime odt = OffsetDateTime.parse(published, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                        na.setPublishedAt(odt);
                    } catch (Exception ex) {
                        na.setPublishedAt(OffsetDateTime.now());
                    }
                } else {
                    na.setPublishedAt(OffsetDateTime.now());
                }

                na.setInsertedAt(OffsetDateTime.now());
                toSave.add(na);
            }

            List<NewsArticle> created = repo.saveAll(toSave);

            if (!created.isEmpty()) {
                logger.info("Saved {} new articles", created.size());
            } else {
                logger.info("No new articles found");
            }

            // Enforce cap of MAX_ARTICLES
            long total = repo.count();
            if (total > MAX_ARTICLES) {
                long toRemove = total - MAX_ARTICLES;
                List<NewsArticle> oldest = repo.findTop200ByOrderByPublishedAtAsc();
                if (oldest.size() > toRemove) {
                    List<NewsArticle> remove = oldest.subList(0, (int) toRemove);
                    if (!remove.isEmpty()) {
                        repo.deleteAllInBatch(remove);
                        logger.info("Deleted {} old articles to enforce cap", remove.size());
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Error parsing NewsAPI response", e);
        }
    }

    public List<NewsArticle> search(String q) {
        if (q == null || q.isBlank()) {
            return repo.findAllByOrderByPublishedAtDesc();
        }
        return repo.findAllByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByPublishedAtDesc(q, q);
    }

}