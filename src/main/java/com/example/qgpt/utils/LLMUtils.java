package com.example.qgpt.utils;

import java.util.Map;

public class LLMUtils {
    private LLMUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static final String MODEL = "mistral";

    public static Map<String, Object> getStringObjectMap(String naturalLanguageQuery, String systemMessage) {
        return Map.of(
                "model", MODEL,
                "temperature", 0,
                "messages", new Object[]{
                        Map.of("role", "system", "content", systemMessage),
                        Map.of("role", "user", "content", naturalLanguageQuery)
                }
        );
    }

    public static String createSystemMessage(String schemaContext, String sampleData) {
        return String.format(
                """
                        You are a SQL query generator. Given the following database schema:
                        
                        %s
                        
                        Here are some sample rows from the database:
                        
                        %s
                        
                        Generate an executable SQL query for MySQL based on the user's request. \
                        Return ONLY the SQL query without any explanations or additional text.""",
                schemaContext, sampleData
        );
    }
}
