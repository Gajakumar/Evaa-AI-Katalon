import com.kms.katalon.core.annotation.AfterTestCase
import com.kms.katalon.core.annotation.AfterTestSuite
import com.kms.katalon.core.context.TestCaseContext
import com.kms.katalon.core.context.TestSuiteContext
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.model.FailureHandling

import javax.mail.*
import javax.mail.internet.*
import java.util.Properties

class SendExecutionSummaryEmail {

    static List failedTests = []
    static List screenshots = []

    // Runs after each Test Case
    @AfterTestCase
    def afterTestCase(TestCaseContext context) {

        println("Test Case Finished: " + context.getTestCaseId())
        println("Status: " + context.getTestCaseStatus())

        if(context.getTestCaseStatus() == "FAILED") {

            println("Failure detected")

            String testCaseName = context.getTestCaseId().tokenize('/').last()

            Map failure = extractFailureReason(context.getMessage())

            // Handle unexpected alerts before screenshot
            try {
                if(WebUI.waitForAlert(2, FailureHandling.OPTIONAL)) {
                    println("Alert detected: " + WebUI.getAlertText())
                    WebUI.acceptAlert()
                }
            } catch(Exception e){
                println("No alert present")
            }

            // Screenshot path
            String path = RunConfiguration.getProjectDir() +
                    "/Screenshots/FAILED_" +
                    testCaseName +
                    "_" +
                    System.currentTimeMillis() +
                    ".png"

            WebUI.takeScreenshot(path)

            println("Screenshot saved: " + path)

            failedTests.add([
                    name: testCaseName,
                    step: failure.step,
                    reason: failure.reason
            ])

            screenshots.add(path)
        }
    }

    // Runs after Test Suite
    @AfterTestSuite
    def afterSuite(TestSuiteContext context) {

        println("Total failures captured: " + failedTests.size())

        if(failedTests.size() == 0) {
            println("No failures detected")
            return
        }

        String body = buildEmailBody(context)

        sendMail(body)

        // Reset lists for next execution
        failedTests.clear()
        screenshots.clear()
    }

