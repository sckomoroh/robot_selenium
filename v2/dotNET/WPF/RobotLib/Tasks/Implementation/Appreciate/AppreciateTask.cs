using System.Linq;
using System.Threading;
using Robot.Tasks.Common;
using WebDriver.Wrapper;

namespace Robot.Tasks.Implementation.Appreciate
{
    public class AppreciateTask : ActionTask
    {
        public AppreciateTask(IWebDriver webDriver, ActionTaskInputData inputTaskData) 
            : base(webDriver, inputTaskData)
        {
        }

        protected override bool IsActionWasPerformed()
        {
            var isAppreciate = false;
            var retryCount = 0;

            do
            {
                var targetElements = WebDriver.GetElementsByClassName("rf-appreciation--date");

                isAppreciate = targetElements.Any(x => x.IsVisible());
                if (isAppreciate == false)
                {
                        Thread.Sleep(1000);
                }
            }
            while (retryCount++ < 5 && isAppreciate == false);

            return isAppreciate;
        }

        protected override void PerformAction()
        {
            var targetElement = WebDriver.GetElementsByClassName("appreciation-button js-appreciate js-adobe-analytics can-unappreciate").FirstOrDefault();
            targetElement.Click();
        }

        protected override void WaitActionCompletion()
        {
            var isAppreciate = false;
            var retryCount = 0;

            do
            {
                var targetElements = WebDriver.GetElementsByClassName("appreciation-button js-appreciate js-adobe-analytics can-unappreciate thanks");

                isAppreciate = targetElements.Any();
                if (isAppreciate == false)
                {
                    Thread.Sleep(500);
                }
            }
            while (retryCount++ < 5 && isAppreciate == false);
        }
    }
}