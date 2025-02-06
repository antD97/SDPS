import { getCurrentWindow } from "@tauri-apps/api/window";
import "./App.css";
import MainWindow from "./mainwindow/mainWindow";
import { MainWindowContextProvider } from "./mainwindow/mainWindowContext";
import OverlayWindow from "./overlays/overlayWindow";
import { OverlayContextProvider } from "./overlays/overlayContext";

function App() {
  return getCurrentWindow().label === 'main'
    ? (<MainWindowContextProvider><MainWindow /></MainWindowContextProvider>)
    : (<OverlayContextProvider><OverlayWindow /></OverlayContextProvider>);
}

export default App;
