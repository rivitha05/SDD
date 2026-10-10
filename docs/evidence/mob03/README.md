# MOB-03 reset evidence

The baseline native test timed out while waiting for the reset link's height to exceed one pixel. The screenshot shows the link visible; the native source reports `here` at `[143,327][175,328]`. The enclosing accessibility WebView retains a shorter height than the rendered view after submission.

- [Baseline screenshot](native-reset-before.png)
- [Baseline native page source](native-reset-before.xml)
- [Successful reset screenshot](reset-after.png)

The revised test waits for the APK's WebView context and uses its DOM controls through ChromeDriver 2.44. It clicks the real `here` link, verifies the original form URL, name default and Volvo selection, then restores the native context. Three consecutive fresh-session runs passed. The screenshot above is from the subsequent full mobile regression run, where MOB-03 also passed.

The supplied APK and reset assertions are retained. No coordinate adjustment, forced navigation or app restart replaces the reset interaction. Full results are in the [execution record](../../execution-record.md).
