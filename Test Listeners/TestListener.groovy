//import com.kms.katalon.core.annotation.*
//import com.kms.katalon.core.context.*
//import com.kms.katalon.core.configuration.RunConfiguration
//import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
//import com.kms.katalon.core.webui.driver.DriverFactory
//import com.kms.katalon.core.model.FailureHandling
//
//class FailureScreenshotListener {
//
//	/*
//	 * Executes before every test case starts.
//	 * @param testCaseContext related information of the executed test case.
//	 */
//	@BeforeTestCase
//def beforeTestCase(TestCaseContext testCaseContext) {
//
//    println testCaseContext.getTestCaseId()
//    println testCaseContext.getTestCaseVariables()
//
//    // ---------------------------
//    // Chrome Arguments
//    // ---------------------------
//    List<String> args = new ArrayList<>()
//
//    args.add("--start-maximized")
//    args.add("--disable-notifications")
//    args.add("--disable-infobars")
//    args.add("--disable-popup-blocking")
//    args.add("--no-sandbox")
//    args.add("--disable-dev-shm-usage")
//    args.add("--disable-gpu")
//    args.add("--disable-save-password-bubble")
//    args.add("--disable-features=AutofillAddressProfileSavePrompt")
//    args.add("--remote-allow-origins=*")
//	args.add("--disable-features=AutofillServerCommunication,AutofillAddressProfileSavePrompt")
//
//    RunConfiguration.setWebDriverPreferencesProperty("args", args)
//
//    // ---------------------------
//    // Chrome Preferences
//    // ---------------------------
//    Map<String, Object> prefs = new HashMap<>()
//
//    prefs.put("credentials_enable_service", false)
//    prefs.put("profile.password_manager_enabled", false)
//    prefs.put("autofill.profile_enabled", false)
//    prefs.put("autofill.credit_card_enabled", false)
//    prefs.put("profile.default_content_setting_values.notifications", 2)
//
//    RunConfiguration.setWebDriverPreferencesProperty("prefs", prefs)
//
//    WebUI.openBrowser('')
//    WebUI.maximizeWindow()
//}
//    /**
//     * Runs AFTER every test case
//     */
//    @AfterTestCase
//    def afterTestCase(TestCaseContext testCaseContext) {
//
//        println "⏹ Finished Test Case : " + testCaseContext.getTestCaseId()
//        println "📌 Status            : " + testCaseContext.getTestCaseStatus()
//
//        if (testCaseContext.getTestCaseStatus() != 'PASS') {
//
//            try {
//                def driver = DriverFactory.getWebDriver()
//
//                // ✅ SAFETY CHECK
//                if (driver == null || driver.getSessionId() == null) {
//                    println "⚠ Browser session not available. Screenshot skipped."
//                    return
//                }
//
//                String projectDir = RunConfiguration.getProjectDir()
//                String failedFolder = projectDir + "/Screenshots/FAILED"
//                new File(failedFolder).mkdirs()
//
//                String testCaseName = testCaseContext.getTestCaseId()
//                        .replaceAll('[^a-zA-Z0-9_]', '_')
//
//                String timeStamp = new Date().format("yyyyMMdd_HHmmss")
//
//                String screenshotPath =
//                        failedFolder + "/" + testCaseName + "_" + timeStamp + ".png"
//
//                WebUI.takeScreenshot(screenshotPath, FailureHandling.OPTIONAL)
//
//                println "📸 Screenshot saved at:"
//                println screenshotPath
//
//            } catch (Exception e) {
//                println "❌ Screenshot capture failed: " + e.getMessage()
//            }
//        }
//
//        // Close browser safely
//        try {
//            if (DriverFactory.getWebDriver() != null) {
//                WebUI.closeBrowser()
//                println "🧹 Browser closed"
//            }
//        } catch (Exception e) {
//            println "⚠ Browser already closed"
//        }
//    }
//
//    /**
//     * Runs BEFORE test suite
//     */
//    @BeforeTestSuite
//    def beforeTestSuite(TestSuiteContext testSuiteContext) {
//        println "🚀 Starting Test Suite : " + testSuiteContext.getTestSuiteId()
//    }
//
//    /**
//     * Runs AFTER test suite
//     */
//    @AfterTestSuite
//    def afterTestSuite(TestSuiteContext testSuiteContext) {
//        println "🏁 Finished Test Suite : " + testSuiteContext.getTestSuiteId()
//    }
//}



