#!/usr/bin/env bash
set -euo pipefail

HOST_BOOTSTRAP=${KAFKA_BOOTSTRAP:-localhost:9092}
CONTAINER_BOOTSTRAP=${KAFKA_BOOTSTRAP_FOR_CONTAINER:-kafka:29092}
BOOTSTRAP=$HOST_BOOTSTRAP
TOPIC=payment_events
READY_TIMEOUT=${KAFKA_READY_TIMEOUT:-60}
SLEEP_SECONDS=2

events=$(cat <<'EOF'
{"eventId":"evt-1","occurredAt":"2024-01-01T10:00:00Z","type":"AUTHORIZED","amountCents":1000,"currency":"USD","paymentId":"pay-123","customerId":"cust-1"},
{"eventId":"evt-2","occurredAt":"2024-01-01T11:00:00Z","type":"CAPTURED","amountCents":1000,"currency":"USD","paymentId":"pay-123","customerId":"cust-1"},
{"eventId":"evt-3","occurredAt":"2024-01-02T09:00:00Z","type":"REFUNDED","amountCents":500,"currency":"USD","paymentId":"pay-123","customerId":"cust-1"}
EOF
)

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

wait_for_topic() {
  local check_fn=$1
  local start_time=$SECONDS
  echo "Waiting for Kafka broker to be ready and topic '$TOPIC' to exist (timeout: ${READY_TIMEOUT}s)..."

  while (( SECONDS - start_time < READY_TIMEOUT )); do
    if "$check_fn"; then
      echo "Kafka is ready and topic '$TOPIC' is available."
      return 0
    fi

    echo "Broker not ready or topic '$TOPIC' missing; retrying in ${SLEEP_SECONDS}s..."
    sleep "$SLEEP_SECONDS"
  done

  echo "Timed out after ${READY_TIMEOUT}s waiting for Kafka broker and topic '$TOPIC'." >&2
  return 1
}

topic_check_local() {
  kafka-topics --bootstrap-server "$BOOTSTRAP" --topic "$TOPIC" --describe >/dev/null 2>&1
}

topic_check_compose() {
  run_compose exec -T kafka bash -lc "kafka-topics --bootstrap-server '$CONTAINER_BOOTSTRAP' --topic '$TOPIC' --describe" >/dev/null 2>&1
}

if command -v kafka-console-producer >/dev/null 2>&1; then
  if ! command -v kafka-topics >/dev/null 2>&1; then
    echo "kafka-topics is required for readiness checks when producing locally. Please install Kafka CLIs or use docker compose." >&2
    exit 1
  fi

  wait_for_topic topic_check_local
  printf "%s" "$events" | kafka-console-producer --bootstrap-server "$BOOTSTRAP" --topic "$TOPIC"
elif run_compose ps >/dev/null 2>&1; then
  echo "kafka-console-producer not found locally; sending via docker compose exec kafka"
  wait_for_topic topic_check_compose
  printf "%s" "$events" | run_compose exec -T kafka bash -lc "kafka-console-producer --bootstrap-server '$CONTAINER_BOOTSTRAP' --topic '$TOPIC'"
else
  echo "kafka-console-producer not available locally and docker compose not detected. Install Kafka CLIs or run within the compose stack." >&2
  exit 1
fi
