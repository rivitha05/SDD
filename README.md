# SDD — SDET assessment

Java automation for the supplied Selendroid Android application, jQuery UI demos and Reqres API, with a separate manual testing assessment of the Spartoo responsive website.

## Technology stack

| Area | Tools |
|---|---|
| Language and build | Java 17+, Maven 3.9.11 wrapper |
| Mobile | Appium Java Client 9.5.0, Appium 2.19.0, UiAutomator2 3.9.9, ChromeDriver 2.44 |
| Web | Playwright Java 1.55.0 |
| API | REST Assured 5.5.6, JSON schema validation |
| Test execution | TestNG 7.11.0, Cucumber 7.27.2, PicoContainer |
| Reporting | Allure, Cucumber HTML/JSON, Surefire |
| CI | GitHub Actions |

## Project structure

```text
src/test/java/com/sdd/assessment/
  core/       Configuration, scenario state and lifecycle/evidence hooks
  mobile/     Android page objects and steps
  web/        jQuery UI page object and steps
  api/        HTTP client, API steps and local contract tests
  runner/     TestNG Cucumber runner
src/test/resources/
  features/   Tagged mobile, web and API scenarios
  config/     Default configuration
  data/       Synthetic registration data
  schemas/    API response schema
apps/         Supplied APK and checksum
manual/       Excel workbook and supporting evidence
docs/         Coverage, execution results and framework notes
.github/workflows/  Regression and on-demand workflows
```

## Automation approach

Page objects contain reusable page-level interactions and selectors. Cucumber features describe the scenarios, and tags map them to assessment IDs. Test data is maintained separately from test logic.

PicoContainer creates scenario-scoped state. Hooks manage browser/device sessions and capture evidence. API scenarios use independent clients; the POST case performs its own GET prerequisite rather than depending on another test's execution order.

Suites run sequentially within a checkout. Mobile uses one device, and Playwright objects stay on their owning thread. CI browser jobs run independently. Browser interactions use Playwright waits; Android uses explicit waits with zero implicit timeout and short polling for transient toast messages.

## Test coverage

| Suite | Coverage |
|---|---|
| Mobile | Nine scenarios: home screen, EN cancellation, WebView form/reset, registration, progress, toast, popup and two deliberate crashes |
| Web | Seven scenarios: droppable, selection, both Controlgroup forms, current date, resize, descending sort and green widget backgrounds |
| Live API | GET page 2/user 10 and a dynamically chained POST with status, values, ID and schema checks |
| Local contracts | Four checks: successful chaining/schema, missing source user, blank job and malformed response |

The [coverage matrix](docs/coverage.md) lists the assertions for each requirement. Local contract tests use a local HTTP server; live API tests call Reqres.

## Setup and installation

A full JDK 17 or newer is required. The checksum-verified Maven wrapper pins Maven 3.9.11. On Windows, use `mvnw.cmd`.

```bash
git clone https://github.com/rivitha05/SDD.git
cd SDD
./mvnw -B -ntp clean test
./mvnw -B -ntp test-compile exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install --with-deps chromium firefox"
```

The default test command runs the four local API contracts. Live scenarios are selected through Maven profiles. On Linux, `--with-deps` may require administrator access for OS libraries. If those libraries are already installed, use `install chromium firefox` instead.

### Android setup

Mobile tests require Node.js 22+, Android SDK command-line tools, platform tools, build tools 35.0.0 and an API 28 emulator or compatible physical device. Set `ANDROID_HOME` and add Java and SDK tools to `PATH`.

```bash
sdkmanager "platform-tools" "build-tools;35.0.0" "platforms;android-28" "system-images;android-28;google_apis;x86_64"
avdmanager create avd -n SDD_API28 -k "system-images;android-28;google_apis;x86_64" --device pixel
emulator -avd SDD_API28 -no-snapshot
```

After the emulator boots, in another terminal:

```bash
adb devices
adb shell getprop sys.boot_completed
adb shell wm size reset
adb shell wm density 160
npm install --global appium@2.19.0
appium driver install uiautomator2@3.9.9
./scripts/install-chromedriver.sh
appium --address 127.0.0.1
```

The boot property should be `1` and Appium's `/status` endpoint should respond before starting tests. Validation used the emulator's physical resolution at 160 dpi.

ChromeDriver 2.44 matches the Chrome 69 WebView in the API 28 image. The installer verifies the Linux x86_64 download checksum and writes `.tools/chromedriver/chromedriver`. Other operating systems or WebView versions require a matching driver configured through `mobile.chromedriverExecutable`. Existing UiAutomator2 4.x installations should be replaced with the pinned 3.9.9 driver; its ChromeDriver adapter supports the legacy protocol.

The supplied APK is version `0.12.0-SNAPSHOT`, package `io.selendroid.testapp`, with launcher `io.selendroid.testapp.HomeScreenActivity`. Appium installs a runtime copy because installation may re-sign the APK. The original binary is retained unchanged.

### Configuration

Defaults are in `src/test/resources/config/default.properties`. Priority is JVM `-D` property, environment variable, then defaults. Keys map to uppercase snake case: `web.baseUrl` → `WEB_BASE_URL`, `mobile.serverUrl` → `MOBILE_SERVER_URL`.

```bash
./mvnw -Pweb test -Dweb.browser=firefox -Dweb.headless=false
./mvnw -Pmobile test -Dmobile.udid=emulator-5554
./mvnw -Pmobile test -Dmobile.serverUrl=http://127.0.0.1:4723
```

