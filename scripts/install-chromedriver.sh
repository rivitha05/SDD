#!/usr/bin/env bash
set -euo pipefail
# ChromeDriver 2.44 supports the Chrome 69 WebView in the API 28 validation image.
if [[ "$(uname -s)" != Linux || "$(uname -m)" != x86_64 ]]; then
  echo 'This installer supports Linux x86_64. Configure mobile.chromedriverExecutable with a matching driver for your OS.' >&2
  exit 1
fi
repo_root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
temporary_dir="$(mktemp -d)"
trap 'rm -rf "$temporary_dir"' EXIT
curl --fail --location --retry 2 --silent --show-error \
  https://storage.googleapis.com/chromedriver/2.44/chromedriver_linux64.zip \
  --output "$temporary_dir/chromedriver.zip"
printf '%s  %s\n' '687d2e15c42908e2911344c08a949461b3f20a83017a7a682ef4d002e05b5d46' "$temporary_dir/chromedriver.zip" | sha256sum --check
mkdir -p "$repo_root/.tools/chromedriver"
unzip -o -q "$temporary_dir/chromedriver.zip" -d "$repo_root/.tools/chromedriver"
chmod +x "$repo_root/.tools/chromedriver/chromedriver"
"$repo_root/.tools/chromedriver/chromedriver" --version
