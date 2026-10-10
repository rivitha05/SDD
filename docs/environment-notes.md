# Validation environment

Recorded runs used Debian Linux x86_64, a full JDK 21 compiling Java 17 bytecode, Maven 3.9.11, Chromium 140.0.7339.16, Firefox 141.0 and an Android API 28 emulator with Chrome 69 WebView. The host has no `/dev/kvm`; local Android runs used software emulation. Appium 2.19.0, UiAutomator2 3.9.9 and ChromeDriver 2.44 were validated together.

## Prepared environment scripts

The validation environment contains these helpers outside the Git checkout:

```bash
source /workspace/tools/cloud-env.sh
/workspace/tools/install-sdd.sh
/workspace/tools/start-sdd-services.sh
sdd_mvn -B -ntp -Pmobile test -Dmobile.serverUrl=http://127.0.0.1:4726
```

`install-sdd.sh` refreshes and validates dependencies. `start-sdd-services.sh` starts the retained emulator and Appium and checks readiness. The prepared environment uses Appium port 4726; portable setup defaults to 4723. Older diagnostic servers used port 4725. These helpers are specific to the prepared environment and are not included in a fresh repository clone.

The 10 October follow-up encountered package-clear timeouts on the retained AVD and an Appium Settings installation stall on a separate fresh AVD. Reboot and host permissions did not resolve them. Hosted Android CI passed on the same framework version; current local mobile setup remains blocked. Detailed results are in the [execution record](execution-record.md).

SDK, AVD and tool caches are retained locally; emulator and Appium processes restart between environment sessions. ADB uses its standard `.android` directory, which must be writable. The repository's `scripts/install-chromedriver.sh` installs and verifies the matching Linux WebView driver. `scripts/run-mobile-ci.sh` runs against an already booted emulator, starts its own Appium server and stops that server after Maven exits. It requires `sdkmanager`, `adb` and `appium` on `PATH`; `MOBILE_SERVER_PORT` selects an unused port.

## Browser trust and sandbox permissions

Validation used the environment's HTTPS proxy, supported Maven proxy settings and Java/browser certificate trust. TLS verification remained enabled. An isolated fontconfig using installed DejaVu/Liberation fonts corrected the initial invisible-text rendering issue.

Firefox cannot start inside the restricted command sandbox (`writing /proc/self/uid_map: EROFS` and graphics initialization failure). Both browser suites passed with host execution permissions. Firefox additionally required a separate NSS trust store for the proxy CA; without it, navigation failed with `SEC_ERROR_UNKNOWN_ISSUER`. Chromium uses the host's existing trust configuration.

An example Linux setup for a certificate-only Firefox trust store:

```bash
mkdir -p /path/to/firefox-trust
certutil -N --empty-password -d sql:/path/to/firefox-trust
certutil -A -d sql:/path/to/firefox-trust -n environment-proxy -t 'C,,' -i /path/to/proxy-ca.crt
./mvnw -B -ntp -Pweb test -Dweb.browser=firefox -Dweb.firefoxTrustStore=/path/to/firefox-trust
```

`certutil` is provided by the OS NSS tools package. Only approved CA certificates belong in this directory. Each scenario copies the trust databases into a new temporary profile and deletes it after browser shutdown. Cookies and browsing state are not shared. The prepared environment supplies `WEB_FIREFOX_TRUST_STORE=/workspace/tools/firefox-trust`.

GitHub-hosted browser jobs passed with normal Playwright contexts and public certificate trust; the optional proxy trust configuration is not required there. Portable prerequisites and commands are in the [README](../README.md).
