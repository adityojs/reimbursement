package com.transaksi.reimbursement;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalRepository extends JpaRepository<Medical, Long> {
        // Custom query methods can be added if needed
}
