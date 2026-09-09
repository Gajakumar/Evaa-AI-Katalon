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
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webui.driver.DriverFactory
import org.openqa.selenium.WebDriver
import org.openqa.selenium.JavascriptExecutor
import com.kms.katalon.core.util.KeywordUtil
import org.openqa.selenium.By

//Navigate to url
WebUI.navigateToUrl(GlobalVariable.Url)


WebUI.waitForPageLoad(30)
CustomKeywords.'utils.JavaScriptErrorCollector.startCollector'()


//// ==========================================================
//// GET DRIVER
//// ==========================================================
//
//WebDriver driver =
//        DriverFactory.getWebDriver()
//
//JavascriptExecutor js =
//        (JavascriptExecutor) driver
//
//
//// ==========================================================
//// VERIFY PAGE LOADED
//// ==========================================================
//
//KeywordUtil.logInfo(
//        "Current URL: " +
//        driver.getCurrentUrl()
//)
//
//KeywordUtil.logInfo(
//        "Page Title: " +
//        driver.getTitle()
//)
//
//
//// ==========================================================
//// ADD INTENTIONALLY BROKEN LINK
//// ==========================================================
//
//js.executeScript("""
//    var link = document.createElement('a');
//
//    link.href =
//        'https://wpenginepowered.com/automation-broken-123456';
//
//    link.innerText =
//        'INTENTIONALLY BROKEN TEST LINK';
//
//    link.id =
//        'automation-broken-link';
//
//    link.style.display =
//        'block';
//
//    document.body.appendChild(link);
//""")
//
//
//// ==========================================================
//// VERIFY TEST LINK WAS ADDED
//// ==========================================================
//
//int testLinkCount =
//        driver.findElements(
//                By.id('automation-broken-link')
//        ).size()
//
//KeywordUtil.logInfo(
//        "Intentionally broken links found: " +
//        testLinkCount
//)


// ==========================================================
// RUN BROKEN LINK CHECKER
// ==========================================================

KeywordUtil.logInfo("BASELINE_STORAGE_URL = " + System.getenv("BASELINE_STORAGE_URL"))

CustomKeywords.'utils.NetworkErrorCollector.startCollector'()

// original behavior, just with proper waiting now:
CustomKeywords.'utils.PageLoadChecker.verifyPageLoaded'()

// stronger check for an SPA - also confirms real content rendered:
//CustomKeywords.'utils.PageLoadChecker.verifyPageLoaded'(10, '//*[@id="results"]/div/div[2]')

CustomKeywords.'utils.ImageChecker.verifyAllImages'(5)

CustomKeywords.'utils.ResponsiveChecker.verifyResponsiveLayout'()

// with a longer settle time and a known decorative exception:
CustomKeywords.'utils.ResponsiveChecker.verifyResponsiveLayout'(600, ['.marquee-ticker'])

CustomKeywords.'utils.EnvironmentUrlChecker.verifyNoDevOrStagingUrls'()

//Veify Broken links with tag <a>
CustomKeywords.'utils.BrokenLinkChecker.verifyVisibleLinks'()

CustomKeywords.'utils.JavaScriptErrorCollector.verifyNoJavaScriptErrors'() 

CustomKeywords.'utils.NetworkErrorCollector.verifyNoNetworkErrors'()