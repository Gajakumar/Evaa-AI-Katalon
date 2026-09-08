//package utils
//
//import com.kms.katalon.core.annotation.Keyword
//import com.kms.katalon.core.util.KeywordUtil
//import com.kms.katalon.core.webui.driver.DriverFactory
//import org.openqa.selenium.By
//import org.openqa.selenium.WebDriver
//import org.openqa.selenium.WebElement
//import org.openqa.selenium.JavascriptExecutor
//
//class ImageChecker {
//
// @Keyword
//def verifyAllImages() {
//
//    WebDriver driver =
//            DriverFactory.getWebDriver()
//
//    JavascriptExecutor js =
//            (JavascriptExecutor) driver
//
//    List<WebElement> images =
//            driver.findElements(By.tagName("img"))
//
//    int broken = 0
//
//    KeywordUtil.logInfo(
//            "=============================================="
//    )
//
//    KeywordUtil.logInfo(
//            "       IMAGE VERIFICATION STARTED"
//    )
//
//    KeywordUtil.logInfo(
//            "=============================================="
//    )
//
//    KeywordUtil.logInfo(
//            "Total images: ${images.size()}"
//    )
//
//    images.eachWithIndex { WebElement img, int index ->
//
//        try {
//
//            String src =
//                    img.getAttribute("src")
//
//            String alt =
//                    img.getAttribute("alt")
//
//            String loading =
//                    img.getAttribute("loading")
//
//            KeywordUtil.logInfo(
//                    "🔎 Checking Image [${index + 1}]"
//            )
//
//            KeywordUtil.logInfo(
//                    "SRC     : ${src}"
//            )
//
//            KeywordUtil.logInfo(
//                    "ALT     : ${alt}"
//            )
//
//            KeywordUtil.logInfo(
//                    "Loading : ${loading}"
//            )
//
//            // ---------------------------------------------------------
//            // SCROLL IMAGE INTO VIEW
//            // Important for loading="lazy"
//            // ---------------------------------------------------------
//
//            js.executeScript(
//                    """
//                    arguments[0].scrollIntoView({
//                        behavior: 'instant',
//                        block: 'center',
//                        inline: 'center'
//                    });
//                    """,
//                    img
//            )
//
//            // Give lazy-loading JavaScript time to trigger
//            Thread.sleep(1500)
//
//            // ---------------------------------------------------------
//            // WAIT FOR IMAGE TO LOAD
//            // ---------------------------------------------------------
//
//            Boolean loaded = false
//
//            long endTime =
//                    System.currentTimeMillis() + 10000
//
//            while (
//                    System.currentTimeMillis() < endTime
//            ) {
//
//                loaded =
//                        (Boolean) js.executeScript(
//                                """
//                                var img = arguments[0];
//
//                                return img.complete &&
//                                       img.naturalWidth > 0 &&
//                                       img.naturalHeight > 0;
//                                """,
//                                img
//                        )
//
//                if (loaded) {
//                    break
//                }
//
//                Thread.sleep(500)
//            }
//
//            // ---------------------------------------------------------
//            // GET FINAL IMAGE STATUS
//            // ---------------------------------------------------------
//
//            Boolean complete =
//                    (Boolean) js.executeScript(
//                            "return arguments[0].complete;",
//                            img
//                    )
//
//            Long naturalWidth =
//                    ((Number) js.executeScript(
//                            "return arguments[0].naturalWidth;",
//                            img
//                    )).longValue()
//
//            Long naturalHeight =
//                    ((Number) js.executeScript(
//                            "return arguments[0].naturalHeight;",
//                            img
//                    )).longValue()
//
//            // ---------------------------------------------------------
//            // RESULT
//            // ---------------------------------------------------------
//
//            if (loaded &&
//                naturalWidth > 0 &&
//                naturalHeight > 0) {
//
//                KeywordUtil.logInfo(
//                        "✅ Image [${index + 1}] OK | ${alt}"
//                )
//
//                KeywordUtil.logInfo(
//                        "   Size : " +
//                        "${naturalWidth}x${naturalHeight}"
//                )
//
//            } else {
//
//                broken++
//
//                KeywordUtil.logInfo(
//                        ""
//                )
//
//                KeywordUtil.logInfo(
//                        "❌ Broken Image [${index + 1}]"
//                )
//
//                KeywordUtil.logInfo(
//                        "SRC          : ${src}"
//                )
//
//                KeywordUtil.logInfo(
//                        "ALT          : ${alt}"
//                )
//
//                KeywordUtil.logInfo(
//                        "Loading      : ${loading}"
//                )
//
//                KeywordUtil.logInfo(
//                        "Complete     : ${complete}"
//                )
//
//                KeywordUtil.logInfo(
//                        "NaturalWidth : ${naturalWidth}"
//                )
//
//                KeywordUtil.logInfo(
//                        "NaturalHeight: ${naturalHeight}"
//                )
//            }
//
//        } catch (Exception e) {
//
//            broken++
//
//            KeywordUtil.logInfo(
//                    "❌ Error checking Image [${index + 1}]"
//            )
//
//            KeywordUtil.logInfo(
//                    "Error: ${e.getMessage()}"
//            )
//        }
//    }
//
//    // ---------------------------------------------------------
//    // RETURN TO TOP
//    // ---------------------------------------------------------
//
//    js.executeScript(
//            "window.scrollTo(0, 0);"
//    )
//
//    Thread.sleep(500)
//
//    // ---------------------------------------------------------
//    // SUMMARY
//    // ---------------------------------------------------------
//
//    KeywordUtil.logInfo(
//            ""
//    )
//
//    KeywordUtil.logInfo(
//            "=============================================="
//    )
//
//    KeywordUtil.logInfo(
//            "       IMAGE VERIFICATION SUMMARY"
//    )
//
//    KeywordUtil.logInfo(
//            "=============================================="
//    )
//
//    KeywordUtil.logInfo(
//            "Total Images  : ${images.size()}"
//    )
//
//    KeywordUtil.logInfo(
//            "Broken Images : ${broken}"
//    )
//
//    KeywordUtil.logInfo(
//            "Valid Images  : ${images.size() - broken}"
//    )
//
//    KeywordUtil.logInfo(
//            "=============================================="
//    )
//
//    if (broken > 0) {
//
//        KeywordUtil.markFailed(
//                "${broken} broken image(s) found."
//        )
//
//    } else {
//
//        KeywordUtil.markPassed(
//                "All ${images.size()} images loaded successfully."
//        )
//    }
//}
//  
//}

