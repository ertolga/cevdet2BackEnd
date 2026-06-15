package com.tolga.customer.dto;

public class CustomerResponse {
    public Long id;
    public String firstName; // fullName SİLİNDİ, firstName GELDİ
    public String lastName;  // lastName GELDİ
    public String email;
    public String phone;
    public String nlIban;
    public String trIban;
    public boolean isProfileComplete; // Yeni eklenen profil durum kilidi
}