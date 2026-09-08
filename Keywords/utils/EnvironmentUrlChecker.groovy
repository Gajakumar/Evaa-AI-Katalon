//package utils
//
//import com.kms.katalon.core.annotation.Keyword
//import com.kms.katalon.core.util.KeywordUtil
//import com.kms.katalon.core.webui.driver.DriverFactory
//import org.openqa.selenium.By
//import org.openqa.selenium.WebDriver
//import org.openqa.selenium.WebElement
//
//class EnvironmentUrlChecker {
//
//    @Keyword
//    def verifyNoDevOrStagingUrls() {
//
//        WebDriver driver =
//                DriverFactory.getWebDriver()
//
//        List<WebElement> links =
//                driver.findElements(By.cssSelector("a[href]"))
//
//        List<String> invalidUrls = []
//
//        links.each { link ->
//
//            String href =
//                    link.getAttribute("href")
//
//            if (!href) {
//                return
//            }
//
//            String lower =
//                    href.toLowerCase()
//
//            if (lower.contains("localhost") ||
//                lower.contains("127.0.0.1") ||
//                lower.contains("staging") ||
//                lower.contains("stage.") ||
//                lower.contains("dev.") ||
//                lower.contains("development") ||
//                lower.contains("qa.") ||
//                lower.contains("test.") ||
//                lower.contains("uat.")) {
//
//                invalidUrls.add(href)
//            }
//        }
//
//        KeywordUtil.logInfo(
//                "Potential environment URLs found: " +
//                invalidUrls.size()
//        )
//
//        invalidUrls.unique().each { url ->
//
//            KeywordUtil.logInfo(
//                    "❌ Environment URL: ${url}"
//            )
//        }
//
//        if (!invalidUrls.isEmpty()) {
//
//            KeywordUtil.markFailed(
//                    "Dev/Staging/Test URL(s) detected."
//            )
//
//        } else {
//
//            KeywordUtil.markPassed(
//                    "No dev/staging/test URLs detected."
//            )
//        }
//    }
//}

package utils

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.driver.DriverFactory

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

import java.net.URI
import java.net.URISyntaxException


class EnvironmentUrlChecker {

	/*
	 * ============================================================
	 * MAIN KEYWORD
	 * ============================================================
	 *
	 * Call this from Katalon, right after navigating OR right after
	 * opening any in-page popup/modal:
	 *
	 * CustomKeywords.'utils.EnvironmentUrlChecker.verifyNoDevOrStagingUrls'()
	 *
	 * Only scans links currently VISIBLE on screen - same behaviour
	 * as BrokenLinkChecker - so it naturally covers whatever popup
	 * or page is showing at the moment you call it, and won't flag
	 * stale links sitting in a hidden menu behind a modal.
	 */
	@Keyword
	def verifyNoDevOrStagingUrls() {

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


		List<WebElement> allLinks = []

		try {

			allLinks = driver.findElements(By.cssSelector("a[href]"))

			if (allLinks == null) {
				allLinks = []
			}

		} catch (Exception e) {

			KeywordUtil.logInfo(
				"Unable to collect links: " + e.getMessage()
			)

			allLinks = []
		}


		KeywordUtil.logInfo(
			"Total <a href> elements found: ${allLinks.size()}"
		)


		Set<String> invalidUrls = new LinkedHashSet<String>()
		Set<String> checkedHrefs = new LinkedHashSet<String>()

		int visibleCount = 0
		int skippedCount = 0


		allLinks.each { WebElement link ->

			if (link == null) {
				return
			}

			boolean visible = false

			try {
				visible = link.isDisplayed()
			} catch (Exception ignored) {
				// stale element or not currently attached - skip it
				return
			}

			if (!visible) {
				return
			}

			visibleCount++

			String href = null

			try {
				href = link.getAttribute("href")
			} catch (Exception ignored) {
				return
			}

			if (!href || !href.trim()) {
				return
			}

			href = href.trim()

			if (checkedHrefs.contains(href)) {
				return
			}

			checkedHrefs.add(href)

			String lower = href.toLowerCase()

			if (lower.startsWith("javascript:") ||
				lower.startsWith("mailto:") ||
				lower.startsWith("tel:") ||
				lower.startsWith("#")) {

				skippedCount++
				return
			}

			String host = extractHost(href)

			if (!host) {
				skippedCount++
				return
			}

			if (isEnvironmentHost(host)) {
				invalidUrls.add(href)
			}
		}


		KeywordUtil.logInfo(
			"Visible links checked: ${visibleCount} " +
			"(skipped ${skippedCount} non-http/unparseable link(s))"
		)

		KeywordUtil.logInfo(
			"Potential environment URLs found: " + invalidUrls.size()
		)

		invalidUrls.each { url ->
			KeywordUtil.logInfo("Environment URL: ${url}")
		}


		if (!invalidUrls.isEmpty()) {

			KeywordUtil.markFailed(
				"Dev/Staging/Test/UAT URL(s) detected: " +
				invalidUrls.join(", ")
			)

		} else {

			KeywordUtil.markPassed(
				"No dev/staging/test/UAT URLs detected among " +
				"${visibleCount} visible link(s)."
			)
		}
	}



	/*
	 * ============================================================
	 * EXTRACT HOSTNAME
	 * ============================================================
	 * Using the actual hostname (not the raw href string) avoids
	 * false positives from paths/query strings that happen to
	 * contain words like "test" or "dev" (e.g. a blog post titled
	 * "how-to-test-your-app", or "?ref=staging-promo" on a
	 * legitimate production link).
	 */

	private String extractHost(String href) {

		try {

			URI uri = new URI(href)
			String host = uri.getHost()

			return host ? host.toLowerCase() : null

		} catch (URISyntaxException e) {

			return null

		} catch (Exception e) {

			return null
		}
	}



	/*
	 * ============================================================
	 * IS ENVIRONMENT HOST
	 * ============================================================
	 * Checks the hostname label-by-label instead of doing a raw
	 * substring match on the whole URL, so:
	 *
	 *   - "contest.example.com"   -> NOT flagged (label is "contest")
	 *   - "backstage.example.com" -> NOT flagged (label is "backstage")
	 *   - "test.example.com"      -> flagged (label is exactly "test")
	 *   - "myapp-dev.example.com" -> flagged (label ends with "-dev")
	 *   - "staging-app.example.com" -> flagged (label starts with
	 *                                   "staging-")
	 *   - "127.0.0.1"             -> flagged
	 *   - "localhost"             -> flagged
	 */

	private static final List<String> ENV_KEYWORDS = [
		"dev",
		"development",
		"staging",
		"stage",
		"qa",
		"test",
		"uat"
	]

	private boolean isEnvironmentHost(String host) {

		if (!host) {
			return false
		}

		if (host == "localhost" || host.startsWith("127.")) {
			return true
		}

		// IPv4 localhost/loopback range check (127.x.x.x already covered above)

		List<String> labels = host.split("\\.") as List<String>

		for (String label : labels) {

			if (!label) {
				continue
			}

			for (String keyword : ENV_KEYWORDS) {

				if (label == keyword) {
					return true
				}

				// Hyphen/underscore-delimited prefix or suffix,
				// e.g. "myapp-dev", "dev-myapp", "staging_app"
				if (label.startsWith(keyword + "-") ||
					label.startsWith(keyword + "_") ||
					label.endsWith("-" + keyword) ||
					label.endsWith("_" + keyword)) {

					return true
				}
			}
		}

		return false
	}
}