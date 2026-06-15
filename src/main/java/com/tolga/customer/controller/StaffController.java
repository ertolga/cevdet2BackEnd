package com.tolga.customer.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/staff")
@CrossOrigin(origins = "https://staff.decontra.nl")
public class StaffController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/customers")
    public List<Map<String, Object>> getCustomersForMonitoring() {
        String sql = "SELECT id, first_name, last_name, email, phone, is_profile_complete, nl_iban, tr_iban FROM customer ORDER BY id DESC";
        return jdbcTemplate.queryForList(sql);
    }

    @GetMapping("/transactions")
    public List<Map<String, Object>> getTransactionsForMonitoring() {
        // CEVDET2: Appended b.flat_fee_eur and b.spread_percentage to select tokens
        String sql = "SELECT b.id, b.created_at, b.calculated_eur_amount, b.requested_try_amount, b.applied_rate, " +
                "b.flat_fee_eur, b.spread_percentage, b.bidding_status, " +
                "c.first_name, c.last_name " +
                "FROM bidding_process b " +
                "INNER JOIN customer c ON b.customer_id = c.id " +
                "ORDER BY b.id DESC";
        return jdbcTemplate.queryForList(sql);
    }

    // 1. Retrieve current live spread and fees
    @GetMapping("/config")
    public Map<String, Object> getSystemConfig() {
        String sql = "SELECT spread_percentage, flat_fee_eur FROM system_config WHERE id = 1";
        return jdbcTemplate.queryForMap(sql);
    }

    // 2. Update live spread and fees dynamically
    @PostMapping("/config")
    public String updateSystemConfig(@RequestBody Map<String, Double> payload) {
        Double spread = payload.get("spread_percentage");
        Double fee = payload.get("flat_fee_eur");

        if (spread == null || fee == null) {
            throw new IllegalArgumentException("Missing values in payload");
        }

        String sql = "UPDATE system_config SET spread_percentage = ?, flat_fee_eur = ?, updated_at = CURRENT_TIMESTAMP WHERE id = 1";
        jdbcTemplate.update(sql, spread, fee);
        return "Configuration updated successfully";
    }
}