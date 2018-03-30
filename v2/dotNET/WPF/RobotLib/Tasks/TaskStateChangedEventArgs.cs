using System;

namespace Robot.Tasks
{
    public class TaskStateChangedEventArgs : EventArgs
    {
        public TaskState State { get; set; }
    }
}
