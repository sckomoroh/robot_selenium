using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;

namespace BehanceRobot.Common
{
    public class SimpleCommand : ICommand
    {
        private readonly Action _action;
        private bool _canExecute;

        public SimpleCommand(Action action)
        {
            _action = action;
            _canExecute = true;
        }

        public event EventHandler CanExecuteChanged;

        public bool CanExecute(object parameter)
        {
            return _canExecute;
        }

        public void SetCanExecute(bool canExecute)
        {
            _canExecute = canExecute;
            if (CanExecuteChanged != null)
            {
                CanExecuteChanged(this, EventArgs.Empty);
            }
        }

        public void Execute(object parameter)
        {
            _action();
        }
    }
}
