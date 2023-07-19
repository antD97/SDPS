/*
 * Copyright © 2023 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
using SDPS.MVVM;
using System.Collections.ObjectModel;

namespace SDPS.ViewModel
{
    public class DamageTableWindowViewModel : ViewModelBase
    {
        public ObservableCollection<DamageRow> DamageRows { get; set; }

        public DamageTableWindowViewModel() {
            DamageRows = new ObservableCollection<DamageRow>();
            DamageRows.Add(new DamageRow()
            {
                Time = "Time",
                Dps = "DPS",
                Damage = "Damage",
                TotalDamage = "Total Damage",
                Mitigated = "Mitigated",
                TotalMitigated = "Total Mitigated"
            });
        }

        public class DamageRow
        {
            public string Time { get; set; }
            public string Dps { get; set; }
            public string Damage { get; set; }
            public string TotalDamage { get; set; }
            public string Mitigated { get; set; }
            public string TotalMitigated { get; set; }
        }
    }
}
