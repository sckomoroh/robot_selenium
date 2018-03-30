using System;

namespace Robot.Tasks
{
    public interface ITask<in TInputTaskData, out TOutpuTaskData> : IGeneralTask
        where TInputTaskData : InputTaskData
        where TOutpuTaskData : OutputTaskData
    {
        TOutpuTaskData TaskData { get; }
    }
}