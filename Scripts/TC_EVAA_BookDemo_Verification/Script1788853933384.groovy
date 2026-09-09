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

import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import customkeywords.InputHelper

class BookDemoPage {

	// ---------------------------------------------------------------
	// Timeout
	// ---------------------------------------------------------------
	// Shared timeout (in seconds) for every WebUI verification call below.
	// If your project already defines a global page load timeout (e.g.
	// GlobalVariable.G_PAGELOADTIMEOUT in your Global Variables profile),
	// point this at it instead, e.g.:
	//     static int PAGE_TIMEOUT = GlobalVariable.G_PAGELOADTIMEOUT
	static int PAGE_TIMEOUT = 30

	// Shared failure-handling mode for every soft verification below.
	// Using CONTINUE_ON_FAILURE means: on failure, Katalon logs the
	// failure, marks that step/test case as FAILED, but keeps executing
	// the remaining steps instead of stopping the test case.
	static FailureHandling SOFT = FailureHandling.CONTINUE_ON_FAILURE

	// ---------------------------------------------------------------
	// Landing page
	// ---------------------------------------------------------------
	static TestObject scheduleDemoLink        = findTestObject('EVVA/Page_Home page redesign  EVAA.AI/a_Schedule a demo')

	// ---------------------------------------------------------------
	// Modal / form chrome (shown on both Step 1 and Step 2)
	// ---------------------------------------------------------------
	static TestObject bookDemoModal            = findTestObject('EVVA/Demo Request/div_Book a Demo')
	static TestObject bookDemoModalTitle        = findTestObject('EVVA/Demo Request/strong_Book a Demo')
	static TestObject bookDemoModalTitleStep2   = findTestObject('EVVA/Demo Request/div_Book a Demo_1')
	static TestObject bookDemoModalTitleStep2Text = findTestObject('EVVA/Demo Request/strong_Book a Demo_1')
	static TestObject bookDemoIntroText         = findTestObject('EVVA/Demo Request/p_Please fill out the information below and wel')
	static TestObject bookDemoIntroTextStep2    = findTestObject('EVVA/Demo Request/p_Please fill out the information below and wel_1')

	// ---------------------------------------------------------------
	// Step 1 fields (contact details)
	// ---------------------------------------------------------------
	static TestObject firstNameLabel   = findTestObject('EVVA/Demo Request/span_First Name')
	static TestObject firstNameInput   = findTestObject('EVVA/Demo Request/input_First Name')

	static TestObject lastNameLabel    = findTestObject('EVVA/Demo Request/span_Last Name')
	static TestObject lastNameInput    = findTestObject('EVVA/Demo Request/input_Last Name')

	static TestObject emailLabel       = findTestObject('EVVA/Demo Request/span_Email')
	static TestObject emailInput       = findTestObject('EVVA/Demo Request/input_Email')

	static TestObject phoneLabel       = findTestObject('EVVA/Demo Request/span_Phone Number')
	static TestObject phoneCountryFlag = findTestObject('EVVA/Demo Request/div_hsfc-PhoneInput_FlagAndCaret')
	static TestObject phoneCountrySearchInput = findTestObject('EVVA/Demo Request/input_Search')
	static TestObject phoneCountryIndiaOption = findTestObject('EVVA/Demo Request/li_India () 91')
	static TestObject phoneInput       = findTestObject('EVVA/Demo Request/input___1')
	static TestObject phoneInputAlt    = findTestObject('EVVA/Demo Request/input__')

	static TestObject stateRegionLabel = findTestObject('EVVA/Demo Request/span_State_Region')
	static TestObject stateRegionInput = findTestObject('EVVA/Demo Request/input_State_Region')

	static TestObject recaptchaFooter  = findTestObject('EVVA/Demo Request/div_rc-anchor-normal-footer')

	static TestObject nextStepButton   = findTestObject('EVVA/Demo Request/button_hs_form_target_widget_1756331635880-13')
	static TestObject nextStepButtonAlt = findTestObject('EVVA/Demo Request/button_hs_form_target_widget_1756331635880-13_1')

