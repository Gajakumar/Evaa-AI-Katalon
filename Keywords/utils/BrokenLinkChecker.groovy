package utils

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.driver.DriverFactory

import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

import java.net.URL
import java.net.URLDecoder
import java.net.HttpURLConnection
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.LinkedHashSet
import java.util.Set


class BrokenLinkChecker {

    /*
     * ============================================================
     * MAIN KEYWORD
     * ============================================================
     *
     * Call this from Katalon, right after navigating OR right after
     * opening any in-page popup/modal:
     *
     * CustomKeywords.'utils.BrokenLinkChecker.verifyVisibleLinks'()
     *
     * Optional 2nd arg (default false): allowBrowserFallback.
     * Leave this OFF when checking a popup/modal. Turning it on makes
     * the keyword literally navigate() the live browser away to
     * double-check ambiguous links, then navigate back - which will
     * close/destroy any in-page popup or modal that isn't backed by
     * its own URL. Only enable it when you are checking a normal,
     * standalone page and don't mind a slower check.
     *
     * NOTE ON REAL NEW-WINDOW POPUPS:
     * This keyword only scans <a> elements that are visible in the
     * driver's CURRENT window/tab. If your "popup" is actually a
     * separate browser window/tab (target="_blank" or window.open()),
     * switch to it FIRST:
     *   WebUI.switchToWindowIndex(1)
     * or
     *   WebUI.switchToWindowTitle('Popup Title')
     * ...then call this keyword. If your popup is an in-page modal
     * rendered in the same DOM (Bootstrap/Material/etc dialog), no
     * switch is needed - just call this keyword once the modal is
     * visible.
     */
    @Keyword
    def verifyVisibleLinks(boolean allowBrowserFallback = false) {

        WebDriver driver = null

        try {
            driver = DriverFactory.getWebDriver()
        } catch (Exception e) {

            KeywordUtil.markFailed(
                "Unable to get WebDriver: " + e.getMessage()
            )

            return
        }

        if (driver == null) {

            KeywordUtil.markFailed(
                "WebDriver is not available."
            )

            return
        }

        String currentUrl = ""
        String pageTitle = ""

        try {

            currentUrl = driver.getCurrentUrl() ?: ""
            pageTitle = driver.getTitle() ?: ""

        } catch (Exception e) {

            KeywordUtil.logInfo(
                "Unable to read current page information: " +
                e.getMessage()
            )
        }


        KeywordUtil.logInfo("")
        KeywordUtil.logInfo(
            "=================================================="
        )

        KeywordUtil.logInfo(
            "       BROKEN LINK VERIFICATION"
        )

        KeywordUtil.logInfo(
            "=================================================="
        )

        KeywordUtil.logInfo(
            "Page : ${pageTitle}"
        )

        KeywordUtil.logInfo(
            "URL  : ${currentUrl}"
        )

        KeywordUtil.logInfo(
            "Browser fallback for ambiguous links: " +
            (allowBrowserFallback ? "ENABLED" : "DISABLED (safe mode)")
        )


        /*
         * --------------------------------------------------------
         * FIND VISIBLE LINKS
         * --------------------------------------------------------
         */

        List<WebElement> links = []


        try {

            List<WebElement> allLinks =
                driver.findElements(
                    By.tagName("a")
                )

            if (allLinks == null) {
                allLinks = []
            }


            KeywordUtil.logInfo(
                "Total <a> elements found: ${allLinks.size()}"
            )


            for (WebElement link : allLinks) {

                try {

                    if (link != null &&
                        link.isDisplayed()) {

                        links.add(link)
                    }

                } catch (Exception ignored) {
                }
            }


            /*
             * Detect popup/dialog only for information.
             * We still check normal page + popup links.
             */

            List<WebElement> dialogs = []

            try {

                dialogs =
                    driver.findElements(
                        By.xpath(
                            "//*[(@role='dialog' or @aria-modal='true') " +
                            "and not(contains(@style,'display: none'))]"
                        )
                    )

                if (dialogs == null) {
                    dialogs = []
                }

            } catch (Exception e) {

                KeywordUtil.logInfo(
                    "Unable to detect dialogs: " +
                    e.getMessage()
                )
            }


            if (!dialogs.isEmpty()) {

                KeywordUtil.logInfo(
                    "========== POPUP / DIALOG DETECTED =========="
                )

                KeywordUtil.logInfo(
                    "Visible page + popup links will be checked."
                )

            } else {

                KeywordUtil.logInfo(
                    "========== PAGE DETECTED =========="
                )
            }


            KeywordUtil.logInfo(
                "Visible links found: ${links.size()}"
            )


        } catch (Exception e) {

            KeywordUtil.logInfo(
                "Unable to collect links."
            )

            KeywordUtil.logInfo(
                "Error   : ${e.getClass().getName()}"
            )

            KeywordUtil.logInfo(
                "Message : ${e.getMessage()}"
            )

            links = []
        }


        if (links == null) {
            links = []
        }


        if (links.isEmpty()) {

            KeywordUtil.logInfo(
                "No visible <a> links found on this page."
            )

            KeywordUtil.logInfo(
                "=================================================="
            )

            return
        }


        verifyBrokenLinks(
            links,
            driver,
            currentUrl,
            allowBrowserFallback
        )
    }



