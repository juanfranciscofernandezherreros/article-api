package com.example.articleapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI articleApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Article API")
                        .description("""
                                API REST para la generación automática de artículos mediante inteligencia artificial.
                                
                                Permite generar artículos de blog de alta calidad utilizando distintos proveedores de IA:
                                - **OpenAI** (GPT-4o y otros modelos)
                                - **Google Gemini** (gemini-2.0-flash y otros modelos)
                                - **Ollama** (modelos locales como llama3)
                                
                                Los artículos se generan en función de categoría, subcategoría, etiqueta e idioma, \
                                y pueden configurarse para evitar títulos ya existentes.
                                """)
                        .version("0.0.1-SNAPSHOT")
                        .contact(new Contact()
                                .name("Juan Francisco Fernández Herreros")
                                .url("https://github.com/juanfranciscofernandezherreros/article-api"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Servidor local de desarrollo")
                ));
    }
}
