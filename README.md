# SDD SDET assessment

Java 17+ / Maven framework for the supplied Selendroid Android, jQuery UI web and Reqres API assignments. Appium, Playwright Java, REST Assured, TestNG, Cucumber and Allure are all used. Execution evidence and limitations are recorded separately; implementing a scenario is not evidence that it passed. **Current mobile limitation: MOB-03 submits and verifies values, but its reset-link interaction still fails in the cloud emulator. Resolve it before claiming a fully passing mobile demonstration.**

## Quick start

Use a full **JDK 17 or newer**, not only a JRE. Maven 3.9.11 is pinned by the checksum-verified wrapper.

```bash
./mvnw -B -ntp clean test
./mvnw -B -ntp test-compile exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install --with-deps chromium firefox"
./mvnw -B -ntp -Pweb test
./mvnw -B -ntp -Pweb test -Dweb.browser=firefox
./mvnw -B -ntp -Papi test
./mvnw -B -ntp allure:report
```

Windows: use `mvnw.cmd`. Linux `--with-deps` may require administrator access to install OS libraries; browsers without OS installation can be installed using `install chromium firefox`. The default `test` command runs four deterministic local API contract checks. **It does not run the assessment's live external scenarios.** Profiles select those explicitly.

Reports: `target/cucumber.html`, `target/cucumber.json`, `target/surefire-reports/`, `target/allure-results/` and `target/site/allure-maven-plugin/`. `./mvnw allure:serve` opens the interactive report on a local development machine. Screenshots are captured after web/mobile scenarios; failed web scenarios also have Playwright traces in `target/evidence/`. View a trace with Playwright CLI `show-trace <file.zip>`.

Run profiles sequentially in a single checkout. Archive reports after each suite because Cucumber and TestNG runner filenames are reused. Allure creates unique result IDs but may include earlier runs until `clean` is used. A clean run gives a fresh report, and deletes previously generated local evidence.

## Android

Prerequisites: Node.js 22+, Android SDK command-line tools, platform tools, build tools 35.0.0, an Android API 28 emulator or compatible physical device, and Appium 2.19.0 with UiAutomator2 4.2.9. SDK tools and Java must be on PATH; set `ANDROID_HOME` to the SDK installation.

```bash
sdkmanager "platform-tools" "build-tools;35.0.0" "platforms;android-28" "system-images;android-28;google_apis;x86_64"
avdmanager create avd -n SDD_API28 -k "system-images;android-28;google_apis;x86_64" --device pixel
emulator -avd SDD_API28 -no-snapshot
adb devices
adb shell getprop sys.boot_completed
adb shell wm size reset
adb shell wm density 160
npm install --global appium@2.19.0
appium driver install uiautomator2@4.2.9
appium --address 127.0.0.1
```

Use the emulator's physical resolution and a 160 dpi density to avoid display overrides in this legacy APK. WebView taps use rendered accessibility bounds and native gestures; the popup enables multi-window accessibility, and toast polling temporarily disables Android's idle wait.

Once the device is online and boot property is `1`, verify the Appium `/status` response, then:

```bash
./mvnw -B -ntp -Pmobile test
./mvnw -B -ntp -Pcrash-demo test
```

The crash demo deliberately asserts the assignment's home-screen expectation after each explicit app crash. **It should fail and return a nonzero exit code.** Keep its results separate from the seven normal mobile cases; a setup failure is not proof of the expected app crash. There is no catch-all exception-to-pass conversion.

The supplied APK (`0.12.0-SNAPSHOT`, package `io.selendroid.testapp`) is kept unchanged. Appium receives an ignored runtime copy because it can re-sign APKs during installation. The original APK does not enable WebView debugging, so its exposed accessibility controls are used for the Hello form. Native waits, title/activity checks, defaults and submitted values are asserted. Synthetic registration data is in `src/test/resources/data/mobile-user.json`.

## Configuration and secrets

Defaults are in `src/test/resources/config/default.properties`. Precedence: JVM `-D` property, environment variable, defaults. Dotted/camelCase keys map to uppercase snake case: `web.baseUrl` → `WEB_BASE_URL`, `mobile.serverUrl` → `MOBILE_SERVER_URL`. Examples:

