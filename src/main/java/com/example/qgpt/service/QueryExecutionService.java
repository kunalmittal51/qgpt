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

    /**
     * Constructs an instance of QueryExecutionService.
     *
     * @param jdbcTemplate the JdbcTemplate to be used for executing queries
     */
    public QueryExecutionService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Executes the given SQL query and returns the results.
     *
     * @param sqlQuery the SQL query to execute
     * @param params   the parameters to be used in the query
     * @return the results of the query
     */
    public List<Map<String, Object>> executeQuery(String sqlQuery, Object... params) {
        // Blocking call to extract String from Mono

        validateQuery(sqlQuery); // Validate to ensure safety
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
