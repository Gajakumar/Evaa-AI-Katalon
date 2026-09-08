//package utils
//
//import com.kms.katalon.core.annotation.Keyword
//import com.kms.katalon.core.util.KeywordUtil
//import com.kms.katalon.core.webui.driver.DriverFactory
//import org.openqa.selenium.WebDriver
//import org.openqa.selenium.JavascriptExecutor
//
//class NetworkErrorCollector {
//
//    @Keyword
//    def startCollector() {
//
//        WebDriver driver = DriverFactory.getWebDriver()
//JavascriptExecutor js = (JavascriptExecutor) driver
//
//        js.executeScript("""
//            window.__evaaNetworkErrors = [];
//
//            var originalFetch = window.fetch;
//
//            window.fetch = function() {
//
//                return originalFetch.apply(this, arguments)
//                    .then(function(response) {
//
//                        if (!response.ok) {
//
//                            window.__evaaNetworkErrors.push(
//                                response.status +
//                                ' | ' +
//                                response.url
//                            );
//                        }
//
//                        return response;
//                    })
//                    .catch(function(error) {
//
//                        window.__evaaNetworkErrors.push(
//                            'FETCH ERROR | ' +
//                            error
//                        );
//
//                        throw error;
//                    });
//            };
//
//            var originalOpen =
//                XMLHttpRequest.prototype.open;
//
//            var originalSend =
//                XMLHttpRequest.prototype.send;
//
//            XMLHttpRequest.prototype.open =
//                function(method, url) {
//
//                    this.__evaaUrl = url;
//
//                    return originalOpen.apply(
//                        this,
//                        arguments
//                    );
//                };
//
//            XMLHttpRequest.prototype.send =
//                function() {
//
//                    this.addEventListener(
//                        'load',
//                        function() {
//
//                            if (this.status >= 400) {
//
//                                window.__evaaNetworkErrors.push(
//                                    this.status +
//                                    ' | ' +
//                                    this.__evaaUrl
//                                );
//                            }
//                        }
//                    );
//
//                    this.addEventListener(
//                        'error',
//                        function() {
//
//                            window.__evaaNetworkErrors.push(
//                                'XHR ERROR | ' +
//                                this.__evaaUrl
//                            );
//                        }
//                    );
//
//                    return originalSend.apply(
//                        this,
//                        arguments
//                    );
//                };
//        """)
//
//        KeywordUtil.logInfo(
//                "Network error collector started."
//        )
//    }
//
//    @Keyword
//    def verifyNoNetworkErrors() {
//
//        WebDriver driver = DriverFactory.getWebDriver()
//JavascriptExecutor js = (JavascriptExecutor) driver
//
//        List errors =
//                js.executeScript(
//                        "return window.__evaaNetworkErrors || [];"
//                ) as List
//
//        KeywordUtil.logInfo(
//                "Network errors found: ${errors.size()}"
//        )
//
//        errors.each { error ->
//
//            KeywordUtil.logInfo(
//                    "❌ Network Error: ${error}"
//            )
//        }
//
//        if (!errors.isEmpty()) {
//
//            KeywordUtil.markFailed(
//                    "${errors.size()} network error(s) detected."
//            )
//
//        } else {
//
//            KeywordUtil.markPassed(
//                    "No network errors detected."
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


class NetworkErrorCollector {

