/*
 * Copyright © 2023 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
using SDPS.MVVM;
using System.Windows;

namespace SDPS.ViewModel.UserControls
{
    public class TitleBarViewModel : ViewModelBase
    {

        private Window parentWindow;

        private string title;

        public string Title
        {
            get { return title; }
            set { title = value; OnPropertyChanged(); }
        }

        private Visibility minimizeBtnVisibility;

        public Visibility MinimizeBtnVisibility
        {
            get { return minimizeBtnVisibility; }
            set { minimizeBtnVisibility = value; OnPropertyChanged(); }
        }

        public RelayCommand CloseBtnCommand => new RelayCommand(execute => parentWindow.Close());

        public TitleBarViewModel(Window parentWindow, string title, bool showMinimizeBtn = true)
        {
            this.parentWindow = parentWindow;
            this.title = title;
            if (showMinimizeBtn) this.minimizeBtnVisibility = Visibility.Visible;
            else this.minimizeBtnVisibility = Visibility.Collapsed;
        }
    }
}