    // Extract failure reason
   Map extractFailureReason(String message){

    String step = "Unknown Step"
    String reason = "Test step failed"
    String element = ""

    if(message == null || message.trim().isEmpty()){
        return [step: step, reason: reason]
    }

    println("Failure Message Captured:")
    println(message)

    // Extract object name
    def objMatch = message =~ /Object Repository\/(.*?)'/

    if(objMatch.find()){
        element = objMatch.group(1).tokenize('/').last()
    }

    // CLICK failure
    if(message.toLowerCase().contains("click")){
        step = "Click Element"

        if(element)
            reason = "Unable to click element '${element}'. It may be hidden or overlapped."
        else
            reason = "Unable to click the target element."
    }

    // ELEMENT NOT FOUND
    else if(message.contains("WebElementNotFoundException")){
        step = "Locate Element"

        if(element)
            reason = "Element '${element}' was not found on the page."
        else
            reason = "Required page element was not found."
    }

    // VERIFY TEXT
    else if(message.contains("Actual text") && message.contains("expected text")){

        def txt = message =~ /Actual text '(.*?)' and expected text '(.*?)'/

        if(txt.find()){
            step = "Verify Text"
            reason = "Expected '${txt.group(2)}' but found '${txt.group(1)}'."
        }
    }

    // VERIFY ATTRIBUTE
    else if(message.contains("verifyElementAttributeValue")){
        step = "Verify Attribute"

        if(element)
            reason = "Attribute value mismatch for element '${element}'."
        else
            reason = "Element attribute value did not match expected value."
    }

    // VERIFY VISIBLE
    else if(message.contains("verifyElementVisible")){
        step = "Verify Element Visible"

        if(element)
            reason = "Element '${element}' is not visible on the page."
        else
            reason = "Expected element is not visible."
    }

    // VERIFY CLICKABLE
    else if(message.contains("verifyElementClickable")){
        step = "Verify Element Clickable"

        if(element)
            reason = "Element '${element}' is present but not clickable."
        else
            reason = "Element is present but not clickable."
    }

    // TIMEOUT
    else if(message.contains("TimeoutException")){
        step = "Page Load"
        reason = "Page took too long to load."
    }
	

/////////
	if(message == null || message.trim().isEmpty()){
		return [step: step, reason: reason]
	}

	println("Failure Message:")
	println(message)

	// -----------------------------
	// ✅ SOFT ASSERTION HANDLING
	// -----------------------------
	if(message.contains("SOFT ASSERT FAILURES")){

		step = "Data Validation"

		def cleaned = message
				.replaceAll("❌ SOFT ASSERT FAILURES \\(\\d+\\):", "")
				.trim()

		reason = "Data mismatch found:\n" + cleaned

		return [step: step, reason: reason]
	}

	// -----------------------------
	// Extract object name
	// -----------------------------
	if(objMatch.find()){
		element = objMatch.group(1).tokenize('/').last()
	}

	// -----------------------------
	// CLICK
	// -----------------------------
	if(message.toLowerCase().contains("click")){
		step = "Click Element"
		reason = element ?
				"Unable to click element '${element}'. It may be hidden or overlapped."
				: "Unable to click the target element."
	}

	// -----------------------------
	// ELEMENT NOT FOUND
	// -----------------------------
	else if(message.contains("WebElementNotFoundException")){
		step = "Locate Element"
		reason = element ?
				"Element '${element}' was not found on the page."
				: "Required page element was not found."
	}

	// -----------------------------
	// TIMEOUT
	// -----------------------------
	else if(message.contains("TimeoutException")){
		step = "Page Load"
		reason = "Page took too long to load."
	}


    return [step: step, reason: reason]
}

    // Build email body
    String buildEmailBody(TestSuiteContext context){

        String body = ""

        body += "Test Suite Execution Report\n\n"
        body += "Test Suite : " + context.getTestSuiteId() + "\n"
        body += "Total Failed : " + failedTests.size() + "\n\n"

        body += "Failed Test Cases\n"
        body += "----------------------------------\n\n"

        failedTests.eachWithIndex{ it, index ->

            body += "Test Case : ${it.name}\n"
            body += "Step      : ${it.step}\n"
            body += "Reason    : ${it.reason}\n"
            body += "Screenshot: Attached (${index+1})\n"
            body += "----------------------------------\n\n"
        }

        return body
    }

    // Send Email
    def sendMail(String body){

        println("Sending email report...")

        String host = "smtp.gmail.com"

        Properties props = new Properties()

        props.put("mail.smtp.host", host)
        props.put("mail.smtp.port", "587")
        props.put("mail.smtp.auth", "true")
        props.put("mail.smtp.starttls.enable", "true")

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator(){
                    protected PasswordAuthentication getPasswordAuthentication(){
                        return new PasswordAuthentication(
                                "gajakumara@first-insight.com",
                                "qnkj qbyt goya wbhd")
                    }
                })

        MimeMessage message = new MimeMessage(session)

        message.setFrom(new InternetAddress("gajakumara@first-insight.com"))

        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse("gajakumara@first-insight.com")
        )

        message.setSubject("Katalon Test Execution Result")

        Multipart multipart = new MimeMultipart()

        // Email body
        MimeBodyPart textPart = new MimeBodyPart()
        textPart.setText(body)

        multipart.addBodyPart(textPart)

        // Attach screenshots
        screenshots.each{ path ->

            File file = new File(path)

            if(file.exists()){

                MimeBodyPart attachment = new MimeBodyPart()

                attachment.attachFile(file)

                multipart.addBodyPart(attachment)
            }
        }

        message.setContent(multipart)

        Transport.send(message)

        println("Email sent successfully")
    }
}