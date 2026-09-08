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
import customkeywords.ScrollVerification
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable



class EVAAHomePage {
	
	   // ---------------------------------------------------------------
	   // Hero / navigation
	   // ---------------------------------------------------------------
	
	   static TestObject meetYourWorkforceLink() {
		   return findTestObject('EVVA/Home page redesign EVAA.AI/a_Meet your workforce')
	   }
	
	   static TestObject watchEvaaInActionLink() {
		   return findTestObject('EVVA/Home page redesign EVAA.AI/a_Watch EVAA in action')
	   }
	
	   static TestObject youtubeCuedOverlayHost() {
		   return findTestObject('EVVA/Home page redesign EVAA.AI/cued-overlay_ytmCuedOverlayHost')
	   }
	
	   /** Generic unnamed button shown after the YouTube overlay appears (e.g. play button). */
	   static TestObject genericButton() {
		   return findTestObject('EVVA/Home page redesign EVAA.AI/button_')
	   }
	
	   // ---------------------------------------------------------------
	   // "Meet EVAA ..." role cards (first pass - opens detail panel)
	   // ---------------------------------------------------------------
	
	   static TestObject receptionMeetEvaaVoiceButton() {
		   return findTestObject('EVVA/Home page redesign EVAA.AI/button_Reception  meet EVAA Voice')
	   }
	
	   static TestObject examRoomsMeetEvaaScribeButton() {
		   return findTestObject('EVVA/Home page redesign EVAA.AI/button_Exam Rooms  meet EVAA Scribe')
	   }
	
	   static TestObject opticalMeetEvaaAssistantButton() {
		   return findTestObject('EVVA/Home page redesign EVAA.AI/button_Optical  meet EVAA Assistant')
	   }
	
	   static TestObject billingMeetEvaaBillingAssistantButton() {
		   return findTestObject('EVVA/Home page redesign EVAA.AI/button_Billing  meet EVAA Billing Assistant')
	   }
	
	   static TestObject patientCommunicationMeetEvaaVirtualButton() {
		   return findTestObject('EVVA/Home page redesign EVAA.AI/button_Patient Communication  meet EVAA Virtual')
	   }
	
	   static TestObject practiceManagementMeetEvaaFastPayButton() {
		   return findTestObject('EVVA/Home page redesign EVAA.AI/button_Practice Management  meet EVAA Fast Pay')
	   }
	
	   /** Shared detail panel/div that becomes visible after opening any role card above. */
	   static TestObject detailPanel() {
		   return findTestObject('EVVA/Home page redesign EVAA.AI/div_')
	   }
	
	   /** Close button used to dismiss whichever detail panel/modal is currently open. */
	   static TestObject closeButton() {
		   return findTestObject('EVVA/Home page redesign EVAA.AI/button_Close')
	   }
	
	   // ---------------------------------------------------------------
	   // Role tabs (second pass - short labels, e.g. a tab strip)
	   // ---------------------------------------------------------------
	
	   static TestObject receptionTab() {
		   return findTestObject('EVVA/Home page redesign EVAA.AI/button_Reception')
	   }
	
	   static TestObject examRoomsTab() {
		   return findTestObject('EVVA/Home page redesign EVAA.AI/button_Exam Rooms')
	   }
	
	   static TestObject opticalTab() {
		   return findTestObject('EVVA/Home page redesign EVAA.AI/button_Optical')
	   }
	
	   static TestObject billingTab() {
		   return findTestObject('EVVA/Home page redesign EVAA.AI/button_Billing')
	   }
	
	   static TestObject patientCommsTab() {
		   return findTestObject('EVVA/Home page redesign EVAA.AI/button_Patient Comms')
	   }
	
	   static TestObject managementTab() {
		   return findTestObject('EVVA/Home page redesign EVAA.AI/button_Management')
	   }
	
	   // ---------------------------------------------------------------
	   // Benefit headline cards ("Never miss another patient call", etc.)
	   // ---------------------------------------------------------------
	
	   static TestObject neverMissPatientCallHeadline() {
		   return findTestObject('EVVA/Home page redesign EVAA.AI/h3_Never miss another patient call')
	   }
	
	   static TestObject reduceDocumentationTimeHeadline() {
		   return findTestObject('EVVA/Home page redesign EVAA.AI/h3_Reduce documentation time')
	   }
	
	   static TestObject improvePatientCommunicationHeadline() {
		   return findTestObject('EVVA/Home page redesign EVAA.AI/h3_Improve patient communication')
	   }
	
