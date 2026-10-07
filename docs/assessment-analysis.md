# Assessment analysis

Source review: full text of both DOCX files and all three embedded images; full user request; APK archive inventory; user screenshot of package/activity names.

## Required automation
- Java Maven: Appium mobile and REST Assured API; Playwright web; TestNG; Cucumber; Page Object Model where useful.
- Nine Android scenarios: home screen; EN cancellation; WebView form and reset; registration and defaults/confirmation; progress wait; toast; popup dismissal; button crash; text-input crash.
- Seven web scenarios: droppable; noncontiguous selection 1/3/7; both Controlgroup rental forms (horizontal SUV/automatic/insurance/2; vertical Truck/standard/insurance/1); current date; resize; descending sort; widget green color.
- GET users page 2, status 200, user 10 named Byron. POST user dynamically derived from GET, status 201, generated id, JSON schema.
- Allure or equivalent reports, screenshots, maintainable synchronization, meaningful assertions/logging, Maven commands, GitHub Actions, source repository and interview demonstration.

## Required manual work
- Choose Spartoo (responsive web permitted) or Alarm Clock Xtreme Android.
- Three observed reproducible defects, submitted in Excel with app, device/OS, priority, severity, title, description, steps, expected/actual, screenshot/evidence.
- No purchases, payments, or financial transactions.

## User deliverables and expectations
- GitHub-ready source, README, configuration, reporting, CI; actual execution status.
- Excel and supporting evidence; ZIP for multiple artifacts.
- Requirement coverage, architecture/design rationale, assumptions/limitations, risk-based coverage, critical review, interview questions.
- No invented defects, results, or hidden failures. Important ambiguity requires clarification before dependent implementation.

## Decisions implemented
- Unified Maven test project with separate domain packages and tagged Cucumber suites; no separate web language needed because Playwright Java satisfies the tool requirement.
- Sequential mobile; isolated browser contexts; independent API client; no shared mutable response across scenarios.
- Allure Cucumber adapter; screenshots and browser traces on failure; redact API authentication.
- Two deliberate crash failures in an explicit demonstration suite, retaining genuine failure exit codes.
- Spartoo responsive web manual assessment, with browser/OS/viewport recorded honestly rather than claiming Android hardware.

## Risks and open points
- Controlgroup image is the only instruction for case 3; both forms are covered. Clicking Book Now is only a demo control; assert selection state, do not invent booking backend behavior.
- POST example Bryant/BA versus chaining Byron: derive the name from user 10 and configure the job; document exact mapping.
- Reqres live GET/POST were executed successfully without a key using an explicit client User-Agent; optional authentication remains externally configured.
- Android API 28 SDK, build tools, Appium and UiAutomator2 are installed. The cloud lacks KVM; a software emulator boots and runs native scenarios.
- The APK lacks WebView debugging. Its accessible controls are used without modifying the supplied binary.
- Live sites became accessible after scoped network setup. Three manual findings were reproduced in separate sessions and recorded in Excel with evidence.
- Optional broader browser coverage is defined in CI but must not be claimed as passed without execution. See the execution record for final results and limitations.
