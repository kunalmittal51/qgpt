package com.example.qgpt.service;


import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SchemaService {
    private final DataSource dataSource;
    private final Map<String, List<String>> schemaCache;

    public SchemaService(DataSource dataSource) {
        this.dataSource = dataSource;
        this.schemaCache = new HashMap<>();
        loadSchema();
    }

    private void loadSchema() {
        try (var connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                List<String> columns = new ArrayList<>();

                ResultSet cols = metaData.getColumns(null, null, tableName, null);
                while (cols.next()) {
                    columns.add(String.format("%s (%s)",
                            cols.getString("COLUMN_NAME"),
                            cols.getString("TYPE_NAME")));
                }

                schemaCache.put(tableName, columns);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load database schema", e);
        }
    }

    public String generateSchemaContext() {
        StringBuilder context = new StringBuilder();
        context.append("Database Schema:\n\n");

        schemaCache.forEach((table, columns) -> {
            context.append("Table: ").append(table).append("\n");
            context.append("Columns:\n");
            columns.forEach(column -> context.append("- ").append(column).append("\n"));
            context.append("\n");
        });

        return context.toString();
    }
}