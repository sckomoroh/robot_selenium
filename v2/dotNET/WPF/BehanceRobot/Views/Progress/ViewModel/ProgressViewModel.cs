using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Diagnostics;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Threading;
using BehanceRobot.Common;
using BehanceRobot.Views.Progress.ViewModel.Items;
using Robot;
using Robot.Tasks;
using Robot.Tasks.Implementation.CollectActivities;
using Robot.Tasks.Implementation.CreateWebDriver;
using Robot.Tasks.Implementation.Login;
using WebDriver.Wrapper;

namespace BehanceRobot.Views.Progress.ViewModel
{
    public class ProgressViewModel : BaseViewModel
    {
        private readonly Dispatcher _uiDispatcher;
        private readonly Random _random;

        private bool _doAppreciate;
        private bool _doComment;
        private bool _doFollow;

        private WebDriverType _selectedWebDriver;

        private string _currentPhase = "Not started yet";
        private int _progressValue = 0;
        private int _progressMaximum = 100;
        private bool _isProgressIndeterminate = false;
        private List<String> _comments;

        public ProgressViewModel()
        {
            _uiDispatcher = Dispatcher.CurrentDispatcher;
            _random = new Random();

            StartCommand = new SimpleCommand(StartCommandHandler);
            ProgressItems = new ObservableCollection<ProgressItem>();

            WebDriverTypes = new List<WebDriverType>
            {
                WebDriverType.Chrome,
                WebDriverType.Firefox
            };

            ReadSettings();
        }

        public bool DoAppreciate
        {
            get {  return _doAppreciate;}

            set
            {
                if (_doAppreciate != value)
                {
                    _doAppreciate = value;
                    OnPropertyChanged();

                    SetActionFlag((int)SelectedActionsFlags.Appreciate, value);
                }
            }
        }

        public bool DoComment
        {
            get {  return _doComment; }

            set
            {
                if (_doComment != value)
                {
                    _doComment = value;
                    OnPropertyChanged();

                    SetActionFlag((int)SelectedActionsFlags.Comment, value);
                }
            }
        }

        public bool DoFollow
        {
            get {  return _doFollow; }

            set
            {
                if (_doFollow != value)
                {
                    _doFollow = value;
                    OnPropertyChanged();

                    SetActionFlag((int)SelectedActionsFlags.Follow, value);
                }
            }
        }

        public bool IsProgressIndeterminate
        {
            get { return _isProgressIndeterminate; }

            set
            {
                if (_isProgressIndeterminate != value)
                {
                    _isProgressIndeterminate = value;
                    OnPropertyChanged();
                }
            }
        }

        public string CurrentPhase
        {
            get { return _currentPhase; }

            set
            {
                if (_currentPhase != value)
                {
                    _currentPhase = value;
                    OnPropertyChanged();
                }
            }
        }

        public int ProgressValue
        {
            get { return _progressValue; }

            set
            {
                if (_progressValue != value)
                {
                    _progressValue = value;
                    OnPropertyChanged();
                }
            }
        }

        public int ProgressMaximum
        {
            get { return _progressMaximum; }

            set
            {
                if (_progressMaximum != value)
                {
                    _progressMaximum = value;
                    OnPropertyChanged();
                }
            }
        }

        public WebDriverType SelectedWebDriver
        {
            get { return _selectedWebDriver; }

            set
            {
                if (_selectedWebDriver != value)
                {
                    _selectedWebDriver = value;
                    OnPropertyChanged();

                    Properties.Settings.Default["SelectedWebDriver"] = _selectedWebDriver.ToString();
                    Properties.Settings.Default.Save();
                }
            }
        }

        public SimpleCommand StartCommand { get; }

        public ObservableCollection<ProgressItem> ProgressItems { get; }

        public IList<WebDriverType> WebDriverTypes { get; }

        private void StartCommandHandler()
        {
            StartCommand.SetCanExecute(false);

            Task.Factory.StartNew(ProcessTasks);
        }

        private void ReadSettings()
        {
            var actionsFlags = (int)Properties.Settings.Default["ActionsFlags"];
            var selectedDriver = (string)Properties.Settings.Default["SelectedWebDriver"];

            WebDriverType selectedWebDriver;
            if (Enum.TryParse(selectedDriver, out selectedWebDriver))
            {
                _selectedWebDriver = selectedWebDriver;
            }
            else
            {
                _selectedWebDriver = WebDriverTypes.First();
            }

            _doAppreciate = (actionsFlags & (int)SelectedActionsFlags.Appreciate) != 0;
            _doComment = (actionsFlags & (int)SelectedActionsFlags.Comment) != 0;
            _doFollow = (actionsFlags & (int)SelectedActionsFlags.Follow) != 0;
        }

