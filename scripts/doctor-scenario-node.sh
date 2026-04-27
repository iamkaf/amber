#!/usr/bin/env bash
set -euo pipefail

if [ "$#" -lt 1 ] || [ "$#" -gt 2 ]; then
  echo "Usage: $0 <node> [timeout-seconds]" >&2
  exit 1
fi

node="$1"
timeout_seconds="${2:-180}"

log_step() {
  printf '==> %s\n' "$*"
}

log_detail() {
  printf '    %s\n' "$*"
}

cd "$(dirname "$0")/.."

if ! just list-nodes | grep -Fxq "$node"; then
  echo "Unknown node: $node" >&2
  exit 1
fi

version="${node%-*}"
loader="${node##*-}"
gradle_task=":$loader:$version:runClient"

case "$loader" in
  fabric)
    instance_path="fabric/versions/$version/runs/client/teakit/instance.json"
    ;;
  forge|neoforge)
    instance_path="$loader/versions/$version/run/teakit/instance.json"
    ;;
  *)
    echo "Unsupported loader: $loader" >&2
    exit 1
    ;;
esac

catalog="/home/kaf/code/mods/version-catalog/mc-$version/gradle/libs.versions.toml"
if [ ! -f "$catalog" ] || ! rg -q '^teakit = ' "$catalog"; then
  echo "TeaKit is not configured in the shared catalog for $version" >&2
  exit 1
fi

scenario_file="test/scenarios/amber/doctor-smoke.json"
log="/tmp/amber-$node.doctor.run.log"
result="/tmp/amber-$node.doctor.result.json"
health="/tmp/amber-$node.doctor.health.json"
rm -f "$instance_path" "$log" "$result" "$health"

log_step "Amber TeaKit scenario smoke check"
log_detail "node: $node"
log_detail "gradle task: $gradle_task"
log_detail "scenario: $scenario_file"
log_detail "TeaKit instance: $instance_path"
log_detail "run log: $log"
log_detail "result JSON: $result"

log_step "Starting Minecraft client with TeaKit"
./gradlew --configure-on-demand "$gradle_task" --console=plain \
  -Damber.withTeaKit=true \
  -Dteakit.autoWorld=true \
  -Dteakit.repoRoot="$PWD" \
  -Dteakit.scenarioRoot="$PWD" \
  >"$log" 2>&1 &
gradle_pid=$!

port=""
token=""
base_url=""

cleanup() {
  set +e
  if [ -f "$instance_path" ]; then
    port="$(jq -r '.port' "$instance_path" 2>/dev/null || true)"
    token="$(jq -r '.token' "$instance_path" 2>/dev/null || true)"
    if [ -n "$port" ] && [ -n "$token" ] && [ "$port" != "null" ] && [ "$token" != "null" ]; then
      curl -fsS -H "X-TeaKit-Token: $token" \
        -H 'Content-Type: application/json' \
        --data '{"delayMs":500}' \
        "http://localhost:$port/action/client/quit" >/dev/null 2>&1 || true
    fi
  fi
  if kill -0 "$gradle_pid" >/dev/null 2>&1; then
    wait "$gradle_pid" >/dev/null 2>&1 || true
  fi
}
trap cleanup EXIT

log_step "Waiting for TeaKit HTTP endpoint"
for _ in $(seq 1 "$timeout_seconds"); do
  if [ -f "$instance_path" ]; then
    port="$(jq -r '.port' "$instance_path" 2>/dev/null || true)"
    token="$(jq -r '.token' "$instance_path" 2>/dev/null || true)"
    base_url="http://localhost:$port"
    if [ -n "$port" ] && [ -n "$token" ] && [ "$port" != "null" ] && [ "$token" != "null" ] \
      && curl -fsS -H "X-TeaKit-Token: $token" "$base_url/health" >"$health" 2>/dev/null; then
      break
    fi
  fi
  if ! kill -0 "$gradle_pid" >/dev/null 2>&1; then
    wait "$gradle_pid"
    tail -n 160 "$log" >&2
    exit 1
  fi
  sleep 1
done

if [ ! -f "$health" ]; then
  echo "Timed out waiting for TeaKit instance at $instance_path" >&2
  tail -n 160 "$log" >&2
  exit 1
fi

log_detail "TeaKit endpoint: $base_url"

log_step "Waiting for generated singleplayer world"
world_ready=0
for _ in $(seq 1 "$timeout_seconds"); do
  if curl -fsS -H "X-TeaKit-Token: $token" "$base_url/health" >"$health" 2>/dev/null \
    && jq -e '.status.worldLoaded == true and .status.singleplayer == true and .status.playerCount > 0' "$health" >/dev/null 2>&1; then
    world_ready=1
    break
  fi
  if ! kill -0 "$gradle_pid" >/dev/null 2>&1; then
    wait "$gradle_pid"
    tail -n 160 "$log" >&2
    exit 1
  fi
  sleep 1
done

if [ "$world_ready" -ne 1 ]; then
  echo "Timed out waiting for TeaKit auto world" >&2
  tail -n 160 "$log" >&2
  exit 1
fi

log_step "Closing menus before scenario execution"
curl -fsS -H "X-TeaKit-Token: $token" \
  -H 'Content-Type: application/json' \
  --data '{}' \
  "$base_url/action/menu/close" >/dev/null || true

log_step "Running TeaKit scenario"
http_code="$(
  curl -sS -o "$result" -w '%{http_code}' \
    --max-time "$((timeout_seconds + 30))" \
    -H "X-TeaKit-Token: $token" \
    -H 'Content-Type: application/json' \
    --data-binary @"$scenario_file" \
    "$base_url/scenario/run"
)"

if [ "$http_code" != "200" ]; then
  echo "Scenario endpoint returned HTTP $http_code" >&2
  cat "$result" >&2 || true
  tail -n 160 "$log" >&2
  exit 1
fi

expected_steps="$(jq '.steps | length' "$scenario_file")"
actual_steps="$(jq '.steps | length' "$result")"
log_detail "scenario steps: $actual_steps/$expected_steps"
if [ "$actual_steps" -lt "$expected_steps" ]; then
  echo "Scenario completed fewer steps than expected" >&2
  cat "$result" >&2 || true
  exit 1
fi

log_step "Verifying Amber and TeaKit log markers"
grep -q 'Initializing TeaKit on' "$log"
grep -q 'Registering Amber commands for' "$log"

log_step "Requesting client shutdown"
curl -fsS -H "X-TeaKit-Token: $token" \
  -H 'Content-Type: application/json' \
  --data '{"delayMs":500}' \
  "$base_url/action/client/quit" >/dev/null

for _ in $(seq 1 15); do
  if ! kill -0 "$gradle_pid" >/dev/null 2>&1; then
    break
  fi
  sleep 1
done

wait "$gradle_pid"
echo "Scenario OK: $node"
