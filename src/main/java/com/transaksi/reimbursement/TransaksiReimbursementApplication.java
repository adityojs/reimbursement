package com.transaksi.reimbursement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EnableJpaRepositories(basePackages = "com.transaksi.reimbursement")
@EntityScan(basePackages = "com.transaksi.reimbursement")
@SpringBootApplication
public class TransaksiReimbursementApplication {
	public static void main(String[] args) {
		SpringApplication.run(TransaksiReimbursementApplication.class, args);
	}
}
