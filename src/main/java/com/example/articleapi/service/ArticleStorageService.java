package com.example.articleapi.service;

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

    public ArticleStorageService(
            ObjectMapper objectMapper,
            @Value("${article-generator.output-path:src/main/resources/articles}") String outputPath) {
        this.objectMapper = objectMapper;
        this.outputPath = outputPath;
    }

    public void save(Article article) {
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
