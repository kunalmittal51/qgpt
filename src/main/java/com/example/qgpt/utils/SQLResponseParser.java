package com.example.qgpt.utils;

import com.example.qgpt.response.ModelResponse;
import reactor.core.publisher.Mono;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SQLResponseParser {
    private SQLResponseParser() {
       throw new IllegalStateException("Utility class");
    }

    public static ModelResponse extractThinkAndSQL(Mono<String> response) {
        String responseString = response.block();
        String thinkText = "";
        String sqlQuery = responseString;

        // Regex to extract <think> ... </think> reasoning part
        Pattern thinkPattern = Pattern.compile("<think>(.*?)</think>", Pattern.DOTALL);
        Matcher thinkMatcher = thinkPattern.matcher(responseString);

        if (thinkMatcher.find()) {
            thinkText = thinkMatcher.group(1).trim();  // Extract reasoning text
            sqlQuery = thinkMatcher.replaceAll("").trim(); // Remove <think> from response
        }

        // Remove any leftover Markdown-style SQL formatting (```sql ... ```)
        sqlQuery = sqlQuery.replaceAll("```sql", "").replaceAll("```", "").trim();

        return ModelResponse.builder()
                .query(sqlQuery)
                .description(thinkText).build();
    }
}