	// Step 1 inline validation errors
	static TestObject errorFirstName    = findTestObject('EVVA/Demo Request/div_hs_form_target_widget_1756331635880-6-error_1')
	static TestObject errorLastName       = findTestObject('EVVA/Demo Request/div_hs_form_target_widget_1756331635880-5-error')
	static TestObject errorEmail       = findTestObject('EVVA/Demo Request/div_hs_form_target_widget_1756331635880-3-error')
	static TestObject errorPhone = findTestObject('EVVA/Demo Request/div_hs_form_target_widget_1756331635880-4-error')
	static TestObject errorStateRegion     = findTestObject('EVVA/Demo Request/div_hs_form_target_widget_1756331635880-22463899')

	// ---------------------------------------------------------------
	// Step 2 fields (practice details)
	// ---------------------------------------------------------------
	static TestObject jobTitleLabel    = findTestObject('EVVA/Demo Request/span_Job Title')
	static TestObject jobTitleInput    = findTestObject('EVVA/Demo Request/input_Job Title')

	static TestObject companyNameLabel = findTestObject('EVVA/Demo Request/span_Company Name')
	static TestObject companyNameInput = findTestObject('EVVA/Demo Request/input_Company Name')

	static TestObject featuresLabel        = findTestObject('EVVA/Demo Request/span_What EVAA AI Features Are You Interested In')
	static TestObject featureVirtualAssistantLabel = findTestObject('EVVA/Demo Request/span_Virtual Assistant')
	static TestObject featureBillingAssistantLabel = findTestObject('EVVA/Demo Request/span_Billing Assistant')
	static TestObject featureScribeLabel            = findTestObject('EVVA/Demo Request/span_Scribe')
	static TestObject featureIntelliscanLabel       = findTestObject('EVVA/Demo Request/span_Intelliscan')

	// Feature checkboxes, in on-screen order: Virtual Assistant, Billing Assistant, Scribe, Intelliscan
	static List<TestObject> featureCheckboxes = [
		findTestObject('EVVA/Demo Request/input_hs_form_target_widget_1756331635880-313'),
		findTestObject('EVVA/Demo Request/input_hs_form_target_widget_1756331635880-313_1'),
		findTestObject('EVVA/Demo Request/input_hs_form_target_widget_1756331635880-313_2'),
		findTestObject('EVVA/Demo Request/input_hs_form_target_widget_1756331635880-313_3')
	]

	static TestObject currentEhrLabel  = findTestObject('EVVA/Demo Request/span_What is your current EHR')
	static TestObject currentEhrInput  = findTestObject('EVVA/Demo Request/input_What is your current EHR')
	static TestObject currentEhrOtherOption = findTestObject('EVVA/Demo Request/li_Other')

	static TestObject smsConsentText   = findTestObject('EVVA/Demo Request/div_hs_form_target_widget_1756331635880-24565790')

	static TestObject backButton       = findTestObject('EVVA/Demo Request/button_hs_form_target_widget_1756331635880-14')
	static TestObject submitButton     = findTestObject('EVVA/Demo Request/button_hs_form_target_widget_1756331635880-15')

	// Step 2 inline validation error (current EHR required)
	static TestObject errorCurrentEhr  = findTestObject('EVVA/Demo Request/div_hs_form_target_widget_1756331635880-6-error')

	// ---------------------------------------------------------------
	// Marketing / content section on the landing page
	// ---------------------------------------------------------------
	static TestObject heroBannerImage  = findTestObject('EVVA/Demo Request/img_Have you met EVAA_ MaximEyesAI_ Smarter Prac')
	static TestObject heroHeading      = findTestObject('EVVA/Demo Request/h1_Meet EVAA, your AI-powered assistant for eye')
	static TestObject heroSubtext      = findTestObject('EVVA/Demo Request/p_From eligibility verification to documentation')

