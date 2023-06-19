/*
 * Copyright © 2023 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
using System.ComponentModel;
using System.Diagnostics;
using System.Runtime.CompilerServices;
using System.Windows;
using System.Windows.Controls;

namespace SDPS.View.UserControls
{
    public partial class TopBar : UserControl, INotifyPropertyChanged
    {
        public event PropertyChangedEventHandler? PropertyChanged;

        private string titleText = "";
        public string TitleText
        {
            get { return titleText; }
            set {
                titleText = value;
                OnPropertyChanged();
            }
        }


        public TopBar() {
            DataContext = this;
            InitializeComponent();
            Trace.WriteLine(DataContext as string);
        }

        private void TopBar_MouseLeftButtonDown(object sender, System.Windows.Input.MouseButtonEventArgs e) { Window.GetWindow(this).DragMove(); }

        private void CloseBtn_Click(object sender, RoutedEventArgs e) { Window.GetWindow(this).Close(); }

        protected void OnPropertyChanged([CallerMemberName] string? name = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(name));
        }
    }
}
