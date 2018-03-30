using System.Linq;
using System.Threading;
using Robot.Tasks.Common;
using WebDriver.Wrapper;

namespace Robot.Tasks.Implementation.Comment
{
    public class CommentTask : ActionTask
    {
        private readonly CommentInputData _commentInputData;

        public CommentTask(IWebDriver webDriver, ActionTaskInputData inputTaskData) 
            : base(webDriver, inputTaskData)
        {
            _commentInputData = inputTaskData as CommentInputData;
        }

        protected override bool IsActionWasPerformed()
        {
            var commentItems = WebDriver.GetElementsByClassName("rf-avatar js-avatar js-mini-profile");

            return commentItems.Any(x => _commentInputData.UserWebReference.Equals(x.GetAttribute("href")));
        }

        protected override void PerformAction()
        {
            var commentInputElement = WebDriver.GetElementById("comment");
            var commentSubmitButton = WebDriver.GetElementsByClassName("form-button js-rf-button rf-button js-submit").FirstOrDefault();

            commentInputElement.SetElementValue(_commentInputData.Comment);

            commentSubmitButton.Click();
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