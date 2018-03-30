using System.Collections.Generic;
using System.Linq;
using System.Threading;
using Robot.Tasks.Common;
using WebDriver.Wrapper;

namespace Robot.Tasks.Implementation.Follow
{
    public class FollowTask : ActionTask
    {
        public FollowTask(IWebDriver webDriver, ActionTaskInputData inputTaskData) 
            : base(webDriver, inputTaskData)
        {
        }

        protected override bool IsActionWasPerformed()
        {
            var isFollowing = false;
            var retryCount = 0;
            do
            {
                var targetElements = WebDriver.ExecuteJavaScript<IEnumerable<object>>("return document.getElementsByClassName('following');");

                isFollowing = targetElements.Any();
                if (isFollowing == false)
                {
                    Thread.Sleep(1000);
                }
            }
            while (retryCount++ < 5 && isFollowing == false);

            return isFollowing;
        }

        protected override void PerformAction()
        {
            var targetElement = WebDriver.GetElementsByClassName("qa-follow-button-container").FirstOrDefault();
            targetElement.Click();
        }

        protected override void WaitActionCompletion()
        {
            while (!IsActionWasPerformed())
            {
                Thread.Sleep(500);
            }
        }
    }
}