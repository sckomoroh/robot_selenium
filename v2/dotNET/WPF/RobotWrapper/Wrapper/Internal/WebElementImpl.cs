using System.Threading;
using OpenQA.Selenium;

using ISeleniumWebElement = OpenQA.Selenium.IWebElement;

namespace WebDriver.Wrapper.Internal
{
    internal class WebElementImpl : IWebElement
    {
        private readonly ISeleniumWebElement _webElement;
        private readonly IJavaScriptExecutor _javascriptExecutor;

        public WebElementImpl(ISeleniumWebElement webElement, IJavaScriptExecutor javascriptExecutor)
        {
            _webElement = webElement;
            _javascriptExecutor = javascriptExecutor;
        }

        public void Click()
        {
            _javascriptExecutor.ExecuteScript("arguments[0].click(); return true;", _webElement);
            Thread.Sleep(1000);
        }

        public string GetAttribute(string attributeName)
        {
            return _webElement.GetAttribute(attributeName);
        }

        public string GetText()
        {
            return _webElement.Text;
        }

        public void SetElementValue(string value)
        {
            _javascriptExecutor.ExecuteScript("arguments[0].value = arguments[1]; return true;", _webElement, value);
        }

        public bool IsVisible()
        {
            return _webElement.Displayed;
        }
    }
}