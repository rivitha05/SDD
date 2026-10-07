# Interview discussion guide

## Architecture to explain
- TestNG executes Cucumber scenarios; tags select web, API, mobile and deliberate crash demonstrations.
- PicoContainer scopes the World and step objects per scenario. Drivers, responses and test data are not shared globally.
- Pages own selectors, frame boundaries, interactions and domain checks. Cucumber remains readable while assertions stay close to observable behavior.
- Explicit Appium waits and Playwright auto-waits replace fixed delays; transient toast checks use short polling.
- REST Assured specs are immutable per client, with no global base URI and no credential logging.
- POST gets fresh source data within its own scenario. This is API chaining, not TestNG order dependency.
- A Selenium BOM pins Appium's permissive transitive version ranges to a compatible version.
- Allure and Cucumber reports, screenshots and failure traces make diagnoses reviewable.
- The supplied legacy APK is unchanged; accessibility-based WebView automation is needed when remote debugging is disabled.

## Likely questions
1. Why Playwright Java rather than TypeScript? A single Maven execution model meets the requested stack and simplifies shared CI/reporting without losing browser automation capability.
2. Why Cucumber and TestNG together? They are mandatory here; Cucumber expresses behavior and TestNG provides the runner. In a project without that constraint, evaluate whether BDD collaboration justifies its cost.
3. How do you prevent flaky drag-and-drop tests? Use real pointer events, target the demo iframe, wait for the resulting DOM state, and verify complete contents/order instead of only action completion.
4. How would you enable parallel runs? Isolate browser processes/context per worker and thread; allocate one Android device per worker; keep data scenario-local. The current suite runs sequentially to respect Playwright thread affinity and a single mobile device.
5. How do you handle the deliberate crash tests? Separate tagged demonstration suite, genuine failure status, evidence and nonzero exit. Do not relaunch the app before asserting or convert every failure into an expected result.
6. Why not share the GET response between API scenarios? That creates order and parallelism coupling. Each scenario retrieves its own prerequisite data.
7. Why a schema plus explicit assertions? Schema validates structure/types; explicit checks prove business values and chaining. A structurally valid wrong name still fails.
8. Why not retry every failing test? Blanket retries hide defects and flakiness. Diagnose first; add narrowly justified retries only for known transient infrastructure behavior.
9. What is the difference between local contract tests and live API validation? Local tests exercise the HTTP client and assertions deterministically; only a successful call to the real endpoint verifies that external service integration.
10. How are secrets handled? Environment/CI secrets only, excluded from files and logs; proxy destination scope matters in cloud tasks.
11. How do severity and priority differ? Severity measures impact; priority measures repair urgency. A visible cosmetic defect can have low severity but high release priority.
12. How did you choose three manual defects? Explain actual exploratory charters, clean-session reproduction, evidence and rejected candidates. Never claim defects that were not observed.
13. What limitations remain? Use the execution record; distinguish failed, skipped, unrun, intentional failures and access blockers.
14. What would you improve with more time? Device/browser coverage, accessibility depth and build-specific baselines based on risk, after required live behavior is established.

## Submission rehearsal
Clone into a fresh directory, verify JDK 17+ and the wrapper, execute contracts, install browsers, run the web suite, generate Allure, then demonstrate Android prerequisites and suites. Explain any external API access failure honestly. Show the manual workbook evidence links and reproduce the defects before submitting.
