//package utils
//
//import com.kms.katalon.core.annotation.Keyword
//import com.kms.katalon.core.util.KeywordUtil
//import com.kms.katalon.core.webui.driver.DriverFactory
//import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
//
//class VisualRegression {
//
//    @Keyword
//    def captureScreenshot(String screenshotName) {
//
//        String path =
//                "Screenshots/EVAA/${screenshotName}.png"
//
//        WebUI.takeFullPageScreenshot(path)
//
//        KeywordUtil.logInfo(
//                "Visual screenshot captured: ${path}"
//        )
//    }
//
//    @Keyword
//    def captureComponent(
//            String screenshotName,
//            String xpath) {
//
//        def element =
//                DriverFactory.getWebDriver()
//                        .findElement(
//                                org.openqa.selenium.By.xpath(xpath)
//                        )
//
//        String path =
//                "Screenshots/EVAA/${screenshotName}.png"
//
//        org.apache.commons.io.FileUtils.copyFile(
//                ((org.openqa.selenium.TakesScreenshot)
//                        DriverFactory.getWebDriver())
//                        .getScreenshotAs(
//                                org.openqa.selenium.OutputType.FILE
//                        ),
//                new File(path)
//        )
//
//        KeywordUtil.logInfo(
//                "Component screenshot captured: ${path}"
//        )
//    }
//}

package utils

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.OutputType
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.NoSuchElementException

import org.apache.commons.io.FileUtils

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import java.net.HttpURLConnection
import java.net.URL


class VisualRegression {

    private static final String BASE_DIR = "Screenshots/EVAA"
    private static final String LOCAL_BASELINE_DIR = "${BASE_DIR}/baseline"
    private static final String DIFF_DIR = "${BASE_DIR}/diff"


    /*
     * ============================================================
     * CAPTURE FULL PAGE SCREENSHOT
     * ============================================================
     * Writes to the LOCAL disk of whatever machine is executing the
     * test (works the same locally or on a cloud runner - it's a
     * per-run artifact, not something that needs to persist across
     * runs, so no remote storage needed here).
     */
    @Keyword
    def captureScreenshot(String screenshotName) {

        if (!screenshotName || !screenshotName.trim()) {

            KeywordUtil.markFailed(
                "captureScreenshot() called with a blank screenshotName."
            )

            return
        }

        String path = "${BASE_DIR}/${screenshotName.trim()}.png"

        try {

            ensureParentDirectoryExists(path)

            WebUI.takeFullPageScreenshot(path)

            KeywordUtil.logInfo("Visual screenshot captured: ${path}")

        } catch (Exception e) {

            KeywordUtil.markFailed(
                "Unable to capture screenshot '${screenshotName}': " + e.getMessage()
            )
        }
    }


    /*
     * ============================================================
     * CAPTURE COMPONENT SCREENSHOT
     * ============================================================
     */
    @Keyword
    def captureComponent(String screenshotName, String xpath, int timeoutInSeconds = 10) {

        if (!screenshotName || !screenshotName.trim()) {

            KeywordUtil.markFailed(
                "captureComponent() called with a blank screenshotName."
            )

            return
        }

        if (!xpath || !xpath.trim()) {

            KeywordUtil.markFailed(
                "captureComponent() called with a blank xpath."
            )

            return
        }

        WebDriver driver = null

        try {
            driver = DriverFactory.getWebDriver()
        } catch (Exception e) {

            KeywordUtil.markFailed(
                "Unable to get WebDriver: " + e.getMessage()
            )

            return
        }

        WebElement element = null

        try {

            element = driver.findElement(By.xpath(xpath))

        } catch (NoSuchElementException e) {

            KeywordUtil.markFailed(
                "captureComponent() failed - no element found for xpath: ${xpath}"
            )

            return

        } catch (Exception e) {

            KeywordUtil.markFailed(
                "captureComponent() failed while locating element: " + e.getMessage()
            )

            return
        }


        try {

            JavascriptExecutor js = (JavascriptExecutor) driver

            js.executeScript(
                """
                arguments[0].scrollIntoView({
                    behavior: 'instant',
                    block: 'center',
                    inline: 'center'
                });
                """,
                element
            )

        } catch (Exception e) {

            KeywordUtil.logInfo(
                "Unable to scroll element into view: " + e.getMessage()
            )
        }


        boolean visible = false

        long endTime = System.currentTimeMillis() + (timeoutInSeconds * 1000L)

        while (System.currentTimeMillis() < endTime) {

            try {

                if (element.isDisplayed()) {
                    visible = true
                    break
                }

            } catch (Exception ignored) {
            }

            Thread.sleep(300)
        }

        if (!visible) {

            KeywordUtil.markFailed(
                "captureComponent() failed - element for xpath '${xpath}' never became " +
                "visible within ${timeoutInSeconds}s."
            )

            return
        }


        String path = "${BASE_DIR}/${screenshotName.trim()}.png"

        try {

            ensureParentDirectoryExists(path)

            File screenshotFile =
                ((TakesScreenshot) element).getScreenshotAs(OutputType.FILE)

            FileUtils.copyFile(screenshotFile, new File(path))

            KeywordUtil.logInfo("Component screenshot captured: ${path}")

        } catch (Exception e) {

            KeywordUtil.markFailed(
                "Unable to capture component screenshot '${screenshotName}': " + e.getMessage()
            )
        }
    }


