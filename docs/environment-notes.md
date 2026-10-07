# Validation environment

The recorded runs used Debian Linux x86_64, a full JDK 21 compiling Java 17 bytecode, Maven 3.9.11, Chromium 140.0.7339.16 and an Android API 28 emulator. The host has no `/dev/kvm`; Android ran without hardware acceleration.

## Prepared environment scripts

The validation environment contains these helpers outside the Git checkout:

```bash
source /workspace/tools/cloud-env.sh
/workspace/tools/install-sdd.sh
/workspace/tools/start-sdd-services.sh
./mvnw -B -ntp -Pmobile test -Dmobile.serverUrl=http://127.0.0.1:4725
```

`install-sdd.sh` refreshes and validates dependencies. `start-sdd-services.sh` restarts the retained emulator and Appium and checks readiness. This environment uses Appium port 4725; portable setup defaults to 4723. These scripts are specific to the prepared environment and are not included in a fresh repository clone.

SDK, AVD and tool caches are retained locally; emulator and Appium processes must restart between environment sessions. ADB uses its standard `.android` directory, which must be writable.

## Network and browser configuration

Validation used the environment's HTTPS proxy, supported Maven proxy settings and existing Java/browser trust configuration. TLS verification remained enabled. An isolated fontconfig using installed DejaVu/Liberation fonts corrected an initial invisible-text rendering issue.

Firefox startup failed under the host's namespace/graphics restrictions. Broader browser and device results remain unverified. Portable prerequisites and commands are in the [README](../README.md).
