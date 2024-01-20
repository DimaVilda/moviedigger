package com.backbase.moviesdigger.migration;

import com.backbase.moviesdigger.config.it.BaseIntegrationTestConfig;
import com.backbase.moviesdigger.config.it.IntegrationTest;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest
public class Migration202401IT extends BaseIntegrationTestConfig {

    @Test
    public void shouldCreateAllRequiredTablesFromMigrationScript() {
        try (Connection conn = DriverManager.getConnection(
                postgresqlContainer.getJdbcUrl(),
                postgresqlContainer.getUsername(),
                postgresqlContainer.getPassword());
             Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM information_schema.tables WHERE table_schema = 'public';");

            Set<String> tableNames = new HashSet<>();
            while (rs.next()) {
                tableNames.add(rs.getString("table_name"));
            }

            assertTrue(tableNames.contains("user_information"));
            assertTrue(tableNames.contains("rating"));
            assertTrue(tableNames.contains("movie"));

        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }
}
