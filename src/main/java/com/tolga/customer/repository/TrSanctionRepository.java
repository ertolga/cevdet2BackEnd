package com.tolga.customer.repository;

import com.tolga.customer.model.TrSanctionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TrSanctionRepository extends JpaRepository<TrSanctionEntity, Long> {

    // 1. Akıllı İsim Arama: Girilen isim, veritabanındaki tümleşik ismin (whole_name) içinde geçiyor mu?
    @Query("SELECT t FROM TrSanctionEntity t WHERE LOWER(t.wholeName) LIKE LOWER(CONCAT('%', :searchName, '%'))")
    List<TrSanctionEntity> findByWholeNameContaining(@Param("searchName") String searchName);

    // 2. Çapraz Kontrol: Hem isim içeren hem de doğum tarihi birebir uyuşan kritik kayıtlar
    @Query("SELECT t FROM TrSanctionEntity t WHERE LOWER(t.wholeName) LIKE LOWER(CONCAT('%', :searchName, '%')) AND t.dateOfBirth = :dob")
    List<TrSanctionEntity> findByWholeNameAndDob(@Param("searchName") String searchName, @Param("dob") LocalDate dob);

    // 3. Nokta Atışı Kimlik Sorgu: T.C. Kimlik No veya Pasaport No üzerinden doğrudan bulma
    List<TrSanctionEntity> findByIdentityNumberOrPassportNumber(String identityNumber, String passportNumber);
}