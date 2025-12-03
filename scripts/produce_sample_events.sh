#!/usr/bin/env bash
set -euo pipefail

BOOTSTRAP=${KAFKA_BOOTSTRAP:-localhost:9092}
TOPIC=payment_events

events='{"eventId":"evt-1","occurredAt":"2024-01-01T10:00:00Z","type":"AUTHORIZED","amountCents":1000,"currency":"USD","paymentId":"pay-123","customerId":"cust-1"}
{"eventId":"evt-2","occurredAt":"2024-01-01T11:00:00Z","type":"CAPTURED","amountCents":1000,"currency":"USD","paymentId":"pay-123","customerId":"cust-1"}
{"eventId":"evt-3","occurredAt":"2024-01-02T09:00:00Z","type":"REFUNDED","amountCents":500,"currency":"USD","paymentId":"pay-123","customerId":"cust-1"}'

echo "Producing sample events to ${BOOTSTRAP}/${TOPIC}"
printf "%s" "$events" | kafka-console-producer --bootstrap-server "$BOOTSTRAP" --topic "$TOPIC"