import com.kms.katalon.core.annotation.*
import com.kms.katalon.core.context.*
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.model.FailureHandling


class FailureScreenshotListener {

	/*
	 * Executes before every test case starts.
	 */
	@BeforeTestCase
	def beforeTestCase(TestCaseContext testCaseContext) {

		println "=============================================="
		println "🚀 Starting Test Case"
		println "Test Case ID       : " + testCaseContext.getTestCaseId()
		println "Test Case Variables: " + testCaseContext.getTestCaseVariables()

		// ---------------------------------------------------------
		// Detect the browser selected for this execution
		// ---------------------------------------------------------
		String browserType = getBrowserType()

		println "🌐 Browser Type     : " + browserType

		// ---------------------------------------------------------
		// Apply Chrome-only capabilities
		//
		// IMPORTANT:
		// Do NOT set "args" or "prefs" for Safari.
		// Safari/TestCloud will reject these as W3C violations.
		// ---------------------------------------------------------
		if (isChrome(browserType)) {

			println "🟢 Chrome detected - applying Chrome options"

			applyChromeOptions()

		} else {

			println "🔵 Non-Chrome browser detected - skipping Chrome options"
			println "   No 'args' or 'prefs' capabilities will be added."
		}

		// ---------------------------------------------------------
		// Open browser
		// ---------------------------------------------------------
		try {

			WebUI.openBrowser('')

			println "✅ Browser opened successfully"

			// Maximize only where it is supported/useful.
			// Safari/TestCloud may ignore this or behave differently.
			try {
				WebUI.maximizeWindow(FailureHandling.OPTIONAL)
			} catch (Exception e) {
				println "⚠ Could not maximize browser: " + e.getMessage()
			}

		} catch (Exception e) {

			println "❌ Browser failed to open"
			println "Error: " + e.getMessage()

			throw e
		}

		println "=============================================="
	}


	/*
	 * Gets the browser type from Katalon's execution properties.
	 *
	 * Examples:
	 * CHROME_DRIVER
	 * SAFARI_DRIVER
	 * FIREFOX_DRIVER
	 * EDGE_DRIVER
	 * REMOTE_WEB_DRIVER
	 */
	private String getBrowserType() {

		try {

			Map executionProperties = RunConfiguration.getExecutionProperties()

			println "🔎 Execution Properties:"
			println executionProperties

			def drivers = executionProperties?.get("drivers")

			def system = drivers?.get("system")

			def webUI = system?.get("WebUI")

			def browserType = webUI?.get("browserType")

			if (browserType != null) {
				return browserType.toString().toUpperCase()
			}

		} catch (Exception e) {

			println "⚠ Unable to read browser type from execution properties."
			println "Error: " + e.getMessage()
		}

		// ---------------------------------------------------------
		// Safe fallback
		//
		// IMPORTANT:
		// Defaulting to Safari/non-Chrome is safer than accidentally
		// sending Chrome capabilities to Safari.
		// ---------------------------------------------------------
		println "⚠ Browser type could not be detected."
		println "⚠ Chrome capabilities will NOT be applied."

		return "UNKNOWN"
	}


	/*
	 * Returns true only when the selected browser is Chrome.
	 */
	private boolean isChrome(String browserType) {

		if (browserType == null) {
			return false
		}

		return browserType.toUpperCase().contains("CHROME")
	}