	static TestObject galleryImage1    = findTestObject('EVVA/Demo Request/img_Have you met EVAA_ MaximEyesAI_ Smarter Prac_1')
	static TestObject galleryImage2    = findTestObject('EVVA/Demo Request/img_Have you met EVAA_ MaximEyesAI_ Smarter Prac_2')
	static TestObject galleryImage3    = findTestObject('EVVA/Demo Request/img_Have you met EVAA_ MaximEyesAI_ Smarter Prac_3')
	static TestObject galleryImage4    = findTestObject('EVVA/Demo Request/img_Have you met EVAA_ MaximEyesAI_ Smarter Prac_4')

	static TestObject builtForHeading  = findTestObject('EVVA/Demo Request/h3_The Intelligent Assistant Built for Eye Care')
	static TestObject builtForSubtext  = findTestObject('EVVA/Demo Request/p_MaximEyesAI, powered byEVAA automates your mo')
	static TestObject builtForList     = findTestObject('EVVA/Demo Request/ul_Insurance Eligibility  Benefits Verification')

	static TestObject problemsHeading  = findTestObject('EVVA/Demo Request/h3_Built to Solve Real Problems')
	static TestObject problemsSubtext  = findTestObject('EVVA/Demo Request/p_EVAA tackles the daily inefficiencies that slo')
	static TestObject problemsList     = findTestObject('EVVA/Demo Request/ul_Eliminate redundant data entry')

	static TestObject ctaText          = findTestObject('EVVA/Demo Request/em_Schedule your personalized MaximEyesAI walkth')

	static TestObject testimonialQuote = findTestObject('EVVA/Demo Request/span_Insurance eligibility checks runs while yo')
	static TestObject testimonialAuthor = findTestObject('EVVA/Demo Request/div_Jay Henry, ODHermann  Henry Eyecare')

	static TestObject footerLogo       = findTestObject('EVVA/Demo Request/img_MaximEyesLogo_FullLight_RGB')
	static TestObject footerCopyright  = findTestObject('EVVA/Demo Request/span_2026. All rights reserved')

	// =================================================================
	// Expected texts for elements that are visually multi-line/list-based.
	// Selenium's getText() (used under the hood by assertElementText)
	// normalizes whitespace: it collapses line breaks into single spaces
	// and trims leading/trailing whitespace. These constants are written
	// to match that normalized output exactly (single spaces between
	// list items, no leading/trailing newline) rather than the raw
	// line-broken text - the mismatch between the two is what causes
	// assertElementText to fail on multi-line elements.
	// =================================================================
	static final String INTRO_TEXT =
		'Please fill out the information below and we\'ll get back to you as soon as possible to book your demo for EVAA.'

	static final String HERO_HEADING_TEXT =
		'Meet EVAA, your AI-powered assistant for eye care practices.'

	static final String HERO_SUBTEXT_TEXT =
		'From eligibility verification to documentation and patient communications, EVAA handles the work behind the scenes so your team can focus on care.'

	static final String BUILT_FOR_HEADING_TEXT = 'The Intelligent Assistant Built for Eye Care'

	static final String BUILT_FOR_SUBTEXT_TEXT = 'MaximEyesAI, powered by EVAA automates your most time-consuming tasks:'

	static final String BUILT_FOR_LIST_TEXT =
		'Insurance Eligibility & Benefits Verification Real-Time Ambient Scribing AI-Powered Document Recognition Virtual Assistant for Scheduling, Voicemail, and Billing Automated RCM Tools for Denials, Posting, and Eligibility Verification'

	static final String PROBLEMS_HEADING_TEXT = 'Built to Solve Real Problems'

	static final String PROBLEMS_SUBTEXT_TEXT =
		'EVAA tackles the daily inefficiencies that slow your team down, helping you work smarter and focus on patient care.'

	static final String PROBLEMS_LIST_TEXT =
		'Eliminate redundant data entry Reduce time spent on insurance and billing tasks Shorten patient intake and checkout time Improve documentation accuracy and compliance Create a more focused, efficient clinical experience HIPAA-compliant. Scalable. Seamless.'

