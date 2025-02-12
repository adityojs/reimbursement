package com.transaksi.reimbursement;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DashboardRepository extends JpaRepository<Dashboard, Long> {
    List<Dashboard> findByName(String name);  // Add this method to find entries by user name
}


