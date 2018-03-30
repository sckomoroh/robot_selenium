using System;
using System.Globalization;
using System.Windows;
using System.Windows.Data;
using Robot.Tasks;

namespace BehanceRobot.Views.Progress.ViewModel.Items
{
    public class ItemFontStyleConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            var taskState = (TaskState)value;
            if (taskState == TaskState.Skipped || taskState == TaskState.Ignored)
            {
                return FontStyles.Italic;
            }

            return FontStyles.Normal;
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            return null;
        }
    }
}