package com.tolga.customer.dto;

/**
 * Android uygulamadan gelen TRY transfer talebini
 * karşılayan Spring Boot DTO sınıfı.
 */
public class QuoteRequestDto {

    private Long customerId;
    private Double requestedTryAmount;

    // 1. Boş Constructor (Jackson kütüphanesinin JSON'ı nesneye çevirmesi için şarttır)
    public QuoteRequestDto() {
    }

    // 2. Dolu Constructor
    public QuoteRequestDto(Long customerId, Double requestedTryAmount) {
        this.customerId = customerId;
        this.requestedTryAmount = requestedTryAmount;
    }

    // 3. GETTER & SETTER METOTLARI

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Double getRequestedTryAmount() {
        return requestedTryAmount;
    }

    public void setRequestedTryAmount(Double requestedTryAmount) {
        this.requestedTryAmount = requestedTryAmount;
    }
}