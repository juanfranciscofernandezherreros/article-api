package com.example.articleapi.service;

import com.example.articleapi.entity.ArticleEntity;
import com.example.articleapi.repository.ArticleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.juanfernandez.article.model.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ArticleStorageService {

    private static final Logger log = LoggerFactory.getLogger(ArticleStorageService.class);

    private final ObjectMapper objectMapper;
    private final String outputPath;
    private final ArticleRepository articleRepository;

    public ArticleStorageService(
            ObjectMapper objectMapper,
            @Value("${article-generator.output-path:src/main/resources/articles}") String outputPath,
            ArticleRepository articleRepository) {
        this.objectMapper = objectMapper;
        this.outputPath = outputPath;
        this.articleRepository = articleRepository;
    }

    public void save(Article article) {
        saveToDatabase(article);
        saveToFile(article);
    }

    private void saveToDatabase(Article article) {
        ArticleEntity entity = new ArticleEntity();
        entity.setTitle(article.getTitle());
        entity.setSlug(article.getSlug());
        entity.setAuthor(article.getAuthor());
        entity.setPayload(serializeToJson(article));
        articleRepository.save(entity);
        log.info("Article saved to database with slug '{}'", article.getSlug());
    }

    private void saveToFile(Article article) {
        try {
            Path dir = Paths.get(outputPath);
            Files.createDirectories(dir);

            String filename = resolveFilename(article, dir);
            Path file = dir.resolve(filename);

            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file.toFile(), article);
            log.info("Article saved to {}", file.toAbsolutePath());
        } catch (IOException e) {
            throw new ArticleStorageException("Failed to save article JSON file", e);
        }
    }

    private String serializeToJson(Article article) {
        try {
            return objectMapper.writeValueAsString(article);
        } catch (JsonProcessingException e) {
            throw new ArticleStorageException("Failed to serialize article to JSON", e);
        }
    }

    private String resolveFilename(Article article, Path dir) {
        String base = resolveBase(article);
        String candidate = base + ".json";
        if (!Files.exists(dir.resolve(candidate))) {
            return candidate;
        }
        return base + "-" + System.currentTimeMillis() + ".json";
    }

    private String resolveBase(Article article) {
        String slug = article.getSlug();
        if (slug != null && !slug.isBlank()) {
            return slug;
        }
        String title = article.getTitle();
        if (title != null && !title.isBlank()) {
            return title.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        }
        return "article-" + System.currentTimeMillis();
    }
}
