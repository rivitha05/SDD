# MOB-03 screenshots and page source

The reset link was visible after submission, but Android reported its bounds as `[143,327][175,328]`. The native test timed out waiting for a usable link height.

- [Screenshot before the fix](native-reset-before.png)
- [Native page source](native-reset-before.xml)
- [Screenshot after reset](reset-after.png)

The test now waits for the WebView context and uses DOM controls through ChromeDriver 2.44. After clicking `here`, it checks the original URL, default name and Volvo selection, then switches back to the native context.

Three separate sessions passed, followed by the full Android suite. The reset screenshot comes from that full-suite run. See [test results](../../execution-record.md).
