package com.example.paymentsledger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class LedgerConsumer {
    private static final Logger log = LoggerFactory.getLogger(LedgerConsumer.class);

    private final LedgerService ledgerService;

    public LedgerConsumer(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @KafkaListener(topics = "payment_events", containerFactory = "kafkaListenerContainerFactory")
    public void consume(PaymentEvent event) {
        log.info("Received payment event {}", event.eventId());
        ledgerService.processEvent(event);
    }
}
