using System;

namespace Robot.Tasks
{
    public class TaskPhaseChangedEventArgs : EventArgs
    {
        public string Phase { get; set; }
    }
}
