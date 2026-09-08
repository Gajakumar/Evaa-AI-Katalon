package customkeywords

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import org.openqa.selenium.WebElement

class InputHelper {

    @Keyword
    def clearInput(TestObject to) {

        WebElement element = WebUI.findWebElement(to, 10)

        WebUI.executeJavaScript("""
            const input = arguments[0];

            const setter = Object.getOwnPropertyDescriptor(
                HTMLInputElement.prototype,
                'value'
            ).set;

            setter.call(input, '');

            input.dispatchEvent(new Event('input', { bubbles: true }));
            input.dispatchEvent(new Event('change', { bubbles: true }));
        """, Arrays.asList(element))
    }

    @Keyword
    def clearAndType(TestObject to, String value) {

        clearInput(to)
        WebUI.setText(to, value)
    }
}