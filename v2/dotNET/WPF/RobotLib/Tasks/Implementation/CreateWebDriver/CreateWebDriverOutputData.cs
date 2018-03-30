using WebDriver.Wrapper;

namespace Robot.Tasks.Implementation.CreateWebDriver
{
    public class CreateWebDriverOutputData : OutputTaskData
    {
        public IWebDriver WebDriver { get; set; }
    }
}
