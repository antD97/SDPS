import { getCurrentWindow } from '@tauri-apps/api/window';
import { VscChromeClose, VscChromeMaximize, VscChromeMinimize } from 'react-icons/vsc';

export const TitleBar = () => {
  const window = getCurrentWindow();
  const btnClassName = "h-full px-2 hover:bg-cyan-500 transition-colors duration-300";
  return (
    <div
      onMouseDown={() => { window.startDragging(); }}
      className="h-6 min-h-6 max-h-6 relative flex items-center justify-center w-full bg-cyan-600 text-black"
    >
      <div className="absolute h-full right-0 flex items-center">
        <button
          onMouseDown={(e) => { e.stopPropagation(); }}
          onClick={() => { window.minimize(); }}
          className={btnClassName}
        >
          <VscChromeMinimize />
        </button>
        <button
          onMouseDown={(e) => { e.stopPropagation(); }}
          onClick={() => { window.toggleMaximize(); }}
          className={btnClassName}
        >
          <VscChromeMaximize />
        </button>
        <button
          onMouseDown={(e) => { e.stopPropagation(); }}
          onClick={() => { window.close(); }}
          className={btnClassName}
        >
          <VscChromeClose />
        </button>
      </div>
    </div>
  );
};
