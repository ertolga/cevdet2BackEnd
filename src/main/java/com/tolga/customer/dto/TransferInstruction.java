package com.tolga.customer.dto; // Burası değişti

public class TransferInstruction {
    private String endToEndId;
    private double amount;
    private String customerName;
    private String sourceIban;
    private String targetIban;
    private String debtorBic;
    private String creditorBic;

    // Constructor (Kutuyu doldurma yöntemi)
    public TransferInstruction(String endToEndId, double amount, String customerName,
                               String sourceIban, String targetIban, String debtorBic, String creditorBic) {
        this.endToEndId = endToEndId;
        this.amount = amount;
        this.customerName = customerName;
        this.sourceIban = sourceIban;
        this.targetIban = targetIban;
        this.debtorBic = debtorBic;
        this.creditorBic = creditorBic;
    }

    // Bilgileri geri okumak için Getter metotları
    public String getEndToEndId() { return endToEndId; }
    public double getAmount() { return amount; }
    public String getCustomerName() { return customerName; }
    public String getSourceIban() { return sourceIban; }
    public String getTargetIban() { return targetIban; }
    public String getDebtorBic() { return debtorBic; }
    public String getCreditorBic() { return creditorBic; }
}