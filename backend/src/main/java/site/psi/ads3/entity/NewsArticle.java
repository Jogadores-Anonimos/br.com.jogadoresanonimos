package site.psi.ads3.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "news_article", indexes = {
    @Index(name = "idx_news_url", columnList = "url" )
})
public class NewsArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String title;

    @Column(length = 4000)
    private String description;

    @Column(length = 2000, unique = true)
    private String url;

    @Column(name = "image_url", length = 2000)
    private String imageUrl;

    @Column(length = 500)
    private String source;

    @Column(name = "published_at")
    private OffsetDateTime publishedAt;

    @Column(name = "inserted_at")
    private OffsetDateTime insertedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public OffsetDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(OffsetDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public OffsetDateTime getInsertedAt() {
        return insertedAt;
    }

    public void setInsertedAt(OffsetDateTime insertedAt) {
        this.insertedAt = insertedAt;
    }
}