    /*
     * ============================================================
     * LINK VERIFICATION
     * ============================================================
     */

    private void verifyBrokenLinks(
            List<WebElement> links,
            WebDriver driver,
            String currentPageUrl,
            boolean allowBrowserFallback) {


        if (links == null) {
            links = []
        }


        if (driver == null) {

            KeywordUtil.markFailed(
                "WebDriver is not available."
            )

            return
        }


        KeywordUtil.logInfo("")
        KeywordUtil.logInfo(
            "=================================================="
        )

        KeywordUtil.logInfo(
            "       BROKEN LINK VERIFICATION STARTED"
        )

        KeywordUtil.logInfo(
            "=================================================="
        )


        KeywordUtil.logInfo(
            "Total <a> elements found: ${links.size()}"
        )


        int validCount = 0
        int brokenCount = 0
        int soft404Count = 0
        int skippedCount = 0
        int unverifiedCount = 0
        int anchorCount = 0
        int brokenAnchorCount = 0
        int browserFallbackCount = 0
        int errorCount = 0


        /*
         * Keep track of URLs already checked.
         */

        Set<String> checkedUrls =
            new LinkedHashSet<String>()


        links.eachWithIndex { WebElement link, int index ->

            try {

                if (link == null) {

                    skippedCount++

                    KeywordUtil.logInfo(
                        "[${index + 1}] Skipped - Null element"
                    )

                    return
                }


                /*
                 * ------------------------------------------------
                 * GET HREF
                 * ------------------------------------------------
                 */

                String originalUrl =
                    link.getAttribute("href")


                if (originalUrl == null ||
                    originalUrl.trim().isEmpty()) {

                    skippedCount++

                    KeywordUtil.logInfo(
                        "[${index + 1}] Skipped - Empty href"
                    )

                    return
                }


                originalUrl =
                    originalUrl.trim()


                String lowerUrl =
                    originalUrl.toLowerCase()


                /*
                 * ------------------------------------------------
                 * NON WEB LINKS
                 * ------------------------------------------------
                 */

                if (lowerUrl.startsWith("javascript:") ||
                    lowerUrl.startsWith("mailto:") ||
                    lowerUrl.startsWith("tel:") ||
                    lowerUrl.startsWith("data:") ||
                    lowerUrl.startsWith("blob:")) {

                    skippedCount++

                    KeywordUtil.logInfo(
                        "[${index + 1}] Skipped - ${originalUrl}"
                    )

                    return
                }


                /*
                 * ------------------------------------------------
                 * ELEMENT NAME
                 * ------------------------------------------------
                 */

                String linkText = ""

                try {

                    linkText =
                        link.getText()?.trim() ?: ""

                } catch (Exception ignored) {
                }


                String ariaLabel = ""

                try {

                    ariaLabel =
                        link.getAttribute(
                            "aria-label"
                        )?.trim() ?: ""

                } catch (Exception ignored) {
                }


                String titleAttribute = ""

                try {

                    titleAttribute =
                        link.getAttribute(
                            "title"
                        )?.trim() ?: ""

                } catch (Exception ignored) {
                }


                String imageAlt = ""

                try {

                    List<WebElement> images =
                        link.findElements(
                            By.tagName("img")
                        )

                    if (images != null &&
                        !images.isEmpty()) {

                        imageAlt =
                            images[0]
                                .getAttribute("alt")
                                ?.trim() ?: ""
                    }

                } catch (Exception ignored) {
                }


                String elementName = ""


                if (linkText) {

                    elementName = linkText

                } else if (ariaLabel) {

                    elementName = ariaLabel

                } else if (titleAttribute) {

                    elementName = titleAttribute

                } else if (imageAlt) {

                    elementName = imageAlt

                } else {

                    try {

                        WebElement parent =
                            link.findElement(
                                By.xpath("./..")
                            )

                        elementName =
                            parent.getText()?.trim() ?: ""

                    } catch (Exception ignored) {
                    }
                }


                if (!elementName) {
                    elementName = "No visible text"
                }


                /*
                 * ------------------------------------------------
                 * GET XPATH
                 * ------------------------------------------------
                 */

                String xpath =
                    getElementXPath(
                        driver,
                        link
                    )


                /*
                 * ------------------------------------------------
                 * RESOLVE URL
                 * ------------------------------------------------
                 */

                String resolvedUrl = ""

                try {

                    resolvedUrl =
                        resolveUrl(
                            currentPageUrl,
                            originalUrl
                        )

                } catch (Exception e) {

                    skippedCount++

                    KeywordUtil.logInfo(
                        "[${index + 1}] Unable to resolve URL: " +
                        originalUrl
                    )

                    return
                }


                if (!resolvedUrl) {

                    skippedCount++

                    KeywordUtil.logInfo(
                        "[${index + 1}] Skipped - " +
                        "Unable to resolve URL"
                    )

                    return
                }


                String resolvedLower =
                    resolvedUrl.toLowerCase()


                if (!resolvedLower.startsWith("http://") &&
                    !resolvedLower.startsWith("https://")) {

                    skippedCount++

                    KeywordUtil.logInfo(
                        "[${index + 1}] Skipped - " +
                        "Unsupported URL: ${resolvedUrl}"
                    )

                    return
                }


                /*
                 * ------------------------------------------------
                 * FRAGMENT
                 * ------------------------------------------------
                 */

                String urlWithoutFragment =
                    resolvedUrl

                String fragment = ""


                int hashIndex =
                    resolvedUrl.indexOf("#")


                if (hashIndex >= 0) {

                    fragment =
                        resolvedUrl.substring(
                            hashIndex + 1
                        )

                    urlWithoutFragment =
                        resolvedUrl.substring(
                            0,
                            hashIndex
                        )
                }


                /*
                 * ------------------------------------------------
                 * CURRENT PAGE ANCHOR
                 * ------------------------------------------------
                 */

                if (originalUrl.startsWith("#")) {

                    if (!fragment ||
                        !fragment.trim()) {

                        skippedCount++

                        KeywordUtil.logInfo(
                            "[${index + 1}] " +
                            "Skipped - Empty anchor (#)"
                        )

                        return
                    }


                    anchorCount++


                    boolean anchorExists =
                        verifyAnchor(
                            driver,
                            fragment
                        )


                    if (anchorExists) {

                        validCount++

                        KeywordUtil.logInfo(
                            "VALID ANCHOR [${index + 1}] " +
                            "UI Element: ${elementName} | " +
                            "Anchor: #${fragment} | XPath: ${xpath}"
                        )

                    } else {

                        brokenCount++
                        brokenAnchorCount++

                        KeywordUtil.logInfo(
                            "BROKEN ANCHOR [${index + 1}] " +
                            "UI Element: ${elementName} | " +
                            "Anchor: #${fragment} | XPath: ${xpath} | " +
                            "Reason: Target anchor does not exist on the current page."
                        )
                    }

                    return
                }


                /*
                 * ------------------------------------------------
                 * DUPLICATE URL
                 * ------------------------------------------------
                 */

                if (checkedUrls.contains(
                        urlWithoutFragment)) {

                    KeywordUtil.logInfo(
                        "[${index + 1}] URL already checked: " +
                        urlWithoutFragment
                    )


                    if (fragment) {

                        KeywordUtil.logInfo(
                            "   Fragment detected: #${fragment}"
                        )
                    }

                    return
                }


                checkedUrls.add(
                    urlWithoutFragment
                )


                /*
                 * =================================================
                 * HTTP HEAD
                 * =================================================
                 */

                UrlCheckResult headResult =
                    checkUrl(
                        urlWithoutFragment,
                        "HEAD",
                        driver
                    )


                UrlCheckResult result =
                    headResult


                String methodUsed = "HEAD"


                /*
                 * =================================================
                 * HTTP RESULT DECISION
                 * =================================================
                 */

                boolean ambiguousResult = false


                /*
                 * 999 = connection/check failure (DNS, timeout, refused).
                 * This is NOT an HTTP status, so we must not treat it
                 * as automatically broken - some corporate/internal
                 * sites block plain HttpURLConnection traffic entirely.
                 */

                if (headResult.statusCode == 999) {
                    ambiguousResult = true
                }


                /*
                 * Many real sites (social platforms, WAF/CDN-protected
                 * sites, auth-gated pages) reject non-browser HEAD/GET
                 * requests with one of these codes even though the link
                 * itself is fine in a real browser. Treat as ambiguous,
                 * not broken.
                 */

                if (headResult.statusCode == 401 ||
                    headResult.statusCode == 403 ||
                    headResult.statusCode == 405 ||
                    headResult.statusCode == 429) {

                    ambiguousResult = true
                }


                if (ambiguousResult) {

                    KeywordUtil.logInfo(
                        "[${index + 1}] AMBIGUOUS - HTTP check returned " +
                        "${headResult.statusCode} for ${urlWithoutFragment}"
                    )


                    if (allowBrowserFallback) {

                        /*
                         * ---------------------------------------------
                         * OPT-IN ONLY: this literally navigates the
                         * live driver away and back. Never use this
                         * while checking an in-page popup/modal - it
                         * will close it.
                         * ---------------------------------------------
                         */

                        browserFallbackCount++


                        boolean browserValid =
                            verifyLinkUsingBrowser(
                                driver,
                                urlWithoutFragment
                            )


                        if (browserValid) {

                            validCount++

                            KeywordUtil.logInfo(
                                "VALID LINK - BROWSER FALLBACK [${index + 1}] " +
                                "UI Element: ${elementName} | URL: ${urlWithoutFragment}"
                            )

                        } else {

                            brokenCount++

                            KeywordUtil.logInfo(
                                "BROKEN LINK [${index + 1}] " +
                                "UI Element: ${elementName} | URL: ${urlWithoutFragment} | " +
                                "Reason: HTTP check ambiguous and browser navigation also failed."
                            )
                        }

                    } else {

                        /*
                         * SAFE MODE (default): don't touch the driver.
                         * Report as unverified rather than guessing.
                         */

                        unverifiedCount++

                        KeywordUtil.logInfo(
                            "UNVERIFIED [${index + 1}] " +
                            "UI Element: ${elementName} | URL: ${urlWithoutFragment} | " +
                            "Reason: Server returned ${headResult.statusCode} to an automated " +
                            "request (likely bot/WAF protection, not necessarily broken). " +
                            "Re-run with allowBrowserFallback=true on a non-popup page to confirm, " +
                            "or verify this link manually."
                        )
                    }


                    return
                }


                /*
                 * =================================================
                 * REAL HTTP ERROR
                 * =================================================
                 */

                if (result.statusCode < 200 ||
                    result.statusCode >= 400) {

                    brokenCount++

                    KeywordUtil.logInfo(
                        "BROKEN LINK [${index + 1}] " +
                        "UI Element: ${elementName} | " +
                        "HTTP Status: ${result.statusCode} | Method: ${methodUsed} | " +
                        "URL: ${urlWithoutFragment} | XPath: ${xpath} | " +
                        "Reason: Server returned a real HTTP error status."
                    )

                    return
                }


                /*
                 * =================================================
                 * GET REQUEST FOR SOFT 404
                 * =================================================
                 */

                UrlCheckResult getResult =
                    checkUrl(
                        urlWithoutFragment,
                        "GET",
                        driver
                    )


                if (getResult.statusCode == 999 ||
                    getResult.statusCode == 401 ||
                    getResult.statusCode == 403 ||
                    getResult.statusCode == 405 ||
                    getResult.statusCode == 429) {

                    unverifiedCount++

                    KeywordUtil.logInfo(
                        "UNVERIFIED [${index + 1}] " +
                        "UI Element: ${elementName} | URL: ${urlWithoutFragment} | " +
                        "Reason: GET returned ${getResult.statusCode} (ambiguous, not counted as broken)."
                    )

                    return
                }


                if (getResult.statusCode < 200 ||
                    getResult.statusCode >= 400) {

                    brokenCount++

                    KeywordUtil.logInfo(
                        "BROKEN LINK [${index + 1}] " +
                        "UI Element: ${elementName} | " +
                        "HTTP Status: ${getResult.statusCode} | " +
                        "URL: ${urlWithoutFragment} | XPath: ${xpath} | " +
                        "Reason: GET request returned a real HTTP error status."
                    )

                    return
                }


                result = getResult
                methodUsed = "GET"


                /*
                 * =================================================
                 * SOFT 404
                 * =================================================
                 */

                boolean soft404Detected =
                    isSoft404(
                        getResult.responseBody,
                        getResult.pageTitle
                    )


                if (soft404Detected) {

                    soft404Count++
                    brokenCount++

                    KeywordUtil.logInfo(
                        "SOFT-404 LINK [${index + 1}] " +
                        "UI Element: ${elementName} | " +
                        "HTTP Status: ${getResult.statusCode} | " +
                        "URL: ${urlWithoutFragment} | Page Title: ${getResult.pageTitle} | " +
                        "XPath: ${xpath} | " +
                        "Reason: Server returned success but the destination looks like a " +
                        "Page Not Found page."
                    )

                    return
                }


                /*
                 * =================================================
                 * VALID LINK
                 * ================================================= */

                validCount++

                KeywordUtil.logInfo(
                    "VALID LINK [${index + 1}] " +
                    "UI Element: ${elementName} | HTTP Status: ${getResult.statusCode} | " +
                    "Method: ${methodUsed} | ContentType: ${getResult.contentType} | " +
                    "Page Title: ${getResult.pageTitle} | URL: ${urlWithoutFragment} | " +
                    "XPath: ${xpath}"
                )


                /*
                 * =================================================
                 * DESTINATION ANCHOR (checked from downloaded HTML only
                 * - no browser navigation involved, safe by default)
                 * ================================================= */

                if (fragment &&
                    fragment.trim()) {

                    boolean destinationAnchorExists =
                        verifyDestinationAnchor(
                            getResult.responseBody,
                            fragment
                        )


                    if (destinationAnchorExists) {

                        KeywordUtil.logInfo(
                            "   Destination anchor exists: #${fragment}"
                        )

                    } else if (allowBrowserFallback) {

                        browserFallbackCount++

                        boolean browserAnchorExists =
                            verifyDestinationAnchorUsingBrowser(
                                driver,
                                resolvedUrl,
                                fragment
                            )

                        if (browserAnchorExists) {

                            KeywordUtil.logInfo(
                                "   Destination anchor exists in browser: #${fragment}"
                            )

                        } else {

                            brokenCount++
                            brokenAnchorCount++

                            KeywordUtil.logInfo(
                                "BROKEN DESTINATION ANCHOR [${index + 1}] " +
                                "UI Element: ${elementName} | URL: ${resolvedUrl} | " +
                                "Anchor: #${fragment} | XPath: ${xpath}"
                            )
                        }

                    } else {

                        KeywordUtil.logInfo(
                            "   Anchor #${fragment} not found in downloaded HTML " +
                            "(may be JS-rendered - not flagged broken in safe mode)."
                        )
                    }
                }


            } catch (Exception e) {

                errorCount++
                brokenCount++

                KeywordUtil.logInfo(
                    "ERROR CHECKING LINK [${index + 1}] " +
                    "Error: ${e.getClass().getName()} | Message: ${e.getMessage()}"
                )
            }
        }


        /*
         * ============================================================
         * SUMMARY
         * ============================================================
         */

        KeywordUtil.logInfo("")
        KeywordUtil.logInfo(
            "=================================================="
        )

        KeywordUtil.logInfo(
            "       BROKEN LINK VERIFICATION SUMMARY"
        )

        KeywordUtil.logInfo(
            "=================================================="
        )

        KeywordUtil.logInfo("Total Links          : ${links.size()}")
        KeywordUtil.logInfo("Unique URLs          : ${checkedUrls.size()}")
        KeywordUtil.logInfo("Valid Links          : ${validCount}")
        KeywordUtil.logInfo("Broken Links         : ${brokenCount}")
        KeywordUtil.logInfo("Soft-404 Links       : ${soft404Count}")
        KeywordUtil.logInfo("Broken Anchors       : ${brokenAnchorCount}")
        KeywordUtil.logInfo("Unverified (ambiguous): ${unverifiedCount}")
        KeywordUtil.logInfo("Skipped Links        : ${skippedCount}")
        KeywordUtil.logInfo("Browser Fallbacks    : ${browserFallbackCount}")
        KeywordUtil.logInfo("Errors               : ${errorCount}")
        KeywordUtil.logInfo(
            "=================================================="
        )


        if (brokenCount > 0) {

            KeywordUtil.markFailed(
                "Broken link verification failed. " +
                "${brokenCount} broken link(s) found. " +
                "Soft-404: ${soft404Count}. Broken anchors: ${brokenAnchorCount}. " +
                "${unverifiedCount} link(s) were unverifiable and not counted as failures."
            )

        } else {

            KeywordUtil.markPassed(
                "Broken link verification passed. No broken links found. " +
                "(${unverifiedCount} link(s) were unverifiable due to bot/WAF protection.)"
            )
        }
    }



