import javax.mail.*
import javax.mail.internet.*
import javax.activation.*
import javax.mail.internet.MimeBodyPart

def sendEmail(String reportPath) {

    Properties props = new Properties()
    props.put("mail.smtp.host", "smtp.gmail.com")
    props.put("mail.smtp.auth", "true")
    props.put("mail.smtp.starttls.enable", "true")
    props.put("mail.smtp.port", "587")

    Session session = Session.getInstance(props, new javax.mail.Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication("qaautomationnoreply@first-insight.com", "cqgcpiurlrpevogl")
        }
    })

    Message msg = new MimeMessage(session)
    msg.setFrom(new InternetAddress("gajakumara@first-insight.com"))
    msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse("gajakumara@first-insight.com"))
    msg.setSubject("Katalon Test Report")
    msg.setText("Please find attached the latest Katalon test report.")

    MimeBodyPart attachment = new MimeBodyPart()
    attachment.attachFile(reportPath)

    Multipart multipart = new MimeMultipart()
    multipart.addBodyPart(attachment)
    msg.setContent(multipart)

    Transport.send(msg)
	println("Email sent successfully to recipient!")
}

@com.kms.katalon.core.annotation.AfterTestSuite
def afterSuite() {
    String reportFolder = RunConfiguration.getReportFolder()
    File reportFile = new File(reportFolder).listFiles().find { it.name.endsWith(".html") }

    if (reportFile != null) {
        sendEmail(reportFile.getAbsolutePath())
    } else {
        println("No HTML report found to attach.")
    }
}