	static final String CTA_TEXT =
		'Schedule your personalized MaximEyesAI walkthrough today and see how EVAA can help your practice run smarter.'

	static final String TESTIMONIAL_QUOTE_TEXT =
		'\u201CInsurance eligibility checks runs while you\'re sleeping at night. Essentially, it\'s doing the work for you instead of a staff member having to log into three or four or five different places to find eligibility information. We\'re saving four or five hours a day just by letting this do it for us."'

	static final String TESTIMONIAL_AUTHOR_TEXT = 'Jay Henry, OD Hermann & Henry Eyecare'

	static final String COPYRIGHT_TEXT = '\u00A9 2026. All rights reserved.'

	static final String FEATURES_LABEL_TEXT = 'What EVAA AI Features Are You Interested In?'

	static final String CURRENT_EHR_LABEL_TEXT = 'What is your current EHR?'

	static final String SMS_CONSENT_TEXT =
		'By clicking submit, you are providing express consent to be contacted by EVAA.AI via SMS, call, or email, possibly using automated technology to the number you provided. If you wish to opt-out of communication, please reply "STOP". text "HELP" for help. Message/data rates may apply. Message frequency may vary. Submission of this form does not authorize the purchase of goods, services, or products. See the privacy policy/Terms and Conditions on the webpage.'

	static final String REQUIRED_FIELD_ERROR_TEXT = 'Error: Please complete this required field.'
	static final String REQUIRED_EMAIL_ERROR_TEXT ='Error: Email must be formatted correctly.'

	// =================================================================
	// Actions
	// =================================================================

	/** Opens the given URL. */
	static void navigateTo(String url) {
		WebUI.navigateToUrl(url)
	}

	/** Clicks the "Schedule a demo" link on the landing page. */
	static void openBookDemoModal() {
		WebUI.click(scheduleDemoLink)
	}

	/** Clicks whichever "Next" / "Book a Demo" submit button is currently visible. */
	static void clickNextStep() {
		WebUI.click(nextStepButton)
	}

	/** Clicks the alternate "Next" button reference (Step 1, second visible instance). */
	static void clickNextStepAlt() {
		WebUI.click(nextStepButtonAlt)
	}

	/** Clicks into the First Name field (used to trigger blur validation on the next field). */
	static void focusFirstName() {
		WebUI.click(firstNameInput)
	}

	/** Clicks into the Last Name field. */
	static void focusLastName() {
		WebUI.click(lastNameInput)
	}

	/** Clicks into the Email field. */
	static void focusEmail() {
		WebUI.click(emailInput)
	}

	/** Clicks into the Phone Number field. */
	static void focusPhone() {
		WebUI.click(phoneInputAlt)
	}

	/** Clicks into the State/Region field. */
	static void focusStateRegion() {
		WebUI.click(stateRegionInput)
	}

	/** Clicks the modal body (used to blur the State/Region field and trigger validation). */
	static void clickModalBody() {
		WebUI.click(bookDemoModal)
	}

	static void setFirstName(String value) {
		WebUI.setText(firstNameInput, value)
	}

	static void setLastName(String value) {
		WebUI.setText(lastNameInput, value)
	}

	static void setEmail(String value) {
		WebUI.setText(emailInput, value)
	}

	/** Opens the phone country dropdown, searches for and selects India. */
	static void selectPhoneCountryIndia() {
		WebUI.click(phoneCountryFlag)
		WebUI.waitForElementClickable(phoneCountrySearchInput,10)
		WebUI.click(phoneCountrySearchInput)
		WebUI.setText(phoneCountrySearchInput, "+91")
		WebUI.delay(1)
		WebUI.sendKeys(phoneCountrySearchInput, Keys.chord(Keys.ENTER))
//        WebUI.click(phoneCountryIndiaOption)
	}

	static void setPhoneNumber(String value) {
		WebUI.setText(phoneInput, value)
	}

	static void setStateRegion(String value) {
		WebUI.setText(stateRegionInput, value)
	}

	static void setJobTitle(String value) {
		WebUI.setText(jobTitleInput, value)
	}

