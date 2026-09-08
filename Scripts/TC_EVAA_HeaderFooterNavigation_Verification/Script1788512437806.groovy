import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.llm.keyword.LlmKeywords as LLM
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.WebDriver
import com.kms.katalon.core.webui.driver.DriverFactory
import customkeywords.ScrollVerification

// ============================================================
// Test Object declarations (all objects resolved once, up top)
// ============================================================

// --- Header objects ---
TestObject evaaLogo            = findTestObject('Object Repository/EVVA/Header/evaa img')
TestObject aiWorkforceMenu     = findTestObject('Object Repository/EVVA/Header/span_AI Workforce')
TestObject solutionsMenu       = findTestObject('Object Repository/EVVA/Header/span_Solutions')
TestObject realResultsMenu     = findTestObject('Object Repository/EVVA/Header/span_Real Results')
TestObject resourcesMenu       = findTestObject('Object Repository/EVVA/Header/span_Resources')
TestObject aboutMenu           = findTestObject('Object Repository/EVVA/Header/span_About')
TestObject loginMenu           = findTestObject('Object Repository/EVVA/Header/span_Login')
TestObject signUpOption        = findTestObject('Object Repository/EVVA/Header/li_Sign Up')

// --- Page navigation objects (destination page markers) ---
TestObject rosterPage          = findTestObject('Object Repository/Page Navigations/page_The roster')
TestObject solutionsPage       = findTestObject('Object Repository/Page Navigations/page_Solutions')
TestObject realResultsPage     = findTestObject('Object Repository/Page Navigations/page_Real results')
TestObject latestResourcesPage = findTestObject('Object Repository/Page Navigations/page_Latest resources')
TestObject aboutPage           = findTestObject('Object Repository/Page Navigations/About Page/page_About')

// --- Login page objects ---
TestObject loginPageLogo       = findTestObject('Object Repository/Page Navigations/Login Page/Evaa logo')
TestObject signInToYourAccount = findTestObject('Object Repository/Page Navigations/Login Page/Sign In to Your Account')

// --- Sign Up page objects ---
TestObject welcomeToEvaa       = findTestObject('Object Repository/Page Navigations/Sign In Page/Welcome to EVAA.AI')

// --- Footer objects ---
TestObject privacyPolicyLink   = findTestObject('Object Repository/EVVA/Footer/span_Privacy Policy')
TestObject copyrightText       = findTestObject('Object Repository/EVVA/Footer/span_2026 EVAA.AI, LLC. All Rights Reserved')

ScrollVerification scrollHelper = new ScrollVerification()
// ============================================================
// Step 1: Navigate to the EVAA application URL
// ============================================================
WebUI.navigateToUrl(GlobalVariable.Url)


// ============================================================
// Step 2: Wait for the page to finish loading
// ============================================================
WebUI.waitForPageLoad(30)


// ============================================================
// Step 3: Verify EVAA logo is visible
// ============================================================
WebUI.verifyElementVisible(evaaLogo)


// ============================================================
// Step 4: Verify "AI Workforce" menu is visible
// ============================================================
WebUI.verifyElementVisible(aiWorkforceMenu)


// ============================================================
// Step 5: Verify "Solutions" menu is visible
// ============================================================
WebUI.verifyElementVisible(solutionsMenu)


// ============================================================
// Step 6: Verify "Real Results" menu is visible
// ============================================================
WebUI.verifyElementVisible(realResultsMenu)


// ============================================================
// Step 7: Verify "Resources" menu is visible
// ============================================================
WebUI.verifyElementVisible(resourcesMenu)


// ============================================================
// Step 8: Verify "About" menu is visible
// ============================================================
WebUI.verifyElementVisible(aboutMenu)


// ============================================================
// Step 9: Verify "Login" menu is visible
// ============================================================
WebUI.verifyElementVisible(loginMenu)


// ============================================================
// Step 10: Verify "Sign Up" option is visible
// ============================================================
WebUI.verifyElementVisible(signUpOption)


// ============================================================
// Step 11: Click on the EVAA logo
// ============================================================
WebUI.click(evaaLogo)


// ============================================================
// Step 12: Verify that the current URL contains "evaaaidev"
// ============================================================
WebUI.verifyMatch(
	WebUI.getUrl(),
	'.*evaaaidev.*',
	true
)


// ============================================================
// Step 13: Click "AI Workforce" menu and verify the roster page loads
// ============================================================
WebUI.click(aiWorkforceMenu)
scrollHelper.verifyAndAssertScrollToSection('#workforce', '#workforce')


// ============================================================
// Step 14: Click "Solutions" menu and verify the Solutions page loads
// ============================================================
WebUI.click(solutionsMenu)
scrollHelper.verifyAndAssertScrollToSection('#solutions', '#solutions')


// ============================================================
// Step 15: Click "Real Results" menu and verify the Real Results page loads
// ============================================================
WebUI.click(realResultsMenu)
scrollHelper.verifyAndAssertScrollToSection('#results', '#results')


// ============================================================
// Step 16: Click "Resources" menu and verify the Latest Resources page loads
// ============================================================
WebUI.click(resourcesMenu)
scrollHelper.verifyAndAssertScrollToSection('#resources', '#resources')


// ============================================================
// Step 17: Click "About" menu, verify URL and About page content
// ============================================================
WebUI.click(aboutMenu)

WebUI.verifyMatch(
	WebUI.getUrl(),
	'https://evaaaidev\\.wpenginepowered\\.com/about/',
	true
)

WebUI.verifyElementVisible(aboutPage)

// Go back to previous page
WebUI.back()
WebUI.waitForPageLoad(10)


// ============================================================
// Step 18: Click "Login" menu and verify Login page content
// ============================================================
WebUI.click(loginMenu)

WebUI.verifyMatch(
    WebUI.getUrl(),
    '.*identity\\.maximeyes\\.com/account/login.*',
    true
)

WebUI.verifyElementVisible(loginPageLogo)
WebUI.verifyElementVisible(signInToYourAccount)

// Go back to previous page
WebUI.back()
WebUI.waitForPageLoad(10)


// ============================================================
// Step 19: Click "Sign Up" option and verify Sign Up page content
// ============================================================
WebUI.click(signUpOption)
WebUI.verifyMatch(
	WebUI.getUrl(),
	'https://account\\.evaa\\.ai/signup/?',
	true
)
WebUI.verifyElementVisible(welcomeToEvaa)


// ============================================================
// Step 20: Return to the EVAA home page
// ============================================================
WebUI.navigateToUrl(GlobalVariable.Url)
WebUI.waitForPageLoad(10)


// ============================================================
// Step 21: Verify "Privacy Policy" option is visible
// ============================================================
WebUI.verifyElementVisible(privacyPolicyLink)


// ============================================================
// Step 22: Verify "©2026 EVAA.AI, LLC. All Rights Reserved." text is visible
// ============================================================
WebUI.verifyElementVisible(copyrightText)


// ============================================================
// Step 23: Click on the "Privacy Policy" link
// ============================================================
WebUI.click(privacyPolicyLink)
WebUI.waitForPageLoad(30)


// ============================================================
// Step 24: Verify Privacy Policy page URL
// ============================================================
WebUI.verifyMatch(
	WebUI.getUrl(),
	'https://evaaaidev\\.wpenginepowered\\.com/privacy-policy/',
	true
)


// ============================================================
// Step 25: Verify the complete Privacy Policy content
// ============================================================
CustomKeywords.'utils.VerifyPrivacyPolicy.verifyFullPrivacyPolicyContent'()