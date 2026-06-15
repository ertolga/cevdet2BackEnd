package com.tolga.customer.controller;

import com.tolga.customer.dto.CustomerRequest;
import com.tolga.customer.dto.CustomerResponse;
import com.tolga.customer.dto.UpdateCustomerRequest; // Az önce Android için yazdığımız modelin backend karşılığı
import com.tolga.customer.model.Customer;
import com.tolga.customer.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    // 1. YENİ MÜŞTERİ KAYDI (İsim ve Soyisim Ayrı)
    @PostMapping("/create")
    public CustomerResponse create(@RequestBody CustomerRequest request) {

        // Java kodundaki yeni Constructor yapımıza göre nesneyi üretiyoruz
        Customer customer = new Customer(
                request.firstName, // fullName GİTTİ, firstName GELDİ
                request.lastName,  // lastName GELDİ
                request.email,
                request.phone,
                request.password
        );

        Customer saved = service.save(customer);

        // Android'e döneceğimiz cevap paketini hazırlıyoruz
        CustomerResponse response = new CustomerResponse();
        response.id = saved.getId();
        response.firstName = saved.getFirstName(); // Yeni alan atandı
        response.lastName = saved.getLastName();   // Yeni alan atandı
        response.email = saved.getEmail();
        response.phone = saved.getPhone();
        response.isProfileComplete = saved.isProfileComplete();

        return response;
    }

    // 2. TÜM MÜŞTERİLERİ LİSTELEME
    @GetMapping
    public List<Customer> getAll() {
        return service.findAll();
    }

    // 2.5. TEK BİR MÜŞTERİYİ ID İLE GETİRME
    @GetMapping("/{id}")
    public CustomerResponse getById(@PathVariable Long id) {
        Customer customer = service.findById(id);

        CustomerResponse response = new CustomerResponse();
        response.id = customer.getId();
        response.firstName = customer.getFirstName();
        response.lastName = customer.getLastName();
        response.email = customer.getEmail();
        response.phone = customer.getPhone();
        response.isProfileComplete = customer.isProfileComplete();

        // 🚨 BUG FIX: Veritabanındaki IBAN'ları backend response paketine mühürlüyoruz!
        response.nlIban = customer.getNlIban();
        response.trIban = customer.getTrIban();

        return response;
    }

    // 3. PROFİL TAMAMLAMA / GÜNCELLEME ENDPOINT'İ (Bug Düzeltilmiş Hali)
    @PutMapping("/update/{id}")
    public CustomerResponse updateCustomer(
            @PathVariable Long id,
            @RequestBody UpdateCustomerRequest request
    ) {
        Customer customer = service.findById(id);

        if (customer == null) {
            throw new RuntimeException("Customer not found with id: " + id);
        }

        // 🚨 BUG FIX: IBAN alanları boş veya sadece boşluktan mı ibaret? Kontrol et!
        if (request.nlIban == null || request.nlIban.trim().isEmpty() ||
                request.trIban == null || request.trIban.trim().isEmpty()) {
            throw new IllegalArgumentException("Hollanda ve Türkiye IBAN alanları boş bırakılamaz!");
        }

        // Bilgiler geçerliyse kaydetmeye devam et
        customer.setFirstName(request.firstName);
        customer.setLastName(request.lastName);
        customer.setPhone(request.phone);
        customer.setNlIban(request.nlIban);
        customer.setTrIban(request.trIban);

        // Sadece geçerli veri geldiğinde profil tamamlandı de!
        customer.setProfileComplete(true);

        Customer updated = service.save(customer);

        CustomerResponse response = new CustomerResponse();
        response.id = updated.getId();
        response.firstName = updated.getFirstName();
        response.lastName = updated.getLastName();
        response.email = updated.getEmail();
        response.phone = updated.getPhone();
        response.isProfileComplete = updated.isProfileComplete();

        return response;
    }


    // 4. GİRİŞ YAPMA / DOĞRULAMA ENDPOINT'İ
    @PostMapping("/login")
    public org.springframework.http.ResponseEntity<?> loginCustomer(@RequestBody java.util.Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        // Service katmanından email ile müşteriyi bulmasını istiyoruz
        Customer customer = service.findByEmail(email);

        if (customer != null && customer.getPassword().equals(password)) {
            // Şifre eşleştiyse Android'in beklediği Response paketini hazırlıyoruz
            CustomerResponse response = new CustomerResponse();
            response.id = customer.getId();
            response.firstName = customer.getFirstName();
            response.lastName = customer.getLastName();
            response.email = customer.getEmail();
            response.phone = customer.getPhone();
            response.isProfileComplete = customer.isProfileComplete();

            return org.springframework.http.ResponseEntity.ok(response);
        } else {
            // Kullanıcı bulunamadı veya şifre yanlışsa 401 Unauthorized dönüyoruz
            return org.springframework.http.ResponseEntity.status(401).body("Credentials not matching");
        }
    }
}