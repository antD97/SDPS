/*
 * Copyright © 2023 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
using SDPS.View.UserControls;
using SDPS.View.UserControls.ControlMenus;
using SDPS.View.Util;
using System;
using System.Collections.Immutable;
using System.Collections.ObjectModel;
using System.Diagnostics;
using System.Windows;
using System.Windows.Controls;

namespace SDPS.View
{
    public partial class CombatTableWindow : Window, ISubWindow
    {
        private CombatTableControls combatTableControls;
        private CheckBox chkboxOverlayMode;

        public ObservableCollection<CombatRow> CombatRows { get; set; }

        public CombatTableWindow(CombatTableControls combatTableControls, CheckBox chkboxOverlayMode)
        {
            DataContext = this;
            CombatRows = new ObservableCollection<CombatRow>();
            InitializeComponent();

            // TODO remove
            CombatRows.Add(new CombatRow()
            {
                Time = "time",
                Dps = "dps",
                Damage = "damage",
                TotalDamage = "total damage"
            });
            CombatRows.Add(new CombatRow()
            {
                HealApplied = "heal applied",
                HealReceived = "heal received",
                Reason = "reason"
            });

            this.combatTableControls = combatTableControls;
            this.chkboxOverlayMode = chkboxOverlayMode;

            CombatTableTopBar.OverrideCloseAction(() =>
            {
                Hide();
                ((MainWindow)Application.Current.MainWindow).UpdateSubWindowControls();
            });

            combatTableControls.IntSpinnerTextSize.OnChangeAction = (fontSize) => DataGridCombatTable.FontSize = fontSize;
        }
        public void UpdateControls()
        {
            combatTableControls.IntSpinnerTextSize.Number = (int)Math.Round(DataGridCombatTable.FontSize);

            // TODO
            // combatTableControls.ChkboxDpsCol.Click += (a, b) => { };

            var chkBoxes = ImmutableList.Create(combatTableControls.ChkboxTimeCol,
                combatTableControls.ChkboxDpsCol, combatTableControls.ChkboxDamageCol, combatTableControls.ChkboxTotalDamageCol, combatTableControls.ChkboxMitigatedCol, combatTableControls.ChkboxTotalMitigatedCol,
                combatTableControls.ChkboxHealReceivedCol, combatTableControls.ChkboxTotalHealReceivedCol,
                combatTableControls.ChkboxHealAppliedCol, combatTableControls.ChkboxTotalHealAppliedCol,
                combatTableControls.ChkboxReasonCol);

            for (int i = 0; i < chkBoxes.Count; i++)
            {
                chkBoxes[i].IsChecked = DataGridCombatTable.Columns[0].Visibility == Visibility.Visible;
            }
        }

        public void SetOverlayModeEnabled(bool overlayModeEnabled)
        {
            if (overlayModeEnabled)
            {
                CombatTableTopBar.Visibility = Visibility.Collapsed;
                ResizeMode = ResizeMode.NoResize;
                Topmost = true;
                WindowsServices.SetWindowExTransparent(this);
            }
            else
            {
                CombatTableTopBar.Visibility = Visibility.Visible;
                ResizeMode = ResizeMode.CanResizeWithGrip;
                Topmost = false;
                WindowsServices.SetWindowExTransparent(this, false);
            }
        }

        public class CombatRow
        {
            public string Time { get; set; }

            public string Dps { get; set; }
            public string Damage { get; set; }
            public string TotalDamage { get; set; }
            public string Mitigated { get; set; }
            public string TotalMitigated { get; set; }

            public string HealReceived { get; set; }
            public string TotalHealReceived { get; set; }

            public string HealApplied { get; set; }
            public string TotalHealApplied { get; set; }

            public string Reason { get; set; }
        }
    }
}
