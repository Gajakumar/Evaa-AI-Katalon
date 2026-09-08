//package utils
//
//import com.kms.katalon.core.annotation.Keyword
//import com.kms.katalon.core.util.KeywordUtil
//import com.kms.katalon.core.webui.driver.DriverFactory
//import org.openqa.selenium.WebDriver
//import org.openqa.selenium.JavascriptExecutor
//
//class JavaScriptErrorCollector {
//
//    @Keyword
//    def startCollector() {
//
//         WebDriver driver = DriverFactory.getWebDriver()
//JavascriptExecutor js = (JavascriptExecutor) driver
//
//        js.executeScript("""
//            window.__evaaJsErrors = [];
//
//            window.addEventListener(
//                'error',
//                function(event) {
//
//                    window.__evaaJsErrors.push(
//                        'JS Error: ' +
//                        event.message +
//                        ' | ' +
//                        event.filename +
//                        ':' +
//                        event.lineno
//                    );
//                }
//            );
//
//            window.addEventListener(
//                'unhandledrejection',
//                function(event) {
//
//                    window.__evaaJsErrors.push(
//                        'Unhandled Promise Rejection: ' +
//                        event.reason
//                    );
//                }
//            );
//        """)
//
//        KeywordUtil.logInfo(
//                "JavaScript error collector started."
//        )
//    }
//
//    @Keyword
//    def verifyNoJavaScriptErrors() {
//
//       WebDriver driver = DriverFactory.getWebDriver()
//JavascriptExecutor js = (JavascriptExecutor) driver
//
//        List errors =
//                js.executeScript(
//                        "return window.__evaaJsErrors || [];"
//                ) as List
//
//        KeywordUtil.logInfo(
//                "JavaScript errors found: ${errors.size()}"
//        )
//
//        errors.each { error ->
//
//            KeywordUtil.logInfo(
//                    "❌ ${error}"
//            )
//        }
//
//        if (!errors.isEmpty()) {
//
//            KeywordUtil.markFailed(
//                    "${errors.size()} JavaScript error(s) detected."
//            )
//
//        } else {
//
//            KeywordUtil.markPassed(
//                    "No JavaScript errors detected."
//            )
//        }
//    }
//}

package utils

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.driver.DriverFactory

import org.openqa.selenium.WebDriver
import org.openqa.selenium.JavascriptExecutor


class JavaScriptErrorCollector {

    /*
     * Known-benign browser noise that isn't worth failing a test over.
     * "Script error." with no detail is what browsers report for errors
     * thrown by cross-origin scripts (analytics/ad tags) due to CORS -
     * there's no actionable info in it either way.
     */
    private static final List<String> IGNORED_ERROR_PATTERNS = [
        "resizeobserver loop limit exceeded",
        "resizeobserver loop completed with undelivered notifications",
        "script error."
    ]


    /*
     * ============================================================
     * START COLLECTOR
     * ============================================================
     *
     * Call this once right after each full page navigation, and
     * again after switching into any popup that opens in a separate
     * browser window/tab (it will NOT see errors that happen in a
     * different window object). You do NOT need to call it again
     * for an in-page popup/modal that's part of the same document -
     * the listener already installed on the page will keep catching
     * errors from inside the modal too.
     *
     * Safe to call multiple times - it will not reset already
     * collected errors or install duplicate listeners.
     */
    @Keyword
    def startCollector() {

        WebDriver driver = DriverFactory.getWebDriver()
        JavascriptExecutor js = (JavascriptExecutor) driver

        js.executeScript("""
            if (!window.__evaaJsErrorsInstalled) {

                window.__evaaJsErrors = [];
                window.__evaaJsErrorsInstalled = true;

                window.addEventListener('error', function(event) {
                    window.__evaaJsErrors.push(
                        'JS Error: ' + event.message +
                        ' | ' + event.filename + ':' + event.lineno
                    );
                });

                window.addEventListener('unhandledrejection', function(event) {
                    window.__evaaJsErrors.push(
                        'Unhandled Promise Rejection: ' + event.reason
                    );
                });
            }
        """)

        KeywordUtil.logInfo("JavaScript error collector started/confirmed active on this page.")
        KeywordUtil.logInfo(
            "NOTE: attached to the CURRENT window/document only. A full page " +
            "navigation destroys it - call startCollector() again after each " +
            "navigation. A popup opened in a separate browser window/tab also " +
            "needs its own startCollector() call after switching to it. An " +
            "in-page modal on the same document does NOT need a second call."
        )
    }


    /*
     * ============================================================
     * VERIFY NO JAVASCRIPT ERRORS
     * ============================================================
     *
     * Call this after navigating/opening a popup (with startCollector
     * already run for that context).
     *
     * clearAfterCheck (default true): clears the error buffer after
     * reporting, so a second call later on the same page (e.g. after
     * opening a different popup) only reports NEW errors instead of
     * repeating ones you already saw. Pass false if you specifically
     * want a running total instead.
     */
    @Keyword
    def verifyNoJavaScriptErrors(boolean clearAfterCheck = true) {

        WebDriver driver = DriverFactory.getWebDriver()
        JavascriptExecutor js = (JavascriptExecutor) driver

        Boolean installed = false

        try {
            installed = (Boolean) js.executeScript(
                "return !!window.__evaaJsErrorsInstalled;"
            )
        } catch (Exception e) {
            installed = false
        }

        if (!installed) {

            KeywordUtil.logInfo(
                "WARNING: the JavaScript error collector is not active on this " +
                "page/window - either startCollector() was never called here, or " +
                "a page navigation reset it since the last call."
            )

            KeywordUtil.markFailed(
                "Cannot verify JavaScript errors - startCollector() has not been " +
                "called (or was reset by navigation) for the current page/window."
            )

            return
        }

        List rawErrors =
            js.executeScript("return window.__evaaJsErrors || [];") as List

        List<String> errors = []

        rawErrors.each { e -> if (e != null) errors.add(e.toString()) }

        List<String> realErrors = []
        List<String> ignoredErrors = []

        errors.each { error ->

            String lower = error.toLowerCase()

            boolean ignore =
                IGNORED_ERROR_PATTERNS.any { pattern -> lower.contains(pattern) }

            if (ignore) {
                ignoredErrors.add(error)
            } else {
                realErrors.add(error)
            }
        }

        KeywordUtil.logInfo(
            "JavaScript errors found: ${errors.size()} " +
            "(${realErrors.size()} actionable, ${ignoredErrors.size()} ignored as known-benign)"
        )

        realErrors.each { error -> KeywordUtil.logInfo("JS ERROR: ${error}") }
        ignoredErrors.each { error -> KeywordUtil.logInfo("Ignored (benign): ${error}") }

        if (clearAfterCheck) {

            js.executeScript("window.__evaaJsErrors = [];")

            KeywordUtil.logInfo(
                "Error buffer cleared - the next check on this page/window will " +
                "only report errors that occur after this point."
            )
        }

        if (!realErrors.isEmpty()) {

            KeywordUtil.markFailed(
                "${realErrors.size()} JavaScript error(s) detected."
            )

        } else {

            KeywordUtil.markPassed(
                "No JavaScript errors detected."
            )
        }
    }
}