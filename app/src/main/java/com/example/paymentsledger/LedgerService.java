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

    /**
     * Computes reconciliation totals for the given date range, inclusive of both start and end dates.
     */
    @Transactional(readOnly = true)
    public List<ReconciliationReport> reconciliationForRange(LocalDate startInclusive, LocalDate endInclusive) {
        OffsetDateTime start = startInclusive.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime endExclusive = endInclusive.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        return repository.dailyTotals(start, endExclusive);
    }
}
