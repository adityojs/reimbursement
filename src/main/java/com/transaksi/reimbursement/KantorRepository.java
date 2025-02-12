package com.transaksi.reimbursement;

import org.springframework.data.jpa.repository.JpaRepository;

public interface KantorRepository extends JpaRepository<Kantor, Long> {
    // Custom query methods can be added if needed
}