    /*
     * ============================================================
     * COMPARE WITH BASELINE
     * ============================================================
     *
     * Works locally AND on cloud/ephemeral runners:
     *
     *   - If baselineStorageUrl is NOT configured: baselines are
     *     read/written on local disk under Screenshots/EVAA/baseline/
     *     - fine for local Katalon Studio/KRE runs on a machine
     *     whose disk persists between runs.
     *
     *   - If baselineStorageUrl IS configured (system property
     *     "baselineStorageUrl" or env var BASELINE_STORAGE_URL):
     *     baselines are fetched/stored over HTTP against that URL
     *     instead, so they survive across runs on ephemeral cloud
     *     execution machines (TestCloud, containerized CI, etc)
     *     where local disk does not persist between runs.
     *
     *     Expected contract at that URL:
     *       GET  {baselineStorageUrl}/{name}.png -> 200 + PNG bytes,
     *            or 404 if no baseline exists yet
     *       PUT  {baselineStorageUrl}/{name}.png with raw PNG bytes
     *            as the body -> 2xx on success
     *
     *     Optional auth header via system property
     *     "baselineStorageAuthHeader" or env var
     *     BASELINE_STORAGE_AUTH_HEADER, formatted as "Header: value"
     *     e.g. "Authorization: Bearer abc123".
     *
     * Everything else (pixel comparison, diff image, tolerance,
     * ignoreRegions) behaves identically in both modes.
     */
    @Keyword
    def compareWithBaseline(
            String screenshotName,
            double maxDiffPercent = 0.5,
            int pixelTolerance = 30,
            List<Map> ignoreRegions = []) {

        if (!screenshotName || !screenshotName.trim()) {

            KeywordUtil.markFailed(
                "compareWithBaseline() called with a blank screenshotName."
            )

            return
        }

        String name = screenshotName.trim()

        String actualPath = "${BASE_DIR}/${name}.png"
        String diffPath = "${DIFF_DIR}/${name}_diff.png"

        File actualFile = new File(actualPath)

        if (!actualFile.exists()) {

            KeywordUtil.markFailed(
                "compareWithBaseline() failed - no captured screenshot found at " +
                "'${actualPath}'. Call captureScreenshot()/captureComponent() with the " +
                "same screenshotName first."
            )

            return
        }

        String storageUrl = resolveBaselineStorageUrl()
        String storageDescription =
            storageUrl ? "remote storage (${storageUrl})" : "local disk (${LOCAL_BASELINE_DIR})"

        byte[] baselineBytes = readBaseline(storageUrl, name)

        if (baselineBytes == null) {

            byte[] actualBytes = null

            try {
                actualBytes = actualFile.getBytes()
            } catch (Exception e) {

                KeywordUtil.markFailed(
                    "Unable to read current capture for '${name}': " + e.getMessage()
                )

                return
            }

            boolean saved = writeBaseline(storageUrl, name, actualBytes)

            if (saved) {

                KeywordUtil.logInfo(
                    "No baseline existed for '${name}' - current capture saved as the " +
                    "new baseline to ${storageDescription}."
                )

                KeywordUtil.markPassed(
                    "Baseline created for '${name}'. Nothing to compare on this run."
                )

            } else {

                KeywordUtil.markFailed(
                    "Unable to create baseline for '${name}' on ${storageDescription}."
                )
            }

            return
        }


        BufferedImage baselineImage = null
        BufferedImage actualImage = null

        try {

            baselineImage = ImageIO.read(new ByteArrayInputStream(baselineBytes))
            actualImage = ImageIO.read(actualFile)

        } catch (Exception e) {

            KeywordUtil.markFailed(
                "Unable to decode image(s) for comparison: " + e.getMessage()
            )

            return
        }

        if (baselineImage == null || actualImage == null) {

            KeywordUtil.markFailed(
                "Unable to decode baseline or actual image for '${name}' - data may be " +
                "corrupt or not a valid PNG."
            )

            return
        }


        int baselineWidth = baselineImage.getWidth()
        int baselineHeight = baselineImage.getHeight()
        int actualWidth = actualImage.getWidth()
        int actualHeight = actualImage.getHeight()

        if (baselineWidth != actualWidth || baselineHeight != actualHeight) {

            KeywordUtil.logInfo(
                "Baseline size: ${baselineWidth}x${baselineHeight} | " +
                "Actual size: ${actualWidth}x${actualHeight}"
            )

            KeywordUtil.markFailed(
                "compareWithBaseline() failed for '${name}' - image dimensions changed " +
                "(baseline ${baselineWidth}x${baselineHeight} vs actual " +
                "${actualWidth}x${actualHeight}). Review manually, then call " +
                "updateBaseline() if the new size is expected."
            )

            return
        }


        int width = actualWidth
        int height = actualHeight

        BufferedImage diffImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

        long totalPixels = (long) width * (long) height
        long differingPixels = 0

        for (int y = 0; y < height; y++) {

            for (int x = 0; x < width; x++) {

                if (isInIgnoreRegion(x, y, ignoreRegions)) {

                    diffImage.setRGB(x, y, baselineImage.getRGB(x, y))
                    continue
                }

                int baselineRgb = baselineImage.getRGB(x, y)
                int actualRgb = actualImage.getRGB(x, y)

                if (pixelsDiffer(baselineRgb, actualRgb, pixelTolerance)) {

                    differingPixels++
                    diffImage.setRGB(x, y, 0xFFFF0000)

                } else {

                    diffImage.setRGB(x, y, dim(actualRgb))
                }
            }
        }

        double diffPercent =
            totalPixels > 0 ? (differingPixels * 100.0 / totalPixels) : 0.0


        try {

            ensureParentDirectoryExists(diffPath)
            ImageIO.write(diffImage, "png", new File(diffPath))

        } catch (Exception e) {

            KeywordUtil.logInfo(
                "Unable to write diff image (comparison still completed): " + e.getMessage()
            )
        }


        KeywordUtil.logInfo("Baseline source: ${storageDescription}")
        KeywordUtil.logInfo("Actual         : ${actualPath}")
        KeywordUtil.logInfo("Diff image     : ${diffPath}")
        KeywordUtil.logInfo(
            "Pixels differing: ${differingPixels} / ${totalPixels} " +
            "(${String.format('%.3f', diffPercent)}%)"
        )


        if (diffPercent > maxDiffPercent) {

            KeywordUtil.markFailed(
                "Visual regression detected for '${name}': ${String.format('%.3f', diffPercent)}% " +
                "of pixels differ (threshold ${maxDiffPercent}%). See diff image: ${diffPath}"
            )

        } else {

            KeywordUtil.markPassed(
                "Visual comparison passed for '${name}': ${String.format('%.3f', diffPercent)}% " +
                "pixel difference (threshold ${maxDiffPercent}%)."
            )
        }
    }


