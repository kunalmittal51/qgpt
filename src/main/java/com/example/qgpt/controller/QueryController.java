package com.example.qgpt.controller;

import com.example.qgpt.request.QueryRequest;
import com.example.qgpt.response.QueryResponse;
import com.example.qgpt.service.LLMService;
import com.example.qgpt.service.QueryExecutionService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

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

    @GetMapping
    public QueryResponse health() {
        return QueryResponse.builder()
                .message("Query service is healthy")
                .build();
    }

    @PostMapping("/executeQuery")
    public List<Map<String, Object>> executeNaturalLanguageQuery(@RequestBody QueryRequest request) {
        // Get the SQL query from LLM asynchronously
        Mono<String> sqlQueryMono = llmService.generateSqlQuery(request.getNaturalLanguageQuery());

        // For testing, simply return the generated SQL query as a response
        Mono<String> sqlQuery =  sqlQueryMono.flatMap(Mono::just)
                .onErrorResume(e -> Mono.just("Error generating SQL query: " + e.getMessage()));
        return queryExecutionService.executeQuery(sqlQuery);
    }

}