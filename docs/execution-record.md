# Execution record

Validation date: 7 October 2026 (UTC). Host: Debian Linux x86_64, JDK 21 compiling Java 17 bytecode. Maven 3.9.11 via its checksum-verified wrapper. Public hosts were accessed through the cloud's configured HTTPS proxy, with TLS verification enabled.

| Check | Observed result | Evidence / scope |
|---|---|---|
| Compile and local API contracts | 4 passed, 0 failed, 0 skipped | Wrapper and repeatable installation refresh ran the actual HTTP client/contract tests; a fresh GitHub clone also passed all four |
| Live Reqres API | 2 passed, 0 failed, 0 skipped | GET page 2 and dynamic POST, without an API key on the execution date |
| Live jQuery UI Chromium | 7 passed, 0 failed, 0 skipped | Chromium 140.0.7339.16; all seven required widgets, screenshots and Allure results |
| Normal Android | 6 distinct scenarios passed; 1 unresolved failure (MOB-03) | Full run: 5 passed / 2 failed; toast subsequently passed; WebView submission passed but reset-link bounds remain clipped |
| Deliberate Android crashes | 2 genuine expected failures | Both app exceptions confirmed in AndroidRuntime logs; both reached the failing home-title assertion |
| Spartoo exploration | 3 findings reproduced in two fresh contexts | Workbook, unaltered images and raw browser observations; responsive browser viewport |
| Firefox on cloud host | Not validated | Restricted namespace/graphics startup failure |
| GitHub Actions | Not verified | Workflow definitions provided; no verified remote run in this record |
| Empty tag selection guard | Expected nonzero exit with 0 scenarios | Surefire failIfNoTests=true prevents a false green zero-test profile |

## Unresolved mobile result

MOB-03 verifies the submitted name and Mercedes selection, but its bottom reset link remains clipped to a one-pixel rectangle on the tested emulator despite native interaction and scrolling diagnostics. The reset assertion remains enabled and fails. The legacy APK/Appium interaction is unresolved; it is not classified as a confirmed product defect. The supplied APK is unchanged. No XML-bounds workaround, forced navigation or assertion bypass is retained.

## Portable suite commands

```bash
./mvnw -B -ntp test
./mvnw -B -ntp -Papi test
./mvnw -B -ntp -Pweb test
./mvnw -B -ntp -Pmobile test
./mvnw -B -ntp -Pcrash-demo test
./mvnw -B -ntp allure:report
```

Profiles reuse Cucumber/Surefire filenames. Run them sequentially and archive each suite's output. The final evidence package includes raw Allure results and earlier diagnostic attempts. Deliberate crashes are intentional failures, rather than infrastructure errors or skipped scenarios.

## Diagnosed setup issues and corrections

The default Java installation was a JRE. A checksum-verified full JDK was installed; JVM proxy trust uses the existing system trust store. Maven uses supported proxy settings. Appium's broad Selenium ranges initially resolved incompatible classes; the Selenium BOM now pins a compatible dependency set.

External applications became reachable after the required domains were allowed in the environment network configuration. Reqres returned an access error for a default diagnostic client identity; an explicit framework User-Agent allowed the required live requests with TLS verification enabled and no API key required on the execution date.

Default font configuration yielded invisible browser text. An isolated fontconfig with installed DejaVu/Liberation fonts restored rendering; all seven live browser scenarios then passed. Browser execution used existing trust configuration with no certificate-error bypass.

There is no /dev/kvm. Android API 28 runs without hardware acceleration. Build tools 35.0.0 and a freshly started Appium server were required for installation. Android's initial compatibility notice is dismissed only when its exact known text appears; app crashes and ANRs are not dismissed in hooks.

Display overrides introduced a coordinate mismatch in the legacy app. Setup now uses physical resolution at 160 dpi. A SystemUI ANR occurred during display reconfiguration; that diagnostic run was interrupted, retained as infrastructure evidence, and the emulator was recovered before fresh tests. An abandoned Appium session later timed out and stopped its instrumentation; subsequent test runs use sequential completed sessions. The WebView dropdown and native popup require multi-window accessibility. Toast initializes its XPath engine before triggering the transient message, temporarily removes idle waits and matches exact text in a single server-side lookup; its rerun passed. WebView reset is still unresolved; the page uses native scrolling and a usable-bounds wait with the original assertions enabled. Diagnostic bounds-only workarounds were removed.

## Environment limitations

Installation refresh and service startup were exercised in the prepared instance. Environment-specific helper paths, proxy/trust configuration and Appium port 4725 are documented in [environment notes](environment-notes.md). Fresh checkouts require the portable prerequisites in the [README](../README.md). Firefox, broader devices and GitHub Actions execution remain unverified.

## Final counts and crash handling

Latest distinct Allure results: **19 passed, 2 failed deliberately, 1 broken/unresolved**, covering 22 distinct tests/scenarios (18 assessment scenarios and 4 additional local contract tests). Across mandatory assessment scenarios: 15 passed, 2 deliberate failures, 1 unresolved mobile failure. Counts represent latest distinct outcomes across the full run and targeted reruns, not a single all-passing mobile invocation. All assessment scenarios remained enabled.

For text-trigger crashes, sendKeys can raise StaleElementReferenceException after the app exits in its synchronous text listener. The trigger catches only that exception and rethrows it if home is still active; the following home-title assertion remains enabled and fails. This allows the assignment's explicit fail-case assertion to execute, without converting the scenario to a pass. Both crash results include native source, screenshots and AndroidRuntime exception evidence.

## Fresh-checkout verification

A fresh clone from https://github.com/rivitha05/SDD (branch `main`) into `/tmp/SDD-fresh-check` compiled all test sources and passed the four default contracts using the pinned wrapper and documented full-JDK prerequisites. The cloud proxy settings and installed toolchain were reused, so this proves checkout/build reproducibility with those prerequisites, not a blank operating-system installation or GitHub Actions success.

The generated Allure report was inspected: 22 distinct results, 19 passed, 2 intentional failures, 1 unresolved broken mobile result, zero skipped/unknown. Raw diagnostic history is retained. A standalone HTML report and source/manual/evidence ZIPs are packaged separately.
