package com.enviro.assessment.junior.fanelesibongesithole.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Secondary safety net after startup — primary migrations run via schema.sql before Hibernate DDL.
 */
@Component
@Order(1)
public class SchemaMigration implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SchemaMigration.class);

    private final JdbcTemplate jdbc;

    public SchemaMigration(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(String... args) {
        if (!tableExists("USERS")) {
            return;
        }
        addColumnIfMissing("phone", "VARCHAR(255)");
        addColumnIfMissing("bio", "VARCHAR(2000)");
        addColumnIfMissing("role", "VARCHAR(255) DEFAULT 'Institutional Investor'");
        addColumnIfMissing("verified", "BOOLEAN DEFAULT TRUE");
        addColumnIfMissing("security_progress", "INT DEFAULT 75");
        addColumnIfMissing("two_factor_enabled", "BOOLEAN DEFAULT FALSE");
        addColumnIfMissing("notify_portfolio", "BOOLEAN DEFAULT TRUE");
        addColumnIfMissing("notify_withdrawals", "BOOLEAN DEFAULT TRUE");
        addColumnIfMissing("notify_compliance", "BOOLEAN DEFAULT TRUE");
        addColumnIfMissing("notify_reports", "BOOLEAN DEFAULT TRUE");
        addColumnIfMissing("notify_marketing", "BOOLEAN DEFAULT FALSE");
        backfillNulls();
    }

    private boolean tableExists(String tableName) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE UPPER(TABLE_NAME) = ?",
                Integer.class,
                tableName.toUpperCase());
        return count != null && count > 0;
    }

    private boolean columnExists(String column) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE UPPER(TABLE_NAME) = 'USERS' AND UPPER(COLUMN_NAME) = ?",
                Integer.class,
                column.toUpperCase());
        return count != null && count > 0;
    }

    private void addColumnIfMissing(String column, String definition) {
        if (columnExists(column)) {
            return;
        }
        try {
            jdbc.execute("ALTER TABLE users ADD COLUMN " + column + " " + definition);
            log.info("Added missing column users.{}", column);
        } catch (Exception e) {
            log.warn("Could not add column users.{}: {}", column, e.getMessage());
        }
    }

    private void backfillNulls() {
        jdbc.update("UPDATE users SET role = 'Institutional Investor' WHERE role IS NULL");
        jdbc.update("UPDATE users SET verified = TRUE WHERE verified IS NULL");
        jdbc.update("UPDATE users SET security_progress = 75 WHERE security_progress IS NULL");
        jdbc.update("UPDATE users SET two_factor_enabled = FALSE WHERE two_factor_enabled IS NULL");
        jdbc.update("UPDATE users SET notify_portfolio = TRUE WHERE notify_portfolio IS NULL");
        jdbc.update("UPDATE users SET notify_withdrawals = TRUE WHERE notify_withdrawals IS NULL");
        jdbc.update("UPDATE users SET notify_compliance = TRUE WHERE notify_compliance IS NULL");
        jdbc.update("UPDATE users SET notify_reports = TRUE WHERE notify_reports IS NULL");
        jdbc.update("UPDATE users SET notify_marketing = FALSE WHERE notify_marketing IS NULL");
    }
}
