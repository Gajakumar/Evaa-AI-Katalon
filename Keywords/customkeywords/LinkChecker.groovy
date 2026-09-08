package customkeywords

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.logging.KeywordLogger
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.By
import org.openqa.selenium.StaleElementReferenceException
import java.net.HttpURLConnection
import java.net.URL
import java.net.MalformedURLException

/**
 * Custom keyword: verifyNoBrokenLinks
 *
 * Scans all <a href="..."> elements CURRENTLY VISIBLE to the driver
 * (whether that's the main page, an in-page modal/popup, or a switched-to
 * new window/tab) and checks each unique href for a broken response.
 *
 * IMPORTANT: this keyword only checks whatever is in the driver's
 * CURRENT context. If your "popup" is a real new browser window/tab
 * (opened via window.open / target="_blank"), you must switch to it
 * FIRST using WebUI.switchToWindowIndex() / switchToWindowTitle()
 * before calling this keyword. If it's an in-page modal (Bootstrap/
 * Angular Material/etc. dialog rendered in the same DOM), no switch
 * is needed - just call it after the modal is visible.
 */
class LinkChecker {

    @Keyword
    List<String> verifyNoBrokenLinks(int timeoutInSeconds = 10, boolean failTestOnBrokenLink = true) {
        KeywordLogger logger = new KeywordLogger()
        WebDriver driver = DriverFactory.getWebDriver()

        String baseUrl
        try {
            baseUrl = driver.getCurrentUrl()
        } catch (Exception e) {
            baseUrl = '(unknown - could not read current URL)'
        }

        // Snapshot hrefs first (avoid StaleElementReferenceException if the
        // DOM re-renders mid-loop, e.g. Angular/React apps)
        List<String> hrefs = []
        List<WebElement> anchors = driver.findElements(By.tagName('a'))

        for (WebElement anchor : anchors) {
            try {
                String href = anchor.getAttribute('href')
                if (href != null && !href.trim().isEmpty()) {
                    hrefs.add(href.trim())
                }
            } catch (StaleElementReferenceException e) {
                // element vanished between findElements and getAttribute - skip it
                continue
            }
        }

        List<String> checked = []
        List<String> brokenLinks = []

        for (String href : hrefs) {
            if (isSkippable(href)) continue
            if (checked.contains(href)) continue // don't re-check duplicates
            checked.add(href)

            int statusCode = getStatusCode(href, timeoutInSeconds)
            logger.logInfo("Checked link: ${href}  ->  Status: ${statusCode}")

            if (statusCode == -1 || statusCode >= 400) {
                brokenLinks.add("${href}  (Status: ${statusCode})")
            }
        }

        if (!brokenLinks.isEmpty()) {
            String message = "Broken link(s) found on [${baseUrl}]:\n" + brokenLinks.join('\n')
            logger.logError(message)
            if (failTestOnBrokenLink) {
                throw new Exception(message)
            }
        } else {
            logger.logInfo("No broken links found on [${baseUrl}]. Checked ${checked.size()} unique link(s).")
        }

        return brokenLinks
    }

    private boolean isSkippable(String href) {
        String h = href.toLowerCase()
        return h.startsWith('javascript:') ||
               h.startsWith('mailto:') ||
               h.startsWith('tel:') ||
               h.startsWith('sms:') ||
               h == '#' ||
               h.startsWith('#') ||
               h.isEmpty()
    }

    private int getStatusCode(String urlString, int timeoutInSeconds) {
        HttpURLConnection connection = null
        try {
            URL url = new URL(urlString)
            connection = openConnection(url, 'HEAD', timeoutInSeconds)
            connection.connect()
            int code = connection.getResponseCode()

            // Some servers don't support HEAD (405/501) or misbehave (-1) -> retry with GET
            if (code == 405 || code == 501 || code == -1) {
                connection.disconnect()
                connection = openConnection(url, 'GET', timeoutInSeconds)
                connection.connect()
                code = connection.getResponseCode()
            }
            return code
        } catch (MalformedURLException e) {
            return -1
        } catch (Exception e) {
            return -1
        } finally {
            if (connection != null) {
                connection.disconnect()
            }
        }
    }

    private HttpURLConnection openConnection(URL url, String method, int timeoutInSeconds) {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection()
        connection.setRequestMethod(method)
        connection.setConnectTimeout(timeoutInSeconds * 1000)
        connection.setReadTimeout(timeoutInSeconds * 1000)
        connection.setInstanceFollowRedirects(true)
        connection.setRequestProperty('User-Agent', 'Mozilla/5.0 (KatalonLinkChecker)')
        return connection
    }
}