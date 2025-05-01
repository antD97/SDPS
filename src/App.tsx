import { getCurrentWindow } from '@tauri-apps/api/window';
import './App.css';
import { MainWindow } from './mainWindow/mainWindow';
import { MainWindowContextProvider } from './mainWindow/mainWindowContext';
import { OverlayContextProvider } from './overlays/overlayContext';
import { OverlayWindow } from './overlays/overlayWindow';

function App() {
  return getCurrentWindow().label === 'main'
    ? (<MainWindowContextProvider><MainWindow /></MainWindowContextProvider>)
    : (<OverlayContextProvider><OverlayWindow /></OverlayContextProvider>);
}

export default App;
