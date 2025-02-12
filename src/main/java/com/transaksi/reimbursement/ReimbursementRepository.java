package com.transaksi.reimbursement;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReimbursementRepository extends JpaRepository<Reimbursement, Long> {
    // Custom query methods can be added if needed
}