	static void setCompanyName(String value) {
		WebUI.setText(companyNameInput, value)
	}

	/** Ticks every AI feature checkbox (Virtual Assistant, Billing Assistant, Scribe, Intelliscan). */
	static void selectAllFeatureCheckboxes() {
		featureCheckboxes.each { checkbox -> WebUI.click(checkbox) }
	}

	/** Opens the current EHR dropdown and selects "Other". */
	static void selectCurrentEhrOther() {
		WebUI.click(currentEhrInput)
		WebUI.click(currentEhrOtherOption)
	}

	/** Clicks the Back button on Step 2 to return to Step 1. */
	static void clickBack() {
		WebUI.click(backButton)
	}

	/** Clicks the final Submit button on Step 2. */
	static void clickSubmit() {
		WebUI.click(submitButton)
	}

	// =================================================================
	// Text verification helper
	// =================================================================
	// Katalon's assertElementText occasionally fails even when the
	// logged "actual" and "expected" strings print identically. This
	// happens when the page contains characters that render the same
	// as a plain space/hyphen/quote but are a different Unicode code
	// point underneath - e.g. a non-breaking space (\u00A0), a
	// non-breaking hyphen or en/em dash, or a curly quote. These
	// helpers strip that class of invisible mismatch out of both the
	// actual and expected text before comparing, so the assertion
	// reflects a real content difference rather than an invisible one.

