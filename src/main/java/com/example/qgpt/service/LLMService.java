package com.example.qgpt.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class LLMService {
    private final SchemaService schemaService;
    private final WebClient webClient;

    public LLMService(SchemaService schemaService) {
        this.schemaService = schemaService;
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:11434")  // Ollama's default API endpoint
                .build();
    }

    public Mono<String> generateSqlQuery(String naturalLanguageQuery) {
        Map<String, Object> requestBody = Map.of(
                "model", "mistral",
                "messages", new Object[]{
                        Map.of("role", "system", "content",
                                "You are a SQL query generator. Given the following schema:\n\n" +
                                        schemaService.generateSchemaContext() +
                                        "\nGenerate only the SQL query without any explanation and query should be executable " +
                                        "with the provided schema."),
                        Map.of("role", "user", "content", naturalLanguageQuery)
                }
        );

        // Handle streaming response
        return webClient.post()
                .uri("/api/chat")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(Map.class)  // Process response as a Flux stream
                .filter(response -> response.containsKey("message"))  // Ensure it has "message" field
                .map(response -> (Map<String, String>) response.get("message")) // Extract message
                .map(message -> message.get("content")) // Get the content field
                .collectList() // Collect all parts of the response into a list
                .map(parts -> String.join("", parts)) // Combine all parts into one query
                .map(sql -> sql.replaceAll("```sql", "").replaceAll("```", "").trim()) // Cleanup formatting
                .onErrorResume(e -> Mono.just("Error generating SQL query: " + e.getMessage()));
    }
}