/*
 * Copyright © 2023 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
using System.ComponentModel;
using System.Runtime.CompilerServices;
using System.Windows.Controls;

namespace SDPS.View.UserControls
{ 
    public partial class LabelledTextBox : UserControl, INotifyPropertyChanged
    {
        public event PropertyChangedEventHandler? PropertyChanged;

        private string labelText = "Input";
        public string LabelText
        {
            get { return labelText; }
            set
            {
                labelText = value;
                OnPropertyChanged();
            }
        }

        public LabelledTextBox()
        {
            DataContext = this;
            InitializeComponent();
        }

        protected void OnPropertyChanged([CallerMemberName] string? name = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(name));
        }
    }
}
