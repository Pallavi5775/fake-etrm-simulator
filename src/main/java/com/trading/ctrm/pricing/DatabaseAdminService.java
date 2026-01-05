package com.trading.ctrm.pricing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Database Administration Service
 * Provides utilities for database maintenance operations
 */
@Service
public class DatabaseAdminService {

    private static final Logger log = LoggerFactory.getLogger(DatabaseAdminService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Truncates all tables in the database
     * Use with caution - this will delete all data!
     */
    @Transactional
    public void truncateAllTables() {
        log.warn("Starting database truncation - all data will be lost!");

        try {
            // Disable foreign key checks
            jdbcTemplate.execute("SET session_replication_role = 'replica'");

            // Truncate tables in dependency order
            truncateTable("ctrm.valuation_result");
            truncateTable("ctrm.valuation_run");
            truncateTable("ctrm.scenario_result");
            truncateTable("ctrm.pnl_explain");

            // Options data
            truncateTable("ctrm.option_forward_curves");
            truncateTable("ctrm.option_volatility");
            truncateTable("ctrm.option_yield_curves");

            // Market data
            truncateTable("ctrm.market_prices");
            truncateTable("ctrm.price_curves");
            truncateTable("ctrm.yield_curves");
            truncateTable("ctrm.volatility_surfaces");
            truncateTable("ctrm.forecast_prices");
            truncateTable("ctrm.weather_data");
            truncateTable("ctrm.generation_forecast");

            // Trades
            truncateTable("ctrm.trade_legs");
            truncateTable("ctrm.trades");
            truncateTable("ctrm.trade_versions");
            truncateTable("ctrm.trade_events");

            // Instruments (inheritance hierarchy)
            truncateTable("ctrm.commodity_option_instruments");
            truncateTable("ctrm.power_forward_instruments");
            truncateTable("ctrm.gas_forward_instruments");
            truncateTable("ctrm.renewable_ppa_instruments");
            truncateTable("ctrm.commodity_swap_instruments");
            truncateTable("ctrm.freight_instruments");
            truncateTable("ctrm.instruments");

            // Reference data
            truncateTable("ctrm.commodities");
            truncateTable("ctrm.counterparties");
            truncateTable("ctrm.portfolios");

            // Users and security
            truncateTable("ctrm.user_roles");
            truncateTable("ctrm.users");
            truncateTable("ctrm.roles");

            // Risk and credit
            truncateTable("ctrm.credit_limits");

            // Re-enable foreign key checks
            jdbcTemplate.execute("SET session_replication_role = 'origin'");

            // Reset sequences after truncation
            resetSequences();

            log.info("Database truncation completed successfully");

        } catch (Exception e) {
            log.error("Error during database truncation", e);
            throw new RuntimeException("Database truncation failed", e);
        }
    }

    /**
     * Truncates a specific table
     */
    private void truncateTable(String tableName) {
        try {
            jdbcTemplate.execute("TRUNCATE TABLE " + tableName + " CASCADE");
            log.debug("Truncated table: {}", tableName);
        } catch (Exception e) {
            log.warn("Could not truncate table {}: {}", tableName, e.getMessage());
        }
    }

    /**
     * Resets all sequences to start from 1
     */
    @Transactional
    public void resetSequences() {
        log.info("Resetting database sequences");

        try {
            // Reset common sequences
            resetSequence("ctrm.instruments_id_seq");
            resetSequence("ctrm.commodities_id_seq");
            resetSequence("ctrm.trades_id_seq");
            resetSequence("ctrm.users_id_seq");
            resetSequence("ctrm.valuation_result_result_id_seq");
            resetSequence("ctrm.valuation_run_id_seq");
            // Add other sequences as needed

            log.info("Sequence reset completed");
        } catch (Exception e) {
            log.warn("Error resetting sequences: {}", e.getMessage());
        }
    }

    /**
     * Resets a specific sequence
     */
    private void resetSequence(String sequenceName) {
        try {
            jdbcTemplate.execute("ALTER SEQUENCE " + sequenceName + " RESTART WITH 1");
            log.debug("Reset sequence: {}", sequenceName);
        } catch (Exception e) {
            log.debug("Could not reset sequence {}: {}", sequenceName, e.getMessage());
        }
    }
}