	/*
	 * ============================================================
	 * START COLLECTOR
	 * ============================================================
	 *
	 * Call this once right after each full page navigation, and
	 * again after switching into a popup that opens in a separate
	 * browser window/tab (fetch/XHR are patched per-window, so a
	 * different window object needs its own patch). An in-page
	 * popup/modal on the same document does NOT need a second call.
	 *
	 * Safe to call multiple times - it will not re-wrap fetch/XHR
	 * or reset already collected errors.
	 */
	@Keyword
	def startCollector() {

		WebDriver driver = DriverFactory.getWebDriver()
		JavascriptExecutor js = (JavascriptExecutor) driver

		js.executeScript("""
            if (!window.__evaaNetworkErrorsInstalled) {

                window.__evaaNetworkErrors = [];
                window.__evaaNetworkErrorsInstalled = true;

                var originalFetch = window.fetch;

                window.fetch = function() {
                    return originalFetch.apply(this, arguments)
                        .then(function(response) {
                            if (!response.ok) {
                                window.__evaaNetworkErrors.push(
                                    response.status + ' | ' + response.url
                                );
                            }
                            return response;
                        })
                        .catch(function(error) {
                            var name = (error && error.name) ? error.name : '';
                            if (name === 'AbortError') {
                                // deliberate cancellation (debounce, unmount,
                                // navigation) - not a real network failure
                                throw error;
                            }
                            window.__evaaNetworkErrors.push(
                                'FETCH ERROR | ' + error
                            );
                            throw error;
                        });
                };

                var originalOpen = XMLHttpRequest.prototype.open;
                var originalSend = XMLHttpRequest.prototype.send;

                XMLHttpRequest.prototype.open = function(method, url) {
                    this.__evaaUrl = url;
                    return originalOpen.apply(this, arguments);
                };

                XMLHttpRequest.prototype.send = function() {

                    this.addEventListener('load', function() {
                        if (this.status >= 400) {
                            window.__evaaNetworkErrors.push(
                                this.status + ' | ' + this.__evaaUrl
                            );
                        }
                    });

                    this.addEventListener('error', function() {
                        window.__evaaNetworkErrors.push(
                            'XHR ERROR | ' + this.__evaaUrl
                        );
                    });

                    this.addEventListener('timeout', function() {
                        window.__evaaNetworkErrors.push(
                            'XHR TIMEOUT | ' + this.__evaaUrl
                        );
                    });

                    return originalSend.apply(this, arguments);
                };
            }
        """)

		KeywordUtil.logInfo("Network error collector started/confirmed active on this page.")
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
	 * VERIFY NO NETWORK ERRORS
	 * ============================================================
	 *
	 * clearAfterCheck (default true): clears the error buffer after
	 * reporting, so a later check on the same page (e.g. after
	 * opening a different popup) only reports NEW errors. Pass
	 * false for a running total instead.
	 *
	 * ignoreUrlContains: optional list of substrings - any error
	 * whose URL contains one of these is logged separately and does
	 * NOT fail the test. Use this for known-noisy third-party calls
	 * you don't control (analytics beacons, ad/tracking pixels that
	 * legitimately 404 or get blocked in your test environment).
	 */
	@Keyword
	def verifyNoNetworkErrors(boolean clearAfterCheck = true, List<String> ignoreUrlContains = []) {

		WebDriver driver = DriverFactory.getWebDriver()
		JavascriptExecutor js = (JavascriptExecutor) driver

		Boolean installed = false

		try {
			installed = (Boolean) js.executeScript(
				"return !!window.__evaaNetworkErrorsInstalled;"
			)
		} catch (Exception e) {
			installed = false
		}

		if (!installed) {

			KeywordUtil.logInfo(
				"WARNING: the network error collector is not active on this " +
				"page/window - either startCollector() was never called here, " +
				"or a page navigation reset it since the last call."
			)

			KeywordUtil.markFailed(
				"Cannot verify network errors - startCollector() has not been " +
				"called (or was reset by navigation) for the current page/window."
			)

			return
		}

		List rawErrors =
			js.executeScript("return window.__evaaNetworkErrors || [];") as List

		List<String> errors = []

		rawErrors.each { e -> if (e != null) errors.add(e.toString()) }

		List<String> lowerIgnorePatterns =
			(ignoreUrlContains ?: []).collect { it.toLowerCase() }

		List<String> realErrors = []
		List<String> ignoredErrors = []

		errors.each { error ->

			String lower = error.toLowerCase()

			boolean ignore =
				lowerIgnorePatterns.any { pattern -> lower.contains(pattern) }

			if (ignore) {
				ignoredErrors.add(error)
			} else {
				realErrors.add(error)
			}
		}

		KeywordUtil.logInfo(
			"Network errors found: ${errors.size()} " +
			"(${realErrors.size()} actionable, ${ignoredErrors.size()} ignored by filter)"
		)

		realErrors.each { error -> KeywordUtil.logInfo("Network Error: ${error}") }
		ignoredErrors.each { error -> KeywordUtil.logInfo("Ignored: ${error}") }

		if (clearAfterCheck) {

			js.executeScript("window.__evaaNetworkErrors = [];")

			KeywordUtil.logInfo(
				"Error buffer cleared - the next check on this page/window will " +
				"only report errors that occur after this point."
			)
		}

		if (!realErrors.isEmpty()) {

			KeywordUtil.markFailed(
				"${realErrors.size()} network error(s) detected."
			)

		} else {

			KeywordUtil.markPassed(
				"No network errors detected."
			)
		}
	}
}