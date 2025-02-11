import AdjustPanel from "./adjustPanel";
import { useOverlayContext } from "./overlayContext";
import OverlaySwitcher from "./overlaySwitcher";

function OverlayWindow() {
  const { overlayData: { windowState } } = useOverlayContext();

  if (windowState === 'hide') { return (<></>); }

  return (
    <main className="min-h-screen h-screen max-h-screen min-w-screen w-screen max-w-screen grid text-white select-none">
      {windowState === 'adjust' && <AdjustPanel />}
      <OverlaySwitcher />
    </main>
  );
}

export default OverlayWindow;
