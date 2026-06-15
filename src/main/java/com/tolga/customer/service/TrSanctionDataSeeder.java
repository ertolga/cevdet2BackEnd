package com.tolga.customer.service;

import com.tolga.customer.model.TrSanctionEntity;
import com.tolga.customer.repository.TrSanctionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class TrSanctionDataSeeder implements CommandLineRunner {

    private final TrSanctionRepository trSanctionRepository;

    public TrSanctionDataSeeder(TrSanctionRepository trSanctionRepository) {
        this.trSanctionRepository = trSanctionRepository;
    }

    @Override
    public void run(String... args) {
        try {
            if (trSanctionRepository.count() == 0) {
                TrSanctionEntity suspect1 = new TrSanctionEntity();
                suspect1.setFirstName("Ahmet");
                suspect1.setLastName("Yılmaz");
                suspect1.setWholeName("Ahmet Yılmaz");
                suspect1.setDateOfBirth(LocalDate.of(1985, 5, 14));
                suspect1.setIdentityNumber("12345678901");
                suspect1.setMotherName("Fatma");
                suspect1.setFatherName("Mehmet");
                suspect1.setTrReferenceId("Resmi Gazete No: 32145");
                suspect1.setLegalBasis("Financing of Prohibited Organizations (Law No: 6415)");
                trSanctionRepository.save(suspect1);

                TrSanctionEntity suspect2 = new TrSanctionEntity();
                suspect2.setFirstName("Cem");
                suspect2.setLastName("Kaya");
                suspect2.setWholeName("Cem Kaya");
                suspect2.setDateOfBirth(LocalDate.of(1990, 11, 23));
                suspect2.setIdentityNumber("98765432109");
                suspect2.setMotherName("Ayşe");
                suspect2.setFatherName("Ali");
                suspect2.setTrReferenceId("Ankara ACM Karar No: 2024/88");
                suspect2.setLegalBasis("High-Risk Fraud and Asset Freeze Decree");
                trSanctionRepository.save(suspect2);

                System.out.println(">> [AML] TR Sanction List initialized successfully.");
            }
        } catch (Exception e) {
            System.out.println(">> [AML] Seeder data initialization log: " + e.getMessage());
        }
    }
}