    /*
     * ============================================================
     * RESOLVE RELATIVE URL
     * ============================================================
     */

    private String resolveUrl(
            String currentPageUrl,
            String href) {


        if (!href) {
            return ""
        }


        href = href.trim()


        if (href.startsWith("http://") ||
            href.startsWith("https://")) {

            return href
        }


        if (href.startsWith("//")) {

            URL base = new URL(currentPageUrl)

            return base.getProtocol() + ":" + href
        }


        if (href.startsWith("#")) {

            return currentPageUrl + href
        }


        URL base = new URL(currentPageUrl)


        return new URL(base, href).toString()
    }



    /*
     * ============================================================
     * HTTP CHECK
     * ============================================================
     */

    private UrlCheckResult checkUrl(
            String urlString,
            String method,
            WebDriver driver) {


        java.net.HttpURLConnection connection = null

        UrlCheckResult result = new UrlCheckResult()


        try {

            URL url = new URL(urlString)

            connection = (java.net.HttpURLConnection) url.openConnection()

            connection.setRequestMethod(method)
            connection.setConnectTimeout(10000)
            connection.setReadTimeout(10000)
            connection.setInstanceFollowRedirects(true)

            connection.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/150.0.0.0 Safari/537.36"
            )

            connection.setRequestProperty(
                "Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9," +
                "image/avif,image/webp,*/*;q=0.8"
            )

            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.9")
            connection.setRequestProperty("Cache-Control", "no-cache")


            /*
             * Copy browser cookies for the CURRENT domain only - Selenium
             * can't hand over cookies for other domains anyway, so this
             * only helps same-domain authenticated links.
             */

            if (driver != null) {

                try {

                    Set<org.openqa.selenium.Cookie> cookies =
                        driver.manage().getCookies()

                    if (cookies != null && !cookies.isEmpty()) {

                        String cookieHeader =
                            cookies.collect { cookie ->
                                "${cookie.getName()}=${cookie.getValue()}"
                            }.join("; ")

                        if (cookieHeader) {
                            connection.setRequestProperty("Cookie", cookieHeader)
                        }
                    }

                } catch (Exception cookieException) {

                    KeywordUtil.logInfo(
                        "Unable to copy browser cookies: " +
                        cookieException.getMessage()
                    )
                }
            }


            connection.connect()

            result.statusCode = connection.getResponseCode()
            result.contentType = connection.getContentType() ?: ""
            result.finalUrl = connection.getURL()?.toString() ?: urlString

            KeywordUtil.logInfo(
                "HTTP CHECK | ${method} | ${result.statusCode} | ${urlString}"
            )


            if (method == "GET") {

                InputStream inputStream = null

                try {

                    if (result.statusCode >= 400) {
                        inputStream = connection.getErrorStream()
                    } else {
                        inputStream = connection.getInputStream()
                    }

                    if (inputStream != null) {

                        BufferedReader reader =
                            new BufferedReader(
                                new InputStreamReader(inputStream, "UTF-8")
                            )

                        StringBuilder response = new StringBuilder()
                        String line

                        while ((line = reader.readLine()) != null) {

                            response.append(line)
                            response.append("\n")

                            if (response.length() >= 500000) {
                                break
                            }
                        }

                        reader.close()

                        result.responseBody = response.toString()
                        result.pageTitle = extractPageTitle(result.responseBody)
                    }

                } catch (Exception bodyException) {

                    KeywordUtil.logInfo(
                        "Unable to read response body: " +
                        bodyException.getMessage()
                    )
                }
            }

            return result


        } catch (Exception e) {

            KeywordUtil.logInfo(
                "Unable to check URL: " + urlString + " | " +
                e.getClass().getName() + " | " + e.getMessage()
            )

            result.statusCode = 999

            return result

        } finally {

            if (connection != null) {

                try {
                    connection.disconnect()
                } catch (Exception ignored) {
                }
            }
        }
    }



    /*
     * ============================================================
     * BROWSER FALLBACK (OPT-IN ONLY)
     * ============================================================
     *
     * WARNING: this navigates the live driver away and back. Never
     * call this path while a popup/modal is what you're trying to
     * verify - it will close it. Only used when allowBrowserFallback
     * is explicitly passed as true.
     */

    private boolean verifyLinkUsingBrowser(
            WebDriver driver,
            String url) {

        if (driver == null || !url) {
            return false
        }

        String originalUrl = ""

        try {

            originalUrl = driver.getCurrentUrl()

            KeywordUtil.logInfo("Browser verification: " + url)

            driver.navigate().to(url)

            Thread.sleep(2000)

            String loadedUrl = driver.getCurrentUrl()
            String title = driver.getTitle()

            KeywordUtil.logInfo("   Browser URL   : ${loadedUrl}")
            KeywordUtil.logInfo("   Browser Title : ${title}")

            String bodyText = ""

            try {
                bodyText = driver.findElement(By.tagName("body")).getText()
            } catch (Exception ignored) {
            }

            String lowerBody = bodyText?.toLowerCase() ?: ""
            String lowerTitle = title?.toLowerCase() ?: ""

            boolean browser404 =
                lowerTitle.contains("404") ||
                lowerTitle.contains("page not found") ||
                lowerBody.contains("page not found") ||
                lowerBody.contains("the page you requested could not be found")

            if (browser404) {

                KeywordUtil.logInfo("   Browser loaded a Page Not Found page.")
                restoreBrowserPage(driver, originalUrl)
                return false
            }

            if (loadedUrl && !loadedUrl.toLowerCase().startsWith("data:")) {
                restoreBrowserPage(driver, originalUrl)
                return true
            }

        } catch (Exception e) {

            KeywordUtil.logInfo(
                "   Browser verification failed: " +
                e.getClass().getName() + " | " + e.getMessage()
            )

            restoreBrowserPage(driver, originalUrl)
            return false
        }

        restoreBrowserPage(driver, originalUrl)
        return false
    }



    private void restoreBrowserPage(
            WebDriver driver,
            String originalUrl) {

        if (driver == null || !originalUrl) {
            return
        }

        try {
            driver.navigate().to(originalUrl)
            Thread.sleep(1000)
        } catch (Exception e) {
            KeywordUtil.logInfo("Unable to restore original page: " + e.getMessage())
        }
    }



    /*
     * ============================================================
     * SOFT 404
     * ============================================================
     * Trimmed to patterns specific enough that they won't false-fire
     * on ordinary "no results" search/listing pages.
     */

    private boolean isSoft404(
            String responseBody,
            String pageTitle) {

        if (!responseBody) {
            return false
        }

        String body = responseBody.toLowerCase().replaceAll("\\s+", " ")
        String title = (pageTitle ?: "").toLowerCase().trim()

        if (title.contains("404") ||
            title.contains("page not found") ||
            title.equals("not found") ||
            title.contains("error 404")) {

            KeywordUtil.logInfo("Soft-404 detected from page title: " + pageTitle)
            return true
        }

        List<String> strongPatterns = [
            "page not found",
            "404 page not found",
            "404 - page not found",
            "404 error",
            "error 404",
            "the page you requested could not be found",
            "the page you are looking for could not be found",
            "requested page could not be found",
            "page doesn't exist",
            "page does not exist",
            "this page doesn't exist",
            "this page does not exist",
            "page cannot be found",
            "page can't be found",
            "sorry, we couldn't find that page",
            "sorry, we could not find that page"
        ]

        for (String pattern : strongPatterns) {

            if (body.contains(pattern)) {
                KeywordUtil.logInfo("Soft-404 detected from content: " + pattern)
                return true
            }
        }

        boolean wordpressPage =
            body.contains("wp-content") || body.contains("wp-includes")

        boolean wordpress404Class =
            body.contains("error404") ||
            body.contains("is-404") ||
            body.contains("page-404")

        if (wordpressPage && wordpress404Class) {
            KeywordUtil.logInfo("WordPress soft-404 detected.")
            return true
        }

        return false
    }



    private String extractPageTitle(String html) {

        if (!html) {
            return ""
        }

        try {

            java.util.regex.Pattern pattern =
                java.util.regex.Pattern.compile("(?is)<title[^>]*>(.*?)</title>")

            java.util.regex.Matcher matcher = pattern.matcher(html)

            if (matcher.find()) {
                return matcher.group(1)
                    .replaceAll("<[^>]+>", "")
                    .replaceAll("\\s+", " ")
                    .trim()
            }

        } catch (Exception ignored) {
        }

        return ""
    }



    private boolean verifyAnchor(
            WebDriver driver,
            String fragment) {

        try {

            String decodedFragment = URLDecoder.decode(fragment, "UTF-8")

            List<WebElement> idElements = driver.findElements(By.id(decodedFragment))

            if (idElements != null && !idElements.isEmpty()) {
                return true
            }

            List<WebElement> nameElements = driver.findElements(By.name(decodedFragment))

            if (nameElements != null && !nameElements.isEmpty()) {
                return true
            }

            return false

        } catch (Exception e) {

            KeywordUtil.logInfo(
                "Unable to verify anchor #" + fragment + " | " + e.getMessage()
            )
            return false
        }
    }



    private boolean verifyDestinationAnchor(
            String responseBody,
            String fragment) {

        if (!responseBody || !fragment) {
            return false
        }

        try {

            String decodedFragment = URLDecoder.decode(fragment, "UTF-8")
            String escaped = java.util.regex.Pattern.quote(decodedFragment)
            String patternString = "(?is)(id|name)\\s*=\\s*['\"]" + escaped + "['\"]"

            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(patternString)
            java.util.regex.Matcher matcher = pattern.matcher(responseBody)

            return matcher.find()

        } catch (Exception e) {

            KeywordUtil.logInfo(
                "Unable to verify destination anchor #" + fragment + " | " + e.getMessage()
            )
            return false
        }
    }



    /*
     * OPT-IN ONLY - navigates the live driver. See warning above.
     */
    private boolean verifyDestinationAnchorUsingBrowser(
            WebDriver driver,
            String url,
            String fragment) {

        if (driver == null || !url || !fragment) {
            return false
        }

        String originalUrl = ""

        try {

            originalUrl = driver.getCurrentUrl()

            driver.navigate().to(url)

            Thread.sleep(1500)

            String decodedFragment = URLDecoder.decode(fragment, "UTF-8")

            List<WebElement> idElements = driver.findElements(By.id(decodedFragment))

            if (idElements != null && !idElements.isEmpty()) {
                restoreBrowserPage(driver, originalUrl)
                return true
            }

            List<WebElement> nameElements = driver.findElements(By.name(decodedFragment))

            if (nameElements != null && !nameElements.isEmpty()) {
                restoreBrowserPage(driver, originalUrl)
                return true
            }

        } catch (Exception e) {

            KeywordUtil.logInfo("Browser anchor verification failed: " + e.getMessage())
        }

        restoreBrowserPage(driver, originalUrl)
        return false
    }



    private String getElementXPath(
            WebDriver driver,
            WebElement element) {

        try {

            JavascriptExecutor js = (JavascriptExecutor) driver

            Object result =
                js.executeScript(
                    """
                    function getXPath(element) {
                        if (element.id !== '') {
                            return '//*[@id="' + element.id + '"]';
                        }
                        if (element === document.body) {
                            return '/html/body';
                        }
                        var ix = 0;
                        var siblings = element.parentNode.children;
                        for (var i = 0; i < siblings.length; i++) {
                            var sibling = siblings[i];
                            if (sibling === element) {
                                return getXPath(element.parentNode) + '/' +
                                    element.tagName.toLowerCase() + '[' + (ix + 1) + ']';
                            }
                            if (sibling.tagName === element.tagName) {
                                ix++;
                            }
                        }
                    }
                    return getXPath(arguments[0]);
                    """,
                    element
                )

            return result?.toString() ?: "Unable to determine XPath"

        } catch (Exception e) {

            return "Unable to determine XPath"
        }
    }



    private static class UrlCheckResult {

        int statusCode = 999
        String responseBody = ""
        String pageTitle = ""
        String contentType = ""
        String finalUrl = ""
    }
}