```bash
./mvnw -Pweb test -Dweb.browser=firefox -Dweb.headless=false
./mvnw -Pmobile test -Dmobile.udid=emulator-5554
./mvnw -Pmobile test -Dmobile.serverUrl=http://127.0.0.1:4723
```

`web.timezone` defaults to `Asia/Dubai`; the browser and current-date assertion use the same timezone. Playwright handles browser waits; Appium uses explicit native waits, zero implicit timeout and short toast polling.

Reqres's legacy demo endpoints were accessible without a key during investigation. If your environment requires authentication, supply `REQRES_API_KEY` securely through the environment or GitHub Actions secrets. Never commit it. The API client sends the key only as an `x-api-key` header and does not log requests/headers. The assessment's POST example is illustrative: its name is derived from user 10's GET `first_name` (Byron), with configured job `BA`. Each POST scenario gets its own fresh prerequisite response.

The framework honors an existing `HTTPS_PROXY` for public HTTPS requests and browser traffic. Cloud proxy CAs must be trusted by Java and the selected browser using supported trust configuration. Do not use relaxed REST Assured HTTPS validation or browser certificate-error bypasses. Proxy hosts and secrets must not be hard-coded into the repository.

## Structure and design

- `core`: configuration, per-scenario World, lifecycle/evidence hooks.
- `web`: jQuery UI page object with explicit demo iframe boundaries and web steps.
- `mobile`: home, registration and accessibility WebView page objects; immutable synthetic data.
- `api`: instance-scoped HTTP specification, chaining logic and contract/live checks.
- `runner`: TestNG Cucumber runner. Feature tags map directly to assessment IDs.
- `src/test/resources`: features, defaults, synthetic data and response schema.
- `.github/workflows`: contract/web regression; separately triggered live API and Android demonstrations.
- `docs`: assessment analysis, manual strategy, execution record, coverage and interview preparation.

PicoContainer scopes state per scenario. No static drivers, mutable global RestAssured configuration, shared response ordering, blanket retries or fixed sleeps are used. Suites are sequential: a single Android device cannot run concurrent sessions, and Playwright objects must remain on their owning thread. Independent CI jobs provide browser-level isolation.

Assertions verify actual outcomes: accepted drop, exact selected subset, both rental groups, current date value, meaningful rendered size change, complete descending order, all three green RGB values, all mobile confirmation fields, response values, generated ID and JSON schema. The Controlgroup image specifies both configurations; Book Now is a UI demo without a booking backend, so no fictitious booking confirmation is asserted.

## CI and submission

`automation.yml` runs local contracts and Chromium/Firefox web jobs on pushes and pull requests. `api-live.yml` runs on demand, with an optional Reqres secret. `mobile.yml` runs on demand using an API 28 emulator; its input selects normal mobile or the deliberately failing crash demo. CI captures reports and evidence even after failures. CI definitions are not evidence that GitHub Actions executed successfully; check actual run results before submission.

Review [assessment analysis](docs/assessment-analysis.md), [manual strategy](docs/manual-test-strategy.md) and [interview guide](docs/interview-guide.md). The manual assignment requires **three real reproduced defects with screenshots**, not generic test cases or guessed bugs. No purchases or financial transactions are permitted.

## Deliverable review

Open [the manual workbook](manual/SDD_Manual_Testing_Assessment.xlsx) with its [evidence folder](manual/README.md). Read [execution results](docs/execution-record.md), [requirement coverage](docs/coverage.md), [critical review and design defence](docs/review.md), and [likely interview questions](docs/interview-guide.md) before submission. The repository contains Java automation; Python was used only for document/APK analysis and producing supporting manual artifacts, not as the test framework language.

In the prepared cloud snapshot, source `/workspace/tools/cloud-env.sh`; run `/workspace/tools/install-sdd.sh` to refresh and validate dependencies. `/workspace/tools/start-sdd-services.sh` starts retained Android tooling and checks readiness; use `-Dmobile.serverUrl=http://127.0.0.1:4725` there. These cloud helpers are environment-specific and do not replace the portable commands above. Saved environment drafts require review/save and publication before they become a reusable published snapshot.