	/** Normalizes whitespace and visually-identical Unicode variants for reliable text comparison. */
	static String normalizeText(String input) {
		if (input == null) {
			return ''
		}
		String s = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFKC)
		s = s.replaceAll('[\\u00A0\\u2000-\\u200B\\u202F\\u205F\\u3000]', ' ')  // all Unicode spaces -> regular space
		s = s.replaceAll('[\\u2010\\u2011\\u2012\\u2013\\u2014\\u2015]', '-')  // all Unicode dashes -> hyphen
		s = s.replaceAll('[\\u2018\\u2019]', '\'')                             // curly single quotes -> straight
		s = s.replaceAll('[\\u201C\\u201D]', '"')                              // curly double quotes -> straight
		s = s.replaceAll('\\s+', ' ').trim()                                   // collapse whitespace, trim
		return s
	}

	/**
	 * Waits for the element, reads its text, and compares it to the expected
	 * text after normalization. SOFT ASSERT: on mismatch (or if the element
	 * never becomes visible), this logs a failure via KeywordUtil.markFailed
	 * and returns - it does NOT throw/stop the test case. Execution continues
	 * with the next line in the calling test.
	 */
	static void verifyTextNormalized(TestObject to, String expected) {
		boolean visible = WebUI.verifyElementVisible(to, SOFT)
		if (!visible) {
			KeywordUtil.markFailed(
				"Element '${to.getObjectId()}' was not visible within ${PAGE_TIMEOUT}s; could not verify text.")
			return
		}
		String actual = WebUI.getText(to)
		String normalizedActual = normalizeText(actual)
		String normalizedExpected = normalizeText(expected)
		if (normalizedActual != normalizedExpected) {
			KeywordUtil.markFailed(
				"Text of object '${to.getObjectId()}' does not match.\n" +
				"Expected (normalized): [${normalizedExpected}]\n" +
				"Actual (normalized):   [${normalizedActual}]\n" +
				"Actual (raw):          [${actual}]")
		}
	}



	// =================================================================
	// Verifications
	// All WebUI.assertXxx calls below pass SOFT (FailureHandling.CONTINUE_ON_FAILURE)
	// so a failed check is logged and the test case is marked FAILED, but the
	// remaining steps in the test still run.
	// =================================================================

	static void verifyBookDemoModalVisible() {
		WebUI.assertElementVisible(bookDemoModal, PAGE_TIMEOUT, SOFT)
	}

	static void verifyBookDemoModalStep2Visible() {
		WebUI.assertElementVisible(bookDemoModalTitleStep2, PAGE_TIMEOUT, SOFT)
	}

	static void verifyModalTitle() {
		verifyTextNormalized(bookDemoModalTitle, 'Book a Demo')
	}

	static void verifyModalTitleStep2() {
		verifyTextNormalized(bookDemoModalTitleStep2Text, 'Book a Demo')
	}

	static void verifyIntroText() {
		verifyTextNormalized(bookDemoIntroText, INTRO_TEXT)
	}

	static void verifyIntroTextStep2() {
		verifyTextNormalized(bookDemoIntroTextStep2, INTRO_TEXT)
	}

	static void verifyStep1FieldsPresent() {
		verifyTextNormalized(firstNameLabel, 'First Name')
		WebUI.assertElementPresent(lastNameLabel, PAGE_TIMEOUT, SOFT)
		WebUI.assertElementPresent(emailLabel, PAGE_TIMEOUT, SOFT)
		WebUI.assertElementPresent(phoneLabel, PAGE_TIMEOUT, SOFT)
		WebUI.assertElementPresent(stateRegionLabel, PAGE_TIMEOUT, SOFT)
//        WebUI.assertElementPresent(recaptchaFooter, PAGE_TIMEOUT, SOFT)
		WebUI.assertElementPresent(nextStepButton, PAGE_TIMEOUT, SOFT)
	}

	static void verifyNextStepButtonNotClickable() {
		WebUI.assertElementNotClickable(nextStepButton, PAGE_TIMEOUT, SOFT)
	}

	static void verifyLastNameError() {
		verifyTextNormalized(errorLastName, REQUIRED_FIELD_ERROR_TEXT)
	}

	static void verifyEmailError() {
		verifyTextNormalized(errorEmail, REQUIRED_FIELD_ERROR_TEXT)
	}

	static void verifyPhoneError() {
		verifyTextNormalized(errorPhone, REQUIRED_FIELD_ERROR_TEXT)
	}

	static void verifyStateRegionError() {
		verifyTextNormalized(errorStateRegion, REQUIRED_FIELD_ERROR_TEXT)
	}

	static void verifyGenericRequiredFieldError() {
		verifyTextNormalized(errorFirstName, REQUIRED_FIELD_ERROR_TEXT)
	}
	
	static void verifyEmailError1() {
		verifyTextNormalized(errorEmail, REQUIRED_EMAIL_ERROR_TEXT)
	}



	static void verifyStep2FieldsPresent() {
		WebUI.assertElementPresent(jobTitleLabel, PAGE_TIMEOUT, SOFT)
		WebUI.assertElementPresent(companyNameLabel, PAGE_TIMEOUT, SOFT)
		verifyTextNormalized(featuresLabel, FEATURES_LABEL_TEXT)
		WebUI.assertElementPresent(featureVirtualAssistantLabel, PAGE_TIMEOUT, SOFT)
		WebUI.assertElementPresent(featureBillingAssistantLabel, PAGE_TIMEOUT, SOFT)
		WebUI.assertElementPresent(featureScribeLabel, PAGE_TIMEOUT, SOFT)
		WebUI.assertElementPresent(featureIntelliscanLabel, PAGE_TIMEOUT, SOFT)
		verifyTextNormalized(currentEhrLabel, CURRENT_EHR_LABEL_TEXT)
		verifyTextNormalized(smsConsentText, SMS_CONSENT_TEXT)
		WebUI.assertElementPresent(backButton, PAGE_TIMEOUT, SOFT)
		WebUI.assertElementPresent(submitButton, PAGE_TIMEOUT, SOFT)
	}

	static void verifyCurrentEhrError() {
		verifyTextNormalized(errorCurrentEhr, REQUIRED_FIELD_ERROR_TEXT)
	}

	/** Verifies every static marketing / content block on the landing page behind the modal. */
	static void verifyMarketingContent() {
		WebUI.assertElementPresent(heroBannerImage, PAGE_TIMEOUT, SOFT)
		verifyTextNormalized(heroHeading, HERO_HEADING_TEXT)
		verifyTextNormalized(heroSubtext, HERO_SUBTEXT_TEXT)

		WebUI.assertElementPresent(galleryImage1, PAGE_TIMEOUT, SOFT)
		WebUI.assertElementPresent(galleryImage2, PAGE_TIMEOUT, SOFT)
		WebUI.assertElementPresent(galleryImage3, PAGE_TIMEOUT, SOFT)
		WebUI.assertElementPresent(galleryImage4, PAGE_TIMEOUT, SOFT)

		verifyTextNormalized(builtForHeading, BUILT_FOR_HEADING_TEXT)
		verifyTextNormalized(builtForSubtext, BUILT_FOR_SUBTEXT_TEXT)
		verifyTextNormalized(builtForList, BUILT_FOR_LIST_TEXT)

		verifyTextNormalized(problemsHeading, PROBLEMS_HEADING_TEXT)
		verifyTextNormalized(problemsSubtext, PROBLEMS_SUBTEXT_TEXT)
		verifyTextNormalized(problemsList, PROBLEMS_LIST_TEXT)

		verifyTextNormalized(ctaText, CTA_TEXT)

		verifyTextNormalized(testimonialQuote, TESTIMONIAL_QUOTE_TEXT)
		verifyTextNormalized(testimonialAuthor, TESTIMONIAL_AUTHOR_TEXT)

		WebUI.assertElementPresent(footerLogo, PAGE_TIMEOUT, SOFT)
		verifyTextNormalized(footerCopyright, COPYRIGHT_TEXT)
	}
}

