package com.example.qgpt.service;

import com.example.qgpt.utils.LLMUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class LLMService {
    private final SchemaService schemaService;
    private final SampleDataService sampleDataService;  // Service for Sample Data
    private final WebClient webClient;


    /**
     * Constructs an instance of LLMService.
     *
     * @param schemaService     the service to generate schema context
     * @param sampleDataService the service to fetch sample data
     */
    public LLMService(SchemaService schemaService, SampleDataService sampleDataService) {
        this.schemaService = schemaService;
        this.sampleDataService = sampleDataService;
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:11434")  // Ollama API endpoint
                .build();
    }

    /**
     * Generates an SQL query from a natural language query using the LLM model.
     *
     * @param naturalLanguageQuery the natural language query
     * @return a Mono emitting the generated SQL query
     */
    public Mono<String> generateSqlQuery(String naturalLanguageQuery) {
        String schemaContext = schemaService.generateSchemaContext();
        String sampleData = sampleDataService.getSampleData();  // Fetch sample rows

        // Construct system message with schema & sample data
        String systemMessage = LLMUtils.createSystemMessage(schemaContext, sampleData);

        // Construct request body
        Map<String, Object> requestBody = LLMUtils.getStringObjectMap(naturalLanguageQuery, systemMessage);

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