	   static TestObject accelerateRevenueCycleHeadline() {
		   return findTestObject('EVVA/Home page redesign EVAA.AI/h3_Accelerate revenue cycle')
	   }
	
	   static TestObject reduceAdministrativeWorkHeadline() {
		   return findTestObject('EVVA/Home page redesign EVAA.AI/h3_Reduce administrative work')
	   }
	
	   static TestObject growWithoutHiringHeadline() {
		   return findTestObject('EVVA/Home page redesign EVAA.AI/h3_Grow without hiring')
	   }
	
	   // ---------------------------------------------------------------
	   // Bottom-of-page calls to action
	   // ---------------------------------------------------------------
	
	   static TestObject meetYourAiWorkforceLink() {
		   return findTestObject('EVVA/Home page redesign EVAA.AI/a_Meet your AI workforce')
	   }
	
	   static TestObject bookADemoLink() {
		   return findTestObject('EVVA/Home page redesign EVAA.AI/a_Book a demo')
	   }
	
	   static TestObject scheduleADemoLink() {
		   return findTestObject('EVVA/Home page redesign EVAA.AI/a_Schedule a demo')
	   }
	
	   // ---------------------------------------------------------------
	   // MaximEyesAI EVAA Demo Request (external window opened from the CTAs)
	   // ---------------------------------------------------------------
	
	   static final String DEMO_REQUEST_WINDOW_TITLE = 'MaximEyesAI: EVAA Demo Request'
	   static final String HOME_PAGE_WINDOW_TITLE = 'Home page redesign | EVAA.AI'
	
	   static TestObject demoRequestPageMarker() {
		   return findTestObject('EVVA/MaximEyesAI EVAA Demo Request/div_window.document.head.appendChild(document.cr')
	   }
   }
int PAGE_TIMEOUT = 10
 
ScrollVerification scrollHelper = new ScrollVerification()
 
// 1. Open the application under test
WebUI.navigateToUrl(GlobalVariable.Url)
 
// 2. Wait for the home page to fully load before interacting with it
WebUI.waitForPageLoad(PAGE_TIMEOUT)
 
// 3. Click the "Meet your workforce" nav link
WebUI.click(EVAAHomePage.meetYourWorkforceLink())
 
// 4. Verify the page scrolled to the #workforce section as expected
scrollHelper.verifyAndAssertScrollToSection('#workforce', '#workforce')
 
// 5. Click "Watch EVAA in action" to trigger the embedded video
WebUI.click(EVAAHomePage.watchEvaaInActionLink())
 
// 6. Confirm the YouTube "cued" overlay (thumbnail/play state) is present
WebUI.assertElementPresent(EVAAHomePage.youtubeCuedOverlayHost(), PAGE_TIMEOUT)
 
// 7. Click the video's play button to start playback
WebUI.click(EVAAHomePage.genericButton())
 
// 8. Open the "Reception - meet EVAA Voice" role card
WebUI.click(EVAAHomePage.receptionMeetEvaaVoiceButton())
 
// 9. Verify the role detail panel is now visible
WebUI.assertElementVisible(EVAAHomePage.detailPanel(), PAGE_TIMEOUT)
 
// 10. Check that all visible links on the page are not broken
CustomKeywords.'utils.BrokenLinkChecker.verifyVisibleLinks'()
 
// 11. Click "Book a demo" to open the demo request flow
WebUI.click(EVAAHomePage.bookADemoLink())
 
// 12. Switch focus to the newly opened MaximEyesAI demo request window
WebUI.switchToWindowTitle(EVAAHomePage.DEMO_REQUEST_WINDOW_TITLE)
 
// 13. Verify the demo request page loaded correctly
WebUI.assertElementVisible(EVAAHomePage.demoRequestPageMarker(), PAGE_TIMEOUT)
 
// 14. Check that all visible links on the demo request page are not broken
CustomKeywords.'utils.BrokenLinkChecker.verifyVisibleLinks'()
 
// 15. Switch focus back to the home page window
WebUI.switchToWindowTitle(EVAAHomePage.HOME_PAGE_WINDOW_TITLE)
 
// 16. Close the role detail panel
WebUI.click(EVAAHomePage.closeButton())
 
// 17. Open the "Exam Rooms - meet EVAA Scribe" role card
WebUI.click(EVAAHomePage.examRoomsMeetEvaaScribeButton())
 
// 18. Verify its detail panel is visible
WebUI.assertElementVisible(EVAAHomePage.detailPanel(), PAGE_TIMEOUT)
 
