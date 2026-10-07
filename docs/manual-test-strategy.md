# Manual assessment strategy

Chosen application: Spartoo responsive website, https://www.spartoo.com/. This is explicitly permitted by the assessment. A responsive desktop browser session must be labelled as such; it is not an Android device demonstration.

## Charter and constraints
Explore product discovery, search, filtering, details, navigation and responsive interaction. Do not submit purchases, payments or financial transactions. Avoid account creation and sending contact messages. Use only synthetic search text; no personal data.

Prioritize defects that prevent discovery or selection, break navigation, or make mobile controls inaccessible. Reproduce a candidate in a clean session before classifying it as a confirmed defect. Record exact URL, browser version, OS, viewport, timestamps, prerequisites and repeatability. Separate observations caused by blocked external resources, automation, anti-bot controls, cookie consent or uncertain requirements.

## Coverage
- Empty search, whitespace, meaningful query, nonexistent product query, punctuation and long query.
- Filter apply/clear, multiple filters, zero-result state, browser back and preserved query state.
- Product detail access, size/stock affordances, back navigation and product image controls.
- Responsive navigation, cookie consent, orientation/viewport changes, readable controls and horizontal overflow.
- Keyboard focus, accessible names, visible labels and modal dismissal where directly observable.
- Language/region state and currency consistency; do not assume international delivery coverage.

## Defect quality gate
A submitted defect needs a specific observable failure, reproducible steps, justified expected behavior and evidence showing actual behavior. Priority expresses business urgency; severity expresses functional impact. Cosmetic findings should not be inflated to major defects. Security or financial claims require evidence and are outside this task's transaction prohibition.

If fewer than three defects are confirmed, mark the workbook incomplete and retain candidate observations separately. Never populate the required count with invented or unverified reports.
