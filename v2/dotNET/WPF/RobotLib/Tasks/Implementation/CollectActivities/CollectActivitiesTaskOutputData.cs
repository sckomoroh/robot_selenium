using System.Collections.Generic;

namespace Robot.Tasks.Implementation.CollectActivities
{
    public class CollectActivitiesTaskOutputData : OutputTaskData
    {
        public IEnumerable<ActivityItem> Activities { get; set; }
    }
}