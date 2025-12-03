package com.example.paymentsledger;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ledger_entries")
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String eventId;

    @Column(nullable = false)
    private OffsetDateTime occurredAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentEventType type;

    @Column(nullable = false)
    private long amountCents;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private String paymentId;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    protected LedgerEntry() {
    }

    public LedgerEntry(PaymentEvent event, OffsetDateTime createdAt) {
        this.eventId = event.eventId();
        this.occurredAt = event.occurredAt();
        this.type = event.type();
        this.amountCents = event.amountCents();
        this.currency = event.currency();
        this.paymentId = event.paymentId();
        this.customerId = event.customerId();
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public OffsetDateTime getOccurredAt() {
        return occurredAt;
    }

    public PaymentEventType getType() {
        return type;
    }

    public long getAmountCents() {
        return amountCents;
    }

    public String getCurrency() {
        return currency;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
