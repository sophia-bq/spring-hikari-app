package com.example.springhikari;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RestController
public class QueryController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private DataSource dataSource;

    @GetMapping("/test")
    public String test() {
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        return "Query result: " + result;
    }

    @GetMapping("/bulk/{count}")
    public String bulk(@PathVariable int count) {
        StringBuilder results = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i % 2 == 0) {
                // Read operation - forces connection to reader
                Integer result = jdbcTemplate.queryForObject("SELECT " + (i + 1), Integer.class);
                results.append("READ Query ").append(i + 1).append(": ").append(result).append("\n");
            } else {
                // Write operation - forces connection to writer
                jdbcTemplate.execute("SET @temp_var = " + (i + 1));
                Integer result = jdbcTemplate.queryForObject("SELECT @temp_var", Integer.class);
                results.append("WRITE Query ").append(i + 1).append(": ").append(result).append("\n");
            }
        }
        return results.toString();
    }

    @GetMapping("/read-write-test/{count}")
    public String readWriteTest(@PathVariable int count) {
        StringBuilder results = new StringBuilder();
        
        try (Connection conn = dataSource.getConnection()) {
            for (int i = 0; i < count; i++) {
                if (i % 2 == 0) {
                    // Switch to reader
                    conn.setReadOnly(true);
                    try (var stmt = conn.createStatement(); var rs = stmt.executeQuery("SELECT @@aurora_server_id")) {
                        rs.next();
                        String endpoint = rs.getString(1);
                        results.append("READ ").append(i + 1).append(" - Endpoint: ").append(endpoint).append("\n");
                    }
                } else {
                    // Switch to writer
                    conn.setReadOnly(false);
                    try (var stmt = conn.createStatement(); var rs = stmt.executeQuery("SELECT @@aurora_server_id")) {
                        rs.next();
                        String endpoint = rs.getString(1);
                        results.append("WRITE ").append(i + 1).append(" - Endpoint: ").append(endpoint).append("\n");
                    }
                }
            }
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
        
        return results.toString();
    }
}
