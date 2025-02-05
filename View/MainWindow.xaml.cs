/*
 * Copyright © 2023 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
<<<<<<< HEAD
using SDPS.View.UserControls;
using SDPS.View.Util;
using System;
using System.ComponentModel;
using System.Diagnostics;
using System.Runtime.CompilerServices;
=======
using SDPS.UserControls.ViewModel;
using SDPS.ViewModel.UserControls;
using SDPS.ViewModel.UserControls.ControlMenus;
>>>>>>> 3acdf97c70c7637d5123d7a3465018c689606147
using System.Windows;

namespace SDPS.ViewModel
{
    public partial class MainWindow : Window, INotifyPropertyChanged
    {
<<<<<<< HEAD
        public event PropertyChangedEventHandler? PropertyChanged;

        private string windowTitle = "";
        public string WindowTitle
        {
            get { return windowTitle; }
            set {
                windowTitle = value;
                OnPropertyChanged();
            }
        }


        public MainWindow() {
            DataContext = this;
            InitializeComponent();

            WindowTitle = $"SDPS {App.Version} - antD";
            winTopBar.TitleText = WindowTitle;
        }

        public void MainWindow_Closing(object sender, CancelEventArgs e)
        {
            ((App)Application.Current).killTasksAndWait();
            Trace.WriteLine("Main window closed.");
        }

        protected void OnPropertyChanged([CallerMemberName] string? name = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(name));
=======
        public MainWindow()
        {
            InitializeComponent();
            DataContext = new MainWindowViewModel();
            Title = $"SDPS {App.Version} - antD";
            WindowTitleBar.DataContext = new TitleBarViewModel(this, Title);
            InGameNameLTBox.DataContext = new LabelledTextBoxViewModel("In-Game Name", true, "↺");
            CombatLogFileLTBox.DataContext = new LabelledTextBoxViewModel("Combat Log File");

            DamageTableControls.DataContext = new DamageTableControlsViewModel();
            DamageTableControls.TextSizeLSpinner.DataContext = new LabelledSpinnerViewModel("Text Size", 12, 1, 72, 1, false);
            DamageTableControls.BackgroundLTBox.DataContext = new LabelledTextBoxViewModel("Background Color");
>>>>>>> 3acdf97c70c7637d5123d7a3465018c689606147
        }
    }
}
