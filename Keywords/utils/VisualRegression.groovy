//package utils
//
//import com.kms.katalon.core.annotation.Keyword
//import com.kms.katalon.core.util.KeywordUtil
//import com.kms.katalon.core.webui.driver.DriverFactory
//import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
//
//class VisualRegression {
//
//    @Keyword
//    def captureScreenshot(String screenshotName) {
//
//        String path =
//                "Screenshots/EVAA/${screenshotName}.png"
//
//        WebUI.takeFullPageScreenshot(path)
//
//        KeywordUtil.logInfo(
//                "Visual screenshot captured: ${path}"
//        )
//    }
//
//    @Keyword
//    def captureComponent(
//            String screenshotName,
//            String xpath) {
//
//        def element =
//                DriverFactory.getWebDriver()
//                        .findElement(
//                                org.openqa.selenium.By.xpath(xpath)
//                        )
//
//        String path =
//                "Screenshots/EVAA/${screenshotName}.png"
//
//        org.apache.commons.io.FileUtils.copyFile(
//                ((org.openqa.selenium.TakesScreenshot)
//                        DriverFactory.getWebDriver())
//                        .getScreenshotAs(
//                                org.openqa.selenium.OutputType.FILE
//                        ),
//                new File(path)
//        )
//
//        KeywordUtil.logInfo(
//                "Component screenshot captured: ${path}"
//        )
//    }
//}

package utils

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.OutputType
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.NoSuchElementException

import org.apache.commons.io.FileUtils

import java.io.File


class VisualRegression {

	/*
	 * ============================================================
	 * CAPTURE FULL PAGE SCREENSHOT
	 * ============================================================
	 */
	@Keyword
	def captureScreenshot(String screenshotName) {

		if (!screenshotName || !screenshotName.trim()) {

			KeywordUtil.markFailed(
				"captureScreenshot() called with a blank screenshotName."
			)

			return
		}

		String path = "Screenshots/EVAA/${screenshotName.trim()}.png"

		try {

			ensureParentDirectoryExists(path)

			WebUI.takeFullPageScreenshot(path)

			KeywordUtil.logInfo("Visual screenshot captured: ${path}")

		} catch (Exception e) {

			KeywordUtil.markFailed(
				"Unable to capture screenshot '${screenshotName}': " + e.getMessage()
			)
		}
	}


	/*
	 * ============================================================
	 * CAPTURE COMPONENT SCREENSHOT
	 * ============================================================
	 *
	 * Captures ONLY the element matched by xpath, cropped to its
	 * rendered bounding box - not the whole page. Scrolls the
	 * element into view and waits briefly for it to be visible
	 * first, since an off-screen or not-yet-rendered element would
	 * otherwise produce a blank/clipped image.
	 *
	 * This is what you want for baselining a popup/modal or a
	 * specific widget without unrelated page content causing false
	 * diffs on every comparison.
	 */
	@Keyword
	def captureComponent(String screenshotName, String xpath, int timeoutInSeconds = 10) {

		if (!screenshotName || !screenshotName.trim()) {

			KeywordUtil.markFailed(
				"captureComponent() called with a blank screenshotName."
			)

			return
		}

		if (!xpath || !xpath.trim()) {

			KeywordUtil.markFailed(
				"captureComponent() called with a blank xpath."
			)

			return
		}

		WebDriver driver = null

		try {
			driver = DriverFactory.getWebDriver()
		} catch (Exception e) {

			KeywordUtil.markFailed(
				"Unable to get WebDriver: " + e.getMessage()
			)

			return
		}

		WebElement element = null

		try {

			element = driver.findElement(By.xpath(xpath))

		} catch (NoSuchElementException e) {

			KeywordUtil.markFailed(
				"captureComponent() failed - no element found for xpath: ${xpath}"
			)

			return

		} catch (Exception e) {

			KeywordUtil.markFailed(
				"captureComponent() failed while locating element: " + e.getMessage()
			)

			return
		}


		try {

			JavascriptExecutor js = (JavascriptExecutor) driver

			js.executeScript(
				"""
                arguments[0].scrollIntoView({
                    behavior: 'instant',
                    block: 'center',
                    inline: 'center'
                });
                """,
				element
			)

		} catch (Exception e) {

			KeywordUtil.logInfo(
				"Unable to scroll element into view: " + e.getMessage()
			)
		}


		boolean visible = false

		long endTime = System.currentTimeMillis() + (timeoutInSeconds * 1000L)

		while (System.currentTimeMillis() < endTime) {

			try {

				if (element.isDisplayed()) {
					visible = true
					break
				}

			} catch (Exception ignored) {
			}

			Thread.sleep(300)
		}

		if (!visible) {

			KeywordUtil.markFailed(
				"captureComponent() failed - element for xpath '${xpath}' never became " +
				"visible within ${timeoutInSeconds}s."
			)

			return
		}


		String path = "Screenshots/EVAA/${screenshotName.trim()}.png"

		try {

			ensureParentDirectoryExists(path)

			File screenshotFile =
				((TakesScreenshot) element).getScreenshotAs(OutputType.FILE)

			FileUtils.copyFile(screenshotFile, new File(path))

			KeywordUtil.logInfo("Component screenshot captured: ${path}")

		} catch (Exception e) {

			KeywordUtil.markFailed(
				"Unable to capture component screenshot '${screenshotName}': " + e.getMessage()
			)
		}
	}


	private void ensureParentDirectoryExists(String path) {

		File file = new File(path)
		File parent = file.getParentFile()

		if (parent != null && !parent.exists()) {
			parent.mkdirs()
		}
	}
}