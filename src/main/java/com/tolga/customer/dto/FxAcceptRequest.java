package com.tolga.customer.dto;

public class FxAcceptRequest {
    private String customerName; // Kontra hesap olduğu için tek isim yeterli
    private double amount;
    private String sourceIban;
    private String targetIban;

    // Spring Boot'un bunu okuyabilmesi için boş constructor şart
    public FxAcceptRequest() {}

    // Getter ve Setter metotları
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getSourceIban() { return sourceIban; }
    public void setSourceIban(String sourceIban) { this.sourceIban = sourceIban; }

    public String getTargetIban() { return targetIban; }
    public void setTargetIban(String targetIban) { this.targetIban = targetIban; }
}