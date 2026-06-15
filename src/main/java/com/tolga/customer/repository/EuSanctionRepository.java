package com.tolga.customer.repository;

// Az önce doğrulamış olduğumuz gerçek model import satırı:
import com.tolga.customer.model.Customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
// JpaRepository<Object, Long> yerine gerçeğini verdik; Spring artık isyan etmeyecek!
public interface EuSanctionRepository extends JpaRepository<Customer, Long> {

    @Query(value = "SELECT id, eu_reference_id, entity_type, full_name, is_primary_name, date_of_birth, birth_year, nationality, passport_number, legal_basis " +
            "FROM public.sanction_list_eu " +
            "WHERE LOWER(full_name) LIKE LOWER(CONCAT('%', :fullName, '%')) " +
            "AND (date_of_birth = :dob OR birth_year = :birthYear OR date_of_birth IS NULL)",
            nativeQuery = true)
    List<Object[]> searchPotentialHits(@Param("fullName") String fullName,
                                       @Param("dob") LocalDate dob,
                                       @Param("birthYear") Integer birthYear);
}