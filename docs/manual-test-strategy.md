# Manual testing scope

Spartoo's responsive website was tested in Chromium with a mobile-sized viewport. The assessment allows this website. Browser, OS and viewport are recorded in the workbook and [manual notes](../manual/README.md).

## Test plan

The plan covers:

- Search: empty, whitespace, valid, unmatched, long and punctuation-only queries.
- Filters: apply, clear, combinations, no results and browser-back behavior.
- Product details: size/stock controls, images and return navigation.
- Responsive layout: navigation, consent, viewport changes and overflow.
- Keyboard access: focus order, labels, control names and modal behavior.
- Regional settings: language, region and currency.

The workbook marks which checks were run. Three findings were reproduced twice in separate browser sessions: unmatched-search feedback, focus leaving the consent overlay, and the skip link failing to bypass the header.

## Bug reports

Each report includes prerequisites, steps, expected and actual results, severity, priority and screenshots. Severity describes the impact; priority describes the urgency.

Search was checked with a valid query as well as an unmatched one. The search finding is recorded as a usability issue because fallback behavior is not documented. The keyboard findings describe the observed focus and navigation problems.

Consent rejection worked once its request completed, so it was not reported as a bug. Blocked analytics and an initial font-rendering issue were also excluded.

Purchases, payments, account creation and contact-message submission were not tested. Testing used a responsive browser, not the native Android/iOS app. Screen-reader testing was not included.
