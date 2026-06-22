package site.psi.ads3.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.psi.ads3.entity.NewsArticle;
import site.psi.ads3.service.NewsService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public List<NewsArticle> list(
            @RequestParam(value = "query", required = false) String query
    ) {
        return newsService.search(query);
    }

    // Simple trigger endpoint (admin) to force a fetch (could be secured later)
    @GetMapping("/fetch-now")
    public Map<String, String> fetchNow() {
        newsService.fetchAndStore();
        return Map.of("status", "ok");
    }

}
