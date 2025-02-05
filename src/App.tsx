import { getCurrentWindow } from "@tauri-apps/api/window";
import "./App.css";
import TitleBar from "./components/titleBar";
import { MainWindowContextProvider } from "./mainwindow/mainWindowContext";
import MainPanel from "./mainwindow/mainpanel/mainPanel";
import NavPanel from "./mainwindow/navpanel/navPanel";
import { OverlayContextProvider } from "./overlays/overlayContext";
import OverlaySwitcher from "./overlays/overlaySwitcher";
import DragOverlay from "./overlays/dragOverlay";

// const logsDir = await join(await path.documentDir(), 'My Games', 'Smite', 'BattleGame', 'Logs');

// const x = async () => {
//   console.log(await readDir(logsDir));
// };

function App() {
  const { label } = getCurrentWindow();
  return label === 'main' ? (
    <MainWindowContextProvider>
      <main className="min-h-screen h-screen max-h-screen min-w-screen w-screen max-w-screen flex flex-col bg-neutral-900/95 text-white select-none">
        <TitleBar />
        <div className="basis-0 grow flex overflow-hidden">
          <NavPanel />
          <div className="grow grid overflow-y-auto">
            <MainPanel />
          </div>
        </div>
      </main>
    </MainWindowContextProvider>
  ) : (
    <OverlayContextProvider>
      <main className="min-h-screen h-screen max-h-screen min-w-screen w-screen max-w-screen grid text-white select-none">
        <DragOverlay />
        <OverlaySwitcher />
      </main>
    </OverlayContextProvider>
  );
}

export default App;
