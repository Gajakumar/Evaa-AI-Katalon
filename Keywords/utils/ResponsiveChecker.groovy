//package utils
//
//import com.kms.katalon.core.annotation.Keyword
//import com.kms.katalon.core.util.KeywordUtil
//import com.kms.katalon.core.webui.driver.DriverFactory
//import org.openqa.selenium.WebDriver
//
//import org.openqa.selenium.JavascriptExecutor
//
//class ResponsiveChecker {
//
//    @Keyword
//    def verifyResponsiveLayout() {
//
//        WebDriver driver = DriverFactory.getWebDriver()
//JavascriptExecutor js = (JavascriptExecutor) driver
//
//Map result = (Map) js.executeScript("""
//return (function () {
//
//    var body = document.body;
//    var html = document.documentElement;
//
//    var viewportWidth = window.innerWidth;
//    var viewportHeight = window.innerHeight;
//
//    var scrollWidth = Math.max(body.scrollWidth, html.scrollWidth);
//
//    var horizontalOverflow = scrollWidth > viewportWidth + 2;
//
//    var overflowingElements = [];
//
//    document.querySelectorAll('body *').forEach(function(el){
//
//        var rect = el.getBoundingClientRect();
//
//        if(rect.width > viewportWidth + 2){
//
//            overflowingElements.push({
//                tag : el.tagName,
//                width : Math.round(rect.width),
//                text : (el.innerText || '').substring(0,80)
//            });
//        }
//    });
//
//    return {
//        viewportWidth : viewportWidth,
//        viewportHeight : viewportHeight,
//        scrollWidth : scrollWidth,
//        horizontalOverflow : horizontalOverflow,
//        overflowingElements : overflowingElements
//    };
//
//})();
//""")
//
//        KeywordUtil.logInfo(
//                "Viewport Width  : ${result.viewportWidth}"
//        )
//
//        KeywordUtil.logInfo(
//                "Viewport Height : ${result.viewportHeight}"
//        )
//
//        KeywordUtil.logInfo(
//                "Document Width  : ${result.scrollWidth}"
//        )
//
//        if (result.horizontalOverflow) {
//
//            KeywordUtil.markFailed(
//                    "Horizontal scrolling detected. " +
//                    "Viewport=${result.viewportWidth}, " +
//                    "Document=${result.scrollWidth}"
//            )
//
//        } else {
//
//            KeywordUtil.markPassed(
//                    "No horizontal scrolling detected."
//            )
//        }
//
//        List overflowing =
//                result.overflowingElements as List
//
//        if (!overflowing.isEmpty()) {
//
//            KeywordUtil.logInfo(
//                    "Elements wider than viewport: " +
//                    overflowing.size()
//            )
//
//            List<Map> elements = (List<Map>) result.get("overflowingElements")
//
//elements.each { Map element ->
//
//    KeywordUtil.logInfo(
//        "${element.get('tag')} | " +
//        "${element.get('width')}px | " +
//        "${element.get('text')}"
//    )
//}
//
//            KeywordUtil.markFailed(
//                    "Responsive layout contains " +
//                    "${overflowing.size()} overflowing element(s)."
//            )
//
//        } else {
//
//            KeywordUtil.logInfo(
//                    "No elements wider than viewport."
//            )
//        }
//
//        KeywordUtil.logInfo(
//                "=============================================="
//        )
//    }
//}

package utils

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.driver.DriverFactory

import org.openqa.selenium.WebDriver
import org.openqa.selenium.JavascriptExecutor


class ResponsiveChecker {

