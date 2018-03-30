using System;

namespace Robot.Tasks.Common
{
    public abstract class BaseTask<TInputTaskData, TOutpuTaskData> : ITask<TInputTaskData, TOutpuTaskData>
        where TInputTaskData : InputTaskData
        where TOutpuTaskData : OutputTaskData
    {
        protected TInputTaskData _inputTaskData;

        protected BaseTask(TInputTaskData inputTaskData)
        {
            State = TaskState.Pending;
            Error = null;
            _inputTaskData = inputTaskData;
        }

        public event EventHandler<TaskPhaseChangedEventArgs> PhaseChanged;

        public event EventHandler<TaskStateChangedEventArgs> StateChanged;

        public TOutpuTaskData TaskData { get; protected set; }

        public string DisplayName { get; protected set; }

        public TaskState State { get; private set; }

        public string Phase { get; private set; }

        public Exception Error { get; private set; }

        public void Run()
        {
            try
            {
                ChangeState(TaskState.Preparing);

                BeforeRun();

                ChangeState(TaskState.Running);

                if (InternalRun())
                {
                    ChangeState(TaskState.Completed);
                }
                else
                {
                    ChangeState(TaskState.Skipped);
                }
            }
            catch (Exception ex)
            {
                Error = ex;

                ChangeState(TaskState.Failed);

                Console.WriteLine(ex);
            }
        }

        protected virtual void BeforeRun()
        {
        }

        protected abstract bool InternalRun();

        protected void ChangeState(TaskState newState)
        {
            State = newState;

            if (StateChanged != null)
            {
                StateChanged(this, new TaskStateChangedEventArgs { State = newState });
            }
        }

        protected void ChangePhase(string newPhase)
        {
            Phase = newPhase;

            if (PhaseChanged != null)
            {
                PhaseChanged(this, new TaskPhaseChangedEventArgs { Phase = newPhase });                
            }
        }
    }
}