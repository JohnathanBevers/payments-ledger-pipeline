package com.example.paymentsledger;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LedgerService {
    private static final Logger log = LoggerFactory.getLogger(LedgerService.class);

    private final LedgerRepository repository;

    public LedgerService(LedgerRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void processEvent(PaymentEvent event) {
        if (repository.existsByEventId(event.eventId())) {
            log.info("Skipping duplicate event {}", event.eventId());
            return;
        }

        try {
            LedgerEntry entry = new LedgerEntry(event, OffsetDateTime.now());
            repository.save(entry);
            log.info("Stored ledger entry for event {}", event.eventId());
        } catch (DataIntegrityViolationException e) {
            log.info("Detected duplicate event {} via constraint", event.eventId());
        }
    }

    @Transactional(readOnly = true)
    public List<ReconciliationReport> reconciliationForRange(LocalDate startInclusive, LocalDate endExclusive) {
        OffsetDateTime start = startInclusive.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime end = endExclusive.atStartOfDay().atOffset(ZoneOffset.UTC);
        return repository.dailyTotals(start, end);
    }
}