    /*
     * ============================================================
     * UPDATE BASELINE
     * ============================================================
     * Explicitly accept the current capture as the new baseline -
     * writes to whichever storage is configured (local or remote),
     * same rule as compareWithBaseline().
     */
    @Keyword
    def updateBaseline(String screenshotName) {

        if (!screenshotName || !screenshotName.trim()) {

            KeywordUtil.markFailed(
                "updateBaseline() called with a blank screenshotName."
            )

            return
        }

        String name = screenshotName.trim()
        String actualPath = "${BASE_DIR}/${name}.png"

        File actualFile = new File(actualPath)

        if (!actualFile.exists()) {

            KeywordUtil.markFailed(
                "updateBaseline() failed - no captured screenshot found at '${actualPath}'."
            )

            return
        }

        String storageUrl = resolveBaselineStorageUrl()
        String storageDescription =
            storageUrl ? "remote storage (${storageUrl})" : "local disk (${LOCAL_BASELINE_DIR})"

        try {

            byte[] actualBytes = actualFile.getBytes()

            boolean saved = writeBaseline(storageUrl, name, actualBytes)

            if (saved) {

                KeywordUtil.markPassed(
                    "Baseline for '${name}' updated on ${storageDescription}."
                )

            } else {

                KeywordUtil.markFailed(
                    "Unable to update baseline for '${name}' on ${storageDescription}."
                )
            }

        } catch (Exception e) {

            KeywordUtil.markFailed(
                "Unable to update baseline for '${name}': " + e.getMessage()
            )
        }
    }


