using System;
using System.Windows.Threading;
using BehanceRobot.Common;
using Robot.Tasks;
using Robot.Tasks.Implementation.Appreciate;
using Robot.Tasks.Implementation.CollectActivities;
using Robot.Tasks.Implementation.Comment;
using Robot.Tasks.Implementation.Follow;
using WebDriver.Wrapper;

namespace BehanceRobot.Views.Progress.ViewModel.Items
{
    public class ProgressItem : BaseViewModel
    {
        private readonly Dispatcher _uiDispatcher;
        private readonly FollowTask _followTask;
        private readonly CommentTask _commentTask;
        private readonly AppreciateTask _appreciateTask;
        private readonly IWebDriver _webDriver;

        public ProgressItem(ActivityItem activity, SelectedActionsFlags flags, IWebDriver webDriver, string comment, string userWebReference, Dispatcher uiDispatcher)
        {
            Activity = activity;
            _webDriver = webDriver;
            _uiDispatcher = uiDispatcher;

            var commentTaskInputData = new CommentInputData
            {
                Comment = comment,
                UserWebReference = userWebReference,
                ActivityWebReference = Activity.Name
            };

            if ((flags & SelectedActionsFlags.Follow) != 0)
            {
                commentTaskInputData.ActionName = "Follow";
                _followTask = new FollowTask(webDriver, commentTaskInputData);
                _followTask.StateChanged += OnFollowTaskStateChanged;
            }

            if ((flags & SelectedActionsFlags.Comment) != 0)
            {
                commentTaskInputData.ActionName = " Comment";
                _commentTask = new CommentTask(webDriver, commentTaskInputData);
                _commentTask.StateChanged += OnCommentTaskStateChanged;
            }

            if ((flags & SelectedActionsFlags.Appreciate) != 0)
            {
                commentTaskInputData.ActionName = "Appreciate";
                _appreciateTask = new AppreciateTask(webDriver, commentTaskInputData);
                _appreciateTask.StateChanged += OnAppreciateTaskStateChanged;
            }
        }

        public ActivityItem Activity { get; }

        public string Name
        {
            get { return Activity.Name; }
        }

        public TaskState FollowTaskState
        {
            get
            {
                if (_followTask == null)
                {
                    return TaskState.Ignored;
                }

                return _followTask.State;
            }
        }

        public TaskState CommentTaskState
        {
            get
            {
                if (_commentTask == null)
                {
                    return TaskState.Ignored;
                }

                return _commentTask.State;
            }
        }

        public TaskState AppreciateTaskState
        {
            get
            {
                if (_appreciateTask == null)
                {
                    return TaskState.Ignored;
                }

                return _appreciateTask.State;
            }
        }

        public void RunItem()
        {
            _webDriver.Navigate(Activity.WebReference, true);

            if (_followTask != null)
            {
                _followTask.Run();
            }

            if (_commentTask != null)
            {
                _commentTask.Run();
            }

            if (_appreciateTask != null)
            {
                _appreciateTask.Run();
            }
        }

        public bool HasRunning
        {
            get
            {
                return (_followTask != null && _followTask.State == TaskState.Running) ||
                       (_commentTask != null && _commentTask.State == TaskState.Running) ||
                       (_appreciateTask != null && _appreciateTask.State == TaskState.Running);
            }
        }

        public object ThisObject
        {
            get { return this; }
        }

        private void OnFollowTaskStateChanged(object sender, TaskStateChangedEventArgs args)
        {
            _uiDispatcher.Invoke(() =>
            {
                OnPropertyChanged("FollowTaskState");
                OnPropertyChanged("HasRunning");
                OnPropertyChanged("ThisObject");
            });
        }

        private void OnCommentTaskStateChanged(object sender, TaskStateChangedEventArgs args)
        {
            _uiDispatcher.Invoke(() =>
            {
                OnPropertyChanged("CommentTaskState");
                OnPropertyChanged("HasRunning");
                OnPropertyChanged("ThisObject");
            });
        }

        private void OnAppreciateTaskStateChanged(object sender, TaskStateChangedEventArgs args)
        {
            _uiDispatcher.Invoke(() =>
            {
                OnPropertyChanged("AppreciateTaskState");
                OnPropertyChanged("HasRunning");
                OnPropertyChanged("ThisObject");
            });
        }
    }
}