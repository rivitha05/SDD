#!/usr/bin/env bash
set -euo pipefail
suite="${1:-mobile}"
case "$suite" in
  mobile|crash-demo) ;;
  *) echo "Unsupported mobile suite: $suite" >&2; exit 2 ;;
esac
port="${MOBILE_SERVER_PORT:-4723}"
repo_root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$repo_root"
sdkmanager "build-tools;35.0.0"
adb shell wm size reset
adb shell wm density 160
if curl --fail --silent --connect-timeout 1 --max-time 2 "http://127.0.0.1:$port/status" > /dev/null; then
  echo "An Appium server already uses port $port; select an unused MOBILE_SERVER_PORT." >&2
  exit 1
fi
appium --address 127.0.0.1 --port "$port" --log target-appium.log > target-appium-console.log 2>&1 &
appium_pid=$!
trap 'kill "$appium_pid" 2>/dev/null || true; wait "$appium_pid" 2>/dev/null || true' EXIT
ready=false
ready_deadline=$((SECONDS + 30))
while (( SECONDS < ready_deadline )); do
  if ! kill -0 "$appium_pid" 2>/dev/null; then
    cat target-appium-console.log >&2
    exit 1
  fi
  if curl --fail --silent --connect-timeout 1 --max-time 2 "http://127.0.0.1:$port/status" > /dev/null; then
    ready=true
    break
  fi
  sleep 1
done
if [[ "$ready" != true ]]; then
  echo 'Appium did not become ready within 30 seconds.' >&2
  tail -50 target-appium-console.log >&2
  exit 1
fi
./mvnw -B -ntp -P"$suite" test -Dmobile.serverUrl="http://127.0.0.1:$port"
