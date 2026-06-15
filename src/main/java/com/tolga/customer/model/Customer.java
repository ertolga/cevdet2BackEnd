package com.tolga.customer.model;

import jakarta.persistence.*;

@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "password")
    private String password;

    @Column(name = "nl_iban")
    private String nlIban;

    @Column(name = "tr_iban") // Yeni eklediğimiz Türkiye IBAN'ı
    private String trIban;

    @Column(name = "is_profile_complete") // Yeni profil tamamlama kilidi
    private boolean isProfileComplete = false;

    // 1. Boş Constructor (JPA için şarttır)
    public Customer() {
    }

    // 2. Dolu Constructor (Kayıt esnasında kolay nesne üretmek için)
    public Customer(String firstName, String lastName, String email, String phone, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.isProfileComplete = false; // İlk kayıt olanın profili her zaman eksiktir (false)
    }

    // 3. GETTER & SETTER METOTLARI

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNlIban() {
        return nlIban;
    }

    public void setNlIban(String nlIban) {
        this.nlIban = nlIban;
    }

    public String getTrIban() {
        return trIban;
    }

    public void setTrIban(String trIban) {
        this.trIban = trIban;
    }

    public boolean isProfileComplete() {
        return isProfileComplete;
    }

    public void setProfileComplete(boolean profileComplete) {
        isProfileComplete = profileComplete;
    }
}