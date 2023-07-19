/*
 * Copyright © 2023 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
using SDPS.UserControls.ViewModel;
using SDPS.ViewModel.UserControls;
using SDPS.ViewModel.UserControls.ControlMenus;
using System.Windows;

namespace SDPS.ViewModel
{
    public partial class MainWindow : Window
    {
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
        }
    }
}
