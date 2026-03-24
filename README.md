# Article API

API REST para la **generación automática de artículos** de blog mediante inteligencia artificial.

Soporta múltiples proveedores de IA: **OpenAI**, **Google Gemini** y **Ollama** (modelos locales).

---

## Tabla de contenidos

- [Requisitos](#requisitos)
- [Configuración](#configuración)
- [Ejecución](#ejecución)
- [Documentación interactiva (Swagger UI)](#documentación-interactiva-swagger-ui)
- [Endpoints](#endpoints)
  - [POST /api/articles/generate](#post-apiarticlesgenerate)
- [Ejemplos de uso](#ejemplos-de-uso)
- [Configuración de proveedores de IA](#configuración-de-proveedores-de-ia)
- [Tecnologías](#tecnologías)

---

## Requisitos

- Java 17+
- Maven 3.8+
- Clave de API de OpenAI, Google Gemini **o** una instancia local de Ollama en ejecución

---

## Configuración

Copia o edita el fichero `src/main/resources/application.yml` y selecciona tu proveedor de IA:

```yaml
article-generator:
  provider: openai          # openai | gemini | ollama
  model: gpt-4o
  openai-api-key: ${OPENAI_API_KEY:}
  site: https://mi-blog.com
  author-username: adminUser
  language: es
```

La clave de API se puede pasar como variable de entorno:

```bash
export OPENAI_API_KEY=sk-...
```

---

## Ejecución

```bash
mvn spring-boot:run
```

La aplicación arrancará en `http://localhost:8080`.

---

## Documentación interactiva (Swagger UI)

Una vez arrancada la aplicación, accede a la documentación interactiva generada automáticamente por SpringDoc/OpenAPI:

| Recurso | URL |
|---|---|
| Swagger UI | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) |
| OpenAPI JSON | [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) |
| OpenAPI YAML | [http://localhost:8080/v3/api-docs.yaml](http://localhost:8080/v3/api-docs.yaml) |

---

## Endpoints

### `POST /api/articles/generate`

Genera un artículo de blog completo usando IA a partir de los parámetros indicados.

#### Request body

```json
{
  "category":      "string (obligatorio)",
  "subcategory":   "string (opcional)",
  "tag":           "string (opcional)",
  "language":      "string (opcional, código ISO 639-1, p. ej. 'es')",
  "site":          "string (opcional, URL del blog)",
  "authorUsername":"string (opcional)",
  "avoidTitles":   ["string", "..."] 
}
```

| Campo | Tipo | Requerido | Descripción |
|---|---|---|---|
| `category` | `string` | ✅ | Categoría principal del artículo |
| `subcategory` | `string` | ❌ | Subcategoría o tema específico |
| `tag` | `string` | ❌ | Etiqueta o palabra clave principal |
| `language` | `string` | ❌ | Idioma del artículo (p. ej. `es`, `en`) |
| `site` | `string` | ❌ | URL del sitio web al que pertenece el artículo |
| `authorUsername` | `string` | ❌ | Nombre de usuario del autor |
| `avoidTitles` | `string[]` | ❌ | Lista de títulos a evitar para no repetir contenido |

#### Respuesta exitosa — `200 OK`

```json
{
  "title":   "Autenticación JWT en Spring Boot 3: guía práctica",
  "slug":    "autenticacion-jwt-en-spring-boot-3-guia-practica",
  "content": "## Introducción\n...",
  "author":  "adminUser",
  ...
}
```

#### Respuestas de error

| Código | Descripción |
|---|---|
| `400 Bad Request` | El campo `category` es obligatorio y no puede estar vacío |
| `500 Internal Server Error` | Error interno o fallo en el proveedor de IA |

---

## Ejemplos de uso

### Con cURL

```bash
curl -X POST http://localhost:8080/api/articles/generate \
  -H "Content-Type: application/json" \
  -d '{
    "category": "Spring Boot",
    "subcategory": "Spring Security",
    "tag": "JWT Authentication",
    "language": "es",
    "site": "https://mi-blog.com",
    "authorUsername": "adminUser",
    "avoidTitles": ["Introducción a JWT", "JWT para principiantes"]
  }'
```

### Con HTTPie

```bash
http POST http://localhost:8080/api/articles/generate \
  category="Spring Boot" \
  subcategory="Spring Security" \
  tag="JWT Authentication" \
  language="es" \
  site="https://mi-blog.com" \
  authorUsername="adminUser" \
  avoidTitles:='["Introducción a JWT", "JWT para principiantes"]'
```

---

## Configuración de proveedores de IA

### OpenAI (por defecto)

```yaml
article-generator:
  provider: openai
  model: gpt-4o
  openai-api-key: ${OPENAI_API_KEY:}
  site: https://mi-blog.com
  author-username: adminUser
  language: es
```

### Google Gemini

```yaml
article-generator:
  provider: gemini
  model: gemini-2.0-flash
  gemini-api-key: ${GEMINI_API_KEY:}
  site: https://mi-blog.com
  author-username: adminUser
  language: es
```

### Ollama (local)

```yaml
article-generator:
  provider: ollama
  model: llama3
  ollama-base-url: http://localhost:11434
  site: https://mi-blog.com
  author-username: adminUser
  language: es
```

---

## Tecnologías

| Tecnología | Versión |
|---|---|
| Java | 17 |
| Spring Boot | 3.3.6 |
| SpringDoc OpenAPI (Swagger) | 2.6.0 |
| Bean Validation (Jakarta) | — |
| article-generator-spring-boot-starter | 1.0.1 |
