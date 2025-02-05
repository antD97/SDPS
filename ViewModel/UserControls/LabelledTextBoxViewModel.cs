/*
 * Copyright © 2023 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
using SDPS.MVVM;
using System.Windows;

namespace SDPS.ViewModel.UserControls
{
    public class LabelledTextBoxViewModel : ViewModelBase
    {
        private string labelText;

        public string LabelText
        {
            get { return labelText; }
            set { labelText = value; OnPropertyChanged(); }
        }

        private string extraBtnContent;

        public string ExtraBtnContent
        {
            get { return extraBtnContent; }
            set { extraBtnContent = value; OnPropertyChanged(); }
        }

        private Visibility extraBtnVisibility;

        public Visibility ExtraBtnVisibility
        {
            get { return extraBtnVisibility; }
            set { extraBtnVisibility = value; OnPropertyChanged(); }
        }

        public LabelledTextBoxViewModel(string labelText, bool extraButtonVisible = false, string extraBtnContent = "")
        {
            this.labelText = labelText;
            if (extraButtonVisible) extraBtnVisibility = Visibility.Visible;
            else extraBtnVisibility = Visibility.Collapsed;
            this.extraBtnContent = extraBtnContent;
        }
    }
}
