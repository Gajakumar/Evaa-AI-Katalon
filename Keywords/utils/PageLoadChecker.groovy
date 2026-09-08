//package utils
//
//import com.kms.katalon.core.annotation.Keyword
//import com.kms.katalon.core.util.KeywordUtil
//import com.kms.katalon.core.webui.driver.DriverFactory
//import org.openqa.selenium.WebDriver
//import org.openqa.selenium.JavascriptExecutor
//
//class PageLoadChecker {
//
//    @Keyword
//    def verifyPageLoaded() {
//
//        WebDriver driver = DriverFactory.getWebDriver()
//JavascriptExecutor js = (JavascriptExecutor) driver
//
//        String title = driver.getTitle()
//        String url = driver.getCurrentUrl()
//
//        String readyState =
//                js.executeScript(
//                        "return document.readyState;"
//                )
//
//        KeywordUtil.logInfo(
//                "Page Title : ${title}"
//        )
//
//        KeywordUtil.logInfo(
//                "URL        : ${url}"
//        )
//
//        KeywordUtil.logInfo(
//                "Ready State: ${readyState}"
//        )
//
//        if (readyState != "complete") {
//
//            KeywordUtil.markFailed(
//                    "Page did not reach complete ready state."
//            )
//
//            return
//        }
//
//        if (!title || title.trim().isEmpty()) {
//
//            KeywordUtil.markFailed(
//                    "Page title is empty."
//            )
//
//            return
//        }
//
//        KeywordUtil.markPassed(
//                "Page loaded successfully."
//        )
//    }
//}

package utils

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.driver.DriverFactory

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.JavascriptExecutor


class PageLoadChecker {

	/*
	 * ============================================================
	 * VERIFY PAGE LOADED
	 * ============================================================
	 *
	 * Polls document.readyState (and the page title) for up to
	 * timeoutInSeconds instead of checking exactly once, so it
	 * isn't flaky against normal navigation/render timing.
	 *
	 * CAVEAT: document.readyState === "complete" only confirms the
	 * initial HTML/sub-resources finished loading. For a JS-heavy
	 * SPA, that can be true while the app is still rendering (blank
	 * page/spinner). If you need a stronger guarantee, pass a CSS
	 * selector via waitForSelector that identifies real rendered
	 * content (e.g. your app's main container) - the keyword will
	 * additionally wait for that selector to exist before passing.
	 * Leave it null/blank to skip that extra check.
	 */
	@Keyword
	def verifyPageLoaded(int timeoutInSeconds = 10, String waitForSelector = null) {

		WebDriver driver = null

		try {
			driver = DriverFactory.getWebDriver()
		} catch (Exception e) {

			KeywordUtil.markFailed(
				"Unable to get WebDriver: " + e.getMessage()
			)

			return
		}

		if (driver == null) {

			KeywordUtil.markFailed(
				"WebDriver is not available."
			)

			return
		}

		JavascriptExecutor js = (JavascriptExecutor) driver


		String readyState = ""
		String title = ""
		String url = ""
		boolean readyStateOk = false

		long endTime = System.currentTimeMillis() + (timeoutInSeconds * 1000L)

		while (System.currentTimeMillis() < endTime) {

			try {

				Object rs = js.executeScript("return document.readyState;")
				readyState = rs != null ? rs.toString() : ""

				title = driver.getTitle() ?: ""
				url = driver.getCurrentUrl() ?: ""

			} catch (Exception e) {

				KeywordUtil.logInfo(
					"Transient error while checking page state: " + e.getMessage()
				)

				Thread.sleep(300)
				continue
			}

			if (readyState == "complete") {
				readyStateOk = true
				break
			}

			Thread.sleep(300)
		}


		KeywordUtil.logInfo("Page Title : ${title}")
		KeywordUtil.logInfo("URL        : ${url}")
		KeywordUtil.logInfo("Ready State: ${readyState}")


		if (!readyStateOk) {

			KeywordUtil.markFailed(
				"Page did not reach 'complete' ready state within ${timeoutInSeconds}s " +
				"(last observed: '${readyState}')."
			)

			return
		}


		if (!title || !title.trim()) {

			KeywordUtil.markFailed(
				"Page title is empty."
			)

			return
		}


		if (waitForSelector != null && waitForSelector.trim()) {

			boolean elementFound = false

			long selectorEndTime = System.currentTimeMillis() + (timeoutInSeconds * 1000L)

			while (System.currentTimeMillis() < selectorEndTime) {

				try {

					int count = driver.findElements(By.cssSelector(waitForSelector.trim())).size()

					if (count > 0) {
						elementFound = true
						break
					}

				} catch (Exception ignored) {
				}

				Thread.sleep(300)
			}

			if (!elementFound) {

				KeywordUtil.markFailed(
					"Page reached 'complete' ready state, but expected content " +
					"selector '${waitForSelector}' did not appear within ${timeoutInSeconds}s. " +
					"The page HTML loaded, but the app may not have finished rendering."
				)

				return
			}

			KeywordUtil.logInfo(
				"Rendered content confirmed via selector: ${waitForSelector}"
			)
		}


		KeywordUtil.markPassed(
			"Page loaded successfully."
		)
	}
}