	/*
	 * ============================================================
	 * VERIFY RESPONSIVE LAYOUT
	 * ============================================================
	 *
	 * settleMillis (default 400): short pause before measuring, to
	 * let CSS transitions/animations (popup slide-in/fade-in etc)
	 * finish before we read element sizes.
	 *
	 * ignoreSelectors: optional list of CSS selectors for elements
	 * you know are intentionally wider than the viewport (e.g. a
	 * decorative marquee/ticker clipped by an ancestor with
	 * overflow:hidden) - these are excluded from the overflow check.
	 */
	@Keyword
	def verifyResponsiveLayout(int settleMillis = 400, List<String> ignoreSelectors = []) {

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

		JavascriptExecutor js = (JavascriptExecutor) driver

		if (settleMillis > 0) {
			Thread.sleep(settleMillis)
		}

		Map result = null

		try {

			result = (Map) js.executeScript(
				"""
                return (function (ignoreSelectors) {

                    var body = document.body;
                    var html = document.documentElement;
                    var viewportWidth = window.innerWidth;
                    var viewportHeight = window.innerHeight;
                    var scrollWidth = Math.max(body.scrollWidth, html.scrollWidth);
                    var horizontalOverflow = scrollWidth > viewportWidth + 2;

                    var overflowingElements = [];

                    document.querySelectorAll('body *').forEach(function(el){

                        for (var i = 0; i < ignoreSelectors.length; i++) {
                            try {
                                if (el.matches(ignoreSelectors[i])) {
                                    return;
                                }
                            } catch (e) {
                                // invalid selector supplied - ignore silently
                            }
                        }

                        var style = window.getComputedStyle(el);

                        if (style.display === 'none' || style.visibility === 'hidden') {
                            return;
                        }

                        var rect = el.getBoundingClientRect();

                        if (rect.width <= viewportWidth + 2) {
                            return;
                        }

                        /*
						 * Only report the TOP-MOST offending element in each
						 * overflow chain. Otherwise a single wide element
						 * causes every ancestor up to <body> to also read as
						 * "overflowing" (block-level width auto-expands to
						 * fit content), producing many duplicate reports for
						 * one root cause.
						 */
                        var parent = el.parentElement;

                        if (parent) {

                            var parentStyle = window.getComputedStyle(parent);

                            if (parentStyle.display !== 'none' &&
                                parentStyle.visibility !== 'hidden') {

                                var parentRect = parent.getBoundingClientRect();

                                if (parentRect.width > viewportWidth + 2) {
                                    return;
                                }
                            }
                        }

                        var selectorHint = el.tagName.toLowerCase();

                        if (el.id) {
                            selectorHint += '#' + el.id;
                        } else if (el.className && typeof el.className === 'string' && el.className.trim()) {
                            selectorHint += '.' + el.className.trim().split(/\\s+/).join('.');
                        }

                        overflowingElements.push({
                            tag : el.tagName,
                            selector : selectorHint,
                            width : Math.round(rect.width),
                            text : (el.innerText || '').substring(0, 80)
                        });
                    });

                    return {
                        viewportWidth : viewportWidth,
                        viewportHeight : viewportHeight,
                        scrollWidth : scrollWidth,
                        horizontalOverflow : horizontalOverflow,
                        overflowingElements : overflowingElements
                    };
                })(arguments[0]);
                """,
				ignoreSelectors ?: []
			)

		} catch (Exception e) {

			KeywordUtil.markFailed(
				"Unable to evaluate responsive layout: " + e.getMessage()
			)

			return
		}

		if (result == null) {

			KeywordUtil.markFailed(
				"Responsive layout check returned no data (script may have been blocked)."
			)

			return
		}

		KeywordUtil.logInfo("Viewport Width  : ${result.viewportWidth}")
		KeywordUtil.logInfo("Viewport Height : ${result.viewportHeight}")
		KeywordUtil.logInfo("Document Width  : ${result.scrollWidth}")

		boolean horizontalOverflow = result.horizontalOverflow as boolean

		List<Map> overflowing = (result.get("overflowingElements") as List<Map>) ?: []

		KeywordUtil.logInfo("Root-cause overflowing elements: ${overflowing.size()}")

		overflowing.each { Map element ->
			KeywordUtil.logInfo(
				"${element.get('selector')} | ${element.get('width')}px | ${element.get('text')}"
			)
		}

		KeywordUtil.logInfo(
			"=============================================="
		)


		/*
		 * Single consolidated verdict instead of two separate
		 * markFailed() calls that could overwrite each other.
		 */

		if (horizontalOverflow || !overflowing.isEmpty()) {

			List<String> reasons = []

			if (horizontalOverflow) {
				reasons.add(
					"horizontal scrolling detected (viewport=${result.viewportWidth}, " +
					"document=${result.scrollWidth})"
				)
			}

			if (!overflowing.isEmpty()) {
				reasons.add(
					"${overflowing.size()} element(s) wider than the viewport"
				)
			}

			KeywordUtil.markFailed(
				"Responsive layout issue(s): " + reasons.join("; ") + "."
			)

		} else {

			KeywordUtil.markPassed(
				"No horizontal scrolling or overflowing elements detected."
			)
		}
	}
}