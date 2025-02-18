package com.example.qgpt.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class LLMService {
    private final SchemaService schemaService;
    private final SampleDataService sampleDataService;  // New Service for Sample Data
    private final WebClient webClient;

    public LLMService(SchemaService schemaService, SampleDataService sampleDataService) {
        this.schemaService = schemaService;
        this.sampleDataService = sampleDataService;
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:11434")  // Ollama API endpoint
                .build();
    }

    public Mono<String> generateSqlQuery(String naturalLanguageQuery) {
        String schemaContext = schemaService.generateSchemaContext();
        String sampleData = sampleDataService.getSampleData();  // Fetch sample rows

        // Construct system message with schema & sample data
        String systemMessage = String.format(
                "You are a SQL query generator. Given the following database schema:\n\n%s\n\n" +
                        "Here are some sample rows from the database:\n\n%s\n\n" +
                        "Generate an executable SQL query for MySQL based on the user's request. " +
                        "Return ONLY the SQL query without any explanations or additional text.",
                schemaContext, sampleData
        );

        Map<String, Object> requestBody = Map.of(
                "model", "mistral",
                "messages", new Object[]{
                        Map.of("role", "system", "content", systemMessage),
                        Map.of("role", "user", "content", naturalLanguageQuery)
                }
        );

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
