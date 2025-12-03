package com.example.paymentsledger;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PaymentEvent(
        String eventId,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX") OffsetDateTime occurredAt,
        PaymentEventType type,
        long amountCents,
        String currency,
        String paymentId,
        String customerId
) {
    public BigDecimal amount() {
        return BigDecimal.valueOf(amountCents, 2);
    }
}