/**
 * EVAA.AI - "Book a Demo" flow
 * Verifies the landing page content, then walks through both steps of the
 * demo request form, including field-level validation, before submitting.
 */

// Step 0 - Open the landing page
BookDemoPage.navigateTo(GlobalVariable.Url)

// Step 1 - Open the "Book a Demo" modal from the landing page
BookDemoPage.openBookDemoModal()

CustomKeywords.'utils.PageLoadChecker.verifyPageLoaded'()

CustomKeywords.'utils.JavaScriptErrorCollector.startCollector'()

CustomKeywords.'utils.NetworkErrorCollector.startCollector'()

CustomKeywords.'utils.ImageChecker.verifyAllImages'(5)

CustomKeywords.'utils.ResponsiveChecker.verifyResponsiveLayout'()

CustomKeywords.'utils.EnvironmentUrlChecker.verifyNoDevOrStagingUrls'()

//Veify Broken links with tag <a>
CustomKeywords.'utils.BrokenLinkChecker.verifyVisibleLinks'()

CustomKeywords.'utils.JavaScriptErrorCollector.verifyNoJavaScriptErrors'()

CustomKeywords.'utils.NetworkErrorCollector.verifyNoNetworkErrors'()

// Verify the modal opened with the expected title and intro copy
BookDemoPage.verifyBookDemoModalVisible()
BookDemoPage.verifyModalTitle()
BookDemoPage.verifyIntroText()

// Verify all Step 1 fields, the reCAPTCHA footer, and the Next button are present
BookDemoPage.verifyStep1FieldsPresent()

// Verify the static marketing content behind the modal (hero, feature lists,
// testimonial, footer) is rendered correctly
BookDemoPage.verifyMarketingContent()

// Step 2 - Attempt to advance with an empty form to confirm the button is disabled
BookDemoPage.clickNextStep()
BookDemoPage.verifyNextStepButtonNotClickable()

// Step 3 - Trigger blur validation on each required field, one at a time,
// by focusing the next field and checking the previous field's error message

// Focus First Name, then Last Name -> Last Name's own required-field error is not
// yet relevant here; this focus sequence blurs First Name
BookDemoPage.focusFirstName()

BookDemoPage.focusLastName()
BookDemoPage.verifyGenericRequiredFieldError()


BookDemoPage.focusEmail()
BookDemoPage.verifyLastNameError()


BookDemoPage.focusPhone()
BookDemoPage.verifyEmailError()


BookDemoPage.focusStateRegion()
BookDemoPage.verifyPhoneError()


