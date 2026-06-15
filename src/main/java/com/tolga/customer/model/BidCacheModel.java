package com.tolga.customer.model;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty; // Added for explicit JSON key naming

public class BidCacheModel {
    private String bidId;
    private Long customerId;
    private Double requestedTryAmount;
    private String biddingStatus; // "PROCESSING", "OFFERED_TO_CUSTOMER", "ACCEPTED", "EXPIRED"
    private LocalDateTime createdAt;

    // YENİ EKLENEN ALANLAR (Hesaplama sonrası dolacaklar)
    private Double calculatedEurAmount;
    private Double appliedRate;
    private String rateSource; // "FRANKFURTER"

    @JsonProperty("flat_fee_eur") // Forces Jackson to match Android's GSON configuration
    private Double flatFeeEur; // CEVDET2: Fixed handling charge slot

    // Dolu Constructor (Aynen kalıyor)
    public BidCacheModel(String bidId, Long customerId, Double requestedTryAmount) {
        this.bidId = bidId;
        this.customerId = customerId;
        this.requestedTryAmount = requestedTryAmount;
        this.biddingStatus = "PROCESSING";
        this.createdAt = LocalDateTime.now();
        this.flatFeeEur = 0.0; // Default initializing state value
    }

    // CEVDET2: FLAT FEE GETTER & SETTER METOTLARI
    public Double getFlatFeeEur() { return flatFeeEur; }
    public void setFlatFeeEur(Double flatFeeEur) { this.flatFeeEur = flatFeeEur; }

    // Add this field right under flatFeeEur:
    @JsonProperty("spread_percentage")
    private Double spreadPercentage = 0.0;

    // Add its Getter and Setter methods:
    public Double getSpreadPercentage() { return spreadPercentage; }
    public void setSpreadPercentage(Double spreadPercentage) { this.spreadPercentage = spreadPercentage; }

    // YENİ ALANLARIN GETTER & SETTER METOTLARI
    public Double getCalculatedEurAmount() { return calculatedEurAmount; }
    public void setCalculatedEurAmount(Double calculatedEurAmount) { this.calculatedEurAmount = calculatedEurAmount; }

    public Double getAppliedRate() { return appliedRate; }
    public void setAppliedRate(Double appliedRate) { this.appliedRate = appliedRate; }

    public String getRateSource() { return rateSource; }
    public void setRateSource(String rateSource) { this.rateSource = rateSource; }

    // ESKİ GETTER & SETTER METOTLARI
    public String getBidId() { return bidId; }
    public void setBidId(String bidId) { this.bidId = bidId; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Double getRequestedTryAmount() { return requestedTryAmount; }
    public void setRequestedTryAmount(Double requestedTryAmount) { this.requestedTryAmount = requestedTryAmount; }
    public String getBiddingStatus() { return biddingStatus; }
    public void setBiddingStatus(String biddingStatus) { this.biddingStatus = biddingStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}