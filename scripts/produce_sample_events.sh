#!/usr/bin/env bash
set -euo pipefail

HOST_BOOTSTRAP=${KAFKA_BOOTSTRAP:-localhost:9092}
CONTAINER_BOOTSTRAP=${KAFKA_BOOTSTRAP_FOR_CONTAINER:-kafka:29092}
BOOTSTRAP=$HOST_BOOTSTRAP
TOPIC=payment_events

events='{"eventId":"evt-1","occurredAt":"2024-01-01T10:00:00Z","type":"AUTHORIZED","amountCents":1000,"currency":"USD","paymentId":"pay-123","customerId":"cust-1"}
{"eventId":"evt-2","occurredAt":"2024-01-01T11:00:00Z","type":"CAPTURED","amountCents":1000,"currency":"USD","paymentId":"pay-123","customerId":"cust-1"}
{"eventId":"evt-3","occurredAt":"2024-01-02T09:00:00Z","type":"REFUNDED","amountCents":500,"currency":"USD","paymentId":"pay-123","customerId":"cust-1"}'

echo "Producing sample events to ${BOOTSTRAP}/${TOPIC}"

run_compose() {
  if command -v docker >/dev/null 2>&1 && docker compose version >/dev/null 2>&1; then
    docker compose "$@"
    return
  fi

  if command -v docker-compose >/dev/null 2>&1; then
    docker-compose "$@"
    return
  fi

  return 1
}

if command -v kafka-console-producer >/dev/null 2>&1; then
  printf "%s" "$events" | kafka-console-producer --bootstrap-server "$BOOTSTRAP" --topic "$TOPIC"
elif run_compose ps >/dev/null 2>&1; then
  echo "kafka-console-producer not found locally; sending via docker compose exec kafka"
  printf "%s" "$events" | run_compose exec -T kafka bash -lc "kafka-console-producer --bootstrap-server '$CONTAINER_BOOTSTRAP' --topic '$TOPIC'"
else
  echo "kafka-console-producer not available locally and docker compose not detected. Install Kafka CLIs or run within the compose stack." >&2
  exit 1
fi
