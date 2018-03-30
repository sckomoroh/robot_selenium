using System.Collections.ObjectModel;
using System.Configuration;
using System.IO;
using System.Windows.Controls;
using System.Windows.Input;
using BehanceRobot.Common;
using Microsoft.Win32;

namespace BehanceRobot.Views.Settings.ViewModel
{
    public class SettingsViewModel : BaseViewModel
    {
        private string _username;
        private string _password;
        private string _fileLocation;

        public SettingsViewModel()
        {
            SaveCredentialsCommand = new SimpleCommand(SaveCredentialsCommandHandler);
            BrowseCommand = new SimpleCommand(BrowseCommandHandler);

            CommentsList = new ObservableCollection<string>();

            ReadSettings();
        }

        public ICommand SaveCredentialsCommand { get; }

        public ICommand BrowseCommand { get; }

        public ObservableCollection<string> CommentsList { get; }

        public string UserName
        {
            get {  return _username;}

            set
            {
                if (_username != value)
                {
                    _username = value;
                    OnPropertyChanged();
                }
            }
        }

        public string Password
        {
            get { return _password; }

            set
            {
                if (_password != value)
                {
                    _password = value;
                    OnPropertyChanged();
                }
            }
        }

        public string FileLocation
        {
            get { return _fileLocation; }

            set
            {
                if (_fileLocation != value)
                {
                    _fileLocation = value;
                    OnPropertyChanged();
                }
            }
        }

        private void SaveCredentialsCommandHandler()
        {
            Properties.Settings.Default["Username"] = _username;
            Properties.Settings.Default["Password"] = _password;

            Properties.Settings.Default.Save();
        }

        private void BrowseCommandHandler()
        {
            var dialog = new OpenFileDialog();
            if (dialog.ShowDialog() == true)
            {
                FileLocation = dialog.FileName;
                ReadComments();

                Properties.Settings.Default["CommentsLocation"] = _fileLocation;
                Properties.Settings.Default.Save();
            }
        }

        private void ReadSettings()
        {
            _username = (string)Properties.Settings.Default["Username"];
            _password = (string)Properties.Settings.Default["Password"];
            _fileLocation = (string)Properties.Settings.Default["CommentsLocation"];

            ReadComments();
        }

        private void ReadComments()
        {
            if (!string.IsNullOrEmpty(_fileLocation) && File.Exists(_fileLocation))
            {
                var comments = File.ReadAllLines(_fileLocation);
                foreach (var comment in comments)
                {
                    CommentsList.Add(comment);
                }
            }
        }
    }
}