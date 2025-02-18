package com.example.qgpt.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryResponse {
    private ModelResponse modelResponse;
    private List<Map<String, Object>> results;
    private String error;
    private String message;
}