package com.example.paymentsledger;

import java.time.LocalDate;

public class ReconciliationReport {
    private final LocalDate date;
    private final PaymentEventType type;
    private final long totalCents;

    public ReconciliationReport(LocalDate date, PaymentEventType type, long totalCents) {
        this.date = date;
        this.type = type;
        this.totalCents = totalCents;
    }

    public LocalDate getDate() {
        return date;
    }

    public PaymentEventType getType() {
        return type;
    }

    public long getTotalCents() {
        return totalCents;
    }
}
