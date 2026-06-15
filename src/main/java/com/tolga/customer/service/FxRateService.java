package com.tolga.customer.service;

import com.tolga.customer.dto.FrankfurterResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FxRateService {

    private final RestTemplate restTemplate;
    // Frankfurter API URL adresi
    private final String FRANKFURTER_URL = "https://api.frankfurter.dev/v1/latest?base=EUR&symbols=TRY";

    public FxRateService() {
        this.restTemplate = new RestTemplate(); // HTTP istekleri atacak motor
    }

    /**
     * Akış şemasındaki "Obtain live rate from FrAPI" adımı.
     * Frankfurter API'ye bağlanıp canlı EUR/TRY kurunu getirir.
     */
    public Double getLiveEurToTryRate() {
        try {
            // API'ye istek atıp yanıtı doğrudan DTO sınıfımıza eşliyoruz
            FrankfurterResponse response = restTemplate.getForObject(FRANKFURTER_URL, FrankfurterResponse.class);

            if (response != null && response.getTryRate() != null) {
                return response.getTryRate(); // Başarılıysa kuru dön (Örn: 35.42)
            }
        } catch (Exception e) {
            // Ağ hatası veya API'nin çökme durumunda log basalım
            System.err.println("Frankfurter API'den kur çekilirken hata oluştu: " + e.getMessage());
        }
        return null; // Bir şeyler ters giderse yedek plan (fail-safe) için null dönecek
    }
}