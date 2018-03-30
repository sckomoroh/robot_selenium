using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Threading;
using OpenQA.Selenium;
using OpenQA.Selenium.Support.UI;
using ISeleniumWebDriver = OpenQA.Selenium.IWebDriver;
using ISeleniumWebElement = OpenQA.Selenium.IWebElement;

namespace WebDriver.Wrapper.Internal
{
    internal class WebDriverImpl : IWebDriver
    {
        private readonly ISeleniumWebDriver _webDriver;
        private readonly IJavaScriptExecutor _javaScriptExecutor;
        private readonly WebDriverWait _webDriverWait;

        public WebDriverImpl(ISeleniumWebDriver webDriver, TimeSpan timeout)
        {
            _webDriver = webDriver;
            _javaScriptExecutor = _webDriver as IJavaScriptExecutor;
            _webDriverWait = new WebDriverWait(_webDriver, timeout);
        }

        public void Navigate(string url, bool waitCompletion)
        {
            _webDriver.Url = url;

            if (waitCompletion)
            {
                WaitPageLoadCompleted();
            }
        }

        public IEnumerable<IWebElement> GetElementsByClassName(string className)
        {
            var resultElements = new List<IWebElement>();

            var javascriptBody = string.Format(
                CultureInfo.InvariantCulture,
                "return document.getElementsByClassName('{0}');", 
                className);

            var webElements = ExecuteJavaScript<IEnumerable<object>>(javascriptBody);

            foreach (var webElement in webElements)
            {
                resultElements.Add(new WebElementImpl((ISeleniumWebElement)webElement, _javaScriptExecutor));
            }

            return resultElements;
        }

        public IWebElement GetElementById(string elementId)
        {
            var javascriptBody = string.Format(
                CultureInfo.InvariantCulture,
                "return document.getElementById('{0}');", 
                elementId);

            var webElement = ExecuteJavaScript<ISeleniumWebElement>(javascriptBody);

            return new WebElementImpl(webElement, _javaScriptExecutor);
        }

        public IEnumerable<IWebElement> GetElementsByClassName(string className, TimeSpan timeout, int retryCount)
        {
            var resultElements = new List<IWebElement>();

            var javascriptBody = string.Format(
                CultureInfo.InvariantCulture,
                "return document.getElementsByClassName('{0}');",
                className);

            var webElements = ExecuteJavaScript<IEnumerable<object>>(javascriptBody);

            while (retryCount-- > 0 && !webElements.Any())
            {
                Thread.Sleep(timeout);
            }

            foreach (var webElement in webElements)
            {
                resultElements.Add(new WebElementImpl((ISeleniumWebElement)webElement, _javaScriptExecutor));
            }

            return resultElements;
        }

        public IWebElement GetElementById(string elementId, TimeSpan timeout, int retryCount)
        {
            var javascriptBody = string.Format(
                CultureInfo.InvariantCulture,
                "return document.getElementById('{0}');",
                elementId);

            var webElement = ExecuteJavaScript<ISeleniumWebElement>(javascriptBody);

            while (retryCount-- > 0 && webElement == null)
            {
                Thread.Sleep(timeout);
            }

            return new WebElementImpl(webElement, _javaScriptExecutor);
        }

        public void WaitPageLoadCompleted()
        {
            _webDriverWait.Until(IsPageLoaded);
        }

        public void Quit()
        {
            _webDriver.Quit();
        }

        public object ExecuteJavaScript(string scriptBody)
        {
            return _javaScriptExecutor.ExecuteScript(scriptBody);
        }

        public TResult ExecuteJavaScript<TResult>(string scriptBody)
        {
            return (TResult)_javaScriptExecutor.ExecuteScript(scriptBody);
        }

        private bool IsPageLoaded(ISeleniumWebDriver webDriver)
        {
            var jQueryType = (string)_javaScriptExecutor.ExecuteScript("return typeof jQuery");
            if (jQueryType.Equals("undefined"))
            {
                return false;
            }

            var documentState = (string)_javaScriptExecutor.ExecuteScript("return document.readyState");
            if (!documentState.Equals("complete"))
            {
                return false;
            }

            var jQueryActive = (long)_javaScriptExecutor.ExecuteScript("return jQuery.active");
            if (jQueryActive != 0)
            {
                return false;
            }

            return true;
        }
    }
}