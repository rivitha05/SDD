# Manual assessment evidence

Application: Spartoo, https://www.spartoo.com/, responsive website viewed in a desktop Chromium 140.0.7339.16 browser at 390×844 CSS pixels. Host: Debian Linux x86_64. A separate 1440×1000 desktop session was used for a search positive control. This is browser-assisted exploratory testing, not a native Android/iOS device claim.

Three observations were reproduced in two independent fresh browser contexts on 7 October 2026. Raw timestamps use UTC; the workbook displays Asia/Dubai dates. Evidence contains unaltered screenshots and browser-observed facts. No purchases, payments, account creation or financial transactions were performed.

1. An unmatched search (`zzzzsddnonexistent987`) returns a catalogue of 243,275 articles with no no-results or explicit fallback explanation. A valid `Adidas` control navigates to the Adidas catalogue; the unmatched query retains its field value but returns the general `Recherche` catalogue. See BUG-01 query/result screenshots, text observation and control JSON. Result counts can change over time.
2. While the cookie-consent overlay remains visible, Tab leaves its controls and reaches background header navigation. The fifth Tab focuses the customer-service phone link behind the overlay. See BUG-02 screenshot and the recorded focus sequence. Rejecting consent does work after its successful asynchronous request; that timing observation was investigated and rejected as a defect.
3. The "Aller au contenu principal" skip link targets two hidden, duplicate `skip-link-anchor` elements. Activating it does not scroll or bypass the header; the next Tab returns to a header link. See BUG-03 screenshot and recorded target/focus state.

Expected behavior for keyboard access is grounded in the skip link's stated purpose and WAI-ARIA modal interaction guidance (with WCAG keyboard/bypass-block expectations), rather than an invented Spartoo functional specification. Search behavior is a usability finding: if fallback to all products is intentional, the UI should explain it instead of presenting it as matching results. These are observed functional/accessibility findings; legal compliance and assistive-technology certification are not claimed.

`evidence/observations.json` records both clean-session runs. `BUG-01-control.json` records the positive/negative desktop comparison. Blocked analytics requests and an early misconfigured font rendering environment were excluded from defect reports. Required content/image resource hosts were allowed before reproduction.

The workbook includes the required fields, risk-based coverage, execution distinctions and evidence links/embedded previews. Keep the workbook and `evidence/` directory together when sharing the ZIP so relative links work.
