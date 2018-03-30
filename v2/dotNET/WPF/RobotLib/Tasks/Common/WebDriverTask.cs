using WebDriver.Wrapper;

namespace Robot.Tasks.Common
{
    public abstract class WebDriverTask<TInputTaskData, TOutpuTaskData> : BaseTask<TInputTaskData, TOutpuTaskData>
        where TInputTaskData : InputTaskData
        where TOutpuTaskData : OutputTaskData
    {
        protected WebDriverTask(IWebDriver webDriver, TInputTaskData inputTaskData) 
            : base(inputTaskData)
        {
            WebDriver = webDriver;
        }

        public IWebDriver WebDriver { get; }

        protected override void BeforeRun()
        {
            ChangePhase("Wait for page completed");
            WebDriver.WaitPageLoadCompleted();
        }
    }
}