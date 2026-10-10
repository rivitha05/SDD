# Execution record

Local regression validation: 7 October 2026; CI follow-up: 10 October 2026. Raw execution timestamps use UTC. Local host: Debian Linux x86_64, full JDK 21 compiling Java 17 bytecode, Maven 3.9.11 wrapper. Browser versions: Chromium 140.0.7339.16 and Firefox 141.0. Mobile: Android API 28, Chrome 69 WebView, Appium 2.19.0, UiAutomator2 3.9.9 and ChromeDriver 2.44. The supplied APK checksum passed after validation.

## Results

| Check | Result | Execution evidence |
|---|---|---|
| Local API contracts | PASS — 4/4, no skipped tests | Fresh local checkout of fix commit `0975c9b`; clean compile and default tests |
| Live Reqres API | PASS — 2/2, no skipped tests | GET page 2 and dynamic POST; no API key required on the execution date |
| Chromium web | PASS — 7/7, no skipped scenarios | Local full suite; screenshots and Cucumber/Surefire/Allure results |
| Firefox web | PASS — 7/7, no skipped scenarios | Local full suite with isolated proxy CA trust profile; all widget assertions retained |
| Normal Android | PASS — 7/7 in one full run | Includes WebView reset, toast and popup; full suite completed in 9m 8s on software emulation |
| MOB-03 stability | PASS — 3/3 fresh sessions, plus full-suite pass | Runs completed at 19:20:50, 19:22:34 and 19:24:05 UTC; full regression passed afterward |
| Deliberate Android crashes | 2 expected failures | Both reached the home-title assertion after app exit; screenshots, native source and AndroidRuntime logs |
| GitHub Actions regression | PASS — contracts, Chromium and Firefox jobs | [Run 38074526669](https://github.com/rivitha05/SDD/actions/runs/38074526669), commit `89f3b13`, 10 October |
| GitHub Actions Android | PASS — normal Android workflow, 6m 10s | [Run 38074526668](https://github.com/rivitha05/SDD/actions/runs/38074526668), commit `89f3b13`, 10 October |
| Spartoo manual exploration | 3 findings reproduced in two fresh contexts | Existing workbook, screenshots and browser observations; responsive browser testing |

The normal regression runs comprise **27 passing checks**: 4 contracts, 2 live API, 7 Chromium, 7 Firefox and 7 Android. The 18 required assessment scenarios comprise 16 passes and the 2 deliberate crash failures. Repeated MOB-03 runs are additional stability checks, not extra coverage cases.

## MOB-03 investigation and fix

The original native implementation submitted the name/Mercedes correctly, then timed out waiting for the `here` link's rectangle to exceed one pixel in height. The [baseline screenshot and native source](evidence/mob03/README.md) show the disagreement: the link is rendered visibly, but accessibility reports `[143,327][175,328]`. The accessibility WebView retains an old height after the result page expands. Native scrolling did not correct those coordinates.

Inspection confirmed a `WEBVIEW_io.selendroid.testapp` context and Chrome 69 debugging socket. The earlier assumption that this APK could not expose a WebView context was incorrect. Waiting for the page/context matters: querying immediately after navigation can return only `NATIVE_APP`.

The initial hybrid attempt exposed a second tooling issue. UiAutomator2 4.2.9 bundles an Appium ChromeDriver adapter that requires a modern `/status` response with `ready`; ChromeDriver 2.44 uses the legacy protocol. Context switching failed with `The response to the /status API is not valid`. UiAutomator2 3.9.9 bundles the compatible adapter and was validated with ChromeDriver 2.44. The repository installer pins the official Linux binary and verifies its SHA-256.

The corrected page object waits for the WebView context, uses `name_input`, `car`, the submit control and the actual `here` link, then verifies the reset URL, original name default and Volvo selection. It restores `NATIVE_APP` in `finally` and checks the question/title/activity after reset. No forced navigation, app relaunch, coordinate adjustment or assertion bypass replaces the reset.

The failure was caused by the automation's use of stale native accessibility bounds and incompatible hybrid tooling. The actual reset link worked through DOM interaction. No application reset defect was reproduced. Three consecutive fresh-session passes and the subsequent full mobile suite confirmed the fix under the tested configuration.

## Browser investigation

Inside the restricted command sandbox, Firefox startup failed with `writing /proc/self/uid_map: EROFS` and graphics initialization errors. With host execution permissions it started, but the proxy certificate was not trusted: navigation failed with `SEC_ERROR_UNKNOWN_ISSUER`. OS-root and certificate-policy options did not resolve that failure.

A separate NSS profile containing the environment's approved proxy CA successfully loaded jQuery UI with TLS verification enabled. The optional `web.firefoxTrustStore` configuration copies only the trust databases into a fresh profile per scenario and removes that profile during cleanup. All seven Firefox cases passed. Chromium initially encountered `ERR_CERT_AUTHORITY_INVALID` inside the restricted sandbox; with host execution permissions and existing browser trust, all seven cases passed. No browser selector or interaction assertions were changed.

GitHub-hosted Chromium and Firefox jobs passed using the normal context configuration, without the local proxy trust store. [Environment notes](environment-notes.md) describe the local configuration.

## CI configuration and verification

`automation.yml` installs a full JDK, uses the Maven wrapper, installs each Playwright browser with OS dependencies and runs contracts plus the Chromium/Firefox matrix. Reports are retained even after failure. The supplied [manual run 37671290666](https://github.com/rivitha05/SDD/actions/runs/37671290666) passed, followed by the push-triggered regression run for fix commit `0975c9b`.

`mobile.yml` pins Appium/UiAutomator2, installs the matching ChromeDriver, enables KVM, starts API 28, sets the validated display configuration, checks Appium readiness and runs the selected Maven profile. Mobile-related pushes run the normal suite; the deliberate crash suite is available through manual selection. Allure output and Appium logs are retained after failures.

`api-live.yml` remains an on-demand live API workflow with optional `REQRES_API_KEY`. Its YAML and artifact/report handling were reviewed; the live API suite passed locally. No separate remote execution of this workflow is recorded here.

The first Android run, [37674374488](https://github.com/rivitha05/SDD/actions/runs/37674374488), was cancelled after the 35-minute job limit. Its annotations reported `The job has exceeded the maximum execution time of 35m0s`, `The operation was canceled`, and a shell exit code of 2.

Review of the emulator action confirmed that it executes each newline in its `script` input in a separate shell. The previous multiline readiness loop was therefore invalid, and background Appium output could keep a runner command open. The workflow now calls one checked-in Bash script. `scripts/run-mobile-ci.sh` owns the Appium process, checks readiness within 30 seconds, rejects a port already in use, retains the Maven exit code and stops its server on exit. Emulator boot also has a five-minute limit. The follow-up Android job succeeded in 6m 7s (6m 10s for the workflow) and uploaded `android-mobile-results` (5.14 MB). The regression workflow also passed all three jobs and uploaded its three report artifacts.

An additional local CI-script run on 10 October failed during session setup: `adb shell pm clear io.selendroid.testapp` timed out after 120000 ms. No scenario assertions completed. The run was stopped (exit 143), its logs retained separately, and its Appium port was confirmed closed. Reboot did not restore package-command responsiveness. A separate fresh API 28 AVD completed its initial boot in 7m 20s, but Appium Settings installation also stalled before assertions. A direct package-clear check with host permissions timed out after 15 seconds. Both diagnostic runs were stopped and their logs retained; no fresh-device pass is claimed. This is a current local emulator setup blocker. The exact cause within the emulator remains undetermined; the same pinned framework passed on the KVM-backed hosted runner. This diagnostic does not replace the earlier seven-scenario local pass or the successful hosted Android run.

Run conclusions were checked on public GitHub run/job pages. Push events triggered the follow-up workflows for commit `89f3b13`; authenticated per-step logs were unavailable through the environment API.

## Reports and evidence

Portable commands are in the [README](../README.md). Profiles share Cucumber/Surefire filenames, so each suite's output was archived separately. Raw Allure history retains earlier diagnostics. The 7 October report contains 22 logical cases: **20 passed, 2 deliberate failures, 0 broken, 0 skipped and 0 unknown**. Chromium and Firefox execution counts are recorded separately above.

Crash cases retain the assignment's failing home-title assertion. The text trigger handles only `StaleElementReferenceException` after the app has left home; it rethrows that exception if home is still active. Neither crash is converted to a pass.

## Remaining scope limits

Local Android execution has no hardware acceleration. The 10 October local follow-up encountered the package-operation timeouts described above, while hosted Android CI passed. Browser commands require host permissions on this restricted environment. Broader device/WebView versions, browsers beyond Chromium/Firefox, transactional manual flows and full assistive-technology certification were outside these runs. Public demo sites and API behavior can change.