	/*
	 * Apply Chrome-specific arguments and preferences.
	 *
	 * NEVER call this method for Safari.
	 */
	private void applyChromeOptions() {

		// ---------------------------------------------------------
		// Chrome Arguments
		// ---------------------------------------------------------
		List<String> args = new ArrayList<>()

		args.add("--start-maximized")
		args.add("--disable-notifications")
		args.add("--disable-infobars")
		args.add("--disable-popup-blocking")
		args.add("--no-sandbox")
		args.add("--disable-dev-shm-usage")
		args.add("--disable-gpu")
		args.add("--disable-save-password-bubble")
		args.add("--disable-features=AutofillAddressProfileSavePrompt")
		args.add("--remote-allow-origins=*")
		args.add("--disable-features=AutofillServerCommunication,AutofillAddressProfileSavePrompt")

		RunConfiguration.setWebDriverPreferencesProperty(
				"args",
				args
		)

		// ---------------------------------------------------------
		// Chrome Preferences
		// ---------------------------------------------------------
		Map<String, Object> prefs = new HashMap<>()

		prefs.put(
				"credentials_enable_service",
				false
		)

		prefs.put(
				"profile.password_manager_enabled",
				false
		)

		prefs.put(
				"autofill.profile_enabled",
				false
		)

		prefs.put(
				"autofill.credit_card_enabled",
				false
		)

		prefs.put(
				"profile.default_content_setting_values.notifications",
				2
		)

		RunConfiguration.setWebDriverPreferencesProperty(
				"prefs",
				prefs
		)

		println "✅ Chrome 'args' configured"
		println "✅ Chrome 'prefs' configured"
	}


	/**
	 * Runs AFTER every test case
	 */
	@AfterTestCase
	def afterTestCase(TestCaseContext testCaseContext) {

		println "=============================================="
		println "⏹ Finished Test Case : " + testCaseContext.getTestCaseId()
		println "📌 Status             : " + testCaseContext.getTestCaseStatus()

		// ---------------------------------------------------------
		// Take screenshot when test case fails
		// ---------------------------------------------------------
		if (testCaseContext.getTestCaseStatus() != 'PASS') {

			try {

				def driver = DriverFactory.getWebDriver()

				// SAFETY CHECK
				if (driver == null || driver.getSessionId() == null) {

					println "⚠ Browser session not available."
					println "⚠ Screenshot skipped."

				} else {

					String projectDir =
							RunConfiguration.getProjectDir()

					String failedFolder =
							projectDir + "/Screenshots/FAILED"

					new File(failedFolder).mkdirs()

					String testCaseName =
							testCaseContext.getTestCaseId()
									.replaceAll(
											'[^a-zA-Z0-9_]',
											'_'
									)

					String timeStamp =
							new Date().format("yyyyMMdd_HHmmss")

					String screenshotPath =
							failedFolder +
							"/" +
							testCaseName +
							"_" +
							timeStamp +
							".png"

					WebUI.takeScreenshot(
							screenshotPath,
							FailureHandling.OPTIONAL
					)

					println "📸 Screenshot saved at:"
					println screenshotPath
				}

			} catch (Exception e) {

				println "❌ Screenshot capture failed:"
				println e.getMessage()
			}
		}

		// ---------------------------------------------------------
		// Close browser safely
		// ---------------------------------------------------------
		try {

			def driver = DriverFactory.getWebDriver()

			if (driver != null) {

				WebUI.closeBrowser(
						FailureHandling.OPTIONAL
				)

				println "🧹 Browser closed"

			} else {

				println "ℹ No browser session to close"
			}

		} catch (Exception e) {

			println "⚠ Browser already closed or unavailable"
		}

		println "=============================================="
	}


	/*
	 * Runs BEFORE test suite
	 */
	@BeforeTestSuite
	def beforeTestSuite(TestSuiteContext testSuiteContext) {

		println "=============================================="
		println "🚀 Starting Test Suite : " +
				testSuiteContext.getTestSuiteId()
		println "=============================================="
	}


	/*
	 * Runs AFTER test suite
	 */
	@AfterTestSuite
	def afterTestSuite(TestSuiteContext testSuiteContext) {

		println "=============================================="
		println "🏁 Finished Test Suite : " +
				testSuiteContext.getTestSuiteId()
		println "=============================================="
	}
}

