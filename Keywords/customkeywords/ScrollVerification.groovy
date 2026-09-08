package customkeywords

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.llm.keyword.LlmKeywords as LLM
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import com.kms.katalon.core.testobject.ConditionType
import internal.GlobalVariable

public class ScrollVerification {
	/**
	 * Clicks a nav link (matched by partial href) and verifies that the
	 * target section actually scrolled into the visible viewport.
	 *
	 * @param navHrefContains  substring to match in the nav link's href, e.g. "#solutions"
	 * @param targetSelector   CSS selector of the section that should become visible, e.g. "#solutions"
	 * @param maxWaitSeconds   max time to poll for the scroll animation to finish
	 * @return true if the target landed inside the viewport
	 */
	@Keyword
	def boolean verifyScrollToSection(String navHrefContains, String targetSelector, int maxWaitSeconds = 3) {
		// 1. Click the nav link
		TestObject navLink = new TestObject()
		navLink.addProperty("xpath", ConditionType.EQUALS,
			"//a[contains(@href,'${navHrefContains}')]")
		WebUI.click(navLink)

		// 2. Poll until the element is in viewport or timeout is reached
		boolean isInViewport = false
		double top = 0
		double bottom = 0
		double vh = 0

		long endTime = System.currentTimeMillis() + (maxWaitSeconds * 1000)

		while (System.currentTimeMillis() < endTime) {
			def rectTop = WebUI.executeJavaScript(
				"var el = document.querySelector('${targetSelector}'); return el ? el.getBoundingClientRect().top : null;", null)
			def rectBottom = WebUI.executeJavaScript(
				"var el = document.querySelector('${targetSelector}'); return el ? el.getBoundingClientRect().bottom : null;", null)
			def viewportHeight = WebUI.executeJavaScript("return window.innerHeight;", null)

			if (rectTop == null || rectBottom == null) {
				WebUI.comment("Target selector '${targetSelector}' not found in DOM yet, retrying...")
				Thread.sleep(200)
				continue
			}

			top = (rectTop as double)
			bottom = (rectBottom as double)
			vh = (viewportHeight as double)

			isInViewport = (top < vh) && (bottom > 0)

			if (isInViewport) {
				break
			}
			Thread.sleep(200)
		}

		WebUI.comment("Scroll check for '${targetSelector}': top=${top}, bottom=${bottom}, viewportHeight=${vh}, inViewport=${isInViewport}")
		return isInViewport
	}

	/**
	 * Same as above but also asserts the result (fails the test step if not in viewport).
	 */
	@Keyword
	def void verifyAndAssertScrollToSection(String navHrefContains, String targetSelector, int maxWaitSeconds = 3) {
		boolean result = verifyScrollToSection(navHrefContains, targetSelector, maxWaitSeconds)
		WebUI.verifyEqual(result, true)
	}
}