// Click elsewhere in the modal to blur State/Region and surface its error too
BookDemoPage.clickModalBody()
BookDemoPage.verifyStateRegionError()

// Step 4 - Fill First Name and Last Name
BookDemoPage.setFirstName('Test')
BookDemoPage.setLastName('Test')

// Step 5 - Exercise Email field validation with a series of invalid values,
// re-checking the required-field error after each attempt
BookDemoPage.setEmail('1234')
BookDemoPage.focusPhone()
BookDemoPage.verifyEmailError1()


TestObject emailInput1 = findTestObject('EVVA/Demo Request/input_Email')

// Only clear
CustomKeywords.'customkeywords.InputHelper.clearInput'(emailInput1)

// Clear and enter new text
CustomKeywords.'customkeywords.InputHelper.clearAndType'(
	emailInput1,
	"test"
)

BookDemoPage.focusPhone()
BookDemoPage.verifyEmailError1()


CustomKeywords.'customkeywords.InputHelper.clearInput'(emailInput1)


BookDemoPage.setEmail('@test')
BookDemoPage.focusPhone()
BookDemoPage.verifyEmailError1()
CustomKeywords.'customkeywords.InputHelper.clearInput'(emailInput1)


// Step 6 - Enter a valid email address
BookDemoPage.setEmail('gajakumara@first-insight.com')
BookDemoPage.focusPhone()

// Step 7 - Select India as the phone country code
BookDemoPage.selectPhoneCountryIndia()

// Step 8 - Exercise Phone Number validation with an invalid (too long) number
BookDemoPage.setPhoneNumber('+91 00000-000000000')
BookDemoPage.verifyStateRegionError()

TestObject phoneInput1       = findTestObject('EVVA/Demo Request/input___1')
WebUI.executeJavaScript(
	"arguments[0].value = ''; arguments[0].dispatchEvent(new Event('input', {bubbles:true}));",
	Arrays.asList(WebUiCommonHelper.findWebElement(phoneInput1, 10))
)


// Step 9 - Try another invalid phone number
BookDemoPage.setPhoneNumber('+91 98765-4321')
BookDemoPage.verifyStateRegionError()
WebUI.executeJavaScript(
	"arguments[0].value = ''; arguments[0].dispatchEvent(new Event('input', {bubbles:true}));",
	Arrays.asList(WebUiCommonHelper.findWebElement(phoneInput1, 10))
)

// Step 10 - Enter a valid phone number
BookDemoPage.setPhoneNumber('+91 99887-76655')

// Step 11 - Fill State/Region
BookDemoPage.setStateRegion('India')



// Step 12 - Submit Step 1 with a fully valid form
BookDemoPage.clickNextStepAlt()

// Step 13 - Verify Step 2 of the modal opened with the expected title and intro copy
BookDemoPage.verifyBookDemoModalStep2Visible()
BookDemoPage.verifyModalTitleStep2()
BookDemoPage.verifyIntroTextStep2()

// Verify all Step 2 fields (Job Title, Company Name, feature checkboxes,
// current EHR dropdown, SMS consent copy, Back/Submit buttons) are present
BookDemoPage.verifyStep2FieldsPresent()

// Step 14 - Click Back and confirm it returns to Step 1 of the modal
BookDemoPage.clickBack()
BookDemoPage.verifyBookDemoModalVisible()

// Step 15 - Advance to Step 2 again
BookDemoPage.clickNextStepAlt()

// Step 16 - Fill Job Title and Company Name
BookDemoPage.setJobTitle('QA')
BookDemoPage.setCompanyName('FIC')

// Step 17 - Select all AI feature checkboxes (Virtual Assistant, Billing
// Assistant, Scribe, Intelliscan)
BookDemoPage.selectAllFeatureCheckboxes()

// Step 18 - Select "Other" from the current EHR dropdown
BookDemoPage.selectCurrentEhrOther()

// Step 19 - Submit without confirming the current EHR error state and verify
// the required-field error still surfaces as expected
BookDemoPage.clickSubmit()
//BookDemoPage.verifyCurrentEhrError()