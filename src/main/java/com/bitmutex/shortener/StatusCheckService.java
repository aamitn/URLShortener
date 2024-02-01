package com.bitmutex.shortener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.HashMap;
@Slf4j
@Service
public class StatusCheckService {

    private final JdbcTemplate jdbcTemplate;

    @Value("${database.port}")  // Inject the server port from application properties
    private String dbPort;

    @Value("${database.name}")  // Inject the server port from application properties
    private String dbName;

    @Autowired
    public StatusCheckService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Integer> getServerStatus() {
        Map<String, Integer> status = new HashMap<>();
        // Add logic to check the server status
        boolean isServerHealthy = checkServerHealth();
        if(isServerHealthy)
            log.info("Server healthy");
        status.put("server_status", isServerHealthy ? 1 : 0);
        return status;
    }

    private boolean checkServerHealth() {
        try {
            String sql = "SELECT SCHEMA_NAME\n" +
                    "  FROM INFORMATION_SCHEMA.SCHEMATA\n" +
                    " WHERE SCHEMA_NAME = '" + dbName + "'";
            // Execute a simple query to check the database connection
            jdbcTemplate.execute(sql);
            log.info("DB connection healthy");
            // Check if the required table exists
            boolean isTableExists = doesTableExist("analytics");
            log.info("DB Tables Found");
            // You can add additional checks here based on your requirements

            return isTableExists;
        } catch (Exception e) {
            return false; // Database connection failed or table does not exist
        }
    }

    private boolean doesTableExist(String tableName) {
        try {
            // Execute a query to check if the table exists
            jdbcTemplate.execute("SELECT 1 FROM " + tableName + " WHERE 1 = 0");
            return true; // Table exists
        } catch (Exception e) {
            return false; // Table does not exist
        }
    }
}
