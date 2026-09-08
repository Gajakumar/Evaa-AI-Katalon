//package utils
//
//import com.kms.katalon.core.annotation.Keyword
//import com.kms.katalon.core.util.KeywordUtil
//import com.kms.katalon.core.webui.driver.DriverFactory
//import org.openqa.selenium.WebDriver
//
//class NavigationChecker {
//
//    @Keyword
//    def verifyNavigation(String expectedUrlPart) {
//
//        WebDriver driver =
//                DriverFactory.getWebDriver()
//
//        String currentUrl =
//                driver.getCurrentUrl()
//
//        KeywordUtil.logInfo(
//                "Current URL : ${currentUrl}"
//        )
//
//        KeywordUtil.logInfo(
//                "Expected URL: ${expectedUrlPart}"
//        )
//
//        if (currentUrl.contains(expectedUrlPart)) {
//
//            KeywordUtil.markPassed(
//                    "Navigation successful."
//            )
//
//        } else {
//
//            KeywordUtil.markFailed(
//                    "Navigation failed. " +
//                    "Expected URL to contain: " +
//                    expectedUrlPart +
//                    " | Actual: " +
//                    currentUrl
//            )
//        }
//    }
//}

package utils

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.driver.DriverFactory

import org.openqa.selenium.WebDriver


class NavigationChecker {

	/*
	 * ============================================================
	 * VERIFY NAVIGATION
	 * ============================================================
	 *
	 * Call this after triggering a navigation (click on a link,
	 * form submit, redirect, etc). It polls the current URL for
	 * up to timeoutInSeconds, rather than checking exactly once,
	 * so it isn't flaky against SPA routing or slow page loads.
	 *
	 * NOT meant for in-page popups/modals - those don't change the
	 * URL (as you already confirmed), so this keyword has nothing
	 * to check in that case. Use BrokenLinkChecker/ImageChecker/etc
	 * for popup content instead; use this one for actual page-to-
	 * page navigation.
	 */
	@Keyword
	def verifyNavigation(String expectedUrlPart, int timeoutInSeconds = 10) {

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


		if (expectedUrlPart == null || !expectedUrlPart.trim()) {

			KeywordUtil.markFailed(
				"verifyNavigation() called with a blank expectedUrlPart - " +
				"refusing to check, since an empty string would always " +
				"match and silently pass regardless of the actual URL."
			)

			return
		}

		String expected = expectedUrlPart.trim()


		String currentUrl = ""
		boolean matched = false

		long endTime = System.currentTimeMillis() + (timeoutInSeconds * 1000L)

		while (System.currentTimeMillis() < endTime) {

			try {

				currentUrl = driver.getCurrentUrl() ?: ""

			} catch (Exception e) {

				KeywordUtil.markFailed(
					"Unable to read current URL: " + e.getMessage()
				)

				return
			}

			if (currentUrl.contains(expected)) {
				matched = true
				break
			}

			Thread.sleep(300)
		}


		KeywordUtil.logInfo("Current URL : ${currentUrl}")
		KeywordUtil.logInfo("Expected URL contains: ${expected}")


		if (matched) {

			KeywordUtil.markPassed(
				"Navigation successful."
			)

		} else {

			KeywordUtil.markFailed(
				"Navigation failed after waiting ${timeoutInSeconds}s. " +
				"Expected URL to contain: ${expected} | Actual: ${currentUrl}"
			)
		}
	}
}