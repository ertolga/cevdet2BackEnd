package com.tolga.customer.controller;

import com.tolga.customer.dto.TransferInstruction;
import com.tolga.customer.service.Pacs008GeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequestMapping("/api/test") // Tarayıcıdan bu adrese çağrı atacağız
public class PacsTestController {

    @Autowired
    private Pacs008GeneratorService pacsService;

    // Tarayıcıdan tetikleyeceğimiz test kapısı
    @GetMapping("/send")
    public String sendTestTransfer(
            @RequestParam String isim,
            @RequestParam double tutar,
            @RequestParam String gonderenIban,
            @RequestParam String aliciIban
    ) {
        // Testi kolaylaştırmak için BIC kodlarını ve benzersiz ID'yi otomatik üretiyoruz
        String rastgeleE2EId = "E2E-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String testDebtorBic = "ABNANL2AXXX"; // Varsayılan Hollanda Bankası
        String testCreditorBic = "TCZATR2AXXX"; // Varsayılan Türk Bankası

        // Kutumuzu dolduruyoruz
        TransferInstruction talimat = new TransferInstruction(
                rastgeleE2EId,
                tutar,
                isim,
                gonderenIban,
                aliciIban,
                testDebtorBic,
                testCreditorBic
        );

        // Motorumuza talimatı gönderiyoruz ve dönen cevabı ekrana basıyoruz
        return pacsService.addTransferToQueue(talimat);
    }
}