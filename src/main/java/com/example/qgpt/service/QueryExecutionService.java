package com.example.qgpt.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
public class QueryExecutionService {
    private static final Pattern UNSAFE_SQL_PATTERN = Pattern.compile(
            "\\b(DROP|DELETE|UPDATE|INSERT|ALTER|TRUNCATE|CREATE|REPLACE)\\b", Pattern.CASE_INSENSITIVE);

    private final JdbcTemplate jdbcTemplate;

    public QueryExecutionService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> executeQuery(String sqlQuery, Object... params) {
        // Blocking call to extract String from Mono

        validateQuery(sqlQuery); // Validate to ensure safety
        System.out.println("Executing query: " + sqlQuery);
        return jdbcTemplate.queryForList(sqlQuery, params); // Safe execution
    }

    private void validateQuery(String sql) {
        if (Objects.isNull(sql)) {
            throw new IllegalArgumentException("queries is empty");
        }

        if (UNSAFE_SQL_PATTERN.matcher(sql).find()) {
            throw new IllegalArgumentException("Only SELECT queries are allowed");
        }
    }
}
