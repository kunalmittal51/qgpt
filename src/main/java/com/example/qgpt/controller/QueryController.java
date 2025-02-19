package com.example.qgpt.controller;

import com.example.qgpt.request.QueryRequest;
import com.example.qgpt.response.ModelResponse;
import com.example.qgpt.response.QueryResponse;
import com.example.qgpt.service.LLMService;
import com.example.qgpt.service.QueryExecutionService;
import com.example.qgpt.utils.SQLResponseParser;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/query")
public class QueryController {
    private final LLMService llmService;
    private final QueryExecutionService queryExecutionService;

    public QueryController(
            LLMService llmService,
            QueryExecutionService queryExecutionService
    ) {
        this.llmService = llmService;
        this.queryExecutionService = queryExecutionService;
    }

    /***
     * Health check endpoint
     * @return a response indicating the service is healthy
     */
    @GetMapping
    public QueryResponse health() {
        return QueryResponse.builder()
                .message("Query service is healthy")
                .build();
    }

    /***
     * Endpoint to execute a natural language query
     * @param request the query request
     * @return a response containing the model response and query results
     */
    @PostMapping("/executeQuery")
    public QueryResponse executeNaturalLanguageQuery(@RequestBody QueryRequest request) {
        // Get the SQL query from LLM asynchronously
        Mono<String> sqlQueryMono = llmService.generateSqlQuery(request.getNaturalLanguageQuery());
        // For testing, simply return the generated SQL query as a response
        Mono<String> rawModelResponse = sqlQueryMono.flatMap(Mono::just)
                .onErrorResume(e -> Mono.just("Error generating SQL query: " + e.getMessage()));

        ModelResponse modelResponse = SQLResponseParser.extractThinkAndSQL(rawModelResponse);
        return QueryResponse.builder()
                .modelResponse(modelResponse)
                .results(queryExecutionService.executeQuery(modelResponse.getQuery()))
                .build();
    }
}