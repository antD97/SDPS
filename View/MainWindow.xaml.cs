/*
 * Copyright © 2023 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
using SDPS.View.UserControls;
using SDPS.View.Util;
using System;
using System.ComponentModel;
using System.Diagnostics;
using System.Runtime.CompilerServices;
using System.Windows;

namespace SDPS.View
{
    public partial class MainWindow : Window, INotifyPropertyChanged
    {
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
        }
    }
}
