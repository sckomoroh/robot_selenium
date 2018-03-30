using WebDriver.Wrapper;

namespace Robot.Tasks.Implementation.CreateWebDriver
{
    public class CreateWebDriverInputData : InputTaskData
    {
        public WebDriverType DriverType { get; set; }

        public string StartUrl { get; set; }
    }
}
