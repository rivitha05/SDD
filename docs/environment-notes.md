# Local setup notes

The local runs used Debian Linux x86_64, JDK 21 compiling Java 17, Maven 3.9.11, Chromium 140.0.7339.16 and Firefox 141.0. Android used API 28 with Chrome 69, Appium 2.19.0, UiAutomator2 3.9.9 and ChromeDriver 2.44.

## Local helpers

The test machine has these helpers outside the repository:

```bash
source /workspace/tools/cloud-env.sh
/workspace/tools/install-sdd.sh
/workspace/tools/start-sdd-services.sh
sdd_mvn -B -ntp -Pmobile test -Dmobile.serverUrl=http://127.0.0.1:4726
```

`install-sdd.sh` checks tools and refreshes dependencies. `start-sdd-services.sh` starts the emulator and Appium, then checks readiness. These files are specific to this machine; a fresh clone uses the [README setup commands](../README.md).

Local Appium uses port 4726. The default is 4723, and earlier troubleshooting used 4725. SDK and tool files remain on disk, but emulator and Appium processes need restarting between sessions. ADB needs a writable `.android` directory.

The repository includes two scripts:

- `scripts/install-chromedriver.sh` downloads ChromeDriver 2.44 and checks its SHA-256.
- `scripts/run-mobile-ci.sh` runs on a booted emulator, starts Appium and stops it after Maven exits. It needs `sdkmanager`, `adb` and `appium` on `PATH`. `MOBILE_SERVER_PORT` sets the port.

## Browser certificates and permissions

Firefox could not start in the restricted sandbox (`writing /proc/self/uid_map: EROFS`). Both browsers passed with host execution permissions. Firefox also needed the proxy CA in a separate NSS certificate store to resolve `SEC_ERROR_UNKNOWN_ISSUER`. Chromium used the host's certificate configuration.

The local setup uses Maven proxy settings and Java/browser certificate trust. TLS verification stays enabled. DejaVu/Liberation fonts and a separate fontconfig fixed an earlier text-rendering issue.

Example Firefox certificate setup on Linux:

```bash
mkdir -p /path/to/firefox-trust
certutil -N --empty-password -d sql:/path/to/firefox-trust
certutil -A -d sql:/path/to/firefox-trust -n environment-proxy -t 'C,,' -i /path/to/proxy-ca.crt
./mvnw -B -ntp -Pweb test -Dweb.browser=firefox -Dweb.firefoxTrustStore=/path/to/firefox-trust
```

`certutil` comes from the OS NSS tools package. Use the CA certificate supplied for the proxy. Each scenario copies the certificate databases into a new profile and deletes it when finished. The local environment sets `WEB_FIREFOX_TRUST_STORE=/workspace/tools/firefox-trust`.

The CI browser jobs passed without this custom certificate store.
