using System.Collections.Generic;
using Robot.Tasks.Common;
using WebDriver.Wrapper;

namespace Robot.Tasks.Implementation.CollectActivities
{
    public class CollectActivitiesTask : WebDriverTask<InputTaskData, CollectActivitiesTaskOutputData>
    {
        public CollectActivitiesTask(IWebDriver webDriver, InputTaskData inputTaskData) 
            : base(webDriver, inputTaskData)
        {
            DisplayName = "Collect activities";
        }

        protected override bool InternalRun()
        {
            ChangePhase("Collect activities");

            var outputData = new List<ActivityItem>();

            var webElements = WebDriver.GetElementsByClassName("rf-project-cover__title js-project-cover-title-link");

            foreach (var webElement in webElements)
            {
                var webReference = webElement.GetAttribute("href");
                var name = webElement.GetText();

                outputData.Add(new ActivityItem
                {
                    Name = name,
                    WebReference = webReference
                });
            }

            TaskData = new CollectActivitiesTaskOutputData
            {
                Activities = outputData
            };

            return true;
        }
    }
}
