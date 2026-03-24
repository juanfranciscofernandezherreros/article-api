package com.example.articleapi.web;

import com.github.juanfernandez.article.model.Article;
import com.github.juanfernandez.article.model.ArticleRequest;
import com.github.juanfernandez.article.service.ArticleGeneratorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleGeneratorService articleGeneratorService;

    public ArticleController(ArticleGeneratorService articleGeneratorService) {
        this.articleGeneratorService = articleGeneratorService;
    }

    @PostMapping("/generate")
    public Article generate(@RequestBody GenerateArticleInput input) {
        ArticleRequest request = ArticleRequest.builder()
                .category(input.category())
                .subcategory(input.subcategory())
                .tag(input.tag())
                .language(input.language())
                .site(input.site())
                .authorUsername(input.authorUsername())
                .avoidTitles(input.avoidTitles())
                .build();

        return articleGeneratorService.generateArticle(request);
    }

    public record GenerateArticleInput(
            String category,
            String subcategory,
            String tag,
            String language,
            String site,
            String authorUsername,
            List<String> avoidTitles
    ) {
    }
}
