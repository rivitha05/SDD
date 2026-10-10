# Test coverage

Cucumber tags match the assessment IDs below. All tests use Java. Run results are in [test results](execution-record.md).

| Assessment ID | Required behavior | Assertions / evidence |
|---|---|---|
| MOB-01 | Launch, title, home elements | Exact configured title, home activity, eleven enabled controls, EN text |
| MOB-02 | EN → No, no → home | Dialog dismissal plus complete home checks |
| MOB-03 | Hello WebView, Mercedes, submit, here → Volvo | Title/activity, question, exact name/car result, reset question, default name and Volvo; DOM link interaction with native context restored |
| MOB-04 | Registration/defaults/details → home | Mr. Burns/Ruby defaults, field controls, all six submitted confirmation values, home |
| MOB-05 | Progress → registration | Loader appears and disappears, registration defaults/controls |
| MOB-06 | Toast | Exact message via native XPath polling |
| MOB-07 | Popup dismissal | Separate accessibility window, dismissal, popup gone and home |
| MOB-08 | Exception button, verify home title (fail case) | Crash leaves home; failing home-title check, screenshot/source/crash log |
| MOB-09 | Type test, verify home title (fail case) | App exits, then the home-title check fails |
| WEB-01 | Droppable | Accepted target text and highlight class |
| WEB-02 | Selectable 1,3,7 | Exact selected subset in DOM order |
| WEB-03 | Pictured Controlgroup selections | Both horizontal/vertical car, transmission, insurance and count values; Book Now state |
| WEB-04 | Datepicker current date | Current Asia/Dubai date, exact formatted field value |
| WEB-05 | Resize | Pointer drag; width and height increase |
| WEB-06 | Sort ASC → DESC | Full original and complete reversed list; each pointer move result |
| WEB-07 | Go Green | All three widget backgrounds equal rgb(64,250,8) |
| API-01 | GET page 2, user 10 Byron | HTTP 200, page 2 and exact first_name |
| API-02 | Dynamic chained POST | Fresh GET source, HTTP 201, echoed name/job, nonblank ID, schema, timestamp |


Four local API contract tests cover successful chaining, a missing source user, a blank job and an invalid response. They run against a local HTTP server; the two live API scenarios call Reqres.

The manual workbook contains three reproduced Spartoo findings, all ten requested report fields and supporting screenshots. It also records planned checks and their execution status. Manual testing used the responsive website.

CI runs the web suite in Chromium and Firefox. Mobile and live API suites have separate workflows; the crash suite is selected manually.
