package com.tolga.customer.controller;

import com.tolga.customer.service.TrAmlScreeningService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/staff/tr-aml-test") // Tünelden pürüzsüz geçecek güvenli TR yolu
@CrossOrigin(origins = "*", allowedHeaders = "*") // Tarayıcı güvenlik kilitlerini kıran anahtar
public class TrAmlTestController {

    private final TrAmlScreeningService trAmlScreeningService;

    public TrAmlTestController(TrAmlScreeningService trAmlScreeningService) {
        this.trAmlScreeningService = trAmlScreeningService;
    }

    @GetMapping
    public ResponseEntity<TrAmlScreeningService.TrScreeningResult> screen(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dob,
            @RequestParam(required = false) String identityNumber) {

        // Gelen bilgileri doğrudan bizim akıllı TR arama motoruna gönderiyoruz
        TrAmlScreeningService.TrScreeningResult result = trAmlScreeningService.screenCustomer(firstName, lastName, dob, identityNumber);
        return ResponseEntity.ok(result);
    }
}