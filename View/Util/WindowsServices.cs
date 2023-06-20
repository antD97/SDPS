using System.Runtime.InteropServices;
using System.Windows.Interop;
using System.Windows;
using System;

namespace SDPS.View.Util
{
    // https://stackoverflow.com/questions/2842667/how-to-create-a-semi-transparent-window-in-wpf-that-allows-mouse-events-to-pass
    internal static class WindowsServices
    {
        const int WS_EX_TRANSPARENT = 0x00000020;
        const int GWL_EXSTYLE = (-20);

        private static int? originalStyle = null;

        [DllImport("user32.dll")]
        static extern int GetWindowLong(IntPtr hwnd, int index);

        [DllImport("user32.dll")]
        static extern int SetWindowLong(IntPtr hwnd, int index, int newStyle);

        public static void SetWindowExTransparent(Window window, bool enabled = true)
        {
            var hwnd = new WindowInteropHelper(window).Handle;
            var extendedStyle = GetWindowLong(hwnd, GWL_EXSTYLE);
            if (originalStyle == null) originalStyle = extendedStyle;
            if (enabled) SetWindowLong(hwnd, GWL_EXSTYLE, extendedStyle | WS_EX_TRANSPARENT);
            else SetWindowLong(hwnd, GWL_EXSTYLE, (int)originalStyle);
        }
    }
}
