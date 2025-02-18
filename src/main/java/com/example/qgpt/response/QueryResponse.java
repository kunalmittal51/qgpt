package com.example.qgpt.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class QueryResponse {
    private String generatedSql;
    private List<Map<String, Object>> results;
    private String error;
    private String message;
}