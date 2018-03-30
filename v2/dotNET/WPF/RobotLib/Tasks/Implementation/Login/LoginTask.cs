using System;
using System.Linq;
using System.Threading;
using Robot.Tasks.Common;
using WebDriver.Wrapper;

namespace Robot.Tasks.Implementation.Login
{
    public class LoginTask : WebDriverTask<LoginInputData, LoginOutputData>
    {
        public LoginTask(IWebDriver webDriver, LoginInputData inputTaskData) 
            : base(webDriver, inputTaskData)
        {
            DisplayName = "Login";
        }

        protected override bool InternalRun()
        {
            NavigateToLoginPage();
            SubmitCredentials();
            FindUserWebreference();

            return true;
        }

        private void NavigateToLoginPage()
        {
            ChangePhase("Navigate to the login page");
            var webElement = WebDriver.GetElementsByClassName("rf-primary-nav__link js-adobeid-signin qa-adobeid-signin").FirstOrDefault();
            webElement.Click();

            Thread.Sleep(1000);

            WebDriver.WaitPageLoadCompleted();
        }

        private void SubmitCredentials()
        {
            ChangePhase("Submit credentials");

            var submitElement = WebDriver.GetElementById("sign_in", TimeSpan.FromMilliseconds(500), 5);
            var usernameElement = WebDriver.GetElementById("adobeid_username", TimeSpan.FromMilliseconds(500), 5);
            var passwordElement = WebDriver.GetElementById("adobeid_password", TimeSpan.FromMilliseconds(500), 5);

            usernameElement.SetElementValue(_inputTaskData.UserName);
            passwordElement.SetElementValue(_inputTaskData.Password);

            var passwordValue = passwordElement.GetAttribute("value");
            while (string.IsNullOrEmpty(passwordValue))
            {
                passwordElement = WebDriver.GetElementById("adobeid_password", TimeSpan.FromMilliseconds(500), 5);
                passwordElement.SetElementValue(_inputTaskData.Password);
                Thread.Sleep(1000);
                passwordValue = passwordElement.GetAttribute("value");
            }

            submitElement.Click();

            Thread.Sleep(1000);

            WebDriver.WaitPageLoadCompleted();
        }

        private void FindUserWebreference()
        {
            ChangePhase("Find user WEB reference");

            var targetElement = WebDriver.GetElementsByClassName("qa-nav-link-image", TimeSpan.FromMilliseconds(500), 5).FirstOrDefault();
            var userWebReference = targetElement.GetAttribute("href");

            TaskData = new LoginOutputData
            {
                UserWebReference = userWebReference
            };
        }
    }
}