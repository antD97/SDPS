/*
 * Copyright © 2023 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
using System.Collections.Immutable;
using System.ComponentModel;
using System.Windows;
using System.Windows.Controls;

namespace SDPS.View
{
    public partial class MainWindow : Window
    {

        private ImmutableList<SubWindowData> subWindows;
        private int selectedSubWindowI = 0;

        public MainWindow() {
            InitializeComponent();

            Title = $"SDPS {App.Version} - antD";
            winTopBar.TitleText = Title;

            subWindows = ImmutableList.Create(
                new SubWindowData(new CombatTableWindow(CombatTableControls, ChkboxOverlayMode), CombatTableControls)
                );

            // TODO load user settings

            UpdateSubWindowControls();
        }

        public void UpdateSubWindowControls()
        {
            var selectedSubWindow = subWindows[selectedSubWindowI].window;
            var selectedSubWindowControls = subWindows[selectedSubWindowI].controls;

            SubWindowName.Text = selectedSubWindow.Title;

            if (selectedSubWindow.Visibility == Visibility.Visible)
            {
                ChkboxEnabled.IsChecked = true;
                selectedSubWindowControls.IsEnabled = true;
                selectedSubWindowControls.Opacity = 1.0;
                ChkboxOverlayMode.IsEnabled = true;
                ChkboxOverlayMode.Opacity = 1.0;
            }
            else
            {
                ChkboxEnabled.IsChecked = false;
                selectedSubWindowControls.IsEnabled = false;
                selectedSubWindowControls.Opacity = 0.5;
                ChkboxOverlayMode.IsEnabled = false;
                ChkboxOverlayMode.Opacity = 0.5;
            }

            ((ISubWindow)selectedSubWindow).UpdateControls();
        }

        private void ChkboxEnabled_Click(object sender, RoutedEventArgs e)
        {
            if (((CheckBox)sender).IsChecked == true)
            {
                subWindows[selectedSubWindowI].window.Show();
                UpdateSubWindowControls();
            }
            else
            {
                subWindows[selectedSubWindowI].window.Hide();
                UpdateSubWindowControls();
            }
        }

        private void ChkboxOverlayMode_Click(object sender, RoutedEventArgs e)
        {
            var overlayModeIsChecked = ((CheckBox)sender).IsChecked;
            if (overlayModeIsChecked == null) return;
            ((ISubWindow)subWindows[selectedSubWindowI].window).SetOverlayModeEnabled((bool)overlayModeIsChecked);
        }

        private void SubWindowSelectorLeft_Click(object sender, RoutedEventArgs e)
        {
            selectedSubWindowI = (selectedSubWindowI - 1 + subWindows.Count) % subWindows.Count;
            UpdateSubWindowControls();
        }

        private void SubWindowSelectorRight_Click(object sender, RoutedEventArgs e)
        {
            selectedSubWindowI = (selectedSubWindowI + 1) % subWindows.Count;
            UpdateSubWindowControls();
        }

        public void MainWindow_Closing(object sender, CancelEventArgs e)
        {
            ((App)Application.Current).killTasksAndWait();
        }

        private record SubWindowData(Window window, UserControl controls);
    }
}