// 19. Check that all visible links are not broken
CustomKeywords.'utils.BrokenLinkChecker.verifyVisibleLinks'()
 
// 20. Close the detail panel
WebUI.click(EVAAHomePage.closeButton())
 
// 21. Open the "Optical - meet EVAA Assistant" role card
WebUI.click(EVAAHomePage.opticalMeetEvaaAssistantButton())
 
// 22. Verify its detail panel is visible
WebUI.assertElementVisible(EVAAHomePage.detailPanel(), PAGE_TIMEOUT)

// 10. Check that all visible links on the page are not broken
CustomKeywords.'utils.BrokenLinkChecker.verifyVisibleLinks'()
 
// 23. Close the detail panel
WebUI.click(EVAAHomePage.closeButton())
 
// 24. Open the "Billing - meet EVAA Billing Assistant" role card
WebUI.click(EVAAHomePage.billingMeetEvaaBillingAssistantButton())
 
// 25. Verify its detail panel is visible
WebUI.assertElementVisible(EVAAHomePage.detailPanel(), PAGE_TIMEOUT)

// 10. Check that all visible links on the page are not broken
CustomKeywords.'utils.BrokenLinkChecker.verifyVisibleLinks'()
 
// 26. Close the detail panel
WebUI.click(EVAAHomePage.closeButton())
 
// 27. Open the "Patient Communication - meet EVAA Virtual" role card
WebUI.click(EVAAHomePage.patientCommunicationMeetEvaaVirtualButton())
 
// 28. Verify its detail panel is visible
WebUI.assertElementVisible(EVAAHomePage.detailPanel(), PAGE_TIMEOUT)
 
// 10. Check that all visible links on the page are not broken
CustomKeywords.'utils.BrokenLinkChecker.verifyVisibleLinks'()

// 29. Close the detail panel
WebUI.click(EVAAHomePage.closeButton())
 
// 30. Open the "Practice Management - meet EVAA Fast Pay" role card
WebUI.click(EVAAHomePage.practiceManagementMeetEvaaFastPayButton())
 
// 31. Verify its detail panel is visible
WebUI.assertElementVisible(EVAAHomePage.detailPanel(), PAGE_TIMEOUT)
 
// 10. Check that all visible links on the page are not broken
CustomKeywords.'utils.BrokenLinkChecker.verifyVisibleLinks'()

// 32. Close the detail panel
WebUI.click(EVAAHomePage.closeButton())
 
// 33. Select the "Reception" tab
WebUI.click(EVAAHomePage.receptionTab())

// 9. Verify the role detail panel is now visible
WebUI.assertElementVisible(EVAAHomePage.detailPanel(), PAGE_TIMEOUT)
 
// 10. Check that all visible links on the page are not broken
CustomKeywords.'utils.BrokenLinkChecker.verifyVisibleLinks'()
 
// 34. Close whatever panel/modal it opened
WebUI.click(EVAAHomePage.closeButton())
 
// 35. Select the "Exam Rooms" tab
WebUI.click(EVAAHomePage.examRoomsTab())

// 25. Verify its detail panel is visible
WebUI.assertElementVisible(EVAAHomePage.detailPanel(), PAGE_TIMEOUT)

// 10. Check that all visible links on the page are not broken
CustomKeywords.'utils.BrokenLinkChecker.verifyVisibleLinks'()
 
// 36. Close the panel
WebUI.click(EVAAHomePage.closeButton())
 
// 37. Select the "Optical" tab
WebUI.click(EVAAHomePage.opticalTab())

// 25. Verify its detail panel is visible
WebUI.assertElementVisible(EVAAHomePage.detailPanel(), PAGE_TIMEOUT)

// 10. Check that all visible links on the page are not broken
CustomKeywords.'utils.BrokenLinkChecker.verifyVisibleLinks'()
 
// 38. Close the panel
WebUI.click(EVAAHomePage.closeButton())
 
// 39. Select the "Billing" tab
WebUI.click(EVAAHomePage.billingTab())

// 25. Verify its detail panel is visible
WebUI.assertElementVisible(EVAAHomePage.detailPanel(), PAGE_TIMEOUT)

// 10. Check that all visible links on the page are not broken
CustomKeywords.'utils.BrokenLinkChecker.verifyVisibleLinks'()
 
// 40. Close the panel
WebUI.click(EVAAHomePage.closeButton())
 
// 41. Select the "Patient Comms" tab
WebUI.click(EVAAHomePage.patientCommsTab())

// 25. Verify its detail panel is visible
WebUI.assertElementVisible(EVAAHomePage.detailPanel(), PAGE_TIMEOUT)

