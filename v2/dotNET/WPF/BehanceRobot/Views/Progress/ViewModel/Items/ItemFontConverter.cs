using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Data;
using Robot.Tasks;

namespace BehanceRobot.Views.Progress.ViewModel.Items
{
    public class ItemFontConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            var taskState = (TaskState)value;
            if (taskState == TaskState.Running || taskState == TaskState.Completed || taskState == TaskState.Failed)
            {
                return FontWeights.Bold;
            }

            return FontWeights.Normal;
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            return null;
        }
    }
}
