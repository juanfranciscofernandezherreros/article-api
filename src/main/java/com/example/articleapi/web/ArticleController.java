package com.example.articleapi.web;

import com.github.juanfernandez.article.model.Article;
import com.github.juanfernandez.article.model.ArticleRequest;
import com.github.juanfernandez.article.service.ArticleGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Artículos", description = "Operaciones para la generación automática de artículos con IA")
@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleGeneratorService articleGeneratorService;

    public ArticleController(ArticleGeneratorService articleGeneratorService) {
        this.articleGeneratorService = articleGeneratorService;
    }

    @Operation(
            summary = "Generar un artículo",
            description = """
                    Genera un artículo de blog completo usando IA (OpenAI, Gemini u Ollama) a partir de los
                    parámetros indicados. El artículo incluye título, slug, contenido en formato Markdown,
                    autor y metadatos adicionales.
                    
                    El campo `category` es obligatorio. El resto de parámetros son opcionales y permiten
                    afinar el tema y el estilo del artículo generado.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Artículo generado correctamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Article.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Petición inválida — el campo `category` es obligatorio y no puede estar vacío",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor o fallo en el proveedor de IA",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @PostMapping("/generate")
    public Article generate(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Parámetros para la generación del artículo",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GenerateArticleInput.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo Spring Boot + JWT",
                                    value = """
                                            {
                                              "category": "Spring Boot",
                                              "subcategory": "Spring Security",
                                              "tag": "JWT Authentication",
                                              "language": "es",
                                              "site": "https://mi-blog.com",
                                              "authorUsername": "adminUser",
                                              "avoidTitles": ["Introducción a JWT", "JWT para principiantes"]
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody GenerateArticleInput input) {
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

    @Schema(description = "Parámetros de entrada para la generación de un artículo")
    public record GenerateArticleInput(
            @Schema(description = "Categoría principal del artículo (obligatorio)", example = "Spring Boot", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotBlank String category,

            @Schema(description = "Subcategoría o tema específico dentro de la categoría", example = "Spring Security")
            String subcategory,

            @Schema(description = "Etiqueta o palabra clave principal del artículo", example = "JWT Authentication")
            String tag,

            @Schema(description = "Idioma en el que se generará el artículo (código ISO 639-1)", example = "es")
            String language,

            @Schema(description = "URL del sitio web al que pertenece el artículo", example = "https://mi-blog.com")
            String site,

            @Schema(description = "Nombre de usuario del autor del artículo", example = "adminUser")
            String authorUsername,

            @Schema(description = "Lista de títulos que deben evitarse en la generación para no repetir contenido",
                    example = "[\"Introducción a JWT\", \"JWT para principiantes\"]")
            List<String> avoidTitles
    ) {
    }
}
