#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
CHECK_COMPARE="${CHECK_COMPARE:-0}"

wait_http() {
  local url="$1"
  local tries="${2:-60}"
  for i in $(seq 1 "$tries"); do
    if curl -fsS "$url" >/dev/null 2>&1; then
      return 0
    fi
    sleep 1
  done
  echo "timeout: $url" >&2
  return 1
}

echo "[1/3] wait: $BASE_URL/"
wait_http "$BASE_URL/"

echo "[2/3] check: /"
curl -fsS "$BASE_URL/" | grep -q '"status":"ok"'

echo "[3/3] check: /health-db"
curl -fsS "$BASE_URL/health-db" | grep -q '"dbOk":true'

if [ "$CHECK_COMPARE" = "1" ]; then
  echo "[compare] check: /compare-db"
  # mismatch면 실패 처리
  curl -fsS "$BASE_URL/compare-db" | grep -q '"status":"ok"'
fi

echo "OK"
