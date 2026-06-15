package com.tolga.customer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bidding_process")
public class BiddingProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "requested_try_amount", nullable = false)
    private Double requestedTryAmount;

    @Column(name = "calculated_eur_amount", nullable = false)
    private Double calculatedEurAmount;

    @Column(name = "applied_rate", nullable = false)
    private Double appliedRate;

    @Column(name = "flat_fee_eur", nullable = true) // CEVDET2: New ledger persist slot
    private Double flatFeeEur;

    @Column(name = "spread_percentage", nullable = true) // CEVDET2: New ledger persist slot
    private Double spreadPercentage;

    @Column(name = "bidding_status", nullable = false)
    private String biddingStatus; // ACCEPTED, EXPIRED, COMPLETED

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Boş Constructor (JPA için şart)
    public BiddingProcess() {}

    // Güncellenmiş Parametreli Constructor (Yeni eklenen alanları içerir)
    public BiddingProcess(Long customerId, Double requestedTryAmount, Double calculatedEurAmount,
                          Double appliedRate, Double flatFeeEur, Double spreadPercentage, String biddingStatus) {
        this.customerId = customerId;
        this.requestedTryAmount = requestedTryAmount;
        this.calculatedEurAmount = calculatedEurAmount;
        this.appliedRate = appliedRate;
        this.flatFeeEur = flatFeeEur;
        this.spreadPercentage = spreadPercentage;
        this.biddingStatus = biddingStatus;
        this.createdAt = LocalDateTime.now();
    }

    // Getter ve Setter'lar
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Double getRequestedTryAmount() { return requestedTryAmount; }
    public void setRequestedTryAmount(Double requestedTryAmount) { this.requestedTryAmount = requestedTryAmount; }

    public Double getCalculatedEurAmount() { return calculatedEurAmount; }
    public void setCalculatedEurAmount(Double calculatedEurAmount) { this.calculatedEurAmount = calculatedEurAmount; }

    public Double getAppliedRate() { return appliedRate; }
    public void setAppliedRate(Double appliedRate) { this.appliedRate = appliedRate; }

    public Double getFlatFeeEur() { return flatFeeEur; }
    public void setFlatFeeEur(Double flatFeeEur) { this.flatFeeEur = flatFeeEur; }

    public Double getSpreadPercentage() { return spreadPercentage; }
    public void setSpreadPercentage(Double spreadPercentage) { this.spreadPercentage = spreadPercentage; }

    public String getBiddingStatus() { return biddingStatus; }
    public void setBiddingStatus(String biddingStatus) { this.biddingStatus = biddingStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}