// 10. Check that all visible links on the page are not broken
CustomKeywords.'utils.BrokenLinkChecker.verifyVisibleLinks'()
 
// 42. Close the panel
WebUI.click(EVAAHomePage.closeButton())
 
// 43. Select the "Management" tab
WebUI.click(EVAAHomePage.managementTab())

// 25. Verify its detail panel is visible
WebUI.assertElementVisible(EVAAHomePage.detailPanel(), PAGE_TIMEOUT)

// 10. Check that all visible links on the page are not broken
CustomKeywords.'utils.BrokenLinkChecker.verifyVisibleLinks'()
 
// 44. Close the panel
WebUI.click(EVAAHomePage.closeButton())
 
// 45. Click the "Never miss another patient call" benefit headline
WebUI.click(EVAAHomePage.neverMissPatientCallHeadline())

// 25. Verify its detail panel is visible
WebUI.assertElementVisible(EVAAHomePage.detailPanel(), PAGE_TIMEOUT)

// 10. Check that all visible links on the page are not broken
CustomKeywords.'utils.BrokenLinkChecker.verifyVisibleLinks'()
 
// 46. Close the panel it opened
WebUI.click(EVAAHomePage.closeButton())
 
// 47. Click the "Reduce documentation time" benefit headline
WebUI.click(EVAAHomePage.reduceDocumentationTimeHeadline())

// 25. Verify its detail panel is visible
WebUI.assertElementVisible(EVAAHomePage.detailPanel(), PAGE_TIMEOUT)

// 10. Check that all visible links on the page are not broken
CustomKeywords.'utils.BrokenLinkChecker.verifyVisibleLinks'()
 
// 48. Close the panel
WebUI.click(EVAAHomePage.closeButton())
 
// 49. Click the "Improve patient communication" benefit headline
WebUI.click(EVAAHomePage.improvePatientCommunicationHeadline())

// 25. Verify its detail panel is visible
WebUI.assertElementVisible(EVAAHomePage.detailPanel(), PAGE_TIMEOUT)

// 10. Check that all visible links on the page are not broken
CustomKeywords.'utils.BrokenLinkChecker.verifyVisibleLinks'()
 
// 50. Close the panel
WebUI.click(EVAAHomePage.closeButton())
 
// 51. Click the "Accelerate revenue cycle" benefit headline
WebUI.click(EVAAHomePage.accelerateRevenueCycleHeadline())
 
// 25. Verify its detail panel is visible
WebUI.assertElementVisible(EVAAHomePage.detailPanel(), PAGE_TIMEOUT)

// 10. Check that all visible links on the page are not broken
CustomKeywords.'utils.BrokenLinkChecker.verifyVisibleLinks'()

// 52. Close the panel
WebUI.click(EVAAHomePage.closeButton())
 
// 53. Click the "Reduce administrative work" benefit headline
WebUI.click(EVAAHomePage.reduceAdministrativeWorkHeadline())
 
// 25. Verify its detail panel is visible
WebUI.assertElementVisible(EVAAHomePage.detailPanel(), PAGE_TIMEOUT)

// 10. Check that all visible links on the page are not broken
CustomKeywords.'utils.BrokenLinkChecker.verifyVisibleLinks'()

// 54. Close the panel
WebUI.click(EVAAHomePage.closeButton())
 
// 55. Click the "Grow without hiring" benefit headline
WebUI.click(EVAAHomePage.growWithoutHiringHeadline())
 
// 25. Verify its detail panel is visible
WebUI.assertElementVisible(EVAAHomePage.detailPanel(), PAGE_TIMEOUT)

// 10. Check that all visible links on the page are not broken
CustomKeywords.'utils.BrokenLinkChecker.verifyVisibleLinks'()

// 56. Close the panel
WebUI.click(EVAAHomePage.closeButton())
 
// 57. Click "Meet your AI workforce" bottom CTA
WebUI.click(EVAAHomePage.meetYourAiWorkforceLink())

scrollHelper.verifyAndAssertScrollToSection('#workforce', '#workforce')
 
// 58. Click "Schedule a demo" bottom CTA
WebUI.click(EVAAHomePage.scheduleADemoLink())
 
// 59. Verify the MaximEyesAI demo request page loaded correctly
WebUI.assertElementVisible(EVAAHomePage.demoRequestPageMarker(), PAGE_TIMEOUT)

// 10. Check that all visible links on the page are not broken
CustomKeywords.'utils.BrokenLinkChecker.verifyVisibleLinks'()