using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Data;
using System.Windows.Media;
using Robot.Tasks;

namespace BehanceRobot.Views.Progress.ViewModel.Items
{
    public class ItemColorConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            var taskState = (TaskState)value;
            switch (taskState)
            {
                case TaskState.Completed:
                    return Brushes.DarkGreen;
                case TaskState.Failed:
                    return Brushes.OrangeRed;
                case TaskState.Ignored:
                case TaskState.Skipped:
                    return Brushes.Brown;
                case TaskState.Pending:
                case TaskState.Preparing:
                    return Brushes.DimGray;
                case TaskState.Running:
                    return Brushes.DarkCyan;
            }

            return Brushes.Black;
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            return null;
        }
    }
}
