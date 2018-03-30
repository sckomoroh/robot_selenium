using System;
using Robot.Tasks;

namespace Robot
{
    public interface IGeneralTask
    {
        event EventHandler<TaskPhaseChangedEventArgs> PhaseChanged;

        event EventHandler<TaskStateChangedEventArgs> StateChanged;

        TaskState State { get; }

        string DisplayName { get; }

        string Phase { get; }

        Exception Error { get; }

        void Run();
    }
}