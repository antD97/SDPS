/*
 * Copyright © 2023 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
using SDPS.ViewModel.UserControls;
using SDPS.ViewModel;
using System.Windows;

namespace SDPS.View
{
    public partial class DamageTableWindow : Window
    {
        public DamageTableWindow()
        {
            InitializeComponent();
            DataContext = new DamageTableWindowViewModel();
            WindowTitleBar.DataContext = new TitleBarViewModel(this, Title, false);
        }
    }
}
