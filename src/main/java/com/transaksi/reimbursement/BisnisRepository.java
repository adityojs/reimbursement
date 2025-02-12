package com.transaksi.reimbursement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface BisnisRepository extends JpaRepository<Bisnis, Long> {
    // Custom query methods can be added if needed
}
