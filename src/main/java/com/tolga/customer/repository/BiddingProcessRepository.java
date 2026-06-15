package com.tolga.customer.repository;

import com.tolga.customer.model.BiddingProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BiddingProcessRepository extends JpaRepository<BiddingProcess, Long> {
    // Tüm veritabanı kaydetme (save), silme, listeleme komutları artık buradan akacak.
}