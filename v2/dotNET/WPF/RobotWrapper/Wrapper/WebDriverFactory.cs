using System;
using OpenQA.Selenium.Chrome;
using OpenQA.Selenium.Firefox;
using WebDriver.Wrapper.Internal;

namespace WebDriver.Wrapper
{
    public static class WebDriverFactory
    {
        public static IWebDriver Create(WebDriverType webDriverType)
        {
            switch (webDriverType)
            {
                case WebDriverType.Chrome:
                    return CreateChromeWebDriver();
                case WebDriverType.Firefox:
                    return CreateFireFoxWebDriver();
            }

            throw new InvalidOperationException("Invalid driver type specified");
        }

        private static IWebDriver CreateChromeWebDriver()
        {
            var webDriver = new ChromeDriver();

            return new WebDriverImpl(webDriver, TimeSpan.FromSeconds(30));
        }

        private static IWebDriver CreateFireFoxWebDriver()
        {
            var webDriver = new FirefoxDriver();

            return new WebDriverImpl(webDriver, TimeSpan.FromSeconds(30));
        }
    }
}
