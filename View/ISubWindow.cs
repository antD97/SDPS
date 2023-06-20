/*
 * Copyright © 2023 antD97
 * Licensed under the MIT License https://antD.mit-license.org/
 */
using System.Windows;

namespace SDPS.View
{
    internal interface ISubWindow
    {
        void UpdateControls();
        void SetOverlayModeEnabled(bool overlayModeEnabled);
    }
}
