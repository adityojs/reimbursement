package com.transaksi.reimbursement;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Reimbursement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double amount;
    private String reason;

    // Constructors
    public Reimbursement() {
    }

    public Reimbursement(String name, double amount, String reason) {
        this.name = name;
        this.amount = amount;
        this.reason = reason;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "Reimbursement{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                ", reason='" + reason + '\'' +
                '}';
    }
}
