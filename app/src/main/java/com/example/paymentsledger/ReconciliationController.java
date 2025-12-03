package com.example.paymentsledger;

import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reconciliation")
public class ReconciliationController {

    private final LedgerService ledgerService;

    public ReconciliationController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @GetMapping
    public ResponseEntity<?> reconciliation(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "json") String format) {

        List<ReconciliationReport> reports = ledgerService.reconciliationForRange(start, end);

        if ("csv".equalsIgnoreCase(format)) {
            StringBuilder builder = new StringBuilder();
            builder.append("date,type,total_cents\n");
            for (ReconciliationReport report : reports) {
                builder.append(report.getDate()).append(',')
                        .append(report.getType()).append(',')
                        .append(report.getTotalCents()).append('\n');
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.csv")
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(builder.toString());
        }

        return ResponseEntity.ok(reports);
    }
}
