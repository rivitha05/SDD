# Test results

Results from the local test runs and GitHub Actions workflows.

## Results

| Suite | Result | Notes |
|---|---|---|
| Local API contracts | 4 passed | Clean build from a fresh checkout of `0975c9b` |
| Live Reqres API | 2 passed | GET page 2 and chained POST; no API key was needed |
| Chromium | 7 passed | Full web suite |
| Firefox | 7 passed | Full web suite, using the local proxy CA |
| Android | 7 passed | Full normal suite; 9m 8s on software emulation |
| MOB-03 repeat runs | 3 passed | Separate sessions, followed by a pass in the full Android suite |
| Android crash tests | 2 failed as expected | MOB-08 and MOB-09 crash the app, then fail the home-title check |
| CI regression | Passed | [Contracts, Chromium and Firefox](https://github.com/rivitha05/SDD/actions/runs/38074526669) |
| CI Android | Passed | [Normal mobile suite](https://github.com/rivitha05/SDD/actions/runs/38074526668); 6m 10s |
| Manual testing | 3 findings reproduced | Each checked twice in separate browser sessions |

The normal suites passed **27 checks**. Of the 18 assessment scenarios, 16 passed and the two crash scenarios failed as expected. Chromium and Firefox run the same seven web scenarios.

## MOB-03 reset fix

The name and Mercedes selection were submitted correctly, but the native locator could not click the reset link. Android reported the link bounds as `[143,327][175,328]`, even though the link was visible in the screenshot. Scrolling did not fix the bounds.

The test now waits for `WEBVIEW_io.selendroid.testapp` and uses the form's DOM controls. It clicks `here`, checks that the original URL, default name and Volvo selection return, then switches back to `NATIVE_APP`.

ChromeDriver 2.44 matches the app's Chrome 69 WebView. UiAutomator2 4.2.9 failed during context switching with `The response to the /status API is not valid`; version 3.9.9 supports this older driver. The installer checks the download's SHA-256.

MOB-03 passed three consecutive sessions, then passed in the full mobile run. [Screenshots and page source](evidence/mob03/README.md) show the original failure and successful reset.

## Browser setup

Firefox initially failed to start with `writing /proc/self/uid_map: EROFS`. Running outside the restricted sandbox resolved startup, but navigation then failed with `SEC_ERROR_UNKNOWN_ISSUER`. A separate Firefox certificate store resolved the proxy trust issue. Chromium also needed host permissions after an initial `ERR_CERT_AUTHORITY_INVALID` error.

Both local browser suites passed. Both CI browser jobs passed without the local certificate configuration. Setup details are in [environment notes](environment-notes.md).

## Android CI fix

The first [Android CI run](https://github.com/rivitha05/SDD/actions/runs/37674374488) hit the 35-minute job timeout and reported shell exit code 2.

The emulator action runs each line of its script input separately, which broke the multiline readiness loop. It now calls `scripts/run-mobile-ci.sh` as one command. The script waits up to 30 seconds for Appium, checks that the port is free, and stops its server when Maven finishes. Emulator boot has a five-minute timeout.

The next Android run passed and uploaded `android-mobile-results`. The regression run also passed and uploaded reports for all three jobs. Both runs used commit `89f3b13`.

The live API suite passed locally. The separate `api-live.yml` workflow was not run.

## Reports

Each suite's Cucumber and Surefire output was saved separately because the profiles write to the same filenames. Allure also contains earlier troubleshooting runs.

The Allure report from the completed regression run shows **20 passed and 2 failed**. Both browser runs appear under the same seven web scenarios. The failures are MOB-08 and MOB-09; screenshots, page source and AndroidRuntime logs are attached.

Report paths and commands are in the [README](../README.md).
