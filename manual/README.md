# Manual testing

Application: [Spartoo](https://www.spartoo.com/), tested in Chromium 140.0.7339.16 on Debian Linux x86_64. The responsive viewport was 390×844; search was also checked at 1440×1000.

The Excel workbook contains three findings, each reproduced in two separate browser sessions:

1. **Unmatched search:** `zzzzsddnonexistent987` returned the general catalogue of 243,275 articles without a no-results message or fallback explanation. `Adidas` opened the Adidas catalogue. The unmatched query stayed in the search field, but the results page was labelled `Recherche`. Product counts may change.
2. **Consent overlay focus:** Tab moved out of the visible cookie overlay into the header. The fifth Tab focused the customer-service phone link behind it.
3. **Skip link:** `Aller au contenu principal` targeted two hidden elements with the same `skip-link-anchor` ID. Activating it did not bypass the header; the next Tab returned to a header link.

The search finding is a usability issue: returning the full catalogue may be intended, but the page does not explain the fallback. The keyboard checks use the skip link's purpose and WAI-ARIA modal guidance as the expected behavior.

Consent rejection worked after its request completed and is not included as a defect. Blocked analytics and an initial font-rendering problem were excluded too.

Screenshots and logs are in `evidence/`. `observations.json` contains both repeat checks, and `BUG-01-control.json` contains the desktop search comparison. The workbook has embedded screenshots and relative links to this folder, so keep it alongside the workbook.

Purchases, payments, account creation and native mobile apps were not tested. Keyboard checks do not include a full screen-reader assessment.