`web.timezone` defaults to `Asia/Dubai`; the browser and current-date assertion use the same timezone. Mobile registration data is in `src/test/resources/data/mobile-user.json`.

Reqres accepts an optional `REQRES_API_KEY`, sent as `x-api-key`. Live validation did not require a key on the execution date. The POST name comes from user 10's GET `first_name` (Byron); the configured job is `BA`.

Firefox can use an optional `web.firefoxTrustStore` directory containing an NSS `cert9.db` and supporting `key4.db`/`pkcs11.txt` files. Only trust databases are copied into a new temporary profile for each scenario; browsing state is not shared. This is useful when a proxy CA is required. [Environment notes](docs/environment-notes.md) include the setup.

Public HTTPS requests and browser traffic support `HTTPS_PROXY`. Proxy certificates must be trusted by Java and the browser; TLS verification remains enabled. Credentials are supplied through environment variables or CI secrets and are excluded from request logging.

## Running tests and available commands

| Command | Purpose |
|---|---|
| `./scripts/install-chromedriver.sh` | Install the pinned Linux x86_64 WebView driver |
| `./scripts/run-mobile-ci.sh mobile` | Run Android tests on an already booted emulator with an owned Appium server |
| `./mvnw -B -ntp clean test` | Compile and run local API contracts |
| `./mvnw -B -ntp -Pweb test` | Run web scenarios in Chromium |
| `./mvnw -B -ntp -Pweb test -Dweb.browser=firefox` | Run web scenarios in Firefox |
| `./mvnw -B -ntp -Papi test` | Run live Reqres scenarios |
| `./mvnw -B -ntp -Pmobile test` | Run seven normal Android scenarios |
| `./mvnw -B -ntp -Pcrash-demo test` | Run the two deliberate crash scenarios |
| `./mvnw -B -ntp allure:report` | Build the Allure report |
| `./mvnw allure:serve` | Open Allure locally |

The crash suite checks the home-screen expectation after each explicit app crash. Both scenarios fail that assertion and return a nonzero exit code. Their results are separate from the normal mobile suite.

Environment-specific installation and service scripts are documented in [environment notes](docs/environment-notes.md).

## Reporting and execution results

Reports are written to:

- `target/cucumber.html` and `target/cucumber.json`
- `target/surefire-reports/`
- `target/allure-results/`
- `target/site/allure-maven-plugin/`

Screenshots are captured after web and mobile scenarios. Failed web scenarios also capture Playwright traces in `target/evidence/`; the Playwright CLI command `show-trace <file.zip>` opens a trace.

Profiles reuse Cucumber and Surefire filenames, so archive output after each suite. Allure retains earlier results until `clean`; cleaning also deletes local evidence under `target/`.

Validation on 7 October 2026 passed four local contracts, two live API scenarios, seven Chromium scenarios, seven Firefox scenarios and all seven normal Android scenarios: **27 regression checks passed**. MOB-03 also passed three consecutive fresh-session runs before the full Android run. The separate crash suite produced its two expected failures. Full details and CI links are in the [execution record](docs/execution-record.md).

## CI

- `automation.yml`: local contracts and a Chromium/Firefox matrix on pushes and pull requests.
- `api-live.yml`: manually triggered live API checks with optional `REQRES_API_KEY` secret.
- `mobile.yml`: API 28 emulator job on mobile-related pushes to `main`, plus manual `mobile` or `crash-demo` selection.

Workflows retain reports and evidence after failures. [Automation regression run 38074526669](https://github.com/rivitha05/SDD/actions/runs/38074526669) passed its contract, Chromium and Firefox jobs on 10 October 2026. [Android run 38074526668](https://github.com/rivitha05/SDD/actions/runs/38074526668) also passed on the same date. Current validation details are in the [execution record](docs/execution-record.md).

## Manual testing deliverables

The [Excel workbook](manual/SDD_Manual_Testing_Assessment.xlsx) contains three reproduced Spartoo findings, the required defect fields, risk-based coverage and execution status. Screenshots and browser observations are in `manual/evidence/`.

Testing used Chromium at a 390×844 responsive viewport on Debian Linux, with a separate desktop search control. Findings cover unmatched-search feedback, focus escaping the consent overlay and a skip link that fails to bypass the header. Each was reproduced in two fresh browser contexts. Purchases, payments and account creation were excluded. Details and evidence links are in the [manual testing notes](manual/README.md).

## Project documentation

- [Requirement coverage](docs/coverage.md)
- [Test execution results](docs/execution-record.md)
- [Framework notes and design decisions](docs/framework-notes.md)
- [Manual testing scope](docs/manual-test-strategy.md)
- [Manual workbook and evidence](manual/README.md)
- [Environment notes](docs/environment-notes.md)

## Known limitations

Android validation uses API 28 with a Chrome 69 WebView. MOB-03 uses Appium’s WebView context because native accessibility bounds become stale after the page changes height. Other device/WebView versions require compatible tooling.

The 10 October local software-emulator follow-up stalled during Android package operations before test assertions. The normal Android workflow passed on GitHub’s KVM-backed runner. The [execution record](docs/execution-record.md) preserves both outcomes.

Public demo sites can change or rate-limit requests. Book Now is a jQuery UI demo control without a booking backend; coverage checks selection state. Manual findings describe observed browser behavior and do not constitute a full screen-reader or legal compliance audit.