    /*
     * ============================================================
     * BASELINE STORAGE - LOCAL / REMOTE ABSTRACTION
     * ============================================================
     */

    private String resolveBaselineStorageUrl() {

        String fromProperty = System.getProperty("baselineStorageUrl")

        if (fromProperty && fromProperty.trim()) {
            return fromProperty.trim().replaceAll('/+$', '')
        }

        String fromEnv = System.getenv("BASELINE_STORAGE_URL")

        if (fromEnv && fromEnv.trim()) {
            return fromEnv.trim().replaceAll('/+$', '')
        }

        return null
    }

    private String resolveAuthHeader() {

        String fromProperty = System.getProperty("baselineStorageAuthHeader")

        if (fromProperty && fromProperty.trim()) {
            return fromProperty.trim()
        }

        String fromEnv = System.getenv("BASELINE_STORAGE_AUTH_HEADER")

        if (fromEnv && fromEnv.trim()) {
            return fromEnv.trim()
        }

        return null
    }

    private void applyAuthHeaderIfConfigured(HttpURLConnection connection) {

        String authHeader = resolveAuthHeader()

        if (!authHeader) {
            return
        }

        int idx = authHeader.indexOf(':')

        if (idx > 0) {

            String headerName = authHeader.substring(0, idx).trim()
            String headerValue = authHeader.substring(idx + 1).trim()

            connection.setRequestProperty(headerName, headerValue)
        }
    }

    /*
     * Returns null if no baseline exists yet (local file missing, or
     * remote returned 404) - callers treat that as "first run, bootstrap
     * a new baseline", NOT as an error.
     */
    private byte[] readBaseline(String storageUrl, String name) {

        if (storageUrl) {
            return readBaselineRemote(storageUrl, name)
        }

        return readBaselineLocal(name)
    }

    private byte[] readBaselineLocal(String name) {

        File f = new File("${LOCAL_BASELINE_DIR}/${name}.png")

        if (!f.exists()) {
            return null
        }

        try {
            return f.getBytes()
        } catch (Exception e) {
            KeywordUtil.logInfo("Unable to read local baseline: " + e.getMessage())
            return null
        }
    }

