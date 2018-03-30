using System;
using System.Collections.Generic;

namespace WebDriver.Wrapper
{
    public interface IWebDriver
    {
        void Navigate(string url, bool waitCompletion);

        IEnumerable<IWebElement> GetElementsByClassName(string className);

        IWebElement GetElementById(string elementId);

        IEnumerable<IWebElement> GetElementsByClassName(string className, TimeSpan timeout, int retryCount);

        IWebElement GetElementById(string elementId, TimeSpan timeout, int retryCount);

        void WaitPageLoadCompleted();

        void Quit();

        object ExecuteJavaScript(string scriptBody);

        T ExecuteJavaScript<T>(string scriptBody);
    }
}