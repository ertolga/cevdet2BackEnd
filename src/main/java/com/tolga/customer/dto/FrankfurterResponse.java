package com.tolga.customer.dto;

import java.util.Map;

/**
 * Frankfurter API'den dönen döviz kuru JSON yapısını
 * karşılayan Java DTO sınıfı.
 */
public class FrankfurterResponse {
    private Double amount;
    private String base;
    private String date;
    private Map<String, Double> rates; // İçinde "TRY" -> 35.42 gibi dinamik map tutacak

    // Boş Constructor
    public FrankfurterResponse() {
    }

    // Yardımcı Metot: Doğrudan TRY kurunu çekebilmek için
    public Double getTryRate() {
        if (rates != null && rates.containsKey("TRY")) {
            return rates.get("TRY");
        }
        return null;
    }

    // GETTER & SETTER METOTLARI
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getBase() { return base; }
    public void setBase(String base) { this.base = base; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public Map<String, Double> getRates() { return rates; }
    public void setRates(Map<String, Double> rates) { this.rates = rates; }
}