        private void SetActionFlag(int flag, bool setFlag)
        {
            var actionsFlags = (int)Properties.Settings.Default["ActionsFlags"];
            if (setFlag)
            {
                actionsFlags = actionsFlags | flag;
            }
            else
            {
                actionsFlags = actionsFlags & ~flag;
            }

            Properties.Settings.Default["ActionsFlags"] = actionsFlags;
            Properties.Settings.Default.Save();
        }

        private void ProcessTasks()
        {
            _uiDispatcher.Invoke(() =>
            {
                ProgressItems.Clear();
                IsProgressIndeterminate = true;
            });

            IWebDriver webDriver = null;
            string userWebReference;
            IEnumerable<ActivityItem> activities;
            try
            {
                webDriver = CreateWebDriver();
                userWebReference = Login(webDriver);
                activities = CollectActivities(webDriver);
            }
            catch (Exception)
            {
                if (webDriver != null)
                {
                    webDriver.Quit();    
                }

                return;
            }

            _uiDispatcher.Invoke(() =>
            {
                IsProgressIndeterminate = false;
                ProgressMaximum = activities.Count();
                ProgressValue = 0;
            });

            var fileLocation = (string)Properties.Settings.Default["CommentsLocation"];

            _comments = File.ReadAllLines(fileLocation).ToList();

            _uiDispatcher.Invoke(() =>
            {
                var actionsFlags = (SelectedActionsFlags)Properties.Settings.Default["ActionsFlags"];

                foreach (var activityItem in activities)
                {
                    var index = _random.Next(_comments.Count);
                    Console.WriteLine("Comment index: {0}, Pass comment: {1} to {2}", index, _comments[index], activityItem.Name);
                    ProgressItems.Add(new ProgressItem(activityItem, actionsFlags, webDriver, _comments[index], userWebReference, _uiDispatcher));
                }

            });

            foreach (var progressItem in ProgressItems)
            {
                progressItem.RunItem();
                _uiDispatcher.Invoke(() =>
                {
                    ProgressValue++;
                });

                Thread.Sleep(1000);
            }

            webDriver.Quit();
        }

        private IWebDriver CreateWebDriver()
        {
            var createWebDriverInputData = new CreateWebDriverInputData
            {
                StartUrl = "http://www.behance.net",
                DriverType = _selectedWebDriver
            };

            var task = new CreateWebDriverTask(createWebDriverInputData);
            task.PhaseChanged += OnGeneralTaskPhaseChanged;
            task.StateChanged += OnGeneralTaskStateChanged;

            task.Run();

            task.PhaseChanged -= OnGeneralTaskPhaseChanged;
            task.StateChanged -= OnGeneralTaskStateChanged;

            return task.TaskData.WebDriver;
        }

        private string Login(IWebDriver webDriver)
        {
            var username = (string)Properties.Settings.Default["Username"];
            var password = (string)Properties.Settings.Default["Password"];

            var loginInputData = new LoginInputData
            {
                UserName = username,
                Password = password
            };

            var task = new LoginTask(webDriver, loginInputData);

            task.PhaseChanged += OnGeneralTaskPhaseChanged;
            task.StateChanged += OnGeneralTaskStateChanged;

            task.Run();

            task.PhaseChanged -= OnGeneralTaskPhaseChanged;
            task.StateChanged -= OnGeneralTaskStateChanged;

            return task.TaskData.UserWebReference;
        }

        private IEnumerable<ActivityItem> CollectActivities(IWebDriver webDriver)
        {
            Thread.Sleep(1000);

            var task = new CollectActivitiesTask(webDriver, null);

            task.PhaseChanged += OnGeneralTaskPhaseChanged;
            task.StateChanged += OnGeneralTaskStateChanged;

            task.Run();

            task.PhaseChanged -= OnGeneralTaskPhaseChanged;
            task.StateChanged -= OnGeneralTaskStateChanged;

            return task.TaskData.Activities;
        }

        private void OnGeneralTaskStateChanged(object sender, TaskStateChangedEventArgs args)
        {
            if (args.State == TaskState.Failed)
            {
                var task = sender as IGeneralTask;

                _uiDispatcher.Invoke(() =>
                {

                    IsProgressIndeterminate = false;
                    ProgressValue = 0;
                    ProgressMaximum = 1;

                    var caption = string.Format(
                        CultureInfo.InvariantCulture,
                        "{0} task error",
                        task.DisplayName);

                    MessageBox.Show(
                        task.Error.Message,
                        caption,
                        MessageBoxButton.OK,
                        MessageBoxImage.Error);

                    StartCommand.SetCanExecute(true);
                    CurrentPhase = string.Format(
                        CultureInfo.InvariantCulture,
                        "Task {0} failed",
                        task.DisplayName);
                });

                throw task.Error;
            }
        }

        private void OnGeneralTaskPhaseChanged(object sender, TaskPhaseChangedEventArgs args)
        {
            _uiDispatcher.Invoke(() =>
            {
                CurrentPhase = args.Phase;
            });
        }
    }
}
