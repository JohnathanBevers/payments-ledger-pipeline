package com.example.paymentsledger;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LedgerRepository extends JpaRepository<LedgerEntry, UUID> {
    boolean existsByEventId(String eventId);

    Optional<LedgerEntry> findByEventId(String eventId);

    @Query("SELECT new com.example.paymentsledger.ReconciliationReport(CAST(le.occurredAt AS date), le.type, SUM(le.amountCents)) " +
            "FROM LedgerEntry le " +
            "WHERE le.occurredAt >= :start AND le.occurredAt < :end " +
            "GROUP BY CAST(le.occurredAt AS date), le.type")
    List<ReconciliationReport> dailyTotals(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);
}
