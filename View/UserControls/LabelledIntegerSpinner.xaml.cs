/*
 * Copyright © 2023 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
using System;
using System.ComponentModel;
using System.Diagnostics;
using System.Runtime.CompilerServices;
using System.Windows.Controls;
using static System.Net.Mime.MediaTypeNames;

namespace SDPS.View.UserControls
{
    public partial class LabelledIntegerSpinner : UserControl, INotifyPropertyChanged
    {
        private const int MAX_VAL = 99;
        private const int MIN_VAL = 1;

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

        private int number = 1;
        public int Number
        {
            get { return number; }
            set
            {
                number = value;
                OnPropertyChanged();
            }
        }

        public Action<int>? OnChangeAction;

        public LabelledIntegerSpinner()
        {
            DataContext = this;
            InitializeComponent();
        }

        private void BtnUp_Click(object sender, System.Windows.RoutedEventArgs e)
        {
            if (Number < MAX_VAL) Number++;
        }

        private void BtnDown_Click(object sender, System.Windows.RoutedEventArgs e)
        {
            if (Number > MIN_VAL) Number--;
        }

        private void TextBox_TextChanged(object sender, TextChangedEventArgs e)
        {
            var textBox = (TextBox)sender;
            var strNumber = textBox.Text;

            if (int.TryParse(strNumber, out int newNumber)
                && newNumber >= MIN_VAL && newNumber <= MAX_VAL)
            {
                Number = newNumber;
                OnChangeAction?.Invoke(Number);
            }
            else textBox.Text = Number.ToString();
        }

        protected void OnPropertyChanged([CallerMemberName] string? name = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(name));
        }
    }
}
