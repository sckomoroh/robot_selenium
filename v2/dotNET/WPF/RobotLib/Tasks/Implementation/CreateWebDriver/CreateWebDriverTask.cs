using System.Globalization;
using Robot.Tasks.Common;
using WebDriver.Wrapper;

namespace Robot.Tasks.Implementation.CreateWebDriver
{
    public class CreateWebDriverTask : BaseTask<CreateWebDriverInputData, CreateWebDriverOutputData>
    {
        public CreateWebDriverTask(CreateWebDriverInputData inputTaskData) 
            : base(inputTaskData)
        {
            DisplayName = "Creating WEB driver";
        }

        protected override bool InternalRun()
        {
            ChangeState(TaskState.Running);
            ChangePhase("Creating WEB driver");

            var webDriver = WebDriverFactory.Create(_inputTaskData.DriverType);
            TaskData = new CreateWebDriverOutputData
            {
                WebDriver = webDriver
            };

            ChangePhase(string.Format(CultureInfo.InvariantCulture, "Navigate to the '{0}'", _inputTaskData.StartUrl));
            webDriver.Navigate(_inputTaskData.StartUrl, true);

            return true;
        }
    }
}