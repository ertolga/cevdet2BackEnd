package com.tolga.customer.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/staff-api") // 🚨 ÇAKIŞMAYI ÖNLEYEN VE İZOLE EDEN TEMEL ADRES
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class StaffController {

    private final JdbcTemplate jdbcTemplate;

    public StaffController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Rota: /staff-api/customers -> CustomerController ile asla çakışmaz!
    @GetMapping("/customers")
    public List<Map<String, Object>> getCustomersForMonitoring() {
        String sql = "SELECT id, first_name, last_name, email, phone, nl_iban, tr_iban, is_profile_complete FROM customer ORDER BY id DESC";
        return jdbcTemplate.queryForList(sql);
    }

    // Rota: /staff-api/transactions
    @GetMapping("/transactions")
    public List<Map<String, Object>> getTransactionsForMonitoring() {
        String sql = "SELECT b.id, b.created_at, b.calculated_eur_amount, b.requested_try_amount, b.applied_rate, " +
                "b.flat_fee_eur, b.spread_percentage, b.bidding_status, " +
                "c.first_name, c.last_name " +
                "FROM bidding_process b " +
                "INNER JOIN customer c ON b.customer_id = c.id " +
                "ORDER BY b.id DESC";
        return jdbcTemplate.queryForList(sql);
    }

    // Rota: /staff-api/config
    @GetMapping("/config")
    public Map<String, Object> getSystemConfig() {
        String sql = "SELECT spread_percentage, flat_fee_eur FROM system_config WHERE id = 1";
        return jdbcTemplate.queryForMap(sql);
    }

    // Rota: /staff-api/config
    @PostMapping("/config")
    public ResponseEntity<?> updateSystemConfig(@RequestBody Map<String, Object> body) {
        try {
            Double spreadPercentage = Double.parseDouble(body.get("spreadPercentage").toString());
            Double flatFeeEur = Double.parseDouble(body.get("flatFeeEur").toString());

            String sql = "UPDATE system_config SET spread_percentage = ?, flat_fee_eur = ? WHERE id = 1";
            jdbcTemplate.update(sql, spreadPercentage, flatFeeEur);

            return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Configuration updated successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", e.getMessage()));
        }
    }
}