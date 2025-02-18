package com.example.qgpt.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SampleDataService {
    private static final int SAMPLE_LIMIT = 5;  // Limit per table
    private final JdbcTemplate jdbcTemplate;

    /***
     * Constructs an instance of SampleDataService.
     *
     * @param jdbcTemplate the JdbcTemplate to be used for fetching sample data
     */
    public SampleDataService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /***
     * Fetches sample data from the database.
     *
     * @return a string containing sample data from the database
     */
    public String getSampleData() {
        try {
            // Get all table names
            List<String> tableNames = jdbcTemplate.queryForList(
                    "SHOW TABLES", String.class);

            // Fetch sample rows for each table
            return tableNames.stream()
                    .map(this::fetchSampleRows)
                    .filter(data -> !data.isEmpty())  // Ignore empty tables
                    .collect(Collectors.joining("\n\n"));
        } catch (Exception e) {
            System.err.println("Error fetching sample data: " + e.getMessage());
            return "Sample data unavailable.";
        }
    }

    /***
     * Fetches sample rows for a given table.
     *
     * @param tableName the name of the table
     * @return a string containing sample rows for the table
     */
    private String fetchSampleRows(String tableName) {
        try {
            // Fetch SAMPLE_LIMIT rows
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT * FROM " + tableName + " LIMIT " + SAMPLE_LIMIT);

            if (rows.isEmpty()) {
                return "";
            }

            // Convert rows to string format
            String header = "Table: " + tableName;
            String data = rows.stream()
                    .map(row -> row.entrySet().stream()
                            .map(entry -> entry.getKey() + "=" + entry.getValue())
                            .collect(Collectors.joining(", ")))
                    .collect(Collectors.joining("\n"));

            return header + "\n" + data;
        } catch (Exception e) {
            System.err.println("Error fetching data for table " + tableName + ": " + e.getMessage());
            return "";
        }
    }
}