package utils

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.driver.DriverFactory

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.StaleElementReferenceException


class ImageChecker {

	/*
	 * ============================================================
	 * MAIN KEYWORD
	 * ============================================================
	 *
	 * Call this from Katalon, right after navigating OR right after
	 * opening any in-page popup/modal:
	 *
	 * CustomKeywords.'utils.ImageChecker.verifyAllImages'()
	 *
	 * Only checks images currently VISIBLE on screen - same
	 * behaviour as BrokenLinkChecker/EnvironmentUrlChecker - so
	 * calling it after opening a popup checks the popup's images
	 * (plus whatever else is visibly on the page) without wasting
	 * time re-scrolling to and waiting on hidden/off-screen images
	 * elsewhere on the page.
	 *
	 * Optional param: maxWaitSeconds (default 10) - how long to
	 * wait for a not-yet-loaded image before giving up on it.
	 */
	@Keyword
	def verifyAllImages(int maxWaitSeconds = 10) {

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


		List<WebElement> allImages = []

		try {

			allImages = driver.findElements(By.tagName("img"))

			if (allImages == null) {
				allImages = []
			}

		} catch (Exception e) {

			KeywordUtil.logInfo(
				"Unable to collect images: " + e.getMessage()
			)

			allImages = []
		}


		List<WebElement> images = []

		for (WebElement img : allImages) {

			try {

				if (img != null && img.isDisplayed()) {
					images.add(img)
				}

			} catch (StaleElementReferenceException ignored) {
				// element detached before we could check it - skip
			} catch (Exception ignored) {
			}
		}


		KeywordUtil.logInfo(
			"=============================================="
		)

		KeywordUtil.logInfo(
			"       IMAGE VERIFICATION STARTED"
		)

		KeywordUtil.logInfo(
			"=============================================="
		)

		KeywordUtil.logInfo(
			"Total <img> elements found : ${allImages.size()}"
		)

		KeywordUtil.logInfo(
			"Visible images to check    : ${images.size()}"
		)


		int broken = 0
		int skipped = 0
		int checked = 0


		images.eachWithIndex { WebElement img, int index ->

			try {

				String src = ""
				String alt = ""
				String loadingAttr = ""

				try {
					src = img.getAttribute("src") ?: ""
					alt = img.getAttribute("alt") ?: ""
					loadingAttr = img.getAttribute("loading") ?: ""
				} catch (StaleElementReferenceException e) {
					skipped++
					KeywordUtil.logInfo(
						"[${index + 1}] Skipped - element became stale " +
						"(popup/page likely re-rendered mid-check)"
					)
					return
				}


				if (!src || !src.trim()) {

					skipped++

					KeywordUtil.logInfo(
						"[${index + 1}] Skipped - empty/unset src attribute"
					)

					return
				}


				checked++

				KeywordUtil.logInfo("Checking Image [${index + 1}]")
				KeywordUtil.logInfo("SRC     : ${src}")
				KeywordUtil.logInfo("ALT     : ${alt}")
				KeywordUtil.logInfo("Loading : ${loadingAttr}")


				/*
				 * ---------------------------------------------------
				 * FAST PATH: check if it's already loaded before
				 * bothering to scroll/wait. Most visible, non-lazy
				 * images will already be loaded by this point.
				 * ---------------------------------------------------
				 */

				Boolean loaded = false

				try {

					loaded = (Boolean) js.executeScript(
						"""
                        var img = arguments[0];
                        return img.complete &&
                               img.naturalWidth > 0 &&
                               img.naturalHeight > 0;
                        """,
						img
					)

				} catch (StaleElementReferenceException e) {

					skipped++

					KeywordUtil.logInfo(
						"[${index + 1}] Skipped - element became stale mid-check"
					)

					return
				}


				if (!loaded) {

					/*
					 * Not loaded yet - scroll into view (important for
					 * loading="lazy") and give it a chance to load.
					 */

					try {

						js.executeScript(
							"""
                            arguments[0].scrollIntoView({
                                behavior: 'instant',
                                block: 'center',
                                inline: 'center'
                            });
                            """,
							img
						)

					} catch (StaleElementReferenceException e) {

						skipped++

						KeywordUtil.logInfo(
							"[${index + 1}] Skipped - element became stale while scrolling"
						)

						return
					}

					long endTime =
						System.currentTimeMillis() + (maxWaitSeconds * 1000L)

					while (System.currentTimeMillis() < endTime) {

						try {

							loaded = (Boolean) js.executeScript(
								"""
                                var img = arguments[0];
                                return img.complete &&
                                       img.naturalWidth > 0 &&
                                       img.naturalHeight > 0;
                                """,
								img
							)

						} catch (StaleElementReferenceException e) {

							skipped++

							KeywordUtil.logInfo(
								"[${index + 1}] Skipped - element became stale while waiting"
							)

							return
						}

						if (loaded) {
							break
						}

						Thread.sleep(500)
					}
				}


				/*
				 * ---------------------------------------------------
				 * FINAL STATUS
				 * ---------------------------------------------------
				 */

				Boolean complete = false
				Long naturalWidth = 0L
				Long naturalHeight = 0L

				try {

					complete = (Boolean) js.executeScript(
						"return arguments[0].complete;", img
					)

					naturalWidth = ((Number) js.executeScript(
						"return arguments[0].naturalWidth;", img
					)).longValue()

					naturalHeight = ((Number) js.executeScript(
						"return arguments[0].naturalHeight;", img
					)).longValue()

				} catch (StaleElementReferenceException e) {

					skipped++

					KeywordUtil.logInfo(
						"[${index + 1}] Skipped - element became stale before final check"
					)

					return
				}


				if (loaded && naturalWidth > 0 && naturalHeight > 0) {

					KeywordUtil.logInfo(
						"OK [${index + 1}] ${alt} | Size: ${naturalWidth}x${naturalHeight}"
					)

				} else {

					broken++

					KeywordUtil.logInfo("")
					KeywordUtil.logInfo("BROKEN IMAGE [${index + 1}]")
					KeywordUtil.logInfo("SRC          : ${src}")
					KeywordUtil.logInfo("ALT          : ${alt}")
					KeywordUtil.logInfo("Loading      : ${loadingAttr}")
					KeywordUtil.logInfo("Complete     : ${complete}")
					KeywordUtil.logInfo("NaturalWidth : ${naturalWidth}")
					KeywordUtil.logInfo("NaturalHeight: ${naturalHeight}")
				}

			} catch (Exception e) {

				broken++

				KeywordUtil.logInfo("Error checking Image [${index + 1}]")
				KeywordUtil.logInfo("Error: ${e.getMessage()}")
			}
		}


		try {
			js.executeScript("window.scrollTo(0, 0);")
			Thread.sleep(300)
		} catch (Exception ignored) {
		}


		KeywordUtil.logInfo("")
		KeywordUtil.logInfo(
			"=============================================="
		)

		KeywordUtil.logInfo(
			"       IMAGE VERIFICATION SUMMARY"
		)

		KeywordUtil.logInfo(
			"=============================================="
		)

		KeywordUtil.logInfo("Total <img> Found  : ${allImages.size()}")
		KeywordUtil.logInfo("Visible & Checked  : ${checked}")
		KeywordUtil.logInfo("Broken Images      : ${broken}")
		KeywordUtil.logInfo("Valid Images       : ${checked - broken}")
		KeywordUtil.logInfo("Skipped (no src/stale): ${skipped}")
		KeywordUtil.logInfo(
			"=============================================="
		)

		if (broken > 0) {

			KeywordUtil.markFailed(
				"${broken} broken image(s) found."
			)

		} else {

			KeywordUtil.markPassed(
				"All ${checked} visible image(s) loaded successfully."
			)
		}
	}
}