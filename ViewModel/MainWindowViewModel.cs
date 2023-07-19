/*
 * Copyright © 2023 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
using SDPS.MVVM;
using SDPS.View;
using System.Diagnostics;
using System.Windows;

namespace SDPS.ViewModel
{
    public class MainWindowViewModel : ViewModelBase
    {
        private DamageTableWindow damageTableWindow = new DamageTableWindow();

        public RelayCommand ClosingCommand => new RelayCommand(execute => {
            ((App)Application.Current).killTasksAndWait();
            Trace.WriteLine("Main window closed.");
        });

        public RelayCommand EnabledCBoxClickCommand = new RelayCommand(execute =>
        {
            
        });

    }
}
