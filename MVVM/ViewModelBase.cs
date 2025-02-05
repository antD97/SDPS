// https://www.youtube.com/watch?v=Fs2gwb6Dqjk
using System.ComponentModel;
using System.Runtime.CompilerServices;

namespace SDPS.MVVM
{
    public class ViewModelBase : INotifyPropertyChanged
    {
        public event PropertyChangedEventHandler? PropertyChanged;

        protected void OnPropertyChanged([CallerMemberName] string? propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
    }
}
