/*
 * Copyright © 2023 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
using SDPS.ViewModel;
using System.Windows;
using System.Windows.Controls;

namespace SDPS.View.UserControls
{
    public partial class TitleBar : UserControl
    {
        public TitleBar()
        {
            InitializeComponent();
        }

        private void TitleBar_MouseLeftButtonDown(object sender, System.Windows.Input.MouseButtonEventArgs e)
        {
            Window.GetWindow(this).DragMove();
        }

        private void MinimizeBtn_Click(object sender, RoutedEventArgs e)
        {
            Window.GetWindow(this).WindowState = WindowState.Minimized;
        }
    }
}
