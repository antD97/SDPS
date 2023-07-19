/*
 * Copyright © 2023 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
using SDPS.MVVM;

namespace SDPS.UserControls.ViewModel
{
    public class LabelledSpinnerViewModel : ViewModelBase
    {
        private double number;
        private double min;
        private double max;
        private double step;
        private bool allowFloat;
        private double lastOkNumber;

        private string labelText;

        public string LabelText
        {
            get { return labelText; }
            set { labelText = value; OnPropertyChanged(); }
        }

        private string fieldText;

        public string FieldText
        {
            get { return fieldText; }
            set { fieldText = value; OnPropertyChanged(); }
        }

        public RelayCommand IncreaseCommand => new RelayCommand(execute => {
            if (number + step <= max) number += step;
            FieldText = number.ToString();
        });

        public RelayCommand DecreaseCommand => new RelayCommand(execute => {
            if (number - step >= min) number -= step;
            FieldText = number.ToString();
        });

        public RelayCommand TextChangedCommand => new RelayCommand(execute => {
            if (allowFloat)
            {
                double parseOut;
                if (double.TryParse(FieldText, out parseOut) && parseOut >= min && parseOut <= max) number = parseOut;
            }
            else
            {
                int parseOut;
                if (int.TryParse(FieldText, out parseOut) && parseOut >= min && parseOut <= max) number = parseOut;
            }
        });

        public LabelledSpinnerViewModel(string labelText, double initNumber, double min, double max, double step, bool allowFloat)
        {
            this.labelText = labelText;
            this.number = initNumber;
            this.FieldText = number.ToString();
            this.min = min;
            this.max = max;
            this.step = step;
            this.allowFloat = allowFloat;
        }
    }
}
