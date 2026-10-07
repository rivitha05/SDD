# Manual testing scope

Application: Spartoo responsive website, https://www.spartoo.com/, as permitted by the assessment. Execution used desktop Chromium with a mobile-sized viewport. Browser, OS, viewport and timestamps are recorded in the workbook and [evidence notes](../manual/README.md).

## Exploratory scope

The risk-based plan covers product discovery, search, filtering, product details, navigation and responsive interaction:

- Search: empty/whitespace queries, meaningful and unmatched queries, punctuation and long input.
- Filters: apply/clear, combinations, zero-result states and browser-back behavior.
- Product details: access, size/stock controls, images and return navigation.
- Responsive interaction: navigation, consent, viewport changes, readable controls and overflow.
- Keyboard access: focus order, labels, accessible names and modal behavior.
- Regional state: language, region and currency consistency.

These are planned coverage areas. The workbook identifies observed checks and unexecuted cases separately. Recorded findings cover unmatched-search feedback, consent-overlay focus and skip-link behavior; each was reproduced in two fresh browser contexts.

## Evidence and classification

Reports include prerequisites, steps, expected/actual behavior, priority, severity and screenshots. Priority reflects business urgency; severity reflects functional impact. Search fallback behavior is qualified as a usability finding because product intent is not documented. Keyboard findings are based on observed focus and navigation behavior.

A valid search was used as a positive control. Blocked analytics and initial font-rendering issues were excluded from defect reports. Consent rejection was investigated and worked after its asynchronous request completed.

Purchases, payments, financial transactions, account creation and contact-message submission were excluded. Search input was synthetic. The findings do not establish full screen-reader or legal compliance coverage.
