# SDD — SDET assessment

This Maven project contains Android tests for Selendroid, web tests for jQuery UI and API tests for Reqres. The manual assessment covers the Spartoo responsive website.

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

PicoContainer gives each scenario its own `World`. Hooks manage sessions and capture screenshots. Each API scenario has its own client, and the POST test fetches its source data with a GET request.

Run one suite at a time in a checkout. Mobile tests use one device, and Playwright runs on one thread per job. CI runs each browser in a separate job. Web tests use Playwright waits; Android uses explicit waits and short polling for toast messages.

## Test coverage

| Suite | Coverage |
|---|---|
| Mobile | Nine scenarios: home screen, EN cancellation, WebView form/reset, registration, progress, toast, popup and two deliberate crashes |
| Web | Seven scenarios: droppable, selection, both Controlgroup forms, current date, resize, descending sort and green widget backgrounds |
| Live API | GET page 2/user 10 and a dynamically chained POST with status, values, ID and schema checks |
| Local contracts | Four checks: successful chaining/schema, missing source user, blank job and malformed response |

The [coverage matrix](docs/coverage.md) lists the assertions for each requirement. Local contract tests use a local HTTP server; live API tests call Reqres.

## Setup and installation

Use a full JDK 17 or newer. The Maven wrapper downloads Maven 3.9.11 and checks its checksum. On Windows, use `mvnw.cmd`.

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

Before running tests, check that the boot property is `1` and Appium responds at `/status`. The tested display configuration uses the emulator's physical resolution at 160 dpi.

ChromeDriver 2.44 matches the Chrome 69 WebView in the API 28 image. The installer verifies the Linux x86_64 download checksum and writes `.tools/chromedriver/chromedriver`. Other operating systems or WebView versions require a matching driver configured through `mobile.chromedriverExecutable`. Existing UiAutomator2 4.x installations should be replaced with the pinned 3.9.9 driver; its ChromeDriver adapter supports the legacy protocol.

The supplied APK is version `0.12.0-SNAPSHOT`, package `io.selendroid.testapp`, with launcher `io.selendroid.testapp.HomeScreenActivity`. Appium installs a copy because it may re-sign the file during installation.

### Configuration

Defaults are in `src/test/resources/config/default.properties`. Priority is JVM `-D` property, environment variable, then defaults. Keys map to uppercase snake case: `web.baseUrl` → `WEB_BASE_URL`, `mobile.serverUrl` → `MOBILE_SERVER_URL`.

```bash
./mvnw -Pweb test -Dweb.browser=firefox -Dweb.headless=false
./mvnw -Pmobile test -Dmobile.udid=emulator-5554
./mvnw -Pmobile test -Dmobile.serverUrl=http://127.0.0.1:4723
```

`web.timezone` defaults to `Asia/Dubai`; the browser and current-date assertion use the same timezone. Mobile registration data is in `src/test/resources/data/mobile-user.json`.

`REQRES_API_KEY` is optional and is sent as `x-api-key`. The recorded live tests passed without a key. The POST uses user 10's `first_name` (Byron) from the GET response and job `BA`.

If Firefox needs a proxy CA, set `web.firefoxTrustStore` to a directory containing `cert9.db`, `key4.db` and `pkcs11.txt`. Each scenario copies these certificate databases into a new temporary profile. [Local setup notes](docs/environment-notes.md) include the commands.

Public HTTPS requests and browser traffic support `HTTPS_PROXY`. Proxy certificates must be trusted by Java and the browser; TLS verification remains enabled. Credentials are supplied through environment variables or CI secrets and are excluded from request logging.

## Running tests and available commands

| Command | Purpose |
|---|---|
| `./scripts/install-chromedriver.sh` | Install the pinned Linux x86_64 WebView driver |
| `./scripts/run-mobile-ci.sh mobile` | Run Android tests on a booted emulator; start and stop Appium automatically |
| `./mvnw -B -ntp clean test` | Compile and run local API contracts |
| `./mvnw -B -ntp -Pweb test` | Run web scenarios in Chromium |
| `./mvnw -B -ntp -Pweb test -Dweb.browser=firefox` | Run web scenarios in Firefox |
| `./mvnw -B -ntp -Papi test` | Run live Reqres scenarios |
| `./mvnw -B -ntp -Pmobile test` | Run seven normal Android scenarios |
| `./mvnw -B -ntp -Pcrash-demo test` | Run the two deliberate crash scenarios |
| `./mvnw -B -ntp allure:report` | Build the Allure report |
| `./mvnw allure:serve` | Open Allure locally |

The two crash tests check the home title after the app exits. Both fail as required by the assessment, so `crash-demo` returns a nonzero exit code.

Machine-specific helpers are listed in [local setup notes](docs/environment-notes.md).

## Reporting and execution results

Reports are written to:

- `target/cucumber.html` and `target/cucumber.json`
- `target/surefire-reports/`
- `target/allure-results/`
- `target/site/allure-maven-plugin/`

Screenshots are captured after web and mobile scenarios. Failed web scenarios also capture Playwright traces in `target/evidence/`; the Playwright CLI command `show-trace <file.zip>` opens a trace.

Cucumber and Surefire reports are overwritten by the next suite, so save a copy when running several profiles. `clean` removes `target/`, including reports and screenshots. Allure keeps earlier results until then.

**27 checks passed**: 4 local contracts, 2 live API, 7 Chromium, 7 Firefox and 7 Android. MOB-03 also passed three repeat runs. The two crash tests failed as expected. See [test results](docs/execution-record.md) for the run details.

## CI

- `automation.yml`: local contracts and a Chromium/Firefox matrix on pushes and pull requests.
- `api-live.yml`: manually triggered live API checks with optional `REQRES_API_KEY` secret.
- `mobile.yml`: API 28 emulator job on mobile-related pushes to `main`, plus manual `mobile` or `crash-demo` selection.

Reports, screenshots and logs are uploaded even when a job fails. The [regression workflow](https://github.com/rivitha05/SDD/actions/runs/38074526669) passed contracts, Chromium and Firefox, and the [Android workflow](https://github.com/rivitha05/SDD/actions/runs/38074526668) passed the normal mobile suite.

## Manual testing deliverables

The [Excel workbook](manual/SDD_Manual_Testing_Assessment.xlsx) contains three Spartoo findings, their bug reports and the test coverage/status. Screenshots and logs are in `manual/evidence/`.

Testing used Chromium at 390×844 on Debian Linux, with a desktop search check at 1440×1000. The findings cover unmatched search, keyboard focus leaving the consent overlay and a skip link that does not bypass the header. Each was reproduced twice in separate browser sessions. Details are in the [manual notes](manual/README.md).

## Project documentation

- [Requirement coverage](docs/coverage.md)
- [Test results](docs/execution-record.md)
- [Framework notes](docs/framework-notes.md)
- [Manual testing scope](docs/manual-test-strategy.md)
- [Manual workbook and evidence](manual/README.md)
- [Local setup notes](docs/environment-notes.md)

## Known limitations

Android was tested on API 28 with Chrome 69 WebView. Other device or WebView versions may need a different ChromeDriver.

The latest local emulator retry stalled during package setup. Android CI passed with KVM. The error and troubleshooting steps are in [test results](docs/execution-record.md).

The demo sites can change or rate-limit requests. Book Now has no booking backend, so the test checks form selections. Manual testing covers the responsive website; purchases, payments, native apps and screen-reader testing were not included.
