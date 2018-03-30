using System.Globalization;
using WebDriver.Wrapper;

namespace Robot.Tasks.Common
{
    public abstract class ActionTask : WebDriverTask<ActionTaskInputData, OutputTaskData>
    {
        protected ActionTask(IWebDriver webDriver, ActionTaskInputData inputTaskData) 
            : base(webDriver, inputTaskData)
        {
        }

        protected override bool InternalRun()
        {
            ChangePhase(string.Format(CultureInfo.InvariantCulture, "Check {0}", _inputTaskData.ActionName));
            if (IsActionWasPerformed())
            {
                ChangePhase(string.Format(CultureInfo.InvariantCulture, "Was already {0}", _inputTaskData.ActionName));
                return false;
            }

            ChangePhase(string.Format(CultureInfo.InvariantCulture, "Perform {0}", _inputTaskData.ActionName));
            PerformAction();

            ChangePhase(string.Format(CultureInfo.InvariantCulture, "Wait {0} completion", _inputTaskData.ActionName));
            WaitActionCompletion();

            return true;
        }

        protected abstract bool IsActionWasPerformed();

        protected abstract void PerformAction();

        protected abstract void WaitActionCompletion();
    }
}