    private byte[] readBaselineRemote(String baseUrl, String name) {

        HttpURLConnection connection = null

        try {

            String url = "${baseUrl}/${name}.png"

            connection = (HttpURLConnection) new URL(url).openConnection()
            connection.setRequestMethod("GET")
            connection.setConnectTimeout(10000)
            connection.setReadTimeout(15000)

            applyAuthHeaderIfConfigured(connection)

            connection.connect()

            int code = connection.getResponseCode()

            if (code == 404) {
                return null
            }

            if (code < 200 || code >= 300) {

                KeywordUtil.logInfo(
                    "Remote baseline GET returned ${code} for ${url} - " +
                    "treating as 'no baseline available'."
                )

                return null
            }

            return connection.getInputStream().getBytes()

        } catch (Exception e) {

            KeywordUtil.logInfo(
                "Unable to fetch remote baseline for '${name}': " + e.getMessage()
            )

            return null

        } finally {

            if (connection != null) {
                connection.disconnect()
            }
        }
    }

    private boolean writeBaseline(String storageUrl, String name, byte[] bytes) {

        if (storageUrl) {
            return writeBaselineRemote(storageUrl, name, bytes)
        }

        return writeBaselineLocal(name, bytes)
    }

    private boolean writeBaselineLocal(String name, byte[] bytes) {

        try {

            String path = "${LOCAL_BASELINE_DIR}/${name}.png"

            ensureParentDirectoryExists(path)

            new File(path).setBytes(bytes)

            return true

        } catch (Exception e) {

            KeywordUtil.logInfo("Unable to write local baseline: " + e.getMessage())
            return false
        }
    }

    private boolean writeBaselineRemote(String baseUrl, String name, byte[] bytes) {

        HttpURLConnection connection = null

        try {

            String url = "${baseUrl}/${name}.png"

            connection = (HttpURLConnection) new URL(url).openConnection()
            connection.setRequestMethod("PUT")
            connection.setDoOutput(true)
            connection.setConnectTimeout(10000)
            connection.setReadTimeout(20000)
            connection.setRequestProperty("Content-Type", "image/png")

            applyAuthHeaderIfConfigured(connection)

            connection.getOutputStream().write(bytes)
            connection.getOutputStream().flush()

            int code = connection.getResponseCode()

            if (code >= 200 && code < 300) {
                return true
            }

            KeywordUtil.logInfo("Remote baseline PUT returned ${code} for ${url}")
            return false

        } catch (Exception e) {

            KeywordUtil.logInfo(
                "Unable to upload remote baseline for '${name}': " + e.getMessage()
            )

            return false

        } finally {

            if (connection != null) {
                connection.disconnect()
            }
        }
    }


    private boolean pixelsDiffer(int rgb1, int rgb2, int tolerance) {

        int r1 = (rgb1 >> 16) & 0xFF
        int g1 = (rgb1 >> 8) & 0xFF
        int b1 = rgb1 & 0xFF

        int r2 = (rgb2 >> 16) & 0xFF
        int g2 = (rgb2 >> 8) & 0xFF
        int b2 = rgb2 & 0xFF

        return Math.abs(r1 - r2) > tolerance ||
               Math.abs(g1 - g2) > tolerance ||
               Math.abs(b1 - b2) > tolerance
    }


    private int dim(int rgb) {

        int a = (rgb >> 24) & 0xFF
        int r = (rgb >> 16) & 0xFF
        int g = (rgb >> 8) & 0xFF
        int b = rgb & 0xFF

        r = (int) (r * 0.6)
        g = (int) (g * 0.6)
        b = (int) (b * 0.6)

        return (a << 24) | (r << 16) | (g << 8) | b
    }


    private boolean isInIgnoreRegion(int x, int y, List<Map> ignoreRegions) {

        if (!ignoreRegions) {
            return false
        }

        for (Map region : ignoreRegions) {

            int rx = (region.get("x") ?: 0) as int
            int ry = (region.get("y") ?: 0) as int
            int rw = (region.get("width") ?: 0) as int
            int rh = (region.get("height") ?: 0) as int

            if (x >= rx && x < (rx + rw) && y >= ry && y < (ry + rh)) {
                return true
            }
        }

        return false
    }


    private void ensureParentDirectoryExists(String path) {

        File file = new File(path)
        File parent = file.getParentFile()

        if (parent != null && !parent.exists()) {
            parent.mkdirs()
        }
    }
}