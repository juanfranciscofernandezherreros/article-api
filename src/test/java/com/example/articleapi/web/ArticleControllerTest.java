package com.example.articleapi.web;

import com.example.articleapi.config.SecurityConfig;
import com.example.articleapi.service.ArticleStorageService;
import com.github.juanfernandez.article.model.Article;
import com.github.juanfernandez.article.model.ArticleRequest;
import com.github.juanfernandez.article.service.ArticleGeneratorService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArticleController.class)
@Import(SecurityConfig.class)
class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticleGeneratorService articleGeneratorService;

    @MockBean
    private ArticleStorageService articleStorageService;

    @Test
    void generateBuildsRequestAndReturnsArticle() throws Exception {
        Article article = new Article();
        article.setTitle("Autenticación JWT en Spring Boot 3: guía práctica");
        article.setSlug("autenticacion-jwt-en-spring-boot-3-guia-practica");
        article.setAuthor("juan");

        when(articleGeneratorService.generateArticle(any(ArticleRequest.class))).thenReturn(article);

        mockMvc.perform(post("/api/articles/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "category": "Spring Boot",
                                  "subcategory": "Spring Security",
                                  "tag": "JWT Authentication",
                                  "language": "es",
                                  "site": "https://mi-blog.com",
                                  "authorUsername": "juan",
                                  "avoidTitles": ["Introducción a JWT", "JWT para principiantes"]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Autenticación JWT en Spring Boot 3: guía práctica"))
                .andExpect(jsonPath("$.slug").value("autenticacion-jwt-en-spring-boot-3-guia-practica"))
                .andExpect(jsonPath("$.author").value("juan"));

        verify(articleStorageService).save(article);

        ArgumentCaptor<ArticleRequest> captor = ArgumentCaptor.forClass(ArticleRequest.class);
        verify(articleGeneratorService).generateArticle(captor.capture());

        ArticleRequest sent = captor.getValue();
        assertThat(sent.getCategory()).isEqualTo("Spring Boot");
        assertThat(sent.getSubcategory()).isEqualTo("Spring Security");
        assertThat(sent.getTag()).isEqualTo("JWT Authentication");
        assertThat(sent.getLanguage()).isEqualTo("es");
        assertThat(sent.getSite()).isEqualTo("https://mi-blog.com");
        assertThat(sent.getAuthorUsername()).isEqualTo("juan");
        assertThat(sent.getAvoidTitles()).isEqualTo(List.of("Introducción a JWT", "JWT para principiantes"));
    }

    @Test
    void generateAcceptsParentArticleId() throws Exception {
        Article article = new Article();
        article.setTitle("Artículo hijo");
        article.setSlug("articulo-hijo");
        article.setAuthor("juan");

        when(articleGeneratorService.generateArticle(any(ArticleRequest.class))).thenReturn(article);

        mockMvc.perform(post("/api/articles/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "category": "Spring Boot",
                                  "parentArticleId": 42
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void generateReturnsBadRequestWhenCategoryIsBlank() throws Exception {
        mockMvc.perform(post("/api/articles/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "category": "   "
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(articleGeneratorService);
    }
}
